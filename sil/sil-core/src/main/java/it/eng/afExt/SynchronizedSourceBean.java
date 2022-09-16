package it.eng.afExt;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

public class SynchronizedSourceBean extends SourceBean {

	public SynchronizedSourceBean(SourceBean sourceBean) throws SourceBeanException {
		super(sourceBean);
		// TODO Auto-generated constructor stub
	}

	public synchronized static SourceBean fromXMLStringSynch(String xml) throws SourceBeanException {
		return fromXMLString(xml);
	}

}
