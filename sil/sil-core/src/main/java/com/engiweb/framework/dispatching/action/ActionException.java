package com.engiweb.framework.dispatching.action;

public class ActionException extends Exception {
	public ActionException(String message) {
		if (message == null)
			_message = "NOT DEFINED";
		else
			_message = message;
	} // public ActionException(String message)

	public String getMessage() {
		return _message;
	} // public String getMessage()

	private String _message = null;
} // public class ActionException extends Exception
