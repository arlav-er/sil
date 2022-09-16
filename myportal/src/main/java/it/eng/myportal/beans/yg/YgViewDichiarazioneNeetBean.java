package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.YgDichiarazioneNeetDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.YgDichiarazioneNeetHome;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class YgViewDichiarazioneNeetBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(YgViewDichiarazioneNeetBean.class);

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	YgDichiarazioneNeetHome ygDichiarazioneNeetHome;

	private Integer dichiarazioneId;
	private YgDichiarazioneNeetDTO dichiarazione;

	@PostConstruct
	public void postConstruct() {
		String dichiarazioneIdParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("idDichiarazione");
		try {
			Integer dichiarazioneId = Integer.parseInt(dichiarazioneIdParam);
			dichiarazione = ygDichiarazioneNeetHome.findDTObyId(dichiarazioneId);
		} catch (Exception e) {
			addErrorMessage("data.error_loading");
		}
	}

	public void documentoIdentitaUploaded(FileUploadEvent event) {
		dichiarazione.setExtDocumentoFileName(event.getFile().getFileName());
		dichiarazione.setExtDocumentoFileMimeType(event.getFile().getContentType());
		dichiarazione.setExtDocumentoFile(event.getFile().getContents());
		ygDichiarazioneNeetHome.mergeDTO(session.getPrincipalId(), dichiarazione);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Operazione completata: il documento è stato caricato."));
	}

	public void dichiarazioneFirmataUploaded(FileUploadEvent event) {
		dichiarazione.setExtUploadNeetFileMimeType(event.getFile().getContentType());
		dichiarazione.setExtUploadNeetFile(event.getFile().getContents());
		dichiarazione.setExtUploadNeetFileName(event.getFile().getFileName());
		ygDichiarazioneNeetHome.mergeDTO(session.getPrincipalId(), dichiarazione);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Operazione completata: il documento è stato caricato."));
	}

	public void cancellaDichiarazione() {
		if (dichiarazione != null && dichiarazione.getId() != null) {
			dichiarazione.setFlgCancellata(true);
			ygDichiarazioneNeetHome.mergeDTO(session.getPrincipalId(), dichiarazione);
		}
	}

	public StreamedContent getDocumentoIdentita() {
		return new DefaultStreamedContent(new ByteArrayInputStream(dichiarazione.getExtDocumentoFile()),
				dichiarazione.getExtDocumentoFileMimeType(), dichiarazione.getExtDocumentoFileName());
	}

	public StreamedContent getDichiarazioneFirmata() {
		return new DefaultStreamedContent(new ByteArrayInputStream(dichiarazione.getExtUploadNeetFile()),
				dichiarazione.getExtUploadNeetFileMimeType(), dichiarazione.getExtUploadNeetFileName());
	}

	public StreamedContent getStampaPdf() {
		return ygDichiarazioneNeetHome.createStampaPdfFile(dichiarazione.getId());
	}

	public YgDichiarazioneNeetDTO getDichiarazione() {
		return dichiarazione;
	}

	public void setDichiarazione(YgDichiarazioneNeetDTO dichiarazione) {
		this.dichiarazione = dichiarazione;
	}

	public Integer getDichiarazioneId() {
		return dichiarazioneId;
	}

	public void setDichiarazioneId(Integer dichiarazioneId) {
		this.dichiarazioneId = dichiarazioneId;
	}
}
