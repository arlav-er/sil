package it.eng.myportal.beans.candidature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import it.eng.myportal.beans.RestoreParameters;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.entity.AcAllegato;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidaturaValutazione;
import it.eng.myportal.entity.AcContatto;
import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;
import it.eng.myportal.entity.home.AcAllegatoHome;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaSegnalazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.entity.home.nodto.AcCandidaturaValutazioneHome;
import it.eng.myportal.entity.home.nodto.AcContattoHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.rest.report.GetCurriculumUtente;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;

/**
 * BackingBean della pagina di visualizzazione dell'elenco delle candidature
 *
 * @author Enrico D'Angelo
 *
 */
@ManagedBean
@ViewScoped
public class VisualizzaListaCandidatureVacancyBean extends AbstractVisualizzaListaCandidatureAziendaBean {
	
	@EJB
	DeComuneHome deComuneHome;
	@EJB
	AcCandidaturaValutazioneHome acCandidaturaValutazioneHome;

	@EJB
	AcContattoHome acContattoHome;
	
	@EJB
	UtenteInfoHome utenteInfoHome;
	
	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	GetCurriculumUtente getCurriculumUtente;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	VaSegnalazioneHome vaSegnalazioneHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;
	
	@EJB
	AcAllegatoHome acAllegatoHome;
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private List<AcVisualizzaCandidaturaDTO> filterCandidatureList;
	private List<AcVisualizzaCandidaturaDTO> selectedCandidature;

	// used in sendEmail modal
	private String messageTitle;
	private String messageBody;

	private Integer vacancyId;
	private StreamedContent xmlReport;
	private VaDatiVacancy vaVacancy;
    private String filtroVeloce;
	RestoreParameters ret;
	DataTable dataTable;

	/**
	 * Restituisce la lista di tutte le candidature inviate alla vacancy passata come input
	 *
	 * @param vacacyId
	 *            id della vacancy
	 * @return lista delle candidature inviate alla vacancy
	 */
	@Override
	protected List<AcVisualizzaCandidaturaDTO> getListaCandidature(Integer vacacyId) {

		this.vacancyId = vacacyId;
		//return filterCandidatureList;
		return acCandidaturaHome.findDtosAcCandSchedValByVacancyId(vacacyId);
		/////return acCandidaturaHome.findDtosAcCandSchedValVaContattoByVacancyId(vacacyId);
	}
	@Override
	public void CheckSessione(){
		if(beanParamsSess != null){
			if(beanParamsSess.containsKey("filtroVeloce")){
				Object obj=beanParamsSess.get("filtroVeloce");
				filtroVeloce = (String)obj;
				setFiltroVeloce(filtroVeloce);
			}
			if(beanParamsSess.containsKey("livelliSelezione")){
				Object obj = beanParamsSess.get("livelliSelezione");
				List<String> list = (List<String>)obj;
				if(!list.isEmpty()){
					setLivelliValutazioneComplessivaSelezionati(list);
					populateValutazioneLabel();
					filtraCandidatureNewVersion();
				}
			}
		}

	}
	public boolean globalFilterFunction(Object filter) {
		filtroVeloce= (filter == null) ? null : filter.toString().trim().toLowerCase();
		if (filtroVeloce == null || filtroVeloce.equals("")) {
			return false;
		}
		return true;
	}

	public boolean checkVisibilityPulsanti() {
		boolean check = true;
		boolean isProprietarioPalese = false;
		if(this.vacancyId !=null) {
			vaVacancy = vaDatiVacancyHome.findById(this.vacancyId);
			
			if (vaVacancy.isFromSIL()) {

				boolean isProprietario = getSession().getPrincipalId()
						.equals(vaVacancy.getPfPrincipal().getIdPfPrincipal());
				if (vaVacancy.getPfPrincipalPalese() != null
						&& getSession().getPrincipalId().equals(vaVacancy.getPfPrincipalPalese().getIdPfPrincipal())) {
					isProprietarioPalese = true;
				}
				boolean isCodEvasioneDFD = vaVacancy.getDeEvasioneRich() != null && vaVacancy.getDeEvasioneRich()
						.getCodEvasione().equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE); // DFD

				if (vaVacancy.getPfPrincipalPalese() != null) { // caso particolare in cui ha senso fare dei controlli
																// --- in tutti gli altri casi vuol dire che l'utente è
																// UNICO propietario e può modificare (in tal caso
																// ovviamente non DFD)
					if (isProprietario && !isProprietarioPalese) { // caso CPI
						if (isCodEvasioneDFD) {
							return false;
						}
					}
				}
			}
		}
		return check;
	}

	public StreamedContent downloadCv() {
		log.info(selectedCandidature.size());
		ByteArrayInputStream stream = null;
		DefaultStreamedContent zipCurriculumSelected;

		if(selectedCandidature.size() !=0){

			try {
				stream = this.generaZipListaCandidature();
				zipCurriculumSelected = new DefaultStreamedContent(stream, "application/zip", "zipCurriculum.zip");
				//stream = this.generaZipListaCandidature();
				return zipCurriculumSelected;
			} catch (IOException e) {
				log.error("Errore durante la generazione del file zip");
				addCustomErrorMessage("Errore durante la generazione del file zip");
				throw new RuntimeException("ERRORE DOWNLOAD CV da gestire");
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}else{
			addAlertWarnMessage("Attenzione", "Nessun elemento selezionato");
			log.error("Non è possibile eseguire il download del curriculum.Nessun elemento selezionato nella lista");
			throw  new MyPortalException("Nessun CV selezionato o lista vuota: "+ selectedCandidature.size());
		}
	}

	/**
	 * Genera e restituisce il report delle candidature.
	 */
	
	public StreamedContent getXmlReport() {
		
		log.info(selectedCandidature.size());
		ByteArrayInputStream stream = null;
		DefaultStreamedContent xlsCurriculumSelected;

		if(selectedCandidature.size() !=0){

			try {
				stream = this.generaXlsListaCandidature();
				xlsCurriculumSelected = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "reportCandidature.xls");
				xmlReport = xlsCurriculumSelected;
				//stream = this.generaZipListaCandidature();
				return xmlReport;
			} catch (IOException e) {
				log.error("Errore durante la generazione del file excel");
				addCustomErrorMessage("Errore durante la generazione del file excel");
				throw new RuntimeException("ERRORE DOWNLOAD CV da gestire");
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}else{
			addAlertWarnMessage("Attenzione", "Nessun elemento selezionato");
			log.error("Non è possibile eseguire il'estrazione delle candidature.Nessun elemento selezionato nella lista");
			throw  new MyPortalException("Nessuna candidatura selezionata o lista vuota: "+ selectedCandidature.size());
		}
	}
	
	
	private ByteArrayInputStream generaZipListaCandidature() throws IOException {
        CvAllegato cvAllegatoTmp = null;
        AcAllegato acAllegatoTmp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ByteArrayInputStream bis = null;
		ByteArrayInputStream bisZip = null;
		int number_of_cv = 1;
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		for (AcVisualizzaCandidaturaDTO candidatura : selectedCandidature) {

			Response response = getCurriculumUtente.getCurriculumUtente(candidatura.getIdCvDatiPersonali(), request);
			bis = (ByteArrayInputStream) response.getEntity();
			out.putNextEntry(new ZipEntry( number_of_cv + "_" + "cv_per_candidatura_" + candidatura.getCognomeCandidato() + "_"
					+ candidatura.getNomeCandidato() + ".pdf"));
			IOUtils.copy(bis, out);
			bis.close();
			out.flush();
			out.closeEntry();
			
			
			/* estraggo allegato per ogni cv */
			cvAllegatoTmp = cvAllegatoHome.findEntityByCurriculumId(candidatura.getIdCvDatiPersonali());
			if(cvAllegatoTmp != null && cvAllegatoTmp.getContenuto() != null) {
				String ext = FilenameUtils.getExtension(cvAllegatoTmp.getFilename());
				bis = new ByteArrayInputStream(cvAllegatoTmp.getContenuto());
				out.putNextEntry(new ZipEntry( number_of_cv +  "_" + "allegato_cv_per_candidatura_" + candidatura.getCognomeCandidato() + "_"
						+ candidatura.getNomeCandidato() + "." + ext));
				IOUtils.copy(bis, out);
				bis.close();
				out.flush();
				out.closeEntry();
			}
			
			/* estraggo allegato per ogni candidatura  */
			if (candidatura.getIdAcAllegato() != null) {
				acAllegatoTmp = acAllegatoHome.findById(candidatura.getIdAcAllegato());
				if (acAllegatoTmp != null && acAllegatoTmp.getContenuto() != null) {
					String ext = FilenameUtils.getExtension(acAllegatoTmp.getFilename());
					bis = new ByteArrayInputStream(acAllegatoTmp.getContenuto());
					out.putNextEntry(
							new ZipEntry( number_of_cv +  "_" + "allegato_candidatura_cv_per_candidatura_" + candidatura.getCognomeCandidato()
									+ "_" + candidatura.getNomeCandidato() + "." + ext));
					IOUtils.copy(bis, out);
					bis.close();
					out.flush();
					out.closeEntry();
				}
			}
			
			number_of_cv++;
		}
		if (out != null) {
			out.finish();
			out.flush();
			IOUtils.closeQuietly(out);
		}
		if (bis != null) {
			bis.close();
		}
		bisZip = new ByteArrayInputStream(baos.toByteArray());

		if (baos != null) {
			baos.close();
		}
		return bisZip;
	}
	private ByteArrayInputStream generaXlsListaCandidature() throws IOException {

		Workbook wb = new HSSFWorkbook();
		Sheet candidatureSheet = wb.createSheet("Candidature");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        CvDatiPersonali cvDatiPersonali = null;
        PfPrincipal pfprincipal = null;
        AcCandidaturaValutazione acCandidaturaValutazione = null;
        String valutazioneComplessiva =null;
		Row labelRow = candidatureSheet.createRow(0);
		labelRow.createCell(0).setCellValue("Nome");
		labelRow.createCell(1).setCellValue("Cognome");

		labelRow.createCell(2).setCellValue("Data di nascita");
		labelRow.createCell(3).setCellValue("Comune domicilio");
		labelRow.createCell(4).setCellValue("Email");
		labelRow.createCell(5).setCellValue("Cellulare");
		labelRow.createCell(6).setCellValue("Data candidatura");
		labelRow.createCell(7).setCellValue("Valutazione complessiva");
		
		int numeroRiga = 1;
		for (AcVisualizzaCandidaturaDTO candidatura : selectedCandidature) {
			valutazioneComplessiva = "";
			acCandidaturaValutazione = null;
			Row candidaturaRow = candidatureSheet.createRow(numeroRiga);
			cvDatiPersonali = cvDatiPersonaliHome.findById(candidatura.getIdCvDatiPersonali());
			pfprincipal = pfPrincipalHome.findById(cvDatiPersonali.getPfPrincipal().getIdPfPrincipal());
			 try {
					acCandidaturaValutazione = acCandidaturaValutazioneHome
							.findByAcCandidaturaId(candidatura.getId());

				} catch (MyPortalNoResultFoundException e) {
					// nessun risultato
				}
			
			candidaturaRow.createCell(0).setCellValue(pfprincipal.getNome());
			candidaturaRow.createCell(1).setCellValue(pfprincipal.getCognome());
			candidaturaRow.createCell(2).setCellValue(cvDatiPersonali.getDtNascita() != null ? dateFormat.format(cvDatiPersonali.getDtNascita()) : "");
			DeComuneDTO comDomicilio = deComuneHome.findDTOById(cvDatiPersonali.getDeComuneDomicilio().getCodCom());
			candidaturaRow.createCell(3).setCellValue(comDomicilio.getDescrizione());
			
	        candidaturaRow.createCell(4).setCellValue(cvDatiPersonali.getEmail());
	        candidaturaRow.createCell(5).setCellValue(cvDatiPersonali.getTel2());
	        candidaturaRow.createCell(6).setCellValue(dateFormat.format(candidatura.getDtmIns()));
	        
	        if(acCandidaturaValutazione != null && acCandidaturaValutazione.getDtmIns() != null && acCandidaturaValutazione.getValutazioneComplessiva() != null) {
	        	switch (acCandidaturaValutazione.getValutazioneComplessiva().getValue()) {
	        	case "L0":
	    			valutazioneComplessiva = "Non idoneo";
	    			break;
	        	case "L1":
	    			valutazioneComplessiva = "1";
	    			break;
	    		case "L2":
	    			valutazioneComplessiva = "2";
	    			break;
	    		case "L3":
	    			valutazioneComplessiva = "3";
	    			break;
	    		case "L4":
	    			valutazioneComplessiva = "4";
	    			break;
	    		default:
	    			break;
	    		}
	        }
	        
	        candidaturaRow.createCell(7).setCellValue(valutazioneComplessiva);
	        numeroRiga++;
		}
		// Genero l'inputStream da restituire
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		wb.write(output);
		wb.close();
		output.close();
		return new ByteArrayInputStream(output.toByteArray());
	}

	
	public List<AcVisualizzaCandidaturaDTO> getFilterCandidatureList() {
		return filterCandidatureList;
	}

	public void setFilterCandidatureList(List<AcVisualizzaCandidaturaDTO> filterCandidatureList) {
		this.filterCandidatureList = filterCandidatureList;
	}

	public String candidatureLabel = "Seleziona...";

	// Backing bean
	public void populateLabel() {
		/* Populating the label with the selected options */
		candidatureLabel = new String("");
		if (getLivelliValutazioneSelezionati().size() == 0) {
			candidatureLabel = "Seleziona...";
		} else {
			getLivelliValutazioneSelezionati();
			candidatureLabel = "";
			for (DeIdoneitaCandidatura current : getLivelliValutazioneSelezionati()) {
				candidatureLabel = candidatureLabel + " " + current.getDescrizione();
			}
		}
	}
	
	public String candidatureValutazioneLabel = "Seleziona...";
	
	public String getCandidatureValutazioneLabel() {
		return candidatureValutazioneLabel;
	}

	public void setCandidatureValutazioneLabel(String candidatureValutazioneLabel) {
		this.candidatureValutazioneLabel = candidatureValutazioneLabel;
	}

	// Backing bean
	public void populateValutazioneLabel() {
		/* Populating the label with the selected options */
		candidatureValutazioneLabel = new String("");
		if (getLivelliValutazioneComplessivaSelezionati().size() == 0) {
			candidatureValutazioneLabel = "Seleziona...";
		} else {
			getLivelliValutazioneComplessivaSelezionati();
			candidatureValutazioneLabel = "";
			for (String current : getLivelliValutazioneComplessivaSelezionati()) {
				candidatureValutazioneLabel = candidatureValutazioneLabel + " " +  current;
			}
			generateRestoreParams();
			putParamsIntoSession();
		}
	}
	
	// Backing bean
	public void populateValutazioneLabelSelectUnSelectAll() {
		/* Populating the label with the selected options */
		candidatureValutazioneLabel = new String("");
		if (getLivelliValutazioneComplessivaSelezionati().size() == 0 || (getLivelliValutazioneComplessivaSelezionati().size() < getLivelliValutazioneComplessiva().size())) { // caso in cui seleziono tutto
			getLivelliValutazioneComplessiva();
			candidatureValutazioneLabel = "";
			for (String current : getLivelliValutazioneComplessiva()) {
				candidatureValutazioneLabel = candidatureValutazioneLabel + " " +  current;
			}
			generateRestoreParamsSelectUnSelectAll();
			putParamsIntoSession();
			
		} else {
            candidatureValutazioneLabel = "Seleziona...";
		}
	}

	public String getCandidatureLabel() {
		return candidatureLabel;
	}

	public void setCandidatureLabel(String candidatureLabel) {
		this.candidatureLabel = candidatureLabel;
	}

	public List<AcVisualizzaCandidaturaDTO> getSelectedCandidature() {
		return selectedCandidature;
	}

	public void setSelectedCandidature(List<AcVisualizzaCandidaturaDTO> selectedCandidature) {
		this.selectedCandidature = selectedCandidature;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public void sendEmail() throws MyPortalNoResultFoundException {
		log.info(selectedCandidature.size());
		AcContatto acContatto = new AcContatto();
		AcCandidatura acCandidatura = null;
		VaDatiVacancy vaDatiVacancy = null;
		Integer idPfPrincipalAzienda = session.getPrincipalId();
		PfPrincipal princ = pfPrincipalHome.findById(idPfPrincipalAzienda);
		if(selectedCandidature.size() != 0){
			RequestContext.getCurrentInstance().addCallbackParam("errore", false);
			Date now = new Date();
			for (AcVisualizzaCandidaturaDTO acVisualizzaCandidaturaDTO : selectedCandidature) {
				CvDatiPersonali cv = cvDatiPersonaliHome.findById(acVisualizzaCandidaturaDTO.getIdCvDatiPersonali());
				
				acCandidatura = acCandidaturaHome.findById(acVisualizzaCandidaturaDTO.getId());
				vaDatiVacancy = vaDatiVacancyHome.findById(acVisualizzaCandidaturaDTO.getIdVaDatiVacancy());
				
				inviaMessaggio(cv);
				/* scrittura tabella AC_Contatto*/
				acContatto = acContattoHome.findAcContattoByCvId(acVisualizzaCandidaturaDTO.getIdCvDatiPersonali());
				
				if (acContatto.getDtmIns() == null) {
					acContatto.setEmailDestinatario(cv.getEmail());
					acContatto.setDataContatto(now);
					acContatto.setDtmIns(now);
					acContatto.setDtmMod(now);
					acContatto.setPfPrincipalIns(princ);
					acContatto.setPfPrincipalMod(princ);
					acContatto.setAcCandidatura(acCandidatura);
					acContatto.setVaDatiVacancy(vaDatiVacancy);
					acContatto = acContattoHome.persist(acContatto, idPfPrincipalAzienda);
				} else {
					acContatto.setEmailDestinatario(cv.getEmail());
					acContatto.setDataContatto(now);
					acContatto.setDtmMod(now);				
					acContatto.setPfPrincipalMod(princ);
					acContatto.setAcCandidatura(acCandidatura);
					acContatto.setVaDatiVacancy(vaDatiVacancy);
					acContatto = acContattoHome.merge(acContatto, idPfPrincipalAzienda);
				}			
			}
		}else{
			RequestContext.getCurrentInstance().addCallbackParam("errore", true);
			addAlertWarnMessage("Attenzione", "Nessun elemento selezionato");
			log.warn("Non è possibile inviare un messaggio. Nessun elemento selezionato nella lista");
		}
	}

	public void refresh() {
	
		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(base
					+ "/faces/secure/azienda/candidature/list/main.xhtml?id="
					+ vacancyId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Invia la segnalazione di una candidatura all'email di un amico.
	 *
	 * @param cv
	 */
	private void inviaMessaggio(CvDatiPersonali cv) {

		// Se non inviata eseguo la procedura di invio email e salvataggio
		// DA RIATTIVARE QUANDO SI EFFETTUA PASSAGGIO IN PROD !!!!!
		EmailDTO emailDTO = EmailDTO.buildContattaCandidatoEmail(cv.getEmail(), messageTitle, messageBody);		
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);
		
		/* su richiesta non creiamo alcun messaggio per ogni candidatura selezionata dalla lista e per la quale clicchiamo sul pulsante Invia Messaggio
		PfPrincipal princ = pfPrincipalHome.findById(session.getPrincipalId());
		PfPrincipal princDest = pfPrincipalHome.findById(cv.getPfPrincipal().getIdPfPrincipal());
		Date now = new Date();
		try {
			// crea il messaggio di primo contatto
			MsgMessaggio msgPrimoContatto = new MsgMessaggio();
			msgPrimoContatto
					.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO));
			msgPrimoContatto.setOggetto(messageTitle);
			msgPrimoContatto.setPfPrincipalFrom(princ);
			msgPrimoContatto.setPfPrincipalTo(princDest);
			msgPrimoContatto.setCorpo(messageBody);
			msgPrimoContatto.setDtmIns(now);
			msgPrimoContatto.setDtmMod(now);
			msgPrimoContatto.setPfPrincipalIns(princ);
			msgPrimoContatto.setPfPrincipalMod(princ);
			msgMessaggioHome.persist(msgPrimoContatto);
			msgPrimoContatto.setTicket("" + msgPrimoContatto.getIdMsgMessaggio());
			msgMessaggioHome.merge(msgPrimoContatto);
		} catch (Exception e) {
			log.error("Errore in inserimento VaContatto in seguito a invio mail risposta candidatura per vacancyId:"
					+ vacancyId);
		}
		 */
		log.info("- Mail candidature inviata: {\n" + emailDTO.toString() + "}");
		// FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MAIL_INVIATA));
	}


	@Override
	public String getBackTo() {
		if (utils.isVDA() && isRedoBySess()) {
			// PURTROPPO IL SEGUENTE CODICE NON SI PUO' USARE PERCHE' FORZANDOLO A MANO IL TOKEN SCAZZA. IN ALTERNATIVA
			// VIENE USATA UNA SOLUZIONE VELOCE MA NON BUONA
			// RestoreParameters restoreParameters =
			// session.getParams().get(SESSION_TOKEN_HISTORY+RicercaOfferteBean.class.getSimpleName());
			// if(restoreParameters !=null){
			// String backTo = restoreParameters.get(BACK_TO).toString();
			// if(backTo != null &&
			// !"".equals(backTo)){
			// return restoreParameters.get(BACK_TO).toString();
			// }
			// }
			// TODO: TROVARE UNA SOLUZIONE MIGLIORE
			// Forzo il redirect alla pagina corrente.
			return getExternalContext().getRequestContextPath() + "/faces/secure/azienda/vacancies/ricerca.xhtml";
		}

        if (utils.isRER()) {
            String str="";
            if(session.getParams() !=null){
                if(session.getParams().containsKey("TOKEN_RicercaOfferteBean")){
                    str= "/faces/secure/azienda/vacancies/ricerca.xhtml";
                }else
                    str = "/faces/secure/azienda/home.xhtml?faces-redirect=true";
            }
            return str;
        }
		
		return super.getBackTo();
	}

	public String getOutcome(AcVisualizzaCandidaturaDTO dtoIn) {
		// bruttarello ma cosi` e` la vita
		CvDatiPersonali cv = cvDatiPersonaliHome.findById(dtoIn.getIdCvDatiPersonali());
		return cv.getFlagIdo() ? "visualizza_curriculumIdo" : "visualizza_curriculum";
	}

	public void addAlertWarnMessage(String title, String message) {
		title = StringEscapeUtils.escapeEcmaScript(title);
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.warnAlert('" + title + "','" + message + "')");
	}

    public String getFiltroVeloce() {
        return filtroVeloce;
    }

    public void setFiltroVeloce(String filtroVeloce) {
        this.filtroVeloce = filtroVeloce;
    }

    @Override
    protected RestoreParameters generateRestoreParams() {
        RestoreParameters ret = super.generateRestoreParams();
        if(filtroVeloce != null){
			ret.put("filtroVeloce", filtroVeloce);
		}
		if(!getLivelliValutazioneComplessivaSelezionati().isEmpty())
        	ret.put("livelliSelezione", getLivelliValutazioneComplessivaSelezionati());
        return ret;
    }
    
    
    protected RestoreParameters generateRestoreParamsSelectUnSelectAll() {
        RestoreParameters ret = super.generateRestoreParams();
        if(filtroVeloce != null){
			ret.put("filtroVeloce", filtroVeloce);
		}
		if(!getLivelliValutazioneComplessiva().isEmpty())
        	ret.put("livelliSelezione", getLivelliValutazioneComplessiva());
        return ret;
    }

	public void filterListener(FilterEvent filterEvent) {
		dataTable = (DataTable) filterEvent.getSource();
		generateRestoreParams();
		putParamsIntoSession();
	}

}
