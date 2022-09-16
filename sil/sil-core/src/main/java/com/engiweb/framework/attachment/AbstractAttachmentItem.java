package com.engiweb.framework.attachment;

import java.io.InputStream;

public abstract class AbstractAttachmentItem implements IFaceAttachmentItem {
	private String _fieldName = null;
	private String _name = null;
	private String _contentType = null;
	private InputStream _inputStream = null;

	public AbstractAttachmentItem() {
		super();
		_fieldName = null;
		_name = null;
		_contentType = null;
		_inputStream = null;
	} // public AbstractAttachmentItem()

	public String getFieldName() {
		return _fieldName;
	} // public String getFieldName()

	public void setFieldName(String fieldName) {
		_fieldName = fieldName;
	} // public void setFieldName(String fieldName)

	public String getName() {
		return _name;
	} // public String getName()

	public void setName(String name) {
		_name = name;
	} // public void setName(String name)

	public String getContentType() {
		return _contentType;
	} // public String getContentType()

	public void setContentType(String contentType) {
		_contentType = contentType;
	} // public void setContentType(String contentType)

	public InputStream getInputStream() {
		return _inputStream;
	} // public InputStream getInputStream()

	public void setInputStream(InputStream inputStream) {
		_inputStream = inputStream;
	} // public void setInputStream(InputStream inputStream)
} // public abstract class AbstractAttachmentItem implements
	// IFaceAttachmentItem
