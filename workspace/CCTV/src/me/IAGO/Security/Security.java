package me.IAGO.Security;

import java.util.UUID;

import me.IAGO.Item.FileSystem_Intfc;

public class Security implements Security_Intfc {    
    private FileSystem_Intfc _filesystem;
    private String _username = "", _uuid;
    private boolean _passing;
    
    public Security(FileSystem_Intfc filesystem){
        _filesystem = filesystem;
        _passing = false;
    }
    
    @Override
    public boolean VerificationStatus() {
        return _passing;
    }

    @Override
    public String OnetimeVerificationInfo(String username, int verificationtimeout) {
        _username = username;
        return _uuid = UUID.randomUUID().toString();
    }

    @Override
    public boolean Verification(String verificationinfo) {
        String password = _filesystem.GetUserPassword(_username);
        if(password != null) {
            StringBuffer stringconnect = new StringBuffer();
            stringconnect.append(_username);
            stringconnect.append(_uuid);
            stringconnect.append(password);
            if(verificationinfo == SHA1(stringconnect.toString())) {
                _passing = true;
            }
        }
        return _passing;
    }

    @Override
    public Byte DecryptData(Byte encrypteddata) {
        Byte re = null;
        if(_passing) {
            re = encrypteddata;
        }
        return re;
    }
    
    @Override
    public PrivilegeLevel Privilege(String coreownername) {
        // TODO
        if(_passing) {
            return PrivilegeLevel.None;
        }
        return PrivilegeLevel.Owner;
    }
        
    private String SHA1(String data){
        return data;
    }
}
