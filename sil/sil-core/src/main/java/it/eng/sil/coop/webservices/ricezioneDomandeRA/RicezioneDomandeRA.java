package it.eng.sil.coop.webservices.ricezioneDomandeRA;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.axis.AxisFault;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.CategoriaEsitoType;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.ComunicazioneDatiDomandaType;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.ComunicazioniEventiSuccessiviType;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.EsitoComunicazioneType;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.ObjectFactory;
import it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat.UtilityRa;

public class RicezioneDomandeRA implements RicezioneDomandeRAInterface {

	private static final long serialVersionUID = 1L;

	private static final String COD_MONO_TIPO_DOMANDA = "N";
	private static final String COD_MONO_TIPO_EVENTO_SUCC = "S";
	private static final String STATO_DOMANDA_DA_ESAMINARE = "DES";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicezioneDomandeRA.class.getName());
	private static final BigDecimal userSP = new BigDecimal("150");
	private JAXBElement<?> dettNra;

	public RicezioneDomandeRA() {
	}

	public RicezioneDomandeRA(String strDomanda) {

		try {
			System.out.println(processDomanda(strDomanda));

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String processDomanda(String inputXML) throws Throwable {
		String outputXML = null;
		ComunicazioneDatiDomandaType comDatiDom = null;
		ComunicazioniEventiSuccessiviType comEventiSucc = null;
		boolean esitoVal = false;
		boolean esito = false;
		try {
			Schema schema = UtilityRa.getXsdSchema(UtilityRa.XSD_DOMANDA, UtilityRa.XSD_COMMON);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(inputXML.trim()));
			validator.validate(datiXmlStreamSource);
			esitoVal = true;
		} catch (Exception e) {
			String validityError = "Errore di validazione xml: " + e.getMessage();
			_logger.error(validityError);
			_logger.warn(inputXML);
		}

		if (esitoVal) {
			dettNra = convertToDomanda(inputXML);

			if (dettNra != null) {
				if (dettNra.getValue() instanceof ComunicazioneDatiDomandaType) {
					comDatiDom = (ComunicazioneDatiDomandaType) dettNra.getValue();
					esito = inserisciDomanda(comDatiDom);
				} else if (dettNra.getValue() instanceof ComunicazioniEventiSuccessiviType) {
					comEventiSucc = (ComunicazioniEventiSuccessiviType) dettNra.getValue();
					esito = inserisciEventoSuccessivo(comEventiSucc);
				}

			}
		}

		if (esito) {
			outputXML = ritornoEsito("A100", "Nessun Errore", "OK");
		} else {
			outputXML = ritornoEsito("A999", "Errore Acquisizione", "KO");
		}

		return outputXML;
	}

	private JAXBElement<?> convertToDomanda(String strDomanda) {
		JAXBContext jaxbContext;
		JAXBElement<?> domandaType = null;
		try {

			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Schema schema = UtilityRa.getXsdSchema(UtilityRa.XSD_DOMANDA, UtilityRa.XSD_COMMON);
			jaxbUnmarshaller.setSchema(schema);
			domandaType = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new StringReader(strDomanda));

		} catch (Exception e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return domandaType;
	}

	private boolean inserisciDomanda(ComunicazioneDatiDomandaType domanda) throws Exception {
		TransactionQueryExecutor transExec = null;
		BigDecimal cdnlavoratore = null;
		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] params = new Object[] { domanda.getRichiedente().getCodiceFiscale() };
			SourceBean row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				cdnlavoratore = (BigDecimal) row.getAttribute("cdnLavoratore");
			}

			Boolean resDomanda = (Boolean) transExec.executeQuery("COOP_INS_DOMANDA_REDDITO_ATTIVAZIONE", new Object[] {
					domanda.getIdDomandaWeb(), domanda.getIdDomandaIntranet(), cdnlavoratore,
					domanda.getRichiedente().getNome(), domanda.getRichiedente().getCognome(),
					domanda.getRichiedente().getCodiceFiscale(),
					DateUtils.formatXMLGregorian(domanda.getRichiedente().getDataNascita()),
					domanda.getRichiedente().getCodiceCatastoNascita(),
					domanda.getResidenzaRichiedente().getIndirizzo(), domanda.getResidenzaRichiedente().getCap(),
					domanda.getResidenzaRichiedente().getCodiceComune(),
					domanda.getResidenzaRichiedente().getCodiceProvincia(),
					DateUtils.formatXMLGregorian(domanda.getDataPresentazioneAsdiNra()),
					DateUtils.formatXMLGregorian(domanda.getDataInizioPrestazioneAsdiNra()),
					DateUtils.formatXMLGregorian(domanda.getDataFinePrestazioneAsdiNra()),
					domanda.getImportoGiornalieroNra(), domanda.getImportoComplessivoNra(),
					domanda.getImportoGiornalieroAsdi(), domanda.getImportoComplessivoAsdi(),
					domanda.getNoteDifferenze() != null ? domanda.getNoteDifferenze() : null,
					domanda.getDatiSottoscrizioneProgettoPersonalizzato() != null
							? domanda.getDatiSottoscrizioneProgettoPersonalizzato().getIdComunicazioneMinisteroLavoro()
							: null,
					domanda.getDatiSottoscrizioneProgettoPersonalizzato() != null
							? domanda.getDatiSottoscrizioneProgettoPersonalizzato().getCodiceIntermediario()
							: null,
					domanda.getDatiSottoscrizioneProgettoPersonalizzato() != null
							? DateUtils.formatXMLGregorian(
									domanda.getDatiSottoscrizioneProgettoPersonalizzato().getDataComunicazione())
							: null,
					domanda.getTipoPrestazione().value(),
					DateUtils.formatXMLGregorian(domanda.getDataCreazioneComunicazione()),
					domanda.getIdentificativoComunicazione(), domanda.getCodiceOperatore(), COD_MONO_TIPO_DOMANDA,
					domanda.getCodiceReiezione(), STATO_DOMANDA_DA_ESAMINARE, userSP, userSP }, "INSERT");

			if (resDomanda == null || !resDomanda.booleanValue()) {
				_logger.error("Errore acquisizione domanda reddito attivazione");
				transExec.rollBackTransaction();
				return false;
			} else {
				transExec.commitTransaction();
				return true;
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			String validityError = "Errore in acquisizione domanda reddito attivazione: " + e.getMessage();
			_logger.error(validityError);
			return false;
		}
	}

	private boolean inserisciEventoSuccessivo(ComunicazioniEventiSuccessiviType domanda) throws Exception {
		TransactionQueryExecutor transExec = null;
		BigDecimal cdnlavoratore = null;
		String codiceIntermediario = null;
		String motivoComunicazione = null;
		String tipoComunicazione = null;
		String motivoSanzione = null;
		String identificativoComunicazioneAnnullata = null;
		String tipoEvento = null;
		Integer motivoEvento = null;
		String descrizioneEvento = null;
		String notaEvento = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Object[] params = new Object[] { domanda.getRichiedente().getCodiceFiscale() };
			SourceBean row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				cdnlavoratore = (BigDecimal) row.getAttribute("cdnLavoratore");
			}

			if (domanda.getComunicazioneMinisteroLavoro() != null) {
				codiceIntermediario = domanda.getComunicazioneMinisteroLavoro().getCodiceIntermediario();
				motivoComunicazione = domanda.getComunicazioneMinisteroLavoro().getMotivoComunicazione().value();
				tipoComunicazione = domanda.getComunicazioneMinisteroLavoro().getTipoComunicazione().value();
				tipoEvento = domanda.getComunicazioneMinisteroLavoro().getTipoEvento().value();
				motivoSanzione = domanda.getComunicazioneMinisteroLavoro().getMotivoSanzione().value();
				identificativoComunicazioneAnnullata = domanda.getComunicazioneMinisteroLavoro()
						.getIdentificativoComunicazioneAnnullata();
			} else if (domanda.getAltreComunicazioni() != null) {
				tipoEvento = domanda.getAltreComunicazioni().getTipoEvento().value();
				motivoEvento = domanda.getAltreComunicazioni().getMotivoEvento();
				descrizioneEvento = domanda.getAltreComunicazioni().getDescrizioneEvento();
				notaEvento = domanda.getAltreComunicazioni().getNotaEvento();
			}

			Boolean resDomanda = (Boolean) transExec.executeQuery("COOP_INS_EVENTO_SUCC_REDDITO_ATTIVAZIONE",
					new Object[] { domanda.getIdDomandaWeb(), domanda.getIdDomandaIntranet(), cdnlavoratore,
							domanda.getRichiedente().getNome(), domanda.getRichiedente().getCognome(),
							domanda.getRichiedente().getCodiceFiscale(),
							DateUtils.formatXMLGregorian(domanda.getRichiedente().getDataNascita()),
							domanda.getRichiedente().getCodiceCatastoNascita(), codiceIntermediario,
							motivoComunicazione, tipoComunicazione, tipoEvento, motivoSanzione,
							identificativoComunicazioneAnnullata, motivoEvento, descrizioneEvento, notaEvento,
							DateUtils.formatXMLGregorian(domanda.getDataCreazioneComunicazione()),
							domanda.getIdentificativoComunicazione(), domanda.getCodiceOperatore(),
							domanda.getImportoComplessivoNraDecurtato(), domanda.getImportoComplessivoAsdiDecurtato(),
							COD_MONO_TIPO_EVENTO_SUCC,
							(domanda.getAltreComunicazioni() != null
									&& domanda.getAltreComunicazioni().getDataEvento() != null)
											? DateUtils
													.formatXMLGregorian(domanda.getAltreComunicazioni().getDataEvento())
											: null,
							STATO_DOMANDA_DA_ESAMINARE, userSP, userSP },
					"INSERT");

			if (resDomanda == null || !resDomanda.booleanValue()) {
				_logger.error("Errore acquisizione evento successivo reddito attivazione");
				transExec.rollBackTransaction();
				return false;
			} else {
				transExec.commitTransaction();
				return true;
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			String validityError = "Errore in acquisizione evento successivo reddito attivazione: " + e.getMessage();
			_logger.error(validityError);
			return false;
		}
	}

	private String ritornoEsito(String codice, String descrizione, String categoria) throws Throwable {
		String xmlEsito = "";
		EsitoComunicazioneType esito = null;

		esito = new EsitoComunicazioneType();
		esito.setCodice(codice);
		esito.setDescrizione(descrizione);
		esito.setCategoria(CategoriaEsitoType.fromValue(categoria));

		xmlEsito = convertInputToString(esito);

		return xmlEsito;
	}

	private String convertInputToString(EsitoComunicazioneType msg) throws Throwable {
		JAXBContext jaxbContext = JAXBContext.newInstance(EsitoComunicazioneType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// da recuperare l'xsd nella cartella corretta
		Schema schema = UtilityRa.getXsdSchema(UtilityRa.XSD_DOMANDA, UtilityRa.XSD_COMMON);
		jaxbMarshaller.setSchema(schema);

		StringWriter writer = new StringWriter();

		// format the XML output
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

		JAXBElement<EsitoComunicazioneType> root = null;
		ObjectFactory obj = new ObjectFactory();

		if (dettNra != null) {
			if (dettNra.getValue() instanceof ComunicazioneDatiDomandaType) {
				root = obj.createComunicazioneDatiDomandaOutput(msg);
			} else if (dettNra.getValue() instanceof ComunicazioniEventiSuccessiviType) {
				root = obj.createComunicazioneEventiSuccessiviOutput(msg);
			}
		} else {
			throw new AxisFault("Xml non valido");
		}

		jaxbMarshaller.marshal(root, writer);

		String xml = writer.getBuffer().toString();
		return xml;
	}

	public static void main(String[] args) {

		String strDomanda = "<tns:comunicazioneDatiDomanda xmlns:tns=\"http://www.inps.it/comunicazioniAsdiNraRequest/0.0.1\" xmlns:tns1=\"http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.inps.it/comunicazioniAsdiNraRequest/0.0.1 Collaborazione_NRA_INPS_To_PAT.xsd\">"
				+ "<tns:idDomandaWeb>0</tns:idDomandaWeb>" + "<tns:idDomandaIntranet>0</tns:idDomandaIntranet>"
				+ "<tns:richiedente>" + " <tns1:nome>tns1:nome</tns1:nome>"
				+ " <tns1:cognome>tns1:cognome</tns1:cognome>"
				+ " <tns1:codiceFiscale>AAAAAA00A01H501Z</tns1:codiceFiscale>"
				+ " <tns1:dataNascita>2001-01-01</tns1:dataNascita>"
				+ " <tns1:codiceCatastoNascita>H501</tns1:codiceCatastoNascita>" + "</tns:richiedente>"
				+ "<tns:residenzaRichiedente>" + "  <tns1:indirizzo>tns1:indirizzo</tns1:indirizzo>"
				+ "  <tns1:cap>00100</tns1:cap>" + "  <tns1:codiceComune>H501</tns1:codiceComune>"
				+ "</tns:residenzaRichiedente>"
				+ "<tns:dataPresentazioneAsdiNra>2001-12-31T12:00:00</tns:dataPresentazioneAsdiNra>"
				+ "<tns:dataInizioPrestazioneAsdiNra>2001-01-01</tns:dataInizioPrestazioneAsdiNra>"
				+ "<tns:dataFinePrestazioneAsdiNra>2001-01-01</tns:dataFinePrestazioneAsdiNra>"
				+ "<tns:importoGiornalieroNra>0.0</tns:importoGiornalieroNra>"
				+ "<tns:importoComplessivoNra>0.0</tns:importoComplessivoNra>"
				+ "<tns:importoGiornalieroAsdi>0.0</tns:importoGiornalieroAsdi>"
				+ "<tns:importoComplessivoAsdi>0.0</tns:importoComplessivoAsdi>"
				+ "<tns:datiSottoscrizioneProgettoPersonalizzato>"
				+ "  <tns:idComunicazioneMinisteroLavoro>20160101000000000</tns:idComunicazioneMinisteroLavoro>"
				+ "  <tns:codiceIntermediario>12345678901</tns:codiceIntermediario>"
				+ "  <tns:dataComunicazione>2001-12-31T12:00:00</tns:dataComunicazione>"
				+ "</tns:datiSottoscrizioneProgettoPersonalizzato>" + "<tns:tipoPrestazione>NRA</tns:tipoPrestazione>"
				+ "<tns1:dataCreazioneComunicazione>2001-12-31T12:00:00</tns1:dataCreazioneComunicazione>"
				+ "<tns1:identificativoComunicazione>20160101000000000</tns1:identificativoComunicazione>"
				+ "<tns1:codiceOperatore>batch</tns1:codiceOperatore>" + "</tns:comunicazioneDatiDomanda>";

		String strComunicazioniSuccessive = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<tns:comunicazioneEventiSuccessivi xmlns:tns=\"http://www.inps.it/comunicazioniAsdiNraRequest/0.0.1\" xmlns:tns1=\"http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.inps.it/comunicazioniAsdiNraRequest/0.0.1 Collaborazione_NRA_INPS_To_PAT.xsd\">"
				+ " <tns:idDomandaWeb>0</tns:idDomandaWeb>" + " <tns:idDomandaIntranet>0</tns:idDomandaIntranet>"
				+ " <tns:richiedente>" + "   <tns1:nome>nome</tns1:nome>" + "   <tns1:cognome>cognome</tns1:cognome>"
				+ "   <tns1:codiceFiscale>RSSBLW76B10Z301S</tns1:codiceFiscale>"
				+ "   <tns1:dataNascita>2001-01-01</tns1:dataNascita>"
				+ "   <tns1:codiceCatastoNascita>H501</tns1:codiceCatastoNascita>" + " </tns:richiedente>"
				+ " <tns:comunicazioneMinisteroLavoro>"
				+ "   <tns:codiceIntermediario>12345678901</tns:codiceIntermediario>"
				+ "   <tns:motivoComunicazione>N</tns:motivoComunicazione>"
				+ "   <tns:tipoComunicazione>FTS</tns:tipoComunicazione>" + "   <tns:tipoEvento>DCR</tns:tipoEvento>"
				+ " </tns:comunicazioneMinisteroLavoro>"
				+ " <tns1:dataCreazioneComunicazione>2001-01-01T00:00:00</tns1:dataCreazioneComunicazione>"
				+ " <tns1:identificativoComunicazione>20010101000000000</tns1:identificativoComunicazione>"
				+ " <tns1:codiceOperatore>batch</tns1:codiceOperatore>" + "</tns:comunicazioneEventiSuccessivi>";

		try {
			RicezioneDomandeRA domanda = new RicezioneDomandeRA(strDomanda);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
