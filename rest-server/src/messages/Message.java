package messages;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = -1545258312706745596L;
  // Attributes
  private String message;
  private Timestamp date;

  // Constructor
  public Message(String message) {
    this.message = message;
    this.date = new Timestamp(System.currentTimeMillis());
  }

  public Message(String message, Timestamp date) {
    this.message = message;
    this.date = date;
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
