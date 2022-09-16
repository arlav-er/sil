package com.engiweb.framework.dbaccess.sql;

import java.sql.Timestamp;

public class TimestampDecorator extends Timestamp {
	private String _stringValue = null;

	public TimestampDecorator(Timestamp objectValue, String stringValue) {
		super(objectValue.getTime());
		_stringValue = stringValue;
	} // public TimestampDecorator(Timestamp objectValue, String stringValue)

	public String toString() {
		return _stringValue;
	} // public String toString()
} // public class TimestampDecorator extends Timestamp
