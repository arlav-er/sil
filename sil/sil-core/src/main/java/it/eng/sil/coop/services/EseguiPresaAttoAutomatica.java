package it.eng.sil.coop.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.service.ServiceIFace;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Engine;
import com.inet.report.ReportException;

import it.eng.sil.coop.bean.XMLCoopMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.InvioDatiCoopMessage;
import it.eng.sil.coop.queues.OutQ;
//import it.eng.sil.coop.utils.ConnessionePerReport;
import it.eng.sil.coop.utils.CoopMessageCodes;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.coop.utils.QueryExecutor;
import it.eng.sil.util.ConnessionePerReport;

/**
 * @author savino
 */
public class EseguiPresaAttoAutomatica implements ServiceIFace {

	private static final Logger log = Logger.getLogger(EseguiPresaAttoAutomatica.class.getName());

	private Connection dataConnection;
	private String dataSourceJNDI;

	private String cdnLavoratore;
	// dati necessari al report letti dal db
	private String nome, cognome, dataNascita, indirizzoDom, strCapDom, descComDom, descStatoOccupaz, dataInizioStOcc,
			dataAnzianitaDisocc, dataInizioDid, dataDa, dataA, codCpiCompLoc, descCpiCompLoc;
	// informazione che non viene utilizzata.
	private String flg181;

	// nuovi dati lavoratore giunti dal polo remoto
	private String cognomeNuovo, nomeNuovo, comuneNascNuovo, dataNascNuovo, codComDomNuovo, indirizzoDomNuovo,
			descComDomNuovo, codComNascNuovo, codCpiTitNuovo, dataTrasferimento, cdnUt, codiceFiscale;
	// dati da trasferire al polo mittente
	private String codStatoOccupaz, mesiAnzianita, mesiSospensione, dataAnzianitaDisoccStoriaInf, descComNasc,
			codComDom;
	// 28/5/2007 aggiunte info sul collocamento mirato
	private String codCMTipoIscr_1, codCMTipoIscr_2;
	private String dataIscrCM_1, dataIscrCM_2;
	private String dataAnzLista68_1, dataAnzLista68_2;

	private String prgPresaAtto;
	private String cdnLavoratoreEncrypt;

	/**
	 * 
	 */
	public void service(SourceBean request, SourceBean response) throws EMFUserError {

		byte[] data = null;

		try {

			// la connessione viene passata dalla classe chiamante.
			// E' gia' stata posta in: autocommit=false

			// se non vengono passate questi due oggetti l'operazione non puo' proseguire
			if (dataConnection == null)
				throw new Exception("connessione al db non trovata");
			if (dataSourceJNDI == null)
				throw new Exception("data source jndi name non trovata");

			// informazioni sul polo AL quale e' stato trasferito il lavoratore
			TestataMessageTO poloTrasferimento = (TestataMessageTO) request.getAttribute("POLO_TRASFERIMENTO");
			// informazioni sul polo DAL quale si e' trasferito il lavoratore
			TestataMessageTO poloPresaAtto = (TestataMessageTO) request.getAttribute("POLO_PRESA_ATTO");

			SourceBean cpiTrasferimento = (SourceBean) request.getAttribute("CPI_TRASFERIMENTO");
			SourceBean cpiPresaAtto = (SourceBean) request.getAttribute("CPI_PRESA_ATTO");
			// ONDE EVITARE EQUIVOCI ......
			// il cpi AL quale il lavoratore e' stato trasferito
			String nuovoCodCpiComp = (String) cpiTrasferimento.getAttribute("codcpi");
			String nuovoDesCpiComp = (String) cpiTrasferimento.getAttribute("descrizione");
			// il cpi DAL quale il lavoratore si trasferisce
			String precCodCpiComp = (String) cpiPresaAtto.getAttribute("codcpi");
			String precDesCpiComp = (String) cpiPresaAtto.getAttribute("descrizione");
			// da notare che se si sta eseguendo una presa atto il polo in cui l'operazione avviene ha la
			// competenza del lavoratore, quindi diventera' il competente precedente.

			cognomeNuovo = (String) request.getAttribute("COGNOME");
			nomeNuovo = (String) request.getAttribute("NOME");
			codiceFiscale = (String) request.getAttribute("CODICEFISCALE");
			dataTrasferimento = (String) request.getAttribute("DATATRASF");
			codComDomNuovo = (String) request.getAttribute("CODCOMUNEDOM");
			indirizzoDomNuovo = (String) request.getAttribute("INDIRIZZODOM");
			comuneNascNuovo = (String) request.getAttribute("COMUNENASCITA");
			dataNascNuovo = (String) request.getAttribute("DATANASCITA");
			codCpiTitNuovo = (String) request.getAttribute("CODCPITITNUOVO"); // an_lav_storia_inf.codcpitit
			cdnUt = (String) request.getAttribute("cdnut"); // se da trasferimento e' 190, se da presa atto aut. e' il
															// cdnut dell'operatore
			prgPresaAtto = (String) request.getAttribute("prgPresaAtto");
			// descCpiLocale = (String)request.getAttribute("descCpiLocale");
			// codCpiLocale = (String)request.getAttribute("codCpiLocale");

			descComDomNuovo = (String) request.getAttribute("comunedomicilio");
			codComNascNuovo = (String) request.getAttribute("codcomnasc");

			check();
			// 0. lettura dati (verranno valorizzate le proprieta' della classe)
			leggiLavoratore();
			// 1. operazioni db presa atto
			registraPresaAtto();
			// 2. generazione pdf
			data = creaPdf();
			// 2.1. chiudo il collocamento mirato se esiste
			// N.B. questa operazione va fatto qui e non nella procedura altrimenti la stampa perde le informazioni sul
			// cm
			chiudiCollocamentoMirato();
			// 3. invio dei dati al polo mittente
			inviaDatiTrasferimento(data, request);

			// e' andato tutto bene :)
			// la connessione non va chiusa perche' viene gestita dalla classe chiamante
		}

		catch (SQLException sqle) {
			log.fatal("Presa atto automatica: errore nella procedura pl-sql", sqle);
			int sqlErrCode = Math.abs(sqle.getErrorCode());
			int codMessaggioErrore = CoopMessageCodes.General.OPERATION_FAIL;
			if (sqlErrCode > 20000 && sqlErrCode < 20999) {
				// errore applicazione
				switch (sqlErrCode) {
				case 20901:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV;
					break;
				case 20902:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S;
					break;
				case 20903:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG;
					break;
				case 20904:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP;
					break;
				case 20905:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV;
					break;
				case 20906:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_OPEN_STATO_OCC;
					break;
				case 20907:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERRORE_INS_STATO_OCC;
					break;
				case 20908:
					codMessaggioErrore = CoopMessageCodes.Trasferimento.ERR_CLOSE_CM_ISCR;
					break;
				default: // resta l'errore generico
				}
			}
			// sqle.getSQLState();
			// sqle.getMessage();
			throw new EMFUserError(EMFErrorSeverity.ERROR, codMessaggioErrore);
		} catch (Exception ex) {
			log.fatal("Presa atto automatica: " + this, ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, CoopMessageCodes.General.OPERATION_FAIL);
		} finally {
			data = null;
			release();
		}
	}

	/**
	 * 
	 */
	private void inviaDatiTrasferimento(byte[] data, SourceBean request) throws Exception {

		TestataMessageTO poloPresaAtto = (TestataMessageTO) request.getAttribute("POLO_PRESA_ATTO");
		TestataMessageTO poloTrasferimento = (TestataMessageTO) request.getAttribute("POLO_TRASFERIMENTO");

		SourceBean cpiTrasferimento = (SourceBean) request.getAttribute("CPI_TRASFERIMENTO");
		SourceBean cpiPresaAtto = (SourceBean) request.getAttribute("CPI_PRESA_ATTO");

		XMLCoopMessage xmlMessage = new XMLCoopMessage();
		xmlMessage.setCodiceCPIMitt(codCpiCompLoc);
		xmlMessage.setDescrizioneMitt(descCpiCompLoc);
		xmlMessage.setNomeServizio("trasferimento");
		xmlMessage.setDati("cognome", cognome);
		xmlMessage.setDati("nome", nome);
		xmlMessage.setDati("codicefiscale", codiceFiscale);
		xmlMessage.setDati("comunenascita", descComNasc);
		xmlMessage.setDati("datanascita", dataNascita);
		xmlMessage.setDati("datatrasf", dataTrasferimento);
		xmlMessage.setDati("codcomunedom", codComDom);
		// TODO Savino: indirizzodom inviato col servizio InvioDatiCoop ma non utilizzato (tab. ca_info_trasferimento)
		xmlMessage.setDati("indirizzodom", indirizzoDom);
		xmlMessage.setDati("codStatoOccupaz", codStatoOccupaz);
		xmlMessage.setDati("strDescStatoOccupaz", descStatoOccupaz);
		xmlMessage.setDati("datanzianitadisoc", dataAnzianitaDisoccStoriaInf);
		xmlMessage.setDati("nummesianzianita", mesiAnzianita);
		xmlMessage.setDati("nummesisosp", mesiSospensione);
		// 28/5/2007 dati iscrizione collocamento mirato
		xmlMessage.setDati("codCMTipoIscr_1", this.codCMTipoIscr_1);
		xmlMessage.setDati("codCMTipoIscr_2", this.codCMTipoIscr_2);
		xmlMessage.setDati("dataAnzLista68_1", this.dataAnzLista68_1);
		xmlMessage.setDati("dataAnzLista68_2", this.dataAnzLista68_2);
		xmlMessage.setDati("dataIscrCM_1", this.dataIscrCM_1);
		xmlMessage.setDati("dataIscrCM_2", this.dataIscrCM_2);

		StringBuffer evidenza = new StringBuffer();
		// NOTA 11/09/2006 Savino: aggiungere l'informazione del polo provinciale che ha inviato il messaggio? NO
		Vector v = new Vector(4);
		v.add((String) cpiPresaAtto.getAttribute("descrizione"));
		v.add((String) cpiPresaAtto.getAttribute("codcpi"));
		v.add((String) cpiTrasferimento.getAttribute("descrizione"));
		v.add(dataTrasferimento);
		String messaggioEvidenza = MessageBundle
				.getMessage(CoopMessageCodes.NOTIFICHE_COMUNI.PRESA_ATTO_INVIO_DATI_AVVENUTO, v);
		xmlMessage.setMessaggio(messaggioEvidenza);

		TestataMessageTO testata = new TestataMessageTO();
		testata.setPoloMittente(poloPresaAtto.getPoloMittente());
		testata.setCdnUtente(poloPresaAtto.getCdnUtente());
		testata.setCdnGruppo(poloPresaAtto.getCdnGruppo());
		testata.setCdnProfilo(poloPresaAtto.getCdnProfilo());
		testata.setStrMittente(poloPresaAtto.getStrMittente());
		// la destinazione e' il polo al quale il lavoratore si e' trasferito
		testata.setDestinazione(poloTrasferimento.getPoloMittente());

		testata.setServizio("InvioDatiCoop");
		InvioDatiCoopMessage invioDatiMessage = new InvioDatiCoopMessage();
		invioDatiMessage.setPdfData(data);
		invioDatiMessage.setXMLMessage(xmlMessage.toXML());
		invioDatiMessage.setTestata(testata);
		invioDatiMessage.setDataSourceJndi(dataSourceJNDI);
		OutQ outQ = new OutQ();
		invioDatiMessage.send(outQ);
	}

	/**
	 * 
	 */
	private void check() throws Exception {
		// controllo che i dati ci siano
		if (isEmpty(codiceFiscale) || isEmpty(dataTrasferimento) || isEmpty(codComDomNuovo)
				|| isEmpty(indirizzoDomNuovo) || isEmpty(cognomeNuovo) || isEmpty(nomeNuovo) || isEmpty(comuneNascNuovo)
				|| isEmpty(dataNascNuovo) || isEmpty(codCpiTitNuovo) || isEmpty(cdnUt) || isEmpty(prgPresaAtto)

		) {
			log.error("Presa atto in cooperazione non possibile per mancanza di almeno un dato richiesto: " + this);
			throw new Exception("Presa atto in cooperazione: almeno un dato richiesto e' mancante");
		}

	}

	private boolean isEmpty(String s) {
		return s == null || "".equals(s);
	}

	/**
	 * 
	 */
	private void registraPresaAtto() throws SQLException {
		String keyCifratura = System.getProperty("_ENCRYPTER_KEY_");
		CallableStatement cs = null;
		String procSql = "{ CALL pg_coop.presa_atto_trasferimento(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		cs = (CallableStatement) dataConnection.prepareCall(procSql);
		int i = 1, j = 0;

		cs.setString(i++, cdnLavoratore); // p_cdnlavoratore IN
		cs.setString(i++, indirizzoDomNuovo); // p_strindirizzodom IN
		cs.setString(i++, dataTrasferimento); // p_dattrasferimento IN
		cs.setString(i++, codCpiTitNuovo); // p_codcpitit IN
		cs.setString(i++, null); // p_codcpiorig IN : lo leggo nella procedure (per ora e' un parametro inutile)
		cs.setString(i++, cdnUt); // p_cdut IN
		cs.setString(i++, codComDomNuovo); // p_codcomdom IN
		cs.setString(i++, prgPresaAtto); // p_prgpresaatto IN
		cs.setString(i++, keyCifratura); // p_key_cifratura IN
		j = i;
		cs.registerOutParameter(i++, Types.VARCHAR); // p_strcapdom OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_datdichiarazione OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_flg181 OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_data_anzianita_disoc OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_data_anzianita_disoc OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_data_inizio_s_occ OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_codstatooccupaz OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_data_nascita OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_cognome OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_nome OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_num_mesi_sosp OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_num_mesi_anz OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_indirizzo_dom OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_desc_statooccup OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_desc_com_dom OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_desc_com_nasc OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_cod_com_dom OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_cod_cpi_loc OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p_desc_cpi_loc OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // p OUT
		cs.registerOutParameter(i++, Types.VARCHAR); // cdnlavoratore crittato del cm

		boolean res = cs.execute();
		// if (!res) throw new SQLException("errore nella chiamata della procedure");
		// se ho errore viene lanciata eccezione
		// la variabile "j" viene inizializzata al successivo valore dell'ultimo parametri di in (j=i)
		strCapDom = cs.getString(j++); // p_strcapdom OUT
		dataInizioDid = cs.getString(j++); // p_datdichiarazione OUT
		flg181 = cs.getString(j++); // p_flg181 OUT
		dataAnzianitaDisocc = cs.getString(j++); // p_data_anzianita_disoc OUT
		dataAnzianitaDisoccStoriaInf = cs.getString(j++); // p_data_anzianita_disoc_storia OUT
		dataInizioStOcc = cs.getString(j++); // p_data_inizio_s_occ OUT
		codStatoOccupaz = cs.getString(j++); // p_codstatooccupaz OUT
		dataNascita = cs.getString(j++); // p_data_nascita OUT
		cognome = cs.getString(j++); // p_cognome OUT
		nome = cs.getString(j++); // p_nome OUT
		mesiSospensione = cs.getString(j++); // p_num_mesi_sosp OUT
		mesiAnzianita = cs.getString(j++); // p_num_mesi_anz OUT
		indirizzoDom = cs.getString(j++); // p_indirizzo_dom OUT
		descStatoOccupaz = cs.getString(j++); // p_desc_statooccup OUT
		descComDom = cs.getString(j++); // p_desc_com_dom OUT
		descComNasc = cs.getString(j++); // p_desc_com_nasc OUT
		codComDom = cs.getString(j++); // p_cod_com_dom OUT
		codCpiCompLoc = cs.getString(j++); // p_cod_cpi_loc OUT
		descCpiCompLoc = cs.getString(j++); // p_desc_cpi_loc OUT
		// 28/5/2007 aggiunte info collocamento mirato (cdnlavoratore criptato)
		codCMTipoIscr_1 = cs.getString(j++); // p_cod_cm_tipo_iscr OUT
		dataIscrCM_1 = cs.getString(j++); // p_data_iscr_cm OUT
		dataAnzLista68_1 = cs.getString(j++); // p_data_anz_lista_68 OUT
		codCMTipoIscr_2 = cs.getString(j++); // p_cod_cm_tipo_iscr OUT
		dataIscrCM_2 = cs.getString(j++); // p_data_iscr_cm OUT
		dataAnzLista68_2 = cs.getString(j++); // p_data_anz_lista_68 OUT
		cdnLavoratoreEncrypt = cs.getString(j++); // p_cdnlavoratore crittato OUT
		cs.close();
	}

	/**
	 * lettura informazioni utilizzate dal report e da inviare al polo richiedente i dati
	 * 
	 * @throws Exception
	 *             in caso di errore o informazioni inesistenti
	 */
	private void leggiLavoratore() throws Exception {
		QueryExecutor qe = new QueryExecutor(dataConnection);
		String stm = "select to_char(lav.cdnLavoratore) as cdnLavoratore from an_lavoratore lav where upper(lav.strCodiceFiscale) =  upper(?)";

		SourceBean row = qe.executeQuery(stm, new String[] { codiceFiscale });
		if (row == null || row.getAttribute("row.cdnlavoratore") == null)
			throw new Exception("Impossibile leggere i dati del lavoratore");
		cdnLavoratore = (String) row.getAttribute("row.cdnlavoratore");
	}

	/**
	 * Generazione del pdf del percorso lavoratore
	 * 
	 * @return il pdf come array di byte
	 * @throws Exception
	 *             in caso di impossibilita' a creare il pdf
	 */
	private byte[] creaPdf() throws Exception {
		File ftemp = null;
		InputStream is = null;
		BufferedReader br = null;
		FileWriter fw = null;
		Engine engine = null;
		ConnessionePerReport miaConn = null;
		ByteArrayOutputStream fout = null;
		try {
			// 1. scrivo il file rpt in un file temporaneo che passero' all'engine e che prima di uscire dal metodo
			// cancellero'
			/*
			 * is = EseguiPresaAttoAutomatica.class.getResourceAsStream("/it/eng/sil/report/PercorsoLavoratore_CC.rpt");
			 * br = new BufferedReader(new InputStreamReader(is)); ftemp = File.createTempFile("PercorsoLavoratore",
			 * ".rpt"); fw = new FileWriter(ftemp); char[] c = new char[1024]; int len=0; while((len=br.read(c))>0)
			 * fw.write(c, 0, len); fw.close(); br.close(); is.close();
			 */
			ftemp = new File(
					ConfigSingleton.getRootPath() + "/WEB-INF/report/Amministrazione/PercorsoLavoratore_CC.rpt");
			// 2. creo l'engine
			engine = new Engine(Engine.EXPORT_PDF);
			String rptPath = "file:" + ftemp.getAbsolutePath();
			engine.setReportFile(rptPath);
			// il report deve utilizzare la mia connessione e non la sua (di crystal clear)
			miaConn = new ConnessionePerReport(dataConnection);
			engine.setConnection(miaConn);
			engine.setCatalog(dataConnection.getCatalog());
			for (int i = 0; i < engine.getSubReportCount(); i++) {
				engine.getSubReport(i).setConnection(miaConn);
				engine.setCatalog(dataConnection.getCatalog());
			}
			// passo gli argomenti
			String intestazione = "PERCORSO LAVORATORE";
			String _info[] = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "L", "M", "N", "O", "P", "Q",
					"R", "S" };

			for (int i = 0; i < _info.length; i++) {
				engine.setPrompt("info" + _info[i], _info[i]);
			}
			engine.setPrompt("cdnLavoratore", cdnLavoratore);
			engine.setPrompt("cdnLavoratoreEncrypt", cdnLavoratoreEncrypt);
			engine.setPrompt("codCpi", codCpiCompLoc);
			engine.setPrompt("codiceFiscale", codiceFiscale);
			engine.setPrompt("comuneDomicilio", descComDom);
			engine.setPrompt("dataInizioAnz", dataAnzianitaDisocc);
			engine.setPrompt("dataInizioDid", dataInizioDid);
			engine.setPrompt("dataInizioStOcc", dataInizioStOcc);
			engine.setPrompt("dataNascita", dataNascita);
			engine.setPrompt("indirizzoDomicilio", indirizzoDom);
			engine.setPrompt("nomeLavoratore", nome + " " + cognome);
			engine.setPrompt("statoOccupaz", descStatoOccupaz);
			engine.setPrompt("titolo", intestazione);
			engine.setPrompt("dataDa", "");
			engine.setPrompt("dataA", "");
			engine.setPrompt("showQualCodB", "S");
			engine.setPrompt("showQualCodO", "S");

			// 3. creo il pdf scrivendolo in un array di byte
			engine.execute();
			fout = new ByteArrayOutputStream();
			int numPag = engine.getPageCount();
			for (int i = 1; i <= numPag; i++) {
				fout.write(engine.getPageData(i));
			}

			return fout.toByteArray();
		} catch (ReportException re) {
			
			// TODO FV 29/7/2020
/*			
			java.util.Properties engineProperties = RDC.getConfigurationProperties();
			java.net.URL enginePath = RDC.getCrystalClearPropertyPath();
			log.fatal("PARAMETRI ENGINE DI CRYSTAL CLEAR");
			log.fatal("Crystal clear properties : " + engineProperties);
			log.fatal("Crystal clear properties PATH: " + enginePath);
*/
			System.out.println("EseguiPresaAttoAutomatica:" + "ERRORE ENGINE DI CRYSTAL CLEAR");

			throw re;
		} finally {
			ftemp = null;
			fout = null;
			engine = null;
			miaConn = null;
			is = null;
			br = null;
			fw = null;
		}
	}

	private void chiudiCollocamentoMirato() throws SQLException {
		try {
			if ((this.codCMTipoIscr_1 != null && !this.codCMTipoIscr_1.equals(""))
					|| (this.codCMTipoIscr_2 != null && !this.codCMTipoIscr_2.equals(""))) {
				String keyCifratura = System.getProperty("_ENCRYPTER_KEY_");
				String sql = "update am_cm_iscr set datdatafine = TO_DATE('" + this.dataTrasferimento
						+ "','DD/MM/YYYY')-1, " + "NUMKLOCMISCR = NUMKLOCMISCR + 1, CODMOTIVOFINEATTO = 'TD', "
						+ "CDNUTMOD = " + this.cdnUt + ", DTMMOD = SYSDATE " + "WHERE am_cm_iscr.prgcmiscr in ("
						+ "select c.prgcmiscr FROM am_cm_iscr c, DE_CM_TIPO_ISCR , am_documento d, am_documento_coll coll where "
						+ this.cdnLavoratore + " = decrypt (c.cdnlavoratore, '" + keyCifratura
						+ "') and DE_CM_TIPO_ISCR.CODCMTIPOISCR = c.CODCMTIPOISCR"
						+ " and d.PRGDOCUMENTO = coll.PRGDOCUMENTO and coll.STRCHIAVETABELLA = c.PRGCMISCR "
						+ " and c.DATDATAFINE is null and d.CODSTATOATTO = 'PR'" + " and d.CDNLAVORATORE =  "
						+ this.cdnLavoratore + ")";

				Statement st = this.dataConnection.createStatement();
				int n = st.executeUpdate(sql);
				st.close();
			}
		} catch (Exception e) {
			log.error(this.getClass().getName() + ": impossibile chiudere il collocamento mirato per il lavoratore "
					+ this.codiceFiscale, e);
			throw new SQLException("Update failed", "", -20908);
		}
	}

	public String toString() {
		return new StringBuffer("Presa atto automatica: ").append("codiceFiscale     =").append(codiceFiscale)
				.append(",dataTrasferimento=").append(dataTrasferimento).append(",codComDomNuovo   =")
				.append(codComDomNuovo).append(",indirizzoDomNuovo=").append(indirizzoDomNuovo)
				.append(",cognomeNuovo     =").append(cognomeNuovo).append(",nomeNuovo        =").append(nomeNuovo)
				.append(",comuneNascNuovo  =").append(comuneNascNuovo).append(",dataNascNuovo    =")
				.append(dataNascNuovo).append(",codCpiTitNuovo   =").append(codCpiTitNuovo)
				.append(",cdnUtente        =").append(cdnUt).append(",prgPresaAtto     =").append(prgPresaAtto)
				.toString();
	}

	/**
	 * @param conn
	 *            la connessione passata dal modulo chiamante
	 */
	public void setConnection(Connection conn) {
		this.dataConnection = conn;
	}

	/**
	 * @param dataSourceJndiName
	 *            passato dal modulo chiamante
	 */
	public void setDataSourceJNDI(String dataSourceJndiName) {
		this.dataSourceJNDI = dataSourceJndiName;
	}

	/**
	 * pongo a null tutte le proprieta'
	 */
	private void release() {
		dataConnection = null;
		dataSourceJNDI = null;
		cdnLavoratore = null;
		nome = null;
		cognome = null;
		dataNascita = null;
		indirizzoDom = null;
		strCapDom = null;
		descComDom = null;
		descStatoOccupaz = null;
		dataInizioStOcc = null;
		dataInizioDid = null;
		dataDa = null;
		dataA = null;
		codCpiCompLoc = null;
		descCpiCompLoc = null;
		flg181 = null;
		cognomeNuovo = null;
		nomeNuovo = null;
		comuneNascNuovo = null;
		dataNascNuovo = null;
		codComDomNuovo = null;
		indirizzoDomNuovo = null;
		codCpiTitNuovo = null;
		dataTrasferimento = null;
		cdnUt = null;
		codiceFiscale = null;
		codStatoOccupaz = null;
		mesiAnzianita = null;
		mesiSospensione = null;
		dataAnzianitaDisocc = null;
		descComNasc = null;
		codComDom = null;
		cdnUt = null;
		prgPresaAtto = null;
	}

}
