package it.eng.myportal.converter.nodto;

import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.exception.MyPortalException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@FacesConverter("deGradoLinConverter")
public class DeGradoLinConverter implements Converter{

    protected final Log log = LogFactory.getLog(this.getClass());


    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().equals("")) {
            return null;
        } else {
            InitialContext ic;
            DeGradoLinHome deGradoLinHome = null;
            try {
                ic = new InitialContext();
                deGradoLinHome = (DeGradoLinHome) ic.lookup("java:module/DeGradoLinHome");
            } catch (NamingException e1) {
                log.warn(e1.getMessage());
                return null;
            }
            DeGradoLin deGradoLin = null;
            try {
                deGradoLin = deGradoLinHome.findById(value);
            } catch (MyPortalException e) {
                log.warn(e.getMessage());
            }
            return deGradoLin;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((DeGradoLin) value).getCodGrado());
        }
    }
}
