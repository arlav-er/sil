package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/*
 * calcola il punteggio presunto per il lavoratore iscritto al cm  
 * 
 * @author Donisi
 * 
 */
public class CMCalcolaPunteggioPresunto_Lav_Isc extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMCalcolaPunteggioPresunto_Lav_Isc.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();

		SessionContainer session = reqCont.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		// ReportOperationResult ror = new ReportOperationResult(this,
		// response);
		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();
			String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

			String Data_Riferimento = (String) request.getAttribute("Data_Riferimento");

			String codMonoTipoGrad = (String) request.getAttribute("ConfigGradCM");

			// anzianita ale
			// calcolo i mesi di anzianità
			/*
			 * String codMonoTipoGrad = "1"; SourceBean querySelectTipoGrad = (SourceBean)
			 * getConfig().getAttribute("CHECK_VERSIONE_GRADUATORIA"); SourceBean datiTipoGradCM = (SourceBean)
			 * transExec.executeQuery(reqCont, resCont, querySelectTipoGrad, "SELECT"); if (datiTipoGradCM != null) {
			 * Vector rowsTipoGradCM = datiTipoGradCM.getAttributeAsVector("ROW"); if (rowsTipoGradCM.size() == 1) {
			 * SourceBean rowtTpo = (SourceBean) rowsTipoGradCM.get(0); codMonoTipoGrad = (String)
			 * rowtTpo.getAttribute("CODMONOTIPOGRADCM"); } }
			 */

			int numMesiAnzianita = 0;
			String dataAnzianitaCM = ""; // datdatainizio
			String dataSospensioneCM = ""; // datsospensione
			String dataPubblicaz = ""; // dataPubblicazioneRichiesta
			String dataChiamataCM = ""; // dataChiamataCM
			int mesiSospCM = 0; // nummesisospesterni
			String dataAnzPregressaOrdinaria = "";

			SourceBean querySelect1 = (SourceBean) getConfig().getAttribute("CM_DATI_ISCRIZIONE_C_P");
			// ale

			SourceBean datiCM1 = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect1, "SELECT");
			if (datiCM1 != null) {
				Vector rowCM1 = datiCM1.getAttributeAsVector("ROW");
				if (rowCM1.size() == 1) {
					SourceBean row1 = (SourceBean) rowCM1.get(0);
					String codMonoTipoRag = (String) row1.getAttribute("CODMONOTIPORAGG");
					dataAnzPregressaOrdinaria = row1.getAttribute("datanzordpregressa") == null ? ""
							: (String) row1.getAttribute("datanzordpregressa");
					if (("D").equalsIgnoreCase(codMonoTipoRag)) {
						dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
								: (String) row1.getAttribute("DATANZIANITA68");
					} else if (("A").equalsIgnoreCase(codMonoTipoRag)) {
						dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
								: (String) row1.getAttribute("DATANZIANITA68");
					} else {
						dataAnzianitaCM = "";
						numMesiAnzianita = -6;
					}
				} else {
					dataAnzianitaCM = "";
					numMesiAnzianita = -6;
				}

				if (numMesiAnzianita >= 0) {
					// SourceBean querySelect = (SourceBean) getConfig().getAttribute("CM_DATAPUBBLICAZIONE_RICHIESTA");
					// SourceBean datiCM = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect, "SELECT");
					/*
					 * if (datiCM != null) { Vector rowCM = datiCM.getAttributeAsVector("ROW"); if (rowCM.size() == 1) {
					 * SourceBean row = (SourceBean)rowCM.get(0);
					 */
					// dataPubblicaz = row.getAttribute("DATPUBBLICAZIONE") == null ? "" :
					// (String)row.getAttribute("DATPUBBLICAZIONE");
					// dataChiamataCM = row.getAttribute("DATCHIAMATACM") == null ? "" : (String)
					// row.getAttribute("DATCHIAMATACM");
					dataChiamataCM = Data_Riferimento;
					// gestione tipo calcolo punteggio a senconda del parametro nel ts_generale
					if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
						if (dataChiamataCM != null && !("").equalsIgnoreCase(dataChiamataCM)) {
							// calcolo i mesi di anzianità
							numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "", 0,
									dataChiamataCM, dataAnzPregressaOrdinaria);
						} else {
							dataChiamataCM = "";
							numMesiAnzianita = -7;
						}
					} else {
						numMesiAnzianita = -5;
						/*
						 * if (dataPubblicaz != null && !("").equalsIgnoreCase(dataPubblicaz)) { // calcolo i mesi di
						 * anzianità numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "", 0,
						 * dataPubblicaz); } else { dataPubblicaz = ""; numMesiAnzianita = -5; }
						 */
					}
				} else {
					dataPubblicaz = "";
					dataChiamataCM = "";
					if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
						numMesiAnzianita = -7;
					} else {
						numMesiAnzianita = -5;
					}
				}

			}
			/*
			 * } }
			 */

			// calcolo il punteggio presunto
			calcolaPunteggio(request, response, numMesiAnzianita, cdnLavoratore, encryptKey);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMCalcolaPunteggio::service(): Impossibile calcolare il punteggio!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param cdnLavoratore
	 * @param dataAnzianitaCM
	 * @param dataSospensioneCM
	 * @param mesiSospCM
	 */
	private void calcolaPunteggio(SourceBean request, SourceBean response, int numMesiAnzianita, Object cdnLavoratore,
			String encryptKey) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");

		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		// ReportOperationResult ror = new ReportOperationResult(this,
		// response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;

			String prgiscr = (String) request.getAttribute("prgCmIscr");

			String tipoincrocio = (String) request.getAttribute("tipoincrocio");

			String datdatainizio = (String) request.getAttribute("DATDATAINIZIO");
			String datultimaiscr = (String) request.getAttribute("DATULTIMAISCR");
			String Data_Riferimento = (String) request.getAttribute("Data_Riferimento");
			String anno_rif = (String) request.getAttribute("anno_rif");

			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";

			String numReddito = "";
			String numPersone = "";
			String datdichcarico = "";
			String codcmtipoiscr = "";
			String numpercinvalidita = "";
			String datanzianita68 = "";
			String mesianzianita = "";
			String flgPatente = "";
			String codgradocapacitaloc = "";
			String flgDisocTi = "";
			String codCmAnnota = "";

			// dati punteggio

			String punt_iniziale = "";
			String punt_carico_fam = "";
			String punt_reddito = "";
			String punt_anzianita = "";
			String punt_invalidita = "";
			String punt_locomozione = "";
			String punt_patente = "";
			String punt_totale = "";

			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);
			SourceBean row = new SourceBean("ROW");

			// calcolo il punteggio se i mesi di anzianità sono stati calcolati
			// altrimenti setto l'errore sull'iscrizione al collocamento mirato
			if (numMesiAnzianita >= 0) {

				statementSB = (SourceBean) getConfig().getAttribute("CM_CALCOLO_PUNTEGGIO_PRESUNTO_CM_ISCR");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String tipoGraduatoria = "CM";
				String p_cdnUtente = String.valueOf(user.getCodut());
				String cdnLavoratoreDecrypt = cdnLavoratore.toString();
				String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratoreDecrypt);

				parameters = new ArrayList(29);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 3.Data rif
				parameters.add(conn.createDataField("Data_Riferimento", java.sql.Types.VARCHAR, Data_Riferimento));
				command.setAsInputParameters(paramIndex++);

				// 4.anno
				parameters.add(conn.createDataField("anno_rif", java.sql.Types.BIGINT, new BigInteger(anno_rif)));
				command.setAsInputParameters(paramIndex++);

				// 5. p_tipoincrocio
				parameters.add(conn.createDataField("tipoincrocio", java.sql.Types.VARCHAR, tipoincrocio));
				command.setAsInputParameters(paramIndex++);

				// 6. p_tipoGraduatoria
				parameters.add(conn.createDataField("tipoGraduatoria", java.sql.Types.VARCHAR, tipoGraduatoria));
				command.setAsInputParameters(paramIndex++);
				// 7. p_mesiAnzianita
				parameters.add(conn.createDataField("p_mesianzianita", java.sql.Types.BIGINT,
						new BigInteger("" + numMesiAnzianita)));
				command.setAsInputParameters(paramIndex++);

				// 8. p_cdnLavoratore
				parameters.add(conn.createDataField("p_cdnLavEncrypt", java.sql.Types.VARCHAR, cdnLavoratoreEncrypt));
				command.setAsInputParameters(paramIndex++);

				// cdnLavoratoreDecrypt
				parameters.add(
						conn.createDataField("cdnLavoratoreDecrypt", java.sql.Types.VARCHAR, cdnLavoratoreDecrypt));
				command.setAsInputParameters(paramIndex++);

				// prgiscr
				parameters.add(conn.createDataField("prgiscr", java.sql.Types.VARCHAR, prgiscr));
				command.setAsInputParameters(paramIndex++);

				// 9 out_p_numReddito
				parameters.add(conn.createDataField("out_p_numReddito", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 10 out_p_numPersone
				parameters.add(conn.createDataField("out_p_numPersone", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 11 out_p_datdichcarico
				parameters.add(conn.createDataField("out_p_datdichcarico", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 12 out_p_codcmtipoiscr
				parameters.add(conn.createDataField("out_p_codcmtipoiscr", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 13 out_p_numpercinvalidita
				parameters.add(conn.createDataField("out_p_numpercinvalidita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 14 out_p_datanzianita68
				parameters.add(conn.createDataField("out_p_datanzianita68", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 15 out_p_mesianzianita
				parameters.add(conn.createDataField("out_p_mesianzianita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 16 out_p_flgpatente
				parameters.add(conn.createDataField("out_p_flgpatente", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 17 out_p_codgradocapacitaloc
				parameters.add(conn.createDataField("out_p_codgradocapacitaloc", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 18 out_p_flgdisocti
				parameters.add(conn.createDataField("out_p_flgdisocti", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 19 out_p_codCmAnnota
				parameters.add(conn.createDataField("out_p_codCmAnnota", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 20 out_punt_iniziale
				parameters.add(conn.createDataField("out_p_punt_iniziale", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 21 out_p_punt_carico_fam
				parameters.add(conn.createDataField("out_p_punt_carico_fam", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 22 out_p_punt_reddito
				parameters.add(conn.createDataField("out_p_punt_reddito", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 23 out_p_punt_anzianita
				parameters.add(conn.createDataField("out_p_punt_anzianita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 24 out_p_punt_invalidita
				parameters.add(conn.createDataField("out_p_punt_invalidita", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 25 out_p_punt_locomozione
				parameters.add(conn.createDataField("out_p_punt_locomozione", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 26 out_p_punt_patente
				parameters.add(conn.createDataField("out_p_punt_patente", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 27 out_p_punt_totale
				parameters.add(conn.createDataField("out_p_punt_totale", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// 2 out_p_persone carico
				parameters.add(conn.createDataField("out_p_persone_carico", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

				// Chiamata alla Stored Procedure
				dr = command.execute(parameters);

				CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
				List outputParams = cdr.getContainedDataResult();

				// 0 codice ritorno
				PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
				DataField df = pdr.getPunctualDatafield();
				codiceRit = df.getStringValue();
				// 1. numReddito
				pdr = (PunctualDataResult) outputParams.get(1);
				df = pdr.getPunctualDatafield();
				numReddito = df.getStringValue();
				if (numReddito == null) {
					numReddito = "";
				}
				// 2. numPersone
				pdr = (PunctualDataResult) outputParams.get(2);
				df = pdr.getPunctualDatafield();
				numPersone = df.getStringValue();
				if (numPersone == null) {
					numPersone = "";
				}

				// 3. datdichcarico
				pdr = (PunctualDataResult) outputParams.get(3);
				df = pdr.getPunctualDatafield();
				datdichcarico = df.getStringValue();

				if (datdichcarico == null) {
					datdichcarico = "";
				}

				// 4. codcmtipoiscr
				pdr = (PunctualDataResult) outputParams.get(4);
				df = pdr.getPunctualDatafield();
				codcmtipoiscr = df.getStringValue();

				if (codcmtipoiscr == null) {
					codcmtipoiscr = "";
				}

				// 5. numpercinvalidita
				pdr = (PunctualDataResult) outputParams.get(5);
				df = pdr.getPunctualDatafield();
				numpercinvalidita = df.getStringValue();
				if (numpercinvalidita == null) {
					numpercinvalidita = "";
				}

				// 6. datanzianita68
				pdr = (PunctualDataResult) outputParams.get(6);
				df = pdr.getPunctualDatafield();
				datanzianita68 = df.getStringValue();
				if (datanzianita68 == null) {
					datanzianita68 = "";
				}

				// 7. mesianzianita
				pdr = (PunctualDataResult) outputParams.get(7);
				df = pdr.getPunctualDatafield();
				mesianzianita = df.getStringValue();
				if (mesianzianita == null) {
					mesianzianita = "";
				}

				// 8. flgpatente
				pdr = (PunctualDataResult) outputParams.get(8);
				df = pdr.getPunctualDatafield();
				flgPatente = df.getStringValue();
				if (flgPatente == null) {
					flgPatente = "";
				}

				// 9. codgradocapacitaloc
				pdr = (PunctualDataResult) outputParams.get(9);
				df = pdr.getPunctualDatafield();
				codgradocapacitaloc = df.getStringValue();
				if (codgradocapacitaloc == null) {
					codgradocapacitaloc = "";
				}

				// 10. disoc TI
				pdr = (PunctualDataResult) outputParams.get(10);
				df = pdr.getPunctualDatafield();
				flgDisocTi = df.getStringValue();
				if (flgDisocTi == null) {
					flgDisocTi = "";
				}
				// 11. codcmannota
				pdr = (PunctualDataResult) outputParams.get(11);
				df = pdr.getPunctualDatafield();
				codCmAnnota = df.getStringValue();
				if (codCmAnnota == null) {
					codCmAnnota = "";
				}

				// 12 out_punt_iniziale
				pdr = (PunctualDataResult) outputParams.get(12);
				df = pdr.getPunctualDatafield();
				punt_iniziale = df.getStringValue();
				if (punt_iniziale == null) {
					punt_iniziale = "";
				}

				// 13 out_p_punt_carico_fam
				pdr = (PunctualDataResult) outputParams.get(13);
				df = pdr.getPunctualDatafield();
				punt_carico_fam = df.getStringValue();
				if (punt_carico_fam == null) {
					punt_carico_fam = "";
				}

				// 14 out_p_punt_reddito
				pdr = (PunctualDataResult) outputParams.get(14);
				df = pdr.getPunctualDatafield();
				punt_reddito = df.getStringValue();

				if (punt_reddito == null) {
					punt_reddito = "";
				}

				// 15 out_p_punt_anzianita
				pdr = (PunctualDataResult) outputParams.get(15);
				df = pdr.getPunctualDatafield();
				punt_anzianita = df.getStringValue();
				if (punt_anzianita == null) {
					punt_anzianita = "";
				}

				// 16 out_p_punt_invalidita
				pdr = (PunctualDataResult) outputParams.get(16);
				df = pdr.getPunctualDatafield();
				punt_invalidita = df.getStringValue();
				if (punt_invalidita == null) {
					punt_invalidita = "";
				}

				// 17 out_p_punt_locomozione
				pdr = (PunctualDataResult) outputParams.get(17);
				df = pdr.getPunctualDatafield();
				punt_locomozione = df.getStringValue();
				if (punt_locomozione == null) {
					punt_locomozione = "";
				}

				// 18 out_p_punt_patente
				pdr = (PunctualDataResult) outputParams.get(18);
				df = pdr.getPunctualDatafield();
				punt_patente = df.getStringValue();
				if (punt_patente == null) {
					punt_patente = "";
				}

				// 19 out_p_punt_totale
				pdr = (PunctualDataResult) outputParams.get(19);
				df = pdr.getPunctualDatafield();
				punt_totale = df.getStringValue();
				if (punt_totale == null) {
					punt_totale = "";
				}

				String persone_carico = "";
				// 20 out_p_persone_carico
				pdr = (PunctualDataResult) outputParams.get(20);
				df = pdr.getPunctualDatafield();
				persone_carico = df.getStringValue();
				if (persone_carico == null) {
					punt_totale = "";
				}

				row.setAttribute("CodiceRit", codiceRit);
				row.setAttribute("numRedditoPres", numReddito);
				row.setAttribute("numPersonePres", numPersone);
				row.setAttribute("datdichcaricoPres", datdichcarico);
				row.setAttribute("codcmtipoiscrPres", codcmtipoiscr);
				row.setAttribute("numpercinvaliditaPres", numpercinvalidita);
				row.setAttribute("datanzianita68Pres", datanzianita68);
				row.setAttribute("mesianzianitaPres", mesianzianita);
				row.setAttribute("flgpatente", flgPatente);
				row.setAttribute("codgradocapacitaloc", codgradocapacitaloc);
				row.setAttribute("flgdisoctiPres", flgDisocTi);
				row.setAttribute("codCmAnnotaPres", codCmAnnota);

				row.setAttribute("punt_iniziale", punt_iniziale);
				row.setAttribute("punt_carico_fam", punt_carico_fam);
				row.setAttribute("punt_reddito", punt_reddito);
				row.setAttribute("punt_anzianita", punt_anzianita);
				row.setAttribute("punt_invalidita", punt_invalidita);
				row.setAttribute("punt_locomozione", punt_locomozione);
				row.setAttribute("punt_patente", punt_patente);
				row.setAttribute("punt_totale", punt_totale);
				row.setAttribute("tipoincrocio", tipoincrocio);
				row.setAttribute("persone_carico", persone_carico);
			} else {

				codiceRit = "-2";
				row.setAttribute("CodiceRit", codiceRit);
				row.setAttribute("numRedditoPres", numReddito);
				row.setAttribute("numPersonePres", numPersone);
				row.setAttribute("datdichcaricoPres", datdichcarico);
				row.setAttribute("codcmtipoiscrPres", codcmtipoiscr);
				row.setAttribute("numpercinvaliditaPres", numpercinvalidita);
				row.setAttribute("datanzianita68Pres", datanzianita68);
				row.setAttribute("mesianzianitaPres", mesianzianita);
				row.setAttribute("flgpatente", flgPatente);
				row.setAttribute("codgradocapacitaloc", codgradocapacitaloc);
				row.setAttribute("flgdisoctiPres", flgDisocTi);
				row.setAttribute("codCmAnnotaPres", codCmAnnota);
			}

			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure per il calcolo punteggio presunto";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}