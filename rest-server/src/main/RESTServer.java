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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import messages.Message;
import responses.ResponseObject;
import spark.Request;
import spark.Response;
import users.User;

public class RESTServer {
  // Attributes
  private static JDBCInterface jdbc;

  public static void main(String[] args) {
    // Imports config
    Properties prop = new Properties();
    InputStream is = null;
    try {
      is = new FileInputStream(args[0]);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    try {
      prop.load(is);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    while (true) {
      try {
        jdbc = (JDBCInterface) Naming.lookup("rmi://" + prop.getProperty("rmi-url") + ":"
            + prop.getProperty("rmi-port") + "/rmi-server");
        break;
      } catch (MalformedURLException | RemoteException | NotBoundException e) {
        System.out.println("Can't find RMIServer. Searching again. CTRL+C to stop.");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        continue;
      }
    }

    get("/users", (req, res) -> {
      return allUsers(req, res);
    });

    get("/users/:id", (req, res) -> {
      return oneUser(req, res);
    });

    post("/users", (req, res) -> {
      return createUser(req, res);
    });

    delete("/users/:id", (req, res) -> {
      return deleteUser(req, res);
    });

    put("/users/:id", (req, res) -> {
      return updateUser(req, res);
    });

    get("/greetings", (req, res) -> {
      return allGreetings(req, res);
    });

    get("/greetings/:id", (req, res) -> {
      return oneGreeting(req, res);
    });

    put("/greetings/:id", (req, res) -> {
      return updateGreeting(req, res);
    });

    delete("/greetings/:id", (req, res) -> {
      return deleteGreeting(req, res);
    });

    post("/greetings", (req, res) -> {
      return createGreeting(req, res);
    });

    notFound((req, res) -> {
      res.type("application/json");
      return pageNotFound(res);
    });
  }

  private static String allUsers(Request req, Response res) {
    res.type("application/json");

    // Calls RMI to select all users
    try {
      ResponseObject response = jdbc.selectAllUsers();
      res.status(response.getStatus());
      if (response.getStatus() != 200) {
        return response.getObject().toJSONString();
      }
      return response.getArray().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String oneUser(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Calls RMI to select all users
    try {
      ResponseObject response = jdbc.selectUser(id);
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String createUser(Request req, Response res) {
    res.type("application/json");
    if (req.contentType().equals("application/json")) {
      return insertJSONUser(req, res);
    }
    return insertUser(req, res);
  }

  private static String deleteUser(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Calls RMI to delete user
    try {
      ResponseObject response = jdbc.deleteUser(id);
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String updateUser(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Checks date
    LocalDate birthday = processDate(req.queryParams("datanascimento"));
    if (birthday == null) {
      res.status(400);
      return returnJSONMessage("Wrong datanascimento format.");
    }

    // Checks name
    String nome = req.queryParams("nome");
    if (nome == null) {
      res.status(400);
      return returnJSONMessage("Missing nome.");
    }

    // Checks localidade
    String localidade = req.queryParams("localidade");
    if (localidade == null) {
      res.status(400);
      return returnJSONMessage("Missing localidade.");
    }

    // Calls RMI to update user
    try {
      ResponseObject response = jdbc.updateUser(id, new User(nome, birthday, localidade));
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String pageNotFound(Response res) {
    res.type("application/json");
    res.status(404);
    return returnJSONMessage("API endpoint not avaiable.");
  }

  private static String allGreetings(Request req, Response res) {
    res.type("application/json");

    // Calls RMI to select all messages
    try {
      ResponseObject response = jdbc.selectAllMessages();
      res.status(response.getStatus());
      if (response.getStatus() != 200) {
        return response.getObject().toJSONString();
      }
      return response.getArray().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String oneGreeting(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Calls RMI to select one message
    try {
      ResponseObject response = jdbc.selectMessage(id);
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String updateGreeting(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Checks message
    String message = req.queryParams("message");
    if (message == null || !Pattern.matches("[a-zA-Z]+", message)) {
      res.status(400);
      return returnJSONMessage("Wrong message. Should match [a-zA-Z]+ pattern.");
    }
    // Calls RMI to update one greeting
    try {
      ResponseObject response = jdbc.updateMessage(id, new Message(message));
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String createGreeting(Request req, Response res) {
    res.type("application/json");
    String message = req.queryParams("message");
    // Checks message
    if (message == null || !Pattern.matches("[a-zA-Z]+", message)) {
      res.status(400);
      return returnJSONMessage("Wrong message. Should match [a-zA-Z]+ pattern.");
    }

    // Calls RMI to insert Greeting
    try {
      ResponseObject response = jdbc.insertMessage(new Message(message));
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
  }

  private static String deleteGreeting(Request req, Response res) {
    res.type("application/json");
    Integer id = Integer.valueOf(req.params(":id"));
    // Calls RMI to delete message
    try {
      ResponseObject response = jdbc.deleteMessage(id);
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      res.status(500);
      return returnJSONMessage("RMIServer error.");
    }
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
  private static String insertJSONUser(Request req, Response res) {
    // In a JSON File
    JSONParser parser = new JSONParser();
    JSONArray responsearray = new JSONArray();
    try {
      JSONArray array = (JSONArray) parser.parse(req.body());
      for (int i = 0; i < array.size(); i++) {
        JSONObject user = (JSONObject) array.get(i);

        // Checks date
        LocalDate birthday = processDate((String) user.get("datanascimento"));
        if (birthday == null) {
          JSONObject obj = new JSONObject();
          obj.put("message", "Wrong datanascimento format.");
          responsearray.add(obj);
          continue;
        }
        // Checks name
        String nome = (String) user.get("nome");
        if (nome == null) {
          JSONObject obj = new JSONObject();
          obj.put("message", "Missing nome.");
          responsearray.add(obj);
          continue;
        }
        // Checks localidade
        String localidade = (String) user.get("localidade");
        if (localidade == null) {
          JSONObject obj = new JSONObject();
          obj.put("message", "Missing localidade.");
          responsearray.add(obj);
          continue;
        }

        // Calls RMI to insert user
        try {
          ResponseObject response = jdbc.insertUser(new User(nome, birthday, localidade));
          responsearray.add(response.getObject());
        } catch (RemoteException e) {
          e.printStackTrace();
          JSONObject obj = new JSONObject();
          obj.put("message", "RMIServer error.");
          responsearray.add(obj);
        }

      }
    } catch (Exception e) {
      res.status(400);
      return returnJSONMessage("Wrong JSON format. Provide JSONArray.");
    }
    res.status(200);
    return responsearray.toJSONString();
  }

  @SuppressWarnings("unchecked")
  private static String insertUser(Request req, Response res) {
    // Checks date
    LocalDate birthday = processDate(req.queryParams("datanascimento"));
    if (birthday == null) {
      JSONObject obj = new JSONObject();
      res.status(400);
      obj.put("message", "Wrong datanascimento format.");
      return obj.toJSONString();
    }
    // Checks name
    String nome = req.queryParams("nome");
    if (nome == null) {
      JSONObject obj = new JSONObject();
      res.status(400);
      obj.put("message", "Missing nome.");
      return obj.toJSONString();
    }
    // Checks localidade
    String localidade = req.queryParams("localidade");
    if (localidade == null) {
      JSONObject obj = new JSONObject();
      res.status(400);
      obj.put("message", "Missing localidade.");
      return obj.toJSONString();
    }

    // Calls RMI to insert user
    try {
      ResponseObject response = jdbc.insertUser(new User(nome, birthday, localidade));
      res.status(response.getStatus());
      return response.getObject().toJSONString();
    } catch (RemoteException e) {
      e.printStackTrace();
      JSONObject obj = new JSONObject();
      res.status(500);
      obj.put("message", "RMIServer error.");
      return obj.toJSONString();
    }
  }
}
