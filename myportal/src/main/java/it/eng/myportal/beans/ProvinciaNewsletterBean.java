package it.eng.myportal.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.entity.ejb.InvioNewsletterSync;

@ManagedBean
@ViewScoped
public class ProvinciaNewsletterBean extends AbstractBaseBean {

	private static final String MAIL_INVIATA = "La mail è stata inviata correttamente";
	private static final String ALLEGATO_CARICATO = "Il caricamento degli allegati è avvenuto correttamente";
	private String mailFrom;
	private String mailToCustom;
	private String mailTo;
	private String mailOggetto;
	private String mailCorpo;
	private UploadedFile allegatoUpload;
	private String attachFileName;
	private String fileNames;
	private List<UploadedFile> uploadedFileList;

	@EJB
	private InvioNewsletterSync invioNewsletterSync;

	@Override
	@PostConstruct
	public void postConstruct() {
		// Solo gli utenti provinciali possono accedere a questa pagina.
		if (!session.isProvincia()) {
			redirectHome();
		}

		uploadedFileList = new ArrayList<UploadedFile>();
		fileNames = " ";
		// TODO everything else
	}

	public void uploadAllegatoFile(FileUploadEvent event) {
		allegatoUpload = event.getFile();
		attachFileName = allegatoUpload.getFileName();
		uploadedFileList.add(allegatoUpload);
		fileNames = fileNames + " " + attachFileName;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ALLEGATO_CARICATO));

	}

	public void sendMail() {
		String toAddress = mailToCustom;
		String[] toArray = toAddress.split(";+");

		List<String> listToAddress = Arrays.asList(toArray);

		List<String> listCcnAddress = new ArrayList<String>();
		if (mailTo != null) {
			String[] ccAdressArray = mailTo.split(";+");
			listCcnAddress = Arrays.asList(ccAdressArray);
		}

		MailVO mail = new MailVO();

		mail.setFrom(mailFrom);
		mail.setSubject(mailOggetto);
		if (mailCorpo == null) {
			mailCorpo = "";
		}

		mail.setBody(mailCorpo);
		mail.setCCNList(listCcnAddress);
		mail.setToList(listToAddress);
		mail.setUploadedFileList(uploadedFileList);

		try {

			invioNewsletterSync.sendMail(mail);
			log.info("- Mail inviata: {\n" + mail.toString() + "}");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MAIL_INVIATA));
		} catch (AddressException e) {
			log.error("Indirizzo mail errato: " + e.getMessage(), e);
			log.error("- Errore nell'invio della mail: {\n" + mail.toString() + "}");
			addErrorMessage("mail.address.error");
		} catch (MessagingException e) {
			log.error("Errore nel corpo della mail: " + e.getMessage(), e);
			log.error("- Errore nell'invio della mail: {\n" + mail.toString() + "}");
			addErrorMessage("mail.server.error");
		} catch (NamingException e) {
			log.error(e.getMessage(), e);
			log.error("- Errore nell'invio della mail: {\n" + mail.toString() + "}");
		}

	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailToCustom() {
		return mailToCustom;
	}

	public void setMailToCustom(String mailToCustom) {
		this.mailToCustom = mailToCustom;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getMailOggetto() {
		return mailOggetto;
	}

	public void setMailOggetto(String mailOggetto) {
		this.mailOggetto = mailOggetto;
	}

	public String getMailCorpo() {
		return mailCorpo;
	}

	public void setMailCorpo(String mailCorpo) {
		this.mailCorpo = mailCorpo;
	}

	public UploadedFile getAllegatoUpload() {
		return allegatoUpload;
	}

	public void setAllegatoUpload(UploadedFile allegatoUpload) {
		this.allegatoUpload = allegatoUpload;
	}

	public String getAttachFileName() {
		return attachFileName;
	}

	public void setAttachFileName(String attachFileName) {
		this.attachFileName = attachFileName;
	}

	public List<UploadedFile> getUploadedFileList() {
		return uploadedFileList;
	}

	public void setUploadedFileList(List<UploadedFile> uploadedFileList) {
		this.uploadedFileList = uploadedFileList;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

}
