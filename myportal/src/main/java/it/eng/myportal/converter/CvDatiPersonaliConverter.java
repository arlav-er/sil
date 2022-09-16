package it.eng.myportal.converter;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("cvDatiPersonaliConverter")
public class CvDatiPersonaliConverter implements Converter {

	protected static Log log = LogFactory.getLog(CvDatiPersonaliConverter.class);

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			CvDatiPersonaliHome cvDatiPersonaliHome = null;
			try {
				ic = new InitialContext();
				cvDatiPersonaliHome = (CvDatiPersonaliHome) ic.lookup("java:app/MyPortal/CvDatiPersonaliHome");
			} catch (NamingException e1) {
				log.error(e1.getMessage());
				return null;
			}

			return cvDatiPersonaliHome.findById(Integer.parseInt(value));

		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((CvDatiPersonali) value).getIdCvDatiPersonali());
		}
	}

}
