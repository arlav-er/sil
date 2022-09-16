package com.engiweb.framework.attachment;

import java.io.InputStream;

public interface IFaceAttachmentItem {
	String getFieldName();

	String getName();

	String getContentType();

	InputStream getInputStream();
} // public interface IFaceAttachmentItem
