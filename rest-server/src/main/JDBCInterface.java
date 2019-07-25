package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import messages.Message;
import responses.ResponseObject;
import users.User;

public interface JDBCInterface extends Remote {
  public ResponseObject insertUser(User user) throws RemoteException;
  public ResponseObject deleteUser(Integer id) throws RemoteException;
  public ResponseObject selectAllUsers() throws RemoteException;
  public ResponseObject selectUser(Integer id) throws RemoteException;
  public ResponseObject updateUser(Integer id, User user) throws RemoteException;
  
  public ResponseObject insertMessage(Message message) throws RemoteException;
  public ResponseObject deleteMessage(Integer id) throws RemoteException;
  public ResponseObject selectAllMessages() throws RemoteException;
  public ResponseObject selectMessage(Integer id) throws RemoteException;
  public ResponseObject updateMessage(Integer id, Message message) throws RemoteException;
}
