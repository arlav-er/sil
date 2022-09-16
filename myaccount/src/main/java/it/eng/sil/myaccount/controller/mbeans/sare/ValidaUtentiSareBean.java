package it.eng.sil.myaccount.controller.mbeans.sare;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.LazyValidaUtentiSareModel;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

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

@ManagedBean(name = "validaUtentiSareBean")
@ViewScoped
public class ValidaUtentiSareBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 2118752514551685461L;

	// Search form params.
	private String codProvincia;
	private String user;
	private String stato;

	// Search results
	private LazyDataModel<AziendaInfo> lazyDataModel;

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

	@EJB
	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	/**
	 * Questo metodo viene chiamato alla creazione del bean.
	 */
	@Override
	protected void initPostConstruct() {
		codProvincia = accountInfoBean.getUserInfo().getProvinciaPOJO().getCodProvincia();
	}

	/**
	 * Questo metodo viene chiamato quando l'utente preme il tasto "cerca". Inizializza il lazyDataModel.
	 */
	public void doSearch() {
		lazyDataModel = new LazyValidaUtentiSareModel(aziendaInfoEJB, user, stato, codProvincia);
	}

	/**
	 * Questo metodo viene chiamato quando l'utente preme il tasto "modifica": rende permanenti le modifiche al campo
	 * flagValida effettuate fino a quel momento.
	 */
	@SuppressWarnings("unchecked")
	public void sync() {
		try {
			// Creo una lista con solo le AziendaInfo da aggiornare.
			List<AziendaInfo> submittedList = (List<AziendaInfo>) lazyDataModel.getWrappedData();
			List<AziendaInfo> daAggiornare = new ArrayList<AziendaInfo>();

			for (AziendaInfo azienda : submittedList) {
				if (azienda.getModificaFlagValida() != null
						&& !azienda.getModificaFlagValida().equals(azienda.getFlagValida())) {
					daAggiornare.add(azienda);
				}
			}

			// Se ce ne sono, aggiorno gli elementi da aggiornare. In ogni caso visualizzo un messaggio.
			if (!daAggiornare.isEmpty()) {
				aziendaInfoEJB.aggiornaUtentiSAREValida(daAggiornare, accountInfoBean.getUserInfo().getIdPfPrincipal());
				String msgok = utils.getUiProperty("data.updated");
				aggiornaTableValidati();
				addJSSuccessMessage(msgok);
			} else {
				String msgok = utils.getUiProperty("data.no_update");
				addJSSuccessMessage(msgok);
			}
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}
	}

	/**
	 * Questo metodo aggiorna il valore "modificaFlagValida" degli elementi visualizzati nella tabella. Serve per
	 * "aggiornare" la tabella visualizzata dopo una modifica e permettere modifiche consecutive.
	 */
	@SuppressWarnings("unchecked")
	private void aggiornaTableValidati() {
		List<AziendaInfo> tableList = (List<AziendaInfo>) lazyDataModel.getWrappedData();
		for (AziendaInfo azienda : tableList) {
			azienda.setModificaFlagValida(azienda.getFlagValida());
		}
	}

	/**
	 * Questo metodo viene chiamato quando l'utente preme il tasto "esporta": crea e restituisce un file .csv contenente
	 * il risultato della ricerca.
	 */
	public StreamedContent downloadCSV() {
		// Creo una stringa contenente tutti i dati da esportare.
		String csv = "";
		try {
			csv = aziendaInfoEJB.exportUtentiSareValidaCsv(user, stato, codProvincia, 0, 0);
		} catch (Exception e) {
			throw new EJBException(e);
		}

		// Trasformo la stringa in un file e la restituisco.
		String csvFilename = "Risultato_Ricerca_Utenti_Valida.csv";
		byte[] buffer = csv.getBytes(Charset.forName("UTF-8"));
		InputStream stream = new ByteArrayInputStream(buffer);
		StreamedContent file = new DefaultStreamedContent(stream, "text/csv", csvFilename);
		return file;
	}

	/**
	 * Questo metodo recupera la colonna "Abilitata" per un'azienda.
	 */
	public String getFlgAbilitatoAzienda(Integer idPfPrincipal) {
		try {
			PfPrincipal utente = pfPrincipalMyAccountEJB.findById(idPfPrincipal);
			if (utente.getFlagAbilitato()) {
				return "Si";
			}

			return "No";
		} catch (Exception e) {
			String msgko = utils.getUiProperty("data.no");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}
	}

	/*
	 * ===================== DA QUI IN POI, CI SONO SEMPLICI GETTER/SETTER =====================
	 */

	public LazyDataModel<AziendaInfo> getLazyDataModel() {
		return lazyDataModel;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
}