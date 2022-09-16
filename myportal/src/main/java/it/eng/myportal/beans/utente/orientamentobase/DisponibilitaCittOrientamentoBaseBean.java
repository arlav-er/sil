package it.eng.myportal.beans.utente.orientamentobase;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.rest.yg.PrenotaAppuntamento;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta.ElencoDisponibilita.DatiAppuntamento;
import it.eng.myportal.utils.Utils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.datatype.XMLGregorianCalendar;

@ManagedBean
@ViewScoped
public class DisponibilitaCittOrientamentoBaseBean extends AbstractBaseBean {

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private PrenotaAppuntamento prenotaAppuntamentoHome;

	private String codProvincia;
	private String codiceFiscale;
	private String cellulare;
	private Date dataDal;
	private Date dataAl;
	private Date dataCorrentePerSlot;
	private String mattinaOPomeriggio;
	private String codCpiRiferimento;
	private Integer codSportelloDistaccato;
	private List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili = new ArrayList<Risposta.ElencoDisponibilita.DatiAppuntamento>();
	private BigInteger identificativoSlot;
	private DeCpiDTO deCpiDTO;
	private Boolean nextSlot;
	private UtenteCompletoDTO utente;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		utente = utenteInfoHome.findDTOCompletoById(getSession().getPrincipalId());
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
			deCpiDTO = deCpiHome.findDTOById(codCpiRiferimento);
			String codSportelloDistaccatoString = getRequestParameter("codSportelloDistaccato");
			if (codSportelloDistaccatoString != null) {
				codSportelloDistaccato = Integer.parseInt(codSportelloDistaccatoString);
			}
		} catch (ParseException e) {
			FacesMessage error = new FacesMessage("Errore di manipolazione dei dati.");
			error.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, error);
		}

		callDispAppuntamento(dataDal);
	}

	private void callDispAppuntamento(Date data) {
		try {
			slotDisponibili = agAppuntamentoHome.disponibilitaAppuntamentoOrientamentoBase(data, dataAl,
					mattinaOPomeriggio, codProvincia, codCpiRiferimento, codSportelloDistaccato);
		} catch (Exception e) {
			FacesMessage error = new FacesMessage("Il servizio di ricerca disponibilità appuntamento"
					+ " non è disponibile.");
			error.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, error);
			return;
		}

		/* ordino le liste di slot per orario */
		DisponibilitaCittOrientamentoBaseBean.SlotComparator slotComparator = new SlotComparator();
		Collections.sort(slotDisponibili, slotComparator);

		/* seleziono la prima data disponibile degli slot */
		if (slotDisponibili != null && slotDisponibili.size() >= 1) {
			/* data dell'appuntamento */
			dataCorrentePerSlot = slotDisponibili.get(0).getDataAppuntamento().toGregorianCalendar().getTime();

			/* controllo la presenza di slot successivi */
			if (dataAl == null) {
				nextSlot = true;
			} else if (dataCorrentePerSlot.before(dataAl)) {
				nextSlot = true;
			}
		} else {
			nextSlot = false;
		}
	}

	public Date getDataCorrentePerSlot() {
		return dataCorrentePerSlot;
	}

	public void setDataCorrentePerSlot(Date dataCorrentePerSlot) {
		this.dataCorrentePerSlot = dataCorrentePerSlot;
	}

	public String getMattinaOPomeriggio() {
		return mattinaOPomeriggio;
	}

	public void setMattinaOPomeriggio(String mattinaOPomeriggio) {
		this.mattinaOPomeriggio = mattinaOPomeriggio;
	}

	public Integer getCodSportelloDistaccato() {
		return codSportelloDistaccato;
	}

	public void setCodSportelloDistaccato(Integer codSportelloDistaccato) {
		this.codSportelloDistaccato = codSportelloDistaccato;
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> getSlotDisponibili() {
		return slotDisponibili;
	}

	public void setSlotDisponibili(List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili) {
		this.slotDisponibili = slotDisponibili;
	}

	public BigInteger getIdentificativoSlot() {
		return identificativoSlot;
	}

	public void setIdentificativoSlot(BigInteger identificativoSlot) {
		this.identificativoSlot = identificativoSlot;
	}

	public void slotValuechange(BigInteger identificativoSlot) {
		this.identificativoSlot = identificativoSlot;
	}

	public DeCpiDTO getDeCpiDTO() {
		return deCpiDTO;
	}

	public void setDeCpiDTO(DeCpiDTO deCpiDTO) {
		this.deCpiDTO = deCpiDTO;
	}

	public Boolean getNextSlot() {
		return nextSlot;
	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public void setNextSlot(Boolean nextSlot) {
		this.nextSlot = nextSlot;
	}

	public Date XMLGregorianCalendarToDate(XMLGregorianCalendar cal) {
		return cal.toGregorianCalendar().getTime();
	}

	public String prenotaAppuntamento() {

		if (identificativoSlot == null) {
			FacesMessage error = new FacesMessage(
					"Prima di poter prenotare un appuntamento è necessario selezionare l'appuntamento desiderato");
			error.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, error);
			return "";
		}

		PrenotaAppuntamento.RispostaFissaAppuntamwento rispostaFissaAppuntamento = callFissaAppuntamento();

		String redirectUrl = getRedirectUrl(rispostaFissaAppuntamento);
		redirectUrl += "&faces-redirect=true";
		return redirectUrl;
	}

	private PrenotaAppuntamento.RispostaFissaAppuntamwento callFissaAppuntamento() {
		boolean consensoSMS = utente.getCellulare() != null ? true : false;
		boolean invioEmail = true;

		PrenotaAppuntamento.RispostaFissaAppuntamwento rispostaFissaAppuntamento = prenotaAppuntamentoHome
				.fissaAppuntamentoOrientamentoOrdinario(session.getPrincipalId(), utente, dataDal, dataAl,
						mattinaOPomeriggio, identificativoSlot, consensoSMS, invioEmail, codProvincia,
						codCpiRiferimento, TipoAppuntamentoEnum.PL_APPOB);

		return rispostaFissaAppuntamento;
	}

	public void giornoSuccessivo() {
		/*
		 * solo se la data corrente e' precedente a quella finale
		 */
		if (dataAl != null && !dataCorrentePerSlot.before(dataAl)) {
			return;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(dataCorrentePerSlot);
		c.add(Calendar.DATE, 1);
		callDispAppuntamento(c.getTime());

		/* deseleziono l'eventuale slot selezionato */
		identificativoSlot = null;

		if (dataAl == null) {
			nextSlot = true;
		} else if (slotDisponibili == null || slotDisponibili.isEmpty() || !dataCorrentePerSlot.before(dataAl)) {
			nextSlot = false;
		} else {
			nextSlot = true;
		}
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

	/**
	 * Classe Comparator per ordinare gli slot secondo l'orario
	 * 
	 * @author enrico
	 *
	 */
	private class SlotComparator implements Comparator<Risposta.ElencoDisponibilita.DatiAppuntamento> {

		/**
		 * Ordina gli appuntamenti per orario. L'orario si presuppone nel formato HH:MM, se c'e' un qualsiasi errore il
		 * primo appuntamento viene ordinato prima del secondo.
		 */
		@Override
		public int compare(DatiAppuntamento a1, DatiAppuntamento a2) {
			try {
				String a1OreStr, a1MinutiStr, a2OreStr, a2MinutiStr;
				Integer a1Ore, a1Minuti, a2Ore, a2Minuti;
				String[] a1Split = a1.getOraAppuntamento().split(":");
				String[] a2Split = a2.getOraAppuntamento().split(":");
				a1OreStr = a1Split[0];
				a1MinutiStr = a1Split[1];
				a2OreStr = a2Split[0];
				a2MinutiStr = a2Split[1];
				a1Ore = Integer.parseInt(a1OreStr);
				a1Minuti = Integer.parseInt(a1MinutiStr);
				a2Ore = Integer.parseInt(a2OreStr);
				a2Minuti = Integer.parseInt(a2MinutiStr);

				if (a1Ore.compareTo(a2Ore) == 0) {
					return a1Minuti.compareTo(a2Minuti);
				} else {
					return a1Ore.compareTo(a2Ore);
				}
			} catch (Exception e) {
				return -1;
			}
		}
	}

	private String getRedirectUrl(PrenotaAppuntamento.RispostaFissaAppuntamwento rispostaFissaAppuntamento) {
		String status = "00".equals(rispostaFissaAppuntamento.getRisposta().getEsito().getCodice()) ? "success"
				: "error";
		/* prendo il messaggio da visualizzare secondo la mappatura */
		boolean checkVisibilitaPeriodoDate = true;
		String msg = Utils.getErrorMessageFissaAppuntamento(rispostaFissaAppuntamento.getRisposta().getEsito()
				.getCodice(), rispostaFissaAppuntamento.getRisposta().getEsito().getDescrizione(),
				checkVisibilitaPeriodoDate);

		/* redirect in pagina di esito */
		StringBuilder sb = new StringBuilder("/secure/utente/orientamento_base/esito_appuntamento.xhtml");
		sb.append("?status=" + status);
		if (!status.equals("success")) {
			sb.append("&msg=" + msg);
		}
		Integer idAgAppuntamento = rispostaFissaAppuntamento.getIdAgAppuntamento();
		Integer idAgAppAnagrafica = rispostaFissaAppuntamento.getIdAgAppAnagrafica();
		if (idAgAppuntamento != null) {
			sb.append("&idAgAppuntamento=" + idAgAppuntamento);
		}
		if (idAgAppAnagrafica != null) {
			sb.append("&idAgAppAnagrafica=" + idAgAppAnagrafica);
		}
		setParametriRicercaLavoratore(sb);
		return sb.toString();
	}
}
