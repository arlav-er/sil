package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.helpers.LazyMittentiSareModel;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.sare.SareImportEJB;
import it.eng.sil.myaccount.model.entity.migrazione.SareImport;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

/**
 * Questa bean è collegato alla pagina "Gestione nuovo SARE" del pannello di amministrazione. Per ora contiene due
 * funzionalità : porting utenti SARE e gestione superuser SARE.
 * 
 * @author gicozza
 */

@ManagedBean
@ViewScoped
public class GpNuovoSareBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = -6557301028400788343L;

	@EJB
	private SareImportEJB sareImportEJB;

	@EJB
	private PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	private GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@EJB
	private GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	private GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	private PfPrincipal superuser;
	private boolean multipleSuperuser;
	private GpProfilatura profilaturaAttuale;
	private LazyMittentiSareModel profilatureMittentiSare;
	private String usernameSearch;
	private String codFiscSearch;

	private GpRuolo ruoloGestisciCO;

	@Override
	protected void initPostConstruct() {
		// Cerco un utente con ruolo "Superuser SARE".
		try {
			superuser = pfPrincipalEJB.findSuperuserSare();
		} catch (MyCasNoResultException e) {
			superuser = null;
			multipleSuperuser = false;
			return;
		} catch (MyCasTooManyResultsException e) {
			superuser = null;
			multipleSuperuser = true;
			return;
		}

		// Se ho trovato il superuser, prendo tra le sue profilature una con ruolo "Gestione CO".
		try {
			ruoloGestisciCO = gpRuoloEJB.findByFilter(ConstantsSingleton.GpRuoloConstants.GESTIONE_CO, null).get(0);
			List<GpProfilatura> profilatureSuperUser = gpProfilaturaEJB.findForIdPfPrincipal(superuser
					.getIdPfPrincipal());
			for (GpProfilatura profilatura : profilatureSuperUser) {
				if (profilatura.getGpRuoloGruppo().getGpRuolo().equals(ruoloGestisciCO)) {
					profilaturaAttuale = profilatura;
					break;
				}
			}
		} catch (Exception e) {
			log.error("Errore durante la ricerca della profilatura del superuser: " + e.toString());
		}
	}

	public void doSearch() {
		// Cerco tutti le profilature disponibili per il ruolo 'Gestione CO'
		try {
			setProfilatureMittentiSare(new LazyMittentiSareModel(usernameSearch, codFiscSearch, gpRuoloGruppoEJB));
		} catch (Exception e) {
			log.error("Errore durante la ricerca delle profilature gestioneCO: " + e.toString());
		}
	}

	/**
	 * Rimuove la 'maschera' attuale al superuser (ovvero l'attuale profilatura con 'Visualizza CO').
	 */
	public void rimuoviMaschera() {
		if (profilaturaAttuale != null) {
			try {
				gpProfilaturaEJB.remove(profilaturaAttuale.getIdGpProfilatura());
				profilaturaAttuale = null;
			} catch (MyCasException e) {
				log.error("Errore durante la rimozione dela maschera del superuser: " + e.toString());
			}
		}
	}

	/**
	 * Toglie la maschera attuale al superuser e gliene assegna un'altra, basata su un altro utente.
	 */
	public void cambiaMaschera(Integer idGruppoDaCopiare) {
		rimuoviMaschera();
		try {
			GpRuoloGruppo nuovaMaschera = gpRuoloGruppoEJB.findOrCreate(ruoloGestisciCO.getIdGpRuolo(),
					idGruppoDaCopiare, accountInfoBean.getIdPfPrincipal());
			profilaturaAttuale = gpProfilaturaEJB.add(superuser.getIdPfPrincipal(), nuovaMaschera,
					accountInfoBean.getIdPfPrincipal());
		} catch (Exception e) {
			log.error("Errore durante cambiaMaschera: " + e.toString());
		}
	}

	public void startPortingUtentiSare(Integer limit) {
		List<SareImport> sareImportRows = sareImportEJB.findAllSareAccounts(limit);
		log.info("Inizio importazione: " + sareImportRows.size());
		for (SareImport sareImportRow : sareImportRows) {
			sareImportEJB.processSingleSareAccount(sareImportRow);
		}
	}

	public void startPortingUtentiSare() {
		log.info("Inizio importazione completa");
		startPortingUtentiSare(null);
	}

	public String getUsernameSearch() {
		return usernameSearch;
	}

	public void setUsernameSearch(String usernameSearch) {
		this.usernameSearch = usernameSearch;
	}

	public String getCodFiscSearch() {
		return codFiscSearch;
	}

	public void setCodFiscSearch(String codFiscSearch) {
		this.codFiscSearch = codFiscSearch;
	}

	public PfPrincipal getSuperuser() {
		return superuser;
	}

	public void setSuperuser(PfPrincipal superuser) {
		this.superuser = superuser;
	}

	public GpProfilatura getProfilaturaAttuale() {
		return profilaturaAttuale;
	}

	public void setProfilaturaAttuale(GpProfilatura profilaturaAttuale) {
		this.profilaturaAttuale = profilaturaAttuale;
	}

	public LazyMittentiSareModel getProfilatureMittentiSare() {
		return profilatureMittentiSare;
	}

	public void setProfilatureMittentiSare(LazyMittentiSareModel profilatureMittentiSare) {
		this.profilatureMittentiSare = profilatureMittentiSare;
	}

	public boolean isMultipleSuperuser() {
		return multipleSuperuser;
	}

	public void setMultipleSuperuser(boolean multipleSuperuser) {
		this.multipleSuperuser = multipleSuperuser;
	}

}
