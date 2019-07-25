package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import messages.Message;
import responses.ResponseObject;
import users.User;

public interface JDBCInterface extends Remote {
  public ResponseObject insertUser(User user) throws RemoteException;
  public ResponseObject deleteUser(Integer id) throws RemoteException;
  public ResponseObject selectAllUsers() throws RemoteException;
  public ResponseObject selectUser(Integer id) throws RemoteException;
  public ResponseObject updateUser(Integer id, User user) throws RemoteException;
  
  public JSONObject insertMessage(Message message) throws RemoteException;
  public JSONObject deleteMessage(Integer id) throws RemoteException;
  public JSONArray selectAllMessages() throws RemoteException;
  public JSONObject selectMessage(Integer id) throws RemoteException;
  public JSONObject updateMessage(Integer id, Message message) throws RemoteException;
}
