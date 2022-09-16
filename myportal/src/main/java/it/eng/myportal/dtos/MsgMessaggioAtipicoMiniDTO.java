package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;

/**
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author turro
 * @see AbstractUpdatableDTO
 * 
 */

public class MsgMessaggioAtipicoMiniDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2698965085713786797L;
	
	private String ticket;
	private String oggetto;
	private String corpo;
	private String codTipoLavoro;
	private String tipoLavoro;
	private String statoPratica;
	private Date dtInvio;
	private Date dtInoltroCoord;
	private Date dtInoltroCPI;
	private Date dtRisposta;
	private Boolean daLeggere;
	private String codStatoPratica;	
	private String codTipoPratica;
	private String tipoPratica;
	private String nome;
	private String cognome;
	private String targaCPIincaricato;
	
	
	
	
	public String getCodStatoPratica() {
		return codStatoPratica;
	}

	public void setCodStatoPratica(String codStatoPratica) {
		this.codStatoPratica = codStatoPratica;
	}

	public MsgMessaggioAtipicoMiniDTO() {
		super();
		
	}
	
	public MsgMessaggioAtipicoMiniDTO(
				Integer id,
				String oggetto, 
				String corpo,
				String codStatoPratica, 
				String statoPratica, 
				Date invio, 
				Date dtInoltroCoord,
				Date dtInoltroCPI, 
				Date dtRisposta, 
				String codTipoLavoro,
				String tipoLavoro,
				String codTipoPratica,
				String tipoPratica,
				String nome,
				String cognome,
				String targaCPIincaricato) {
		super();
		
		this.id = id;
		this.ticket = id.toString();
		this.oggetto = oggetto;
		this.corpo = corpo.substring(0, Math.min(29, corpo.length()));
		this.codStatoPratica = codStatoPratica;
		this.statoPratica = statoPratica;
		this.dtInvio = invio;
		this.dtInoltroCoord = dtInoltroCoord;
		this.dtInoltroCPI = dtInoltroCPI;
		this.dtRisposta = dtRisposta;
		this.codTipoLavoro = codTipoLavoro;
		this.tipoLavoro = tipoLavoro;
		this.tipoPratica = tipoPratica;
		this.codTipoPratica = codTipoPratica;
		this.nome = nome;
		this.cognome = cognome;
		this.targaCPIincaricato = targaCPIincaricato;
		
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public Date getDtInvio() {
		return dtInvio;
	}

	public void setDtInvio(Date dtInvio) {
		this.dtInvio = dtInvio;
	}

	public Boolean getDaLeggere() {
		return daLeggere;
	}

	public void setDaLeggere(Boolean daLeggere) {
		this.daLeggere = daLeggere;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	

	public String getStatoPratica() {
		return statoPratica;
	}

	public void setStatoPratica(String deStatoPratica) {
		this.statoPratica = deStatoPratica;
	}

	public String getTipoLavoro() {
		return tipoLavoro;
	}

	public void setTipoLavoro(String tipoLavoro) {
		this.tipoLavoro = tipoLavoro;
	}

	public Date getDtInoltroCoord() {
		return dtInoltroCoord;
	}

	public void setDtInoltroCoord(Date dtInoltroCoord) {
		this.dtInoltroCoord = dtInoltroCoord;
	}

	public Date getDtInoltroCPI() {
		return dtInoltroCPI;
	}

	public void setDtInoltroCPI(Date dtInoltroCPI) {
		this.dtInoltroCPI = dtInoltroCPI;
	}

	public Date getDtRisposta() {
		return dtRisposta;
	}

	public void setDtRisposta(Date dtRisposta) {
		this.dtRisposta = dtRisposta;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	public String getTipoPratica() {
		return tipoPratica;
	}

	public void setTipoPratica(String tipoPratica) {
		this.tipoPratica = tipoPratica;
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

	public String getTargaCPIincaricato() {
		return targaCPIincaricato;
	}

	public void setTargaCPIincaricato(String targaCPIincaricato) {
		this.targaCPIincaricato = targaCPIincaricato;
	}

	public String getCodTipoPratica() {
		return codTipoPratica;
	}

	public void setCodTipoPratica(String codTipoPratica) {
		this.codTipoPratica = codTipoPratica;
	}
	
	
	public boolean isPartitaIva() {
		return ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA.equals(codTipoLavoro);
	}
	
	
	public boolean isAtipico() {
		return ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(codTipoLavoro);
	}
}
