package me.IAGO.Item;

public enum Label {
    SERVER("server"),
    SERVER_LOGON("serverlogon"),
    SERVER_LOGONCONFIRM("coreserver_logonconfirm"),
    SERVER_LOGIN("coreserver_login"),
    SERVER_VERIFICATION("coreserver_verification"),
    SERVER_VERIFICATION_STATUS("coreserver_verification_status"),
    SERVER_GETMEDIAINDEX("coreserver_getmediaindex"),
    SERVER_DELETEMEDIA("coreserver_deletemedia"),
    SERVER_PUSHMEDIA("coreserver_pushmedia"),
    SERVER_STARTWATCH("coreserver_startwatch"),
    SERVER_STOPWATCH("coreserver_stopwatch"),
    SERVER_PULLMEDIA("coreserver_pullmediadata"),
    
    FIELD_PUBLICKEY("publiykey"),
    FIELD_MESSAGE("logonmessage"),
    FIELD_USERNAME("logonusername"),
    FIELD_USERPASSWORD("logonuserpassword"),
    FIELD_ONETIMECODE("login_onetimecode"),
    FIELD_VERIFICATIONSTATUS("verification_status"),
    FIELD_MEDIAINDEX("mediaindex"),
    FIELD_MEDIAINDEXNUM("mediaindexnum"),
    FIELD_MEDIADATA("mediadata"),
    FIELD_MEDIADOWNLOAD("downloadmedia"),
    
    FIELD_MEDIASAVE("mediasave"),
    FIELD_MEDIATIMELIMIT("mediatimelimit"),
    
    FIELD_STOREDATESTART("datestart"),
    FIELD_STOREDATEEND("dateend"),
    
    CONST_STORETIMEFORMAT("yyyy-MM-dd HH:mm:ss"),
    CONST_DATABASEDRIVER("oracle.jdbc.OracleDriver"),
    CONST_DATABASEURL("jdbc:oracle:thin://localhost:1521/sqltestdb"),
    CONST_DATABASEUSERNAME(""),
    CONST_DATABASEPASSWORD(""),
    
    CONST_SQLADD("INSERT INTO userinfo VALUES(?,?)"),
    CONST_SQLDELETE("DELETE FROM USERINFO WHERE username = ?"),
    CONST_SQLUPGRADE("UPDATE userinfo SET password = ? WHERE username = ?"),
    CONST_SQLQUERY("SELECT * FROM userinfo WHERE username = ?"),
    
    CONST_SQLUSERNAME("scott"),
    CONST_SQLUSERPASSWORD("tiger"),
    
    CONST_FILEMAINDIR("E:/Media"),
    CONST_FILEINDEX("index.json"),
    FIELD_FILEINDEXNUM("fileindexnum")
    ;

    private String _str;
    
    private Label(String str){
        _str = str;
    }
    public String toString() {
        return _str;
    }
}
