package it.eng.myportal.converter.nodto;
  

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;

@FacesConverter("dePatentinoSilConverter")
public class DePatentinoSilConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			DePatentinoSilHome dePatentinoEJB = null;
			DePatentinoSil dePatentino = null;
			try {
				InitialContext ic = new InitialContext();
				dePatentinoEJB = (DePatentinoSilHome) ic.lookup("java:module/DePatentinoSilHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			 
			try {
				dePatentino = dePatentinoEJB.findById(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dePatentino;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DePatentinoSil) value).getCodPatentinoSil());
		}
	}
}
