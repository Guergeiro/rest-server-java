package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.json.simple.JSONObject;
import user.User;

public interface JDBCInterface extends Remote {
  public JSONObject insertUser(User user) throws RemoteException;
  public String deleteUser(Integer id) throws RemoteException;
  public String selectAllUsers() throws RemoteException;
  public String selectUser(Integer id) throws RemoteException;
  public String updateUser(Integer id, User user) throws RemoteException;
}
