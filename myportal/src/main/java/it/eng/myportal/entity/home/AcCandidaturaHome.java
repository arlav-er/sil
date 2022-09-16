package it.eng.myportal.entity.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.dtos.AcAllegatoDTO;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.AcCandidaturaDatiDTO;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.DeIdoneitaCandidaturaDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VaVisualizzaDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidaturaDati;
import it.eng.myportal.entity.AcCandidaturaValutazione;
import it.eng.myportal.entity.AcCandidatura_;
import it.eng.myportal.entity.AcContatto;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CodValutazioneEnum;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvDatiPersonali_;
import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.CvLetteraAcc_;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.SvAziendaInfo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaDatiVacancy_;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;
import it.eng.myportal.entity.ejb.ClicLavoroCandidaturaEjb;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.NotificationBuilder;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeIdoneitaCandidaturaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.entity.home.nodto.AcCandidaturaValutazioneHome;
import it.eng.myportal.entity.home.nodto.AcContattoHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.exception.StatoOccupazionaleException;
import it.eng.myportal.rest.services.StatoOccupazionale;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 *
 * Classe dei servizi associati alla candidatura di un utente ad una vacancy.
 *
 * @author Rodi A.
 */
@Stateless
public class AcCandidaturaHome extends AbstractUpdatableHome<AcCandidatura, AcCandidaturaDTO> {
	
	@EJB
	protected AcCandidaturaHome acCandidaturaHome;
	
	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	CvLetteraAccHome cvLetteraAccHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	DeIdoneitaCandidaturaHome deIdoneitaCandidaturaHome;

	@EJB
	AcAllegatoHome acAllegatoHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	StatoOccupazionale statoOccupazionale;

	@EJB
	ClicLavoroCandidaturaEjb clicLavoroCandidaturaEjb;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	AcCandidaturaDatiHome acCandidaturaDatiHome;
	
	@EJB
	AcContattoHome acContattoHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	AcCandidaturaValutazioneHome acCandidaturaValutazioneHome;
	
	private AcCandidaturaValutazione acCandidaturaValutazione;
	
	private AcContatto acContatto;
	
	private static final String[] COD_STATO_OCC_PER_CANDIDATURA_CRESCO = { "A21", "A213", "A22", "A223" };

	/**
	 * Restituisce la Entity che su DB ha la chiave passata come parametro.
	 *
	 * @return l'entity presente su DB
	 */
	@Override
	public AcCandidatura findById(Integer id) {
		return findById(AcCandidatura.class, id);
	}

	/**
	 * Questo metodo fa i controlli necessari a candidarsi a un'offerta CRESCO: chiama i due SIL dell'Umbria e chiede lo
	 * stato occupazionale, fa i controlli, e salva i dati nella candidatura.
	 */
	public boolean checkCandidaturaCresco(AcCandidaturaDTO acCandidaturaDTO, Integer idPfPrincipal)
			throws StatoOccupazionaleException {
		boolean wsTerminato = false;
		boolean controlliSuperati = false;
		// Chiamo il WS di Perugia
		try {
			JSONObject statoOccOutputPg = statoOccupazionale.getStatoOccupazionaleInternal(idPfPrincipal, "54");
			wsTerminato = true;
			if (statoOccOutputPg != null) {
				AcCandidaturaDatiDTO candidaturaDatiPg = new AcCandidaturaDatiDTO(statoOccOutputPg);
				candidaturaDatiPg.setIdAcCandidatura(acCandidaturaDTO.getId());
				candidaturaDatiPg.setDeProvenienzaDTO(deProvenienzaHome.findDTOById("SILPG"));
				acCandidaturaDTO.getAcCandidaturaDatiList().add(candidaturaDatiPg);
				controlliSuperati = controlliSuperati || controllaStatoOccupazionaleCresco(candidaturaDatiPg);
			}
		} catch (StatoOccupazionaleException e) {
			// Lascio passare, potrebbe essere nell'altro SIL
		} catch (JSONException e) {
			log.error("Errore durante i controlli per pacchetto CRESCO: " + e.toString());
		} catch (Exception e) {
			log.error("Errore GENERICO durante i controlli per pacchetto CRESCO: " + e.toString());
		}

		// Chiamo il WS di Terni
		try {
			JSONObject statoOccOutputTr = statoOccupazionale.getStatoOccupazionaleInternal(idPfPrincipal, "55");
			wsTerminato = true;
			if (statoOccOutputTr != null) {
				AcCandidaturaDatiDTO candidaturaDatiTr = new AcCandidaturaDatiDTO(statoOccOutputTr);
				candidaturaDatiTr.setIdAcCandidatura(acCandidaturaDTO.getId());
				candidaturaDatiTr.setDeProvenienzaDTO(deProvenienzaHome.findDTOById("SILTR"));
				acCandidaturaDTO.getAcCandidaturaDatiList().add(candidaturaDatiTr);
				controlliSuperati = controlliSuperati || controllaStatoOccupazionaleCresco(candidaturaDatiTr);
			}
		} catch (StatoOccupazionaleException e) {
			// Lascio passare, potrebbe essere nell'altro SIL
		} catch (JSONException e) {
			log.error("Errore durante i controlli per pacchetto CRESCO: " + e.toString());
		} catch (Exception e) {
			log.error("Errore GENERICO durante i controlli per pacchetto CRESCO: " + e.toString());
		}

		if (!wsTerminato) {
			// Se NESSUNO dei due ws è andato a buon fine, lancio un'eccezione.
			throw new StatoOccupazionaleException("Errore durante i controlli CRESCO");
		} else {
			// Se almeno uno dei due ws è andato a buon fine, restituisco il risultato dei controlli.
			return controlliSuperati;
		}
	}

	/**
	 * Controlla che uno stato occupazionale sia giusto per candidarsi a un'offerta CRESCO.
	 */
	private boolean controllaStatoOccupazionaleCresco(AcCandidaturaDatiDTO candidaturaDati) {
		String codStatoOcc = candidaturaDati.getCodStatoOccupazionale();

		// Se sei lavoratore intermittente, puoi sempre candidarti
		if (candidaturaDati.getFlagIntermittente()) {
			return true;
		}

		// Altrimenti, controllo lo stato occupazionale
		if (Arrays.asList(COD_STATO_OCC_PER_CANDIDATURA_CRESCO).contains(codStatoOcc)) {
			return true;
		}

		return false;
	}

	/**
	 * Metodo per inviare una candidatura, cioe' fa la persist dell'entity che rappresenta una candidatura. Overload del
	 * metodo corrispondente con 5 parametri.
	 *
	 * @param acCandidaturaDTO
	 *            DTO della candidatura
	 * @param idPrincipal
	 *            principalId dell'utente che compie l'operazione
	 * @return true se l'operazione e' andata a buon fine, false altrimenti
	 */
	public Boolean inviaCandidatura(AcCandidaturaDTO acCandidaturaDTO, Integer idPrincipal) {
		return inviaCandidatura(acCandidaturaDTO, idPrincipal, (Integer) null, (Integer) null, null);
	}

	/**
	 * Metodo per inviare una candidatura, cioe' fa la persist dell'entity che rappresenta una candidatura.
	 *
	 * @param acCandidaturaDTO
	 *            DTO della candidatura
	 * @param idPrincipal
	 *            principalId dell'utente che compie l'operazione
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda che rappresenta la sede di lavoro (se prevista)
	 * @param idVaDatiVacancy
	 *            id della vacancy associata (se prevista)
	 * @return true se l'operazione e' andata a buon fine, false altrimenti
	 */
	public Boolean inviaCandidatura(AcCandidaturaDTO acCandidaturaDTO, Integer idPrincipal,
			Integer idPfPrincipalAzienda, Integer idVaDatiVacancy, byte[] file) {
		Boolean result = false;

		if (idPfPrincipalAzienda != null) {
			acCandidaturaDTO.setIdPfPrincipalAzienda(idPfPrincipalAzienda);
		}
		if (idVaDatiVacancy != null) {
			acCandidaturaDTO.setIdVaDatiVacancy(idVaDatiVacancy);
		}

		acCandidaturaDTO.setDeIdoneitaCandidaturaDTO(
				deIdoneitaCandidaturaHome.findDTOById(DeIdoneitaCandidaturaHome.NON_ESAMINATO));

		Date now = new Date();
		acCandidaturaDTO.setDtmIns(now);
		acCandidaturaDTO.setDtmMod(now);

		List<AcCandidaturaDatiDTO> candidaturaDatiList = acCandidaturaDTO.getAcCandidaturaDatiList();
		acCandidaturaDTO.setAcCandidaturaDatiList(new ArrayList<AcCandidaturaDatiDTO>(0));
		AcCandidaturaDTO acCandPersisted = persistDTO(acCandidaturaDTO, idPrincipal);

		// gestione AcCandidaturaDati
		for (AcCandidaturaDatiDTO candidaturaDati : candidaturaDatiList) {
			candidaturaDati.setIdAcCandidatura(acCandPersisted.getId());
			acCandidaturaDatiHome.persistDTO(candidaturaDati, idPrincipal);
		}

		// gestione file allegato
		try {
			if (file != null) {
				byte[] contenuto = file;
				AcAllegatoDTO acAllegato = new AcAllegatoDTO();
				acAllegato.setIdAcCandidatura(acCandPersisted.getId());
				acAllegato.setFilename(acCandidaturaDTO.getAllegatoFileName());
				acAllegato.setContenuto(contenuto);
				acAllegatoHome.persistDTO(acAllegato, idPrincipal);
			}
			result = true;
		} catch (Exception e) {
			throw new MyPortalException("candidatura.allegato.error", e);
		}
		return result;
	}

	public AcCandidatura inviaCandidaturaNoDTO(AcCandidaturaDTO acCandidaturaDTO, Integer idPrincipal,
			Integer idPfPrincipalAzienda, Integer idVaDatiVacancy, byte[] file) {
		Boolean result = false;

		if (idPfPrincipalAzienda != null) {
			acCandidaturaDTO.setIdPfPrincipalAzienda(idPfPrincipalAzienda);
		}
		if (idVaDatiVacancy != null) {
			acCandidaturaDTO.setIdVaDatiVacancy(idVaDatiVacancy);
		}

		acCandidaturaDTO.setDeIdoneitaCandidaturaDTO(
				deIdoneitaCandidaturaHome.findDTOById(DeIdoneitaCandidaturaHome.NON_ESAMINATO));

		Date now = new Date();
		acCandidaturaDTO.setDtmIns(now);
		acCandidaturaDTO.setDtmMod(now);

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			acCandidaturaDTO.setCodProvenienzaCandidatura(ConstantsSingleton.DeProvenienza.COD_MYPORTAL);
		}

		List<AcCandidaturaDatiDTO> candidaturaDatiList = acCandidaturaDTO.getAcCandidaturaDatiList();
		acCandidaturaDTO.setAcCandidaturaDatiList(new ArrayList<AcCandidaturaDatiDTO>(0));
		AcCandidaturaDTO acCandPersisted = persistDTO(acCandidaturaDTO, idPrincipal);

		// gestione AcCandidaturaDati
		for (AcCandidaturaDatiDTO candidaturaDati : candidaturaDatiList) {
			candidaturaDati.setIdAcCandidatura(acCandPersisted.getId());
			acCandidaturaDatiHome.persistDTO(candidaturaDati, idPrincipal);
		}

		// gestione file allegato
		try {
			if (file != null) {
				byte[] contenuto = file;
				AcAllegatoDTO acAllegato = new AcAllegatoDTO();
				acAllegato.setIdAcCandidatura(acCandPersisted.getId());
				acAllegato.setFilename(acCandidaturaDTO.getAllegatoFileName());
				acAllegato.setContenuto(contenuto);
				acAllegatoHome.persistDTO(acAllegato, idPrincipal);
			}

		} catch (Exception e) {
			throw new MyPortalException("candidatura.allegato.error", e);
		}
		return fromDTO(acCandPersisted);
	}

	/**
	 *
	 * @param acCandidaturaDTO
	 * @param idPrincipal
	 * @param idPfPrincipalAzienda
	 * @param idVaDatiVacancy
	 * @param file
	 * @return Boolean Questo metodo è una copia di inviaCandidatura, ma con una modifica sul tipo del parametro di
	 *         ingresso "file". Il metodo è stato sviluppato durante la migrazione in Primefaces
	 */
	public Boolean inviaAutoCandidatura(AcCandidaturaDTO acCandidaturaDTO, Integer idPrincipal,
			Integer idPfPrincipalAzienda, Integer idVaDatiVacancy, UploadedFile file) {
		Boolean result = false;

		if (idPfPrincipalAzienda != null) {
			acCandidaturaDTO.setIdPfPrincipalAzienda(idPfPrincipalAzienda);
		}
		if (idVaDatiVacancy != null) {
			acCandidaturaDTO.setIdVaDatiVacancy(idVaDatiVacancy);
		}

		acCandidaturaDTO.setDeIdoneitaCandidaturaDTO(
				deIdoneitaCandidaturaHome.findDTOById(DeIdoneitaCandidaturaHome.NON_ESAMINATO));

		Date now = new Date();
		acCandidaturaDTO.setDtmIns(now);
		acCandidaturaDTO.setDtmMod(now);

		List<AcCandidaturaDatiDTO> candidaturaDatiList = acCandidaturaDTO.getAcCandidaturaDatiList();
		acCandidaturaDTO.setAcCandidaturaDatiList(new ArrayList<AcCandidaturaDatiDTO>(0));
		AcCandidaturaDTO acCandPersisted = persistDTO(acCandidaturaDTO, idPrincipal);

		// gestione AcCandidaturaDati
		for (AcCandidaturaDatiDTO candidaturaDati : candidaturaDatiList) {
			candidaturaDati.setIdAcCandidatura(acCandPersisted.getId());
			acCandidaturaDatiHome.persistDTO(candidaturaDati, idPrincipal);
		}

		// gestione file allegato
		try {
			if (file != null) {
				AcAllegatoDTO acAllegato = new AcAllegatoDTO();
				acAllegato.setIdAcCandidatura(acCandPersisted.getId());
				acAllegato.setFilename(file.getFileName());
				acAllegato.setContenuto(file.getContents());
				acAllegatoHome.persistDTO(acAllegato, idPrincipal);
			}
			result = true;
		} catch (Exception e) {
			throw new MyPortalException("candidatura.allegato.error", e);
		}
		return result;
	}

	public Boolean inviaAutoCandidaturaRER(AcCandidaturaDTO acCandidaturaDTO, Integer idPrincipal,
			Integer idPfPrincipalAzienda, Integer idVaDatiVacancy, UploadedFile file) {
		Boolean result = false;

		if (idPfPrincipalAzienda != null) {
			acCandidaturaDTO.setIdPfPrincipalAzienda(idPfPrincipalAzienda);
		}
		if (idVaDatiVacancy != null) {
			acCandidaturaDTO.setIdVaDatiVacancy(idVaDatiVacancy);
		}

		acCandidaturaDTO.setDeIdoneitaCandidaturaDTO(
				deIdoneitaCandidaturaHome.findDTOById(DeIdoneitaCandidaturaHome.NON_ESAMINATO));

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			acCandidaturaDTO.setCodProvenienzaCandidatura(ConstantsSingleton.DeProvenienza.COD_MYPORTAL);
		}

		Date now = new Date();
		acCandidaturaDTO.setDtmIns(now);
		acCandidaturaDTO.setDtmMod(now);

		List<AcCandidaturaDatiDTO> candidaturaDatiList = acCandidaturaDTO.getAcCandidaturaDatiList();
		acCandidaturaDTO.setAcCandidaturaDatiList(new ArrayList<AcCandidaturaDatiDTO>(0));
		AcCandidaturaDTO acCandPersisted = persistDTO(acCandidaturaDTO, idPrincipal);

		// gestione AcCandidaturaDati
		for (AcCandidaturaDatiDTO candidaturaDati : candidaturaDatiList) {
			candidaturaDati.setIdAcCandidatura(acCandPersisted.getId());
			acCandidaturaDatiHome.persistDTO(candidaturaDati, idPrincipal);
		}

		// gestione file allegato
		try {
			if (file != null) {
				AcAllegatoDTO acAllegato = new AcAllegatoDTO();
				acAllegato.setIdAcCandidatura(acCandPersisted.getId());
				acAllegato.setFilename(file.getFileName());
				acAllegato.setContenuto(file.getContents());
				acAllegatoHome.persistDTO(acAllegato, idPrincipal);
			}
			result = true;
		} catch (Exception e) {
			throw new MyPortalException("candidatura.allegato.error", e);
		}
		return result;
	}

	@Override
	public AcCandidaturaDTO toDTO(AcCandidatura entity) {
		if (entity == null)
			return null;
		AcCandidaturaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdAcCandidatura());

		CvDatiPersonali cvDatiPersonali = entity.getCvDatiPersonali();
		if (cvDatiPersonali != null) {
			dto.setIdCvDatiPersonali(cvDatiPersonali.getIdCvDatiPersonali());
		}

		CvLetteraAcc lettera = entity.getCvLetteraAcc();
		if (lettera != null) {
			dto.setIdCvLetteraAcc(lettera.getIdCvLetteraAcc());
		}

		VaDatiVacancy vacancy = entity.getVaDatiVacancy();
		Integer idPfPrincipal = null;
		//campo nuovo
		Integer idPfPrincipalPalese = null;
		if (vacancy != null) {
			dto.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
			idPfPrincipal = vacancy.getPfPrincipal().getIdPfPrincipal();

			//Nuovo Campo ID Azienda Palese
			if (vacancy.getPfPrincipalPalese() != null){
				dto.setIdPfPrincipalAziendaPalese(vacancy.getPfPrincipalPalese().getIdPfPrincipal());
				idPfPrincipalPalese = vacancy.getPfPrincipalPalese().getIdPfPrincipal();
			}
		} else {
			idPfPrincipal = entity.getPfPrincipal().getIdPfPrincipal();
		}
		dto.setIdPfPrincipalAzienda(idPfPrincipal);
		//Nuovo Campo ID Azienda Palese
		dto.setIdPfPrincipalAziendaPalese(idPfPrincipalPalese);


		DeIdoneitaCandidatura deIdoneitaCandidatura = entity.getDeIdoneitaCandidatura();
		if (deIdoneitaCandidatura != null) {
			dto.setDeIdoneitaCandidaturaDTO(deIdoneitaCandidaturaHome.toDTO(deIdoneitaCandidatura));
		} else {
			dto.setDeIdoneitaCandidaturaDTO(
					deIdoneitaCandidaturaHome.findDTOById(ConstantsSingleton.DeIdoneitaCandidatura.NON_ESAMINATO));
		}

		List<AcAllegatoDTO> l = acAllegatoHome.findDTOByAcCandidaturaId(dto.getId());
		if (l != null && !l.isEmpty()) {
			AcAllegatoDTO cva = l.get(0);
			dto.setIdAcAllegato(cva.getId());
			dto.setAllegatoFileName(cva.getFilename());
		}

		dto.setCommento(entity.getCommento());
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			try {
				acContatto = acContattoHome.findAcContattoByCvId(entity.getCvDatiPersonali().getIdCvDatiPersonali());
				if(acContatto != null && acContatto.getDataContatto() != null) {
				  dto.setDataContatto(acContatto.getDataContatto());
				}
			} catch (MyPortalNoResultFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace(); // corrisponde a nessun risultato
			}
			dto.setCodProvenienzaCandidatura(entity.getCodProvenienzaCandidatura());
		}
		if (entity.getAcCandidaturaDatiList() != null && !entity.getAcCandidaturaDatiList().isEmpty()) {
			for (AcCandidaturaDati acCandidaturaDati : entity.getAcCandidaturaDatiList()) {
				dto.getAcCandidaturaDatiList().add(acCandidaturaDatiHome.toDTO(acCandidaturaDati));
			}
		}

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			try {
				acCandidaturaValutazione = acCandidaturaValutazioneHome.findByAcCandidaturaId(dto.getId());
				if(acCandidaturaValutazione != null && acCandidaturaValutazione.getDtmIns() != null) {
					//aggiungere il set della valutazione complessiva
					dto.setCodValutazioneAcCandidatura(acCandidaturaValutazione.getValutazioneComplessiva());
				}
				
			} catch (MyPortalNoResultFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace(); // corrisponde a nessun risultato
			}		
		}
		
		return dto;
	}

	public int checkNumberOrderedVacancyToCandidate(Integer vacancyId, Integer acCandidaturaId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		for (int j = 0; j < listcandidature.size(); j++) {
			if(listcandidature.get(j).getIdAcCandidatura() == acCandidaturaId.intValue()) {
				i = j;
				break;
			}
		}
		return ++i;
	}
	
	public AcVisualizzaCandidaturaDTO toVisualizzaDTO(AcCandidatura entity) {
		AcVisualizzaCandidaturaDTO visualizzaDTO = null;
		int numCandidatura = 0;
		if (entity != null) {
			AcCandidaturaDTO dto = toDTO(entity);
			visualizzaDTO = new AcVisualizzaCandidaturaDTO();
			if(entity.getDeIdoneitaCandidatura() != null) {
			  visualizzaDTO.setCodIdoneitaCandidatura(entity.getDeIdoneitaCandidatura().getCodIdoneitaCandidatura());
			}
			visualizzaDTO.setId(dto.getId());
			if(entity.getCvDatiPersonali().getFlagIdo()== null) {
				entity.getCvDatiPersonali().setFlagIdo(ConstantsSingleton.App.NUOVO_IDO);
			}
			
			visualizzaDTO.setFlagIdo(entity.getCvDatiPersonali().getFlagIdo());
			if (UtilsBean.isStaticRER()) {
				if(entity.getVaDatiVacancy() != null) {
      			  visualizzaDTO.setFlagIdoVA(entity.getVaDatiVacancy().getFlagIdo());
				}
			}
			visualizzaDTO.setIdCvDatiPersonali(dto.getIdCvDatiPersonali());
			visualizzaDTO.setIdPfPrincipalAzienda(dto.getIdPfPrincipalAzienda());
			visualizzaDTO.setIdVaDatiVacancy(dto.getIdVaDatiVacancy());
			visualizzaDTO.setIdCvLetteraAcc(dto.getIdCvLetteraAcc());
			visualizzaDTO.setDeIdoneitaCandidaturaDTO(dto.getDeIdoneitaCandidaturaDTO());
			visualizzaDTO.setIdAcAllegato(dto.getIdAcAllegato());
			visualizzaDTO.setAllegatoFileName(dto.getAllegatoFileName());
			visualizzaDTO.setCommento(dto.getCommento());
			//Controllo Campo ID Azienda Palese
			if(dto.getIdPfPrincipalAziendaPalese() != null){
				visualizzaDTO.setIdPfPrincipalPalese(dto.getIdPfPrincipalAziendaPalese());
			}

			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			    visualizzaDTO.setDataContatto(dto.getDataContatto());
			}
			// Inserisco i CandidaturaDati
			visualizzaDTO.setAcCandidaturaDatiList(dto.getAcCandidaturaDatiList());

			
			
			// Genero la descrizione dello stato occupazionale e del flag intermittente concatenando quelle che mi sono
			// arrivate dal SIL, se sono più di una
			String descStatoOccupazionale = "";
			String descFlgIntermittente = "";
			for (AcCandidaturaDatiDTO candidaturaDati : dto.getAcCandidaturaDatiList()) {
				descStatoOccupazionale += candidaturaDati.getCodStatoOccupazionale();
				descFlgIntermittente += (candidaturaDati.getFlagIntermittente() != null
						&& candidaturaDati.getFlagIntermittente()) ? "Sì" : "No";

				if (candidaturaDati.getDeProvenienzaDTO() != null) {
					String provinciaProvenienza = candidaturaDati.getDeProvenienzaDTO().getId();
					if (provinciaProvenienza != null && provinciaProvenienza.length() > 3) {
						provinciaProvenienza = provinciaProvenienza.substring(3);
					}

					descStatoOccupazionale += "(" + provinciaProvenienza + ")";
					descFlgIntermittente += "(" + provinciaProvenienza + ")";
				}
				descStatoOccupazionale += "<br />";
				descFlgIntermittente += "<br />";
			}

			// Tolgo l'ultima barretta
			if (descStatoOccupazionale.length() > 5) {
				descStatoOccupazionale = descStatoOccupazionale.substring(0, descStatoOccupazionale.length() - 6);
			}
			if (descFlgIntermittente.length() > 5) {
				descFlgIntermittente = descFlgIntermittente.substring(0, descFlgIntermittente.length() - 6);
			}

			visualizzaDTO.setStatoOccupazionale(descStatoOccupazionale);
			visualizzaDTO.setFlgIntermittente(descFlgIntermittente);

			visualizzaDTO.setDtmMod(dto.getDtmMod());
			visualizzaDTO.setDtmIns(dto.getDtmIns());
			visualizzaDTO.setIdPrincipalIns(dto.getIdPrincipalIns());
			visualizzaDTO.setIdPrincipalMod(dto.getIdPrincipalMod());

			//SET Campo ID Azienda Palese
			visualizzaDTO.setIdPfPrincipalAziendaPalese(dto.getIdPfPrincipalAziendaPalese());

			Integer idCvDatiPersonali = dto.getIdCvDatiPersonali();
			Integer idCvLetteraAcc = dto.getIdCvLetteraAcc();
			Integer idAziendaInfo = dto.getIdPfPrincipalAzienda();

			//Nuovo Campo ID Azienda Palese
			Integer idAziendaInfoPalese = dto.getIdPfPrincipalAziendaPalese();

			Integer idVaDatiVacancy = dto.getIdVaDatiVacancy();
			CodValutazioneEnum codValutazioneAcCandidatura= dto.getCodValutazioneAcCandidatura(); 

			if (idCvDatiPersonali != null) {
				CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(idCvDatiPersonali);

				if (cvDatiPersonali.getPfPrincipalPalese() != null) {
					visualizzaDTO.setNomeCandidato(cvDatiPersonali.getPfPrincipalPalese().getNome());
					visualizzaDTO.setCognomeCandidato(cvDatiPersonali.getPfPrincipalPalese().getCognome());
				} else {
					visualizzaDTO.setNomeCandidato(cvDatiPersonali.getPfPrincipal().getNome());
					visualizzaDTO.setCognomeCandidato(cvDatiPersonali.getPfPrincipal().getCognome());

				}

				String descrizioneCurriculum = cvDatiPersonali.getDescrizione();
				if (descrizioneCurriculum != null && descrizioneCurriculum.length() > 15) {
					descrizioneCurriculum = descrizioneCurriculum.substring(0, 14) + "...";
				}
				visualizzaDTO.setDescrizioneCurriculum(descrizioneCurriculum);
				visualizzaDTO.setCodiceFiscaleCandicato(cvDatiPersonali.getCodiceFiscale());
			}

			if (idCvLetteraAcc != null) {
				visualizzaDTO.setNomeLetteraAcc(cvLetteraAccHome.findById(idCvLetteraAcc).getNome());
			}

			if (idAziendaInfo != null) {
				AziendaInfo aziendaInfo = aziendaInfoHome.findById(idAziendaInfo);
				visualizzaDTO.setRagioneSocialeAz(aziendaInfo.getRagioneSociale());
				SvAziendaInfo svAziendaInfo = aziendaInfo.getPfPrincipal().getSvAziendaInfo();
				if (svAziendaInfo != null) {
					visualizzaDTO.setIdVetrina(svAziendaInfo.getIdPfPrincipal());
				}
			}
			//controllo Azienda Palese
			if(idAziendaInfoPalese != null){
				AziendaInfo aziendaInfoPalese = aziendaInfoHome.findById(idAziendaInfoPalese);
				visualizzaDTO.setRagioneSocialePalese(aziendaInfoPalese.getRagioneSociale());
				SvAziendaInfo svAziendaInfoPalese = aziendaInfoPalese.getPfPrincipal().getSvAziendaInfo();
				if (svAziendaInfoPalese != null) {
					visualizzaDTO.setIdVetrinaPalese(svAziendaInfoPalese.getIdPfPrincipal());
				}
			}

			if (idVaDatiVacancy != null) {
				VaDatiVacancy vacancy = vaDatiVacancyHome.findById(idVaDatiVacancy);
				visualizzaDTO.setDescrizioneVacancy(vacancy.getAttivitaPrincipale());
				visualizzaDTO.setFlagCresco(vacancy.getFlgPacchettoCresco());
			}
			
			/* settare numero candidatura */
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				if (idVaDatiVacancy != null) {
					VaDatiVacancy vacancy = vaDatiVacancyHome.findById(idVaDatiVacancy);
					numCandidatura =  checkNumberOrderedVacancyToCandidate(vacancy.getIdVaDatiVacancy(),entity.getIdAcCandidatura());
					visualizzaDTO.setNumCandidatura(numCandidatura);
				}
			}
			
			
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				/* aggiungere il set della valutazione complessiva */
				if(codValutazioneAcCandidatura != null) {
					visualizzaDTO.setCodValutazioneAcCandidatura(codValutazioneAcCandidatura);
					switch (codValutazioneAcCandidatura.getValue()) {
					case "L0":
		    			visualizzaDTO.setCodValutazioneAcCandidaturaDescr("Non idoneo");
		    			break;
					case "L1":
		    			visualizzaDTO.setCodValutazioneAcCandidaturaDescr("1");
		    			break;
		    		case "L2":
		    			visualizzaDTO.setCodValutazioneAcCandidaturaDescr("2");
		    			break;
		    		case "L3":
		    			visualizzaDTO.setCodValutazioneAcCandidaturaDescr("3");
		    			break;
		    		case "L4":
		    			visualizzaDTO.setCodValutazioneAcCandidaturaDescr("4");
		    			break;
		    		default:
		    			break;
		    		}
				}
			}

		} else {
			log.warn("Entity AcCandidatura null");
		}

		return visualizzaDTO;
	}

	@Override
	public AcCandidatura fromDTO(AcCandidaturaDTO dto) {
		if (dto == null)
			return null;
		AcCandidatura entity = super.fromDTO(dto);

		entity.setIdAcCandidatura(dto.getId());

		Integer idPfPrincipal = dto.getIdPfPrincipalAzienda();
		if (idPfPrincipal != null) {
			entity.setPfPrincipal(pfPrincipalHome.findById(idPfPrincipal));
		}

		Integer idCv = dto.getIdCvDatiPersonali();
		if (idCv != null) {
			entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(idCv));
		}

		Integer idLettera = dto.getIdCvLetteraAcc();
		if (idLettera != null) {
			entity.setCvLetteraAcc(cvLetteraAccHome.findById(dto.getIdCvLetteraAcc()));
		}

		Integer idVacancy = dto.getIdVaDatiVacancy();
		if (idVacancy != null) {
			entity.setVaDatiVacancy(vaDatiVacancyHome.findById(idVacancy));
		}

		DeIdoneitaCandidaturaDTO deIdoneitaCandidaturaDTO = dto.getDeIdoneitaCandidaturaDTO();
		if (deIdoneitaCandidaturaDTO != null) {
			entity.setDeIdoneitaCandidatura(deIdoneitaCandidaturaHome.findById(deIdoneitaCandidaturaDTO.getId()));
		} else {
			entity.setDeIdoneitaCandidatura(
					deIdoneitaCandidaturaHome.findById(ConstantsSingleton.DeIdoneitaCandidatura.NON_ESAMINATO));
		}
		entity.setCommento(dto.getCommento());
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			entity.setCodProvenienzaCandidatura(dto.getCodProvenienzaCandidatura());
		}
		if (dto.getAcCandidaturaDatiList() != null && !dto.getAcCandidaturaDatiList().isEmpty()) {
			for (AcCandidaturaDatiDTO acCandidaturaDatiDTO : dto.getAcCandidaturaDatiList()) {
				entity.getAcCandidaturaDatiList().add(acCandidaturaDatiHome.fromDTO(acCandidaturaDatiDTO));
			}
		}

		return entity;
	}

	/**
	 * Cerca e verifica la candidatura di un utente verso un'azienda o per una vacancy.
	 *
	 * @param idUtente
	 *            id dell'utente che ha effettuato la candidatura.
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda alla quale ha inviato un'autocandidatura.
	 * @param idVaDatiVacancy
	 *            id dell'offerta di lavoro alla quale ci si è candidati.
	 *
	 *            I parametri idAzienda e idVacancy sono mutualmente esclusivi. Se se imposta idAzienda verrà effettuata
	 *            la ricerca di una autocandidatura, se si imposta idVacancy verrà ricercata una semplice candidatura,
	 *            se vengono, per errore, passati entrambi, cerca una candidatura.
	 * @return Un DTO con i dati della candidatura. null nel caso l'utente non abbia effettuato alcuna candidatura.
	 */
	public AcCandidaturaDTO findDTOByUtenteAndAziendaIdPfPrincipal(Integer idPfPrincipal, Integer idPfPrincipalAzienda,
			Integer idVaDatiVacancy) {
		List<AcCandidatura> list = retrieveListaCandidature(idPfPrincipal, idPfPrincipalAzienda, idVaDatiVacancy);

		AcCandidaturaDTO ret = null;

		log.debug("Ho recuperato " + list.size() + " candidature per l'utente " + idPfPrincipal);

		if (list.size() > 0) {
			ret = toDTO(list.iterator().next());
		}

		return ret;
	}

	public List<AcCandidatura> findByUtenteAndAziendaIdPfPrincipal(Integer idPfPrincipal, Integer idPfPrincipalAzienda,
			Integer idVaDatiVacancy) {
		List<AcCandidatura> list = retrieveListaCandidature(idPfPrincipal, idPfPrincipalAzienda, idVaDatiVacancy);

		return list;
	}

	
	/**
	 * Verifica la candidatura di un utente verso un'azienda o per una vacancy. 
	 * 2020-02-19: Modificato il ritorno da boolean a lista delle candidature
	 *
	 * @param idPfPrincipal
	 *            id dell'utente che ha effettuato la candidatura.
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda alla quale ha inviato un'autocandidatura.
	 * @param idVaDatiVacancy
	 *            id dell'offerta di lavoro alla quale ci si è candidati.
	 * @return
	 */
	public List<AcCandidatura> checkCandidaturaUtente(Integer idPfPrincipal, Integer idPfPrincipalAzienda, Integer idVaDatiVacancy) {
	
		List<AcCandidatura> list = retrieveListaCandidature(idPfPrincipal, idPfPrincipalAzienda, idVaDatiVacancy);

		log.debug("Ho recuperato " + list.size() + " candidature per l'utente " + idPfPrincipal);

		return list;
	}

	/**
	 * Cerca e verifica la candidatura di un utente verso un'azienda o per una vacancy.
	 *
	 * @param idUtente
	 *            id dell'utente che ha effettuato la candidatura.
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda alla quale ha inviato un'autocandidatura.
	 * @param idVaDatiVacancy
	 *            id dell'offerta di lavoro alla quale ci si è candidati.
	 *
	 *            I parametri idAzienda e idVacancy sono mutualmente esclusivi. Se se imposta idAzienda verrà effettuata
	 *            la ricerca di una autocandidatura, se si imposta idVacancy verrà ricercata una semplice candidatura,
	 *            se vengono, per errore, passati entrambi, cerca una candidatura.
	 * @return elenco delle AcCandidatura con i dati della candidatura. null nel caso l'utente non abbia effettuato
	 *         alcuna candidatura.
	 */
	private List<AcCandidatura> retrieveListaCandidature(Integer idPfPrincipal, Integer idPfPrincipalAzienda,
			Integer idVaDatiVacancy) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> query = qb.createQuery(AcCandidatura.class);
		Root<AcCandidatura> acCandidatura = query.from(AcCandidatura.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		if (idPfPrincipal != null) {
			/*
			 * prendo tutti i CV, anche quelli intermediati, l'idPfPrincipal dell'utente puo' essere sia in pfPrincipal
			 * che in pfPrincipalPalese di CvDatiPersonali
			 */
			Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = acCandidatura.join(AcCandidatura_.cvDatiPersonali);
			Predicate pfPrincipalPredicate = qb.equal(cvDatiPersonali.get(CvDatiPersonali_.pfPrincipal), idPfPrincipal);
			Predicate pfPrincipalPalesePredicate = qb.equal(cvDatiPersonali.get(CvDatiPersonali_.pfPrincipalPalese),
					idPfPrincipal);
			whereConditions.add(qb.or(pfPrincipalPredicate, pfPrincipalPalesePredicate));
		}
		if (idVaDatiVacancy != null) {
			Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy);
			whereConditions.add(qb.equal(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy), idVaDatiVacancy));
		} else if (idPfPrincipalAzienda != null) {
			Join<AcCandidatura, PfPrincipal> pfPrincipalAzienda = acCandidatura.join(AcCandidatura_.pfPrincipal);
			whereConditions.add(qb.equal(pfPrincipalAzienda.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda));
		}
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<AcCandidatura> q = entityManager.createQuery(query);
		List<AcCandidatura> list = q.getResultList();
		return list;
	}

	/**
	 * Cerca SOLO le autocandidature effettuate dall'utente passato verso l'azenda passata
	 *
	 * @param idUtente
	 *            id dell'utente di cui vogliamo cercare l'autocandidatura
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda verso la quale vogliamo cercare l'autocandidatura
	 * @return
	 */
	public AcCandidaturaDTO findDTOByUtenteAndAziendaIdPfPrincipalAutocandidature(Integer idPfPrincipal,
			Integer idPfPrincipalAzienda) {
		AcCandidaturaDTO ret = null;
		try {
			CriteriaBuilder qb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AcCandidatura> query = qb.createQuery(AcCandidatura.class);
			Root<AcCandidatura> acCandidatura = query.from(AcCandidatura.class);
			List<Predicate> whereConditions = new ArrayList<Predicate>();

			if (idPfPrincipal != null) {
				Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = acCandidatura
						.join(AcCandidatura_.cvDatiPersonali);
				Join<CvDatiPersonali, PfPrincipal> pfPrincipal = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipal);
				whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));
			}
			if (idPfPrincipalAzienda != null) {
				Join<AcCandidatura, PfPrincipal> pfPrincipalAzienda = acCandidatura.join(AcCandidatura_.pfPrincipal);
				whereConditions.add(qb.equal(pfPrincipalAzienda.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda));
			}

			// trovo le autocandidature all'azienda
			whereConditions.add(qb.isNull(acCandidatura.get(AcCandidatura_.vaDatiVacancy)));

			query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
			TypedQuery<AcCandidatura> q = entityManager.createQuery(query);

			ret = toDTO(q.getSingleResult());
		} catch (NoResultException nre) {
		}
		return ret;
	}

	/**
	 * Restituisce le candidature appartenenti all'utente il cui id viene passato come parametro. La lista delle
	 * candidature viene restituita ordinata in maniera decrescente per data di ultima modifica.
	 *
	 * @param idPfPrincipal
	 *            id dell'utente di cui si vogliono ottenere le candidature
	 * @return Una lista contente i DTO delle candidature, se ce ne sono, una lista vuota altrimenti.
	 */
	public List<AcVisualizzaCandidaturaDTO> findDtosByIdPfPrincipal(int idPfPrincipal) {
		return findDtosByIdPfPrincipal(idPfPrincipal, 0);
	}

	public List<AcVisualizzaCandidaturaDTO> findDtosByIdPfPrincipalAndFlgCresco(int idPfPrincipal, boolean flgCresco) {
		return findDtosByIdPfPrincipal(idPfPrincipal, 0, flgCresco);
	}

	/**
	 * Restituisce le candidature appartenenti all'utente il cui id viene passato come parametro. La lista delle
	 * candidature viene restituita ordinata in maniera decrescente per data di ultima modifica. Il numero di risultati
	 * restituiti viene regolato dal parametro maxResult se maggiore di 0, altrimenti vengono restituite tutte le
	 * candidature
	 *
	 * @param userId
	 *            id dell'utente di cui si vogliono ottenere le candidature
	 * @param maxResults
	 *            numero massimo di risultati da restituire. Se il valore passato e' minore o uguale a 0 allora vengono
	 *            restituite tutte le candidature
	 * @return Una lista contente i DTO delle candidature, se ce ne sono, una lista vuota altrimenti.
	 */
	public List<AcVisualizzaCandidaturaDTO> findDtosByIdPfPrincipal(int idPfPrincipal, int maxResults,
			boolean flgCresco) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> query = cb.createQuery(AcCandidatura.class);

		Root<AcCandidatura> from = query.from(AcCandidatura.class);
		Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = from.join(AcCandidatura_.cvDatiPersonali);
		Join<AcCandidatura, VaDatiVacancy> joinVaDatiVacancy = from.join(AcCandidatura_.vaDatiVacancy);

		/*
		 * prendo tutti i cv dell'utente, anche quelli intermediati, il cittadino deve essere indicato come pfPrincipal
		 * o come pfPrincipalPalese in CvDatiPersonali
		 */
		Predicate equalIdUtente = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.pfPrincipal), idPfPrincipal);
		Predicate equalIdUtentePalese = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.pfPrincipalPalese),
				idPfPrincipal);
		Predicate idUtentePredicate = cb.or(equalIdUtente, equalIdUtentePalese);

		// Filtro in base al flgCresco
		Predicate flgCrescoPredicate = cb.equal(joinVaDatiVacancy.get(VaDatiVacancy_.flgPacchettoCresco), flgCresco);

		query.where(cb.and(idUtentePredicate, flgCrescoPredicate));
		query.orderBy(cb.desc(from.get(AcCandidatura_.dtmMod)));
		TypedQuery<AcCandidatura> typedQuery = entityManager.createQuery(query);

		if (maxResults > 0) {
			typedQuery.setMaxResults(maxResults);
		}

		List<AcCandidatura> list = typedQuery.getResultList();
		List<AcVisualizzaCandidaturaDTO> listDto = new ArrayList<AcVisualizzaCandidaturaDTO>();
		for (AcCandidatura acCandidatura : list) {
			listDto.add(toVisualizzaDTO(acCandidatura));
		}

		log.debug("L'utente ha " + listDto.size() + " candidature");
		return listDto;
	}

	/**
	 * Se non ho il flgCresco, do per scontato che sto cercando le candidature che NON appartengono ad offerte Cresco.
	 */
	public List<AcVisualizzaCandidaturaDTO> findDtosByIdPfPrincipal(int idPfPrincipal, int maxResults) {
		return findDtosByIdPfPrincipal(idPfPrincipal, maxResults, false);
	}

	/**
	 * Restituisce tutte le candidature fatte all'azienda (AziendaInfo) passata come id in cui compare il CV passato
	 * come id.
	 *
	 * @param idCvDatiPersonali
	 *            id del curriculum
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda
	 * @return
	 */
	public List<AcCandidatura> findAllByIdCvDatiPersonaliIdAziendaInfo(Integer idCvDatiPersonali,
			Integer idPfPrincipalAzienda) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> query = cb.createQuery(AcCandidatura.class);

		Root<AcCandidatura> from = query.from(AcCandidatura.class);
		Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = from.join(AcCandidatura_.cvDatiPersonali);
		Join<AcCandidatura, PfPrincipal> pfPrincipalAzienda = from.join(AcCandidatura_.pfPrincipal);

		Predicate equalIdCvDatiPersonali = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				idCvDatiPersonali);
		Predicate equalIdAziendaInfo = cb.equal(pfPrincipalAzienda.get(PfPrincipal_.idPfPrincipal),
				idPfPrincipalAzienda);
		Predicate whereClause = cb.and(equalIdCvDatiPersonali, equalIdAziendaInfo);

		query.where(whereClause);
		TypedQuery<AcCandidatura> typedQuery = entityManager.createQuery(query);
		List<AcCandidatura> list = typedQuery.getResultList();

		return list;
	}

	/**
	 * Restituisce tutte le candidature fatte all'azienda (AziendaInfo) passata come id in cui compare la lettera di
	 * presentazione passata come id.
	 *
	 * @param idCvLetteraAcc
	 *            id della lettera
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda
	 * @return
	 */
	public List<AcCandidatura> findAllByIdCvLetteraAccIdAziendaInfo(Integer idCvLetteraAcc,
			Integer idPfPrincipalAzienda) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> query = cb.createQuery(AcCandidatura.class);

		Root<AcCandidatura> from = query.from(AcCandidatura.class);
		Join<AcCandidatura, CvLetteraAcc> cvLetteraAcc = from.join(AcCandidatura_.cvLetteraAcc);
		Join<AcCandidatura, PfPrincipal> pfPrincipalAzienda = from.join(AcCandidatura_.pfPrincipal);

		Predicate equalIdCvLetteraAcc = cb.equal(cvLetteraAcc.get(CvLetteraAcc_.idCvLetteraAcc), idCvLetteraAcc);
		Predicate equalIdAziendaInfo = cb.equal(pfPrincipalAzienda.get(PfPrincipal_.idPfPrincipal),
				idPfPrincipalAzienda);
		Predicate whereClause = cb.and(equalIdCvLetteraAcc, equalIdAziendaInfo);

		query.where(whereClause);
		TypedQuery<AcCandidatura> typedQuery = entityManager.createQuery(query);
		List<AcCandidatura> list = typedQuery.getResultList();

		return list;
	}

	/**
	 * Restituiscee tutte le candidature di un'azienda, autocandidature, candidature dirette palesi e anonime.
	 *
	 * @param idPfPrincipalAzienda
	 * @return
	 */
	public List<AcVisualizzaCandidaturaDTO> findDtosByIdPfPrincipalAzienda(Integer idPfPrincipalAzienda) {
		PfPrincipal pfPrincipalAzienda = pfPrincipalHome.findById(idPfPrincipalAzienda);
		List<AcVisualizzaCandidaturaDTO> list = new ArrayList<AcVisualizzaCandidaturaDTO>();
		List<AcCandidatura> enListNewVersion = null;
		Set<AcCandidatura> enList = null;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) { // per gestire ordinamento desc
			enListNewVersion = acCandidaturaHome.findCandidaturesById(idPfPrincipalAzienda);
			for (AcCandidatura acCandidatura : enListNewVersion) {
				list.add(toVisualizzaDTO(acCandidatura));
			}
		} else {
			enList = pfPrincipalAzienda.getAcCandidaturas();
			for (AcCandidatura acCandidatura : enList) {
				list.add(toVisualizzaDTO(acCandidatura));
			}
		}
		
		return list;
	}
	
	public List<AcVisualizzaCandidaturaDTO> findAutocandidatureDtosByIdPfPrincipalAzienda(Integer idPfPrincipalAzienda) {
		PfPrincipal pfPrincipalAzienda = pfPrincipalHome.findById(idPfPrincipalAzienda);
		List<AcVisualizzaCandidaturaDTO> list = new ArrayList<AcVisualizzaCandidaturaDTO>();
		List<AcCandidatura> enListNewVersion = null;
		enListNewVersion = acCandidaturaHome.findAutoCandidaturesById(idPfPrincipalAzienda);
		for (AcCandidatura acCandidatura : enListNewVersion) {
			list.add(toVisualizzaDTO(acCandidatura));
		}
		return list;
	}
	
	

	public List<AcCandidatura> findCandidaturesById(Integer idPfPrincipal){
		List<AcCandidatura> candList = entityManager.createNamedQuery("findCandidaturesById", AcCandidatura.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();		
		return candList;
	}
	
	public List<AcCandidatura> findAutoCandidaturesById(Integer idPfPrincipal){
		List<AcCandidatura> candList = entityManager.createNamedQuery("findAutoCandidaturesById", AcCandidatura.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();		
		return candList;
	}

	/**
	 * Restituisce la lista di tutte le autocandidature inviate all'azienda passata in input
	 *
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda
	 * @return
	 */
	public List<AcVisualizzaCandidaturaDTO> findDtosAutocandidatureByIdPfPrincipalAzienda(
			Integer idPfPrincipalAzienda) {
		List<AcVisualizzaCandidaturaDTO> list = findAutocandidatureDtosByIdPfPrincipalAzienda(idPfPrincipalAzienda);
		return list;
	}

	public Long getNumAcCandidaturaByIdVacancy(int vacacyId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<AcCandidatura> f = c.from(AcCandidatura.class);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = f.join(AcCandidatura_.vaDatiVacancy);
		Predicate equalPredicate = cb.equal(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy), vacacyId);
		c = c.select(cb.count(f));
		c = c.where(equalPredicate);
		Long singleResult = entityManager.createQuery(c).getSingleResult();
		long count = singleResult.longValue();
		return count;
	}

	public List<AcVisualizzaCandidaturaDTO> findDtosByVacancyId(int vacacyId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> c = cb.createQuery(AcCandidatura.class);
		Root<AcCandidatura> f = c.from(AcCandidatura.class);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = f.join(AcCandidatura_.vaDatiVacancy);
		Predicate equalPredicate = cb.equal(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy), vacacyId);
		c = c.where(equalPredicate);
		List<AcCandidatura> list = entityManager.createQuery(c).getResultList();
		List<AcVisualizzaCandidaturaDTO> listDto = new ArrayList<AcVisualizzaCandidaturaDTO>();
		for (AcCandidatura acCandidatura : list) {
			listDto.add(toVisualizzaDTO(acCandidatura));
		}
		return listDto;
	}
	
	public List<AcVisualizzaCandidaturaDTO> findDtosAcCandSchedValByVacancyId(int vacacyId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> c = cb.createQuery(AcCandidatura.class);
		Root<AcCandidatura> f = c.from(AcCandidatura.class);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = f.join(AcCandidatura_.vaDatiVacancy);
		Predicate equalPredicate = cb.equal(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy), vacacyId);		
		c = c.where(equalPredicate);
		c.orderBy(cb.asc(f.get(AcCandidatura_.dtmIns)));
		List<AcCandidatura> list = entityManager.createQuery(c).getResultList();
		List<AcVisualizzaCandidaturaDTO> listDto = new ArrayList<AcVisualizzaCandidaturaDTO>();
		for (AcCandidatura acCandidatura : list) {
			listDto.add(toVisualizzaDTO(acCandidatura));
		}
		return listDto;
	}
	
	/*
	public List<AcVisualizzaCandidaturaDTO> findDtosAcCandSchedValVaContattoByVacancyId(int vacacyId){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> c = cb.createQuery(AcCandidatura.class);
		Root<AcCandidatura> f = c.from(AcCandidatura.class);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = f.join(AcCandidatura_.vaDatiVacancy);
		Join<AcCandidatura, AcContatto> acContatto = f.join(AcCandidatura_.acContattos, JoinType.LEFT);
		Predicate equalPredicate = cb.equal(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy), vacacyId);		
		c = c.where(equalPredicate);
		c.orderBy(cb.asc(f.get(AcCandidatura_.dtmIns)));
		List<AcCandidatura> list = entityManager.createQuery(c).getResultList();
		List<AcVisualizzaCandidaturaDTO> listDto = new ArrayList<AcVisualizzaCandidaturaDTO>();
		for (AcCandidatura acCandidatura : list) {
			listDto.add(toVisualizzaDTO(acCandidatura));
		}
		return listDto;
	}
    */
	public Long findCandidatureCountByVacancyId(Integer vacancyId) {
		TypedQuery<Long> query = entityManager.createNamedQuery("findAcCandidaturaCountByVacancyId", Long.class);
		query.setParameter("paramVacancyId", vacancyId);
		return query.getSingleResult();
	}

	public List<AcCandidatura> findCandidatureByVacancyId(Integer vacancyId) {
		try {
			List<AcCandidatura> candidature = entityManager
					.createNamedQuery("findAcCandidaturaByVacancyId", AcCandidatura.class)
					.setParameter("paramVacancyId", vacancyId).getResultList();

			return candidature;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public AcCandidaturaDTO persistDTO(AcCandidaturaDTO data, Integer idPrincipalIns) {
		AcCandidaturaDTO ret = new AcCandidaturaDTO();
		String descrizione;
		Integer idCvDatiPersonaliCopia = null;
		Integer idLetteraCopia = null;

		CvDatiPersonali cvDatiPersonali = null;
		if (data.getFlagClicLavoro()) {
			cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonaliCl());
			descrizione = cvDatiPersonali.getDescrizione();
		} else {
			cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonali());
			descrizione = cvDatiPersonali.getDescrizione();
		}

		boolean cvInviato = cvDatiPersonali.getFlagInviato();

		if (cvInviato) {
			throw new MyPortalException("Il curriculum che stai cercando di inviare fa gia' parte di una candidatura.",
					true);
		}
		/*
		 * prima di salvare la candidatura crea una copia del CV e della lettera di accompagnamento, se non fanno gia'
		 * parte di una candidatura
		 */
		CvDatiPersonali cvDto = cvDatiPersonaliHome.copyByIdNoDTO(idPrincipalIns,
				cvDatiPersonali.getIdCvDatiPersonali(), descrizione, true);
		idCvDatiPersonaliCopia = cvDto.getIdCvDatiPersonali();

		Integer idLettera = data.getIdCvLetteraAcc();
		if (idLettera != null) {
			CvLetteraAcc cvLeteraAcc = cvLetteraAccHome.findById(idLettera);
			boolean letteraInviata = cvLeteraAcc.getFlagInviato();

			if (letteraInviata) {
				throw new MyPortalException(
						"La lettera di presentazione che stai cercando di inviare fa gia' parte di una candidatura.",
						true);
			}
			CvLetteraAccDTO letteraDTO = cvLetteraAccHome.copyById(idPrincipalIns, idLettera,
					"Copia lettera per candidatura", true);
			idLetteraCopia = letteraDTO.getId();
		}

		data.setIdCvDatiPersonali(idCvDatiPersonaliCopia);
		data.setIdCvLetteraAcc(idLetteraCopia);
		ret = super.persistDTO(data, idPrincipalIns);

		// invia i messaggi di notifica
		AcCandidatura acCandidatura = fromDTO(ret);
		Set<MsgMessaggioDTO> notifications = null;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			notifications = notificationBuilder.buildNotificationsNewIdoRER(acCandidatura);
		} else {
			notifications = notificationBuilder.buildNotifications(acCandidatura);
		}
		notificationBuilder.sendNotification(notifications);

		/*
		 * invio un messaggio di candidatura
		 */
		CvCandidaturaCl cvCandidaturaCl = cvDatiPersonali.getCvCandidaturaCl();
		clicLavoroCandidaturaEjb.creaMessaggioCandidatura(acCandidatura, cvCandidaturaCl,
				pfPrincipalHome.findById(idPrincipalIns));

		return ret;
	}

	/**
	 * Controlla che il curriculum passato come id sia presente in una candidatura verso l'azienda passata come id.
	 *
	 * @param idCvDatiPersonali
	 *            id del curriculum
	 * @param idAziendaInfo
	 *            id dell'azienda
	 * @return TRUE se il CV e' stato inviato in una candidatura all'azienda, FALSE altrimenti
	 */
	public boolean checkCurriculumCandidatura(Integer idCvDatiPersonali, Integer idAziendaInfo) {
		boolean result = false;

		List<AcCandidatura> candidature = findAllByIdCvDatiPersonaliIdAziendaInfo(idCvDatiPersonali, idAziendaInfo);

		if (candidature.size() > 0) {
			result = true;
		}

		return result;
	}

	/**
	 * Controlla che la lettera di presentazione passata come id sia presente in una candidatura verso l'azienda passata
	 * come id.
	 *
	 * @param idCvLetteraAcc
	 *            id della lettera
	 * @param idAziendaInfo
	 *            id dell'azienda
	 * @return TRUE se il CV e' stato inviato in una candidatura all'azienda, FALSE altrimenti
	 */
	public boolean checkLetteraCandidatura(Integer idCvLetteraAcc, Integer idAziendaInfo) {
		boolean result = false;

		List<AcCandidatura> candidature = findAllByIdCvLetteraAccIdAziendaInfo(idCvLetteraAcc, idAziendaInfo);

		if (candidature.size() > 0) {
			result = true;
		}

		return result;
	}

	@Override
	public void remove(AcCandidatura acCandidatura) {
		removeReferencesFromMessages(acCandidatura, acCandidatura.getIdAcCandidatura());
		super.remove(acCandidatura);
	}

	/**
	 * Controlla se la vacancy e' referenziata da qualche messaggio e in caso ne elimina i riferimenti.
	 *
	 * @param entity
	 * @param idCvDatiPersonali
	 */
	private void removeReferencesFromMessages(AcCandidatura entity, Integer idAcCandidatura) {
		List<MsgContatto> msgContattos = msgMessaggioHome.findMsgContattoByIdAcCandidatura(idAcCandidatura);
		for (MsgContatto msgContatto : msgContattos) {
			msgContatto.setAcCandidatura(null);
			entityManager.merge(entity);
		}
	}

	/**
	 * Genera un documento Excel contenente la lista di candidature a una vacancy.
	 */
	public ByteArrayInputStream generaXlsListaCandidature(VaVisualizzaDTO vacancy) throws IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet candidatureSheet = wb.createSheet("Candidature");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Row labelRow = candidatureSheet.createRow(0);
		labelRow.createCell(0).setCellValue("Nome");
		labelRow.createCell(1).setCellValue("Cognome");
		labelRow.createCell(2).setCellValue("Codice fiscale");
		labelRow.createCell(3).setCellValue("Data di nascita");
		labelRow.createCell(4).setCellValue("Email");
		labelRow.createCell(5).setCellValue("Telefono");
		labelRow.createCell(6).setCellValue("Cellulare");
		labelRow.createCell(7).setCellValue("Indirizzo domicilio");
		labelRow.createCell(8).setCellValue("Comune domicilio");
		labelRow.createCell(9).setCellValue("Provincia domicilio");
		labelRow.createCell(10).setCellValue("Data candidatura");

		labelRow.createCell(11).setCellValue("");
		labelRow.createCell(12).setCellValue("Stato occupazionale (PG)");
		labelRow.createCell(13).setCellValue("Lavoratore intermittente (PG)");
		labelRow.createCell(14).setCellValue("Liste speciali (PG)");
		labelRow.createCell(15).setCellValue("Data dichiarazione (PG)");

		labelRow.createCell(16).setCellValue("");
		labelRow.createCell(17).setCellValue("Stato occupazionale (TR)");
		labelRow.createCell(18).setCellValue("Lavoratore intermittente (TR)");
		labelRow.createCell(19).setCellValue("Liste speciali (TR)");
		labelRow.createCell(20).setCellValue("Data dichiarazione (TR)");

		int numeroRiga = 1;
		for (AcVisualizzaCandidaturaDTO candidatura : vacancy.getListAcCandidaturaDTO()) {
			UtenteCompletoDTO utente = utenteInfoHome.findDTOCompletoById(candidatura.getIdPrincipalIns());
			DeProvinciaDTO provDomicilio = deProvinciaHome.findDTOById(utente.getComuneDomicilio().getIdProvincia());

			Row candidaturaRow = candidatureSheet.createRow(numeroRiga);
			candidaturaRow.createCell(0).setCellValue(utente.getNome());
			candidaturaRow.createCell(1).setCellValue(utente.getCognome());
			candidaturaRow.createCell(2).setCellValue(utente.getCodiceFiscale());
			candidaturaRow.createCell(3)
					.setCellValue(utente.getDataNascita() != null ? dateFormat.format(utente.getDataNascita()) : "---");
			candidaturaRow.createCell(4).setCellValue(utente.getEmail());
			candidaturaRow.createCell(5).setCellValue(utente.getTelCasa());
			candidaturaRow.createCell(6).setCellValue(utente.getCellulare());
			candidaturaRow.createCell(7).setCellValue(utente.getIndirizzoDomicilio());
			candidaturaRow.createCell(8).setCellValue(utente.getComuneDomicilio().getDescrizione());
			candidaturaRow.createCell(9).setCellValue(provDomicilio.getDescrizione());
			candidaturaRow.createCell(10).setCellValue(dateFormat.format(candidatura.getDtmIns()));

			for (AcCandidaturaDatiDTO candidaturaDati : candidatura.getAcCandidaturaDatiList()) {
				int primaCella = 12; // Di default, do per scontato che venga da Perugia
				if (candidaturaDati.getDeProvenienzaDTO() != null
						&& candidaturaDati.getDeProvenienzaDTO().getId().equals("SILTR")) {
					primaCella = 17;
				}

				candidaturaRow.createCell(primaCella).setCellValue(candidaturaDati.getDescStatoOccupazionale());
				candidaturaRow.createCell(primaCella + 1).setCellValue(
						candidaturaDati.getFlagIntermittente() != null && candidaturaDati.getFlagIntermittente() ? "Sì"
								: "No");
				candidaturaRow.createCell(primaCella + 2).setCellValue(candidaturaDati.getListeSpeciali());
				if (candidaturaDati.getDataDichiarazione() != null) {
					candidaturaRow.createCell(primaCella + 3)
							.setCellValue(dateFormat.format(candidaturaDati.getDataDichiarazione()));
				}
			}

			numeroRiga++;
		}

		// Genero l'inputStream da restituire
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		wb.write(output);
		wb.close();
		output.close();
		return new ByteArrayInputStream(output.toByteArray());
	}

	public AcCandidatura merge(AcCandidatura in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}
}
