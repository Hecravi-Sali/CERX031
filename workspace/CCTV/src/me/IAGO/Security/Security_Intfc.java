package me.IAGO.Security;

public interface Security_Intfc {
	public boolean GetVerificationStatus();
	public String OnetimeVerificationInfo(String username, int verificationtimeout);
	public boolean Verification(String verificationinfo);
	public Byte DecryptData(Byte encrypteddata);
}
