package it.eng.myportal.beans.sare;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAbilitatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoDelegatoHome;
import it.eng.myportal.enums.SoftwareSAREUtilizzato;
import it.eng.myportal.utils.ConstantsSingleton.DeTipoAbilitato;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean della pagina di dettaglio dei dati SARE dell'azienda,
 * visualizzata dagli utenti provincia.<br/>
 * <br/>
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere una provincia.</li>
 * </ul>
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class DettaglioDatiSAREBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(DettaglioDatiSAREBean.class);

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private DeTipoAbilitatoHome deTipoAbilitatoHome;

	@EJB
	private DeTipoDelegatoHome deTipoDelegatoHome;

	private AziendaInfoDTO data;

	/**
	 * Tipi possibili di richiedente nella registrazione dell'azienda
	 */
	private List<SelectItem> tipiRichiedente;

	/**
	 * Determina se e' possibile scegliere il tipo di delegato in fase di
	 * registrazione
	 */
	private boolean editTipoDelegato;

	/**
	 * Determina se e' possibile scegliere il soggetto abilitato in fase di
	 * registrazione
	 */
	private boolean editSoggettoAbilitato;

	/**
	 * Determina se e' possibile scegliere l'agenzia di somministrazione in fase
	 * di registrazione
	 */
	private boolean editAgenziaSomministrazione;

	/**
	 * Tipi possibili di delegato nella registrazione dell'azienda
	 */
	private List<SelectItem> tipiDelegato;

	/**
	 * Determina se mostrare la sezione 'Dati agenzia di somministrazione' in
	 * fase di registrazione
	 */
	private Boolean showAgenziaSomministrazioneSection;

	/**
	 * Determina se mostrare la sezione 'Dati soggetto abilitato' in fase di
	 * registrazione
	 */
	private Boolean showDatiSoggettoAbilitatoSection;

	/**
	 * Tipi di software utilizzati per le comunicazioni oblbigatorie
	 */
	private List<SelectItem> softwareSAREUtilizzati;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isProvincia()) {
				Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap();
				Integer idPfPrincipal = Integer.parseInt(StringUtils.defaultString(map.get("idAziendaInfo")));

				data = aziendaInfoHome.findDTOById(idPfPrincipal);
				tipiRichiedente = deTipoAbilitatoHome.getListItems(false);
				tipiDelegato = deTipoDelegatoHome.getListItems(true);
				softwareSAREUtilizzati = SoftwareSAREUtilizzato.asSelectItems();

				/*
				 * visualizza/nasconde le sezioni relative al soggetto delegato
				 * e all'agenzia di somministrazione
				 */
				changeTipoRichiedente(data.getTipoAbilitato().getId());

				// Clean-up delle informazioni in sessione
				session.getParams().clear();

				log.debug("Costruito il Bean per il dettaglio dei dati SARE dell'azienda.");
			} else {
				addErrorMessage("provincia.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			// in caso di errori durante il recupero dei dati ritorna
			// all'HomePage
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public void changeTipoRichiedente(String val) {
		if (val == null) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.DATORE_PRIVATO.equals(val)) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.DATORE_PUBBLICO.equals(val)) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(val)) {
			showAgenziaSomministrazioneSection = true;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.SOGGETTO_ABILITATO.equals(val)) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = true;
		} else if (DeTipoAbilitato.AGENZIA_LAVORO.equals(val)) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.SOGGETTO_TIROCINI.equals(val)) {
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else {
			log.error("Errore nella gestione del tipo delegato. Tipo '" + val + "' sconosciuto.");
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		}
	}

	/**
	 * @return the data
	 */
	public AziendaInfoDTO getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(AziendaInfoDTO data) {
		this.data = data;
	}

	public List<SelectItem> getTipiRichiedente() {
		return tipiRichiedente;
	}

	public void setTipiRichiedente(List<SelectItem> tipiRichiedente) {
		this.tipiRichiedente = tipiRichiedente;
	}

	public Boolean getEditTipoDelegato() {
		return editTipoDelegato;
	}

	public void setEditTipoDelegato(Boolean editTipoDelegato) {
		this.editTipoDelegato = editTipoDelegato;
	}

	public Boolean getEditSoggettoAbilitato() {
		return editSoggettoAbilitato;
	}

	public void setEditSoggettoAbilitato(Boolean editSoggettoAbilitato) {
		this.editSoggettoAbilitato = editSoggettoAbilitato;
	}

	public Boolean getEditAgenziaSomministrazione() {
		return editAgenziaSomministrazione;
	}

	public void setEditAgenziaSomministrazione(Boolean editAgenziaSomministrazione) {
		this.editAgenziaSomministrazione = editAgenziaSomministrazione;
	}

	public List<SelectItem> getTipiDelegato() {
		return tipiDelegato;
	}

	public void setTipiDelegato(List<SelectItem> tipiDelegato) {
		this.tipiDelegato = tipiDelegato;
	}

	public List<SelectItem> getSoftwareSAREUtilizzati() {
		return softwareSAREUtilizzati;
	}

	public void setSoftwareSAREUtilizzati(List<SelectItem> softwareSAREUtilizzato) {
		this.softwareSAREUtilizzati = softwareSAREUtilizzato;
	}

	public Boolean getShowAgenziaSomministrazioneSection() {
		return showAgenziaSomministrazioneSection;
	}

	public void setShowAgenziaSomministrazioneSection(Boolean showAgenziaSomministrazioneSection) {
		this.showAgenziaSomministrazioneSection = showAgenziaSomministrazioneSection;
	}

	public Boolean getShowDatiSoggettoAbilitatoSection() {
		return showDatiSoggettoAbilitatoSection;
	}

	public void setShowDatiSoggettoAbilitatoSection(Boolean showDatiSoggettoAbilitatoSection) {
		this.showDatiSoggettoAbilitatoSection = showDatiSoggettoAbilitatoSection;
	}
}
