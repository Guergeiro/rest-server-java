package main;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map.Entry;

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
			return updateGreeting(res, Integer.valueOf(req.params(":id")), new Message(req.queryParams("message"), new Timestamp(System.currentTimeMillis())));
		});

		delete("/greetings/:id", (req, res) -> {
			return deleteGreeting(res, Integer.valueOf(req.params(":id")));
		});

		post("/greetings", (req, res) -> {
			return createGreeting(res, new Message(req.queryParams("message"), new Timestamp(System.currentTimeMillis())));
		});

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
		JSONObject obj = new JSONObject();
		Message message = greetings.get(id);
		if (message != null) {
			obj.put(id, message.toJSON());
			res.status(200);
		} else {
			obj.put("message", "Key doesn't exist.");
			res.status(400);
		}
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String updateGreeting(Response res, Integer id, Message message) {
		res.type("application/json");
		Message value = greetings.get(id);
		JSONObject obj = new JSONObject();
		if (value != null) {
			greetings.put(id, message);
			obj.put(id, message.toJSON());
			res.status(200);
		} else {
			obj.put("message", "Key doesn't exist.");
			res.status(400);
		}
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String createGreeting(Response res, Message message) {
		res.type("application/json");
		res.status(200);
		JSONObject obj = new JSONObject();
		greetings.put(++count, message);
		obj.put(count, message.toJSON());
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String deleteGreeting(Response res, Integer id) {
		res.type("application/json");
		JSONObject obj = new JSONObject();
		Message message = greetings.get(id);
		if (message != null) {
			obj.put("message", "Delete Successful.");
			res.status(200);
			greetings.remove(id);
		} else {
			obj.put("message", "Key doesn't exist.");
			res.status(400);
		}
		return obj.toJSONString();
	}
}