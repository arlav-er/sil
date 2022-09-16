package it.eng.myportal.beans.curriculum.pf;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.FormTypeAction;

/**
 * Bean delle Competenze Trasversali
 *
 *
 */
public class CvDatiPrincipaliPfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	// non c'e` injection, vengono passati dal mbean
	private DeComuneHome deComuneHome;
	private DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;
	private CvAllegatoHome cvAllegatoHome;
	private CvAllegato cvAllegato;
	private CvAltreInfoHome cvAltreInfoHome;
	private CvAltreInfo cvAltreInfo;

	// per quelle LAZY
	private DeComune comuneNascita;
	private DeComune comuneDomicilio;
	private UploadedFile file;
	private FileUpload component;
	protected FormTypeAction formTypeAction = FormTypeAction.I;


	private List<SelectItem> deAmbitoDiffusioneItems;

	private byte[] foto;

	// Costruttore
	public CvDatiPrincipaliPfSection(CurriculumVitaePfBean cvBean, DeComuneHome deComuneHome,
			DeAmbitoDiffusioneHome deAmbitoDiffusioneHome, CvAllegatoHome cvAllegatoHome,
			CvAltreInfoHome cvAltreInfoHome) {
		super(cvBean);
		this.deComuneHome = deComuneHome;
		this.deAmbitoDiffusioneHome = deAmbitoDiffusioneHome;
		this.cvAllegatoHome = cvAllegatoHome;
		this.cvAltreInfoHome = cvAltreInfoHome;
	}

	public DeComune getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(DeComune comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public DeComune getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(DeComune comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public CvDatiPersonali getCvDatiPersonali() {
		return getCurriculumVitaePfBean().getCvDatiPersonali();
	}

	public List<SelectItem> getDeAmbitoDiffusioneItems() {
		return deAmbitoDiffusioneItems;
	}

	public void setDeAmbitoDiffusioneItems(List<SelectItem> deAmbitoDiffusioneItems) {
		this.deAmbitoDiffusioneItems = deAmbitoDiffusioneItems;
	}

	public CvAllegato getCvAllegato() {
		return cvAllegato;
	}

	public void setCvAllegato(CvAllegato cvAllegato) {
		this.cvAllegato = cvAllegato;
	}

	public CvAltreInfo getCvAltreInfo() {
		return cvAltreInfo;
	}

	public void setCvAltreInfo(CvAltreInfo cvAltreInfo) {
		this.cvAltreInfo = cvAltreInfo;
	}

	@Override
	public void initSection() {
		deAmbitoDiffusioneItems = deAmbitoDiffusioneHome.getListItems(false, "descrizione");
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		cvAllegato = new CvAllegato();
		if (cvDatiPersonali.getIdCvDatiPersonali() != null) {
			if (cvDatiPersonali.getDeComuneDomicilio() != null) {
				comuneDomicilio = deComuneHome.findById(cvDatiPersonali.getDeComuneDomicilio().getCodCom());
			}
			if (cvDatiPersonali.getDeComuneNascita() != null) {
				comuneNascita = deComuneHome.findById(cvDatiPersonali.getDeComuneNascita().getCodCom());
			}
			List<CvAltreInfo> cvAltreInfos = cvAltreInfoHome
					.findProperByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
			if (cvAltreInfos.size() == 1) {
				cvAltreInfo = cvAltreInfos.get(0);
			} else {
				log.error("GRAVE: sono presenti più istanze di cvAltreInfo per il curriculum vitae con id: "
						+ getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
			}
		} else {
			cvDatiPersonali.setDtNascita(getCurriculumVitaePfBean().getUtenteCompletoDTO().getDataNascita());
			cvDatiPersonali.setCodiceFiscale(getCurriculumVitaePfBean().getUtenteCompletoDTO().getCodiceFiscale());
			comuneNascita = deComuneHome.findById(
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getUtenteInfo().getComuneNascita().getId());
			comuneDomicilio = deComuneHome.findById(
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getUtenteInfo().getComuneDomicilio().getId());
			cvDatiPersonali.setIndirizzoDomicilio(
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getUtenteInfo().getIndirizzoDomicilio());
			cvDatiPersonali.setEmail(getCurriculumVitaePfBean().getUtenteCompletoDTO().getEmail());
			cvDatiPersonali.setTel2(getCurriculumVitaePfBean().getUtenteCompletoDTO().getUtenteInfo().getCellulare());
			cvDatiPersonali.setVisibilita(new Boolean(true));
			// Init CvAltreInfo
			cvAltreInfo = new CvAltreInfo();
			cvAltreInfo.setFlagAutomunito(false);
			cvAltreInfo.setFlagMotomunito(false);
		}
		// inizzializzazione Allegato
		cvAllegato.setCvDatiPersonali(cvDatiPersonali);
		cvAllegato = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
		if (cvAllegato != null) {
			log.debug("Il Curriculum già possiede un allegato. " + cvAllegato.getIdCvAllegato());
		}
	}

	public void handleFileFotoUpload(FileUploadEvent event) {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
		try {
			UploadedFile file = event.getFile();
			cvDatiPersonali.setFoto(file.getContents());
			homeEJB.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
		} catch (Exception e) {
			String msgko = "Errore durante caricamento foto";
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
		String msgok = "Immagine aggiornata";
		addJSSuccessMessage(msgok);
	}

	public void removeFotoProfilePic() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
		cvDatiPersonali.setFoto(null);
		try {
			homeEJB.merge(cvDatiPersonali);

		} catch (Exception e) {
			String msgko = "Errore durante eliminazione foto"; // utils.getUiProperty("msg.account.upload_photo.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
			return;
		}

		String msgok = "Foto rimossa correttamente";// utils.getUiProperty("msg.account.upload_photo.ok");
		addJSSuccessMessage(msgok);

	}

	public void handleFileUpload(FileUploadEvent event) {
		log.debug("handleFileUpload is called");
		cvAllegato = creaAllegatoFromFile(event);
		this.setFile(event.getFile());
	}

	private CvAllegato creaAllegatoFromFile(FileUploadEvent event) {
		cvAllegato = new CvAllegato();
		if (formTypeAction.equals(FormTypeAction.R)) {
			cvAllegato.setContenuto(event.getFile().getContents());
			cvAllegato.setFilename(event.getFile().getFileName());
			formTypeAction = FormTypeAction.E;
		} else {
			cvAllegato.setContenuto(event.getFile().getContents());
			cvAllegato.setFilename(event.getFile().getFileName());
			formTypeAction = FormTypeAction.I;
		}
		return cvAllegato;
	}

	public void deleteFile() {
		this.setFile(null);
		setCvAllegato(null);
		formTypeAction = FormTypeAction.R;
	}

	public UploadedFile getFile() {
		return file;
	}

	public FileUpload getComponent() {
		return component;
	}

	public void setComponent(FileUpload component) {
		this.component = component;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		Date now = new Date();

		CvDatiPersonaliHome homeEJB = getCurriculumVitaePfBean().getCvDatiPersonaliHome();
		CvAllegatoHome homeAllegato = getCurriculumVitaePfBean().getCvAllegatoHome();
		cvDatiPersonali.setDeComuneDomicilio(comuneDomicilio);
		cvDatiPersonali.setDeComuneNascita(comuneNascita);

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 60));
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 180));
		} else {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 30));
		}

		if (cvDatiPersonali.getDtmIns() == null)

		{
			CvDatiPersonali savedCvDatiPersonali;
			CvAllegato savedCvAllegato;

			try {
				savedCvDatiPersonali = homeEJB.persist(cvDatiPersonali,
						getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
				getCurriculumVitaePfBean().setCvDatiPersonali(savedCvDatiPersonali);
				RequestContext.getCurrentInstance().addCallbackParam("datiPersonaliId",
						savedCvDatiPersonali.getIdCvDatiPersonali());
				// Controllo Allegato;
				if (file != null && formTypeAction.equals(FormTypeAction.I)) {
					// cvAllegato = new CvAllegato();
					cvAllegato.setFilename(file.getFileName());
					cvAllegato.setContenuto(file.getContents());
					savedCvAllegato = homeAllegato.persistAllegato(savedCvDatiPersonali, cvAllegato,
							getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
					getCurriculumVitaePfBean().setCvAllegato(savedCvAllegato);
					RequestContext.getCurrentInstance().addCallbackParam("datiPersonaliId",
							savedCvAllegato.getIdCvAllegato());
				}

			} catch (MyPortalException e) {
				log.error(e.getMessage());
				addAlertErrorMessage(e.getCodErrore(), e.getMessage());
			}

			cvAltreInfo.setCvDatiPersonali(cvDatiPersonali);
			cvAltreInfo = cvAltreInfoHome.persist(cvAltreInfo,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
			addJSSuccessMessage("Il tuo CV è stato creato.\n"
					+ "Dopo aver inserito i dati generali continua con la compilazione delle altre sezioni.\n"
					+ "Potrai allegare anche un file in modo da arricchire le informazioni che stai inserendo.");
			log.info("Inserimento Altre Info");
		} else {
			CvDatiPersonali mergedCvDatiPersonali = homeEJB.merge(cvDatiPersonali,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
			getCurriculumVitaePfBean().setCvDatiPersonali(mergedCvDatiPersonali);
			// merge CV ALLEGATO
			cvAllegato = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
			CvAllegato cvAllegatoInsert = new CvAllegato();
			if (cvAllegato == null && file != null) {
				cvAllegatoInsert.setFilename(file.getFileName());
				cvAllegatoInsert.setContenuto(file.getContents());
				if (formTypeAction.equals(FormTypeAction.I)
						|| formTypeAction.equals(FormTypeAction.E) && file != null) {
					CvAllegato savedCvAllegato = homeAllegato.persistAllegato(mergedCvDatiPersonali, cvAllegatoInsert,
							getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
					log.debug("Persist Allegato avvenuto con successo: "
							+ cvAllegatoInsert.getCvDatiPersonali().getIdCvDatiPersonali());
					getCurriculumVitaePfBean().setCvAllegato(savedCvAllegato);
				}

			} else if (formTypeAction.equals(FormTypeAction.E) && file != null) {
				cvAllegato.setFilename(file.getFileName());
				cvAllegato.setContenuto(file.getContents());
				// cvAllegatoInsert.setCvDatiPersonali(mergedCvDatiPersonali);
				CvAllegato savedCvAllegato = homeAllegato.merge(cvAllegato,
						getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
				getCurriculumVitaePfBean().setCvAllegato(savedCvAllegato);
				log.debug("Merge Allegato avvenuto con successo:"
						+ cvAllegato.getCvDatiPersonali().getIdCvDatiPersonali());
			}

			cvAltreInfo.setCvDatiPersonali(cvDatiPersonali);
			cvAltreInfo = cvAltreInfoHome.merge(cvAltreInfo,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
			log.info("Update Altre Info");
		}
		if (formTypeAction.equals(FormTypeAction.R)) {
			cvAllegato = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());
			int removeCvAllegato = homeAllegato
					.removeByCurriculumId(cvAllegato.getCvDatiPersonali().getIdCvDatiPersonali());
			if (removeCvAllegato > 0) {

				log.debug("rimozione Allegato avvenuto con successo: "
						+ cvAllegato.getCvDatiPersonali().getIdCvDatiPersonali());
			} else {
				log.error("Nessun Allegato trovato con idCvDatiPersonali. " + cvDatiPersonali.getIdCvDatiPersonali());
				throw new MyPortalException("Allegato not found");
			}
		}
		curriculumVitaePfBean.calcCompletionPercentage();
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public StreamedContent downloadAllegatoFile() {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cvAllegato.getContenuto());
			String ext = FilenameUtils.getExtension(cvAllegato.getFilename());
			return new DefaultStreamedContent(is, ext, cvAllegato.getFilename());
		} catch (NullPointerException e) {
			log.error("Errore durante downloadAllegatoFile : " + e.toString());
			return null;
		}
	}
}