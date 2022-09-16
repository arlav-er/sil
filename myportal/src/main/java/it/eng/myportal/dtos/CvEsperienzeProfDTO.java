package it.eng.myportal.dtos;

/**
 * Data transfer object del CV,
 * sezione Esperienze professionali
 * 
 * @author iescone
 * 
 * @see AbstractUpdatableDTO
 * @see ICurriculumSection
 */
import java.util.Date;

public class CvEsperienzeProfDTO extends AbstractUpdatablePkDTO implements ICurriculumSection {
	private static final long serialVersionUID = 1L;

	private Integer idCvDatiPersonali;
	private String codMansione;
	private String strMansione;
	private String codAteco;
	private String strAteco;
	private Date da;
	private Date a;

	private boolean inCorso;
	private String attivitaResponsabilita;
	private String datoreLavoro;
	private DeContrattoDTO deContratto;
	private DeMansioneMinDTO qualificaSvolta;

	private DeAreaSilDTO deAreaSil;
	private DeMotivoCessazioneDTO deMotivoCessazione;
	private String altroMotivoCessazione;
	private String partitaIva;
	private String codiceFiscale;
	private DeComuneDTO deComuneDatore;

	public CvEsperienzeProfDTO() {
		super();
		deContratto = new DeContrattoDTO();
		qualificaSvolta = new DeMansioneMinDTO();
		deAreaSil = new DeAreaSilDTO();
		deMotivoCessazione = new DeMotivoCessazioneDTO();
		deComuneDatore = new DeComuneDTO();
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	public String getStrMansione() {
		return strMansione;
	}

	public void setStrMansione(String strMansione) {
		this.strMansione = strMansione;
	}

	public String getCodAteco() {
		return codAteco;
	}

	public void setCodAteco(String codAteco) {
		this.codAteco = codAteco;
	}

	public String getStrAteco() {
		return strAteco;
	}

	public void setStrAteco(String strAteco) {
		this.strAteco = strAteco;
	}

	public Date getDa() {
		return da;
	}

	public void setDa(Date da) {
		this.da = da;
	}

	public Date getA() {
		return a;
	}

	public void setA(Date a) {
		this.a = a;
	}

	public boolean getInCorso() {
		return this.inCorso;
	}

	public void setInCorso(boolean inCorso) {
		this.inCorso = inCorso;
	}

	public String getAttivitaResponsabilita() {
		return attivitaResponsabilita;
	}

	public void setAttivitaResponsabilita(String attivitaResponsabilita) {
		this.attivitaResponsabilita = attivitaResponsabilita;
	}

	public String getDatoreLavoro() {
		return datoreLavoro;
	}

	public void setDatoreLavoro(String datoreLavoro) {
		this.datoreLavoro = datoreLavoro;
	}

	// public DeRapportoLavoroDTO getRapportoLavoro() {
	// return rapportoLavoro;
	// }
	//
	// public void setRapportoLavoro(DeRapportoLavoroDTO rapportoLavoro) {
	// this.rapportoLavoro = rapportoLavoro;
	// }

	public DeMansioneMinDTO getQualificaSvolta() {
		return qualificaSvolta;
	}

	public void setQualificaSvolta(DeMansioneMinDTO qualificaSvolta) {
		this.qualificaSvolta = qualificaSvolta;
	}

	public DeContrattoDTO getDeContratto() {
		return deContratto;
	}

	public void setDeContratto(DeContrattoDTO deContratto) {
		this.deContratto = deContratto;
	}

	public DeAreaSilDTO getDeAreaSil() {
		return deAreaSil;
	}

	public void setDeAreaSil(DeAreaSilDTO deAreaSil) {
		this.deAreaSil = deAreaSil;
	}

	public DeMotivoCessazioneDTO getDeMotivoCessazione() {
		return deMotivoCessazione;
	}

	public void setDeMotivoCessazione(DeMotivoCessazioneDTO deMotivoCessazione) {
		this.deMotivoCessazione = deMotivoCessazione;
	}

	public String getAltroMotivoCessazione() {
		return altroMotivoCessazione;
	}

	public void setAltroMotivoCessazione(String altroMotivoCessazione) {
		this.altroMotivoCessazione = altroMotivoCessazione;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public DeComuneDTO getDeComuneDatore() {
		return deComuneDatore;
	}

	public void setDeComuneDatore(DeComuneDTO deComuneDatore) {
		this.deComuneDatore = deComuneDatore;
	}

}