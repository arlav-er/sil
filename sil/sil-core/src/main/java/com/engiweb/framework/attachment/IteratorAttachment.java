package com.engiweb.framework.attachment;

import java.util.Iterator;

public class IteratorAttachment {
	private Iterator _iterator = null;

	public IteratorAttachment(Iterator iterator) {
		super();
		_iterator = iterator;
	} // public IteratorAttachment(Iterator iterator)

	public boolean hasNext() {
		return _iterator.hasNext();
	} // public boolean hasNext()

	public IFaceAttachmentItem next() {
		return (IFaceAttachmentItem) _iterator.next();
	} // public IFaceAttachmentItem next()

	public void remove() {
		_iterator.remove();
	} // public void remove()
} // public class IteratorAttachment
