package it.eng.myportal.beans.corsi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.DeTipoFormazioneDTO;
import it.eng.myportal.dtos.OrCorsoDTO;
import it.eng.myportal.dtos.OrSedeCorsoDTO;
import it.eng.myportal.dtos.RicercaCorsoDTO;
import it.eng.myportal.entity.home.OrCorsoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoFormazioneHome;
import it.eng.myportal.enums.TipoRicercaCorso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * BackingBean della pagina di ricerca dei corsi ministeriali
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class RicercaCorsiBean extends AbstractBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5707184558130094752L;

	@EJB
	OrCorsoHome orCorsoHome;
	@EJB
	DeTipoFormazioneHome deTipoFormazioneHome;

	private static final String PARAMETRI_RICERCA = "parametriRicerca";

	private RicercaCorsoDTO parametriRicerca;

	private List<OrCorsoDTO> corsi;

	private List<SelectItem> tipiRicerca;

	private List<SelectItem> listDeTipoFormazione;
	
	private boolean eseguitaRicerca = false;

	public List<OrCorsoDTO> getCorsi() {
		return corsi;
	}

	public void setCorsi(List<OrCorsoDTO> corsi) {
		this.corsi = corsi;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		// if (session.isProvincia()) {
		String idString = getRequestParameter("id");
		if (idString != null) {
			try {
				@SuppressWarnings("unused")
				int ricercaId = Integer.parseInt(idString);
				
			} catch (NumberFormatException e) {
				log.warn("Tentativo di manipolazione parametri.");
				redirectHome();
			}
		}
		// crea il dto solo se serve, altrimenti si perdono i dati recuperati
		// dalla sessione
		if (parametriRicerca == null) {
			parametriRicerca = new RicercaCorsoDTO();
		}
		if (corsi == null) {
			corsi = new ArrayList<OrCorsoDTO>();
		}

		List<SelectItem> listDeTipoFormazioneTmp = deTipoFormazioneHome.getListItems(true);
		setListDeTipoFormazione(new ArrayList<SelectItem>());
		for (SelectItem selectItem : listDeTipoFormazioneTmp) {
			// il codice FR ha una sua sezione apposita, viene tolto dalla
			// select
			Object value = selectItem.getValue();
			if (value == null) {
				getListDeTipoFormazione().add(selectItem);
			} else if (value instanceof DeTipoFormazioneDTO) {
				DeTipoFormazioneDTO dto = (DeTipoFormazioneDTO) value;
				if (!"FR".equals(dto.getId())) {
					getListDeTipoFormazione().add(selectItem);
				}
			}
		}

		tipiRicerca = TipoRicercaCorso.asSelectItems();

		log.debug("Costruito il Bean per cercare i corsi.");
	}

	public void rifPaSearch() {

		String rifpa = parametriRicerca.getCodRifPA();
		parametriRicerca = new RicercaCorsoDTO();
		
		parametriRicerca.setTipo(TipoRicercaCorso.RIF_PA);
		parametriRicerca.setCodRifPA(rifpa);
		search();
	}

	public void multipleSearch() {
		// boolean hasProvincia =
		// StringUtils.isNotBlank(parametriRicerca.getProvincia()) ||
		// StringUtils.isNotBlank(parametriRicerca.getProvinciaFR());
		// boolean hasFormazione =
		// StringUtils.isNotBlank(parametriRicerca.getFormazione());
		// boolean hasGruppoProfessionale =
		// StringUtils.isNotBlank(parametriRicerca.getStrGruppoProfessionale());
		// if (hasProvincia || hasFormazione || hasGruppoProfessionale)

		String codProv 	= parametriRicerca.getCodProvincia();
		String prov 		= parametriRicerca.getProvincia();
		String codForm 	= parametriRicerca.getCodFormazione();
		String codGPro 	= parametriRicerca.getCodGruppoProfessionale();
		String strGPro 	= parametriRicerca.getStrGruppoProfessionale();
			
		parametriRicerca = new RicercaCorsoDTO();		
		parametriRicerca.setTipo(TipoRicercaCorso.RICERCA_MULTIPLA);
		parametriRicerca.setCodProvincia(codProv);
		parametriRicerca.setProvincia(prov);
		parametriRicerca.setCodFormazione(codForm);
		parametriRicerca.setCodGruppoProfessionale(codGPro);
		parametriRicerca.setStrGruppoProfessionale(strGPro);

		search();
	}

	public void multipleSearchFR() {
		// boolean hasProvincia =
		// StringUtils.isNotBlank(parametriRicerca.getProvincia()) ||
		// StringUtils.isNotBlank(parametriRicerca.getProvinciaFR());
		// boolean hasFormazione =
		// StringUtils.isNotBlank(parametriRicerca.getFormazione());
		// boolean hasGruppoProfessionale =
		// StringUtils.isNotBlank(parametriRicerca.getStrGruppoProfessionale());
		// if (hasProvincia || hasFormazione || hasGruppoProfessionale)
		
		String codProFr 	= parametriRicerca.getCodProvinciaFR();
		String proFr 			= parametriRicerca.getProvinciaFR();
		String codProfe 	= parametriRicerca.getCodProfessione();
		String strProfe 	= parametriRicerca.getStrProfessione();
		
		parametriRicerca = new RicercaCorsoDTO();		
		parametriRicerca.setTipo(TipoRicercaCorso.RICERCA_MULTIPLA_FR);
		parametriRicerca.setCodProvinciaFR(codProFr);
		parametriRicerca.setProvinciaFR(proFr);
		parametriRicerca.setCodProfessione(codProfe);
		parametriRicerca.setStrProfessione(strProfe);

		search();
	}

	public void freeSearch() {
		
		String ricL = parametriRicerca.getRicercaLibera();
		parametriRicerca = new RicercaCorsoDTO();		

		parametriRicerca.setTipo(TipoRicercaCorso.RICERCA_LIBERA);
		parametriRicerca.setRicercaLibera(ricL);
		search();
	}

	public void search() {
		eseguitaRicerca = true;
		putParamsIntoSession();
		corsi = orCorsoHome.findByFilter(parametriRicerca, getSession().getPrincipalId());
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(PARAMETRI_RICERCA, parametriRicerca);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		parametriRicerca = (RicercaCorsoDTO) restoreParams.get(PARAMETRI_RICERCA);
		search();
	};

	public void fintoSearch() {
		corsi = orCorsoHome.findAllDTO();
		corsi = new ArrayList<OrCorsoDTO>();
		OrCorsoDTO corso = new OrCorsoDTO();
		corso.setId(1);
		corso.setCodiceIdentificativo("ASDF");
		OrSedeCorsoDTO sede = new OrSedeCorsoDTO();
		sede.setStrComune("Bologna");
		List<OrSedeCorsoDTO> sedi = new ArrayList<OrSedeCorsoDTO>();
		sedi.add(sede);
		corso.setOrSedeCorsos(sedi);
		corso.setTitoloCorso("JQuery Masters");
		corsi.add(corso);
	}

	public RicercaCorsoDTO getParametriRicerca() {
		return parametriRicerca;
	}

	public void setParametriRicerca(RicercaCorsoDTO parametriRicerca) {
		this.parametriRicerca = parametriRicerca;
	}

	public List<SelectItem> getTipiRicerca() {
		return tipiRicerca;
	}

	public void setTipiRicerca(List<SelectItem> tipiRicerca) {
		this.tipiRicerca = tipiRicerca;
	}

	public List<SelectItem> getListDeTipoFormazione() {
		return listDeTipoFormazione;
	}

	public void setListDeTipoFormazione(List<SelectItem> listDeTipoFormazione) {
		this.listDeTipoFormazione = listDeTipoFormazione;
	}

	public boolean isEseguitaRicerca() {
		return eseguitaRicerca;
	}

	public void setEseguitaRicerca(boolean eseguitaRicerca) {
		this.eseguitaRicerca = eseguitaRicerca;
	}

	
}
