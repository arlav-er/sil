package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.YgDichiarazioneNeetDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.YgDichiarazioneNeetHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class YgNewDichiarazioneNeetBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(YgNewDichiarazioneNeetBean.class);

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	YgDichiarazioneNeetHome ygDichiarazioneNeetHome;

	@EJB
	DeComuneHome deComuneHome;

	private YgDichiarazioneNeetDTO dichiarazione;
	private UploadedFile dichiarazioneUpload;

	@PostConstruct
	public void postConstruct() {
		dichiarazione = new YgDichiarazioneNeetDTO();
		dichiarazione.setStrRagioneSocialeEnte(session.getConnectedAzienda().getRagioneSociale());
		dichiarazione.setStrCodiceFiscaleEnte(aziendaInfoHome.findById(session.getConnectedAzienda().getId())
				.getCodiceFiscale());
		dichiarazione.setDtDichiarazione(Calendar.getInstance().getTime());
	}

	public void documentoIdentitaUploaded(FileUploadEvent event) {
		dichiarazioneUpload = event.getFile();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Operazione completata: il documento è stato caricato."));
	}

	public List<DeComuneDTO> completeComune(String query) {
		return deComuneHome.findBySuggestion(query);
	}

	public void sync() {
		try {
			if (dichiarazioneUpload != null) {
				dichiarazione.setExtDocumentoFileName(dichiarazioneUpload.getFileName());
				dichiarazione.setExtDocumentoFile(dichiarazioneUpload.getContents());
				dichiarazione.setExtDocumentoFileMimeType(dichiarazioneUpload.getContentType());
			}
			ygDichiarazioneNeetHome.persistDTO(session.getPrincipalId(), dichiarazione);
		} catch (Exception e) {
			addErrorMessage("generic.error");
		}
	}

	public YgDichiarazioneNeetDTO getDichiarazione() {
		return dichiarazione;
	}

	public void setDichiarazione(YgDichiarazioneNeetDTO dichiarazione) {
		this.dichiarazione = dichiarazione;
	}

	public UploadedFile getDichiarazioneUpload() {
		return dichiarazioneUpload;
	}

	public void setDichiarazioneUpload(UploadedFile dichiarazioneUpload) {
		this.dichiarazioneUpload = dichiarazioneUpload;
	}
}
