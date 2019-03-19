package me.IAGO.Security;

import java.util.Date;
import java.util.UUID;

import me.IAGO.Item.FileSystem_Intfc;

public class Security implements Security_Intfc {
    private FileSystem_Intfc _filesystem;
    private Date _lastaction;
    private int _timeout;
    private String _username, _uuid;
    
    @Override
    public boolean GetVerificationStatus() {
        return (new Date().getTime() - _lastaction.getTime()) / 1000 >= _timeout;  
    }

    @Override
    public String OnetimeVerificationInfo(String username, int verificationtimeout) {
        watchdog();
        _timeout = verificationtimeout;
        _username = username;
        _uuid = UUID.randomUUID().toString();
        return _uuid;
    }

    @Override
    public boolean Verification(String verificationinfo) {
        if(GetVerificationStatus()) {
            watchdog();
            String password = _filesystem.GetUserPassword(_username);
            if(password != null) {
                StringBuffer stringconnect = new StringBuffer();
                stringconnect.append(_username);
                stringconnect.append(_uuid);
                stringconnect.append(password);
                if(verificationinfo == SHA1(stringconnect.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Byte DecryptData(Byte encrypteddata) {
        Byte re = null;
        if(GetVerificationStatus()) {
            watchdog();
            re = encrypteddata;
        }
        return re;
    }

    private String SHA1(String data){
        return data;
    }
    
    private void watchdog() {
        _lastaction = new Date();
    }
}
