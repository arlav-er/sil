package com.engiweb.framework.navigation;

public class NavigationException extends Exception {
	public NavigationException(String message) {
		if (message == null)
			_message = "NOT DEFINED";
		else
			_message = message;
	} // public NavigationException(String message)

	public String getMessage() {
		return _message;
	} // public String getMessage()

	private String _message = null;
} // public class NavigationException extends Exception
