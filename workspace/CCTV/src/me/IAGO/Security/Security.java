package me.IAGO.Security;

import java.util.UUID;

import me.IAGO.Item.FileSystem_Intfc;

public class Security implements Security_Intfc {    
    private FileSystem_Intfc _filesystem;
    private String _username = "", _uuid = UUID.randomUUID().toString();
    private boolean _passing;
    
    public Security(FileSystem_Intfc filesystem){
        _filesystem = filesystem;
        _passing = false;
    }
    
    public String GetUsername() {
        return _username;
    }
    
    @Override
    public boolean VerificationStatus() {
        return _passing;
    }

    @Override
    public String OnetimeVerificationInfo(String username, int verificationtimeout) {
        String re = "";
        if(!_passing) {
            _username = username;
            re = _uuid;
        }
        return re;
    }

    @Override
    public boolean Verification(String verificationinfo) {
        String password = _filesystem.GetUserPassword(_username);
        if(password != null) {
            StringBuffer stringconnect = new StringBuffer();
            stringconnect.append(_username);
            stringconnect.append(_uuid);
            stringconnect.append(password);
            if(verificationinfo.equals(SHA1(stringconnect.toString()))) {
                _passing = true;
            }
        }
        return _passing;
    }

    @Override
    public String DecryptData(String encrypteddata) {
        String re = null;
        if(_passing) {
            re = encrypteddata;
        }
        return re;
    }
    
    @Override
    public PrivilegeLevel Privilege(String coreownername) {
        // TODO
        if(!_passing) {
            return PrivilegeLevel.None;
        }
        if(coreownername.equals(_username)) {
            return PrivilegeLevel.Owner;
        }
        return PrivilegeLevel.Group;
    }
        
    private String SHA1(String data){
        return data;
    }
}
