package com.engiweb.framework.attachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AttachmentHandler {
	private static final String FIELD_NAME_NOT_DEFINED = "FIELD_NAME_NOT_DEFINED";
	private Map _attachments = null;

	public AttachmentHandler() {
		super();
		_attachments = new HashMap();
	} // public AttachmentHandler()

	public IFaceAttachmentItem getAttachment(String fieldName) {
		IFaceAttachmentItem[] attachmentsValues = getAttachmentValues(fieldName);
		if (attachmentsValues.length == 0)
			return null;
		return attachmentsValues[0];
	} // public IFaceAttachmentItem getAttachment(String fieldName)

	public IFaceAttachmentItem[] getAttachmentValues(String fieldName) {
		ArrayList attachmentsValues = (ArrayList) _attachments.get(fieldName);
		if (attachmentsValues == null)
			attachmentsValues = new ArrayList();
		return (IFaceAttachmentItem[]) attachmentsValues.toArray();
	} // public IFaceAttachmentItem[] getAttachmentValues(String fieldName)

	public void addAttachment(IFaceAttachmentItem attachment) {
		String fieldName = attachment.getFieldName();
		if (fieldName == null)
			fieldName = FIELD_NAME_NOT_DEFINED;
		ArrayList attachmentsValues = (ArrayList) _attachments.get(fieldName);
		if (attachmentsValues == null) {
			attachmentsValues = new ArrayList();
			_attachments.put(fieldName, attachmentsValues);
		} // if (attachmentsValues == null)
		attachmentsValues.add(attachment);
	} // public void addAttachment(IFaceAttachmentItem attachment)

	public void addAllAttachments(IFaceAttachmentItem[] attachments) {
		for (int i = 0; i < attachments.length; i++)
			addAttachment(attachments[i]);
	} // public void addAttachment(IFaceAttachmentItem attachment)

	public IteratorAttachment iterator() {
		ArrayList values = new ArrayList();
		Set keySet = _attachments.keySet();
		Iterator keyIterator = keySet.iterator();
		while (keyIterator.hasNext()) {
			String fieldName = (String) keyIterator.next();
			ArrayList attachmentsValues = (ArrayList) _attachments.get(fieldName);
			values.addAll(attachmentsValues);
		} // while (keyIterator.hasNext())
		return new IteratorAttachment(values.iterator());
	} // public IteratorAttachment iterator()
} // public class AttachmentHandler
