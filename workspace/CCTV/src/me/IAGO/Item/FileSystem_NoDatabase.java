package me.IAGO.Item;

import java.util.HashMap;
import java.util.Map;

import me.IAGO.Log.Log_Intfc;

public class FileSystem_NoDatabase extends  FileSystem {        
    public FileSystem_NoDatabase(Log_Intfc log) {
        super(log);
        AddUserInfo("admin", "admin");
    }

    Map<String, String>_userinfo = new HashMap<>();
    
    @Override
    public boolean AddUserInfo(String username, String password) {
        boolean re = false;
        if(_userinfo.get(username) == null) {
            _userinfo.put(username, password);
            re = true;
        }
        return re;
    }
    
    @Override
    public boolean RemoveUserInfo(String username) {
        return _userinfo.remove(username) != null;
    }
    
    @Override
    public boolean ChangeUserPassword(String username, String newpassword) {
        boolean re = false;
        if(_userinfo.remove(username) != null) {
            _userinfo.put(username, newpassword);
            re = true;
        }
        return re;
    }
    
    @Override
    public String GetUserPassword(String username) {
        return _userinfo.get(username);
    }
}
