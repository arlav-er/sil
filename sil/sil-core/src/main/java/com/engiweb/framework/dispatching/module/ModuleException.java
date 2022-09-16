package com.engiweb.framework.dispatching.module;

/**
 * Implementa un eccezione generica sollevata in modalità PageTemplate
 */
public class ModuleException extends Exception {
	public ModuleException(String message) {
		if (message == null)
			_message = "NOT DEFINED";
		else
			_message = message;
	} // public ModuleException(String message)

	public String getMessage() {
		return _message;
	} // public String getMessage()

	private String _message = null;
} // public class ModuleException extends Exception
