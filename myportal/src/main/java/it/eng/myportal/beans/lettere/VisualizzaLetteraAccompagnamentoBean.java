package it.eng.myportal.beans.lettere;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.CvVisualizzaLetteraAccDTO;
import it.eng.myportal.entity.home.CvLetteraAccHome;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Classe per gestire la pagina di visualizzazione di una lettera di
 * accompagnamento
 *
 * @author Rodi A.
 *
 *         TODO commentare adeguatamente e controlli di sicurezza
 *
 */
@ManagedBean
@ViewScoped
public class VisualizzaLetteraAccompagnamentoBean extends
		AbstractBaseBean {

	private static final String LETTERA_ID = "letteraId";

	@EJB
	CvLetteraAccHome cvLetteraAccHome;

	private Integer letteraId;
	private CvVisualizzaLetteraAccDTO data;
	private String letteraIdStr;

	public Integer getLetteraId() {
		return letteraId;
	}

	public void setLetteraId(Integer letteraId) {
		this.letteraId = letteraId;
	}

	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		try {
			data = retrieveData(); // recupera i dati
			if (data == null) { // se non trovo niente allora costruisco una nuova istanza in modifica.
				data = buildNewDataIstance();
			}
		} catch (EJBException e) { // in caso di errori durante il recupero dei dati ritorna all'HomePage
			gestisciErrore(e, "data.error_loading");
			redirectHome();
		}
	}

	protected CvVisualizzaLetteraAccDTO buildNewDataIstance() {
		return new CvVisualizzaLetteraAccDTO();
	}

	/**
	 * @see it.eng.myportal.beans.AbstractEditableBean#dontedit()
	 *
	 *      La pressione di un pulsante implica sempre il post dei valori
	 *      attualmente presenti nella form;<br>
	 *      per implementare la funzionalità di annullamento delle modifiche<br>
	 *      vengono ricaricati i dati.
	 *
	 * @return CvVisualizzaLetteraAccDTO
	 */
	protected CvVisualizzaLetteraAccDTO retrieveData() {
		Map<String, String> map = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String id = ObjectUtils.toString(map.get("id"),letteraIdStr);
		if (id != null) {
			try {
				letteraId = Integer.valueOf(id);
				putParamsIntoSession();
			} catch (Exception ex) {
				throw new RuntimeException(
						"Errore durante il recupero dei dati");
			}

			CvVisualizzaLetteraAccDTO cvVisualizzaLetteraAccDTO = cvLetteraAccHome
					.findVisualizzaLetteraAccompagnamentoDTOById(letteraId,getSession().getPrincipalId());

			return cvVisualizzaLetteraAccDTO;
		} else {
			return null;
		}
	}


	public CvVisualizzaLetteraAccDTO getData() {
		return data;
	}

	public void setData(CvVisualizzaLetteraAccDTO data) {
		this.data = data;
	}


	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(LETTERA_ID, letteraId);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		letteraIdStr=ObjectUtils.toString(restoreParameters.get(LETTERA_ID ));
	}
}
