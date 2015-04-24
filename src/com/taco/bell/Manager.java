package com.taco.bell;

public class Manager extends User {
	long managerID;
		
	public Manager(long managerID) {
		this.managerID = managerID;
		type = Types.MANAGER;
	}

	public void setManagerID( long serverID ) { this.managerID = serverID; }
	public long getManagerID() { return this.managerID; }

}
