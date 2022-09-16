package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object del CV, sezione Professioni Desiderate
 * 
 * @author turrini
 * 
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvProfDesiderateDTO extends AbstractUpdatablePkDTO implements ICurriculumSection, IHasUniqueValue {
	private static final long serialVersionUID = -1811585714201774349L;

	private Integer idCvDatiPersonali;
	private DeMansioneMinDTO deMansioneMin;
	private DeMansioneDTO deMansione;
	private String descrizioneProfessione;
	private Boolean flagEspSettore;
	private String breveDescrProfessione;
	private Boolean flagDispTrasferte;
	private Boolean flagDispMezzoProprio;
	private String noteComuni;
	private Boolean flagDispMezziPubblici;
	private Boolean flagPendolarismo;
	private Boolean flagMobilitaSett;
	private Integer numOrePercorrenza;
	private DeTrasfertaDTO deTrasferta;
	private String noteDisponibilita;

	private String dispTerritorialeComuni;
	private String dispTerritorialeProvince;
	private String dispTerritorialeRegioni;

	private List<IDecode> listaOrario;
	private List<IDecode> listaRapportoLavoro;
	private List<DeMansioneMinDTO> listaDeMansioneMin;
	private List<IDecode> listaTurno;

	private List<DeComuneDTO> listaComuni;
	private List<DeProvinciaDTO> listaProvince;
	private List<DeRegioneDTO> listaRegioni;
	private List<DeComuneDTO> listaStati;

	public CvProfDesiderateDTO() {
		super();
		deMansioneMin = new DeMansioneMinDTO();
		deMansione = new DeMansioneDTO();
		listaOrario = new ArrayList<IDecode>();
		listaRapportoLavoro = new ArrayList<IDecode>();
		listaDeMansioneMin = new ArrayList<DeMansioneMinDTO>();
		listaTurno = new ArrayList<IDecode>();
		listaComuni = new ArrayList<DeComuneDTO>();
		listaProvince = new ArrayList<DeProvinciaDTO>();
		listaRegioni = new ArrayList<DeRegioneDTO>();
		listaStati = new ArrayList<DeComuneDTO>();
		deTrasferta = new DeTrasfertaDTO();
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	@Override
	public String getUniqueValue() {
		return deMansioneMin.getId();
	}

	public DeMansioneMinDTO getDeMansioneMin() {
		return deMansioneMin;
	}

	public void setDeMansioneMin(DeMansioneMinDTO deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	public String getDescrizioneProfessione() {
		return descrizioneProfessione;
	}

	public void setDescrizioneProfessione(String descrizioneProfessione) {
		this.descrizioneProfessione = descrizioneProfessione;
	}

	public Boolean getFlagEspSettore() {
		return flagEspSettore;
	}

	public void setFlagEspSettore(Boolean flagEspSettore) {
		this.flagEspSettore = flagEspSettore;
	}

	public String getBreveDescrProfessione() {
		return breveDescrProfessione;
	}

	public void setBreveDescrProfessione(String breveDescrProfessione) {
		this.breveDescrProfessione = breveDescrProfessione;
	}

	public Boolean getFlagDispTrasferte() {
		return flagDispTrasferte;
	}

	public void setFlagDispTrasferte(Boolean flagDispTrasferte) {
		this.flagDispTrasferte = flagDispTrasferte;
	}

	public Boolean getFlagDispMezzoProprio() {
		return flagDispMezzoProprio;
	}

	public void setFlagDispMezzoProprio(Boolean flagDispMezzoProprio) {
		this.flagDispMezzoProprio = flagDispMezzoProprio;
	}

	public List<IDecode> getListaOrario() {
		return listaOrario;
	}

	public void setListaOrario(List<IDecode> listaOrario) {
		this.listaOrario = listaOrario;
	}

	public List<IDecode> getListaRapportoLavoro() {
		return listaRapportoLavoro;
	}

	public void setListaRapportoLavoro(List<IDecode> listaRapportoLavoro) {
		this.listaRapportoLavoro = listaRapportoLavoro;
	}

	public DeMansioneDTO getDeMansione() {
		return deMansione;
	}

	public void setDeMansione(DeMansioneDTO deMansione) {
		this.deMansione = deMansione;
	}

	public List<DeMansioneMinDTO> getListaDeMansioneMin() {
		return listaDeMansioneMin;
	}

	public void setListaDeMansioneMin(List<DeMansioneMinDTO> list) {
		this.listaDeMansioneMin = list;
	}

	public List<IDecode> getListaTurno() {
		return listaTurno;
	}

	public void setListaTurno(List<IDecode> listaTurno) {
		this.listaTurno = listaTurno;
	}

	public List<DeComuneDTO> getListaComuni() {
		return listaComuni;
	}

	public void setListaComuni(List<DeComuneDTO> listaComuni) {
		this.listaComuni = listaComuni;
	}

	public List<DeProvinciaDTO> getListaProvince() {
		return listaProvince;
	}

	public void setListaProvince(List<DeProvinciaDTO> listaProvince) {
		this.listaProvince = listaProvince;
	}

	public List<DeRegioneDTO> getListaRegioni() {
		return listaRegioni;
	}

	public void setListaRegioni(List<DeRegioneDTO> listaRegioni) {
		this.listaRegioni = listaRegioni;
	}

	public List<DeComuneDTO> getListaStati() {
		return listaStati;
	}

	public void setListaStati(List<DeComuneDTO> listaStati) {
		this.listaStati = listaStati;
	}

	public String getNoteComuni() {
		return noteComuni;
	}

	public void setNoteComuni(String noteComuni) {
		this.noteComuni = noteComuni;
	}

	public Boolean getFlagDispMezziPubblici() {
		return flagDispMezziPubblici;
	}

	public void setFlagDispMezziPubblici(Boolean flagDispMezziPubblici) {
		this.flagDispMezziPubblici = flagDispMezziPubblici;
	}

	public Boolean getFlagPendolarismo() {
		return flagPendolarismo;
	}

	public void setFlagPendolarismo(Boolean flagPendolarismo) {
		this.flagPendolarismo = flagPendolarismo;
	}

	public Boolean getFlagMobilitaSett() {
		return flagMobilitaSett;
	}

	public void setFlagMobilitaSett(Boolean flagMobilitaSett) {
		this.flagMobilitaSett = flagMobilitaSett;
	}

	public Integer getNumOrePercorrenza() {
		return numOrePercorrenza;
	}

	public void setNumOrePercorrenza(Integer numOrePercorrenza) {
		this.numOrePercorrenza = numOrePercorrenza;
	}

	public DeTrasfertaDTO getDeTrasferta() {
		return deTrasferta;
	}

	public void setDeTrasferta(DeTrasfertaDTO deTrasferta) {
		this.deTrasferta = deTrasferta;
	}

	public String getNoteDisponibilita() {
		return noteDisponibilita;
	}

	public void setNoteDisponibilita(String noteDisponibilita) {
		this.noteDisponibilita = noteDisponibilita;
	}

	public String getDispTerritorialeComuni() {
		return dispTerritorialeComuni;
	}

	public void setDispTerritorialeComuni(String dispTerritorialeComuni) {
		this.dispTerritorialeComuni = dispTerritorialeComuni;
	}

	public String getDispTerritorialeProvince() {
		return dispTerritorialeProvince;
	}

	public void setDispTerritorialeProvince(String dispTerritorialeProvince) {
		this.dispTerritorialeProvince = dispTerritorialeProvince;
	}

	public String getDispTerritorialeRegioni() {
		return dispTerritorialeRegioni;
	}

	public void setDispTerritorialeRegioni(String dispTerritorialeRegioni) {
		this.dispTerritorialeRegioni = dispTerritorialeRegioni;
	}

}
