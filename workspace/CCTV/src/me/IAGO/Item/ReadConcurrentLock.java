package me.IAGO.Item;

public interface ReadConcurrentLock {
    public boolean TryRead();
    public void Read();
    public boolean TryWrite();
    public void Write();
}
