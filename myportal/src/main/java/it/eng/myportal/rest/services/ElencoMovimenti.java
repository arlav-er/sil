package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.exception.ElencoMovimentiException;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.movimenti.GetUltimoMovimentoProxy;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Stateless
@Path("rest/services/")
public class ElencoMovimenti {

	protected static Log log = LogFactory.getLog(ElencoMovimenti.class);

	private final static int ESITO_POSITIVO = 0;

	private final static String EL_CODICE_FISC = "CodiceFiscale";
	private final static String EL_ID_PROV = "IdProvincia";

	private final static String EL_ULTIMOPERIODO = "UltimoPeriodo";
	private final static String EL_RAPPORTOLAVORATIVO = "RapportoLavorativo";
	private final static String EL_DATAFINERAPPORTO = "DataFineRapporto";
	private final static String EL_ESITO = "Esito";
	private final static String EL_CODICE = "codice";
	private final static String EL_DESCRIZIONE = "descrizione";

	private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	private String TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<UltimoPeriodo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" schemaVersion=\"1\">    <Esito>        <codice>00</codice>        <descrizione>Nessun Errore</descrizione>  "
			+ "  </Esito>    <DatiLavoratore>        <CodiceFiscale>CCTRSR66E21C002D</CodiceFiscale>        <Cognome>ACCATTATO</Cognome>        <Nome>ROSARIO</Nome>        <DataNascita>1966-05-21</DataNascita>        <CodComNascita>C002</CodComNascita>        <ComNascita>CASSANO ALLO IONIO</ComNascita>        <TargaComNascita>CS</TargaComNascita>        <Sesso>M</Sesso>        <Cittadinanza>000</Cittadinanza>      "
			+ "  <Residenza>            <CodiceComune>F257</CodiceComune>            <DescrComune>MODENA</DescrComune>            <Targa>MO</Targa>            <Indirizzo>VIA ZAPPELLACCIO 73</Indirizzo>        </Residenza>        <Domicilio>            <CodiceComune>F257</CodiceComune>            <DescrComune>MODENA</DescrComune>            <Targa>MO</Targa>            <Indirizzo>VIA ZAPPELLACCIO 73</Indirizzo>        </Domicilio>  "
			+ "  </DatiLavoratore>    <DidStipulabile>        <codice>10</codice>        <descrizione>Superamento reddito</descrizione>    </DidStipulabile>    "
			+ "<RapportoLavorativo>        <DataInizioRapporto>2012-01-01</DataInizioRapporto>        <DataFineRapporto xsi:nil=\"true\"/>        <DatiAzienda>            <CodiceFiscale>00000001178</CodiceFiscale>      "
			+ "      <RagioneSociale>RISTORANTE GROTTA AZZURRA</RagioneSociale>         "
			+ "   <CodComuneSede>F257</CodComuneSede>            <ComuneSede>MODENA</ComuneSede>            <TargaSede>MO</TargaSede>            <Indirizzo>VIA NONANTOLANA</Indirizzo>            <Cap>41100</Cap>            <CodAteco>55300</CodAteco>            <Telefono>059</Telefono>       "
			+ "    <Fax xsi:nil=\"true\"/>            <Email xsi:nil=\"true\"/>            <NumAlbo xsi:nil=\"true\"/>        </DatiAzienda>        <DatiAziendaLegale>            <CodComuneLegale>F257</CodComuneLegale>            <ComuneLegale>MODENA</ComuneLegale>            <TargaLegale>MO</TargaLegale>            <IndirizzoLegale>VIA NONANTOLANA</IndirizzoLegale>         "
			+ "   <CapLegale>41100</CapLegale>            <TelefonoLegale>059</TelefonoLegale>            <FaxLegale xsi:nil=\"true\"/>            <EmailLegale xsi:nil=\"true\"/>        </DatiAziendaLegale>        <Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine xsi:nil=\"true\"/>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>     "
			+ "       <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>N</RedditoSanato>            <RetribuzioneMensile xsi:nil=\"true\"/>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>      "
			+ "      <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>         "
			+ "   <PubblicaAmministrazione>N</PubblicaAmministrazione>    "
			+ "        <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>      "
			+ "      </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>       "
			+ " </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine>2012-05-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>780</RetribuzioneMensile>           <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/> "
			+ "           <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>     "
			+ "           <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>     "
			+ "        <DataFine>2013-01-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>         "
			+ "   <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>1250</RetribuzioneMensile>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                "
			+ "<Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento>    "
			+ "</RapportoLavorativo>"
			+ "<RapportoLavorativo>        <DataInizioRapporto>2012-01-01</DataInizioRapporto>        <DataFineRapporto>2013-01-01</DataFineRapporto>        <DatiAzienda>            <CodiceFiscale>00000001178</CodiceFiscale>      "
			+ "      <RagioneSociale>RISTORANTE GROTTA AZZURRA</RagioneSociale>         "
			+ "   <CodComuneSede>F257</CodComuneSede>            <ComuneSede>MODENA</ComuneSede>            <TargaSede>MO</TargaSede>            <Indirizzo>VIA NONANTOLANA</Indirizzo>            <Cap>41100</Cap>            <CodAteco>55300</CodAteco>            <Telefono>059</Telefono>       "
			+ "    <Fax xsi:nil=\"true\"/>            <Email xsi:nil=\"true\"/>            <NumAlbo xsi:nil=\"true\"/>        </DatiAzienda>        <DatiAziendaLegale>            <CodComuneLegale>F257</CodComuneLegale>            <ComuneLegale>MODENA</ComuneLegale>            <TargaLegale>MO</TargaLegale>            <IndirizzoLegale>VIA NONANTOLANA</IndirizzoLegale>         "
			+ "   <CapLegale>41100</CapLegale>            <TelefonoLegale>059</TelefonoLegale>            <FaxLegale xsi:nil=\"true\"/>            <EmailLegale xsi:nil=\"true\"/>        </DatiAziendaLegale>        <Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine xsi:nil=\"true\"/>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>     "
			+ "       <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>N</RedditoSanato>            <RetribuzioneMensile xsi:nil=\"true\"/>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>      "
			+ "      <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>         "
			+ "   <PubblicaAmministrazione>N</PubblicaAmministrazione>    "
			+ "        <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>      "
			+ "      </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>       "
			+ " </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine>2012-05-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>780</RetribuzioneMensile>           <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/> "
			+ "           <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>     "
			+ "           <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>     "
			+ "        <DataFine>2013-01-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>         "
			+ "   <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>1250</RetribuzioneMensile>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                "
			+ "<Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento>    "
			+ "</RapportoLavorativo>"
			+ "<RapportoLavorativo>        <DataInizioRapporto>2010-01-01</DataInizioRapporto>        <DataFineRapporto>2011-01-01</DataFineRapporto>        <DatiAzienda>            <CodiceFiscale>00000001178</CodiceFiscale>      "
			+ "      <RagioneSociale>RISTORANTE GROTTA AZZURRA</RagioneSociale>         "
			+ "   <CodComuneSede>F257</CodComuneSede>            <ComuneSede>MODENA</ComuneSede>            <TargaSede>MO</TargaSede>            <Indirizzo>VIA NONANTOLANA</Indirizzo>            <Cap>41100</Cap>            <CodAteco>55300</CodAteco>            <Telefono>059</Telefono>       "
			+ "    <Fax xsi:nil=\"true\"/>            <Email xsi:nil=\"true\"/>            <NumAlbo xsi:nil=\"true\"/>        </DatiAzienda>        <DatiAziendaLegale>            <CodComuneLegale>F257</CodComuneLegale>            <ComuneLegale>MODENA</ComuneLegale>            <TargaLegale>MO</TargaLegale>            <IndirizzoLegale>VIA NONANTOLANA</IndirizzoLegale>         "
			+ "   <CapLegale>41100</CapLegale>            <TelefonoLegale>059</TelefonoLegale>            <FaxLegale xsi:nil=\"true\"/>            <EmailLegale xsi:nil=\"true\"/>        </DatiAziendaLegale>        <Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine xsi:nil=\"true\"/>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>     "
			+ "       <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>N</RedditoSanato>            <RetribuzioneMensile xsi:nil=\"true\"/>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>      "
			+ "      <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>         "
			+ "   <PubblicaAmministrazione>N</PubblicaAmministrazione>    "
			+ "        <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>      "
			+ "      </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>       "
			+ " </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>            <DataFine>2012-05-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>            <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>780</RetribuzioneMensile>           <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/> "
			+ "           <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>     "
			+ "           <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                <Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento><Movimento tipo=\"AVV\">            <ChiaveMovimento>4447976</ChiaveMovimento>            <TipoMovimento>AVV</TipoMovimento>            <DataInizioAvviamento>2012-01-01</DataInizioAvviamento>            <DataInizio>2012-01-01</DataInizio>     "
			+ "        <DataFine>2013-01-01</DataFine>            <CodLivelloIstruzione xsi:nil=\"true\"/>            <CodTipoContratto>A.01.00</CodTipoContratto>         "
			+ "   <DescTipoContratto>LAVORO A TEMPO INDETERMINATO</DescTipoContratto>            <CodQualificaProf>5225201</CodQualificaProf>            <CodCCNL>167</CodCCNL>            <CodOrario>F</CodOrario>            <NumOrePartTime>0</NumOrePartTime>            <RedditoSanato>S</RedditoSanato>            <RetribuzioneMensile>1250</RetribuzioneMensile>            <Livello>5</Livello>            <FlagLegge68>N</FlagLegge68>            <DatConvenzioneNullaOstaLegge68 xsi:nil=\"true\"/>            <NumConvenzioneLegge68 xsi:nil=\"true\"/>            <FlagSocio xsi:nil=\"true\"/>            <CodEnte/>            <CodEntePrev/>            <PatINAL/>            <FlagLavoroAgricoltura>N</FlagLavoroAgricoltura>            <GiornatePrevisteLavoroAgricoltura xsi:nil=\"true\"/>            <TipoLavAgricoltura/>            <PubblicaAmministrazione>N</PubblicaAmministrazione>            <DatiLegaleRappresentante>                <Cognome xsi:nil=\"true\"/>                <Nome xsi:nil=\"true\"/>                <Sesso xsi:nil=\"true\"/>                <DataNascita xsi:nil=\"true\"/>                <Comune xsi:nil=\"true\"/>                "
			+ "<Cittadinanza xsi:nil=\"true\"/>                <NumTitoloSoggiorno xsi:nil=\"true\"/>            </DatiLegaleRappresentante>            <ChiaveMovPrec/>            <ChiaveMovSucc/>            <CodLock>42559606</CodLock>        </Movimento>    "
			+ "</RapportoLavorativo>" + "</UltimoPeriodo>";

	@GET
	@Path("elenco_movimenti")
	public String getElencoMovimenti(@QueryParam("user_id") Integer idPfPrincipal, @Context HttpServletRequest request) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			// SICUREZZA. Controllo che l'utente abbia richiesto il proprio
			// stato occupazionale e che ne abbia la facoltà
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("ElencoMovimenti fallita: sessione nulla, utente non loggato");
				throw new ElencoMovimentiException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());
			
			if (idPfPrincipal == null) {
				log.error("ElencoMovimenti fallita: id utente non passato");
				throw new ElencoMovimentiException();
			}
			
			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteDTO == null) {
				log.error("ElencoMovimenti fallita: recupero utente nullo username="+username);
				throw new ElencoMovimentiException();
			}
			
			if (utenteDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new ElencoMovimentiException("Non puoi richiedere l'elenco dei movimenti di un altro lavoratore.");
			}
			if (!utenteDTO.getAbilitatoServizi()) {
				throw new ElencoMovimentiException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}
			if (StringUtils.isEmpty(utenteDTO.getCodiceFiscale())) {
				throw new ElencoMovimentiException("Non hai indicato il tuo codice fiscale.");
			}
			DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element statoOccupazionale = (Element) document.createElement(EL_ULTIMOPERIODO);

			document.appendChild(statoOccupazionale);

			Element codiceFiscale = document.createElement(EL_CODICE_FISC);
			codiceFiscale.appendChild(document.createTextNode(utenteDTO.getCodiceFiscale().toUpperCase()));
			Element idProvincia = document.createElement(EL_ID_PROV);
			idProvincia.appendChild(document.createTextNode(provinciaRif.getId()));
			statoOccupazionale.appendChild(codiceFiscale);
			statoOccupazionale.appendChild(idProvincia);

			String ret = Utils.domToString(document);
			Utils.validateXml(ret, "servizi" + File.separator + "listaMovimenti.xsd");

			String endpoint = wsEndpointHome.getElencoMovimentiAddress(provinciaRif.getId());
			GetUltimoMovimentoProxy service = new GetUltimoMovimentoProxy(endpoint);

			String response = service.getUltimoMovimento(ret);
			//String response = TEST_XML;

			return elaboraRisposta(response);
		} catch (ParserConfigurationException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (TransformerException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (SAXException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (IOException e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		} catch (ElencoMovimentiException e) {
			log.error(e);
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (Exception e) {
			log.error(e);
			return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
		}
	}

	/**
	 * Elabora la risposta del WebService e restituisce il JSON da passare
	 * all'interfaccia
	 * 
	 * @param response
	 * @return
	 * @throws JSONException
	 *             nel caso succedano casini mentre elaboro il JSON
	 * @throws ElencoMovimentiException
	 *             nel caso sia necessario mostrare un errore all'utente.
	 * @throws ParseException nel caso ci siano date non formattate nella maniera opportune
	 */
	private String elaboraRisposta(String response) throws ElencoMovimentiException, JSONException, ParseException {
		JSONObject resp = XML.toJSONObject(response);
		JSONObject risposta = resp.getJSONObject(EL_ULTIMOPERIODO);
		JSONObject esito = risposta.getJSONObject(EL_ESITO);
		Integer codEsito = esito.getInt(EL_CODICE);
		// il servizio ha risposto correttamente
		if (codEsito.intValue() == ESITO_POSITIVO) {
			JSONArray rapportiLavorativi = new JSONArray(); // memorizzo in
															// una variabile
															// tutti i
															// rapportiLavorativi
															// che mi sono
															// arrivati
			Object rappLav = risposta.get(EL_RAPPORTOLAVORATIVO);
			if (rappLav != null) { // se non vi sono rapporti di lavoro
				if (rappLav instanceof JSONArray) { // se c'è più di un
													// rapporto di lavoro
					rapportiLavorativi = (JSONArray) rappLav;
				} else {
					rapportiLavorativi.put(rappLav);
				}
			}
			// è d'obbligo che nella risposta vi sia un unico
			// rapportoLavorativo. lo memorizzo qui.
			JSONObject lastRapporto = null;
			int countAperti = 0; // conto quanti sono i rapporti validi
									// all'interno della risposta.
			for (int i = 0; i < rapportiLavorativi.length(); i++) {
				JSONObject rapporto = (JSONObject) rapportiLavorativi.get(i);
				Object dataFineObj = rapporto.get(EL_DATAFINERAPPORTO);
				if (dataFineObj instanceof String) {
					java.util.Date dataFine = formatter1.parse((String) dataFineObj);
					if (dataFine.compareTo(new java.util.Date()) > 0) {
						countAperti++;
						lastRapporto = rapporto;
					}
				}
				else {
					countAperti++;
					lastRapporto = rapporto;
				}
				
				// String data = dataFineObj.get(arg0)
			}
			//
			if (countAperti == 0) {
				throw new ElencoMovimentiException("Non sono presenti rapporti lavorativi aperti da sanare.");
			} else if (countAperti == 1) {
				risposta.put(EL_RAPPORTOLAVORATIVO, lastRapporto);
				return risposta.toString();
			} else {
				throw new ElencoMovimentiException("Sono presenti troppi rapporti lavorativi aperti da sanare.");
			}
		} else {
			throw new ElencoMovimentiException(esito.getString(EL_DESCRIZIONE));
		}
	}
}
