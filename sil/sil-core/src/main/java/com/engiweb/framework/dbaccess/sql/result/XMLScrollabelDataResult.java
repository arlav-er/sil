package com.engiweb.framework.dbaccess.sql.result;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.AbstractXMLObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.error.EMFInternalError;

public class XMLScrollabelDataResult extends AbstractXMLObject {
	private SourceBean _rowsBean = null;
	private boolean _asElement = false;
	private boolean _asCData = false;

	public XMLScrollabelDataResult(SourceBean rowsBean, boolean asElement, boolean asCData) throws EMFInternalError {
		super();
		_rowsBean = rowsBean;
		_asElement = asElement;
		_asCData = asCData;
		// navigazione sdr
	} // public XMLScrollabelDataResult(SourceBean rowsBean, boolean
		// asElement, boolean asCData) throws EMFInternalError

	public Element toElement(Document document) {
		Element rowsElement = document.createElement(ScrollableDataResult.ROWS_TAG);
		Vector rowBeanVector = _rowsBean.getAttributeAsVector(DataRow.ROW_TAG);
		for (int i = 0; i < rowBeanVector.size(); i++) {
			Element rowElement = document.createElement(DataRow.ROW_TAG);
			SourceBean rowBean = (SourceBean) rowBeanVector.get(i);
			Vector columnBeanVector = rowBean.getContainedAttributes();
			for (int j = 0; j < columnBeanVector.size(); j++) {
				SourceBeanAttribute columnBean = (SourceBeanAttribute) columnBeanVector.get(j);
				if (!_asElement)
					rowElement.setAttribute(columnBean.getKey(), columnBean.getValue().toString());
				else {
					Element columnElement = document.createElement(columnBean.getKey().toUpperCase());
					if (!_asCData)
						columnElement.appendChild(document.createTextNode(columnBean.getValue().toString()));
					else
						columnElement.appendChild(document.createCDATASection(columnBean.getValue().toString()));
					rowElement.appendChild(columnElement);
				} // if (!_asElement) else
			} // for (int j = 0; j < columnsBeanVector.size(); j++)
			rowsElement.appendChild(rowElement);
		} // for (int i = 0; i < _rows.size(); i++)
		return rowsElement;
	} // public Element toElement(Document document)
} // public class XMLScrollabelDataResult extends AbstractXMLObject
