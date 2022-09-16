package com.engiweb.framework.base;

class ApplicationContainerItem {
	public ApplicationContainerItem(Object value, int timeout) {
		_timestamp = System.currentTimeMillis();
		_value = value;
		if (timeout <= 0)
			_timeout = INFINITE_TIMEOUT;
		else
			_timeout = timeout * 1000;
	} // public ApplicationContainerItem()

	public synchronized Object getValue() {
		_timestamp = System.currentTimeMillis();
		return _value;
	} // public synchronized Object getValue()

	public synchronized void setValue(Object value) {
		_timestamp = System.currentTimeMillis();
		_value = value;
	} // public synchronized void setValue(Object value)

	public synchronized long getLastUse() {
		return _timestamp;
	} // public synchronized long getLastUse()

	public long getTimeout() {
		return _timeout;
	} // public long getTimeout()

	private long _timestamp = 0;
	private Object _value = null;
	private long _timeout = 0;
	public static int INFINITE_TIMEOUT = 0;
} // class ApplicationContainerItem
