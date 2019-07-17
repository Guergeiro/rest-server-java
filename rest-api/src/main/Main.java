package main;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static spark.Spark.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import messages.Message;
import spark.Response;

public class Main {
	// Attributes
	private static HashMap<Integer, Message> greetings = new HashMap<>();
	private static Integer count = 0;

	public static void main(String[] args) {
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
}