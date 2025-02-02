package interfaces.database.dao;

import core.entities.User;

public interface IUserDAO {
    public int getUserID(String email);

    public boolean userExists(int id);

    public boolean userExists(String email);

    public boolean validCredentials(String email, String password);

    public void addUser(String name, String email, String password, String role);

    public void addUser(String name, String email, String password, String role, String gender, String contactNo, String address);

    public void deleteUser(int id);

    public void deleteUser(String email);

    public User searchUser(int id);

    public User searchUser(String email);

    public String getUserRole(int id);

    public String getUserRole(String email);

    public boolean updateUser(int id, String name, String email, String password, String role, String gender, String contactNo, String address);

    public boolean updateUser(int id, String name, String email, String password, String role);
}
