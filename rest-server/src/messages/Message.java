package messages;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

public class Message {
	// Attributes
	private String message;
	private Timestamp date;

	// Constructor
	public Message(String message, Timestamp date) {
		this.message = message;
		this.date = date;
	}

	// Convert to JSON
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("message", this.message);
		obj.put("date", this.date.toString());
		return obj;
	}

	// Get & Set
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
}
