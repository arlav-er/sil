package it.eng.myportal.entity.ejb;

import it.eng.myportal.cliclavoro.vacancy.CondizioniOfferte;
import it.eng.myportal.cliclavoro.vacancy.DatiAnagrafici;
import it.eng.myportal.cliclavoro.vacancy.DatiContatto;
import it.eng.myportal.cliclavoro.vacancy.DatiSistema;
import it.eng.myportal.cliclavoro.vacancy.DatoreLavoro;
import it.eng.myportal.cliclavoro.vacancy.Vacancy;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Session Bean implementation class ClicLavoroEjb
 */
@Stateless
public class AddAzVacancyClicLavoroEjb {

	protected final Log log = LogFactory.getLog(AddAzVacancyClicLavoroEjb.class);
	
	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;
	
	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;

	@EJB
	PfPrincipalHome pfPrincipalHome;
	
	@EJB
	AziendaInfoHome aziendaInfoHome;
	
	@EJB
	DeComuneHome deComuneHome;
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	
	public void aggiornaVacancyClicLavoro(int numVacancies) {  
		try {		
			// recupero tutte le numVacancies vacancies con id_pf_principal_palese e di provenienza MIN
			log.info("===FORZATURA AGGIORNAMENTO VACANCY MIN START===");
			List<VaDatiVacancy> elencoVacancies = getVacancyDaAggiornare(numVacancies);
			for (VaDatiVacancy vacancy : elencoVacancies) {				
				// recupero gli xml per le vacancy estratte (cod_comunicazione)
				Integer idVac = vacancy.getIdVaDatiVacancy();
				VaVacancyCl vacCl = vacancy.getVaVacancyCl();
				String codComunicazione = vacCl.getCodComunicazione();
				List<ClInvioComunicazione> listComs = getComunicazioniDaInserire(codComunicazione);
				if (!listComs.isEmpty()) {
					ClInvioComunicazione com =listComs.get(0);
					if (("INVIO_VACANCY").equalsIgnoreCase(com.getAzioneServizio().name())) {
						Vacancy objVacancy = clicLavoroVacancyEjb.convertToVacancy(com.getFileComunicazione());
						// inserisco l'azienda palese
						
						DatoreLavoro datore = objVacancy.getDatoreLavoro();
						CondizioniOfferte condizioniOfferta = objVacancy.getRichiesta().getCondizioniOfferte();
						DatiSistema datiSistema = objVacancy.getDatiSistema();
						PfPrincipal utenzaPalese = trovaUtenzaPalese(datore, datiSistema, "MIN");
						
						// 	aggiorno la vacancy
						Date now = new Date();
						vacancy.setPfPrincipalPalese(utenzaPalese);
						vacancy.setDtmMod(now);						
						entityManager.merge(vacancy);
					}
				}
			}
			log.info("===FORZATURA AGGIORNAMENTO VACANCY MIN STOP===");
		} catch (Exception e) {
			throw new MyPortalException("Errore durante l'aggiornamento", e, true);
		}

	}
	
	
	private PfPrincipal trovaUtenzaPalese(DatoreLavoro datore, DatiSistema datiSistema, String provenienza) {
		PfPrincipal utenzaPalese = null;
		String cfAz = datore.getDatiAnagrafici().getCodicefiscale();
		String emailAz = datore.getDatiContatto().getEmail();
		
		// verifico l'esistenza di una azienda (con accesso al SARE) con il
		// cf uguale a quello dell'xml
		// se non esiste verifico la presenza di un'azienda con la mail di
		// registrazione
		// se non esiste nessuna la vacancy NON risulta PALESE			
		List<AziendaInfo> aziendaInfoList = aziendaInfoHome.findListByCodiceFiscale(cfAz.toUpperCase());
		if (aziendaInfoList.size() == 1) {
			AziendaInfo aziendaInfo = aziendaInfoList.get(0);
			DeAutorizzazioneSare deAutorizzazioneSare = aziendaInfo.getDeAutorizzazioneSare();
			if (deAutorizzazioneSare != null) {	// 03/06/13 fix per aziende senza codice autorizzazione
				String codAutorizzazioneSare = deAutorizzazioneSare.getCodAutorizzazioneSare();
				if ("4".equalsIgnoreCase(codAutorizzazioneSare)) {
					utenzaPalese = aziendaInfo.getPfPrincipal();
				}
			}
		} else {
			List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailAz.toUpperCase());
			if (listaPrincipal.size() == 1) {
				PfPrincipal pfPrinc = listaPrincipal.get(0);
				if (pfPrinc.isAzienda()) {
					AziendaInfo az = pfPrinc.getAziendaInfo();
					DeAutorizzazioneSare deAutorizzazioneSare = az.getDeAutorizzazioneSare();
					if (deAutorizzazioneSare != null) {	// 06/06/13 fix per aziende senza codice autorizzazione
						String codAutorizzazioneSare = deAutorizzazioneSare.getCodAutorizzazioneSare();
						if ("4".equalsIgnoreCase(codAutorizzazioneSare)) {
							utenzaPalese = pfPrinc;
						}
					}
				}
			}
		}
		// 12/06/2013 DONA 
		// per inviare il messaggion di contatto la vacancy
		// deve essere agganciata sempre ad una azienda (se non trovata viene creata una fittizia)   
		if (utenzaPalese == null) {
			// verifico se non ho già creato un'azienda palese cl_XXXXXXXX
			List<AziendaInfo> aziendaInfoClPaleseList = aziendaInfoHome.findListByCodiceFiscale(Utils.cut("cl_"+cfAz, 16, false));
			if (aziendaInfoClPaleseList.size() == 1) {
				AziendaInfo aziendaInfo = aziendaInfoClPaleseList.get(0);
				utenzaPalese = aziendaInfo.getPfPrincipal();
			} else {				
				AziendaInfo azInfo = creaAziendaPalese(datore, false, provenienza);
				utenzaPalese = azInfo.getPfPrincipal();
			}
		}			
	
		return utenzaPalese;
	}
	
	
	private AziendaInfo creaAziendaPalese(DatoreLavoro datore, boolean flagAttiva, String provenienza) {
		DatiAnagrafici datiAnag = datore.getDatiAnagrafici();
		DatiContatto datiCont = datore.getDatiContatto();
		// inserisce un nuovo utente per l'azienda richiedente
		// inserimento dei principal delle aziende
		RegisterAziendaDTO registerAzRichiedente = new RegisterAziendaDTO();

		registerAzRichiedente.setUsername(Utils.cut("cl_"+datiAnag.getCodicefiscale(), 16, false));
		registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
		registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		String mailRandom = "cl_"+Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH)+"@mail.it";
		registerAzRichiedente.setEmail(mailRandom);
		registerAzRichiedente.setEmailConfirm(mailRandom);
		
		registerAzRichiedente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		registerAzRichiedente.setAttivo(false);
		registerAzRichiedente.setAcceptInformativa(true);
		registerAzRichiedente.setNome(Utils.cut(datiAnag.getCodicefiscale().trim(), 50, false));
		registerAzRichiedente.setCognome(Utils.cut(datiAnag.getDenominazione().trim(), 50, false));
		registerAzRichiedente.setCodiceFiscale(datiAnag.getCodicefiscale());
		registerAzRichiedente.setIndirizzo(datiCont.getIndirizzo());
		registerAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		registerAzRichiedente.setCap(datiCont.getCap());
		registerAzRichiedente.setTelefono(datiCont.getTelefono());
		registerAzRichiedente.setDomanda("Il codice fiscale dell'azienda è?");
		registerAzRichiedente.setRisposta(datiAnag.getCodicefiscale());

		registerAzRichiedente.setRagioneSociale(datiAnag.getDenominazione());
		SedeDTO sedeAzRichiedente = new SedeDTO();
		sedeAzRichiedente.setIndirizzo(datiCont.getIndirizzo());
		sedeAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		sedeAzRichiedente.setCap(datiCont.getCap());
		sedeAzRichiedente.setFax(datiCont.getFax());
		sedeAzRichiedente.setTelefono(datiCont.getTelefono());
		registerAzRichiedente.setSedeOperativa(sedeAzRichiedente);

		AziendaInfo azPalese = aziendaInfoHome.register(registerAzRichiedente, true);

		return azPalese;
	}
	
	private List<ClInvioComunicazione> getComunicazioniDaInserire(String codComunicazione) {
		TypedQuery<ClInvioComunicazione> query = entityManager.createNamedQuery("findComunicazioneByCodComunicazione", ClInvioComunicazione.class).setParameter("codComunicazione", codComunicazione);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
	
	private List<VaDatiVacancy> getVacancyDaAggiornare(int numLimit) {
		TypedQuery<VaDatiVacancy> query = entityManager.createNamedQuery("findVaDatiVacancyClDaAgg", VaDatiVacancy.class).setMaxResults(numLimit);
		List<VaDatiVacancy> list = query.getResultList();
		
		return list;
	}
}
