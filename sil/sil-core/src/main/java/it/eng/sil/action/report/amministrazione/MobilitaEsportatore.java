package it.eng.sil.action.report.amministrazione;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.svcon.jdbf.DBFWriter;
import com.svcon.jdbf.JDBFException;
import com.svcon.jdbf.JDBField;

import it.eng.sil.bean.MobilitaExpThread;
import it.eng.sil.util.ZipEntryFile;
import it.eng.sil.util.ZipPackager;

/**
 * @author Alessandro Pegoraro
 * @since 2.1.0a_test
 * 
 *        19/09/2007
 *
 */
public class MobilitaEsportatore {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaEsportatore.class.getName());
	// static HashSet<String> pivotmoblav = new HashSet<String>();
	private final String className = this.getClass().getName();
	// final String dirDestFileDBF = "c:\\DatMB_DBF\\";
	private String outputFileName = null;
	/* Query di estrazione mobilità */
	String sql_isc = SQLStatements.getStatement("MB_DBF_ISCR");
	String sql_iscrei = SQLStatements.getStatement("MB_DBF_ISCREI");
	String sql_iscstu = SQLStatements.getStatement("MB_DBF_ISCSTU");
	String sql_iscmob = SQLStatements.getStatement("MB_DBF_ISCMOB");
	String sql_iscatt = SQLStatements.getStatement("MB_DBF_ISCATT");
	String sql_aziende = SQLStatements.getStatement("MB_DBF_AZIENDE");
	String sql_isccan = SQLStatements.getStatement("MB_DBF_ISCCAN");
	String sql_iscavv = SQLStatements.getStatement("MB_DBF_ISCAVV");
	String sql_destfile = SQLStatements.getStatement("MB_DBF_FILENAME");
	String sql_insert_appoggio = SQLStatements.getStatement("MB_DBF_INSERT_APPOGGIO");

	String sql_isc_tutto = SQLStatements.getStatement("MB_DBF_ISCR_TUTTO");
	String sql_iscrei_tutto = SQLStatements.getStatement("MB_DBF_ISCREI_TUTTO");
	String sql_iscstu_tutto = SQLStatements.getStatement("MB_DBF_ISCSTU_TUTTO");
	String sql_iscmob_tutto = SQLStatements.getStatement("MB_DBF_ISCMOB_TUTTO");
	String sql_iscatt_tutto = SQLStatements.getStatement("MB_DBF_ISCATT_TUTTO");
	String sql_aziende_tutto = SQLStatements.getStatement("MB_DBF_AZIENDE_TUTTO");
	String sql_isccan_tutto = SQLStatements.getStatement("MB_DBF_ISCCAN_TUTTO");
	String sql_iscavv_tutto = SQLStatements.getStatement("MB_DBF_ISCAVV_TUTTO");
	String sql_destfile_tutto = SQLStatements.getStatement("MB_DBF_FILENAME_TUTTO");

	/* Esporta anche inviati ? */
	String ancheinviati = "";
	String sessionid = "";

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	/* Connessione al DB per la transazione */
	DataConnection dataConn = null;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	/* Files DBF da esportare */
	DBFWriter iscAvvWriter = null;
	DBFWriter aziendeWriter = null;
	DBFWriter iscAttWriter = null;
	DBFWriter iscMobWriter = null;
	DBFWriter iscrWriter = null;
	DBFWriter iscReiWriter = null;
	DBFWriter iscCanWriter = null;
	DBFWriter iscStuWriter = null;
	/* data e formattatore per ora non sono usati */
	Date expdate = new Date();
	Format formatter;
	String formatted;

	/**
	 * Il metodo execute apre i file in scrittura e richiama le funzioni di generazione; quindi crea uno ZIP file
	 * contenente i DBF esportati
	 * 
	 * @author Pegoraro
	 * @return il File contenente tutti i DBF della mobilità
	 * @param list
	 *            il thread di esportazione a cui passare il feedback
	 * @since 2.1.0a_test
	 */
	public File execute(MobilitaExpThread list) {
		formatter = new SimpleDateFormat("HH:mm:ss");
		expdate = new Date();

		list.add("Preparazione del processo di esportazione...Attendere");
		// list.add("Inizio del processo di esportazione.." + formatter.format(expdate));
		/* array dei file da zippare */
		File[] outFiles = new File[8];
		/* output ZIP File */
		File outputfile;

		FileFilter dbffilter = new DBFFileFilter();
		File emptyDbfDir = new File(ConfigSingleton.getRootPath() + "\\WEB-INF\\report\\DBF\\MB\\tracce\\");
		// System.out.println(emptyDbfDir.listFiles(dbffilter).length);
		int emptyFiles = emptyDbfDir.listFiles(dbffilter).length;
		File[] emptyDbf = emptyDbfDir.listFiles(dbffilter);
		try {

			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();
			con.setAutoCommit(false);

			/* Trovo nome file */
			Statement stmt2;
			stmt2 = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt2.executeQuery(sql_destfile);

			if (rs.next()) {
				outputFileName = rs.getString("STRVALORE");
			}

			/* Lancio Stored Procedure per popolare la tabella temporanea */
			CallableStatement stmt = con.prepareCall("{call pg_mb.fill_global_mb}");
			stmt.execute();

			list.add("Preparazione completata. Inizio del processo di Esportazione");
			/* Esportazioni collegate alle aziende->Transazione */
			outFiles[0] = esportaIscmob(list);
			outFiles[1] = esportaAziende(list);
			outFiles[2] = esportaIscavv(list);

			/* La commit termina la transazione e tronca la tabella globale */
			con.commit();

		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "execute(): errore EMF", (Exception) e);

			try {
				con.rollback();
			} catch (SQLException e1) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "execute():errore nella rollback()", e1);

			}
			return null;
		} catch (SQLException e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "execute():errore SQL", (Exception) e);

			try {
				con.rollback();
			} catch (SQLException e1) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "execute():errore nella rollback()", e1);

			}
			return null;
			/* Chiusura connessione JDBC relativa alla transazione che accede a GLOBAL_MB */
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (dataConn != null)
					dataConn.close();
			} catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":execute():Errore rilascio Risorse:", ex);

			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":execute():Errore chiusura connessione",
						(Exception) e);

			}
		}
		/*
		 * Esecuzione esportazioni slegate dalle aziende. Ognuna apre e chiude la connessione gestendo eventuali errori
		 */
		outFiles[3] = esportaIscr(list);
		outFiles[4] = esportaIscrei(list);
		outFiles[5] = esportaIscstu(list);
		outFiles[6] = esportaIscatt(list);
		outFiles[7] = esportaIscan(list);

		/* Ora zippo tutto e buonanotte al secchio */
		ZipPackager zipPackager = new ZipPackager();
		ZipEntryFile[] filesToZip = new ZipEntryFile[8 + emptyFiles];

		filesToZip[0] = new ZipEntryFile("MOBMOB.DBF", outFiles[0].getAbsolutePath());
		filesToZip[1] = new ZipEntryFile("MOBENDE.DBF", outFiles[1].getAbsolutePath());
		filesToZip[2] = new ZipEntryFile("MOBAVV.DBF", outFiles[2].getAbsolutePath());

		filesToZip[3] = new ZipEntryFile("MOBR.DBF", outFiles[3].getAbsolutePath());
		filesToZip[4] = new ZipEntryFile("MOBREI.DBF", outFiles[4].getAbsolutePath());
		filesToZip[5] = new ZipEntryFile("MOBSTU.DBF", outFiles[5].getAbsolutePath());
		filesToZip[6] = new ZipEntryFile("MOBATT.DBF", outFiles[6].getAbsolutePath());
		filesToZip[7] = new ZipEntryFile("MOBCAN.DBF", outFiles[7].getAbsolutePath());

		/*
		 * PER I TEST filesToZip[0] = new ZipEntryFile("MOBMOB.DBF", outFiles[0].getAbsolutePath()); filesToZip[1] = new
		 * ZipEntryFile("MOBENDE.DBF", outFiles[0].getAbsolutePath()); filesToZip[2] = new ZipEntryFile("MOBAVV.DBF",
		 * outFiles[0].getAbsolutePath());
		 * 
		 * filesToZip[3] = new ZipEntryFile("MOBR.DBF", outFiles[0].getAbsolutePath()); filesToZip[4] = new
		 * ZipEntryFile("MOBREI.DBF", outFiles[0].getAbsolutePath()); filesToZip[5] = new ZipEntryFile("MOBSTU.DBF",
		 * outFiles[0].getAbsolutePath()); filesToZip[6] = new ZipEntryFile("MOBATT.DBF",
		 * outFiles[0].getAbsolutePath()); filesToZip[7] = new ZipEntryFile("MOBCAN.DBF",
		 * outFiles[0].getAbsolutePath());
		 */

		/* Aggiungo all'archivio i DBF vuoti */
		for (int i = 0; i < emptyFiles; i++) {
			filesToZip[8 + i] = new ZipEntryFile(emptyDbf[i].getName(), emptyDbf[i].getAbsolutePath());
		}

		try {
			/* Creazione zipfile da restituire */
			outputfile = File.createTempFile("~DBF", ".zip");
			zipPackager.zip(outputfile, filesToZip);

			list.add("Esportazione DBF Completata");

			// list.add(outputfile.getAbsolutePath() + " contiene i files esportati");
			// System.out.println(outputfile.getAbsolutePath() + " contiene i files esportati");

		} catch (IOException ie) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":execute() :errore zippatura File", ie);

			ie.printStackTrace();
			return null;
		}
		/* Cancellazione Files temporanei */
		/*
		 * for (int i = 0; i < 8; i++) { boolean res; res = outFiles[i].delete(); if (!res) _logger.debug( className +
		 * ":execute() :errore cancellazione File temporanei");
		 * 
		 * }
		 */

		_logger.debug(className + ":execute() :prodotto: " + outputfile.getAbsolutePath());

		return outputfile;
	}

	/* Esportazione del file iscmob.DBF */
	private File esportaIscmob(MobilitaExpThread list) throws SQLException {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscmob_fields = new JDBField[25];
		/* Contenitore Record DBF */
		Object[] mob_iscmob_record = new Object[25];
		/* File da restituire */
		File tmpFile = null;
		/* Inizializza la connessione per il recupero Dati */
		expdate = new Date();
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		// list.add("Inizio Esportazione ISCMOB.DBF - " + formatter.format(expdate));
		try {
			/* Dichiarazione campi DBF */
			int j = 0;
			mob_iscmob_fields[j++] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscmob_fields[j++] = new JDBField("RAGMOB", 'C', 21, 0); //
			mob_iscmob_fields[j++] = new JDBField("TIPMOB", 'C', 2, 0); //
			mob_iscmob_fields[j++] = new JDBField("ASSMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("CESMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("INIMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("SPEMOB", 'C', 1, 0);
			mob_iscmob_fields[j++] = new JDBField("INDMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("FNDMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("FINMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("MOTMOB", 'C', 1, 0);
			mob_iscmob_fields[j++] = new JDBField("DIFMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("NOTMOB", 'C', 1, 0);
			mob_iscmob_fields[j++] = new JDBField("CRIMOB", 'D', 8, 0);
			mob_iscmob_fields[j++] = new JDBField("PROMOB", 'C', 2, 0);
			mob_iscmob_fields[j++] = new JDBField("NUMMOB", 'C', 6, 0);
			mob_iscmob_fields[j++] = new JDBField("QUAMOB", 'C', 6, 0);
			mob_iscmob_fields[j++] = new JDBField("AREMOB", 'C', 2, 0);
			mob_iscmob_fields[j++] = new JDBField("GRAMOB", 'C', 2, 0);
			mob_iscmob_fields[j++] = new JDBField("CNLMOB", 'C', 4, 0);
			mob_iscmob_fields[j++] = new JDBField("LIVMOB", 'C', 2, 0);
			mob_iscmob_fields[j++] = new JDBField("SCHMOB", 'C', 1, 0);
			mob_iscmob_fields[j++] = new JDBField("DISMOB", 'C', 10, 0);
			mob_iscmob_fields[j++] = new JDBField("LUNMOB", 'C', 1, 0);
			mob_iscmob_fields[j++] = new JDBField("INTMOB", 'C', 1, 0);
		} catch (JDBFException je) {
			it.eng.sil.util.TraceWrapper.error(_logger, className + ":esporta_iscmob() :errore Scrittura header JDBF",
					je);

			return null;
		}

		try {

			// Statement del = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// del.executeQuery("TRUNCATE TABLE AM_MOBILITA_APPOGGIO");
			// del.close();

			tmpFile = File.createTempFile("~ISCMOB", "DBF");
			iscMobWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscmob_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_iscmob_tutto);
			else
				rs = stmt.executeQuery(sql_iscmob);

			_logger.info("esporta anche inviati? " + ancheinviati);

			while (rs.next()) {

				String datnasclav = rs.getString("DATNAS_LA") == null ? "" : rs.getString("DATNAS_LA");
				String datAss = rs.getString("ASSMOB") == null ? "" : rs.getString("ASSMOB");
				String datalicBonificata = rs.getString("CESMOB") == null ? "" : rs.getString("CESMOB");
				String codcpitit = rs.getString("CODCPITIT") == null ? "" : rs.getString("CODCPITIT");

				String strLocalitaRes = rs.getString("STRLOCALITARES") == null ? "" : rs.getString("STRLOCALITARES");
				String strLocalitaDom = rs.getString("STRLOCALITADOM") == null ? "" : rs.getString("STRLOCALITADOM");

				String strRagioneSoc = rs.getString("STRRAGIONESOCIALE") == null ? ""
						: rs.getString("STRRAGIONESOCIALE");
				String strCodiceFiscaleAz = rs.getString("STRCODICEFISCALEAZ") == null ? ""
						: rs.getString("STRCODICEFISCALEAZ");
				/* Solo se sospesa inserisci in appoggio */

				String CODMONOATT = rs.getString("CODMONOATT");
				String CODFIS_LA = rs.getString("CODFIS_LA");
				if (CODMONOATT.compareTo("S") == 0) {
					final String PRGMOBILITA = rs.getString("PRGMOBILITA");
					final String STRCOGNOME = rs.getString("STRCOGNOME");
					final String STRNOME = rs.getString("STRNOME");
					final String CODCOMNAS = rs.getString("CODCOMNAS");
					final String CODTIPOMOB = rs.getString("CODTIPOMOB");
					final String STRSESSO = rs.getString("STRSESSO");
					final String CODCITTADINANZA = rs.getString("CODCITTADINANZA");
					final String CODCOMDOM = rs.getString("CODCOMDOM");
					final String STRINDIRIZZODOM = rs.getString("STRINDIRIZZODOM");
					final String CODCOMRES = rs.getString("CODCOMRES");
					final String STRINDIRIZZORES = rs.getString("STRINDIRIZZORES");
					/*
					 * String todo = sql_insert_appoggio + "VALUES ('" + CODFIS_LA + "','" + sessionid + "','" +
					 * CODMONOATT + "'," + PRGMOBILITA + ",'" + STRCOGNOME + "','" + STRNOME + "', to_date('" +
					 * datnasclav + "','dd/mm/yyyy')" + ",'" + CODCOMNAS + "','" + CODTIPOMOB + "', to_date('" +
					 * datalicBonificata + "','yyyymmdd')" + ",'" + codcpitit + "','" + strRagioneSoc.replaceAll("'",
					 * "''") + "', to_date('" + datAss + "','yyyymmdd')" + ",'" + STRSESSO + "','" + CODCITTADINANZA +
					 * "','" + CODCOMRES + "','" + STRINDIRIZZORES + "','" + strLocalitaRes + "','" + CODCOMDOM + "','"
					 * + STRINDIRIZZODOM + "','" + strLocalitaDom + "','" + strCodiceFiscaleAz + "')";
					 * _logger.info(todo); Statement ins = con.createStatement( ResultSet.TYPE_FORWARD_ONLY,
					 * ResultSet.CONCUR_UPDATABLE); ins.executeQuery(todo); //rstemp.close(); ins.close();
					 */
					_logger.info(sql_insert_appoggio);
					PreparedStatement ps = con.prepareStatement(sql_insert_appoggio);
					int i = 1;
					ps.setString(i++, CODFIS_LA);
					ps.setString(i++, sessionid);
					ps.setString(i++, CODMONOATT);
					final long parseLongPrgMobilita = Long.parseLong(PRGMOBILITA);
					ps.setLong(i++, parseLongPrgMobilita);
					ps.setString(i++, STRCOGNOME);
					ps.setString(i++, STRNOME);
					ps.setString(i++, datnasclav);
					ps.setString(i++, CODCOMNAS);
					ps.setString(i++, CODTIPOMOB);
					ps.setString(i++, datalicBonificata);
					ps.setString(i++, codcpitit);
					ps.setString(i++, strRagioneSoc);
					ps.setString(i++, datAss);
					ps.setString(i++, STRSESSO);
					ps.setString(i++, CODCITTADINANZA);
					ps.setString(i++, CODCOMRES);
					ps.setString(i++, STRINDIRIZZORES);
					ps.setString(i++, strLocalitaRes);
					ps.setString(i++, CODCOMDOM);
					ps.setString(i++, STRINDIRIZZODOM);
					ps.setString(i++, strLocalitaDom);
					ps.setString(i++, strCodiceFiscaleAz);
					int risultato = ps.executeUpdate();
					if (risultato < 1) {
						_logger.error("Non inserito record sulla tabella am_rep_mobilita_appoggio: CODFIS_LA:"
								+ CODFIS_LA + ", STRCOGNOME:" + STRCOGNOME + ", STRNOME:" + STRNOME);
					}
					ps.close();

				}
				/* Riempio la tabella per la stampa */
				// pivotmoblav.add(rs.getString("CODFIS_LA"));
				mob_iscmob_record[0] = CODFIS_LA;
				mob_iscmob_record[1] = rs.getString("RAGMOB");
				mob_iscmob_record[2] = rs.getString("TIPMOB");
				if (rs.getString("ASSMOB") != null)
					mob_iscmob_record[3] = mob_iscmob_fields[3].parse(rs.getString("ASSMOB"));
				else
					mob_iscmob_record[3] = rs.getString("ASSMOB");
				if (rs.getString("CESMOB") != null)
					mob_iscmob_record[4] = mob_iscmob_fields[4].parse(rs.getString("CESMOB"));
				else
					mob_iscmob_record[4] = rs.getString("CESMOB");
				if (rs.getString("INIMOB") != null)
					mob_iscmob_record[5] = mob_iscmob_fields[5].parse(rs.getString("INIMOB"));
				else
					mob_iscmob_record[5] = rs.getString("INIMOB");
				mob_iscmob_record[6] = rs.getString("SPEMOB");
				if (rs.getString("INDMOB") != null)
					mob_iscmob_record[7] = mob_iscmob_fields[7].parse(rs.getString("INDMOB"));
				else
					mob_iscmob_record[7] = rs.getString("INDMOB");
				if (rs.getString("FNDMOB") != null)
					mob_iscmob_record[8] = mob_iscmob_fields[8].parse(rs.getString("FNDMOB"));
				else
					mob_iscmob_record[8] = rs.getString("FNDMOB");
				if (rs.getString("FINMOB") != null)
					mob_iscmob_record[9] = mob_iscmob_fields[9].parse(rs.getString("FINMOB"));
				else
					mob_iscmob_record[9] = rs.getString("FINMOB");
				mob_iscmob_record[10] = rs.getString("MOTMOB");
				if (rs.getString("DIFMOB") != null)
					mob_iscmob_record[11] = mob_iscmob_fields[11].parse(rs.getString("DIFMOB"));
				else
					mob_iscmob_record[11] = rs.getString("DIFMOB");
				mob_iscmob_record[12] = rs.getString("NOTMOB");
				if (rs.getString("CRIMOB") != null)
					mob_iscmob_record[13] = mob_iscmob_fields[13].parse(rs.getString("CRIMOB"));
				else
					mob_iscmob_record[13] = rs.getString("CRIMOB");
				mob_iscmob_record[14] = rs.getString("PROMOB");
				mob_iscmob_record[15] = rs.getString("NUMMOB");
				mob_iscmob_record[16] = rs.getString("QUAMOB");
				mob_iscmob_record[17] = rs.getString("AREMOB");
				mob_iscmob_record[18] = rs.getString("GRAMOB");
				mob_iscmob_record[19] = rs.getString("CNLMOB");
				mob_iscmob_record[20] = rs.getString("LIVMOB");
				mob_iscmob_record[21] = rs.getString("SCHMOB");
				mob_iscmob_record[22] = rs.getString("DISMOB");
				mob_iscmob_record[23] = rs.getString("LUNMOB");
				mob_iscmob_record[24] = rs.getString("INTMOB");
				/* Aggiunge il record e cicla */
				try {
					iscMobWriter.addRecord(mob_iscmob_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscmob():impossibile esportare questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscmob():esportati " + iCount + " Record");

			list.add("esportato <b>MOBMOB.DBF</b>");
			/*
			 * list.add("esportato <b>ISCMOB.DBF</b>: " + iCount+" iscrizioni"); if (iJump>0)
			 * list.add("!!!! ISCMOB.DBF: " + iCount+" iscrizioni");
			 */
			list.add("------");
			return tmpFile;

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					className + ":esporta_iscmob():errore nella addRecord(mob_iscmob_record)", jex);

		} catch (IOException jex2) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					className + ":esporta_iscmob():errore nella addRecord(mob_iscmob_record)", jex2);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, className + ":esporta_iscmob():errore:", ex);

		} finally {
			if (iscMobWriter != null) {
				try {
					iscMobWriter.close();
				} catch (JDBFException je) {
					it.eng.sil.util.TraceWrapper.error(_logger, className + ":esporta_iscmob():errore chiusura file",
							(Exception) je);
				}
			}
		}
		/* Chiusura connessione JDBC */
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		return tmpFile;
		// close DBF
	} // esporta_iscmob()

	private File esportaAziende(MobilitaExpThread list) throws SQLException {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_aziende_fields = new JDBField[23];
		/* Contenitore Record DBF */
		Object[] mob_aziende_record = new Object[23];
		/* File da restituire */
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		expdate = new Date();
		// list.add("Inizio Esportazione AZIENDE.DBF - " + formatter.format(expdate));
		try {
			/* Dichiarazione campi DBF */
			int j = 0;
			mob_aziende_fields[j++] = new JDBField("PART_IVA", 'C', 21, 0);
			mob_aziende_fields[j++] = new JDBField("RAG_SOC", 'C', 35, 0);
			mob_aziende_fields[j++] = new JDBField("COD_ATT", 'C', 5, 0);
			mob_aziende_fields[j++] = new JDBField("CCNL_AZ", 'C', 4, 0);
			mob_aziende_fields[j++] = new JDBField("IND_AZ", 'C', 30, 0);
			mob_aziende_fields[j++] = new JDBField("TEL_AZ", 'C', 15, 0);
			mob_aziende_fields[j++] = new JDBField("LIB_AZ", 'C', 9, 0);
			mob_aziende_fields[j++] = new JDBField("CAP_AZ", 'C', 5, 0);
			mob_aziende_fields[j++] = new JDBField("CON_AZ", 'C', 1, 0);
			mob_aziende_fields[j++] = new JDBField("DAL_AZ", 'D', 8, 0);
			mob_aziende_fields[j++] = new JDBField("NUM_AZ", 'C', 6, 0);
			mob_aziende_fields[j++] = new JDBField("INP_AZ", 'C', 16, 0);
			mob_aziende_fields[j++] = new JDBField("RCD_AZ", 'C', 6, 0);
			mob_aziende_fields[j++] = new JDBField("ALI_AZ", 'C', 3, 0);
			mob_aziende_fields[j++] = new JDBField("RIF_AZ", 'C', 25, 0);
			mob_aziende_fields[j++] = new JDBField("VAL_AZ", 'C', 1, 0);
			mob_aziende_fields[j++] = new JDBField("CL9_AZ", 'C', 1, 0);
			mob_aziende_fields[j++] = new JDBField("PI_AZ", 'C', 11, 0);
			mob_aziende_fields[j++] = new JDBField("MAIL_AZ", 'C', 50, 0);
			mob_aziende_fields[j++] = new JDBField("FAX_AZ", 'C', 15, 0);
			mob_aziende_fields[j++] = new JDBField("TIP_AZ", 'C', 2, 0);
			mob_aziende_fields[j++] = new JDBField("ASS_AZ", 'C', 5, 0);
			mob_aziende_fields[j++] = new JDBField("POS_AZ", 'C', 1, 0);
		} catch (JDBFException je) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_aziende() :errore Scrittura header JDBF",
					je);

			return null;
		}
		try {
			tmpFile = File.createTempFile("~AZIENDE", "DBF");
			aziendeWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_aziende_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_aziende_tutto);
			else
				rs = stmt.executeQuery(sql_aziende);

			while (rs.next()) {
				int j = 0;
				mob_aziende_record[j++] = rs.getString("PART_IVA");
				mob_aziende_record[j++] = rs.getString("RAG_SOC");
				mob_aziende_record[j++] = rs.getString("COD_ATT");
				mob_aziende_record[j++] = rs.getString("CCNL_AZ");
				mob_aziende_record[j++] = rs.getString("IND_AZ");
				mob_aziende_record[j++] = rs.getString("TEL_AZ");
				mob_aziende_record[j++] = rs.getString("LIB_AZ");
				mob_aziende_record[j++] = rs.getString("CAP_AZ");
				mob_aziende_record[j++] = rs.getString("CON_AZ");
				if (rs.getString("DAL_AZ") != null)
					mob_aziende_record[j] = mob_aziende_fields[j].parse(rs.getString("DAL_AZ"));
				else
					mob_aziende_record[j] = rs.getString("DAL_AZ");
				j++;
				mob_aziende_record[j++] = rs.getString("NUM_AZ");
				mob_aziende_record[j++] = rs.getString("INP_AZ");
				mob_aziende_record[j++] = rs.getString("RCD_AZ");
				mob_aziende_record[j++] = rs.getString("ALI_AZ");
				mob_aziende_record[j++] = rs.getString("RIF_AZ");
				mob_aziende_record[j++] = rs.getString("VAL_AZ");
				mob_aziende_record[j++] = rs.getString("CL9_AZ");
				mob_aziende_record[j++] = rs.getString("PI_AZ");
				mob_aziende_record[j++] = rs.getString("MAIL_AZ");
				mob_aziende_record[j++] = rs.getString("FAX_AZ");
				mob_aziende_record[j++] = rs.getString("TIP_AZ");
				mob_aziende_record[j++] = rs.getString("ASS_AZ");
				mob_aziende_record[j++] = rs.getString("POS_AZ");
				/* Aggiunge il record e cicla */
				try {
					aziendeWriter.addRecord(mob_aziende_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_aziende():impossibile esportare questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_aziende():esportati " + iCount + " Record");

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_aziende():errore nella addRecord(mob_iscr_record)", jex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_aziende():errore nella scrittura del file temporaneo", iex);

		} finally {
			if (aziendeWriter != null) {
				try {
					aziendeWriter.close();
				} catch (JDBFException je) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_aziende():errore chiusura file",
							(Exception) je);

				}
			}
		}
		/* Chiusura connessione JDBC */
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		list.add("esportato <b>MOBENDE.DBF</b>");
		list.add("------");
		return tmpFile;
	} // esporta_aziende()

	/* Esportazione del file iscavv.DBF */
	private File esportaIscavv(MobilitaExpThread list) throws SQLException {

		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscavv_fields = new JDBField[40];
		/* Contenitore Record DBF */
		Object[] mob_iscavv_record = new Object[40];
		/* File da restituire */
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		// expdate = new Date();
		// list.add("Inizio Esportazione ISCAVV.DBF - " + formatter.format(expdate));
		try {
			/* Dichiarazione campi DBF */
			int j = 0;
			mob_iscavv_fields[j++] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscavv_fields[j++] = new JDBField("DATA_AVV", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("TIPO_AVV", 'C', 3, 0);
			mob_iscavv_fields[j++] = new JDBField("RISE_AVV", 'C', 1, 0);
			mob_iscavv_fields[j++] = new JDBField("RAPP_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("NORM_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("PART_IVA", 'C', 21, 0);
			mob_iscavv_fields[j++] = new JDBField("QUAL_AVV", 'C', 6, 0);
			mob_iscavv_fields[j++] = new JDBField("GRAD_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("CCNL_AVV", 'C', 4, 0);
			mob_iscavv_fields[j++] = new JDBField("CAMPO1", 'C', 10, 0);
			mob_iscavv_fields[j++] = new JDBField("CAMPO2", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("DATA_LIC", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("MOT_LIC", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("SEZ_CIRC", 'C', 9, 0);
			mob_iscavv_fields[j++] = new JDBField("PRECED", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("VALPRE", 'C', 1, 0);
			mob_iscavv_fields[j++] = new JDBField("DATPROT1", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("DATPROT2", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("MESI_AVV", 'C', 4, 0);
			mob_iscavv_fields[j++] = new JDBField("ORE_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("NOTE_AVV", 'C', 10, 0);
			mob_iscavv_fields[j++] = new JDBField("BENE_AVV", 'C', 1, 0);
			mob_iscavv_fields[j++] = new JDBField("AREA_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("LIVE_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("LUOG_AVV", 'C', 21, 0);
			mob_iscavv_fields[j++] = new JDBField("CAMPO3", 'C', 3, 0);
			mob_iscavv_fields[j++] = new JDBField("MATR_AVV", 'C', 5, 0);
			mob_iscavv_fields[j++] = new JDBField("GGPRE", 'C', 3, 0);
			mob_iscavv_fields[j++] = new JDBField("GGEFF", 'C', 3, 0);
			mob_iscavv_fields[j++] = new JDBField("REGIMP", 'C', 1, 0);
			mob_iscavv_fields[j++] = new JDBField("LAV_AVV", 'C', 4, 0);
			mob_iscavv_fields[j++] = new JDBField("POD_AVV", 'C', 2, 0);
			mob_iscavv_fields[j++] = new JDBField("COR_INI", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("COR_ORE", 'C', 3, 0);
			mob_iscavv_fields[j++] = new JDBField("DCONL68", 'D', 8, 0);
			mob_iscavv_fields[j++] = new JDBField("NCONL68", 'C', 6, 0);
			mob_iscavv_fields[j++] = new JDBField("REDD_AVV", 'C', 10, 0);
			mob_iscavv_fields[j++] = new JDBField("PT_AVV", 'C', 1, 0);
			mob_iscavv_fields[j++] = new JDBField("DISP_AVV", 'C', 10, 0);
		} catch (JDBFException je) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscavv() :errore Scrittura header JDBF",
					je);

			return null;
		}
		try {
			tmpFile = File.createTempFile("~ISCAVV", "DBF");
			iscAvvWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscavv_fields);
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_iscavv);

			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_iscavv_tutto);
			else
				rs = stmt.executeQuery(sql_iscavv);

			while (rs.next()) {
				mob_iscavv_record[0] = rs.getString("CODFIS_LA");
				if (rs.getString("DATA_AVV") != null)
					mob_iscavv_record[1] = mob_iscavv_fields[1].parse(rs.getString("DATA_AVV"));
				else
					mob_iscavv_record[1] = rs.getString("DATA_AVV");
				mob_iscavv_record[2] = rs.getString("TIPO_AVV");
				mob_iscavv_record[3] = rs.getString("RISE_AVV");
				mob_iscavv_record[4] = rs.getString("RAPP_AVV");
				mob_iscavv_record[5] = rs.getString("NORM_AVV");
				mob_iscavv_record[6] = rs.getString("PART_IVA");
				mob_iscavv_record[7] = rs.getString("QUAL_AVV");
				mob_iscavv_record[8] = rs.getString("GRAD_AVV");
				mob_iscavv_record[9] = rs.getString("CCNL_AVV");
				mob_iscavv_record[10] = rs.getString("CAMPO1");
				if (rs.getString("CAMPO2") != null)
					mob_iscavv_record[11] = mob_iscavv_fields[11].parse(rs.getString("CAMPO2"));
				else
					mob_iscavv_record[11] = rs.getString("CAMPO2");
				if (rs.getString("DATA_LIC") != null)
					mob_iscavv_record[12] = mob_iscavv_fields[12].parse(rs.getString("DATA_LIC"));
				else
					mob_iscavv_record[12] = rs.getString("DATA_LIC");
				mob_iscavv_record[13] = rs.getString("MOT_LIC");
				mob_iscavv_record[14] = rs.getString("SEZ_CIRC");
				if (rs.getString("PRECED") != null)
					mob_iscavv_record[15] = mob_iscavv_fields[15].parse(rs.getString("PRECED"));
				else
					mob_iscavv_record[15] = rs.getString("PRECED");
				mob_iscavv_record[16] = rs.getString("VALPRE");
				if (rs.getString("DATPROT1") != null)
					mob_iscavv_record[17] = mob_iscavv_fields[17].parse(rs.getString("DATPROT1"));
				else
					mob_iscavv_record[17] = rs.getString("DATPROT1");
				if (rs.getString("DATPROT2") != null)
					mob_iscavv_record[18] = mob_iscavv_fields[18].parse(rs.getString("DATPROT2"));
				else
					mob_iscavv_record[18] = rs.getString("DATPROT2");
				// mob_iscavv_record[19] = rs.getString("MESI_AVV");
				// mob_iscavv_record[20] = rs.getString("ORE_AVV");
				String tmp = rs.getString("MESI_AVV");
				if (tmp != null && tmp.length() > 4)
					mob_iscavv_record[19] = "";
				else
					mob_iscavv_record[19] = rs.getString("MESI_AVV");

				Long tempry = new Long(Math.round(rs.getDouble("ORE_AVV")));
				mob_iscavv_record[20] = tempry.toString();

				mob_iscavv_record[21] = rs.getString("NOTE_AVV");
				mob_iscavv_record[22] = rs.getString("BENE_AVV");
				mob_iscavv_record[23] = rs.getString("AREA_AVV");
				mob_iscavv_record[24] = rs.getString("LIVE_AVV");
				mob_iscavv_record[25] = rs.getString("LUOG_AVV");
				mob_iscavv_record[26] = rs.getString("CAMPO3");
				mob_iscavv_record[27] = rs.getString("MATR_AVV");
				mob_iscavv_record[28] = rs.getString("GGPRE");
				mob_iscavv_record[29] = rs.getString("GGEFF");
				mob_iscavv_record[30] = rs.getString("REGIMP");
				mob_iscavv_record[31] = rs.getString("LAV_AVV");
				mob_iscavv_record[32] = rs.getString("POD_AVV");
				if (rs.getString("COR_INI") != null)
					mob_iscavv_record[33] = mob_iscavv_fields[33].parse(rs.getString("COR_INI"));
				else
					mob_iscavv_record[33] = rs.getString("COR_INI");
				mob_iscavv_record[34] = rs.getString("COR_ORE");
				if (rs.getString("DCONL68") != null)
					mob_iscavv_record[35] = mob_iscavv_fields[35].parse(rs.getString("DCONL68"));
				else
					mob_iscavv_record[35] = rs.getString("DCONL68");
				mob_iscavv_record[36] = rs.getString("NCONL68");
				mob_iscavv_record[37] = rs.getString("REDD_AVV");
				mob_iscavv_record[38] = rs.getString("PT_AVV");
				mob_iscavv_record[39] = rs.getString("DISP_AVV");
				/* Aggiunge il record e cicla */
				try {
					iscAvvWriter.addRecord(mob_iscavv_record);
				} catch (JDBFException sex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscavv():impossibile aggiungere il record", sex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscavv():esportati " + iCount + " Record");

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscavv():errore nella addRecord(mob_iscavv_record)", jex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscavv():errore nella scrittura del file temporaneo", iex);

		} finally {
			if (iscAvvWriter != null) {
				try {
					iscAvvWriter.close();
				} catch (JDBFException je) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscavv():errore chiusura file",
							(Exception) je);

				}
			}
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		/* Chiusura connessione JDBC */
		/*
		 * if (rs != null) rs.close(); if (stmt != null) stmt.close();
		 */
		list.add("esportato <b>MOBAVV.DBF</b>");
		list.add("------");
		return tmpFile;
	} // esporta_iscavv()

	/* Esportazione del file ISCR.DBF */
	private File esportaIscr(MobilitaExpThread list) {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscr_fields = new JDBField[37];
		/* Contenitore Record DBF */
		Object[] mob_iscr_record = new Object[37];
		/* File da restituire */
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		/* Inizializza la connessione per il recupero Dati */
		try {
			tmpFile = File.createTempFile("~ISCR", "DBF");
			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();
			/* Dichiarazione campi DBF */
			mob_iscr_fields[0] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscr_fields[1] = new JDBField("COG_LA", 'C', 30, 0);
			mob_iscr_fields[2] = new JDBField("NOME_LA", 'C', 20, 0);
			mob_iscr_fields[3] = new JDBField("NAS_LA", 'D', 8, 0);
			mob_iscr_fields[4] = new JDBField("SESSO_LA", 'C', 1, 0);
			mob_iscr_fields[5] = new JDBField("CODNAS_LA", 'C', 4, 0);
			mob_iscr_fields[6] = new JDBField("CIT_LA", 'C', 3, 0);
			mob_iscr_fields[7] = new JDBField("STACIV_LA", 'C', 2, 0);
			mob_iscr_fields[8] = new JDBField("CODSED_LA", 'C', 4, 0);
			mob_iscr_fields[9] = new JDBField("INDSED_LA", 'C', 40, 0);
			mob_iscr_fields[10] = new JDBField("CAPSED_LA", 'C', 5, 0);
			mob_iscr_fields[11] = new JDBField("CODRES_LA", 'C', 4, 0);
			mob_iscr_fields[12] = new JDBField("INDIRIZZO", 'C', 40, 0);
			mob_iscr_fields[13] = new JDBField("CAP", 'C', 5, 0);
			mob_iscr_fields[14] = new JDBField("TEL", 'C', 15, 0);
			mob_iscr_fields[15] = new JDBField("TEL2", 'C', 15, 0);
			mob_iscr_fields[16] = new JDBField("CLASSE", 'C', 30, 0);
			mob_iscr_fields[17] = new JDBField("N_ARCHI", 'C', 6, 0);
			mob_iscr_fields[18] = new JDBField("FLAG", 'C', 1, 0);
			mob_iscr_fields[19] = new JDBField("CLA181", 'C', 4, 0);
			mob_iscr_fields[20] = new JDBField("DATA_REV", 'D', 8, 0);
			mob_iscr_fields[21] = new JDBField("DATA_181", 'D', 8, 0);
			mob_iscr_fields[22] = new JDBField("DATA_ELE", 'D', 8, 0);
			mob_iscr_fields[23] = new JDBField("L407", 'C', 1, 0);
			mob_iscr_fields[24] = new JDBField("LISTE", 'C', 5, 0);
			mob_iscr_fields[25] = new JDBField("VALISCR", 'C', 1, 0);
			mob_iscr_fields[26] = new JDBField("RISERVA", 'C', 1, 0);
			mob_iscr_fields[27] = new JDBField("CIRC_LA", 'C', 9, 0);
			mob_iscr_fields[28] = new JDBField("OBBSCO", 'C', 1, 0);
			mob_iscr_fields[29] = new JDBField("OBBSOG", 'C', 1, 0);
			mob_iscr_fields[30] = new JDBField("MAIL_LA", 'C', 50, 0);
			mob_iscr_fields[31] = new JDBField("ANZ56", 'C', 3, 0);
			mob_iscr_fields[32] = new JDBField("MOD56", 'C', 1, 0);
			mob_iscr_fields[33] = new JDBField("DISL68", 'C', 3, 0);
			mob_iscr_fields[34] = new JDBField("IS_TMOD", 'C', 1, 0);
			mob_iscr_fields[35] = new JDBField("IS_DMOD", 'D', 8, 0);
			mob_iscr_fields[36] = new JDBField("IS_HMOD", 'C', 8, 0);
			iscrWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscr_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_isc);

			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_isc_tutto);
			else
				rs = stmt.executeQuery(sql_isc);

			while (rs.next()) {
				mob_iscr_record[0] = rs.getString("CODFIS_LA");
				mob_iscr_record[1] = rs.getString("COG_LA");
				mob_iscr_record[2] = rs.getString("NOME_LA");
				mob_iscr_record[3] = mob_iscr_fields[3].parse(rs.getString("NAS_LA"));
				mob_iscr_record[4] = rs.getString("SESSO_LA");
				mob_iscr_record[5] = rs.getString("CODNAS_LA");
				mob_iscr_record[6] = rs.getString("CIT_LA");
				mob_iscr_record[7] = rs.getString("STACIV_LA");
				mob_iscr_record[8] = rs.getString("CODSED_LA");
				mob_iscr_record[9] = rs.getString("INDSED_LA");
				mob_iscr_record[10] = rs.getString("CAPSED_LA");
				mob_iscr_record[11] = rs.getString("CODRES_LA");
				mob_iscr_record[12] = rs.getString("INDIRIZZO");
				mob_iscr_record[13] = rs.getString("CAP");
				mob_iscr_record[14] = rs.getString("TEL");
				mob_iscr_record[15] = rs.getString("TEL2");
				mob_iscr_record[16] = rs.getString("CLASSE");
				mob_iscr_record[17] = rs.getString("N_ARCHI");
				mob_iscr_record[18] = rs.getString("FLAG");
				mob_iscr_record[19] = rs.getString("CLA181");
				if (rs.getString("DATA_REV") != null)
					mob_iscr_record[20] = mob_iscr_fields[20].parse(rs.getString("DATA_REV"));
				else
					mob_iscr_record[20] = rs.getString("DATA_REV");
				if (rs.getString("DATA_181") != null)
					mob_iscr_record[21] = mob_iscr_fields[21].parse(rs.getString("DATA_181"));
				else
					mob_iscr_record[21] = rs.getString("DATA_181");
				if (rs.getString("DATA_ELE") != null)
					mob_iscr_record[22] = mob_iscr_fields[22].parse(rs.getString("DATA_ELE"));
				else
					mob_iscr_record[22] = rs.getString("DATA_ELE");
				mob_iscr_record[23] = rs.getString("L407");
				mob_iscr_record[24] = rs.getString("LISTE");
				mob_iscr_record[25] = rs.getString("VALISCR");
				mob_iscr_record[26] = rs.getString("RISERVA");
				mob_iscr_record[27] = rs.getString("CIRC_LA");
				mob_iscr_record[28] = rs.getString("OBBSCO");
				mob_iscr_record[29] = rs.getString("OBBSOG");
				mob_iscr_record[30] = rs.getString("MAIL_LA");
				mob_iscr_record[31] = rs.getString("ANZ56");
				mob_iscr_record[32] = rs.getString("MOD56");
				mob_iscr_record[33] = rs.getString("DISL68");
				mob_iscr_record[34] = rs.getString("IS_TMOD");
				mob_iscr_record[35] = rs.getString("IS_DMOD");
				mob_iscr_record[36] = rs.getString("IS_HMOD");
				/* Aggiunge il record e cicla */
				try {
					iscrWriter.addRecord(mob_iscr_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscr():impossibile aggiungere questo record)", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscr():esportati " + iCount + " Record");

		} catch (EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore EMF", (Exception) ex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore scrittura File temporaneo",
					iex);

		} catch (SQLException sex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore SQL", sex);

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscr():errore nella addRecord(mob_iscr_record)", jex);

		} finally {
			/* Rilascio risorse JDBC e DBF */
			try {
				if (iscrWriter != null)
					iscrWriter.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (dataConn != null)
					dataConn.close();
			} catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():Errore rilascio Risorse:", ex);

			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():Errore chiusura connessione",
						(Exception) e);

			} catch (JDBFException je) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore chiusura file",
						(Exception) je);

			}
			list.add("esportato <b>MOBR.DBF</b>");
			list.add("------");
			return tmpFile;
		} // esporta_iscr()
	}

	private File esportaIscrei(MobilitaExpThread list) {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscrei_fields = new JDBField[18];
		/* Contenitore Record DBF */
		Object[] mob_iscrei_record = new Object[18];
		/* File da restituire */
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		/* Inizializza la connessione per il recupero Dati */
		try {
			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();
		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscrei():errore EMF", (Exception) e);

		}

		try {
			tmpFile = File.createTempFile("~ISCREI", "DBF");
			/* Dichiarazione campi DBF */
			int j = 0;
			mob_iscrei_fields[j++] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscrei_fields[j++] = new JDBField("DATA_REI", 'D', 8, 0);
			mob_iscrei_fields[j++] = new JDBField("TIPO_ISCR", 'C', 2, 0);
			mob_iscrei_fields[j++] = new JDBField("CLAS_ISCR", 'C', 4, 0);
			mob_iscrei_fields[j++] = new JDBField("C4", 'C', 6, 0);
			mob_iscrei_fields[j++] = new JDBField("DATAC4", 'D', 8, 0);
			mob_iscrei_fields[j++] = new JDBField("M_SOS", 'C', 3, 0);
			mob_iscrei_fields[j++] = new JDBField("REDDITO", 'C', 10, 0);
			mob_iscrei_fields[j++] = new JDBField("DIC_RED", 'C', 2, 0);
			mob_iscrei_fields[j++] = new JDBField("REDD_AUT", 'C', 10, 0);
			mob_iscrei_fields[j++] = new JDBField("CARICO", 'C', 2, 0);
			mob_iscrei_fields[j++] = new JDBField("CAR_ALTRO", 'C', 2, 0);
			mob_iscrei_fields[j++] = new JDBField("RINENTI", 'C', 4, 0);
			mob_iscrei_fields[j++] = new JDBField("RINDITTE", 'C', 4, 0);
			mob_iscrei_fields[j++] = new JDBField("NUCLEO", 'C', 1, 0);
			mob_iscrei_fields[j++] = new JDBField("DIFF", 'C', 1, 0);
			mob_iscrei_fields[j++] = new JDBField("PUNT_INV", 'C', 7, 0);
			mob_iscrei_fields[j++] = new JDBField("PUNT_TOT", 'C', 7, 0);

			iscReiWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscrei_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_iscrei);
			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_iscrei_tutto);
			else
				rs = stmt.executeQuery(sql_iscrei);
			while (rs.next()) {
				mob_iscrei_record[0] = rs.getString("CODFIS_LA");
				if (rs.getString("DATA_REI") != null)
					mob_iscrei_record[1] = mob_iscrei_fields[1].parse(rs.getString("DATA_REI"));
				else
					mob_iscrei_record[1] = rs.getString("DATA_REI");
				mob_iscrei_record[2] = rs.getString("TIPO_ISCR");
				mob_iscrei_record[3] = rs.getString("CLAS_ISCR");
				mob_iscrei_record[4] = rs.getString("C4");
				if (rs.getString("DATAC4") != null)
					mob_iscrei_record[5] = mob_iscrei_fields[5].parse(rs.getString("DATAC4"));
				else
					mob_iscrei_record[5] = rs.getString("DATAC4");
				mob_iscrei_record[6] = rs.getString("M_SOS");
				mob_iscrei_record[7] = rs.getString("REDDITO");
				mob_iscrei_record[8] = rs.getString("DIC_RED");
				mob_iscrei_record[9] = rs.getString("REDD_AUT");
				mob_iscrei_record[10] = rs.getString("CARICO");
				mob_iscrei_record[11] = rs.getString("CAR_ALTRO");
				mob_iscrei_record[12] = rs.getString("RINENTI");
				mob_iscrei_record[13] = rs.getString("RINDITTE");
				mob_iscrei_record[14] = rs.getString("NUCLEO");
				mob_iscrei_record[15] = rs.getString("DIFF");
				mob_iscrei_record[16] = rs.getString("PUNT_INV");
				mob_iscrei_record[17] = rs.getString("PUNT_TOT");
				/* Aggiunge il record e cicla */
				try {
					iscReiWriter.addRecord(mob_iscrei_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscrei():impossibile aggiungere questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscrei():esportati " + iCount + " Record");

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscrei():errore scrittura File temporaneo", (Exception) iex);

		} catch (SQLException sex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscrei():errore SQL", sex);

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscrei():errore nella addRecord(mob_iscr_record)", jex);

		} finally {
			try {
				if (iscReiWriter != null)
					iscReiWriter.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (dataConn != null)
					dataConn.close();
			} catch (JDBFException je) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscrei():errore chiusura file",
						(Exception) je);

			} catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscrei()Errore rilascio Risorse:",
						ex);

			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscrei()Errore chiusura connessione",
						(Exception) e);

			}
		}
		list.add("esportato <b>MOBREI.DBF</b>");
		list.add("------");
		return tmpFile;
	} // esporta_iscrei()

	private File esportaIscstu(MobilitaExpThread list) {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscstu_fields = new JDBField[4];
		/* Contenitore Record DBF */
		Object[] mob_iscstu_record = new Object[4];
		/* File da restituire */
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		/* Inizializza la connessione per il recupero Dati */
		try {
			tmpFile = File.createTempFile("~ISCSTU", "DBF");
			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();

			/* Dichiarazione campi DBF */
			mob_iscstu_fields[0] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscstu_fields[1] = new JDBField("TIT", 'C', 6, 0);
			mob_iscstu_fields[2] = new JDBField("VOTO", 'C', 3, 0);
			mob_iscstu_fields[3] = new JDBField("AN_TIT", 'C', 4, 0);

			iscStuWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscstu_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_iscstu);
			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_iscstu_tutto);
			else
				rs = stmt.executeQuery(sql_iscstu);
			while (rs.next()) {
				mob_iscstu_record[0] = rs.getString("CODFIS_LA");
				mob_iscstu_record[1] = rs.getString("TIT");
				mob_iscstu_record[2] = rs.getString("VOTO");
				mob_iscstu_record[3] = rs.getString("ANTIT");
				/* Aggiunge il record e cicla */
				try {
					iscStuWriter.addRecord(mob_iscstu_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscstu():impossibile esportare questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscstu():esportati " + iCount + " Record");

		} catch (EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscstu():errore EMF", (Exception) ex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscstu():errore scrittura File temporaneo", iex);

		} catch (SQLException sex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscstu():errore SQL", sex);

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscstu():errore nella addRecord(mob_iscr_record)", jex);

		} finally {
			try {
				if (iscStuWriter != null)
					iscStuWriter.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (dataConn != null)
					dataConn.close();
			} catch (JDBFException je) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore chiusura file",
						(Exception) je);

			} catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "Errore rilascio Risorse:", ex);

			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "Errore chiusura connessione", (Exception) e);

			}
		}
		return tmpFile;
	} // esporta_iscstu()

	/* Esportazione del file iscatt.DBF */
	private File esportaIscatt(MobilitaExpThread list) {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_iscatt_fields = new JDBField[13];
		/* Contenitore Record DBF */
		Object[] mob_iscatt_record = new Object[13];
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		/* Inizializza la connessione per il recupero Dati */
		try {
			tmpFile = File.createTempFile("~ISCATT", "DBF");
			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();
			/* Dichiarazione campi DBF */
			mob_iscatt_fields[0] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_iscatt_fields[1] = new JDBField("DATACC", 'D', 8, 0);
			mob_iscatt_fields[2] = new JDBField("NOTACC", 'C', 1, 0);
			mob_iscatt_fields[3] = new JDBField("DATSAN", 'D', 8, 0);
			mob_iscatt_fields[4] = new JDBField("DATCER", 'D', 8, 0);
			mob_iscatt_fields[5] = new JDBField("NUMCER", 'C', 6, 0);
			mob_iscatt_fields[6] = new JDBField("DATLIB", 'D', 8, 0);
			mob_iscatt_fields[7] = new JDBField("NUMLIB", 'C', 6, 0);
			mob_iscatt_fields[8] = new JDBField("EXTTIP", 'C', 1, 0);
			mob_iscatt_fields[9] = new JDBField("EXTDEL", 'D', 8, 0);
			mob_iscatt_fields[10] = new JDBField("DATSCA", 'D', 8, 0);
			mob_iscatt_fields[11] = new JDBField("TIPENT", 'C', 1, 0);
			mob_iscatt_fields[12] = new JDBField("EXTMOD", 'C', 1, 0);

			iscAttWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_iscatt_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_iscatt);
			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_iscatt_tutto);
			else
				rs = stmt.executeQuery(sql_iscatt);
			while (rs.next()) {
				mob_iscatt_record[0] = rs.getString("CODFIS_LA");
				if (rs.getString("DATACC") != null)
					mob_iscatt_record[1] = mob_iscatt_fields[1].parse(rs.getString("DATACC"));
				else
					mob_iscatt_record[1] = rs.getString("DATACC");
				mob_iscatt_record[2] = rs.getString("NOTACC");
				if (rs.getString("DATSAN") != null)
					mob_iscatt_record[3] = mob_iscatt_fields[3].parse(rs.getString("DATSAN"));
				else
					mob_iscatt_record[3] = rs.getString("DATSAN");
				if (rs.getString("DATCER") != null)
					mob_iscatt_record[4] = mob_iscatt_fields[4].parse(rs.getString("DATCER"));
				else
					mob_iscatt_record[4] = rs.getString("DATCER");
				mob_iscatt_record[5] = rs.getString("NUMCER");
				if (rs.getString("DATLIB") != null)
					mob_iscatt_record[6] = mob_iscatt_fields[6].parse(rs.getString("DATLIB"));
				else
					mob_iscatt_record[6] = rs.getString("DATLIB");
				mob_iscatt_record[7] = rs.getString("NUMLIB");
				mob_iscatt_record[8] = rs.getString("EXTTIP");
				if (rs.getString("EXTDEL") != null)
					mob_iscatt_record[9] = mob_iscatt_fields[9].parse(rs.getString("EXTDEL"));
				else
					mob_iscatt_record[9] = rs.getString("EXTDEL");
				if (rs.getString("DATSCA") != null)
					mob_iscatt_record[10] = mob_iscatt_fields[10].parse(rs.getString("DATSCA"));
				else
					mob_iscatt_record[10] = rs.getString("DATSCA");
				mob_iscatt_record[11] = rs.getString("TIPENT");
				mob_iscatt_record[12] = rs.getString("EXTMOD");
				/* Aggiunge il record e cicla */
				try {
					iscAttWriter.addRecord(mob_iscatt_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_iscatt():impossibile esportare questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_iscatt():esportati " + iCount + " Record");

		} catch (EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscatt():errore EMF", (Exception) ex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscatt():errore scrittura File temporaneo", iex);

		} catch (SQLException sex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscatt():errore SQL", sex);

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_iscatt():errore nella addRecord(mob_iscatt_record)", jex);

		} finally {
			try {
				iscAttWriter.close();
			} catch (JDBFException je) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscatt():errore chiusura file",
						(Exception) je);

			}
		}
		/* Chiusura connessione JDBC */
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
			if (dataConn != null)
				dataConn.close();
			// close DBF
		} catch (SQLException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscatt():Errore rilascio Risorse:", ex);

		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscatt():Errore chiusura connessione",
					(Exception) e);

		}
		list.add("esportato <b>MOBSTU.DBF</b>");
		list.add("------");
		return tmpFile;
	} // esporta_iscatt()

	private File esportaIscan(MobilitaExpThread list) {
		/*
		 * Campi delle tabelle DBF 'N' - numeric; 'C' - character; 'L' - logical; 'D' - date; 'F' - floating point.
		 */
		JDBField[] mob_isccan_fields = new JDBField[8];
		/* Contenitore Record DBF */
		Object[] mob_isccan_record = new Object[8];
		File tmpFile = null;
		/* Contatori record aggiunt/saltati */
		int iCount = 0;
		int iJump = 0;
		/* Inizializza la connessione per il recupero Dati */
		try {
			tmpFile = File.createTempFile("~ISCCAN", "DBF");
			dataConn = DataConnectionManager.getInstance().getConnection();
			con = dataConn.getInternalConnection();
			/* Dichiarazione campi DBF */
			int j = 0;
			mob_isccan_fields[j++] = new JDBField("CODFIS_LA", 'C', 16, 0);
			mob_isccan_fields[j++] = new JDBField("DATA_CANC", 'D', 8, 0);
			mob_isccan_fields[j++] = new JDBField("MOTIVO", 'C', 2, 0);
			mob_isccan_fields[j++] = new JDBField("NOTE_CANC", 'C', 21, 0);
			mob_isccan_fields[j++] = new JDBField("DATA_RIC", 'D', 8, 0);
			mob_isccan_fields[j++] = new JDBField("PROT_RIC", 'C', 7, 0);
			mob_isccan_fields[j++] = new JDBField("DATA_REV", 'D', 8, 0);
			mob_isccan_fields[j++] = new JDBField("MOTI_REV", 'C', 2, 0);
			/* Istanzio Writer DBF e la connessione */
			iscCanWriter = new DBFWriter(tmpFile.getAbsolutePath(), mob_isccan_fields);
			// dataConn = DataConnectionManager.getInstance().getConnection();
			// con = dataConn.getInternalConnection();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// rs = stmt.executeQuery(sql_isccan);
			/* Gestione particolarità esportaz. "anche già inviati" */
			if (ancheinviati.compareToIgnoreCase("Y") == 0)
				rs = stmt.executeQuery(sql_isccan_tutto);
			else
				rs = stmt.executeQuery(sql_isccan);
			while (rs.next()) {
				mob_isccan_record[0] = rs.getString("CODFIS_LA");
				if (rs.getString("DATA_CANC") != null)
					mob_isccan_record[1] = mob_isccan_fields[1].parse(rs.getString("DATA_CANC"));
				else
					mob_isccan_record[1] = rs.getString("DATA_CANC");
				mob_isccan_record[2] = rs.getString("MOTIVO");
				mob_isccan_record[3] = rs.getString("NOTE_CANC");
				if (rs.getString("DATA_RIC") != null)
					mob_isccan_record[4] = mob_isccan_fields[4].parse(rs.getString("DATA_RIC"));
				else
					mob_isccan_record[4] = rs.getString("DATA_RIC");
				mob_isccan_record[5] = rs.getString("PROT_RIC");
				if (rs.getString("DATA_REV") != null)
					mob_isccan_record[6] = mob_isccan_fields[6].parse(rs.getString("DATA_REV"));
				else
					mob_isccan_record[6] = rs.getString("DATA_REV");
				mob_isccan_record[7] = rs.getString("MOTI_REV");
				/* Aggiunge il record e cicla */
				try {
					iscCanWriter.addRecord(mob_isccan_record);
				} catch (JDBFException jex) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							className + ":esporta_isccan():impossibile aggingere questo record", jex);

					iJump++;
					continue;
				}
				iCount++;
			}
			_logger.debug(className + ":esporta_isccan():esportati " + iCount + " Record");

		} catch (EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_isccan():errore EMF", (Exception) ex);

		} catch (IOException iex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_isccan():errore scrittura File temporaneo", iex);

		} catch (SQLException sex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_isccan():errore SQL", sex);

		} catch (JDBFException jex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + ":esporta_isccan():errore nella addRecord(mob_iscr_record)", jex);

			/* Chiusura connessione JDBC */
		} finally {
			try {
				if (iscCanWriter != null)
					iscCanWriter.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (dataConn != null)
					dataConn.close();
			} catch (JDBFException je) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + ":esporta_iscr():errore chiusura file",
						(Exception) je);

			} catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "Errore rilascio Risorse:", ex);

			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "Errore chiusura connessione", (Exception) e);

			}
		}
		list.add("esportato <b>MOBCAN.DBF</b>");
		list.add("------");
		return tmpFile;
	} // esporta_isccan()

	/**
	 * @return
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	public MobilitaEsportatore(String ancheinviati) {
		super();
		this.ancheinviati = ancheinviati;
	}

}

/* Filtro per l'inclusione dei files nello zip. Sono compresi anche alcuni files vuoti di Prolabor */
class DBFFileFilter implements java.io.FileFilter {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaEsportatore.class.getName());

	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		String name = f.getName().toLowerCase();
		return name.endsWith("dbf") || name.endsWith("DBF") || name.endsWith("dbt") || name.endsWith("DBT")
				|| name.endsWith("sci") || name.endsWith("SCI");
	} // end accept
} // end class HTMLFileFilter