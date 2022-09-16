package it.eng.sil.myaccount.controller.mbeans.sare;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.LazyAbilitaUtentiSareModel;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "abilitaUtentiSareBean")
@ViewScoped
public class AbilitaUtentiSareBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 2118752514551685461L;

	/**
	 * Search Form Params
	 */
	private DeAutorizzazioneSare deAutorizzazioneSare;
	private String user;
	private boolean utenteRetDati;
	private String codProvinica;

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

	// Search results
	private LazyDataModel<AziendaInfo> lazyDataModel;

	@Override
	protected void initPostConstruct() {
		codProvinica = accountInfoBean.getUserInfo().getProvinciaPOJO().getCodProvincia();
	}

	public void doSearch() {
		lazyDataModel = new LazyAbilitaUtentiSareModel(aziendaInfoEJB, user, deAutorizzazioneSare, codProvinica,
				utenteRetDati);
	}

	@SuppressWarnings("unchecked")
	public void sync() {
		try {
			List<AziendaInfo> daAggiornare = new ArrayList<AziendaInfo>();
			List<AziendaInfo> submitedList = (List<AziendaInfo>) lazyDataModel.getWrappedData();

			for (AziendaInfo utenteS : submitedList) {
				boolean hasModifyAuthSare = utenteS.getModificaAutorizzazioneSARE() != null
						&& !utenteS.getModificaAutorizzazioneSARE().equals(utenteS.getCodAutorizzazione());
				boolean hasModifyCodTipoUtente = utenteS.getModificaCodTipoUtenteSare() != null
						&& !utenteS.getModificaCodTipoUtenteSare().equals(utenteS.getCodUtenteSare());
				if (hasModifyCodTipoUtente || hasModifyAuthSare) {
					daAggiornare.add(utenteS);
				}
			}

			// se ci sono effettivamente utenti da modificare
			if (!daAggiornare.isEmpty()) {
				aziendaInfoEJB
						.aggiornaUtentiSAREAbilita(daAggiornare, accountInfoBean.getUserInfo().getIdPfPrincipal());
				String msgok = utils.getUiProperty("data.updated");
				addJSSuccessMessage(msgok);
			} else {

				String msgok = utils.getUiProperty("data.no_update");
				addJSSuccessMessage(msgok);
			}
		} catch (Exception e) {
			log.error(e.toString());
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
		}
	}

	public String export() {
		String csv = "";
		try {
			csv = aziendaInfoEJB.exportUtentiSareAbilitaCsv(user, deAutorizzazioneSare, codProvinica, utenteRetDati, 0,
					0);
		} catch (Exception e) {
			throw new EJBException(e);
		}
		return csv;
	}

	public StreamedContent downloadCSV() {
		String csv = export();
		String csvFilename = "Risultato_Ricerca_Utenti_SARE_abilita.csv";
		byte[] buffer = csv.getBytes(Charset.forName("UTF-8"));
		InputStream stream = new ByteArrayInputStream(buffer);
		StreamedContent file = new DefaultStreamedContent(stream, "text/csv", csvFilename);
		return file;
	}

	public boolean isSareDisabled() {
		return constantsSingleton.isSareDisabled();
	}

	/*
	 * =================== DA QUI IN POI CI SONO SEMPLICI GETTER/SETTER ===============
	 */

	public DeAutorizzazioneSare getDeAutorizzazioneSare() {
		return deAutorizzazioneSare;
	}

	public void setDeAutorizzazioneSare(DeAutorizzazioneSare deAutorizzazioneSare) {
		this.deAutorizzazioneSare = deAutorizzazioneSare;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isUtenteRetDati() {
		return utenteRetDati;
	}

	public void setUtenteRetDati(boolean utenteRetDati) {
		this.utenteRetDati = utenteRetDati;
	}

	public LazyDataModel<AziendaInfo> getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(LazyDataModel<AziendaInfo> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}
}
