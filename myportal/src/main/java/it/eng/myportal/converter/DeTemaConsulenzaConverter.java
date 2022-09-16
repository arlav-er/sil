package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.DeTemaConsulenza;
import it.eng.myportal.entity.home.decodifiche.DeTemaConsulenzaHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deTemaConsulenzaConverter")
public class DeTemaConsulenzaConverter implements Converter{
	protected static Log log = LogFactory.getLog(CvDatiPersonaliConverter.class);

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTemaConsulenzaHome deTemaConsulenzaHome = null;
			try {
				ic = new InitialContext();
				deTemaConsulenzaHome = (DeTemaConsulenzaHome) ic.lookup("java:app/MyPortal/DeTemaConsulenzaHome");
			} catch (NamingException e1) {
				log.error(e1.getMessage());
				return null;
			}

			return deTemaConsulenzaHome.findById(value);

		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return ((DeTemaConsulenza)value).getCodTema();
		}
	}
}
