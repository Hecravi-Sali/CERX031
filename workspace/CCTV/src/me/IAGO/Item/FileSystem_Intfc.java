package me.IAGO.Item;

import java.util.List;

public interface FileSystem_Intfc {
    /*
     *   通知文件系统可用，初始化正确
     * */
    public boolean Available();
    
    /*
     *   用户的增删改查，在数据库上进行
     * */
    public boolean AddUserInfor(String username, String password);
    public boolean RemoveUserInfo(String username);
    public boolean ChangeUserPassword(String username, String newpassword);
    public String GetUserPassword(String username);
    
    /*
     * MainPath -   |   -username - | - yyyy-mm-dd - | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               .....
     *                              | - yyyy-mm-dd - | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               .....
     *                              | - yyyy-mm-dd
     * 
     * 
     *              |   -username - | - yyyy-mm-dd - | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               .....
     *                              | - yyyy-mm-dd - | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               | - hh-mm-ss-hh-mm-ss.media
     *                                               .....
     *                              | - yyyy-mm-dd 
     * */
    /*
     *  切换工作目录到 username那一级
     * */
    public boolean GotoUserPath(String username);
    /*
      *  返回该目录下（user）所有media文件名，media的文件名由 开始时间和结束时间构成 hh-mm-ss 只包含小时等
     * StoreDate_Intfc 包含两个 date 表示起止时间，包含年月日等信息
     * */
    public List<StoreDate_Intfc> GetUserFileIndex();
    
    /*
     *  保存为byte流文件，StoreDate_Intfc 提供了保存路径以及文件名
     * */
    public boolean SaveUserFile(Byte data, StoreDate_Intfc date);
    public boolean DeleteUserFile(StoreDate_Intfc date);
    /*
     *   取media文件，StoreDate_Intfc 同上
     * */
    public Byte GetUserFile(StoreDate_Intfc date);
}
