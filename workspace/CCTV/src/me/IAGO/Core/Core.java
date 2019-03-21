package me.IAGO.Core;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import me.IAGO.Item.Label;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Media.Media;
import me.IAGO.Media.Media_Intfc;
import me.IAGO.Security.Security;
import me.IAGO.Security.Security_Intfc.PrivilegeLevel;

public class Core {
    private class PortInfo {
        public Security security;
        public Media_Intfc.MediaDataWatcher mediawatcher;
        PortInfo(FileSystem_Intfc filesystem, Media_Intfc.MediaDataWatcher sendfunction) {
            security = new Security(filesystem);
            mediawatcher = sendfunction;
        }
    }
    
    static private FileSystem_Intfc _filesystem = null;
    private Media _media;
    private Map<String, PortInfo> _userlinkport;
    
    Core(String ownername){
        if(_filesystem == null) {
            //  TODO 文件系统初始化
        }
        _media = new Media(_filesystem, ownername);
        _userlinkport = new HashMap<>();  
    }
    
    public synchronized void OnOpen(String portid, Byte message, Media_Intfc.MediaDataWatcher sendfunction) {
        _userlinkport.put(portid, new PortInfo(_filesystem, sendfunction));
    }
    
    public synchronized JSONObject OnMessage(String portid, Byte message) {
        JSONObject backto = new JSONObject();
        PortInfo portinfo = _userlinkport.get(portid);
        try {
            JSONObject json = new JSONObject(message.toString());
            switch(Label.valueOf(json.getString(Label.SERVER.toString()))) {
            case SERVER_LOGON : {
                backto.put(
                        Label.FIELD_PUBLICKEY.toString(), 
                        GetPublicKey());
            }
            case SERVER_LOGONCONFIRM : {
                JSONObject logonmessage = 
                        new JSONObject(PrivatekeyDecryption(json.getString(Label.FIELD_MESSAGE.toString())));
                try {
                    LogonInfoCheck(logonmessage);
                    _filesystem.AddUserInfo(
                            logonmessage.getString(Label.FIELD_USERNAME.toString()), 
                            logonmessage.getString(Label.FIELD_USERPASSWORD.toString()));
                }
                catch(Exception e) {
                    // TODO 用户名或者密码检查错误
                }
            }
            case SERVER_LOGIN : {
                backto.put(
                        Label.FIELD_ONETIMECODE.toString(), 
                        portinfo.security.OnetimeVerificationInfo(
                                json.getString(
                                        Label.FIELD_USERNAME.toString()), 
                                300));
            }
            case SERVER_VERIFICATION : {
                backto.put(
                        Label.FIELD_VERIFICATIONSTATUS.toString(), 
                        portinfo.security.Verification(json.getString(Label.FIELD_MESSAGE.toString())));     
            }
            case SERVER_VERIFICATION_STATUS : {
                backto.put(
                        Label.FIELD_VERIFICATIONSTATUS.toString(), 
                        portinfo.security.VerificationStatus());     
            }
            case SERVER_GETMEDIAINDEX : {
                if(portinfo.security.VerificationStatus()) {
                    backto.put(
                            Label.FIELD_MEDIAINDEX.toString(), 
                            _media.GetMediaRecordDate());
                }
            }
            case SERVER_DELETEMEDIA : {
                _media.DelectMediaDate(json.getJSONObject(Label.FIELD_MEDIAINDEX.toString()));
            }
            case SERVER_PUSHMEDIA: {
                if(portinfo.security.Privilege(_media.OwnerName()) == PrivilegeLevel.Owner) {
                    _media.PushMediaData(new Byte(json.getString(Label.FIELD_MEDIADATA.toString())));
                }
            }
            case SERVER_STARTWATCH : {
                //  TODO 权限检查 
                if(portinfo.security.VerificationStatus()) {
                    _media.StartMediaForward(
                            portid, 
                            new Media_Intfc.MediaDataWatcher() {
                                @Override
                                public boolean Push(Byte data) {
                                    JSONObject json = new JSONObject();
                                    json.put(
                                            Label.FIELD_MEDIADATA.toString(), 
                                            data.toString());
                                    portinfo.mediawatcher.Push(new Byte(json.toString()));
                                    return true;
                                }});
                }
            }
            case SERVER_STOPWATCH : {
                if(portinfo.security.VerificationStatus()) {
                    _media.StopMediaForward(portid);
                }
            }
            case SERVER_PULLMEDIA : {
                if(portinfo.security.VerificationStatus()) {
                    backto.put(
                            Label.FIELD_MEDIADOWNLOAD.toString(), 
                            _media.PullMediaData(json.getJSONObject(Label.SERVER_GETMEDIAINDEX.toString())));
                }
            }

            default:
                //  TODO 无法处理的请求打日志
                break;
            }
        }
        catch (ParseException w) {
            
        }
        catch (JSONException e) {
            
        }
        catch (IllegalArgumentException e) {
            
        }
        return backto;
    }
    
    public synchronized void OnClose(String portid) {
        _userlinkport.remove(portid);
    }
    
    public synchronized void OnError(String portid, Throwable error) {
        //  TODO 记录服务器日志
    }
    
    
    private String GetPublicKey() {
        return "";
    }
    private String PrivatekeyDecryption(String data) {
        return data;
    }
    private void LogonInfoCheck(JSONObject logoninfo) throws Exception {
        
    }
}
