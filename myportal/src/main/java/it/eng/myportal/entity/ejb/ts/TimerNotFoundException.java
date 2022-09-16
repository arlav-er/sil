package it.eng.myportal.entity.ejb.ts;

import javax.ejb.ApplicationException;

@ApplicationException
public class TimerNotFoundException extends Exception {

	public TimerNotFoundException(String string) {
		super(string);
	}

	public TimerNotFoundException() {
		super();
	}

}
