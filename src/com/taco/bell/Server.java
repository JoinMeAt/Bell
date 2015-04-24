package com.taco.bell;

public class Server extends User {
	long serverID;
	
	public Server(long serverID) {
		this.serverID = serverID;
		type = Types.SERVER;
	}

	public void setServerID(long serverID ) { this.serverID = serverID; }
	public long getServerID() { return this.serverID; }

}
