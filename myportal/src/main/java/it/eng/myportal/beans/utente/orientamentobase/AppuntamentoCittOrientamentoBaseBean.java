package it.eng.myportal.beans.utente.orientamentobase;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class AppuntamentoCittOrientamentoBaseBean extends AbstractBaseBean {

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeAmbienteSilHome deAmbienteSilHome;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	private UtenteCompletoDTO utente;
	private String codProvincia;
	private String codiceFiscale;
	private String cellulare;
	private Date dataDal;
	private Date dataAl;
	private String mattinaOPomeriggio;
	private String codCpiRiferimento;
	private Integer codSportelloDistaccato;
	private List<SelectItem> provinceList;
	private List<SelectItem> cpiRiferimentoList;
	private List<SelectItem> sportelloDistaccatoList;
	private AppuntamentoDTO latestAppuntamento;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		// Recupero informazioni sul patronato connesso e sulle sue province
		// abilitate.
		utente = utenteInfoHome.findDTOCompletoById(getSession().getPrincipalId());
		latestAppuntamento = agAppuntamentoHome.findLatestAppuntamento(getSession().getPrincipalId(),
				TipoAppuntamentoEnum.PL_APPOB);

		/* leggo i parametri che saranno presenti nel caso torni da una ricerca */
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			codProvincia = getRequestParameter("codProvincia");
			codiceFiscale = getRequestParameter("codiceFiscale");
			cellulare = getRequestParameter("cellulare");
			mattinaOPomeriggio = getRequestParameter("mattinaOPomeriggio");

			String dataDalString = getRequestParameter("dataDal");
			if (dataDalString != null) {
				dataDal = sdf.parse(dataDalString);
			}
			String dataAlString = getRequestParameter("dataAl");
			if (dataAlString != null) {
				dataAl = sdf.parse(dataAlString);
			}

			// Carico la lista delle provincie
			provinceList = new ArrayList<SelectItem>();
			for (DeProvinciaDTO deProvincia : deProvinciaHome.findByRegione(ConstantsSingleton.COD_REGIONE.toString())) {
				provinceList.add(new SelectItem(deProvincia.getId(), deProvincia.getDescrizione()));
			}

			// Carico la lista di CPI e/o sportelli.
			onProvinciaSelezionata();
			codCpiRiferimento = getRequestParameter("codCpiRiferimento");
			onCpiRiferimentoSelezionato();
			String codSportelloDistaccatoString = getRequestParameter("codSportelloDistaccato");
			if (codSportelloDistaccatoString != null) {
				codSportelloDistaccato = Integer.parseInt(codSportelloDistaccatoString);
			}
		} catch (ParseException e) {
			addErrorMessage("generic.manipulation_error");
		}

		// Creo la lista di CPI di riferimento e sportelli distaccati
		// (prima regione selezionata di default) se non e' gia' stata settata
		// dai parametri
		if (codProvincia == null) {
			mattinaOPomeriggio = "";
			codProvincia = utente.getProvinciaRiferimento().getId();
			onProvinciaSelezionata();
		}
	}

	/**
	 * Quando l'utente seleziona una provincia, cambio la lista di CPI di riferimento.
	 */
	public void onProvinciaSelezionata() {
		// Svuoto la lista, aggiungo un valore "null" ed annullo l'eventuale
		// scelta di sportello distaccato.
		cpiRiferimentoList = new ArrayList<SelectItem>();
		cpiRiferimentoList.add(new SelectItem(null, " -- Seleziona un CPI di riferimento -- "));
		codCpiRiferimento = null;
		onCpiRiferimentoSelezionato();

		// Prendo i valori riferiti alla provincia selezionata.
		if (codProvincia != null) {
			cpiRiferimentoList = deCpiHome.getListItemsCpiByProvincia(codProvincia, true);
			if (cpiRiferimentoList.size() > 1)
				cpiRiferimentoList.get(0).setLabel(" -- Seleziona un CPI di riferimento -- ");
		}
	}

	/**
	 * Quando l'utente seleziona un CPI di riferimento, popolo la lista degli sportelli.
	 */
	public void onCpiRiferimentoSelezionato() {
		// Svuoto la lista ed aggiungo un valore "null".
		sportelloDistaccatoList = new ArrayList<SelectItem>();
		sportelloDistaccatoList.add(new SelectItem(null, " -- Seleziona uno sportello distaccato -- "));
		codSportelloDistaccato = null;

		// Prendo i valori riferiti al CPI selezionato.
		if (codCpiRiferimento != null) {
			sportelloDistaccatoList = deAmbienteSilHome.getListaSelectItem(codCpiRiferimento);
			if (sportelloDistaccatoList.size() > 1)
				sportelloDistaccatoList.get(0).setLabel(" -- Seleziona uno sportello distaccato -- ");
			else
				sportelloDistaccatoList.get(0).setLabel(" -- Nessuno sportello distaccato disponibile -- ");
		}
	}

	public String verificaSlot() {
		/* outcome per il redirect */
		StringBuilder sb = new StringBuilder();
		sb.append("/secure/utente/orientamento_base/disponibilita_appuntamento.xhtml?faces-redirect=true");
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

	public boolean isLatestAppuntamentoAttivo() {
		if (latestAppuntamento == null || latestAppuntamento.getData() == null
				|| latestAppuntamento.getData().before(Calendar.getInstance().getTime())) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isDatiAnagraficiMissing() {
		boolean res = false;
		if (utente.getCodiceFiscale() == null || utente.getCodiceFiscale().isEmpty()) {
			res = true;
			return res;
		}
		if (utente.getNome() == null || utente.getNome().isEmpty()) {
			res = true;
			return res;
		}
		if (utente.getCognome() == null || utente.getCognome().isEmpty()) {
			res = false;
			return res;
		}
		if (utente.getCittadinanza() == null) {
			res = true;
			return res;
		}
		return res;
	}

	public boolean isDatiNascitaMissing() {
		boolean res = false;
		if (utente.getDataNascita() == null) {
			res = true;
			return res;
		}
		if (utente.getComuneNascita() == null) {
			res = true;
			return res;
		}
		return res;
	}

	public boolean isDatiDomicilioMissing() {
		boolean res = false;
		if (utente.getComuneDomicilio() == null) {
			res = false;
			return true;
		}
		if (utente.getIndirizzoDomicilio() == null || utente.getIndirizzoDomicilio().isEmpty()) {
			res = true;
			return res;
		}
		return res;
	}

	public boolean isDatiResidenzaMissing() {
		boolean res = false;
		if (utente.getComuneResidenza() == null) {
			res = true;
			return res;
		}
		if (utente.getIndirizzoResidenza() == null || utente.getIndirizzoResidenza().isEmpty()) {
			res = true;
			return res;
		}
		return res;
	}

	public boolean isDatiUtenteMissing() {
		if (isDatiAnagraficiMissing() || isDatiNascitaMissing() || isDatiDomicilioMissing() || isDatiResidenzaMissing()) {
			return true;
		} else {
			return false;
		}
	}

	public void redirectHome() {
		super.redirectHome();
	}

	public boolean isUmbria() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public List<SelectItem> getCpiRiferimentoList() {
		return cpiRiferimentoList;
	}

	public void setCpiRiferimentoList(List<SelectItem> cpiRiferimentoList) {
		this.cpiRiferimentoList = cpiRiferimentoList;
	}

	public Date getDataDal() {
		return dataDal;
	}

	public void setDataDal(Date dataDal) {
		this.dataDal = dataDal;
	}

	public Date getDataAl() {
		return dataAl;
	}

	public void setDataAl(Date dataAl) {
		this.dataAl = dataAl;
	}

	public String getMattinaOPomeriggio() {
		return mattinaOPomeriggio;
	}

	public void setMattinaOPomeriggio(String mattinaOPomeriggio) {
		this.mattinaOPomeriggio = mattinaOPomeriggio;
	}

	public List<SelectItem> getSportelloDistaccatoList() {
		return sportelloDistaccatoList;
	}

	public void setSportelloDistaccatoList(List<SelectItem> sportelloDistaccatoList) {
		this.sportelloDistaccatoList = sportelloDistaccatoList;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getCodCpiRiferimento() {
		return codCpiRiferimento;
	}

	public void setCodCpiRiferimento(String codCpiRiferimento) {
		this.codCpiRiferimento = codCpiRiferimento;
	}

	public Integer getCodSportelloDistaccato() {
		return codSportelloDistaccato;
	}

	public void setCodSportelloDistaccato(Integer codSportelloDistaccato) {
		this.codSportelloDistaccato = codSportelloDistaccato;
	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public List<SelectItem> getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List<SelectItem> provinceList) {
		this.provinceList = provinceList;
	}

	public AppuntamentoDTO getLatestAppuntamento() {
		return latestAppuntamento;
	}

	public void setLatestAppuntamento(AppuntamentoDTO latestAppuntamento) {
		this.latestAppuntamento = latestAppuntamento;
	}

}
