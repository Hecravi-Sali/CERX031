package me.IAGO.UnitTest;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.alibaba.druid.util.Base64;

import me.IAGO.Core.Core;
import me.IAGO.Item.FileSystem;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.FileSystem_NoDatabase;
import me.IAGO.Item.Label;
import me.IAGO.Item.StoreDate;
import me.IAGO.Log.Log;
import me.IAGO.Log.Log_Intfc;
import me.IAGO.Media.Media;
import me.IAGO.Media.Media_Intfc.MediaDataWatcher;

public class UnitTest {

    private static Log_Intfc _logger = new Log();
    @Test
    public void test() throws UnsupportedEncodingException, InterruptedException {
        FileSystem file = new FileSystem(_logger);
        Date starttime = new Date();
        Thread.sleep(500);
        Date endtime = new Date();
        String data = "worinixianren";
        if(true) {
            file.SaveUserFile(
                    "halaoshi", 
                    data, 
                    new StoreDate(starttime, endtime));
        }
        System.out.println(data);
        System.out.println(file.GetUserFileIndex("halaoshi").get(0));
        System.out.println(
                file.GetUserFile(
                        "halaoshi", 
                        file.GetUserFileIndex("halaoshi").get(0)));
        System.out.println(file.GetUserFileIndex("halaoshi"));
        if(true) {
            file.DeleteUserFile("halaoshi", file.GetUserFileIndex("halaoshi").get(0));
        }
    }
    
    @Test
    public void MediaUnitTest() throws InterruptedException, JSONException, ParseException {
        String username = "halaoshi";
        FileSystem file = new FileSystem(_logger);
        Media media = new Media(file, username);
        media.StartMediaForward(
                "1", 
                new MediaDataWatcher() {
                    @Override
                    public boolean Push(String data) {
                        System.out.println("1" + "Get message" + data);
                        return true;
                    }
                });
        media.StartMediaForward(
                "2", 
                new MediaDataWatcher() {
                    @Override
                    public boolean Push(String data) {
                        System.out.println("2" + "Get message" + data);
                        return true;
                    }
                });
        
        media.PushMediaData("saonima");
        
        media.StopMediaForward("1");
        
        JSONObject conf = new JSONObject();
        conf.put(Label.FIELD_MEDIASAVE.toString(), true);
        conf.put(Label.FIELD_MEDIATIMELIMIT.toString(), 1);
        assertTrue(media.Config(conf));
        StringBuffer checkin = new StringBuffer();
        for(int i = 0; i < 20; i++) {
            String rand = String.valueOf(Math.random());
            media.PushMediaData(Base64.byteArrayToBase64(rand.getBytes()));
            checkin.append(rand);
            Thread.sleep(250);
        }
        conf.remove(Label.FIELD_MEDIASAVE.toString());
        conf.put(Label.FIELD_MEDIASAVE.toString(), false);
        assertTrue(media.Config(conf));
        
        JSONObject index = media.GetMediaRecordDate();
        String getdata = new String(Base64.base64ToByteArray(media.PullMediaData(index)));
        System.out.println(checkin);
        System.out.println(getdata);
        assertTrue(checkin.toString().equals(getdata));
        if(true) {
            media.DelectMediaDate(media.GetMediaRecordDate());
        }
    }
    
    private void Server_Logon(Core core, String uuid, String username, String password) {
        JSONObject json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_LOGON.toString());
        
        System.out.println(core.OnMessage(uuid, json));
        
        json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_LOGONCONFIRM.toString());
        JSONObject userinfo = new JSONObject();
        userinfo.put(Label.FIELD_USERNAME.toString(), username);
        userinfo.put(Label.FIELD_USERPASSWORD.toString(), password); 
        json.put(Label.FIELD_MESSAGE.toString(), userinfo.toString());

        System.out.println(core.OnMessage(uuid, json));;
    }
    
    private void Server_Login(Core core, String uuid, String username, String password) {
        JSONObject json = new JSONObject();
        JSONObject toserver;
        
        json.put(Label.SERVER.toString(), Label.SERVER_LOGIN.toString());
        json.put(Label.FIELD_USERNAME.toString(), username);
        toserver = core.OnMessage(uuid, json);
        System.out.println(toserver);
        
        json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_VERIFICATION.toString());
        json.put(
                Label.FIELD_MESSAGE.toString(), 
                username + toserver.getString(Label.FIELD_ONETIMECODE.toString()) + password);
        System.out.println(core.OnMessage(uuid, json));
    }
    
    private void Server_StartWatch(Core core, String uuid) {
        JSONObject json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_STARTWATCH.toString());
        System.out.println(core.OnMessage(uuid, json));  
    }
    
    private void Server_StopWatch(Core core, String uuid) {
        JSONObject json = new JSONObject();        
        json.put(Label.SERVER.toString(), Label.SERVER_STOPWATCH.toString());
        System.out.println(core.OnMessage(uuid, json));  
    }
    
    private void Server_PushData(Core core, String uuid, String data) {
        JSONObject json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_PUSHMEDIA.toString());
        json.put(Label.FIELD_MEDIADATA.toString(), data);
        System.out.println(core.OnMessage(uuid, json));
    }
    
    private void Server_Config(Core core, String uuid, boolean issave, int timelimit) {
        JSONObject json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_CONFIGMEDIA.toString());
        JSONObject conf = new JSONObject();
        conf.put(Label.FIELD_MEDIASAVE.toString(), issave);
        conf.put(Label.FIELD_MEDIATIMELIMIT.toString(), timelimit);
        json.put(Label.FIELD_CONFIGMEDIA.toString(), conf.toString());
        System.out.println(core.OnMessage(uuid, json));
    }
    
    private JSONObject Server_Getindex(Core core, String uuid) {
        JSONObject json = new JSONObject();
        json.put(Label.SERVER.toString(), Label.SERVER_GETMEDIAINDEX.toString());
        json = core.OnMessage(uuid, json);
        System.out.println(json);
        return json;
    }
    
    private void Server_DeleteMedia(Core core, String uuid, JSONObject index) {
        index.put(Label.SERVER.toString(), Label.SERVER_DELETEMEDIA.toString());
        System.out.println(core.OnMessage(uuid, index));
    }
    
    private String Server_PullMedia(Core core, String uuid, JSONObject index) {
        index.put(Label.SERVER.toString(), Label.SERVER_PULLMEDIA.toString());
        return new String(Base64.base64ToByteArray(core.OnMessage(uuid, index).getString(Label.FIELD_MEDIADOWNLOAD.toString())));
    }
    
    @Test
    public void CoreUnitTest() throws InterruptedException {
        final String username1 = "halaoshi";
        final String password1 = "123";
        final String uuid1 = UUID.randomUUID().toString();
        FileSystem_Intfc filesystem = new FileSystem_NoDatabase(_logger);
        Core core = new Core(username1, filesystem, _logger);
        
        core.OnOpen(
                uuid1,
                (String data) ->{
                    System.out.println("uuid1 Get Data to Server : " + data);
                    return true;
                });
        Server_Logon(core, uuid1, username1, password1);
        Server_Login(core, uuid1, username1, password1);
        Server_Login(core, uuid1, username1, password1);
        
        final String username2 = "daye";
        final String password2 = "123";
        final String uuid2 = UUID.randomUUID().toString();
        core.OnOpen(
                uuid2,
                (String data) ->{
                    System.out.println("uuid2 Get Data to Server : " + data);
                    return true;
                });
        Server_Logon(core, uuid2, username2, password2);
        Server_Login(core, uuid2, username2, password2);
        
        Server_StartWatch(core, uuid1);
        Server_StartWatch(core, uuid2);
        
        Server_PushData(core, uuid2, "uuid 2 send data");
        Server_PushData(core, uuid1, "uuid 1 send data");
        
        Server_StopWatch(core, uuid1);
        Server_PushData(core, uuid1, "uuid 1 send data");

        Server_Config(core, uuid2, true, 1);
        Server_Config(core, uuid1, true, 1);
        
        StringBuffer CheckBuffer = new StringBuffer();
        for(int i = 0; i < 10; i++) {
            String rand = String.valueOf(Math.random());
            Server_PushData(core, uuid1, Base64.byteArrayToBase64(rand.getBytes()));
            CheckBuffer.append(rand);
            Thread.sleep(250);
        }
        
        Server_Config(core, uuid1, false, 1);
        Server_PushData(core, uuid1, Base64.byteArrayToBase64(String.valueOf(Math.random()).getBytes()));

        CheckBuffer.toString().equals(Server_PullMedia(core, uuid1, Server_Getindex(core, uuid1)));
        
        Server_DeleteMedia(core, uuid2, Server_Getindex(core, uuid1));
        Server_DeleteMedia(core, uuid1, Server_Getindex(core, uuid1));
    }
}
