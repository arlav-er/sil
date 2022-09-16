package it.eng.sil.module.movimenti;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import oracle.sql.CLOB;

public class TrasferimentoRamoAzDBLogger {
	private static final Logger _logger = Logger.getLogger(TrasferimentoRamoAzDBLogger.class);

	private String codComunicazione;
	private String codTipoTrasf;
	private String strCfAzPrecedente;
	private String datInizio;
	private String comXml;

	public TrasferimentoRamoAzDBLogger(String codComunicazione, String codTipoTrasf, String strCfAzPrecedente,
			String datInizio, String comXml) {
		this.codComunicazione = codComunicazione;
		this.codTipoTrasf = codTipoTrasf;
		this.strCfAzPrecedente = strCfAzPrecedente;
		this.datInizio = datInizio;
		this.comXml = comXml;
	}

	public void logResult(String descrizioneErrore) {
		String html = getHTML(descrizioneErrore);
		TransactionQueryExecutor tex = null;
		try {
			tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			tex.initTransaction();
			Object prgVardatoriScarto = getKey(tex);
			Object fields[] = new Object[7];
			fields[0] = prgVardatoriScarto;
			fields[1] = codComunicazione;
			fields[2] = codTipoTrasf;
			fields[3] = strCfAzPrecedente;
			fields[4] = datInizio;
			fields[5] = html;
			fields[6] = new BigDecimal("190");
			tex.executeQuery("INSERT_CO_VARDATORI_SCARTO", fields, "INSERT");
			writeBLOB(tex, prgVardatoriScarto);
			tex.commitTransaction();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore nell'inserimento in CO_VARDATORI_SCARTO", e);
			if (tex != null)
				try {
					tex.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "IMPOSSIBILE ESEGUIRE LA ROLLBACK", e1);
				}

		}

	}

	private String getHTML(String err) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body><center><A3>Trasferimento Ramo Azienda proveniente da Comunicazione Obbligatoria</a3>");
		sb.append("<p><b>Operazione Fallita: ");
		sb.append(err);
		sb.append("</b></p>");
		sb.append("<p>Codice Comunicazione: ");
		sb.append(this.codComunicazione);
		sb.append("</p>");
		sb.append("<p>CF Azienda Precedente: ");
		sb.append(this.strCfAzPrecedente);
		sb.append("</p>");
		sb.append("<p>Cod. Tipo Trasferimento: ");
		sb.append(this.codTipoTrasf);
		sb.append("</p>");
		sb.append("<p>Data Inizio Trasferimento: ");
		sb.append(this.datInizio);
		sb.append("</p>");
		sb.append("</center></body></html>");

		return sb.toString();
	}

	protected void writeBLOB(TransactionQueryExecutor trans, Object key) throws Exception {

		DataConnection conn = trans.getDataConnection();
		String stmt = SQLStatements.getStatement("SELECT_FOR_UPDATE_CO_VARDATORI_SCARTO");
		SQLCommand selectCommand = conn.createSelectCommand(stmt);
		ArrayList inputParameters = new ArrayList(1);
		inputParameters.add(conn.createDataField("prgVardatoriScarto", Types.BIGINT, key));
		try {
			DataResult dr = selectCommand.execute(inputParameters);
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("XMLTRACCIATOINVIATO");
			CLOB resultClob = (CLOB) df.getObjectValue();
			Writer outStream = resultClob.setCharacterStream(0L);

			StringReader br = new StringReader(this.comXml);
			int chunkSize = resultClob.getChunkSize();
			char[] buff = new char[chunkSize];
			int nRead = 0;
			while ((nRead = br.read(buff)) > 0) {
				outStream.write(buff, 0, nRead);
			}
			outStream.flush();
			outStream.close();
			return;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile scrivere il log del risultato", e);
			throw new Exception("Impossibile scrivere il log del risultato");
		}
	}

	private Object getKey(TransactionQueryExecutor tex) throws Exception {
		Object key;
		SourceBean sb = (SourceBean) tex.executeQuery("SELECT_CO_VARDATORI_SCARTO_NEXTVAL", new Object[] {}, "SELECT");
		if (sb == null)
			throw new Exception("Impossibile leggere il CO_VARDATORI_SCARTO.NEXTVAL");
		key = sb.getAttribute("ROW.NEXTVAL");
		if (key == null)
			throw new Exception("Impossibile leggere il CO_VARDATORI_SCARTO.NEXTVAL");
		return key;
	}
}
