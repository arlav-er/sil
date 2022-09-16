package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.ClInvioComunicazioneDTO;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroMessaggioEjb;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@ManagedBean  
@SessionScoped
public class ClicLavoroBean {
	
	protected static Log log = LogFactory.getLog(ClicLavoroBean.class);

	
	private List<ClInvioComunicazioneDTO> comunicazioni;
	
		
	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	ClicLavoroMessaggioEjb clicLavoroMessaggioEjb;
	
	@EJB
	ClicLavoroEjb clicLavoroEjb;
	
	public List<ClInvioComunicazioneDTO> getComunicazioni() {
		return comunicazioni;
	}

	public void setComunicazioni(List<ClInvioComunicazioneDTO> comunicazioni) {
		this.comunicazioni = comunicazioni;
	}
	
	
}
