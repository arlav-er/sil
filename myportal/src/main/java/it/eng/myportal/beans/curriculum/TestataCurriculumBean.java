package it.eng.myportal.beans.curriculum;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.UtenteHomeBean;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean per inserire la testata di un curriculum.
 * 
 * @author Rodi A., Pasqualicchio P.
 * 
 */
@ManagedBean
@ViewScoped
public class TestataCurriculumBean extends AbstractBaseBean {

	/**
	 * Dati della form.
	 */
	private int numeroCurricula;
	private UtenteCompletoDTO user;
	private DeComuneDTO comuneDomicilio;
	private DeComuneDTO comuneNascita;
	private UploadedFile file;

	@Valid
	protected CvDatiPersonaliDTO data = new CvDatiPersonaliDTO();

	@ManagedProperty(value = "#{utenteHomeBean}")
	private UtenteHomeBean utenteHomeBean;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeProvenienzaHome deProvenienzaHome;

	@EJB
	private DeComuneHome deComuneHome;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// Controllo sul numero massimo di Curriculum Vitae consentito
		user = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		numeroCurricula = utenteHomeBean.getNumeroLiveCurricula();

		// Preimposto i dati che mi ha fornito in fase di registrazione
		data.setEmail(user.getEmail());

		UtenteInfoDTO utenteInfo = utenteInfoHome.findDTOById(user.getId());
		comuneDomicilio = utenteInfo.getComuneDomicilio();
		if (comuneDomicilio != null) {
			data.setCodComDomicilio(comuneDomicilio.getId());
			data.setStrComDomicilio(comuneDomicilio.getDescrizione());
		}
		comuneNascita = utenteInfo.getComuneNascita();
		if (comuneNascita != null) {
			data.setCodComNascita(comuneNascita.getId());
			data.setStrComNascita(comuneNascita.getDescrizione());
		}

		if (ConstantsSingleton.COD_REGIONE == 22) {
			// Vale solo per TRENTO come da mail richiesta a gennaio 2014

			Date dataNascita = utenteInfo.getDataNascita();
			if (dataNascita != null) {
				data.setDtNascita(dataNascita);
			}
			String indirizzoDomicilio = utenteInfo.getIndirizzoDomicilio();
			if (StringUtils.isNotBlank(indirizzoDomicilio)) {
				data.setIndirizzoDomicilio(indirizzoDomicilio);
			}
			String tel1 = utenteInfo.getTelCasa();
			if (StringUtils.isNotBlank(tel1)) {
				data.setTel1(tel1);
			}
			String tel2 = utenteInfo.getCellulare();
			if (StringUtils.isNotBlank(tel2)) {
				data.setTel2(tel2);
			}

		} else {

			if (numeroCurricula > 0) {
				CvDatiPersonaliDTO dto = cvDatiPersonaliHome.findLastCurriculumByIdPfPrincipal(user.getId());

				Date dataNascita = dto.getDtNascita();
				if (dataNascita != null) {
					data.setDtNascita(dataNascita);
				}
				String indirizzoDomicilio = dto.getIndirizzoDomicilio();
				if (StringUtils.isNotBlank(indirizzoDomicilio)) {
					data.setIndirizzoDomicilio(indirizzoDomicilio);
				}
				String tel1 = dto.getTel1();
				if (StringUtils.isNotBlank(tel1)) {
					data.setTel1(tel1);
				}
				String tel2 = dto.getTel2();
				if (StringUtils.isNotBlank(tel2)) {
					data.setTel2(tel2);
				}
				String fax = dto.getFax();
				if (StringUtils.isNotBlank(fax)) {
					data.setFax(fax);
				}
			}
		}
	}

	public List<DeComuneDTO> completeComune(String query) {
		return deComuneHome.findBySuggestion(query);
	}

	public void handleFileUpload(FileUploadEvent event) {
		this.setFile(event.getFile());
	}

	public void uploadFileReset() {
		file = null;
	}

	/**
	 * Salva i dati del CV
	 * 
	 * @return il path relativo dell'edit curriculum
	 */
	public String save() {
		// Controllo che il titolo del CV non contenga riferimenti ai dati personali.
		String erroreTitoloCv = cvDatiPersonaliHome.checkTitoloCv(data, user);
		if (erroreTitoloCv != null && !erroreTitoloCv.trim().isEmpty()) {
			FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, erroreTitoloCv, erroreTitoloCv);
			addMessage(null, errorMessage);
			return ""; // Non vado alla pagina successiva, e non salvo il curriculum
		}

		// Inserisco un CV solo se non ho ancora raggiunto il numero massimo
		if (numeroCurricula < ConstantsSingleton.CVMAXCURR) {
			try {
				data.setIdPfPrincipal(getSession().getConnectedUtente().getId());

				data.setFlagInviato(false);

				/* Setto il tipo di decodifiche in base alle impostazioni del portale */
				if (ConstantsSingleton.usaDecodificheSilPerCurriculum()) {
					data.setOpzTipoDecodifiche(CvDatiPersonali.OpzTipoDecodifiche.SIL);
				} else {
					data.setOpzTipoDecodifiche(CvDatiPersonali.OpzTipoDecodifiche.MINISTERO);
				}

				/* il CV viene inserito dal portale e la provenienza e' MYPORTAL */
				data.setProvenienzaCurriculum(
						deProvenienzaHome.findDTOById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));

				/* il CV viene inserito dal portale e la visibilta' e' true */
				data.setVisibilita(true);
				// all'inizio non sarà ricercabile
				data.setRicercabile(false);
				data.setFlagEliminato(false);
				data.setFlgIdo(false);//qui e` vecchio CV, false per forza

				if (comuneDomicilio != null) {
					data.setCodComDomicilio(comuneDomicilio.getId());
					data.setStrComDomicilio(comuneDomicilio.getDescrizione());
				}

				if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {
					Date now = new Date();
					data.setDtScadenza(DateUtils.addDays(now, 30));
				}

				byte[] contenuto = null;
				if (file != null) {
					data.setFilename(file.getFileName());
					data.setFileNameTmp(file.getFileName());
					contenuto = file.getContents();
				}
				if (comuneNascita != null) {
					data.setCodComNascita(comuneNascita.getId());
					data.setStrComNascita(comuneNascita.getDescrizione());
				}

				// data = homePersist(cvDatiPersonaliHome, data);
				data = cvDatiPersonaliHome.newPersistDTO(data, getSession().getPrincipalId(), contenuto);
			} catch (MyPortalException e) {
				addErrorMessage(e.getCodErrore(), e);
				return "";
			} catch (EJBException e) {
				addErrorMessage("data.error_saving", e);
				return "";
			}

			String outcome = "edit?faces-redirect=true&id=" + data.getId() + "&backTo=" + ConstantsSingleton.BASE_URL
					+ ConstantsSingleton.HOME_UTENTE;

			return outcome;
		} else {
			redirectGrave("generic.manipulation_error");
			return "";
		}
	}

	/**
	 * Dato che sto inserendo un nuovo curriculum, decido se usare le decodifiche SIL in base alle impostazioni generali
	 * del portale.
	 */
	public boolean usaDecodificheSil() {
		return ConstantsSingleton.usaDecodificheSilPerCurriculum();
	}

	public UtenteHomeBean getUtenteHomeBean() {
		return utenteHomeBean;
	}

	public void setUtenteHomeBean(UtenteHomeBean utenteHomeBean) {
		this.utenteHomeBean = utenteHomeBean;
	}

	public CvDatiPersonaliDTO getData() {
		return data;
	}

	public void setData(CvDatiPersonaliDTO data) {
		this.data = data;
	}

	public DeComuneDTO getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(DeComuneDTO comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public DeComuneDTO getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(DeComuneDTO comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}