package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType;
import it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType;

public class ComunicazioniSuccessiveNraBean {

	private BigDecimal prgNuovoRa = null;
	private BigDecimal idDomandaWeb = null;
	private BigDecimal idDomandaIntranet = null;
	private BigDecimal cdnLavoratore = null;
	private String nome = null;
	private String cognome = null;
	private String codiceFiscale = null;
	private String dataNascita = null;
	private String codiceCatastoNascita = null;
	private String indirizzo = null;
	private String cap = null;
	private String codiceComune = null;
	private String codiceProvincia = null;
	private String dataPresentazioneAsDiNra = null;
	private String dataInizioPresentazioneAsDiNra = null;
	private String dataFinePresentazioneAsDiNra = null;
	private double importoGiornalieroNra;
	private double importoComplessivoNra;
	private double importoGiornalieroAsdi;
	private double importoComplessivoAsdi;
	private String noteDifferenze = null;
	private String idComunicazioneMinLav = null;
	private String codiceIntermediario = null;
	private String dataComunicazione = null;
	private String tipoPrestazione = null;
	private String dataCreazioneComunicazione = null;
	private String identificativoComunicazione = null;
	private String codiceOperatore = null;
	private String numeroProvvedimento = null;
	private String dataProvvedimento = null;
	private String esitoElaborazione = null;
	private BigDecimal codiceRicezione = null;
	private String idComunicazioneRichiesta = null;
	private String codMotivoComunicazione = null;
	private String codTipoComunicazione = null;
	private String codTipoEvento = null;
	private String codMotivoSanzione = null;
	private String idComunicazioneAnnullata = null;
	private BigDecimal motivoEvento = null;
	private String descrizioneEvento = null;
	private String notaEvento = null;
	private double importoComplessivoNraDec;
	private double importoComplessivoAsdiDec;
	private String codTipoDomanda = null;
	private BigDecimal numkloNuovoRa = null;
	private BigDecimal cdnutins = null;
	private BigDecimal cdnutmod = null;
	private String dtmins = null;
	private String dtmmod = null;
	private String dataCreazioneComunicazValidata = null;
	private String idComunicazioneValidata = null;
	private String codiceOperatoreValidata = null;
	private String flgInviata = null;
	private String codTipoProv = null;
	private String datVariazioneRes = null;

	public ComunicazioniSuccessiveNraBean() {
		// TODO Auto-generated constructor stub
	}

	public ComunicazioniSuccessiveNraBean(SourceBean row) {
		// TODO Auto-generated constructor stub
		this.idDomandaWeb = SourceBeanUtils.getAttrBigDecimal(row, "IDDOMANDAWEB", new BigDecimal(-1));
		this.idDomandaIntranet = SourceBeanUtils.getAttrBigDecimal(row, "IDDOMANDAINTRANET", new BigDecimal(-1));
		this.nome = SourceBeanUtils.getAttrStr(row, "NOME");
		this.cognome = SourceBeanUtils.getAttrStr(row, "COGNOME");
		this.codiceFiscale = SourceBeanUtils.getAttrStr(row, "CODICEFISCALE");
		this.dataNascita = SourceBeanUtils.getAttrStr(row, "DATANASCITA");
		this.codiceCatastoNascita = SourceBeanUtils.getAttrStr(row, "CODICECATASTONASCITA");
		this.numeroProvvedimento = SourceBeanUtils.getAttrStr(row, "NUMEROPROVVEDIMENTO");
		this.codTipoEvento = SourceBeanUtils.getAttrStr(row, "CODTIPOEVENTO");
		this.dataProvvedimento = SourceBeanUtils.getAttrStr(row, "DATAPROVVEDIMENTO");
		this.dataCreazioneComunicazione = SourceBeanUtils.getAttrStr(row, "DATACREAZIONECOMUNICAZIONE");
		this.identificativoComunicazione = SourceBeanUtils.getAttrStr(row, "IDENTIFICATIVOCOMUNICAZIONE");
		this.codiceOperatore = SourceBeanUtils.getAttrStr(row, "CODICEOPERATORE");
		this.idComunicazioneRichiesta = SourceBeanUtils.getAttrStr(row, "IDCOMUNICAZIONERICHIESTA");

		this.dataCreazioneComunicazValidata = SourceBeanUtils.getAttrStr(row, "DATACREAZIONECOMUNICAZVALIDATA");
		this.idComunicazioneValidata = SourceBeanUtils.getAttrStr(row, "IDCOMUNICAZIONEVALIDATA");
		this.codiceOperatoreValidata = SourceBeanUtils.getAttrStr(row, "CODICEOPERATOREVALIDATA");

	}

	public ComunicazioniSuccessiveNraType comunicazioneDomandaNra()
			throws DatatypeConfigurationException, ParseException {
		ComunicazioniSuccessiveNraType domanda = new ComunicazioniSuccessiveNraType();

		domanda.setIdDomandaWeb(this.idDomandaWeb.intValue());
		domanda.setIdDomandaIntranet(this.idDomandaIntranet.intValue());

		DatiRichiedenteType richiedente = new DatiRichiedenteType();
		richiedente.setNome(this.nome);
		richiedente.setCognome(this.cognome);
		richiedente.setCodiceFiscale(this.codiceFiscale);
		richiedente.setDataNascita(DateUtils.getDate(this.dataNascita));
		richiedente.setCodiceCatastoNascita(this.codiceCatastoNascita);
		domanda.setRichiedente(richiedente);

		if (this.numeroProvvedimento != null) {
			domanda.setNumeroProvvedimento(this.numeroProvvedimento);
		}
		if (this.dataProvvedimento != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.getDate(this.dataProvvedimento));
			domanda.setDataProvvedimento(cal);
		}
		if (this.codTipoEvento != null) {
			domanda.setTipoProvvedimento(TipologiaEventoType.fromValue(this.codTipoEvento));
		}
		if (this.dataCreazioneComunicazValidata != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.getDate(this.dataCreazioneComunicazValidata));
			domanda.setDataCreazioneComunicazione(cal);
		}
		if (this.idComunicazioneValidata != null) {
			domanda.setIdentificativoComunicazione(this.idComunicazioneValidata);
		}
		if (this.codiceOperatoreValidata != null) {
			domanda.setCodiceOperatore(codiceOperatoreValidata);
		}
		if (this.identificativoComunicazione != null) {
			domanda.setIdentificativoComunicazioneRichiesta(identificativoComunicazione);
		}

		return domanda;
	}

}
