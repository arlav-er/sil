package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.ZipEntryFile;
import it.eng.sil.util.ZipPackager;

public class EsportaDecaduti extends AbstractAction {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private File outFileDecaduti;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EsportaDecaduti.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor txExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		BigDecimal progressivo = null;
		BigDecimal numkloRA = null;
		try {
			BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			String dataRif = request.containsAttribute("datRifDecadenza")
					? request.getAttribute("datRifDecadenza").toString()
					: "";
			ArrayList<SourceBean> domandeDecadute = new ArrayList<SourceBean>();

			if (!dataRif.equals("")) {
				Vector rows = getLavoratoriDaTrattare();
				int numLavoratori = rows.size();
				for (int i = 0; i < numLavoratori; i++) {
					try {
						SourceBean domanda = (SourceBean) rows.get(i);
						String datInizioPrestazione = domanda.containsAttribute("DATINIZIOPRESTAZIONE")
								? domanda.getAttribute("DATINIZIOPRESTAZIONE").toString()
								: "";
						String datFinePrestazione = domanda.containsAttribute("DATFINEPRESTAZIONE")
								? domanda.getAttribute("DATFINEPRESTAZIONE").toString()
								: "";

						if (!datInizioPrestazione.equals("") && DateUtils.compare(dataRif, datInizioPrestazione) >= 0
								&& !datFinePrestazione.equals("")
								&& DateUtils.compare(dataRif, datFinePrestazione) <= 0) {

							txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
							txExec.initTransaction();

							DomandaBean dm = new DomandaBean(domanda, txExec);

							String motivoVerifica = null;
							progressivo = (BigDecimal) domanda.getAttribute("PRGREDDITOATTIVAZIONE");
							numkloRA = (BigDecimal) domanda.getAttribute("NUMKLORA");

							boolean isDecaduto = dm.verificaDecadenza(dataRif);
							if (isDecaduto) {
								motivoVerifica = dm.getMotivoVerifica();
								Boolean res = (Boolean) txExec.executeQuery("UPDATE_DECADUTO_AUTORIZZATO_ALLA_DATA",
										new Object[] { Decodifica.Stato.DECADUTO, dataRif, motivoVerifica, numkloRA,
												cdnUtente, progressivo },
										"UPDATE");
								if (!res.booleanValue())
									throw new Exception("Impossibile procedere con l'operazione");

								// aggiorno la data decadenza e la data del controllo
								domanda.updAttribute("DATRIFERIMENTO", dataRif);
								domanda.updAttribute("DATORACONTROLLO", DateUtils.getNow());
								domandeDecadute.add(domanda);
							}

							txExec.commitTransaction();
						}

					} catch (Exception ex) {
						if (txExec != null) {
							txExec.rollBackTransaction();
						}
					}
				}

				// devo creare il file xls
				esportaDomandeExcel(domandeDecadute);
				// lo setto nella response per scaricarlo
				ZipPackager zipPackager = new ZipPackager();
				ZipEntryFile[] filesToZip = new ZipEntryFile[1];
				filesToZip[0] = new ZipEntryFile(outFileDecaduti.getName(), outFileDecaduti.getAbsolutePath());
				File outputfile = File.createTempFile("EXPORT_DECADUTI_", ".zip");
				zipPackager.zip(outputfile, filesToZip);
				response.setAttribute("fileListaDecaduti", outputfile.getAbsolutePath());

				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} else {
				reportOperation.reportFailure(MessageCodes.RedditoAttivazione.ERRORE_DATA_DECADENZA);
			}
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito esporta decaduti.", ex);
		}
	}

	public Vector getLavoratoriDaTrattare() throws Exception {
		Object params[] = null;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_DECADUTI_LAVORATORI_RA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori");
	}

	private void esportaDomandeExcel(ArrayList<SourceBean> domandeDecadute) throws Exception {
		outFileDecaduti = File.createTempFile("ListaDecaduti_", ".xls");

		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Decaduti");

		Font font = workBook.createFont();
		font.setFontName("Verdana");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);

		HSSFCellStyle style = workBook.createCellStyle();
		//style.setFillForegroundColor(HSSFColor.BLACK.index);
		style.setFont(font);

		setListToFile(sheet, style, domandeDecadute);

		FileOutputStream fileOut = new FileOutputStream(outFileDecaduti);
		workBook.write(fileOut);
		fileOut.close();
	}

	public void setListToFile(HSSFSheet sheet, HSSFCellStyle style, ArrayList<SourceBean> domandeDecadute)
			throws Exception {
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCell headerCell_0 = headerRow.createCell(0);
		headerCell_0.setCellStyle(style);
		headerCell_0.setCellValue("DATA INIZIO PRESTAZIONE");
		HSSFCell headerCell_1 = headerRow.createCell(1);
		headerCell_1.setCellStyle(style);
		headerCell_1.setCellValue("DATA FINE PRESTAZIONE");
		HSSFCell headerCell_2 = headerRow.createCell(2);
		headerCell_2.setCellStyle(style);
		headerCell_2.setCellValue("DURATA PRESTAZIONE");
		HSSFCell headerCell_3 = headerRow.createCell(3);
		headerCell_3.setCellStyle(style);
		headerCell_3.setCellValue("TIPO PRESTAZIONE");
		HSSFCell headerCell_4 = headerRow.createCell(4);
		headerCell_4.setCellStyle(style);
		headerCell_4.setCellValue("COGNOME");
		HSSFCell headerCell_5 = headerRow.createCell(5);
		headerCell_5.setCellStyle(style);
		headerCell_5.setCellValue("NOME");
		HSSFCell headerCell_6 = headerRow.createCell(6);
		headerCell_6.setCellStyle(style);
		headerCell_6.setCellValue("DATA DI NASCITA");
		HSSFCell headerCell_7 = headerRow.createCell(7);
		headerCell_7.setCellStyle(style);
		headerCell_7.setCellValue("CODICE FISCALE");
		HSSFCell headerCell_8 = headerRow.createCell(8);
		headerCell_8.setCellStyle(style);
		headerCell_8.setCellValue("COMUNE DI NASCITA");
		HSSFCell headerCell_9 = headerRow.createCell(9);
		headerCell_9.setCellStyle(style);
		headerCell_9.setCellValue("PROVINCIA DI NASCITA");
		HSSFCell headerCell_10 = headerRow.createCell(10);
		headerCell_10.setCellStyle(style);
		headerCell_10.setCellValue("STATO DI NASCITA");
		HSSFCell headerCell_11 = headerRow.createCell(11);
		headerCell_11.setCellStyle(style);
		headerCell_11.setCellValue("INDIRIZZO");
		HSSFCell headerCell_12 = headerRow.createCell(12);
		headerCell_12.setCellStyle(style);
		headerCell_12.setCellValue("CAP");
		HSSFCell headerCell_13 = headerRow.createCell(13);
		headerCell_13.setCellStyle(style);
		headerCell_13.setCellValue("COMUNE");
		HSSFCell headerCell_14 = headerRow.createCell(14);
		headerCell_14.setCellStyle(style);
		headerCell_14.setCellValue("PROVINCIA");
		HSSFCell headerCell_15 = headerRow.createCell(15);
		headerCell_15.setCellStyle(style);
		headerCell_15.setCellValue("DATA DECADENZA");
		HSSFCell headerCell_16 = headerRow.createCell(16);
		headerCell_16.setCellStyle(style);
		headerCell_16.setCellValue("DATA CONTROLLO");

		// inserisco le righe
		int contRows = domandeDecadute.size();
		for (int i = 0; i < contRows; i++) {
			SourceBean row = (SourceBean) domandeDecadute.get(i);

			HSSFRow r_1 = sheet.createRow(i + 1);
			HSSFCell c_0 = r_1.createCell(0);
			c_0.setCellValue(row.getAttribute("DATINIZIOPRESTAZIONE") == null ? ""
					: (String) row.getAttribute("DATINIZIOPRESTAZIONE"));
			HSSFCell c_1 = r_1.createCell(1);
			c_1.setCellValue(row.getAttribute("DATFINEPRESTAZIONE") == null ? ""
					: (String) row.getAttribute("DATFINEPRESTAZIONE"));
			HSSFCell c_2 = r_1.createCell(2);
			BigDecimal durata = row.getAttribute("NUMDURATAPRESTAZIONE") == null ? null
					: (BigDecimal) row.getAttribute("NUMDURATAPRESTAZIONE");
			String strDurata = "";
			if (durata != null) {
				strDurata = durata.toString();
			}
			c_2.setCellValue(strDurata);
			HSSFCell c_3 = r_1.createCell(3);
			c_3.setCellValue(row.getAttribute("STRTIPOPRESTAZIONE") == null ? ""
					: (String) row.getAttribute("STRTIPOPRESTAZIONE"));
			HSSFCell c_4 = r_1.createCell(4);
			c_4.setCellValue(row.getAttribute("STRCOGNOME") == null ? "" : (String) row.getAttribute("STRCOGNOME"));
			HSSFCell c_5 = r_1.createCell(5);
			c_5.setCellValue(row.getAttribute("STRNOME") == null ? "" : (String) row.getAttribute("STRNOME"));
			HSSFCell c_6 = r_1.createCell(6);
			c_6.setCellValue(row.getAttribute("DATNASCITA") == null ? "" : (String) row.getAttribute("DATNASCITA"));
			HSSFCell c_7 = r_1.createCell(7);
			c_7.setCellValue(
					row.getAttribute("STRCODICEFISCALE") == null ? "" : (String) row.getAttribute("STRCODICEFISCALE"));
			HSSFCell c_8 = r_1.createCell(8);
			c_8.setCellValue(
					row.getAttribute("STRCOMUNENASC") == null ? "" : (String) row.getAttribute("STRCOMUNENASC"));
			HSSFCell c_9 = r_1.createCell(9);
			c_9.setCellValue(
					row.getAttribute("STRPROVINCIANASC") == null ? "" : (String) row.getAttribute("STRPROVINCIANASC"));
			HSSFCell c_10 = r_1.createCell(10);
			c_10.setCellValue(
					row.getAttribute("CODSTATONASCITA") == null ? "" : (String) row.getAttribute("CODSTATONASCITA"));
			HSSFCell c_11 = r_1.createCell(11);
			c_11.setCellValue(
					row.getAttribute("STRINDIRIZZO") == null ? "" : (String) row.getAttribute("STRINDIRIZZO"));
			HSSFCell c_12 = r_1.createCell(12);
			c_12.setCellValue(row.getAttribute("STRCAP") == null ? "" : (String) row.getAttribute("STRCAP"));
			HSSFCell c_13 = r_1.createCell(13);
			c_13.setCellValue(row.getAttribute("STRCOMUNE") == null ? "" : (String) row.getAttribute("STRCOMUNE"));
			HSSFCell c_14 = r_1.createCell(14);
			c_14.setCellValue(
					row.getAttribute("STRPROVINCIA") == null ? "" : (String) row.getAttribute("STRPROVINCIA"));
			HSSFCell c_15 = r_1.createCell(15);
			c_15.setCellValue(
					row.getAttribute("DATRIFERIMENTO") == null ? "" : (String) row.getAttribute("DATRIFERIMENTO"));
			HSSFCell c_16 = r_1.createCell(16);
			c_16.setCellValue(
					row.getAttribute("DATORACONTROLLO") == null ? "" : (String) row.getAttribute("DATORACONTROLLO"));
		}
	}

}
