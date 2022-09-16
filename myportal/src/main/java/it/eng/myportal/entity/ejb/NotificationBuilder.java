package it.eng.myportal.entity.ejb;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoRettificaDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.utils.AziendaInfoDTOHelper;
import it.eng.myportal.utils.ConstantsSingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class NotificationBuilder {

	private final String BASE_URL = "/" + ConstantsSingleton.CONTESTO_APP + "/faces/secure/";

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	/**
	 * Invia una notifica. La notifica viene inviata dal sistema (utente '0')
	 * 
	 * @param notifications
	 *            la notifica da inviare
	 * @return la notifica inviata
	 */
	public void sendNotification(Set<MsgMessaggioDTO> notifications) {
		for (MsgMessaggioDTO notification : notifications) {
			sendNotification(notification);
		}
	}

	public void sendNotification(MsgMessaggioDTO notification) {
		/* la notifica viene inviata dal sistema: utente '0' */
		msgMessaggioHome.persistDTO(notification, 0);
	}

	/**
	 * Data una richiesta di rettifica dati azienda, restituisce l'insieme delle
	 * notifiche da inviare
	 * 
	 * @param rettifica
	 * @return
	 */
	public Set<MsgMessaggioDTO> buildNotifications(AziendaInfoRettificaDTO rettifica) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		AziendaInfo aziendaInfo = aziendaInfoHome.findById(rettifica.getId());
		PfPrincipal pfPrincipal = aziendaInfo.getPfPrincipal();

		MsgMessaggioDTO toProvincia = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		toProvincia.setOggetto("Richiesta rettifica dati dell'utente " + pfPrincipal.getUsername());

		String strMsgAzienda = "L'utente\n" + "username: " + pfPrincipal.getUsername() + "<br/>" + "ragione sociale: "
				+ aziendaInfo.getRagioneSociale() + "<br/>"
				+ "ha richiesto una rettifica dei seguenti dati profilo:<br/>" + "<ul>";

		Map<String, Object> asMap = AziendaInfoDTOHelper.asMap(rettifica);
		for (Entry<String, Object> field : asMap.entrySet()) {
			if (field.getValue() != null) {
				strMsgAzienda += "<li>" + field.getKey() + ": " + field.getValue().toString() + "</li>";
			}
		}

		strMsgAzienda += "</ul>";

		strMsgAzienda += "Accedere alla pagina di rettifica direttamente da <a href=\"" + BASE_URL
				+ "provincia/modifica_dati_azienda.xhtml?idAziendaInfo=" + rettifica.getId()
				+ "\">questo link</a><br/>"
				+ "oppure spostarsi nell'apposita pagina di elenco di richieste di rettifica.";

		msgMessaggioHome.setDestinatario(toProvincia, pfPrincipal);
		toProvincia.setCorpo(strMsgAzienda);

		ret.add(toProvincia);
		return ret;
	}

	/**
	 * Data una candidatura, restituisce l'insieme di notifiche da inviare.
	 * 
	 * 
	 * @param candidatura
	 *            la candidatura che è appena stata effettuata
	 * @return
	 */
	public Set<MsgMessaggioDTO> buildNotifications(AcCandidatura candidatura) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		VaDatiVacancy datiVacancy = candidatura.getVaDatiVacancy();
		// la prima notifica è rivolta all'azienda che ha inserito l'offerta di
		// lavoro.
		// invio una notifica a ciascun utente dell'azienda
		PfPrincipal pfPrincipal;
		PfPrincipal pfPrincipalPalese = null;
		String strMsgAzienda;

		// se si tratta di una candidatura a vacancy

		String strViewCv;
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			strViewCv = ConstantsSingleton.LINK_VIEW_NOTIFICHE_CVRER;

		}else{
			strViewCv = ConstantsSingleton.LINK_VIEW_NOTIFICHE_OTHER;

		}
		if (datiVacancy != null) {
			strMsgAzienda = "Un utente si è candidato alla tua offerta di lavoro '" + datiVacancy.getNumRichiesta()
					+ "/" + datiVacancy.getNumAnno() + " " + datiVacancy.getDeProvenienzaVacancy().getDescrizione()
					+ " " + datiVacancy.getAttivitaPrincipale() + "'.<br/><a href=\"" + BASE_URL
					+ strViewCv + candidatura.getCvDatiPersonali().getIdCvDatiPersonali()
					+ "\">Visualizza il suo curriculum</a>.";
			if (candidatura.getCvLetteraAcc() != null && candidatura.getCvLetteraAcc().getIdCvLetteraAcc() != null) {
				strMsgAzienda += "<br/><a href=\"" + BASE_URL + "utente/lettere/visualizza_non_utente.xhtml?id="
						+ candidatura.getCvLetteraAcc().getIdCvLetteraAcc()
						+ "\">Visualizza la sua lettera di accompagnamento</a>.";
			}
			pfPrincipal = datiVacancy.getPfPrincipal();
			// recupero l'azienda per cui e' stata pubblicata la vacancy (nel
			// caso si tratti di una vacancy indiretta palese)
			pfPrincipalPalese = datiVacancy.getPfPrincipalPalese();
		} else { // se si tratta di un'autocandidatura spontanea (come un
					// rutto...)
			strMsgAzienda = "Un utente ti ha inviato un Curriculum Vitae.<br/><a href=\"" + BASE_URL
					+ strViewCv + candidatura.getCvDatiPersonali().getIdCvDatiPersonali()
					+ "\">Visualizza il suo curriculum</a>.";
			if (candidatura.getCvLetteraAcc() != null && candidatura.getCvLetteraAcc().getIdCvLetteraAcc() != null) {
				strMsgAzienda += "<br/><a href=\"" + BASE_URL + "utente/lettere/visualizza_non_utente.xhtml?id="
						+ candidatura.getCvLetteraAcc().getIdCvLetteraAcc()
						+ "\">Visualizza la sua lettera di accompagnamento</a>.";
			}
			pfPrincipal = candidatura.getPfPrincipal();
		}

		// la prima notifica è di sistema
		MsgMessaggioDTO msgAzienda = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		msgAzienda.setOggetto("Nuova candidatura");
		msgAzienda.setIdTo(pfPrincipal.getIdPfPrincipal());
		msgAzienda.setCorpo(strMsgAzienda);
		ret.add(msgAzienda);

		// Invio notifica a tutte le aziende associate ad AziendaInfo secondaria
		// (vacancy indiretta palese)
		if (pfPrincipalPalese != null) {
			MsgMessaggioDTO msgAzienda2 = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
			msgAzienda2.setOggetto("Nuova candidatura");
			msgAzienda2.setIdTo(pfPrincipalPalese.getIdPfPrincipal());
			msgAzienda2.setCorpo(strMsgAzienda);
			ret.add(msgAzienda2);
		}

		// notifica all'utente che si è candidato.

		String strViewVacancy;
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			strViewVacancy = ConstantsSingleton.LINK_VIEW_NOTIFICHE_VARER;

		}else{
			strViewVacancy = ConstantsSingleton.LINK_VIEW_NOTIFICHE_VAOTHER;

		}


		MsgMessaggioDTO msgUtente = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		if (datiVacancy != null) {
			msgUtente.setCorpo("La tua candidatura per l'offerta '" + datiVacancy.getNumRichiesta() + "/"
					+ datiVacancy.getNumAnno() + " " + datiVacancy.getDeProvenienzaVacancy().getDescrizione() + " "
					+ datiVacancy.getAttivitaPrincipale() + "' " + "è stata inviata correttamente.<br/>" + "<a href=\""
					+ BASE_URL + strViewVacancy + datiVacancy.getIdVaDatiVacancy()
					+ "\">Visualizza l'offerta</a>.");
		} else {
			msgUtente.setCorpo("Il tuo Curriculum è stato inviato all'azienda '"
					+ pfPrincipal.getAziendaInfo().getRagioneSociale() + "' correttamente.");
		}
		msgUtente.setOggetto("Candidatura inviata");
		CvDatiPersonali cvCand = candidatura.getCvDatiPersonali();
		Integer idCandidaturaUtente;
		if (cvCand.getPfPrincipalPalese() != null) {
			idCandidaturaUtente = cvCand.getPfPrincipalPalese().getIdPfPrincipal();
		}
		else {
			idCandidaturaUtente = cvCand.getPfPrincipal().getIdPfPrincipal();
		}
		msgUtente.setIdTo(idCandidaturaUtente);

		ret.add(msgUtente);
		return ret;
	}

	/**
	 * Data una candidatura, restituisce l'insieme di notifiche da inviare.
	 * 
	 * 
	 * @param candidatura
	 *            la candidatura che è appena stata effettuata
	 * @return
	 */
	public Set<MsgMessaggioDTO> buildNotificationsNewIdoRER(AcCandidatura candidatura) {
		boolean checkNotificaBuild = false;
		boolean checkNotificaBuildPalese = false;
		
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		VaDatiVacancy datiVacancy = candidatura.getVaDatiVacancy();
		// la prima notifica è rivolta all'azienda che ha inserito l'offerta di
		// lavoro.
		// invio una notifica a ciascun utente dell'azienda
		PfPrincipal pfPrincipal;
		PfPrincipal pfPrincipalPalese = null;
		String strMsgAzienda;

		// se si tratta di una candidatura a vacancy

		String strViewCv;
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			strViewCv = ConstantsSingleton.LINK_VIEW_NOTIFICHE_CVRER;

		}else{
			strViewCv = ConstantsSingleton.LINK_VIEW_NOTIFICHE_OTHER;
		}
		if (datiVacancy != null) {
			
			if(datiVacancy.getDeProvenienzaVacancy() != null) { // caso Provenienza MYPORTAL
			   if(datiVacancy.getDeProvenienzaVacancy().getCodProvenienza().equalsIgnoreCase(ConstantsSingleton.DeProvenienza.COD_MYPORTAL) ) {
				   //   && datiVacancy.getPfPrincipal().getIdPfPrincipal() == candidatura.getPfPrincipal().getIdPfPrincipal() ) {
				   checkNotificaBuild = true;
			   }else{ // caso Pubblicazione Anonima – Pubblicazione Anonima/Preselezione quando provenienza SIL%
				   if(datiVacancy.getDeProvenienzaVacancy().getCodProvenienza().startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)){
					   //if( datiVacancy.getPfPrincipal().getIdPfPrincipal() == candidatura.getPfPrincipal().getIdPfPrincipal() ) { //solo per l’azienda titolare dell’annuncio. INUTILE ERRATO
					     if (datiVacancy.getDeEvasioneRich() != null && !datiVacancy.getDeEvasioneRich().getCodEvasione().isEmpty()){ // vacancy dotata di cod evasione
					       if ( datiVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_ANONIMA) 
					            || datiVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_ANONIMA_PRESELEZIONE) )  {
					    	   // invio solo al titolare dell'annuncio che è il CPI
					    	   checkNotificaBuild = true;
					       }else if(datiVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE)) {
					    	     //invio del messaggio solo all'azienda ID_PF_PRINCIPAL_PALESE
					    	   checkNotificaBuildPalese = true;
					         }else if(datiVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE_PRESELEZIONE)) {
					        	// invio solo al titolare dell'annuncio che è il CPI
					        	 checkNotificaBuild = true;
					        }
					     }					  
					  }
			       }
			   }
			
			strMsgAzienda = "Un utente si è candidato alla tua offerta di lavoro '" + datiVacancy.getNumRichiesta()
					+ "/" + datiVacancy.getNumAnno() + " " + datiVacancy.getDeProvenienzaVacancy().getDescrizione()
					+ " " + datiVacancy.getAttivitaPrincipale() + "'.<br/><a href=\"" + BASE_URL + strViewCv
					//+ candidatura.getCvDatiPersonali().getIdCvDatiPersonali() + "\">Visualizza il suo curriculum</a>.";
			        + candidatura.getCvDatiPersonali().getIdCvDatiPersonali() + "&idAcCandidatura=" +  candidatura.getIdAcCandidatura() + "\">Visualizza il suo curriculum</a>.";
			
			if (candidatura.getCvLetteraAcc() != null && candidatura.getCvLetteraAcc().getIdCvLetteraAcc() != null) {
				strMsgAzienda += "<br/><a href=\"" + BASE_URL + "utente/lettere/visualizza_non_utente.xhtml?id="
						+ candidatura.getCvLetteraAcc().getIdCvLetteraAcc()
						+ "\">Visualizza la sua lettera di accompagnamento</a>.";
			}
			pfPrincipal = datiVacancy.getPfPrincipal();
			// recupero l'azienda per cui e' stata pubblicata la vacancy (nel
			// caso si tratti di una vacancy indiretta palese)
			pfPrincipalPalese = datiVacancy.getPfPrincipalPalese();
			
		} else { // se si tratta di un'autocandidatura spontanea (come un
					// rutto...)
			checkNotificaBuild = true;
			
			strMsgAzienda = "Un utente ti ha inviato un Curriculum Vitae.<br/><a href=\"" + BASE_URL
					+ strViewCv + candidatura.getCvDatiPersonali().getIdCvDatiPersonali()
					+ "&idAcCandidatura="
					+ candidatura.getIdAcCandidatura()
					+ "\">Visualizza il suo curriculum</a>.";
			if (candidatura.getCvLetteraAcc() != null && candidatura.getCvLetteraAcc().getIdCvLetteraAcc() != null) {
				strMsgAzienda += "<br/><a href=\"" + BASE_URL + "utente/lettere/visualizza_non_utente.xhtml?id="
						+ candidatura.getCvLetteraAcc().getIdCvLetteraAcc()
						+ "\">Visualizza la sua lettera di accompagnamento</a>.";
			}
			pfPrincipal = candidatura.getPfPrincipal();
		}

		// la prima notifica è di sistema
		if(checkNotificaBuild) {		 // caso 1) vacancy SIL%: anonima e anonima prselezione 2) autocandidatura 3) vacancy con prevenienza MYPORTAL 4) vacancy SIL% Palese/Preselezione
		  MsgMessaggioDTO msgAzienda = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
	  	  msgAzienda.setOggetto("Nuova candidatura");
		  msgAzienda.setIdTo(pfPrincipal.getIdPfPrincipal());
		  msgAzienda.setCorpo(strMsgAzienda);
		  ret.add(msgAzienda);
		}
		
		// Invio notifica a tutte le aziende associate ad AziendaInfo secondaria
		// (vacancy indiretta palese)
		if(checkNotificaBuildPalese) {		 // caso 1) vacancy SIL%: Palese 			
			if (pfPrincipalPalese != null) {
				MsgMessaggioDTO msgAzienda2 = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
				msgAzienda2.setOggetto("Nuova candidatura");
				msgAzienda2.setIdTo(pfPrincipalPalese.getIdPfPrincipal());
				msgAzienda2.setCorpo(strMsgAzienda);
				ret.add(msgAzienda2);
			}
		}
		// notifica all'utente che si è candidato.

		String strViewVacancy;
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			strViewVacancy = ConstantsSingleton.LINK_VIEW_NOTIFICHE_VARER;

		}else{
			strViewVacancy = ConstantsSingleton.LINK_VIEW_NOTIFICHE_VAOTHER;

		}


		MsgMessaggioDTO msgUtente = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		if (datiVacancy != null) {
			msgUtente.setCorpo("La tua candidatura per l'offerta '" + datiVacancy.getNumRichiesta() + "/"
					+ datiVacancy.getNumAnno() + " " + datiVacancy.getDeProvenienzaVacancy().getDescrizione() + " "
					+ datiVacancy.getAttivitaPrincipale() + "' " + "è stata inviata correttamente.<br/>" + "<a href=\""
					+ BASE_URL + strViewVacancy + datiVacancy.getIdVaDatiVacancy()
					+ "\">Visualizza l'offerta</a>.");
		} else {
			msgUtente.setCorpo("Il tuo Curriculum è stato inviato all'azienda '"
					+ pfPrincipal.getAziendaInfo().getRagioneSociale() + "' correttamente.");
		}
		msgUtente.setOggetto("Candidatura inviata");
		CvDatiPersonali cvCand = candidatura.getCvDatiPersonali();
		Integer idCandidaturaUtente;
		if (cvCand.getPfPrincipalPalese() != null) {
			idCandidaturaUtente = cvCand.getPfPrincipalPalese().getIdPfPrincipal();
		}
		else {
			idCandidaturaUtente = cvCand.getPfPrincipal().getIdPfPrincipal();
		}
		msgUtente.setIdTo(idCandidaturaUtente);

		ret.add(msgUtente);
		return ret;
	}
	
	/**
	 * Data una candidatura, restituisce l'insieme di notifiche da inviare.
	 * 
	 * @param datiVacancy
	 *            che è appena stata effettuata inserita/aggiornata
	 * @param isUpdate
	 * @param codProvenienza
	 * @return
	 */
	public Set<MsgMessaggioDTO> buildNotifications(VaDatiVacancy datiVacancy, boolean isUpdate, String codProvenienza) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		// invio una notifica a ciascuna azienda relativa alla vacancy
		AziendaInfo aziendaInfo;
		AziendaInfo aziendaInfoPalese = null;
		String strMsgAzienda;

		aziendaInfo = datiVacancy.getPfPrincipal().getAziendaInfo();
		// recupero l'azienda per cui e' stata pubblicata la vacancy (nel
		// caso si tratti di una vacancy indiretta palese)

		PfPrincipal pfPrincPalese = datiVacancy.getPfPrincipalPalese();
		if (pfPrincPalese != null) {
			aziendaInfoPalese = pfPrincPalese.getAziendaInfo();
		}
		// se si tratta di una vacancy aggiornata
		String oggettoNotifica;
		if (isUpdate) {
			oggettoNotifica = "Aggiornamento offerta di lavoro gestita da CPI";
			strMsgAzienda = "Aggiornamento offerta di lavoro '" + datiVacancy.getAttivitaPrincipale()
					+ "' gestita dal CPI di " + codProvenienza + ".<br/><a href=\"" + BASE_URL
					+ "azienda/vacancies/visualizza.xhtml?id=" + datiVacancy.getIdVaDatiVacancy()
					+ "\">Visualizza</a>.";
		} else {
			// se si tratta di una vacancy inserita
			oggettoNotifica = "Inserimento offerta di lavoro gestita da CPI";
			strMsgAzienda = "Inserimento offerta di lavoro '" + datiVacancy.getAttivitaPrincipale()
					+ "' gestita dal CPI di " + codProvenienza + ".<br/><a href=\"" + BASE_URL
					+ "azienda/vacancies/visualizza.xhtml?id=" + datiVacancy.getIdVaDatiVacancy()
					+ "\">Visualizza</a>.";
		}

		// Invio notifica

		MsgMessaggioDTO msgAzienda = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		msgAzienda.setOggetto(oggettoNotifica);
		msgAzienda.setIdTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		msgAzienda.setCorpo(strMsgAzienda);
		ret.add(msgAzienda);

		// Invio notifica a tutte le aziende associate ad AziendaInfo secondaria
		// (vacancy indiretta palese)
		if (aziendaInfoPalese != null) {
			MsgMessaggioDTO msgAzienda2 = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
			msgAzienda2.setOggetto(oggettoNotifica);
			msgAzienda2.setIdTo(aziendaInfoPalese.getPfPrincipal().getIdPfPrincipal());
			msgAzienda2.setCorpo(strMsgAzienda);
			ret.add(msgAzienda);
		}

		return ret;
	}

	/**
	 * Crea le notifiche per la cancellazione di una richiesta di rettifica.
	 * 
	 * @param pfPrincipal
	 *            utente che ha effettuato l'annullamento della rettifica
	 * @return la lista di notifiche da inviare
	 */
	public Set<MsgMessaggioDTO> buildNotificationsRettificaDelete(PfPrincipal pfPrincipal) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		MsgMessaggioDTO toProvincia = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		toProvincia.setOggetto("ANNULLAMENTO Richiesta rettifica dati dell'utente " + pfPrincipal.getUsername());

		String strMsgAzienda = "L'utente<br/>" + "username: " + pfPrincipal.getUsername() + "<br/>"
				+ "ragione sociale: " + pfPrincipal.getAziendaInfo().getRagioneSociale() + "<br/>"
				+ "ha annullato la precedente richiesta di rettifica dei dati profilo.";

		msgMessaggioHome.setDestinatario(toProvincia, pfPrincipal);
		toProvincia.setCorpo(strMsgAzienda);
		ret.add(toProvincia);
		return ret;
	}

	/**
	 * Crea le notifiche per la cancellazione di una richiesta di rettifica.
	 * 
	 * @param pfPrincipal
	 *            utente che ha effettuato l'annullamento della rettifica
	 * @return la lista di notifiche da inviare
	 */
	public Set<MsgMessaggioDTO> buildNotificationsRettificaRejected(Provincia provincia, AziendaInfo aziendaInfo) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		String oggetto = "Richiesta rettifica dati su "+ ConstantsSingleton.TITLE_APP +" respinta";
		String strMsgAzienda = "La provincia di "
				+ provincia.getDeProvincia().getDenominazione()
				+ " titolare delle informazioni "
				+ "ha respinto la tua richiesta di rettifica dei dati di profilo. Contattare eventualmente la provincia di riferimento per ulteriori informazioni.<br/>"
				+ "Saluti,<br/>" + ConstantsSingleton.TITLE_APP;

		MsgMessaggioDTO toAzienda = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		toAzienda.setOggetto(oggetto);
		toAzienda.setIdTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		toAzienda.setCorpo(strMsgAzienda);
		ret.add(toAzienda);

		return ret;
	}

	/**
	 * Crea le notifiche da inviare agli utenti di un'azienda quando la
	 * provincia ne modifica i dati
	 * 
	 * @param pfPrincipal
	 *            utente che ha effettuato la modifica dei dati
	 * @param aziendaInfo
	 *            azienda alla quale sono stati modificati i dati
	 * @return la lista di notifiche da inviare
	 */
	public Set<MsgMessaggioDTO> buildNotificationsAziendaInfoModified(Provincia provincia, AziendaInfo aziendaInfo) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		String oggetto = "Sono stati modificati i dati del tuo profilo su "+ ConstantsSingleton.TITLE_APP;

		String strMsgAzienda = "La provincia di " + provincia.getDeProvincia().getDenominazione()
				+ " titolare delle informazioni "
				+ "ha modificato i tuoi dati di profilo. Si prega di verificare che le modifiche siano corrette.<br/>"
				+ "Saluti,<br/>" + ConstantsSingleton.TITLE_APP;

		MsgMessaggioDTO toAzienda = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		toAzienda.setOggetto(oggetto);
		toAzienda.setIdTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		toAzienda.setCorpo(strMsgAzienda);
		ret.add(toAzienda);

		return ret;
	}

	public Set<MsgMessaggioDTO> buildNotifications(DeCpiDTO deCpi, UtenteCompletoDTO destinatario, Date data) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		String format = "dd-MM-yyyy";
		DateFormat df = new SimpleDateFormat(format);
		String strMsg = "";
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) {
			strMsg = "A seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su "+ ConstantsSingleton.TITLE_APP +" in data "
					+ df.format(data.getTime())
					+ ", ti è stato fissato un appuntamento presso il CPI a cui hai rilasciato la dichiarazione (indicato nella stampa della DID) per un colloquio con un operatore del CPI. La data e l’ora dell’appuntamento ti verranno comunicati tramite e-mail e/o SMS. Qualora non ti pervenisse nessuna informazione a riguardo è necessario che contatti il CPI entro 3 mesi dalla data di stipula della DID. Ti ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.";			
		}
		else {
			strMsg = "A seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su "+ ConstantsSingleton.TITLE_APP +" in data "
					+ df.format(data.getTime())
					+ ", ti è stato fissato un appuntamento presso il CPI a cui hai rilasciato la dichiarazione (indicato nella stampa della DID) per un colloquio con un operatore del CPI. La data e l’ora dell’appuntamento ti verranno comunicati tramite e-mail e/o SMS. Qualora non ti pervenisse nessuna informazione a riguardo è necessario che contatti il CPI. Ti ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.";
		}
				
		MsgMessaggioDTO msg = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);

		msg.setOggetto("Stipulata nuova DID");
		msg.setIdTo(destinatario.getId());
		msg.setIdFrom(0);
		msg.setCorpo(strMsg);
		ret.add(msg);

		return ret;
	}

	public MsgMessaggioDTO buildNotificationAddVetrina(PfPrincipal pf) {

		String msg = "A seguito dell'inserimento della tua offerta, "+ ConstantsSingleton.TITLE_APP +" ha creato automaticamente la tua"
				+ " vetrina con i dati minimi a disposizione. Per incrementare la tua visibilita' e rendere piu'"
				+ " completo il tuo profilo visualizzabile dagli utenti, compila le altre sezioni della vetrina"
				+ " cliccando su \"Aggiorna la tua vetrina\" e non dimenticare di inserire il tuo logo dalla"
				+ " sezione \"Profilo\". ";

		MsgMessaggioDTO msgDTO = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);

		msgDTO.setOggetto("Creazione automatica vetrina");
		msgDTO.setIdTo(pf.getIdPfPrincipal());
		msgDTO.setIdFrom(0);
		msgDTO.setCorpo(msg);
		return msgDTO;
	}


	/**
	 * Crea la notifica verso la redazione per richieste primo contatto di cv
	 * intermediati
	 * 
	 * @param cvDatiPersonali
	 * @param denominazioneAzienda
	 * @return
	 */
	public Set<MsgMessaggioDTO> buildNotificationCvIntermediato(CvDatiPersonali cvDatiPersonali,
			String denominazioneAzienda) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();

		PfPrincipal destinatario = cvDatiPersonali.getPfPrincipal();
		String strMsg;
		strMsg = "L'azienda " + denominazioneAzienda + " ha contattato un lavoratore." + "<br/><a href=\""
				+ BASE_URL
				+ "utente/curriculum/view.xhtml?id=" + cvDatiPersonali.getIdCvDatiPersonali()
				+ "\">Visualizza il suo CV</a>.";
		// la prima notifica è di sistema
		MsgMessaggioDTO msgRedazione = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);
		msgRedazione.setOggetto("Nuova richiesta di contatto");
		msgRedazione.setIdTo(destinatario.getIdPfPrincipal());
		msgRedazione.setCorpo(strMsg);
		ret.add(msgRedazione);

		return ret;
	}
	
	
	public Set<MsgMessaggioDTO> buildNotificationAnnullamentoFirmaMyStage(PfPrincipal pf) {
		Set<MsgMessaggioDTO> ret = new HashSet<MsgMessaggioDTO>();
		MsgMessaggioDTO msgDTO = new MsgMessaggioDTO(ConstantsSingleton.MsgMessaggio.NOTIFICA);

		String messaggio = "A seguito di una modifica dei dati sul sistema tirocini è stata annullata la tua firma al progetto formativo/convenzione. "
				+ "Si prega di verificare che le modifiche siano corrette.<br/>"
				+ "Saluti,<br/>" + ConstantsSingleton.TITLE_APP;
		
		msgDTO.setOggetto("Annullamento firma sistema tirocini");
		msgDTO.setIdTo(pf.getIdPfPrincipal());
		msgDTO.setIdFrom(0);
		msgDTO.setCorpo(messaggio);
		ret.add(msgDTO);

		return ret;
	}
}
