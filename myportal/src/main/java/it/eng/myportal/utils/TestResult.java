package it.eng.myportal.utils;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
	boolean failed = false;
	List<String>  errors;
	
	
	
	public TestResult() {
		super();
		failed = false;
		errors = new ArrayList<String>();
	}
	public TestResult(boolean failed) {
		this.failed = failed;
	}
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	
	
}
