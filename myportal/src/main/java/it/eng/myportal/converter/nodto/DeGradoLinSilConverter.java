package it.eng.myportal.converter.nodto;


import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.exception.MyPortalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deGradoLinSilConverter")
public class DeGradoLinSilConverter implements Converter {

    protected final Log log = LogFactory.getLog(this.getClass());


    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().equals("")) {
            return null;
        } else {
            InitialContext ic;
            DeGradoLinSilHome deGradoLinSilHome = null;
            try {
                ic = new InitialContext();
                deGradoLinSilHome = (DeGradoLinSilHome) ic.lookup("java:module/DeGradoLinSilHome");
            } catch (NamingException e1) {
                log.warn(e1.getMessage());
                return null;
            }
            DeGradoLinSil deGradoLinSil = null;
            try {
                deGradoLinSil = deGradoLinSilHome.findById(value);
            } catch (MyPortalException e) {
                log.warn(e.getMessage());
            }
            return deGradoLinSil;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((DeGradoLinSil) value).getCodGradoLinSil());
        }
    }


}
