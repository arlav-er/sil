package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeAmbienteSilDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.YgImpostazioni;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgImpostazioniHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAppuntamentoHome;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta;
import it.eng.myportal.utils.ConstantsSingleton;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.datatype.XMLGregorianCalendar;

@ManagedBean
@ViewScoped
public class YgAppuntamentoBean extends AbstractBaseBean {

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private DeRegioneHome deRegioneHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeAmbienteSilHome deAmbienteSilHome;

	@EJB
	private YgImpostazioniHome ygImpostazioniHome;

	@EJB
	private DeTipoAppuntamentoHome deTipoAppuntamentoHome;

	private Integer idYgAdesione;
	private Date dataDa;
	private Date dataA;
	private String mattinaPomeriggio;
	private UtenteInfoDTO utenteInfoDTO;
	private String codProvincia;
	private String descrizioneProvincia;
	private YgAdesioneDTO ygAdesioneDTO;
	private String codCpi;
	private String descrizioneCpi;
	private boolean editProvincia;
	private boolean editCellulare;
	private boolean editCpi;
	private List<SelectItem> provinciaList;
	private List<SelectItem> cpiList;
	private String consensoSms;
	private String cellulare;
	private String tipoAppuntamento;
	private String strDataDa;
	private String strDataA;
	private Integer idAmbienteSil;
	private String descrizioneAmbienteSil;
	private List<SelectItem> ambienteSilList;

	// elenco degli slot disponibili alla data corrente
	private List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili = new ArrayList<Risposta.ElencoDisponibilita.DatiAppuntamento>();
	// data corrente degli slot visualizzati
	private Date dataCorrentePerSlot = null;
	private String strDenominazioneCPI;
	private Boolean viewMsgTerni;
	private Boolean nextSlot; // true se esistono altri slot successivi
	private BigInteger identificativoSlot; // lo slot selezionato dall'utente

	/* impostazioni relative a YG */
	private YgImpostazioni ygImpostazioni;

	public Integer getIdYgAdesione() {
		return idYgAdesione;
	}

	public void setIdYgAdesione(Integer idYgAdesione) {
		this.idYgAdesione = idYgAdesione;
	}

	public Date getDataDa() {
		return dataDa;
	}

	public void setDataDa(Date dataDa) {
		this.dataDa = dataDa;
	}

	public Date getDataA() {
		return dataA;
	}

	public void setDataA(Date dataA) {
		this.dataA = dataA;
	}

	public String getMattinaPomeriggio() {
		return mattinaPomeriggio;
	}

	public void setMattinaPomeriggio(String mattinaPomeriggio) {
		this.mattinaPomeriggio = mattinaPomeriggio;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getDescrizioneProvincia() {
		return descrizioneProvincia;
	}

	public void setDescrizioneProvincia(String descrizioneProvincia) {
		this.descrizioneProvincia = descrizioneProvincia;
	}

	public String getCodCpi() {
		return codCpi;
	}

	public void setCodCpi(String codCpi) {
		this.codCpi = codCpi;
	}

	public String getDescrizioneCpi() {
		return descrizioneCpi;
	}

	public void setDescrizioneCpi(String descrizioneCpi) {
		this.descrizioneCpi = descrizioneCpi;
	}

	public boolean isEditProvincia() {
		return editProvincia;
	}

	public void setEditProvincia(boolean editProvincia) {
		this.editProvincia = editProvincia;
	}

	public boolean isEditCellulare() {
		return editCellulare;
	}

	public void setEditCellulare(boolean editCellulare) {
		this.editCellulare = editCellulare;
	}

	public boolean isEditCpi() {
		return editCpi;
	}

	public void setEditCpi(boolean editCpi) {
		this.editCpi = editCpi;
	}

	public List<SelectItem> getProvinciaList() {
		return provinciaList;
	}

	public void setProvinciaList(List<SelectItem> provinciaList) {
		this.provinciaList = provinciaList;
	}

	public List<SelectItem> getCpiList() {
		return cpiList;
	}

	public void setCpiList(List<SelectItem> cpiList) {
		this.cpiList = cpiList;
	}

	public String getConsensoSms() {
		return consensoSms;
	}

	public void setConsensoSms(String consensoSms) {
		this.consensoSms = consensoSms;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public String getTipoAppuntamento() {
		return tipoAppuntamento;
	}

	public void setTipoAppuntamento(String tipoAppuntamento) {
		this.tipoAppuntamento = tipoAppuntamento;
	}

	public String getStrDataDa() {
		return strDataDa;
	}

	public void setStrDataDa(String strDataDa) {
		this.strDataDa = strDataDa;
	}

	public String getStrDataA() {
		return strDataA;
	}

	public void setStrDataA(String strDataA) {
		this.strDataA = strDataA;
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> getSlotDisponibili() {
		return slotDisponibili;
	}

	public void setSlotDisponibili(List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili) {
		this.slotDisponibili = slotDisponibili;
	}

	public Date getDataCorrentePerSlot() {
		return dataCorrentePerSlot;
	}

	public void setDataCorrentePerSlot(Date dataCorrentePerSlot) {
		this.dataCorrentePerSlot = dataCorrentePerSlot;
	}

	public Boolean getNextSlot() {
		return nextSlot;
	}

	public void setNextSlot(Boolean nextSlot) {
		this.nextSlot = nextSlot;
	}

	public BigInteger getIdentificativoSlot() {
		return identificativoSlot;
	}

	public void setIdentificativoSlot(BigInteger identificativoSlot) {
		this.identificativoSlot = identificativoSlot;
	}

	public Integer getIdAmbienteSil() {
		return idAmbienteSil;
	}

	public void setIdAmbienteSil(Integer idAmbienteSil) {
		this.idAmbienteSil = idAmbienteSil;
	}

	public String getDescrizioneAmbienteSil() {
		return descrizioneAmbienteSil;
	}

	public void setDescrizioneAmbienteSil(String descrizioneAmbienteSil) {
		this.descrizioneAmbienteSil = descrizioneAmbienteSil;
	}

	public List<SelectItem> getAmbienteSilList() {
		return ambienteSilList;
	}

	public void setAmbienteSilList(List<SelectItem> ambienteSilList) {
		this.ambienteSilList = ambienteSilList;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		ygImpostazioni = ygImpostazioniHome.findByCodRegione("" + ConstantsSingleton.COD_REGIONE);
		tipoAppuntamento = deTipoAppuntamentoHome.findById(TipoAppuntamentoEnum.PL_APPGG.getCodTipoAppuntamento())
				.getDescrizione();

		/*
		 * controllo se ho i dati nella request
		 */
		readRequestParameters();

		if (getRequestParameter("dispAppuntamento") != null) {
			// FASE DI VERIFICA SLOT

			/* Verifica disponibilita' dell 'appuntamento */
			callDispAppuntamento(dataDa);
		} else if (getRequestParameter("esito") != null) {
			// FASE DI PRENOTAZIONE SLOT
		} else {
			// FASE DI COMPILAZIONE DATI

			/* compilo i dati del lavoratore secondo quello che ho su DB */
			getDatiUtente();

			/*
			 * Carmela 2014 07 02: CPI sempre editabile
			 */
			editCpi = true;

			provinciaList = deRegioneHome.getProvinceListItems(String.valueOf(ConstantsSingleton.COD_REGIONE), true);

			ambienteSilList = deAmbienteSilHome.getListaSelectItem(codCpi);

			popolaCpiRiferimentoList(codProvincia);

			/*
			 * se il consenso non mi e' stato passato come parametro prendo quello su DB
			 */
			if (consensoSms == null) {
				if (session.getConnectedUtente() != null) {
					UtenteInfoDTO utenteInfoDTO = utenteInfoHome.findDTOById(session.getConnectedUtente().getId());
					if (utenteInfoDTO.getFlgConsensoSms() != null) {
						consensoSms = utenteInfoDTO.getFlgConsensoSms() ? "S" : "N";
					} else {
						consensoSms = "N";
					}
				} else {
					consensoSms = "N";
				}
			}
		}
	}

	private void getDatiUtente() {
		/*
		 * se ho gia' il numero di cell uso quello (non modificabile), altrimenti deve essere inserito
		 */
		utenteInfoDTO = utenteInfoHome.findDTOById(getSession().getPrincipalId());
		cellulare = utenteInfoDTO.getCellulare();
		if (cellulare == null || cellulare.isEmpty()) {
			editCellulare = true;
		}

		/* seleziono la provincia di riferimento se possibile o la richiedo */
		if (utenteInfoDTO.getProvinciaRiferimento() == null || utenteInfoDTO.getProvinciaRiferimento().getId() == null) {
			editProvincia = true;
			utenteInfoDTO.setProvinciaRiferimento(new DeProvinciaDTO());
		} else if (utenteInfoDTO.getProvinciaRiferimento() != null
				&& !ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(
						utenteInfoDTO.getProvinciaRiferimento().getIdRegione())) {
			editProvincia = true;
			utenteInfoDTO.setProvinciaRiferimento(new DeProvinciaDTO());
		} else {
			editProvincia = false;
			codProvincia = utenteInfoDTO.getProvinciaRiferimento().getId();
			descrizioneProvincia = utenteInfoDTO.getProvinciaRiferimento().getDescrizione();
		}

		/*
		 * ottengo l'adesione di riferimento (sempre l'ultima, l'unica che dovrebbe essere attiva)
		 */
		ygAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(getSession().getPrincipalId());
		if (ygAdesioneDTO != null) {
			idYgAdesione = ygAdesioneDTO.getId();
		}

		/* seleziono il cpi di riferimento se possibile o la richiedo */
		if (ygAdesioneDTO.getDeCpiAssegnazione() == null || ygAdesioneDTO.getDeCpiAssegnazione().getId() == null) {
			editCpi = true;
		} else {
			String codProvinciaCpiRiferimento = ygAdesioneDTO.getDeCpiAssegnazione().getCodProvincia();
			if (codProvinciaCpiRiferimento != null && !codProvinciaCpiRiferimento.isEmpty() && codProvincia != null) {
				if (!codProvincia.equals(codProvinciaCpiRiferimento)) {
					/*
					 * se il CPI selezionato non e' nella provincia di riferimento ne devo selezionare un altro
					 */
					editCpi = true;
				} else {
					editCpi = false;
					codCpi = ygAdesioneDTO.getDeCpiAssegnazione().getId();
					descrizioneCpi = ygAdesioneDTO.getDeCpiAssegnazione().getDescrizione().toUpperCase() + " - "
							+ ygAdesioneDTO.getDeCpiAssegnazione().getIndirizzo();
				}
			} else {
				editCpi = false;
				codCpi = ygAdesioneDTO.getDeCpiAssegnazione().getId();
				descrizioneCpi = ygAdesioneDTO.getDeCpiAssegnazione().getDescrizione().toUpperCase() + " - "
						+ ygAdesioneDTO.getDeCpiAssegnazione().getIndirizzo();
			}
		}
	}

	private void readRequestParameters() {
		String strIdYgAdesione = getRequestParameter("idYgAdesione");
		if (strIdYgAdesione != null && !strIdYgAdesione.isEmpty()) {
			try {
				idYgAdesione = Integer.parseInt(strIdYgAdesione);
			} catch (NumberFormatException e) {
				log.error(e);
			}
		}
		String strIdentificativoSlot = getRequestParameter("identificativoSlot");
		if (strIdentificativoSlot != null && !strIdentificativoSlot.isEmpty()) {
			try {
				identificativoSlot = new BigInteger(strIdentificativoSlot);
			} catch (NumberFormatException e) {
				log.error(e);
			}
		}

		String strAmbiente = getRequestParameter("ambiente");
		if (strAmbiente != null && !strAmbiente.isEmpty()) {
			try {
				idAmbienteSil = Integer.parseInt(strAmbiente);
			} catch (NumberFormatException e) {
				log.error(e);
			}
		}

		/*
		 * Se decommentata questa parte fa si che tornando indietro nella ricerca i campi si ripopolino con le scelte
		 * precedenti.
		 */
		// strDataDa = getRequestParameter("dataDa");
		// if (strDataDa != null) {
		// try {
		// dataDa = sdf.parse(strDataDa);
		// } catch (ParseException e) {
		// log.error(e);
		// }
		// }
		// strDataA = getRequestParameter("dataA");
		// if (strDataA != null) {
		// try {
		// dataA = sdf.parse(strDataA);
		// } catch (ParseException e) {
		// log.error(e);
		// }
		// }
		// String strMattinaPomeriggio =
		// getRequestParameter("mattinaPomeriggio");
		// if (strMattinaPomeriggio != null && !strMattinaPomeriggio.isEmpty())
		// {
		// mattinaPomeriggio = strMattinaPomeriggio;
		// } else {
		// mattinaPomeriggio = "I";
		// }
		// String strCodProvincia = getRequestParameter("provinciaRiferimento");
		// if (strCodProvincia != null && !strCodProvincia.isEmpty()) {
		// codProvincia = strCodProvincia;
		// }
		// String strCodCpi = getRequestParameter("cpiRiferimento");
		// if (strCodCpi != null && !strCodCpi.isEmpty()) {
		// codCpi = strCodCpi;
		// }
		// String strAmbiente = getRequestParameter("ambiente");
		// if (strAmbiente != null && !strAmbiente.isEmpty()) {
		// try {
		// idAmbienteSil = Integer.parseInt(strAmbiente);
		// } catch (NumberFormatException e) {
		// log.error(e);
		// }
		// }
		// String strCellulare = getRequestParameter("cellulare");
		// if (strCellulare != null && !strCellulare.isEmpty()) {
		// cellulare = strCellulare;
		// }
		// String strConsensoSms = getRequestParameter("consenso");
		// if (strConsensoSms != null && !strConsensoSms.isEmpty()) {
		// consensoSms = strConsensoSms;
		// }
	}

	private void popolaCpiRiferimentoList(String codProvincia) {
		if (codProvincia != null && !codProvincia.isEmpty()) {
			DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(codProvincia);
			if (deProvinciaDTO != null
					&& ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(deProvinciaDTO.getIdRegione())) {
				cpiList = deCpiHome.getListItemsCpiByProvincia(codProvincia, true);
			} else {
				cpiList = new ArrayList<SelectItem>();
			}
		} else {
			cpiList = new ArrayList<SelectItem>();
		}
	}

	public void onChangeProvinciaRiferimento() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String codProvincia = map.get("prenota_appuntamento_form:provincia_riferimento:combobox");
		DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(codProvincia);

		popolaCpiRiferimentoList(deProvinciaDTO.getId());
		codCpi = "";
		descrizioneCpi = "";
	}

	/* compongo la descrizione del CPI di riferimento da visualizzare */
	public void onChangeCpiRiferimento() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String codCpi = map.get("prenota_appuntamento_form:cpi_riferimento:combobox");
		DeCpiDTO deCpiDTO = deCpiHome.findDTOById(codCpi);

		descrizioneCpi = deCpiDTO.getDescrizione().toUpperCase() + " - " + deCpiDTO.getIndirizzo();

		ambienteSilList = deAmbienteSilHome.getListaSelectItem(codCpi);
	}

	public void onChangeAmbiente() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		Integer idAmbienteSil = Integer.parseInt(map.get("prenota_appuntamento_form:ambiente:combobox"));
		DeAmbienteSilDTO deAmbienteSilDTO = deAmbienteSilHome.findDTOById(idAmbienteSil);

		descrizioneAmbienteSil = deAmbienteSilDTO.getDescrizione();
	}

	/**
	 * Prepara i dati degli slot disponibili ricevuti dal WS per la visualizzazione
	 */
	private void callDispAppuntamento(Date data) {
		/* I significa che e' indifferente, ma il WS vuole M, P o null */
		if (mattinaPomeriggio != null && mattinaPomeriggio.equals("I")) {
			mattinaPomeriggio = null;
		}
		List<Risposta.ElencoDisponibilita.DatiAppuntamento> result = agAppuntamentoHome.disponibilitaAppuntamentoYG(
				session.getPrincipalId(), idYgAdesione, data, dataA, mattinaPomeriggio, idAmbienteSil);

		slotDisponibili = result;

		UtenteInfo utente = utenteInfoHome.findById(session.getPrincipalId());
		String codProvinciaServAmm = utente.getDeProvincia().getCodProvincia();
		if (("55").equalsIgnoreCase(codProvinciaServAmm)) {
			viewMsgTerni = true;
		} else {
			viewMsgTerni = false;
		}

		/* ordino le liste di slot per orario */
		SlotComparator slotComparator = new SlotComparator();
		Collections.sort(slotDisponibili, slotComparator);

		/* seleziono la prima data disponibile degli slot */
		if (slotDisponibili != null && slotDisponibili.size() >= 1) {
			/* data dell'appuntamento */
			dataCorrentePerSlot = slotDisponibili.get(0).getDataAppuntamento().toGregorianCalendar().getTime();
			strDenominazioneCPI = " CPI di " + slotDisponibili.get(0).getDenominazioneCPI() + "<br />" + "<br />"
					+ slotDisponibili.get(0).getIndirizzoCPIstampa() + "<br />";
			/* controllo la presenza di slot successivi */
			if (dataA == null) {
				nextSlot = true;
			} else if (dataCorrentePerSlot.before(dataA)) {
				nextSlot = true;
			}
		} else {
			nextSlot = false;
		}
	}

	/**
	 * Questo metodo redirige alla pagina di visualizzazione degli slot disponibili. Il BB dell apagina prende i
	 * parametri passatigli, invoca il WS del SIL che restituisce gli slot disponibili e li visualizza
	 * 
	 * @return
	 */
	public String verificaSlot() {
		/*
		 * controllo di avere un numero di telefono se do il consenso a ricevere sms
		 */
		if ("S".equalsIgnoreCase(consensoSms) && (cellulare == null || cellulare.isEmpty())) {
			addErrorMessage("appuntamento.error.consensosms");
			return "";
		}

		/* se il cellulare era editabile allora ne salvo il valore */
		if (editCellulare) {
			utenteInfoDTO.setCellulare(cellulare);
		}

		/* se il consenso per gli sms era editabile allora ne salvo il valore */
		if (isSezioneSmsVisibile()) {
			utenteInfoDTO.setFlgConsensoSms("S".equals(consensoSms) ? true : false);
		}

		/*
		 * se la provincia di riferimento era editabile allora ne salvo il valore
		 */
		if (editProvincia) {
			if (codProvincia != null) {
				DeProvinciaDTO provinciaDTO = deProvinciaHome.findDTOById(codProvincia);
				utenteInfoDTO.setProvinciaRiferimento(provinciaDTO);
				ygAdesioneDTO.setDeProvinciaNotifica(provinciaDTO);
			}
		}
		/* se il cpi di riferimento era editabile allora ne salvo il valore */
		if (editCpi) {
			if (codCpi != null) {
				ygAdesioneDTO.setDeCpiAssegnazione(deCpiHome.findDTOById(codCpi));
			}
		}

		/* se c'era qualcosa di editabile faccio la merge di tutto */
		if (editCellulare || editProvincia || editCpi || isSezioneSmsVisibile()) {
			utenteInfoHome.mergeDTO(utenteInfoDTO, getSession().getPrincipalId());
			ygAdesioneHome.mergeDTO(ygAdesioneDTO, getSession().getPrincipalId());
		}

		/* outcome per il redirect */
		StringBuffer sb = new StringBuffer();
		sb.append("/secure/utente/yg/disp_appuntamento.xhtml?faces-redirect=true");
		sb.append("&dispAppuntamento=true");
		sb.append(getAllParamsQueryString());

		return sb.toString();
	}

	/**
	 * Metodo per la prenotazione effettiva dello slot. Il metodo redirige ad un'altra pagina che visualizza l'esito
	 * della prenotazione che avviene tramite WS del SIL.
	 * 
	 * @return
	 */
	public String prenota() {
		if (identificativoSlot == null) {
			addErrorMessage("appuntamento.error.seleziona_slot");
			return "";
		}

		/*
		 * al servizio di prenotazione ripasso tutti i dati anche se probabilmente molti sono inutili dato che ho
		 * l'identificativo dello slot. Ciononostante si e' oculatamente deciso di lasciare anche tutti i dati di prima
		 * senza un motivo apparente.
		 */
		StringBuffer sb = new StringBuffer();
		sb.append("/secure/utente/yg/esito_appuntamento.xhtml?faces-redirect=true");
		sb.append("&esito=" + true);
		sb.append(getAllParamsQueryString());

		return sb.toString();
	}

	/**
	 * Crea una query string con tutti i parametri disponibili nella pagina
	 * 
	 * @return
	 */
	private String getAllParamsQueryString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuffer sb = new StringBuffer();

		if (idYgAdesione != null) {
			sb.append("&idYgAdesione=" + idYgAdesione);
		}
		if (dataDa != null) {
			sb.append("&dataDa=" + sdf.format(dataDa));
		}
		if (dataA != null) {
			sb.append("&dataA=" + sdf.format(dataA));
		}
		if (mattinaPomeriggio != null) {
			sb.append("&mattinaPomeriggio=" + mattinaPomeriggio);
		}
		if (codProvincia != null) {
			sb.append("&provinciaRiferimento=" + codProvincia);
		}
		if (codCpi != null) {
			sb.append("&cpiRiferimento=" + codCpi);
		}
		if (idAmbienteSil != null) {
			sb.append("&ambiente=" + idAmbienteSil);
		}
		if (cellulare != null) {
			sb.append("&cellulare=" + cellulare);
		}
		if (consensoSms != null) {
			sb.append("&consenso=" + consensoSms);
		}
		if (identificativoSlot != null) {
			sb.append("&identificativoSlot=" + identificativoSlot);
		}

		return sb.toString();
	}

	public void slotGiornoSuccessivo() {
		/*
		 * solo se la data corrente e' precedente a quella finale
		 */
		if (dataA != null && !dataCorrentePerSlot.before(dataA)) {
			return;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(dataCorrentePerSlot);
		c.add(Calendar.DATE, 1);
		callDispAppuntamento(c.getTime());

		/* deseleziono l'eventuale slot selezionato */
		identificativoSlot = null;

		if (dataA == null) {
			nextSlot = true;
		} else if (slotDisponibili == null || slotDisponibili.isEmpty() || !dataCorrentePerSlot.before(dataA)) {
			nextSlot = false;
		} else {
			nextSlot = true;
		}
	}

	public void slotValuechange(BigInteger identificativoSlot) {
		this.identificativoSlot = identificativoSlot;
	}

	/*
	 * Metodo usato in disp_appuntamento.xhtml
	 */
	public Date XMLGregorianCalendarToDate(XMLGregorianCalendar cal) {
		return cal.toGregorianCalendar().getTime();
	}

	public boolean isSezioneSmsVisibile() {
		/*
		 * Metodo fatto per TRENTO che non visualizza la sezione SMS
		 */
		return ConstantsSingleton.COD_REGIONE != 22;
	}

	public boolean isUmbria() {
		return ConstantsSingleton.COD_REGIONE == 10;
	}

	public boolean viewComboAmbiente() {
		return ygImpostazioni.getFlgAbilitazioneAmbiente();
	}

	public boolean viewRangeDate() {
		return ygImpostazioni.getFlgAbilitazioneParData();
	}

	public String tornaARicerca() {
		StringBuilder sb = new StringBuilder();
		sb.append("/secure/utente/yg/prenota_appuntamento.xhtml?faces-redirect=true");
		sb.append(getAllParamsQueryString());

		return sb.toString();
	}

	public void tornaAScrivania() {
		redirectHome();
	}

	public String getStrDenominazioneCPI() {
		return strDenominazioneCPI;
	}

	public void setStrDenominazioneCPI(String strDenominazioneCPI) {
		this.strDenominazioneCPI = strDenominazioneCPI;
	}

	public Boolean getViewMsgTerni() {
		return viewMsgTerni;
	}

	public void setViewMsgTerni(Boolean viewMsgTerni) {
		this.viewMsgTerni = viewMsgTerni;
	}

}
