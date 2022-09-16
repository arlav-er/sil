package com.engiweb.framework.dbaccess.sql;

import java.sql.Date;

public class DateDecorator extends Date {
	private String _stringValue = null;

	public DateDecorator(Date objectValue, String stringValue) {
		super(objectValue.getTime());
		_stringValue = stringValue;
	} // public DateDecorator(Date objectValue, String stringValue)

	public String toString() {
		return _stringValue;
	} // public String toString()
} // public class DateDecorator extends Date
