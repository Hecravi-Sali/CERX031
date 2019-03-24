package me.IAGO.Security;

public interface Security_Intfc {
    public enum PrivilegeLevel {
        Owner, 
        Group, 
        Visitor,
        None
    };
    public boolean VerificationStatus();
	public String OnetimeVerificationInfo(String username, int verificationtimeout);
	public boolean Verification(String verificationinfo);
	public String DecryptData(String encrypteddata);
	public PrivilegeLevel Privilege(String coreownername);
}
