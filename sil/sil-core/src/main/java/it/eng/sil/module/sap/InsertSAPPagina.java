package it.eng.sil.module.sap;

import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;

public class InsertSAPPagina extends InsertSAP {
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertSAPPagina.class.getName());

	public void service(SourceBean request, SourceBean response) {
		for (Object attributo : request.getContainedSourceBeanAttributes()) {
			SourceBeanAttribute bean = (SourceBeanAttribute) attributo;
			_logger.info(bean.getClass() + " " + bean.getKey() + " = '" + bean.getValue() + "';");
		} // for
		List<String> attributi = attributi(request, "frmAnagra");
		_logger.info("frmAnagra: " + attributi.toString());
	}

	private List<String> attributi(SourceBean request, String form) {
		SourceBean frmBean = (SourceBean) request.getAttribute(form);
		List<String> attributi = new ArrayList<String>();
		try {
			for (Object attributo : frmBean.getContainedSourceBeanAttributes()) {
				SourceBeanAttribute bean = (SourceBeanAttribute) attributo;
				attributi.add(bean.getKey());
			}
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return attributi;
	}
}
