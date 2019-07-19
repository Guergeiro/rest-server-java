package main;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.post;
import static spark.Spark.put;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import messages.Message;
import spark.Request;
import spark.Response;
import user.User;

/* args0 -> config file */

public class RESTServer {
  // Attributes
  private static HashMap<Integer, Message> greetings = new HashMap<>();
  private static Integer count = 0;
  private static JDBCInterface jdbc;
  
  public static void main(String[] args) {
    Properties prop = new Properties();
    InputStream is;
    try {
      is = new FileInputStream(args[0]);
      prop.load(is);
    } catch (IOException e1) {
      System.out.println("Can't open properties.");
      System.exit(1);
    }
    
    try {
      jdbc = (JDBCInterface) Naming.lookup("rmi://" + "rmi-server:7654" + "/jdbc");
    } catch (MalformedURLException | RemoteException | NotBoundException e) {
      System.out.println("Can't find RMIServer.");
      System.exit(1);
    }

    get("/users", (req, res) -> {
      return allUsers(res);
    });

    get("/users/:id", (req, res) -> {
      return oneUser(res, Integer.valueOf(req.params(":id")));
    });

    post("/users", (req, res) -> {
      return createUser(req, res);
    });

    delete("/users/:id", (req, res) -> {
      return deleteUser(res, Integer.valueOf(req.params(":id")));
    });

    put("/users/:id", (req, res) -> {
      return updateUser(req, res, Integer.valueOf(req.params(":id")));
    });

    get("/greetings", (req, res) -> {
      return allGreetings(res);
    });

    get("/greetings/:id", (req, res) -> {
      return oneGreeting(res, Integer.valueOf(req.params(":id")));
    });

    put("/greetings/:id", (req, res) -> {
      return updateGreeting(res, Integer.valueOf(req.params(":id")),
          new Message(req.queryParams("message"), new Timestamp(System.currentTimeMillis())));
    });

    delete("/greetings/:id", (req, res) -> {
      return deleteGreeting(res, Integer.valueOf(req.params(":id")));
    });

    post("/greetings", (req, res) -> {
      return createGreeting(res,
          new Message(req.queryParams("message"), new Timestamp(System.currentTimeMillis())));
    });

    notFound((req, res) -> {
      res.type("application/json");
      return pageNotFound(res);
    });
  }

  private static String allUsers(Response res) {
    res.type("application/json");
    res.status(200);

    // Calls RMI to insert user
    String returnedValue;
    try {
      returnedValue = jdbc.selectAllUsers();
      res.status(200);
      return returnedValue;
    } catch (RemoteException e) {
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String oneUser(Response res, Integer id) {
    res.type("application/json");

    // Calls RMI to insert user
    String returnedValue;
    try {
      returnedValue = jdbc.selectUser(id);
      if (returnedValue == null) {
        return returnJSONMessage("Key doesn't exist.");
      }
      res.status(200);
      return returnedValue;
    } catch (RemoteException e) {
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }

  }

  @SuppressWarnings("unchecked")
  private static String createUser(Request req, Response res) {
    res.type("application/json");
    if (req.contentType().equals("application/json")) {
      // In a JSON File
      JSONParser parser = new JSONParser();
      JSONArray response = new JSONArray();
      try {
        JSONArray array = (JSONArray) parser.parse(req.body());
        for (int i = 0; i < array.size(); i++) {
          JSONObject user = (JSONObject) array.get(i);
          response.add(insertDB(user.get("nome").toString(), user.get("datanascimento").toString(),
              user.get("localidade").toString(), res));
        }
      } catch (Exception e) {
        res.status(400);
        return returnJSONMessage("Wrong JSON format. Provide JSONArray.");
      }
      return response.toJSONString();
    } else {
      return insertDB(req.queryParams("nome"), req.queryParams("datanascimento"),
          req.queryParams("localidade"), res).toJSONString();
    }
  }

  private static String deleteUser(Response res, Integer id) {
    res.type("application/json");
    // Calls RMI to insert user
    String returnedValue;
    try {
      returnedValue = jdbc.deleteUser(id);
      if (returnedValue == null) {
        res.status(404);
        return returnJSONMessage("Key doesn't exist.");
      }
      res.status(200);
      return returnedValue;
    } catch (RemoteException e) {
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  @SuppressWarnings("unchecked")
  private static String updateUser(Request req, Response res, Integer id) {
    res.type("application/json");
    JSONObject obj = new JSONObject();

    // Checks date
    LocalDate birthday = processDate(req.queryParams("datanascimento"));
    if (birthday == null) {
      res.status(400);
      obj.put("message", "Wrong datanascimento format.");
      return obj.toJSONString();
    }

    // Checks name
    String nome = req.queryParams("nome");
    if (nome == null) {
      res.status(400);
      obj.put("message", "Missing nome.");
      return obj.toJSONString();
    }

    // Checks localidade
    String localidade = req.queryParams("localidade");
    if (localidade == null) {
      res.status(400);
      obj.put("message", "Missing localidade.");
      return obj.toJSONString();
    }

    // Calls RMI to update user
    String returnedValue;
    try {
      returnedValue = jdbc.updateUser(id, new User(nome, birthday, localidade));
      if (returnedValue == null) {
        res.status(404);
        return returnJSONMessage("Key doesn't exist.");
      }
      res.status(200);
      return returnedValue;
    } catch (RemoteException e) {
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  @SuppressWarnings("unchecked")
  private static String pageNotFound(Response res) {
    res.type("application/json");
    res.status(404);
    JSONObject obj = new JSONObject();
    obj.put("message", "API endpoint not avaiable.");
    return obj.toJSONString();
  }

  @SuppressWarnings("unchecked")
  private static String allGreetings(Response res) {
    res.type("application/json");
    res.status(200);
    JSONArray array = new JSONArray();
    for (Entry<Integer, Message> entry : greetings.entrySet()) {
      JSONObject obj = new JSONObject();
      obj.put(entry.getKey(), entry.getValue().toJSON());
      array.add(obj);
    }
    return array.toJSONString();
  }

  @SuppressWarnings("unchecked")
  private static String oneGreeting(Response res, Integer id) {
    res.type("application/json");

    // Checks if key exist
    if (!keyExists(id)) {
      res.status(404);
      return returnJSONMessage("Key doesn't exist.");
    }

    // Everything okay!
    res.status(200);
    JSONObject obj = new JSONObject();
    obj.put(id, greetings.get(id).toJSON());
    return obj.toJSONString();
  }

  @SuppressWarnings("unchecked")
  private static String updateGreeting(Response res, Integer id, Message message) {
    res.type("application/json");

    // Checks if key exist
    if (!keyExists(id)) {
      res.status(404);
      return returnJSONMessage("Key doesn't exist.");
    }

    // Check if message matches regex
    if (!Pattern.matches("[a-zA-Z]+", message.getMessage())) {
      res.status(400);
      return returnJSONMessage("Message should only allow [a-zA-Z]+ pattern.");
    }

    // Everything okay!
    res.status(200);
    greetings.put(id, message);

    JSONObject obj = new JSONObject();
    obj.put(id, message.toJSON());
    return obj.toJSONString();
  }

  @SuppressWarnings("unchecked")
  private static String createGreeting(Response res, Message message) {
    res.type("application/json");

    // Check if message matches regex
    if (!Pattern.matches("[a-zA-Z]+", message.getMessage())) {
      res.status(400);
      return returnJSONMessage("Message should only allow [a-zA-Z]+ pattern.");
    }

    // Everything okay!
    res.status(200);
    greetings.put(++count, message);

    JSONObject obj = new JSONObject();
    obj.put(count, message.toJSON());
    return obj.toJSONString();
  }

  private static String deleteGreeting(Response res, Integer id) {
    res.type("application/json");

    // Checks if key exist
    if (!keyExists(id)) {
      res.status(404);
      return returnJSONMessage("Key doesn't exist.");
    }

    // Everything okay!
    res.status(200);
    greetings.remove(id);
    return returnJSONMessage("Delete Successful.");
  }

  private static boolean keyExists(Integer id) {
    Message message = greetings.get(id);
    if (message == null)
      return false;
    return true;
  }

  @SuppressWarnings("unchecked")
  private static String returnJSONMessage(String message) {
    JSONObject obj = new JSONObject();
    obj.put("message", message);
    return obj.toJSONString();
  }

  private static LocalDate processDate(String data) {
    if (data == null) {
      return null;
    }

    // Initialize variables
    LocalDate birthday;

    if (data.charAt(4) == '-') {
      // YYYY-MM-DD
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      try {
        birthday = LocalDate.parse(data, formatter);
      } catch (DateTimeParseException e) {
        // Wrong date format
        return null;
      }
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      try {
        birthday = LocalDate.parse(data, formatter);
      } catch (DateTimeParseException e) {
        // Wrong date format
        return null;
      }
    }
    return birthday;
  }

  @SuppressWarnings("unchecked")
  private static JSONObject insertDB(String nome, String data, String localidade, Response res) {
    JSONObject obj = new JSONObject();
    // Checks date
    LocalDate birthday = processDate(data);
    if (birthday == null) {
      res.status(400);
      obj.put("message", "Wrong datanascimento format.");
      return obj;
    }

    // Checks name
    if (nome == null) {
      res.status(400);
      obj.put("message", "Missing nome.");
      return obj;
    }

    // Checks localidade
    if (localidade == null) {
      res.status(400);
      obj.put("message", "Missing localidade.");
      return obj;
    }

    // Calls RMI to insert user
    try {
      res.status(200);
      return jdbc.insertUser(new User(nome, birthday, localidade));
    } catch (RemoteException e) {
      res.status(500);
      obj.put("message", "RMIServer error.");
      return obj;
    }
  }
}
