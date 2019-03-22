package me.IAGO.Item;

import java.util.List;
/*
 * 
 */
public interface FileSystem_Intfc {
    
    public boolean AddUserInfo(String username, String password);
    public boolean RemoveUserInfo(String username);
    public boolean ChangeUserPassword(String username, String newpassword);
    public String GetUserPassword(String username);
    
    public List<StoreDate_Intfc> GetUserFileIndex(String username);
    public boolean SaveUserFile(String username, Byte data, StoreDate_Intfc date);
    public boolean DeleteUserFile(String username, StoreDate_Intfc date);
    public Byte GetUserFile(String username, StoreDate_Intfc date);
}
