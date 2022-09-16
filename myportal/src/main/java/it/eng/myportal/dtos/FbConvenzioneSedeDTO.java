package it.eng.myportal.dtos;

public class FbConvenzioneSedeDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -3493206947784435658L;

	private String nome;
	private String indirizzo;
	private DeComuneDTO comune;
	private String cap;
	private FbConvenzioneDTO convenzione;
	private boolean flgAccreditata = false; // Default FALSE
	private Double latitudine;
	private Double longitudine;
	private String motivoEliminazione;
	private Integer idSedeAccreditataMycas;

	public FbConvenzioneSedeDTO() {
		convenzione = new FbConvenzioneDTO();
		comune = new DeComuneDTO();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public DeComuneDTO getComune() {
		return comune;
	}

	public void setComune(DeComuneDTO comune) {
		this.comune = comune;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public FbConvenzioneDTO getConvenzione() {
		return convenzione;
	}

	public void setConvenzione(FbConvenzioneDTO convenzione) {
		this.convenzione = convenzione;
	}

	public boolean getFlgAccreditata() {
		return flgAccreditata;
	}

	public void setFlgAccreditata(boolean flgAccreditata) {
		this.flgAccreditata = flgAccreditata;
	}

	public Double getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(Double latitudine) {
		this.latitudine = latitudine;
	}

	public Double getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(Double longitudine) {
		this.longitudine = longitudine;
	}

	public String getMotivoEliminazione() {
		return motivoEliminazione;
	}

	public void setMotivoEliminazione(String motivoEliminazione) {
		this.motivoEliminazione = motivoEliminazione;
	}

	public Integer getIdSedeAccreditataMycas() {
		return idSedeAccreditataMycas;
	}

	public void setIdSedeAccreditataMycas(Integer idSedeAccreditataMycas) {
		this.idSedeAccreditataMycas = idSedeAccreditataMycas;
	}
}
