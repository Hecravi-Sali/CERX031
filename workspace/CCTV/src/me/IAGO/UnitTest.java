package me.IAGO;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import me.IAGO.Item.FileSystem;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.StoreDate;

public class UnitTest {

    @Test
    public void test() throws InterruptedException {
        Date start = new Date();
        Thread.sleep(1000);
        Byte data = new Byte(new String("123"));
        Date end = new Date();
        FileSystem_Intfc file = new FileSystem();
        file.SaveUserFile(
                "halaoshi", 
                data, 
                new StoreDate(start, end));
        assertTrue(true);
    }

}
