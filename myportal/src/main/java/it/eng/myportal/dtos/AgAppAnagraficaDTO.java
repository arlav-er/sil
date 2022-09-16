package it.eng.myportal.dtos;

import java.util.Date;

public class AgAppAnagraficaDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5815730465749293755L;

	private AgAppuntamentoDTO agAppuntamentoDTO;
	private String codiceFiscale;
	private String codMonoTipoSogg;
	private String nome;
	private String cognome;
	private String sesso;
	private Date dtNascita;
	private DeComuneDTO deComNascitaDTO;
	private String ragioneSociale;
	private DeComuneDTO deComuneDTO;
	private String indirizzo;
	private String cellulare;

	public AgAppuntamentoDTO getAgAppuntamentoDTO() {
		return agAppuntamentoDTO;
	}

	public void setAgAppuntamentoDTO(AgAppuntamentoDTO agAppuntamentoDTO) {
		this.agAppuntamentoDTO = agAppuntamentoDTO;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCodMonoTipoSogg() {
		return codMonoTipoSogg;
	}

	public void setCodMonoTipoSogg(String codMonoTipoSogg) {
		this.codMonoTipoSogg = codMonoTipoSogg;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public Date getDtNascita() {
		return dtNascita;
	}

	public void setDtNascita(Date dtNascita) {
		this.dtNascita = dtNascita;
	}

	public DeComuneDTO getDeComNascitaDTO() {
		return deComNascitaDTO;
	}

	public void setDeComNascitaDTO(DeComuneDTO deComNascitaDTO) {
		this.deComNascitaDTO = deComNascitaDTO;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public DeComuneDTO getDeComuneDTO() {
		return deComuneDTO;
	}

	public void setDeComuneDTO(DeComuneDTO deComuneDTO) {
		this.deComuneDTO = deComuneDTO;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

}
