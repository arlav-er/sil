package it.eng.myportal.entity.ejb;

import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.MaPatente;
import it.eng.myportal.entity.MaPatentino;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.MaPatenteHome;
import it.eng.myportal.entity.home.MaPatentinoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Una classe scritta così male non la vedevo dal primo anno dell'università....
 * 
 * TODO ho tolto la sicurezza dai metodi per poterli richiamare in startup. con un attimo di calma bisogna reinserirla e
 * riuscire a chiamare l'ejb allo startup con i permessi di amministratore.
 * 
 * @author Vuoto F.
 * 
 */
// @SecurityDomain(it.eng.myportal.security.SecurityDomains.MYPORTAL)
@RolesAllowed({ it.eng.myportal.security.Roles.AMMINISTRATORE })
@Stateless
public class DbManagerEjb {

	@EJB
	transient AcCandidaturaHome acCandidaturaHome;

	@EJB
	transient ProvinciaHome provinciaHome;
	@EJB
	transient CvDatiPersonaliHome cvs;
	@EJB
	transient DeTipoMessaggioHome deTipoMessaggioHome;
	@EJB
	transient MsgMessaggioHome msgMessaggioHome;
	@EJB
	transient PfPrincipalHome pfPrincipalHome;
	@EJB
	transient UtenteInfoHome utenteInfoHome;
	@EJB
	transient VaDatiVacancyHome vaDatiVacancyHome;
	@EJB
	transient PtScrivaniaHome ptScrivaniaHome;
	@EJB
	transient DePatenteHome dePatente;
	@EJB
	transient DePatentinoHome dePatentino;

	@EJB
	transient MaPatenteHome maPatente;
	@EJB
	transient MaPatentinoHome maPatentino;

	@EJB
	CvAlboHome cvAlboHome;

	@PersistenceContext
	private EntityManager entityManager;

	private Log log = LogFactory.getLog(this.getClass());

	public DbManagerEjb() {

	}

	public int adeguaMessaggi() {
		TypedQuery<String> q = entityManager.createNamedQuery("findTickets", String.class);
		List<String> tickets = q.getResultList();

		int num = 0;
		// scorri tutti i ticket estratti e riordina i messaggi uno ad uno
		for (String ticket : tickets) {
			TypedQuery<MsgMessaggio> query = entityManager.createNamedQuery("findAllByTicket", MsgMessaggio.class)
					.setParameter("ticket", ticket);
			List<MsgMessaggio> messaggi = query.getResultList();
			for (int i = 0; i < messaggi.size(); i++) {
				MsgMessaggio messaggio = messaggi.get(i);
				if (i > 0) {
					MsgMessaggio precedente = messaggi.get(i - 1);
					messaggio.setPrecedente(precedente);
					entityManager.merge(messaggio);
					num++;
				}
			}
		}
		return num;

	}

	/**
	 * Inserisce le portlet sulle scrivania dell'utente.
	 * 
	 * @param user
	 *            utente a cui fornire la portlet
	 * @param amministratore
	 *            principal dell'amministratore
	 * @param codTipoGruppo
	 *            tipologia del gruppo dell'utente
	 */
	/*
	 * private void inserisciPortlets(PfPrincipal user, PfPrincipal amministratore, String codTipoGruppo) {
	 * 
	 * DeTipoGruppo deTipoGruppo = deTipoGruppoHome.findById(codTipoGruppo);
	 * 
	 * CriteriaBuilder qb = entityManager.getCriteriaBuilder(); CriteriaQuery<PtPortlet> query =
	 * qb.createQuery(PtPortlet.class); Root<PtPortlet> portlet = query.from(PtPortlet.class); Join<PtPortlet,
	 * DeTipoGruppo> gruppo = portlet.join(PtPortlet_.deTipoGruppo);
	 * query.where(qb.equal(gruppo.get(DeTipoGruppo_.codTipoGruppo), codTipoGruppo));
	 * 
	 * TypedQuery<PtPortlet> q = entityManager.createQuery(query); List<PtPortlet> portlets = q.getResultList(); //
	 * recupera tutte le // portlet di quel tipo // gruppo
	 * 
	 * if (portlets != null) { for (int i = 0; i < portlets.size(); i++) { PtPortlet iesimaPortlet = portlets.get(i);
	 * PtScrivania iesimaScrivania = findByIdPfPrincipalAndIdPtPortlet(user.getIdPfPrincipal(),
	 * iesimaPortlet.getIdPtPortlet()); if (iesimaScrivania != null) continue; // questa portlet ce l'ha già
	 * iesimaScrivania = new PtScrivania(); iesimaScrivania.setPfPrincipal(user); iesimaScrivania.setFlagRidotta(false);
	 * iesimaScrivania.setFlagVisualizza(true); iesimaScrivania.setPtPortlet(iesimaPortlet);
	 * iesimaScrivania.setDtmIns(new Date()); iesimaScrivania.setDtmMod(new Date());
	 * iesimaScrivania.setPfPrincipalIns(amministratore); iesimaScrivania.setPfPrincipalMod(amministratore); // torna il
	 * massimo numero di posizione presente +1 //
	 * 
	 * // calcolata in base allo schema seguente // left right // 1 2 // 3 4 // 5 6 e così di seguito
	 * iesimaScrivania.setPosizione(i % 5); iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");
	 * 
	 * ptScrivaniaHome.persist(iesimaScrivania); } } }
	 */

	/**
	 * Trova un record ptScrivania a partire da idPortlet e idUtente
	 * 
	 * @param idPtPortlet
	 * @param idPfPrincipal
	 * @return
	 */
	/*
	 * private PtScrivania findByIdPfPrincipalAndIdPtPortlet(Integer idPfPrincipal, Integer idPtPortlet) {
	 * CriteriaBuilder qb = entityManager.getCriteriaBuilder(); CriteriaQuery<PtScrivania> query =
	 * qb.createQuery(PtScrivania.class); Root<PtScrivania> scrivania = query.from(PtScrivania.class); Join<PtScrivania,
	 * PtPortlet> portlet = scrivania.join(PtScrivania_.ptPortlet); Join<PtScrivania, PfPrincipal> pfPrincipal =
	 * scrivania.join(PtScrivania_.pfPrincipal); query.where(qb.equal(portlet.get(PtPortlet_.idPtPortlet), idPtPortlet),
	 * qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal)); TypedQuery<PtScrivania> q =
	 * entityManager.createQuery(query); List<PtScrivania> portlets = q.getResultList(); // recupera tutte le // portlet
	 * di quel tipo // gruppo if (portlets != null && portlets.size() == 1) { return portlets.get(0); } else { return
	 * null; }
	 * 
	 * }
	 */

	public int adeguaNoteVacancy() {
		List<VaDatiVacancy> cv = vaDatiVacancyHome.findAll();
		StringBuilder finalNote = new StringBuilder();
		boolean firstTime = true;
		boolean done;
		int updated = 0;
		List<MaPatente> patenti = maPatente.findAll();
		// faccio caching per le prestazioni
		HashMap<String, String> codpatenti = new HashMap<String, String>();
		HashMap<String, String> codpatentini = new HashMap<String, String>();

		for (MaPatente maPatente : patenti) {
			codpatenti.put(maPatente.getCodPatente(), maPatente.getCodPatenteMin());
		}
		List<MaPatentino> patentini = maPatentino.findAll();
		for (MaPatentino maPatentino : patentini) {
			codpatentini.put(maPatentino.getCodPatentino(), maPatentino.getCodPatentinoMin());
		}

		// SCORRE TUTTI I CURRICULUM alla ricerce di codifiche non ministeriali
		for (VaDatiVacancy vaDatiVac : cv) {
			finalNote.delete(0, finalNote.length());
			if (vaDatiVac.getNoteAbilitazioni() != null)
				finalNote.append(vaDatiVac.getNoteAbilitazioni());
			finalNote.append("Per consentire la sincronizzazione del CV sul sistema nazionale Cliclavoro,"
					+ " la classificazione di Albi, Patenti e Patentini è stata aggiornata. Di seguito"
					+ " un riepilogo delle abilitazioni precedentemente inserite e non più usabili:");

			done = false;
			firstTime = true;
			// Integer me = vaDatiVac.getIdVaDatiVacancy();
			// spazzola gli albi
			for (VaAlbo vacancy : vaDatiVac.getVaAlbos()) {

				if (!vacancy.getDeAlbo().getFlagMin()) {
					if (firstTime) {
						finalNote.append("\n-Albi: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + vacancy.getDeAlbo().getDescrizione()) == -1) {
						finalNote.append("\n    " + vacancy.getDeAlbo().getDescrizione());
					}
					done = true;
					entityManager.remove(vacancy);
					// cvDatiPersonali.getCvAlbos().remove(cvAlbo);
				} else {
					// niente: le codifiche sono già ministeriali
				}
			}
			firstTime = true;
			for (VaPatente patente : vaDatiVac.getVaPatentes()) {
				if (!codpatenti.containsKey(patente.getDePatente().getCodPatente())) {
					if (firstTime) {
						finalNote.append("\n-Patenti: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + patente.getDePatente().getDescrizione()) == -1) {
						finalNote.append("\n    " + patente.getDePatente().getDescrizione());
					}
					done = true;
					// TODO cancellazione
					entityManager.remove(patente);
				} else {// aggiorno a ministeriale
					String newcod = codpatenti.get(patente.getDePatente().getCodPatente());
					patente.setDePatente(dePatente.findById(newcod));
					entityManager.persist(patente);
				}
			}
			firstTime = true;
			for (VaPatentino patentino : vaDatiVac.getVaPatentinos()) {
				if (!codpatentini.containsKey(patentino.getDePatentino().getCodPatentino())) {
					if (firstTime) {
						finalNote.append("\n-Patentini: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + patentino.getDePatentino().getDescrizione()) == -1) {
						finalNote.append("\n    " + patentino.getDePatentino().getDescrizione());
					}
					done = true;
					entityManager.remove(patentino);
				} else {
					String newcod = codpatentini.get(patentino.getDePatentino().getCodPatentino());
					patentino.setDePatentino(dePatentino.findById(newcod));
					entityManager.persist(patentino);
				}
			}
			// forza le cancellazioni

			// Aggiorna il curriculum
			if (done) {
				entityManager.refresh(vaDatiVac);
				updated++;
				vaDatiVac.setNoteAbilitazioni(finalNote.toString());
				entityManager.merge(vaDatiVac);
				entityManager.flush();
			}

		}

		return updated;
	}

	public int adeguaNoteCurriculum() {
		List<CvDatiPersonali> cv = cvs.findAll();
		StringBuilder finalNote = new StringBuilder();
		boolean firstTime = true;
		boolean done;
		int updated = 0;
		List<MaPatente> patenti = maPatente.findAll();
		// faccio caching per le prestazioni
		HashMap<String, String> codpatenti = new HashMap<String, String>();
		HashMap<String, String> codpatentini = new HashMap<String, String>();
		// le righe delle note non vanno ripetute
		// HashSet<String> doNotBotherMe = new HashSet<String>();

		for (MaPatente maPatente : patenti) {
			codpatenti.put(maPatente.getCodPatente(), maPatente.getCodPatenteMin());
		}
		List<MaPatentino> patentini = maPatentino.findAll();
		for (MaPatentino maPatentino : patentini) {
			codpatentini.put(maPatentino.getCodPatentino(), maPatentino.getCodPatentinoMin());
		}

		// SCORRE TUTTI I CURRICULUM alla ricerce di codifiche non ministeriali
		for (CvDatiPersonali cvDatiPersonali : cv) {
			finalNote.delete(0, finalNote.length());
			if (cvDatiPersonali.getNoteAbilitazioni() != null)
				finalNote.append(cvDatiPersonali.getNoteAbilitazioni());
			finalNote.append("Per consentire la sincronizzazione del CV sul sistema nazionale Cliclavoro,"
					+ " la classificazione di Albi, Patenti e Patentini è stata aggiornata. Di seguito"
					+ " un riepilogo delle abilitazioni precedentemente inserite e non più usabili:");

			done = false;
			firstTime = true;
			// Integer me = cvDatiPersonali.getIdCvDatiPersonali();
			// spazzola gli albi
			for (CvAlbo cvAlbo : cvDatiPersonali.getCvAlbos()) {

				if (!cvAlbo.getDeAlbo().getFlagMin()) {
					if (firstTime) {
						finalNote.append("\n-Albi: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + cvAlbo.getDeAlbo().getDescrizione()) == -1) {
						finalNote.append("\n    " + cvAlbo.getDeAlbo().getDescrizione());
					}
					done = true;
					entityManager.remove(cvAlbo);
					// cvDatiPersonali.getCvAlbos().remove(cvAlbo);
				} else {
					// niente: le codifiche sono già ministeriali
				}
			}
			firstTime = true;
			for (CvPatente patente : cvDatiPersonali.getCvPatentes()) {
				if (!codpatenti.containsKey(patente.getDePatente().getCodPatente())) {
					if (firstTime) {
						finalNote.append("\n-Patenti: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + patente.getDePatente().getDescrizione()) == -1) {
						finalNote.append("\n    " + patente.getDePatente().getDescrizione());
					}
					done = true;
					// TODO cancellazione
					entityManager.remove(patente);
				} else {// aggiorno a ministeriale
					String newcod = codpatenti.get(patente.getDePatente().getCodPatente());
					patente.setDePatente(dePatente.findById(newcod));
					entityManager.persist(patente);
				}
			}
			firstTime = true;
			for (CvPatentino patentino : cvDatiPersonali.getCvPatentinos()) {
				if (!codpatentini.containsKey(patentino.getDePatentino().getCodPatentino())) {
					if (firstTime) {
						finalNote.append("\n-Patentini: ");
						firstTime = false;
					}
					if (finalNote.indexOf("\n    " + patentino.getDePatentino().getDescrizione()) == -1) {
						finalNote.append("\n    " + patentino.getDePatentino().getDescrizione());
					}
					done = true;
					entityManager.remove(patentino);
				} else {
					String newcod = codpatentini.get(patentino.getDePatentino().getCodPatentino());
					patentino.setDePatentino(dePatentino.findById(newcod));
					entityManager.persist(patentino);
				}
			}
			// forza le cancellazioni

			// Aggiorna il curriculum
			if (done) {
				entityManager.refresh(cvDatiPersonali);
				updated++;
				cvDatiPersonali.setNoteAbilitazioni(finalNote.toString());
				entityManager.merge(cvDatiPersonali);
				entityManager.flush();
			}

		}

		return updated;
	}

	/**
	 * Rigenera i POI delle vacancy, di tutte oppure solo di quelle che non ne hanno gia' uno
	 * 
	 * @param tutte
	 *            se true allora rigenera i poi di tutte le vacancies, altrimenti solo di quelle senza gia' uno
	 */
	public Integer generaPoiVacancies() {
		List<VaDatiVacancy> vacancies = new ArrayList<VaDatiVacancy>();
		Integer count = 0;

		vacancies = vaDatiVacancyHome.findAllWithoutPOI();

		for (VaDatiVacancy vacancy : vacancies) {
			try {
				vaDatiVacancyHome.secureMerge(vacancy);
			} catch (Exception e) {
				log.error("Errore nella generazione della vacancy con idVaDatiVacancy = "
						+ vacancy.getIdVaDatiVacancy() + " " + e);
			}
			count++;
		}

		return count;
	}
}