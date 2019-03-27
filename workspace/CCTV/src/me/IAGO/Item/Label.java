package me.IAGO.Item;

public enum Label {
    PORT_CONNECTCORE("port_connectcore"),
    PORT_CONNECTCORE_OPEN("port_coreopen"),
    PORT_CONNECTCORE_STABLE("port_corestable"),
    PORT_CONNECTCORE_CLOSE("port_coreclose"),
    FIELD_COREOWNERNAME("port_ownername"),
    FIELD_COREMESSAGE("port_coremessage"),
    
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
    SERVER_CONFIGMEDIA("coreserver_configmedia"),
    
    FIELD_PUBLICKEY("publiykey"),
    FIELD_MESSAGE("message"),
    FIELD_USERNAME("username"),
    FIELD_USERPASSWORD("userpassword"),
    FIELD_ONETIMECODE("onetimecode"),
    FIELD_VERIFICATIONSTATUS("verification_status"),
    FIELD_MEDIAINDEX("mediaindex"),
    FIELD_MEDIAINDEXNUM("mediaindexnum"),
    FIELD_MEDIADATA("mediadata"),
    FIELD_MEDIADOWNLOAD("downloadmedia"),
    FIELD_CONFIGMEDIA("configmedia"),
    
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
    
    CONST_FILEMAINDIR("F:/Media"),
    CONST_FILEINDEX("mediaindex"),
    CONST_FILEBUFFERSIZE("1024"),
    FIELD_FILEINDEXNUM("fileindexnum"),
    
    CONST_LOGDIR("F:/Media"),
    CONST_LOGNAME("logger.txt");

    private String _str;
    
    private Label(String str){
        _str = str;
    }
    public String toString() {
        return _str;
    }
    public synchronized static Label fromString(String text) {
        if (text != null) {
            for (Label b : Label.values()) {
                if (text.equals(b.toString())) {
                    return b;
                }
            }
        }
        return null;
    }
}
