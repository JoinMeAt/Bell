package com.taco.bell;

public class BellException extends Throwable {
	private static final long serialVersionUID = -4873181721894036295L;
	private int errorID = -1;
	private String message = null;
	
	public BellException(int errorID) {
		message = getMessage(errorID);
		this.errorID = errorID;
	}
	
	public BellException(Exception e) {
		message = e.getMessage();
		this.errorID = 0;
	}
	
	public String getMessage() {
		return this.message;
	}

	private String getMessage(int errorID) {
		switch(errorID) {		
		case 0:
			message = "General Error";
			break;
		case 1:
			message = "Invalid Email or Password";
			break;
		case 2:
			message = "UserID doesn't exist";
			break;
		case 3:
			message = "Server doesn't exist";
			break;
		case 4:
			message = "Service request doesn't exist";
			break;
		case 5:
			message = "Manager doesn't exist";
			break;
		default:
			message = errorID + ": Unknown Error";
		}
		
		return message;
	}
}
