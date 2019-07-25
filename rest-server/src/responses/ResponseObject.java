package responses;

import java.io.Serializable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ResponseObject implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 5269497055369364941L;
  // Attributes
  private Integer status;
  private JSONObject object;
  private JSONArray array;
  
  // Constructor
  public ResponseObject(Integer status, JSONObject object) {
    this.status = status;
    this.object = object;
  }
  
  public ResponseObject(Integer status, JSONArray array) {
    this.status = status;
    this.array = array;
  }

  // Get & Set
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public JSONObject getObject() {
    return object;
  }

  public void setObject(JSONObject object) {
    this.object = object;
  }

  public JSONArray getArray() {
    return array;
  }

  public void setArray(JSONArray array) {
    this.array = array;
  }
}
