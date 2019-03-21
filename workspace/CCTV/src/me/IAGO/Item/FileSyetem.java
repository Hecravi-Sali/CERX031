package me.IAGO.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private Connection GetDatabaseConnection() {
        Connection conn = null;
        try {
            Class.forName(Label.CONST_DATABASEDRIVER.toString());
            // TODO 使用 connection.isValid(1) 减少链接次数
            conn = DriverManager.getConnection(
                    Label.CONST_DATABASEURL.toString(),
                    Label.CONST_DATABASEUSERNAME.toString(), 
                    Label.CONST_DATABASEPASSWORD.toString());
        } catch (ClassNotFoundException e) {
            // TODO 日志
        } catch (SQLException e) {
            // TODO 日志
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
    
    private boolean CreatFile(String filePath) 
            throws IOException {
        boolean re = false;
        File file = new File(filePath);
        if(!file.exists()){
            re = file.createNewFile();
        }
        return re;
    }
    private boolean CreateDirectory(String directory)
            throws IOException {
        boolean re = false;
        File file = new File(directory);
        if(!file.exists()){
            re = file.mkdirs();
        }
        return re;
    }
    private void DeleteFileORDirectory(String filePathORdirectory) {
        File file = new File(filePathORdirectory);
        if(!file.exists()){
            return;
        }
        if(file.isFile()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File myfile : files) {
                DeleteFileORDirectory(filePathORdirectory + "/" + myfile.getName());
            }
            
            file.delete();
        }
    }
    private String ReadFileByBytes(String filePath)
            throws FileNotFoundException, IOException {
        StringBuffer re = null;
        File file = new File(filePath);
        if(file.exists() && file.isFile()){
            re = new StringBuffer();
            byte[] temp = new byte[1024];
            FileInputStream fileInputStream = new FileInputStream(file);
            while(fileInputStream.read(temp) != -1){
                re.append(new String(temp));
                temp = new byte[1024];
            }
            
            fileInputStream.close();
        }
        return re.toString();
    }

    private void WriteFileByFileWriter(String filePath, String data) 
            throws IOException {
        //  TODO 并发阻塞 读并发，写等待阻塞
        FileWriter fw = new FileWriter(filePath);
        fw.write(data);
        fw.close();
    }
}
