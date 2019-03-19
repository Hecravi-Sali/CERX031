package me.IAGO.Core;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Media.Media;
import me.IAGO.Security.Security;

public class Core {
    final String SERVER = "coreserver";
    
    final String SERVER_LOGIN = "coreserver_login";
    final String SERVER_VERIFICATION = "coreserver_verification";
    final String SERVER_VERIFICATION_STATUS = "coreserver_verification_status";
    final String SERVER_GETMEDIAINDEX = "coreserver_getmediaindex";

    final String OPERATONSTATUS = "operationstatus";
    final String LOGIN_USERNAME = "loginusername";
    final String LOGIN_ONETIMECODE = "login_onetimecode";
    final String LOGIN_USERVERIFICATIONCODE = "loginuservcode"; 
    final String VERIFICATION_STATUS = "verification_status";
    
    final String MEDIA_INDEX = "mediaindex";
    
    
    final int PORT_TIMEOUT = 300;
    
    private FileSystem_Intfc _filesystem;
    private Media _media;
    private Map<String, Security> _userlinkport;
    
    Core(FileSystem_Intfc filesystem, String username){
        
    }
    
    public void OnOpen(String portid, Byte message) {
        _userlinkport.put(portid, new Security(_filesystem));
    }
    
    public JSONObject OnMessage(String portid, Byte message) {
        JSONObject backto = new JSONObject();
        
        //  TODO 操作成功与否的提示
        backto.put(OPERATONSTATUS, false);
        
        Security portsecurity = _userlinkport.get(portid);
        try {
            JSONObject json = new JSONObject(message.toString());
            switch(json.getString(SERVER)) {
            case SERVER_LOGIN : {
                backto.put(LOGIN_ONETIMECODE, portsecurity.OnetimeVerificationInfo(json.getString(LOGIN_USERNAME), PORT_TIMEOUT));
            }
            case SERVER_VERIFICATION : {
                backto.put(VERIFICATION_STATUS, portsecurity.Verification(json.getString(LOGIN_USERVERIFICATIONCODE)));     
            }
            case SERVER_VERIFICATION_STATUS : {
                backto.put(VERIFICATION_STATUS, portsecurity.GetVerificationStatus());     
            }
            case SERVER_GETMEDIAINDEX : {
                if(portsecurity.GetVerificationStatus()) {
                    backto.put(MEDIA_INDEX, _media.GetMediaRecordDate());
                }
                else {
                    
                }
            }
            
            }
        }
        catch (JSONException e) {
            
        }
        return backto;
    }
    
    public void OnClose(String portid) {
        _userlinkport.remove(portid);
    }
    
    public void OnError(String portid, Throwable error) {
        
    }
}
