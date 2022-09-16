package it.eng.sil.action.report.rosaCandidati;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.inet.report.Engine;
import com.inet.report.Group;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.Sottosistema;

public class IDOrosaCandidatiCV extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(IDOrosaCandidatiCV.class.getName());

	private String fileType = Engine.EXPORT_PDF;

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		TransactionQueryExecutor txExec = null;

		try {

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("CVperROSA." + tipoFile);
				else
					setStrNomeDoc("CVperROSA.pdf");
				setStrDescrizione("Curriculum per rosa candidati IDO");
				setReportPath("RosaCandidati_IDO/CVperROSA_CC.rpt");

				Vector params = new Vector(7);
				params.add(request.getAttribute("mostraPerLavoratore"));
				params.add(request.getAttribute("prgRosa"));
				params.add(request.getAttribute("prgRichAzienda"));
				params.add(request.getAttribute("cdnGruppo"));
				params.add(request.getAttribute("numAnno"));
				params.add(request.getAttribute("numRichiesta"));

				BigDecimal parametro = new BigDecimal((String) request.getAttribute("prgAzienda"));
				if (parametro != null)
					this.setPrgAzienda(parametro);

				parametro = new BigDecimal((String) request.getAttribute("prgUnita"));
				if (parametro != null)
					this.setPrgUnita(parametro);

				/*
				 * BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null); if (numProt !=
				 * null) { setNumProtocollo(numProt); params.add(numProt.toString()); }
				 * 
				 * String annoProt = (String) request.getAttribute("annoProt"); if (annoProt != null &&
				 * !annoProt.equals("")) { setNumAnnoProt(new BigDecimal(annoProt)); params.add(annoProt); }
				 */

				String kLock = (String) request.getAttribute("kLockProt");
				if (kLock != null && !kLock.equals("")) {
					setNumKeyLock(new BigDecimal(kLock));
				}

				String isCM = "" + request.getAttribute("isCM");

				if (Sottosistema.CM.isOn() && !isCM.equals("false")) {
					params.add("true");
				} else {
					params.add("false");
				}

				setParams(params);

				String tipoDoc = (String) request.getAttribute("tipoDoc");
				if (tipoDoc != null)
					setCodTipoDocumento(tipoDoc);

				// GG 18/2/2005
				String docInOut = SourceBeanUtils.getAttrStr(request, "docInOut", "O");
				setCodMonoIO(docInOut);

				com.inet.report.Engine eng = null;

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {
					insertDocument(request, response);
					/*
					 * txExec = new TransactionQueryExecutor(getPool()); txExec.initTransaction();
					 * 
					 * eng = apriSubReport(request, txExec.getDataConnection().getInternalConnection(), null); // dopo
					 * aver recuperato le info per generare il report abilito la transazione // per l'inserimento del
					 * documento e per le eventuali operazioni successive if (insertDocument(request, response, txExec,
					 * eng)){ // se FAIL il metodo ritorna false txExec.commitTransaction(); }else{ throw new
					 * Exception("Errore nella creazione del documento"); }
					 */
				} else if (apri != null && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
					/*
					 * eng = apriSubReport(request,
					 * DataConnectionManager.getInstance().getConnection().getInternalConnection(), null);
					 * showDocument(request, response, eng);
					 */
				}
			} // else
		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "IDOrosaCandidatiCV.service()", "");
			if (txExec != null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Stampa rosaCandidatiCV fallita. Fallito anche il tentativo successivo di roolback.",
							(Exception) e1);

				}
			}
		}
	}

	private Engine apriSubReport(SourceBean request, Connection conn1, String codStatoAtto) throws Exception {

		String cdnLavoratore = "" + request.getAttribute("cdnLavoratore");

		String tipoFile = (String) request.getAttribute("tipoFile");
		setFileType(tipoFile);
		Engine engine = new Engine(getFileType());

		String prgRosa = "" + request.getAttribute("prgRosa");
		String prgRichAzienda = "" + request.getAttribute("prgRichAzienda");
		String cdnGruppo = "" + request.getAttribute("cdnGruppo");
		String numAnno = "" + request.getAttribute("numAnno");
		String numRichiesta = "" + request.getAttribute("numRichiesta");

		// ---------------------------------------------------- Lavoratori
		java.sql.Statement stLav = conn1.createStatement();
		String sqlLav = "	SELECT DISTINCT an_lavoratore.cdnlavoratore "
				+ " 						   FROM an_lavoratore an_lavoratore, "
				+ " 								de_cittadinanza de_cittadinanza, "
				+ " 								do_rosa do_rosa, "
				+ " 								do_nominativo do_nominativo, "
				+ " 								do_incrocio do_incrocio, "
				+ " 								do_richiesta_az do_richiesta_az, "
				+ " 								de_comune de_comune, "
				+ " 								de_comune de_comune_1, "
				+ " 								de_provincia de_provincia, "
				+ " 								de_provincia de_provincia_1, "
				+ " 								vw_do_elenco_rosa_def vw_do_elenco_rosa_def, "
				+ " 								pr_nota_lav pr_nota_lav " + " 						  WHERE (    "
				+ " 									(     " + " 										 (     "
				+ " 											  (do_incrocio.prgrichiestaaz = " + prgRichAzienda
				+ ") AND (do_rosa.prgrosa = " + prgRosa + ") " + " 										 ) "
				+ " 										 AND do_nominativo.cdnutcanc IS NULL "
				+ " 									) " + " 									OR  "
				+ " 									(     " + " 										 (     "
				+ " 											  (     "
				+ " 												   (do_richiesta_az.numanno = " + numAnno
				+ ") AND (do_richiesta_az.numrichiesta = " + numRichiesta + ") "
				+ " 											  ) "
				+ " 											  AND (do_incrocio.prgrichiestaaz = do_richiesta_az.prgrichiestaaz) "
				+ " 										 ) "
				+ " 										 AND do_nominativo.cdnutcanc IS NULL "
				+ " 									) " + " 								) "
				+ " 							AND an_lavoratore.codcittadinanza = de_cittadinanza.codcittadinanza "
				+ " 							AND an_lavoratore.cdnlavoratore = do_nominativo.cdnlavoratore "
				+ " 							AND an_lavoratore.codcomnas = de_comune.codcom "
				+ " 							AND an_lavoratore.codcomdom = de_comune_1.codcom "
				+ " 							AND an_lavoratore.cdnlavoratore = vw_do_elenco_rosa_def.cdnlavoratore "
				+ " 							AND an_lavoratore.cdnlavoratore = pr_nota_lav.cdnlavoratore(+) "
				+ " 							AND vw_do_elenco_rosa_def.numanno = do_richiesta_az.numanno "
				+ " 							AND vw_do_elenco_rosa_def.numrichiesta = do_richiesta_az.numrichiesta "
				+ " 							AND de_comune_1.codprovincia = de_provincia_1.codprovincia "
				+ " 							AND de_comune.codprovincia = de_provincia.codprovincia "
				+ " 							AND do_incrocio.prgincrocio = do_rosa.prgincrocio "
				+ " 							AND do_incrocio.prgrichiestaaz = do_richiesta_az.prgrichiestaaz "
				+ " 							AND do_rosa.prgrosa = do_nominativo.prgrosa "
				+ " 					   ORDER BY an_lavoratore.cdnlavoratore ";
		java.sql.ResultSet rsLav = stLav.executeQuery(sqlLav);
		// ----------------------------------------------------

		String cdnLavoratoreCrypted = "";
		String cdnLav = "";
		Vector lavCrypted = new Vector();
		Vector cdnLavoratori = new Vector();
		while (rsLav.next()) {
			cdnLav = rsLav.getString("cdnLavoratore");
			cdnLavoratoreCrypted = EncryptDecryptUtils.encrypt(cdnLav);
			lavCrypted.add(cdnLavoratoreCrypted);
			cdnLavoratori.add(cdnLav);
		}

		rsLav.close();
		stLav.close();

		String path = ConfigSingleton.getRootPath() + "/WEB-INF/report/RosaCandidati_IDO/CVperROSA_CC.rpt";
		File f = new File(path);
		engine.setReportFile("file:" + f.getAbsolutePath());

		com.inet.report.Engine subreport1 = engine.getSubReport(12);
		String nomeReport = subreport1.getReportTitle(); // nome Sottoreport
															// n'12
		Group gruppoLav = subreport1.getGroup(1);

		// Codifica del cdnLavoratore
		// String cdnLavoratoreCrypted =
		// EncryptDecryptUtils.encrypt(cdnLavoratore);
		java.sql.Statement st1 = null;
		java.sql.ResultSet rs1 = null;
		for (int i = 0; i < lavCrypted.size(); i++) {

			// ---------------------------------------------------- SottoReport
			// "cm" del report CVperROSA_CC.rpt
			st1 = conn1.createStatement();
			String sql1 = " SELECT AM_CM_ISCR.CODCMTIPOISCR, " + " 	     AM_CM_ISCR.DATDATAINIZIO, "
					+ " 	     AM_CM_ISCR.DATDATAFINE, " + " 	     AM_CM_ISCR.CDNLAVORATORE, "
					+ " 	     DE_CM_TIPO_ISCR.STRDESCRIZIONE, " + " 	     AM_DOCUMENTO.CODTIPODOCUMENTO, "
					+ " 	     AM_DOCUMENTO.CODSTATOATTO " + "   FROM AM_CM_ISCR AM_CM_ISCR, "
					+ " 	     DE_CM_TIPO_ISCR DE_CM_TIPO_ISCR, " + " 	     AM_DOCUMENTO AM_DOCUMENTO, "
					+ " 	     AM_DOCUMENTO_COLL AM_DOCUMENTO_COLL " + " WHERE ((((AM_CM_ISCR.CDNLAVORATORE = '"
					+ lavCrypted.get(i) + "' ) and AM_CM_ISCR.DATDATAFINE is "
					+ " 	     null) and (AM_DOCUMENTO.CODTIPODOCUMENTO = 'L68')) and (AM_DOCUMENTO.CDNLAVORATORE = "
					+ cdnLavoratori.get(i) + ") and " + " 	     (AM_DOCUMENTO.CODSTATOATTO = 'PR')) and  "
					+ " 	     AM_CM_ISCR.CODCMTIPOISCR = DE_CM_TIPO_ISCR.CODCMTIPOISCR and "
					+ " 	     AM_CM_ISCR.PRGCMISCR = AM_DOCUMENTO_COLL.STRCHIAVETABELLA and  "
					+ " 	     AM_DOCUMENTO_COLL.PRGDOCUMENTO = AM_DOCUMENTO.PRGDOCUMENTO	";
			rs1 = st1.executeQuery(sql1);
			// ----------------------------------------------------

			if (gruppoLav.indexOf() == i)
				subreport1.setData(rs1);

			// rs1.close();
			// st1.close();

		}
		// subreport1.setData(rs1);
		rs1.close();
		st1.close();

		return engine;
	}

	private String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

}// class IDOrosaCandidatiCV
