package com.taco.bell;

public class User {
	int type;
	long userID;
	String firstName;
	String lastName;
	String email;
	
	public User() {
		type = Types.USER;
		userID = -1;
	}
	
	public long getUserID() {
		return userID;
	}
	
	public void setUserID(long userID) {
		this.userID = userID;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public class Types {
		public static final int USER = 1;
		public static final int SERVER = 2;
		public static final int MANAGER = 3;
		public static final int HOST = 4;
	}
}
