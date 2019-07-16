package main;

import java.util.HashMap;
import java.util.Map.Entry;

import static spark.Spark.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Main {
	// Attributes
	private static HashMap<Integer, String> greetings = new HashMap<>();
	private static Integer count = 0;

	public static void main(String[] args) {
		get("/greetings", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return allGreetings();
		});

		get("/greetings/:id", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return oneGreeting(Integer.valueOf(req.params(":id")));
		});

		put("/greetings/:id", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return updateGreeting(Integer.valueOf(req.params(":id")), req.queryParams("message"));
		});

		delete("/greetings/:id", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return deleteGreeting(Integer.valueOf(req.params(":id")));
		});

		post("/greetings", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return createGreeting(req.queryParams("message"));
		});

	}

	@SuppressWarnings("unchecked")
	private static String allGreetings() {
		JSONArray array = new JSONArray();
		for (Entry<Integer, String> entry : greetings.entrySet()) {
			JSONObject obj = new JSONObject();
			obj.put(entry.getKey(), entry.getValue());
			array.add(obj);
		}
		return array.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String oneGreeting(Integer id) {
		JSONObject obj = new JSONObject();
		String message = greetings.get(id);
		if (message != null) {
			obj.put(id, message);
		} else {
			obj.put("message", "Key doesn't exist.");
		}
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String updateGreeting(Integer id, String message) {
		String value = greetings.get(id);
		JSONObject obj = new JSONObject();
		if (value != null) {
			greetings.put(id, message);
			obj.put(id, message);
		} else {
			obj.put("message", "Key doesn't exist.");
		}
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String createGreeting(String message) {
		JSONObject obj = new JSONObject();
		greetings.put(++count, message);
		obj.put("message", "Message created.");
		return obj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static String deleteGreeting(Integer id) {
		JSONObject obj = new JSONObject();
		String message = greetings.get(id);
		if (message != null) {
			obj.put("message", "Delete Successful.");
			greetings.remove(id);
		} else {
			obj.put("message", "Key doesn't exist.");
		}
		return obj.toJSONString();
	}
}