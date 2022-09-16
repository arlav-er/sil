package it.eng.myportal.converter.nodto;


import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.exception.MyPortalException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deLinguaConverter")
public class DeLinguaConverter implements Converter {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().equals("")) {
            return null;
        } else {
            InitialContext ic;
            DeLinguaHome deLinguaHome = null;
            try {
                ic = new InitialContext();
                deLinguaHome = (DeLinguaHome) ic.lookup("java:module/DeLinguaHome");
            } catch (NamingException e1) {
                log.warn(e1.getMessage());
                return null;
            }
            DeLingua deLingua = null;
            try {
                deLingua = deLinguaHome.findById(value);
            } catch (MyPortalException e) {
                log.warn(e.getMessage());
            }
            return deLingua;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((DeLingua) value).getCodLingua());
        }
    }

}
