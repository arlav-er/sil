package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * 1-1
 * 
 * @author Rodi A. iescone
 * 
 */
public class VaAltreInfoDTO extends AbstractUpdatablePkDTO implements IVacancySection {
	private static final long serialVersionUID = -3599849591582771196L;

	private String fuorisede;
	private String trasferta;
	private DeTrasfertaDTO tipoTrasferta;
	private String automunito;
	private String motomunito;
	private String vitto;
	private String alloggio;
	private String opzNullaOsta;
	private List<IDecode> listaAgevolazioniDTO;
	private String ulterioriRequisiti;

	// Campi aggiunti per Trento (aprile 2017)
	private String opzMilite;
	private DeGenereDTO deGenere;
	private DeMotivoGenereSilDTO deMotivoGenereSil;
	private String notaMotivoGenere;
	private DeAreaSilDTO deAreaSil;
	private String localita;

	public VaAltreInfoDTO() {
		super();
		tipoTrasferta = new DeTrasfertaDTO();
		listaAgevolazioniDTO = new ArrayList<IDecode>();
		deMotivoGenereSil = new DeMotivoGenereSilDTO();
		deAreaSil = new DeAreaSilDTO();
		deGenere = new DeGenereDTO();
	}

	public String getAutomunito() {
		return automunito;
	}

	public void setAutomunito(String automunito) {
		this.automunito = automunito;
	}

	public String getMotomunito() {
		return motomunito;
	}

	public void setMotomunito(String motomunito) {
		this.motomunito = motomunito;
	}

	public String getVitto() {
		return vitto;
	}

	public void setVitto(String vitto) {
		this.vitto = vitto;
	}

	public String getAlloggio() {
		return alloggio;
	}

	public void setAlloggio(String alloggio) {
		this.alloggio = alloggio;
	}

	public String getFuorisede() {
		return fuorisede;
	}

	public void setFuorisede(String fuorisede) {
		this.fuorisede = fuorisede;
	}
	
	public String getTrasferta() {
		return trasferta;
	}

	public void setTrasferta(String trasferta) {
		this.trasferta = trasferta;
	}

	@Override
	public Integer getIdVaDatiVacancy() {
		return id;
	}

	public DeTrasfertaDTO getTipoTrasferta() {
		return tipoTrasferta;
	}

	public void setTipoTrasferta(DeTrasfertaDTO tipoTrasferta) {
		this.tipoTrasferta = tipoTrasferta;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.id = idVaDatiVacancy;
	}

	public String getOpzNullaOsta() {
		return opzNullaOsta;
	}

	public void setOpzNullaOsta(String opzNullaOsta) {
		this.opzNullaOsta = opzNullaOsta;
	}

	public List<IDecode> getListaAgevolazioniDTO() {
		return listaAgevolazioniDTO;
	}

	public void setListaAgevolazioniDTO(List<IDecode> listaAgevolazioniDTO) {
		this.listaAgevolazioniDTO = listaAgevolazioniDTO;
	}

	/**
	 * Restituisce la descrizione a fronte del parmetro codice
	 * 
	 * @param codice
	 *            String
	 * @return String
	 */
	public String getOpzione(String codice) {
		return ConstantsSingleton.opzValue(codice);
	}

	public String getUlterioriRequisiti() {
		return ulterioriRequisiti;
	}

	public void setUlterioriRequisiti(String ulterioriRequisiti) {
		this.ulterioriRequisiti = ulterioriRequisiti;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("VaAltreInfoDTO [trasferta=%s, fuorisede=%s, tipoTrasferta=%s, automunito=%s, motomunito=%s, vitto=%s, alloggio=%s, listaAgevolazioniDTO=%s, ulterioriRequisiti=%s]",
						trasferta, fuorisede, tipoTrasferta, automunito, motomunito, vitto, alloggio, listaAgevolazioniDTO,
						ulterioriRequisiti);
	}

	public String getOpzMilite() {
		return opzMilite;
	}

	public void setOpzMilite(String opzMilite) {
		this.opzMilite = opzMilite;
	}

	public DeGenereDTO getDeGenere() {
		return deGenere;
	}

	public void setDeGenere(DeGenereDTO deGenere) {
		this.deGenere = deGenere;
	}

	public DeMotivoGenereSilDTO getDeMotivoGenereSil() {
		return deMotivoGenereSil;
	}

	public void setDeMotivoGenereSil(DeMotivoGenereSilDTO deMotivoGenereSil) {
		this.deMotivoGenereSil = deMotivoGenereSil;
	}

	public String getNotaMotivoGenere() {
		return notaMotivoGenere;
	}

	public void setNotaMotivoGenere(String notaMotivoGenere) {
		this.notaMotivoGenere = notaMotivoGenere;
	}

	public DeAreaSilDTO getDeAreaSil() {
		return deAreaSil;
	}

	public void setDeAreaSil(DeAreaSilDTO deAreaSil) {
		this.deAreaSil = deAreaSil;
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

}
