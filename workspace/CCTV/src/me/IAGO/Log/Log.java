package me.IAGO.Log;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import me.IAGO.Item.Label;

public class Log implements Log_Intfc {
    private static Logger _logger = Logger.getLogger(Log.class.toString());
    private static FileHandler loggerfile;

    public Log() {
        File file = new File(Label.CONST_LOGDIR.toString(), Label.CONST_LOGNAME.toString());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
            System.out.println(file.getAbsolutePath());
            loggerfile = new FileHandler(file.getAbsolutePath());
            _logger.addHandler(loggerfile); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        _logger.info("The log file was created successfully");
    }
    
    @Override
    public void Info(String message) {
        _logger.info(message);
    }

    @Override
    public void Debug(String message) {
        _logger.severe(message);
    }

    @Override
    public void Warring(String message) {
        _logger.severe(message);
    }

    @Override
    public void Error(String message) {
        _logger.severe(message);
    }

}
