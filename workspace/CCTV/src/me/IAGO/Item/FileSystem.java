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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

public class FileSystem implements FileSystem_Intfc {   
    private static Lock _databaselock = new ReentrantLock();
    private static Lock _filelock = new ReentrantLock();
    
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
        List<StoreDate_Intfc> re = new ArrayList<>();
        _filelock.lock();
        if(CheckUserDir(Label.CONST_FILEMAINDIR + "/" + username)) {
            CatalogTraversal(
                    Label.CONST_FILEMAINDIR + "/" + username,
                    (File file) -> {
                        try {
                            re.add(new StoreDate(new JSONObject(file.getName())));
                        } catch (JSONException e) {
                            // TODO 
                        } catch (ParseException e) {
                            // TODO
                        }
                    });
        }
        _filelock.unlock();
        return re;
    }

    @Override
    public boolean SaveUserFile(String username, Byte data, StoreDate_Intfc date) {
        boolean re = false;
        _filelock.lock();
        FileWriter fw;
        if(CheckUserDir(Label.CONST_FILEMAINDIR + "/" + username)) {
            try {
                date.toString();
                fw = new FileWriter(
                        Label.CONST_FILEMAINDIR + 
                        "/" + username +
                        "/" + date.toString());
                fw.write(data);
                fw.close();
                re = true;
            } catch (IOException e) {
                // TODO 
            }
        }
        _filelock.unlock();
        return re;
    }

    @Override
    public boolean DeleteUserFile(String username, StoreDate_Intfc date) {
        _filelock.lock();
        CatalogTraversal(
                (Label.CONST_FILEMAINDIR + "/" + username),
                (File file) -> {
                    file.delete();
                });
        _filelock.unlock();
        return true;
    }

    @Override
    public Byte GetUserFile(String username, StoreDate_Intfc date) {
        String re = new String();
        _filelock.lock();
        try {
            re = ReadFileByBytes(
                    Label.CONST_FILEMAINDIR + 
                    "/" + username + 
                    "/" + date.toString());
        } catch (FileNotFoundException e) {
            // TODO
            e.printStackTrace();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        _filelock.unlock();
        return new Byte(re);
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
    
    private interface DatabaseParameter {
        public PreparedStatement Filling(PreparedStatement pstm) throws SQLException;
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
    
    private boolean CheckUserDir(String userdir) {
        boolean re = true;
        File file = new File(userdir);
        if(!file.exists()){
            re = file.mkdirs();
        }
        return re;
    }
    
    private interface TraversalOperation {
        void Do(File file);
    }
    private void CatalogTraversal(String fileordir, TraversalOperation c) {
        File file = new File(fileordir);
        if(file.exists()){
            if(file.isFile()){
                c.Do(file);
            }else if(file.isDirectory()){
                File[] files = file.listFiles();
                for (File myfile : files) {
                    CatalogTraversal(fileordir + "/" + myfile.getName(), c);
                }           
                c.Do(file);
            }
        }
    }

    private String ReadFileByBytes(String filePath)
            throws FileNotFoundException, IOException {
        _filelock.lock();
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
        _filelock.unlock();
        return re.toString();
    }
}
