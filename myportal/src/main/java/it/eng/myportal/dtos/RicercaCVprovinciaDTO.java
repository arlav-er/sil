package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe contenitore delle informazioni su un CV in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author turro
 * @see AbstractUpdatableDTO
 * 
 */

public class RicercaCVprovinciaDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2698965085713786797L;
	
	
	private String nomePalese;
	private String cognomePalese;
	private String nome;
	private String cognome;
	private String codiceFiscalePalese;
	private String codiceFiscale;
	private Integer idCvDatiPersonali;
	private String descrizioneCV;
	private Date dataInserimentoCV;
	private String comuneDomicilio;
	private String provenienza;
	
	public RicercaCVprovinciaDTO() {
		super();
	}
	
	public RicercaCVprovinciaDTO(String nomePalese, String cognomePalese,String nome, String cognome,String codiceFiscalePalese, String codiceFiscale, Integer idCvDatiPersonali,
			String descrizioneCV, Date dataInserimentoCV, String comuneDomicilio, String provenienza) {
		super();
		this.nomePalese = nomePalese;
		this.cognomePalese = cognomePalese;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.codiceFiscalePalese = codiceFiscalePalese;
		this.idCvDatiPersonali = idCvDatiPersonali;
		this.descrizioneCV = descrizioneCV;
		this.dataInserimentoCV = dataInserimentoCV;
		this.comuneDomicilio = comuneDomicilio;
		this.provenienza = provenienza;
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
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}
	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}
	public String getDescrizioneCV() {
		return descrizioneCV;
	}
	public void setDescrizioneCV(String descrizioneCV) {
		this.descrizioneCV = descrizioneCV;
	}
	public Date getDataInserimentoCV() {
		return dataInserimentoCV;
	}
	public void setDataInserimentoCV(Date dataInserimentoCV) {
		this.dataInserimentoCV = dataInserimentoCV;
	}
	public String getComuneDomicilio() {
		return comuneDomicilio;
	}
	public void setComuneDomicilio(String comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}
	public String getProvenienza() {
		return provenienza;
	}
	public void setProvenienza(String provenienza) {
		this.provenienza = provenienza;
	}

	public String getNomePalese() {
		return nomePalese;
	}

	public void setNomePalese(String nomePalese) {
		this.nomePalese = nomePalese;
	}

	public String getCognomePalese() {
		return cognomePalese;
	}

	public void setCognomePalese(String cognomePalese) {
		this.cognomePalese = cognomePalese;
	}

	public String getCodiceFiscalePalese() {
		return codiceFiscalePalese;
	}

	public void setCodiceFiscalePalese(String codiceFiscalePalese) {
		this.codiceFiscalePalese = codiceFiscalePalese;
	}
	
}
