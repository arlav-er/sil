package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio;

public class MsgMessaggioDTO extends AbstractUpdatablePkDTO implements Serializable {
	private static final long serialVersionUID = -1019680005847508845L;

	/**
	 * idPfPrincipal del destinatario
	 */
	private Integer idTo;
	private DeProvinciaDTO provinciaTo;
	private Integer idFrom;
	private DeRuoloPortaleDTO ruoloPortaleTo;

	private String codTipoMessaggio;
	private DeTemaConsulenzaDTO temaConsulenza;

	private String ticket;
	private String oggetto;
	private String corpo;
	private String mittente;
	private String destinatario;
	private Date scadenza;
	private Boolean letto;

	private Integer idMsgMessaggioPrecedente;

	private List<MsgAllegatoDTO> allegati;

	/**
	 * 
	 * id file allegato
	 */
	private Integer idMsgAllegato;
	/**
	 * nome del file allegato
	 */
	private String allegatoFileName;
	/**
	 * nome del file temporaneo che deve essere allegato
	 */
	private String allegatoFileNameTmp;

	List<MsgMessaggioDTO> risposte;

	List<MsgMessaggioDTO> inoltri;

	private Integer idMsgMessaggioInoltrante;

	/**
	 * Indicatore di invio messaggio anche come notifica ad App
	 */
	private boolean sendNotifyToApp;

	/**
	 * Eventuali informazioni della notifica inviata ad App
	 */
	private Integer idNotifyToApp;
	private StatoNotifica statoNotifyToApp;

	public MsgMessaggioDTO() {
		super();
		setProvinciaTo(new DeProvinciaDTO());
		temaConsulenza = new DeTemaConsulenzaDTO();
		allegati = new ArrayList<MsgAllegatoDTO>();
		letto = true;
	}

	/**
	 * Costruisce una nuova notifica. Il mittente è l'amministratore (utente 0), la scadenza è il giorno attuale + 30,
	 * lo stato è 'non letto'
	 * 
	 * @param codTipoMessaggio
	 */
	public MsgMessaggioDTO(String codTipoMessaggio) {
		this();
		this.codTipoMessaggio = codTipoMessaggio;
		this.idFrom = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, MsgMessaggio.GIORNI_SCADENZA);
		this.scadenza = c.getTime();
		letto = false;
	}

	public Integer getIdTo() {
		return idTo;
	}

	public void setIdTo(Integer idTo) {
		this.idTo = idTo;
	}

	public Integer getIdFrom() {
		return idFrom;
	}

	public void setIdFrom(Integer idFrom) {
		this.idFrom = idFrom;
	}

	public String getCodTipoMessaggio() {
		return codTipoMessaggio;
	}

	public void setCodTipoMessaggio(String codTipoMessaggio) {
		this.codTipoMessaggio = codTipoMessaggio;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	public Date getScadenza() {
		return scadenza;
	}

	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public Boolean getLetto() {
		return letto;
	}

	public void setLetto(Boolean letto) {
		this.letto = letto;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public List<MsgAllegatoDTO> getAllegati() {
		return allegati;
	}

	public DeTemaConsulenzaDTO getTemaConsulenza() {
		return temaConsulenza;
	}

	public void setTemaConsulenza(DeTemaConsulenzaDTO temaConsulenza) {
		this.temaConsulenza = temaConsulenza;
	}

	public void setAllegati(List<MsgAllegatoDTO> allegati) {
		this.allegati = allegati;
	}

	/**
	 * Il proprietario del messaggio e' il pfprincipal che l'ha spedito
	 */
	public boolean isProprietary(Integer idPfPrincipal) {
		return idFrom.equals(idPfPrincipal);
	}

	public Integer getIdMsgMessaggioPrecedente() {
		return idMsgMessaggioPrecedente;
	}

	public void setIdMsgMessaggioPrecedente(Integer idMsgMessaggioPrecedente) {
		this.idMsgMessaggioPrecedente = idMsgMessaggioPrecedente;
	}

	public List<MsgMessaggioDTO> getRisposte() {
		return risposte;
	}

	public void setRisposte(List<MsgMessaggioDTO> risposte) {
		this.risposte = risposte;
	}

	public List<MsgMessaggioDTO> getInoltri() {
		return inoltri;
	}

	public void setInoltri(List<MsgMessaggioDTO> inoltri) {
		this.inoltri = inoltri;
	}

	public Integer getIdMsgMessaggioInoltrante() {
		return idMsgMessaggioInoltrante;
	}

	public void setIdMsgMessaggioInoltrante(Integer idMsgMessaggioInoltrante) {
		this.idMsgMessaggioInoltrante = idMsgMessaggioInoltrante;
	}

	public Integer getIdMsgAllegato() {
		return idMsgAllegato;
	}

	public void setIdMsgAllegato(Integer idAcAllegato) {
		this.idMsgAllegato = idAcAllegato;
	}

	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

	public String getAllegatoFileNameTmp() {
		return allegatoFileNameTmp;
	}

	public void setAllegatoFileNameTmp(String allegatoFileNameTmp) {
		this.allegatoFileNameTmp = allegatoFileNameTmp;
	}

	public MsgMessaggioDTO getLast() {
		if (risposte == null || risposte.isEmpty())
			return this;
		return risposte.get(risposte.size() - 1);
	}

	public DeProvinciaDTO getProvinciaTo() {
		return provinciaTo;
	}

	public void setProvinciaTo(DeProvinciaDTO provinciaTo) {
		this.provinciaTo = provinciaTo;
	}

	public DeRuoloPortaleDTO getRuoloPortaleTo() {
		return ruoloPortaleTo;
	}

	public void setRuoloPortaleTo(DeRuoloPortaleDTO ruoloPortaleTo) {
		this.ruoloPortaleTo = ruoloPortaleTo;
	}

	public boolean getSendNotifyToApp() {
		return sendNotifyToApp;
	}

	public void setSendNotifyToApp(boolean sendNotifyToApp) {
		this.sendNotifyToApp = sendNotifyToApp;
	}

	public Integer getIdNotifyToApp() {
		return idNotifyToApp;
	}

	public void setIdNotifyToApp(Integer idNotifyToApp) {
		this.idNotifyToApp = idNotifyToApp;
	}

	public StatoNotifica getStatoNotifyToApp() {
		return statoNotifyToApp;
	}

	public void setStatoNotifyToApp(StatoNotifica statoNotifyToApp) {
		this.statoNotifyToApp = statoNotifyToApp;
	}

	private Boolean isMessageFromApp_;

	public boolean getIsMessageFromApp() {
		boolean ret = false;

		if (isMessageFromApp_ == null) {
			/*
			 * Questo metodo viene utilizzato dal front-end per visualizzare il check di invio notifica ad app.
			 * L'abilitazione, ad oggi, viene controllata rispetto all'oggetto, se contiene la chiave [App]: un nuovo
			 * ticket inviato da portale non può mai contenere questa chiave. In attesa di analisi future, una notifica
			 * ad app relativa ad un messaggio può essere inviata solamente per tipo messaggio Esperto (l'app inserisce
			 * il messaggio con questa tipologia) e per un messaggio di risposta (che già contiene nell'oggetto la
			 * chiave [App]), non per nuovi ticket.
			 */
			if (StringUtils.startsWith(this.getOggetto(),
					ConstantsSingleton.App.MSG_RICHIESTA_ASSISTENZA_OGGETTO_KEY)) {
				ret = true;
			}
			isMessageFromApp_ = ret;
		} else {
			ret = isMessageFromApp_;
		}

		return ret;

	}

}