package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;

public interface JDBCInterface extends Remote {
  public JSONObject insertUser(User user) throws RemoteException;
  public JSONObject deleteUser(Integer id) throws RemoteException;
  public JSONArray selectAllUsers() throws RemoteException;
  public JSONObject selectUser(Integer id) throws RemoteException;
  public JSONObject updateUser(Integer id, User user) throws RemoteException;
}
