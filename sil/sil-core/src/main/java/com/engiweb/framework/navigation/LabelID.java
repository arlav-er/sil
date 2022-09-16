package com.engiweb.framework.navigation;

import java.io.Serializable;

public class LabelID implements Serializable {
	private String _label = null;
	private String _id = null;

	public LabelID() {
		super();
		_label = null;
		_id = null;
	} // public LabelID()

	public String getLabel() {
		return _label;
	} // public String getLabel()

	public void setLabel(String string) {
		_label = string;
	} // public void setLabel(String string)

	public String getId() {
		return _id;
	} // public String getId()

	public void setId(String string) {
		_id = string;
	} // public void setId(String string)
} // public class LabelID implements Serializable
