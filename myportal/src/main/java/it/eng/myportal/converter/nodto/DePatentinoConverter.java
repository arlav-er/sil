package it.eng.myportal.converter.nodto;
  

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;

@FacesConverter("dePatentinoConverter")
public class DePatentinoConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			DePatentinoHome dePatentinoEJB = null;
			DePatentino dePatentino = null;
			try {
				InitialContext ic = new InitialContext();
				dePatentinoEJB = (DePatentinoHome) ic.lookup("java:module/DePatentinoHome");
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
			return String.valueOf(((DePatentino) value).getCodPatentino());
		}
	}
}
