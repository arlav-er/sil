package it.eng.myportal.timer;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.cliclavoro.candidatura.Candidatura;
import it.eng.myportal.cliclavoro.messaggio.Messaggio;
import it.eng.myportal.cliclavoro.vacancy.Vacancy;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroMessaggioEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.ejb.DbManagerEjb;
import it.eng.myportal.entity.ejb.GenDecodRandom;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

@Startup
@Singleton
public class ReceiveClicLavoroEjb {

	protected final Log log = LogFactory.getLog(ReceiveClicLavoroEjb.class);
	private static final String TIMER_NAME = "receiveClicLavoroTimer";

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	DbManagerEjb dbManagerEjb;

	@EJB
	GenDecodRandom genDecodeRandom;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;

	@EJB
	ClicLavoroMessaggioEjb clicLavoroMessaggioEjb;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	TsTimerEJB tsTimerEJB;

	@Schedule(minute = "*/10", hour = "*", persistent = false)
	public void createCandidaturaMyportal() {
		Calendar batchStart = Calendar.getInstance();
		try {
			if (!tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
				log.warn("TIMER " + TIMER_NAME + " DISABILITATO, non eseguo. JBOSS_NODE_NAME="+System.getProperty(TsTimerEJB.JBOSS_NODE_NAME));
				return;
			}
			processCreazioneMessaggioMyPortal();
			processCreazioneCandidatura();
			processCreazioneVacancy();
			processComunicazioniDaInserire();
			processVacancyDaInserire();
			
			
			long elapsedTimeMillis = Calendar.getInstance().getTimeInMillis() - batchStart.getTimeInMillis();
			float elapsedTimeMin = elapsedTimeMillis / (60 * 1000F);
			log.info("===BATCH CONCLUSO, TEMPO TOTALE " + elapsedTimeMin + " MINUTI");

		} catch (Throwable e) {
			// NON dovrebbe MAI succedere, significa che c'è roba non trappata
			log.error("System error non gestito:" + e.getMessage());
		}
	}

	public void processVacancyDaInserire() {
		int processedTemp = 0;
		log.info("===BATCH CREAZIONE VACANCY MYPORTAL START===");
		List<ClInvioComunicazione> elencoVacancy = getComunicazioniDaInserire(AzioneServizio.INVIO_VACANCY);
		for (ClInvioComunicazione clInvioCom : elencoVacancy) {
			try {
				Vacancy vac = clicLavoroVacancyEjb.convertToVacancy(clInvioCom.getFileComunicazione());
				// inserisco la vacancy -> NUOVA TRANSAZIONE
				clicLavoroVacancyEjb.riceviVacancyCliclavoro(vac, clInvioCom.getMittente());
				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);
				processedTemp++;
			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA
						.equals(e.getCodErrore())) {
					log.info(
							"Ricezione vacancy cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
									+ clInvioCom.getCodComunicazione());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata.");
					clInvioComunicazioneHome.merge(clInvioCom);

				} else {
					log.error("Errore durante la creazione della vacancy:" + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Errore durante la creazione della vacancy: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della vacancy: " + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della vacancy: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===BATCH CREAZIONE VACANCY MYPORTAL TERMINATO, PROCESSATI: " + processedTemp);
	}

	public void processComunicazioniDaInserire() {
		int processedTemp = 0;
		log.info("===BATCH CREAZIONE CANDIDATURA MYPORTAL START===");
		List<ClInvioComunicazione> elencoCandidature = getComunicazioniDaInserire(AzioneServizio.INVIO_CANDIDATURA);
		for (ClInvioComunicazione clInvioCom : elencoCandidature) {
			try {
				// recupero oggetto candidatura per inserimento record
				// CV
				// sul portale
				Candidatura candidatura = clicLavoroEjb.convertToCandidatura(clInvioCom.getFileComunicazione());
				// inserisco il cv -> NUOVA TRANSAZIONE
				clicLavoroEjb.riceviCandidaturaCliclavoro(candidatura, clInvioCom.getMittente());

				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);
				processedTemp++;
			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA
						.equals(e.getCodErrore())) {
					log.info(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
									+ clInvioCom.getCodComunicazione());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata.");
					clInvioComunicazioneHome.merge(clInvioCom);
				} else {
					log.error("Errore durante la creazione della candidatura: " + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Errore durante la creazione della candidatura: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===BATCH CREAZIONE CANDIDATURA MYPORTAL TERMINATO, PROCESSATI: " + processedTemp);
	}

	public void processCreazioneVacancy() {
		int processedTemp = 0;
		log.info("===BATCH CREAZIONE VACANCY MYPORTAL DA SIL START===");
		List<ClInvioComunicazione> elencoVacancyDaSil = getComunicazioniDaInserireFromSil(
				AzioneServizio.INVIO_VACANCY);
		for (ClInvioComunicazione clInvioCom : elencoVacancyDaSil) {
			try {
				Vacancy vac = clicLavoroVacancyEjb.convertToVacancy(clInvioCom.getFileComunicazione());
				// inserisco la vacancy -> NUOVA TRANSAZIONE
				clicLavoroVacancyEjb.riceviVacancyCliclavoro(vac, clInvioCom.getMittente());
				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);
				processedTemp++;
			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA
						.equals(e.getCodErrore())) {
					log.info(
							"Ricezione vacancy cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
									+ clInvioCom.getCodComunicazione());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata.");
					clInvioComunicazioneHome.merge(clInvioCom);

				} else {
					log.error("Errore durante la creazione della vacancy: " + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Errore durante la creazione della vacancy: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della vacancy: " + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della vacancy: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===BATCH CREAZIONE VACANCY MYPORTAL DA SIL TERMINATO, PROCESSATI: " + processedTemp);
	}

	public void processCreazioneCandidatura() {
		int processedTemp = 0;
		log.info("===BATCH CREAZIONE CANDIDATURA MYPORTAL DA SIL START===");
		List<ClInvioComunicazione> elencoCandidatureDaSil = getComunicazioniDaInserireFromSil(
				AzioneServizio.INVIO_CANDIDATURA);
		for (ClInvioComunicazione clInvioCom : elencoCandidatureDaSil) {
			try {
				// recupero oggetto candidatura per inserimento record
				// CV
				// sul portale
				Candidatura candidatura = clicLavoroEjb.convertToCandidatura(clInvioCom.getFileComunicazione());
				// inserisco il cv -> NUOVA TRANSAZIONE
				clicLavoroEjb.riceviCandidaturaCliclavoro(candidatura, clInvioCom.getMittente());

				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);
				processedTemp++;

			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA
						.equals(e.getCodErrore())) {
					log.info(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
									+ clInvioCom.getCodComunicazione());

				} else {
					log.error("Errore durante la creazione della candidatura: " + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Errore durante la creazione della candidatura: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===BATCH CREAZIONE CANDIDATURA MYPORTAL DA SIL TERMINATO, PROCESSATI: " + processedTemp);
	}

	public void processCreazioneMessaggioMyPortal() {
		int processedTemp = 0;
		log.info("===BATCH CREAZIONE MESSAGGIO MYPORTAL START===");
		List<ClInvioComunicazione> elencoMessaggi = getComunicazioniDaInserire(AzioneServizio.INVIO_MESSAGGIO);
		for (ClInvioComunicazione clInvioCom : elencoMessaggi) {
			try {
				Messaggio msg = clicLavoroMessaggioEjb.convertToMessaggio(clInvioCom.getFileComunicazione());
				// inserisco il messaggio
				clicLavoroMessaggioEjb.riceviMessaggioCliclavoro(msg);
				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);
				processedTemp++;
			} catch (MyPortalException e) {
				log.error("Errore durante la creazione del messaggio" + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom
						.setDescrizioneErrore("Errore durante la creazione del messaggio: " + e.getStrMessaggio());
				clInvioComunicazioneHome.merge(clInvioCom);
			} catch (Exception e) {
				log.error("Errore durante la creazione del messaggio" + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione del messaggio: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===BATCH CREAZIONE MESSAGGIO MYPORTAL TERMINATO, PROCESSATI: " + processedTemp);
	}

	/**
	 * Restituisce l'elenco di tutte le candidature da inserire sul portale di provenienza da CLICLAVORO e SIL
	 * 
	 * 
	 * @return
	 */
	private List<ClInvioComunicazione> getComunicazioniDaInserire(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager
				.createNamedQuery("findComunicazioniDaInserire", ClInvioComunicazione.class)
				.setParameter("azServ", servizio).setMaxResults(300);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}

	private List<ClInvioComunicazione> getComunicazioniDaInserireFromSil(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager
				.createNamedQuery("findComunicazioniDaInserireFromSil", ClInvioComunicazione.class)
				.setParameter("azServ", servizio).setMaxResults(300);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
}
