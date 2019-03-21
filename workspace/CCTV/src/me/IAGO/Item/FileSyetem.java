package me.IAGO.Item;

import java.nio.Buffer;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import antlr.ByteBuffer;

public class FileSyetem implements FileSystem_Intfc {
    private interface DatabaseParameter {
        public PreparedStatement Filling(PreparedStatement pstm) throws SQLException;
    }
    
    private static Lock _databaselock = new ReentrantLock();
    
    @Override
    public boolean AddUserInfo(String username, String password) {
        ResultSet re = DatabaseOperator(
                Label.CONST_SQLADD.toString(),
                (pstm) -> {
                    pstm.setString(1, username);
                    pstm.setString(2, password);  
                    return pstm;
                });
        return re != null;
    }

    @Override
    public boolean RemoveUserInfo(String username) {
        ResultSet re = DatabaseOperator(
                Label.CONST_SQLQUERY.toString(),
                (pstm) -> {
                    pstm.setString(1, username);  
                    return pstm;
                });
        return re != null;
    }

    @Override
    public boolean ChangeUserPassword(String username, String newpassword) {
        ResultSet re = DatabaseOperator(
                Label.CONST_SQLUPGRADE.toString(),
                (pstm) -> {
                    pstm.setString(1, newpassword);
                    pstm.setString(2, username);
                    return pstm;
                });
        return re != null;
    }

    @Override
    public String GetUserPassword(String username) {
        String password = null;
        ResultSet re = DatabaseOperator(
                Label.CONST_SQLUPGRADE.toString(),
                (pstm) -> {
                    pstm.setString(1, username);
                    return pstm;
                });
        if(re != null) {
            try {
                password = re.getString(1);
            } catch (SQLException e) {
                // TODO 日志
            } 
        }
        return password;
    }

    @Override
    public List<StoreDate_Intfc> GetUserFileIndex(String username) {
        StringBuffer dir = new StringBuffer();
        File userdir = new File(Label.CONST_FILEMAINDIR.toString(), username);
        File userindex = new File(userdir, Label.CONST_FILEINDEX.toString());
        if(!userdir.exists()) {
            userdir.mkdirs();
            try {
                userindex.createNewFile();
            } catch (IOException e) {
                // TODO 文件创建失败
            }
        }
        if(userindex.exists()) {
            https://www.cnblogs.com/qi-dian/p/6132694.html
        }
        else {
            //  TODO error
        }
        return null;
    }

    @Override
    public boolean SaveUserFile(String username, Byte data, StoreDate_Intfc date) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean DeleteUserFile(String username, StoreDate_Intfc date) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Byte GetUserFile(String username, StoreDate_Intfc date) {
        // TODO Auto-generated method stub
        return null;
    }

    private synchronized Connection GetDatabaseConnection() {
        Connection conn = null;
        try {
            Class.forName(Label.CONST_DATABASEDRIVER.toString());
            // TODO 使用 connection.isValid(1) 减少链接次数
            conn = DriverManager.getConnection(
                    Label.CONST_DATABASEURL.toString(),
                    Label.CONST_DATABASEUSERNAME.toString(), 
                    Label.CONST_DATABASEPASSWORD.toString());
        } catch (ClassNotFoundException e) {
            // TODO 打印错误日志
        } catch (SQLException e) {
            // TODO 打印错误日志
        }
        return conn;
    }
    
    private ResultSet DatabaseOperator(String sql, DatabaseParameter parameter) {
        ResultSet re = null;
        PreparedStatement pstm = null;
        _databaselock.lock();
        try {
            pstm = (PreparedStatement)parameter.Filling(GetDatabaseConnection().prepareStatement(sql));
            re = pstm.executeQuery();
            pstm.close();
        } catch (SQLException e) {
            // TODO 日志
        }
        _databaselock.unlock();
        return re;
    }
    
}
