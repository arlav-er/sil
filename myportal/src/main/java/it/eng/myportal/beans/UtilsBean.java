package it.eng.myportal.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.DeAttivitaDTO;
import it.eng.myportal.dtos.DeCittadinanzaMinDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCondizioneOccupazMinDTO;
import it.eng.myportal.dtos.DeFbCategoriaDTO;
import it.eng.myportal.dtos.DeFbTipoTirocinioDTO;
import it.eng.myportal.dtos.DeIscrizioneCorsoMinDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeNaturaGiuridicaDTO;
import it.eng.myportal.dtos.DePosizioneProfessionaleMinDTO;
import it.eng.myportal.dtos.DePresenzaItaliaMinDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.dtos.DeStatoFbChecklistDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.entity.home.decodifiche.DeAtpAttivitaSvoltaHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpEnteConsulenteHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCondizioneOccupazMinHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeFbCategoriaHome;
import it.eng.myportal.entity.home.decodifiche.DeFbTipoTirocinioHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeIscrizioneCorsoMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;
import it.eng.myportal.entity.home.decodifiche.DeNaturaGiuridicaHome;
import it.eng.myportal.entity.home.decodifiche.DePosizioneProfessionaleMinHome;
import it.eng.myportal.entity.home.decodifiche.DePresenzaItaliaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeSistemaMyCasHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbChecklistHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.enums.FasciaEta;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.enums.DeSistemaEnum;

/**
 * 
 * ManagedBean JSF per contenere i dati dell'utente che ha effettuato l'accesso al sistema. Il Bean viene creato una
 * sola volta per ogni sessione e ricava tutte le informazioni utili sull'utente connesso.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ApplicationScoped
public class UtilsBean {

	protected static Log log = LogFactory.getLog(UtilsBean.class);
	// timestamp di quando è stata inizializzata l'applicazione
	private long starttime;

	// nome della regione (oppure della provincia)
	/**
	 * Il nome della ragione è accessibile anche da #{txt['regione.nome']}
	 */
	private String nomeRegione;

	// contiene "Regione di " + nomeRegione
	// oppure "Provincia di " + nomeRegione
	private String nomeRegioneEsteso;

	private String myportalVersion;

	@EJB
	DeSistemaMyCasHome deSistemaMyCasHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeTipoPraticaHome deTipoPraticaHome;

	@EJB
	DeTipoConsulenzaHome deTipoConsulenzaHome;

	@EJB
	DeAtpAttivitaSvoltaHome deAtpAttivitaSvoltaHome;

	@EJB
	DeAtpContrattoHome deAtpContratto;

	@EJB
	DeAtpEnteConsulenteHome deAtpEnteConsulenteHome;

	@EJB
	DeGenereHome deGenereHome;

	@EJB
	DeMotivoPermessoHome deMotivoPermessoHome;

	@EJB
	DeStatoAdesioneHome deStatoAdesioneHome;

	@EJB
	DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	DePresenzaItaliaMinHome dePresenzaItaliaMinHome;

	@EJB
	DeIscrizioneCorsoMinHome deIscrizioneCorsoMinHome;

	@EJB
	DeCondizioneOccupazMinHome deCondizioneOccupazMinHome;

	@EJB
	DeCittadinanzaMinHome deCittadinanzaMinHome;

	@EJB
	DePosizioneProfessionaleMinHome dePosizioneProfessionaleMinHome;

	@EJB
	DeStatoFbChecklistHome deStatoFbChecklistHome;

	@EJB
	DeFbCategoriaHome deFbCategoriaHome;

	@EJB
	DeFbTipoTirocinioHome deFbTipoTirocinioHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeNaturaGiuridicaHome deNaturaGiuridicaHome;

	private List<SelectItem> fascieEta;
	private List<SelectItem> attivita;
	private List<SelectItem> tipiContratto;
	private List<SelectItem> tipiPraticaPIva;
	private List<SelectItem> tipiPraticaAtipici;
	private List<SelectItem> tipiConsulenza;
	private List<SelectItem> tipiAttivitaSvolte;
	private List<SelectItem> tipiSindacati;
	private List<SelectItem> regioni;
	private List<SelectItem> genere;
	private List<SelectItem> motivoPermesso;
	private List<SelectItem> statoAdesione;
	private List<SelectItem> statoAdesioneMin;

	public List<SelectItem> asSelectItemList(List<? extends GenericDecodeDTO> list) {
		if (list == null)
			return null;
		List<SelectItem> ret = new ArrayList<SelectItem>();
		for (GenericDecodeDTO abstractDecodeDTO : list) {
			ret.add(new SelectItem(abstractDecodeDTO, abstractDecodeDTO.getDescrizione()));
		}
		return ret;
	}

	public List<SelectItem> getFascieEta() {
		return fascieEta;
	}

	public List<SelectItem> getAttivita() {
		return attivita;
	}

	public List<SelectItem> getTipiContratto() {
		return tipiContratto;
	}

	public List<SelectItem> getTipiPraticaPIva() {
		return tipiPraticaPIva;
	}

	public List<SelectItem> getTipiPraticaAtipici() {
		return tipiPraticaAtipici;
	}

	public List<SelectItem> getSindacati() {
		return tipiSindacati;
	}

	public List<SelectItem> getTipiConsulenza() {
		return tipiConsulenza;
	}

	public List<SelectItem> getTipiAttivitaSvolte() {
		return tipiAttivitaSvolte;
	}

	public String escapeJS(String value) {
		value = replace(value, "\\", "\\\\"); // backslash --> doppio
		// backslash
		value = replace(value, "\"", "\\\""); // doppio-apice --> backslash +
		// doppio-apice
		value = replace(value, "\n", "\\n"); // invio --> backslash + invio
		value = replace(value, "\'", "\\'"); // apice --> backslash + apice
		// apice
		value = replace(value, "'", "\\'");

		return value;
	}

	public static String replace(String subject, String find, String replace) {
		if (subject == null)
			return null;

		StringBuilder buf = new StringBuilder();
		int l = find.length();
		int s = 0;
		int i = subject.indexOf(find);
		while (i != -1) {
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
			i = subject.indexOf(find, s);
		}
		buf.append(subject.substring(s));
		return buf.toString();
	}

	/**
	 * Lista delle 9 province della regione. Sono in ApplicationScope in quanto mi auguro che non cambino così di
	 * frequente.
	 */
	private List<SelectItem> province;

	/**
	 * Codice regionale di configurazione
	 */
	private Integer regione;

	public final static int EMILIA_ROMAGNA = 8;
	public final static int PUGLIA = 16;
	public final static int UMBRIA = 10;
	public final static int VDA = 2;
	public final static int TRENTO = 22;
	public final static int CALABRIA = 18;

	private String template;

	private String templatePrimefaces;

	private String bootstrapTemplate;

	private String mapZoom;

	private String mapCenter;

	/**
	 * Chiamata dalla postConstruct() E dal refresh esplicito sulla pagina di amministrazione
	 */
	public void initFields() {
		log.warn("INIT DE_ TaBLES");
		regione = ConstantsSingleton.COD_REGIONE;
		province = deRegioneHome.getProvinceListItems(String.valueOf(regione), true);
		province.get(0).setDescription("==Scegli una provincia di riferimento==");
		nomeRegione = deRegioneHome.findDTOById(String.valueOf(regione)).getDenominazione();
		nomeRegione = WordUtils.capitalizeFully(nomeRegione, " -".toCharArray());
		fascieEta = FasciaEta.asSelectItems(true);
		attivita = deAttivitaHome.getListItems(true);
		tipiContratto = deContrattoHome.getListItems(true);
		tipiPraticaPIva = deTipoPraticaHome.getPartitaIvaListItems(true);
		tipiPraticaAtipici = deTipoPraticaHome.getAtipiciListItems(true);
		tipiConsulenza = deTipoConsulenzaHome.getListItems(true);
		tipiAttivitaSvolte = deAtpAttivitaSvoltaHome.getListItems(true);
		tipiSindacati = deAtpEnteConsulenteHome.getListItems(true);
		regioni = deRegioneHome.getListItems(true, "denominazione");
		genere = deGenereHome.getListItems(true, "descrizione");
		motivoPermesso = deMotivoPermessoHome.getListItems(true, "descrizione");
		statoAdesione = deStatoAdesioneHome.getListItems(true, "descrizione");
		statoAdesioneMin = deStatoAdesioneMinHome.getListItems(true, "descrizione");
	}

	@PostConstruct
	public void postConstruct() {
		starttime = new Date().getTime();

		this.myportalVersion = ConstantsSingleton.getBuildVersion();
		try {
			deSistemaMyCasHome.synchVersionePom(DeSistemaEnum.LXTE.toString(), this.myportalVersion);
		} catch (Exception e) {
			log.error("GRAVE impossibile aggiornare versione pom su DB: "+ e.getMessage());
		}
		

		initFields();

		switch (regione) {
		case EMILIA_ROMAGNA:
			template = "/template/emiliaRomagna.xhtml";
			templatePrimefaces = "/template/primefacesEmilia.xhtml";
			mapZoom = "8";
			mapCenter = "44.5,11";
			nomeRegioneEsteso = "Regione " + nomeRegione;
			bootstrapTemplate = "emiliaRomagna";
			break;
		case PUGLIA:
			template = "/template/puglia.xhtml";
			templatePrimefaces = "/template/primefacesPuglia.xhtml";
			mapZoom = "8";
			mapCenter = "40.64,17.45";
			nomeRegioneEsteso = "Regione " + nomeRegione;
			bootstrapTemplate = "puglia";
			break;
		case UMBRIA:
			template = "/template/umbria.xhtml";
			templatePrimefaces = "/template/primefacesUmbria.xhtml";
			mapZoom = "8";
			mapCenter = "43.121034,12.413635";
			nomeRegioneEsteso = "Regione " + nomeRegione;
			bootstrapTemplate = "umbria";
			break;
		case VDA:
			template = "/template/vda.xhtml";
			templatePrimefaces = "/template/primefacesVda.xhtml";
			mapZoom = "8";
			mapCenter = "45.740693,7.41806";
			nomeRegioneEsteso = "Regione " + nomeRegione;
			bootstrapTemplate = "vda";
			break;
		case TRENTO:
			template = "/template/trento.xhtml";
			templatePrimefaces = "/template/primefacesTrento.xhtml";
			mapZoom = "8";
			mapCenter = "46.070849,11.120853";
			nomeRegioneEsteso = "Provincia Autonoma di " + nomeRegione;
			bootstrapTemplate = "trento";
			break;
		case CALABRIA:
			template = "/template/calabria.xhtml";
			templatePrimefaces = "/template/primefacesCalabria.xhtml";
			mapZoom = "8";
			mapCenter = "39.20,16.50";
			nomeRegioneEsteso = "Regione " + nomeRegione;
			// FIXME are we missing a template or shall this be removed?
			bootstrapTemplate = "emiliaRomagna";
			break;
		}

	}

	public static boolean isCalabria() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_CALABRIA);
	}

	public boolean isRER() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER);
	}

	public static boolean isStaticRER() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER);
	}

	public boolean isVDA() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_VDA);
	}

	public static boolean isUmbria() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	public boolean isPAT() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO);
	}

	public static boolean isStaticPAT() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO);
	}

	public boolean isPuglia() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_PUGLIA);
	}

	public List<SelectItem> getProvince() {
		return province;
	}

	public void setProvince(List<SelectItem> province) {
		this.province = province;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public Integer getRegione() {
		return regione;
	}

	public void setRegione(Integer regione) {
		this.regione = regione;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(String mapZoom) {
		this.mapZoom = mapZoom;
	}

	public String getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(String mapCenter) {
		this.mapCenter = mapCenter;
	}

	/**
	 * Restituisce il nome della Regione
	 * 
	 * @return
	 */

	public String getNomeRegione() {
		return nomeRegione;
	}

	/*
	 * Restituisce "Regione di " oopure "Provincia di " concatenato al nome della Regione/Provincia
	 */

	public String getNomeRegioneEsteso() {
		return nomeRegioneEsteso;
	}

	public String getNomeComuneEsteso(String comuneId) {

		if (comuneId != null && comuneId.length() > 0) {
			DeComuneDTO c = deComuneHome.findDTOById(comuneId);
			DeProvinciaDTO p = deProvinciaHome.findDTOById(c.getIdProvincia());
			DeRegioneDTO r = deRegioneHome.findDTOById(p.getIdRegione());

			return c.getDescrizione() + " (" + p.getTarga() + ") " + r.getDescrizione();
		} else
			return "";
	}

	public String getNomeComuneRegione(String comuneId, String comuneDescrizioneEstesa) {
		if (comuneId != null && comuneId.length() > 0) {
			DeRegioneDTO regioneDTO = deComuneHome.findRegioneDTOByComuneId(comuneId);
			if (regioneDTO != null) {
				return comuneDescrizioneEstesa + " " + regioneDTO.getDescrizione();
			}
		}
		return "";
	}

	public List<SelectItem> getRegioni() {
		return regioni;
	}

	public void setRegioni(List<SelectItem> regioni) {
		this.regioni = regioni;
	}

	public String cutDescrizione(String descrizione, int maxLength) {
		return Utils.cut(descrizione, maxLength, true);
	}

	public String getTemplatePrimefaces() {
		return templatePrimefaces;
	}

	public void setTemplatePrimefaces(String templatePrimefaces) {
		this.templatePrimefaces = templatePrimefaces;
	}

	public List<SelectItem> getGenere() {
		return genere;
	}

	public void setGenere(List<SelectItem> genere) {
		this.genere = genere;
	}

	public List<SelectItem> getMotivoPermesso() {
		return motivoPermesso;
	}

	public void setMotivoPermesso(List<SelectItem> motivoPermesso) {
		this.motivoPermesso = motivoPermesso;
	}

	public List<SelectItem> getStatoAdesione() {
		return statoAdesione;
	}

	public void setStatoAdesione(List<SelectItem> statoAdesione) {
		this.statoAdesione = statoAdesione;
	}

	public List<SelectItem> getStatoAdesioneMin() {
		return statoAdesioneMin;
	}

	public void setStatoAdesioneMin(List<SelectItem> statoAdesioneMin) {
		this.statoAdesioneMin = statoAdesioneMin;
	}

	public List<DePresenzaItaliaMinDTO> getDePresenzaItaliaMinList() {
		return dePresenzaItaliaMinHome.findAllDTO();
	}

	public List<DeIscrizioneCorsoMinDTO> getDeIscrizioneCorsoMinList() {
		return deIscrizioneCorsoMinHome.findAllDTO();
	}

	public List<DeCondizioneOccupazMinDTO> getDeCondizioneOccupazMinList() {
		return deCondizioneOccupazMinHome.findAllDTO();
	}

	public List<DeCittadinanzaMinDTO> getDeCittadinanzaMinList() {
		return deCittadinanzaMinHome.findAllDTO();
	}

	public List<DePosizioneProfessionaleMinDTO> getDePosizioneProfessionaleMinList() {
		return dePosizioneProfessionaleMinHome.findAllDTO();
	}

	public List<DeFbCategoriaDTO> getDeFbCategoriaList() {
		return deFbCategoriaHome.findAllDTO();
	}

	public List<DeFbTipoTirocinioDTO> getDeFbTipoTirocinioList() {
		return deFbTipoTirocinioHome.findAllDTO();
	}

	public List<DeStatoFbChecklistDTO> getDeStatoFbChecklistList() {
		return deStatoFbChecklistHome.findAllDTO();
	}

	public List<DeComuneDTO> completeDeComuneValido(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}

	public List<DeComuneDTO> completeDeComuneValidoSoloNazione(String par) {
		return deComuneHome.findNazioniValideBySuggestion(par);
	}

	public List<DeAttivitaDTO> completeDeAttivita(String par) {
		return deAttivitaHome.findBySuggestion(par);
	}

	public List<DeNaturaGiuridicaDTO> completeDeNaturaGiuridica(String par) {
		return deNaturaGiuridicaHome.findBySuggestion(par);
	}

	public List<DeMansioneDTO> completeDeMansione(String par) {
		return deMansioneHome.findBySuggestion(par);
	}

	public List<DeTitoloDTO> completeDeTitolo(String par) {
		return deTitoloHome.findBySuggestion(par);
	}

	public String getBootstrapTemplate() {
		return bootstrapTemplate;
	}

	public void setBootstrapTemplate(String bootstrapTemplate) {
		this.bootstrapTemplate = bootstrapTemplate;
	}

	public String getCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -18);
		return dateFormat.format(c.getTime());
	}

	public String getCurrentCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, 0);
		return dateFormat.format(c.getTime());
	}

	public String getMinCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -120);
		return dateFormat.format(c.getTime());
	}

	public String getMaxCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -10);
		return dateFormat.format(c.getTime());
	}

	public String getPublicCssStyle() {
		return ConstantsSingleton.getDefaultCssStyle();
	}

	public String getLogoutUrl() {
		return ConstantsSingleton.getMyCasLogoutUrl();
	}

	public boolean isSysBetweenDtScadDtPubb(Date dtScadPubb, Date dtPubb) {

		return ConstantsSingleton.isSysBetweenDtScadDtPubbStatic(dtScadPubb, dtPubb);
	}

	public static Date getZeroTimeDate(Date fecha) {
		Date res = fecha;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(fecha);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		res = calendar.getTime();

		return res;
	}

	public String getMyportalVersion() {
		return myportalVersion;
	}

	public void setMyportalVersion(String myportalVersion) {
		this.myportalVersion = myportalVersion;
	}
}
