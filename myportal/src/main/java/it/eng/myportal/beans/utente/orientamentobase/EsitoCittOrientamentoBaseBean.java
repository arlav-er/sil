package it.eng.myportal.beans.utente.orientamentobase;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.exception.MyPortalException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class EsitoCittOrientamentoBaseBean extends AbstractBaseBean {
	// dati per rieffettuare la ricerca
	private String codProvincia;
	private String codiceFiscale;
	private String cellulare;
	private Date dataDal;
	private Date dataAl;
	private String mattinaOPomeriggio;
	private String codCpiRiferimento;
	private Integer codSportelloDistaccato;
	//
	private Integer idAgAppuntamento = null;
	private AppuntamentoDTO appuntamentoDTO;
	private boolean success = false;
	private String msg = null;
	private StreamedContent promemoriaPdfFile;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		readParameters();
	}

	private void readParameters() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			codProvincia = getRequestParameter("codProvincia");
			codiceFiscale = getRequestParameter("codiceFiscale");
			cellulare = getRequestParameter("cellulare");

			String dataDalString = getRequestParameter("dataDal");
			if (dataDalString != null) {
				dataDal = sdf.parse(dataDalString);
			}
			String dataAlString = getRequestParameter("dataAl");
			if (dataAlString != null) {
				dataAl = sdf.parse(dataAlString);
			}
			mattinaOPomeriggio = getRequestParameter("mattinaOPomeriggio");
			codCpiRiferimento = getRequestParameter("codCpiRiferimento");
			String codSportelloDistaccatoString = getRequestParameter("codSportelloDistaccato");
			if (codSportelloDistaccatoString != null) {
				codSportelloDistaccato = Integer.parseInt(codSportelloDistaccatoString);
			}
			String idAgAppuntamentoString = getRequestParameter("idAgAppuntamento");
			if (idAgAppuntamentoString != null) {
				idAgAppuntamento = Integer.parseInt(idAgAppuntamentoString);
			}
			String status = getRequestParameter("status");
			msg = getRequestParameter("msg");
			if ("success".equals(status)) {
				success = true;
			}
			if (idAgAppuntamento != null) {
				appuntamentoDTO = agAppuntamentoHome.findAppuntamentoDTObyIdAgAppuntamento(idAgAppuntamento);
			} else if (success) {
				// Non posso avere successo e gli id vuoti!
				FacesMessage error = new FacesMessage("Errore di manipolazione dei dati.");
				error.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, error);
			}
		} catch (ParseException e) {
			FacesMessage error = new FacesMessage("Errore di manipolazione dei dati.");
			error.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, error);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public AppuntamentoDTO getAppuntamentoDTO() {
		return appuntamentoDTO;
	}

	public void setAppuntamentoDTO(AppuntamentoDTO appuntamentoDTO) {
		this.appuntamentoDTO = appuntamentoDTO;
	}

	public StreamedContent getPromemoriaPdfFile() {
		if (success) {
			try {
				promemoriaPdfFile = agAppuntamentoHome.createPromemoriaPdfFile(idAgAppuntamento);
			} catch (MyPortalException e) {
				addErrorMessage("pdf.error");
			}
		}
		return promemoriaPdfFile;
	}

	public void setPromemoriaPdfFile(StreamedContent promemoriaPdfFile) {
		this.promemoriaPdfFile = promemoriaPdfFile;
	}

	public String nuovaRicerca() {
		return "/secure/patronato/appuntamento/prenota_appuntamento.xhtml?faces-redirect=true";
	}

	public String nuovaRicercaStessoLav() {
		/* outcome per il redirect */
		StringBuilder sb = new StringBuilder();
		sb.append("/secure/patronato/appuntamento/prenota_appuntamento.xhtml?faces-redirect=true");
		setParametriRicercaLavoratore(sb);

		return sb.toString();
	}

	private StringBuilder setParametriRicercaLavoratore(StringBuilder sb) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		if (codProvincia != null) {
			sb.append("&codProvincia=" + codProvincia);
		}
		if (codiceFiscale != null) {
			sb.append("&codiceFiscale=" + codiceFiscale);
		}
		if (cellulare != null) {
			sb.append("&cellulare=" + cellulare);
		}
		if (dataDal != null) {
			sb.append("&dataDal=" + sdf.format(dataDal));
		}
		if (dataAl != null) {
			sb.append("&dataAl=" + sdf.format(dataAl));
		}
		if (mattinaOPomeriggio != null) {
			sb.append("&mattinaOPomeriggio=" + mattinaOPomeriggio);
		}
		if (codCpiRiferimento != null) {
			sb.append("&codCpiRiferimento=" + codCpiRiferimento);
		}
		if (codSportelloDistaccato != null) {
			sb.append("&codSportelloDistaccato=" + codSportelloDistaccato);
		}

		return sb;
	}

	public void tornaAScrivania() {
		redirectHome();
	}
}
