/**
 * DBLoad.java
 *
 * Created on 06 ottobre 2004
 */

package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;

/**
 *
 * @author Giovanni Landi
 */
public class DBLoad {
	/**
	 * utilizza la nuova query nel file selezione cat 181.txt
	 * 
	 * @param SourceBean
	 *            movimento e' la riga estratta dal db contenente un movimento del lavoratore da cui estrai il
	 *            cdnLavoratore necessario per la ricerca delle info necessarie per il calcolo della categoria di
	 *            appartenenza del lavoratore
	 */
	public static SourceBean getCat181(SourceBean movimento) throws Exception {
		SourceBean row = null;
		// riprendi i servicecontainer e requestcontainer
		// crea la servicerequest e la serviceresponse
		// setta la servicerequest
		// istanzia il modulo di ricerca
		// service(request, response);
		// riprendi il SourceBean dei dati dalla response
		if (DatiDiTest.TEST) {
			row = DatiDiTest.cat181;
		} else {
			Object o = movimento.getAttribute("CDNLAVORATORE");
			BigDecimal cdnLav = null;
			if (o instanceof String)
				cdnLav = new BigDecimal((String) o);
			else
				cdnLav = (BigDecimal) o;
			Object[] params = new Object[] { cdnLav, cdnLav };
			row = (SourceBean) QueryExecutor.executeQuery("GET_CAT_181", params, "SELECT", Values.DB_SIL_DATI);
		}
		return getRowAttribute(row);
	}

	/**
	 * Legge dal db i movimenti di un lavoratore a cavallo della data della did
	 */
	public static Vector getMovimentiDID(String dateDID, String dateFineAnno, Object cdnLavoratore) throws Exception {
		Object params[] = new Object[5];
		params[0] = cdnLavoratore;
		params[1] = dateDID;
		params[2] = dateDID;
		params[3] = dateDID;
		params[4] = dateFineAnno;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_ANNO_DID", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		} else {
			return DatiDiTest.movimenti;
		}

	}

	/**
	 * Legge dal db i movimenti di un lavoratore terminati in una certa data Utilizzata nel batch delle cessazioni a
	 * tempo determinato
	 */
	public static Vector getMovimentiTerminati(String dataFineMov) throws Exception {
		Object params[] = new Object[1];
		params[0] = dataFineMov;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TERMINATI", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere i movimenti terminati ");
		} else {
			return DatiDiTest.movimenti;
		}
	}

	/**
	 * Legge dal db i movimenti di un lavoratore a cavallo di un anno e considera solo i movimenti dei lavoratori che al
	 * giorno precedente l'inizio del nuovo anno sono disoccupati
	 */
	public static Vector getMovimentiTraAnniNew(String dataRif, String cdnGruppo) throws Exception {
		SourceBean res = null;
		Object params[] = new Object[5];
		params[0] = dataRif;
		params[1] = dataRif;
		params[2] = dataRif;
		params[3] = dataRif;
		params[4] = cdnGruppo;
		res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TRA_ANNI_NEW", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti");
	}

	/**
	 * Legge dal db i movimenti di un lavoratore a cavallo di un anno e considera solo i movimenti dei lavoratori che
	 * non hanno uno stato occupazionale nel nuovo anno
	 */
	public static Vector getMovimentiTraAnni(String dataRif, String flagImpattiTraAnniSoloDis, String cdnGruppo)
			throws Exception {
		SourceBean res = null;
		Object params[] = new Object[3];
		params[0] = dataRif;
		params[1] = dataRif;
		params[2] = cdnGruppo;
		if (!flagImpattiTraAnniSoloDis.equalsIgnoreCase("S")) {
			res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TRA_ANNI", params, "SELECT",
					Values.DB_SIL_DATI);
		} else {
			res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TRA_ANNI_SOLO_DISOC", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti");
	}

	/**
	 * Legge dal db i movimenti di un lavoratore a cavallo di un anno
	 */
	public static Vector getMovimentiTraAnniSenzaSOcc(String dataRif, String flagImpattiTraAnniSoloDis,
			String cdnGruppo) throws Exception {
		SourceBean res = null;
		Object params[] = new Object[4];
		params[0] = dataRif;
		params[1] = dataRif;
		params[2] = dataRif;
		params[3] = cdnGruppo;

		if (!flagImpattiTraAnniSoloDis.equalsIgnoreCase("S")) {
			res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TRA_ANNI_SENZA_SOCC", params, "SELECT",
					Values.DB_SIL_DATI);
		} else {
			res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TRA_ANNI_SOLO_DISOC_SENZA_SOCC", params,
					"SELECT", Values.DB_SIL_DATI);
		}
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti");
	}

	/**
	 * Legge dal db i dati relativi all'utente loggato Utilizzata nel batch delle cessazioni a tempo determinato e
	 * movimenti futuri
	 */
	public static Vector getUser(String user, String profilo, String cdnGruppo) throws Exception {
		Object params[] = new Object[3];
		params[0] = user;
		params[1] = profilo;
		params[2] = cdnGruppo;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_USER_BATCH", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile ricavare l'utente");
		} else {
			return DatiDiTest.movimenti;
		}
	}

	/**
	 * Legge dal db i movimenti di un lavoratore in un range di date. Utilizzata nel batch delle cessazioni a tempo
	 * determinato
	 */
	public static Vector getMovimentiTerminatiRange(String dataFineMov1, String dataFineMov2) throws Exception {
		Object params[] = new Object[4];
		params[0] = dataFineMov1;
		params[1] = dataFineMov2;
		params[2] = dataFineMov1;
		params[3] = dataFineMov2;

		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_TERMINATI_RANGE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti terminati ");
	}

	/**
	 * Recupera dal DB tutti i movimenti che devono essere processati dal batch che si occupa degli impatti su moviemnti
	 * con data inizio futura.
	 */
	public static Vector getMovimentiFuturi(String dataInizioMov) throws Exception {
		Object params[] = new Object[2];
		params[0] = dataInizioMov;
		params[1] = dataInizioMov;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_FUTURI", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere i movimenti terminati ");
		} else {
			return DatiDiTest.movimenti;
		}
	}

	/**
	 * 
	 * @param dataInizioAnno
	 *            data riferimento
	 * @param dataFineAnno
	 *            uguale al 31/12/anno di riferimento
	 * @param cdnLavoratore
	 * @param txExecutor
	 * @return Legge dal db i movimenti di un lavoratore che hanno data inizio minore o uguale dataFineAnno e data fine
	 *         maggiore o uguale dataInizioAnno
	 * @throws Exception
	 */
	public static Vector getMovimentiAnno(String dataInizioAnno, String dataFineAnno, Object cdnLavoratore,
			TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = new Object[4];
		params[0] = cdnLavoratore;
		params[1] = dataFineAnno;
		params[2] = dataInizioAnno;
		params[3] = dataInizioAnno;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) txExecutor.executeQuery("GET_MOVIMENTI_ANNO", params, "SELECT");
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		} else {
			return DatiDiTest.movimenti;
		}
	}

	/**
	 * 
	 * @param dataInizioAnno
	 *            data riferimento
	 * @param dataFineAnno
	 *            uguale al 31/12/anno di riferimento
	 * @param cdnLavoratore
	 * @return Legge dal db i movimenti di un lavoratore che hanno data inizio minore o uguale dataFineAnno e data fine
	 *         maggiore o uguale dataInizioAnno
	 * @throws Exception
	 */
	public static Vector getMovimentiAnno(String dataInizioAnno, String dataFineAnno, Object cdnLavoratore)
			throws Exception {
		Object params[] = new Object[4];
		params[0] = cdnLavoratore;
		params[1] = dataFineAnno;
		params[2] = dataInizioAnno;
		params[3] = dataInizioAnno;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_ANNO", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		} else {
			return DatiDiTest.movimenti;
		}
	}

	public static Vector getMovimentiProroghe(Object prgMovimentoPrec) throws Exception {
		Vector v = new Vector();
		SourceBean sb = null;
		while (prgMovimentoPrec != null) {
			sb = DBLoad.getMovimento(prgMovimentoPrec);
			sb = getRowAttribute(sb);
			v.insertElementAt(sb, 0);
			if (MovimentoBean.getTipoMovimento(sb) == MovimentoBean.PROROGA
					|| MovimentoBean.getTipoMovimento(sb) == MovimentoBean.TRASFORMAZIONE)
				prgMovimentoPrec = sb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
			else
				prgMovimentoPrec = null;
		}
		return v;
	}

	/**
	 * VIENE CHIAMATO SOLO DALLE PROROGHE
	 * 
	 * @param prgMovimentoPrec
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static Vector getMovimentiProroghe(Object prgMovimentoPrec, TransactionQueryExecutor txExecutor)
			throws Exception {
		Vector v = new Vector();
		SourceBean sb = null;
		while (prgMovimentoPrec != null) {
			sb = DBLoad.getMovimento(prgMovimentoPrec, txExecutor);
			sb = getRowAttribute(sb);
			v.insertElementAt(sb, 0);
			if (MovimentoBean.getTipoMovimento(sb) == MovimentoBean.PROROGA)
				prgMovimentoPrec = sb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
			else
				prgMovimentoPrec = null;
		}
		return v;
	}

	/**
	 * Legge tutti i movimenti aperti di un lavoratore a partire da una certa data
	 */
	public static Vector getMovimentiApertiDa(String data, Object cdnLavoratore) throws Exception {
		Object params[] = new Object[3];
		params[0] = cdnLavoratore;
		params[1] = data;
		params[2] = data;
		Vector movimenti = null;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_APERTI_DA", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				movimenti = res.getAttributeAsVector("ROW");
		} else {
			movimenti = DatiDiTest.movimenti;
		}
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	/**
	 * Legge dal db i movimenti di un lavoratore aperti in una certa data
	 */
	public static Vector getMovimentiApertiAnno(String data, Object cdnLavoratore) throws Exception {
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = data;
		Vector movimenti = null;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_APERTI_IN_DATA", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				movimenti = res.getAttributeAsVector("ROW");
		} else {
			movimenti = DatiDiTest.movimenti;
		}
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	/**
	 * Legge dal db i movimenti di un lavoratore a partire da una data
	 */
	public static Vector getMovimentiDa(String data, Object cdnLavoratore) throws Exception {
		Object params[] = new Object[3];
		params[0] = cdnLavoratore;
		params[1] = data;
		params[2] = data;
		Vector movimenti = null;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_DA", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				movimenti = res.getAttributeAsVector("ROW");
		} else {
			movimenti = DatiDiTest.movimenti;
		}
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	public static Vector getMovimentiLavoratore(Object cdnLavoratore, TransactionQueryExecutor tx) throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		Vector movimenti = null;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) tx.executeQuery("GET_TUTTI_MOVIMENTI_LAVORATORE", params, "SELECT");
			if (res != null)
				movimenti = res.getAttributeAsVector("ROW");
		} else {
			movimenti = DatiDiTest.movimenti;
		}
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	public static Vector getMovimentiLavoratoreProtocollati(Object cdnLavoratore, TransactionQueryExecutor tx)
			throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		Vector movimenti = null;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) tx.executeQuery("GET_AMM_MOVIMENTI_PROTOCOLLATI_LAVORATORE", params,
					"SELECT");
			if (res != null)
				movimenti = res.getAttributeAsVector("ROW");
		} else {
			movimenti = DatiDiTest.movimenti;
		}
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	public static SourceBean getUltimoMovimento(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("GET_ULTIMO_MOV", params, "SELECT");
		return getRowAttribute(row);
	}

	/**
	 * Legge dal db lo stato occupazionale con la query "GET_STATO_OCCUPAZ_APERTO"
	 */
	public static StatoOccupazionaleBean getStatoOccupazionale(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[] { cdnLavoratore };
		SourceBean row = null;
		if (!DatiDiTest.TEST)
			row = (SourceBean) transExec.executeQuery("GET_STATO_OCCUPAZ_APERTO", params, "SELECT");
		else {
			row = DatiDiTest.statoOccupazionale;
		}
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale");
		// crea un oggetto StatoOccupazionaleBean
		StatoOccupazionaleBean so = new StatoOccupazionaleBean(row);
		return so;
	}

	/**
	 * Recupero lo stato Occupazionale aperto del lavoratore, senza il transactionQueryExecutor
	 * 
	 * @param codStatoOccupaz
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean getStatoOccupazionale(Object cdnLavoratore) throws Exception {
		Object params[] = new Object[] { cdnLavoratore };
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("GET_STATO_OCCUPAZ_APERTO", params, "SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale");
		// crea un oggetto StatoOccupazionaleBean
		StatoOccupazionaleBean so = new StatoOccupazionaleBean(row);
		return so;
	}

	/**
	 * @param codice
	 *            dello stato occupazionale
	 * @return la lista dei lavoratori con quel codStatoOccupaz Classe per caricare tutti i lavoratori che hanno un
	 *         determinato codice stato occupazionale. Viene utilizzato per il batch dei tirocini senza contratto
	 */
	public static ArrayList getLavoratoriDaCodStatoOccupaz(String codStatoOccupaz) throws Exception {
		ArrayList lavoratori;
		Object params[] = new Object[1];
		params[0] = codStatoOccupaz;
		Vector rows = null;
		SourceBean row = null;

		row = (SourceBean) QueryExecutor.executeQuery("GET_LAVORATORI_DA_CODSTATOOCCUPAZ", params, "SELECT",
				Values.DB_SIL_DATI);
		rows = row.getAttributeAsVector("ROW");
		if ((rows != null) && !rows.isEmpty()) {
			lavoratori = new ArrayList(rows.size());
			for (int i = 0; i < rows.size(); i++) {
				row = (SourceBean) rows.get(i);
				lavoratori.add(i, row);
			}
		} else
			lavoratori = new ArrayList(0);
		return lavoratori;
	}

	public static SourceBean getStatoOccupazionaleSpecifico(Object prgStatoOccupazionale,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[] { prgStatoOccupazionale };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_STATO_OCCUPAZIONALE_PER_PRG", params, "SELECT");
		return getRowAttribute(row);
	}

	/**
	 * Legge dal db lo stato occupazionale con la query "GET_STATO_OCC_PRECEDENTE"
	 */
	public static StatoOccupazionaleBean getStatoOccupazionalePrecedente(StatoOccupazionaleBean statoOcc,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[2];
		params[0] = statoOcc.getCdnLavoratore();
		params[1] = statoOcc.getProgressivoDB();
		SourceBean row = null;
		if (!DatiDiTest.TEST)
			row = (SourceBean) transExec.executeQuery("GET_STATO_OCC_PRECEDENTE", params, "SELECT");
		else {
			row = DatiDiTest.statoOccupazionale;
		}
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale precedente");
		// crea un oggetto StatoOccupazionaleBean
		StatoOccupazionaleBean so = new StatoOccupazionaleBean(row);
		return so;
	}

	public static StatoOccupazionaleBean getStatoOccupazionaleUltimo(SourceBean mov, TransactionQueryExecutor transExec)
			throws Exception {
		Object cdnLavoratore = mov.getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		return getStatoOccupazionaleUltimo(cdnLavoratore, dataInizio, transExec);
	}

	public static StatoOccupazionaleBean getStatoOccupazionaleUltimo(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = dataInizio;
		SourceBean row = null;
		if (!DatiDiTest.TEST)
			row = (SourceBean) transExec.executeQuery("GET_STATO_OCC_PRECEDENTE_DATA", params, "SELECT");
		else {
			row = DatiDiTest.statoOccupazionaleUltimo;
		}
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale");
		// crea un oggetto StatoOccupazionaleBean
		if (row.getAttribute("row.prgStatoOccupaz") == null)
			return null;
		StatoOccupazionaleBean so = new StatoOccupazionaleBean(row);
		return so;
	}

	/**
	 * Legge dal db lo stato occupazionale con la query "GET_STATI_OCCUPAZIONALI2" legge tutti gli stati occupazionali
	 * di un lavoratore a partire da una data e del tipo 'D4'
	 */
	public static Vector getStatiOccupazionali(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = dataInizio;
		if (!DatiDiTest.TEST)
			row = (SourceBean) transExec.executeQuery("GET_STATI_OCCUPAZIONALI", params, "SELECT");
		else {
			row = DatiDiTest.statiOccupazionali;
		}
		if (row == null)
			throw new Exception("impossibile estrarre gli stati occupazionali");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getStatiOccupazionali(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = "01/01/1900";
		if (!DatiDiTest.TEST)
			row = (SourceBean) transExec.executeQuery("GET_STATI_OCCUPAZIONALI", params, "SELECT");
		else {
			row = DatiDiTest.statiOccupazionali;
		}
		if (row == null)
			throw new Exception("impossibile estrarre gli stati occupazionali");
		return row.getAttributeAsVector("ROW");
	}

	/**
	 * Estrae il primo stato occupazionale decaduto (C11, C12, C13, C14) con data inizio compresa tra dataInizio e
	 * dataFine; tra i campi ci sara' il NUMMESIBLOCCO per poter calcolare se una did puo' essere nuovamente stipulata.
	 * 
	 * @return
	 */
	public static SourceBean getStatoOccupazionaleDecadutoDa(Object cdnLavoratore, String dataInizio, String dataFine,
			TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object params[] = new Object[3];
		params[0] = cdnLavoratore;
		params[1] = dataInizio;
		params[2] = dataFine;
		row = (SourceBean) transExec.executeQuery("GET_STATO_OCCUPAZIONALE_DECADUTO_DA", params, "SELECT");

		if (row == null)
			throw new Exception("impossibile estrarre gli stati occupazionali");
		return getRowAttribute(row);
	}

	/**
	 * Legge dal db l'eventuale record della lista speciale a cui il lavoratore e' iscritto. Nome dello statement
	 * "AMSTR_GET_AM_CM_ISCR_IMPATTI"
	 */
	public static SourceBean getCollocamentoMirato(SourceBean movimento, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[2];
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
		params[0] = encryptKey;
		params[1] = movimento.getAttribute("CDNLAVORATORE");
		SourceBean row = null;
		Vector vettCM = null;
		if (DatiDiTest.TEST) {
			row = DatiDiTest.cm;
		} else {
			if (transExec != null) {
				row = (SourceBean) transExec.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT");
			} else {
				row = (SourceBean) QueryExecutor.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT",
						"SIL_DATI");
			}
			vettCM = row.getAttributeAsVector("ROW");
			if (vettCM.size() > 1) {
				// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
				row = (SourceBean) vettCM.get(0);
			}
		}
		return getRowAttribute(row);
	}

	public static Vector getAllDisabiliCollocamentoMirato(SourceBean movimento, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[2];
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
		params[0] = encryptKey;
		params[1] = movimento.getAttribute("CDNLAVORATORE");
		SourceBean row = null;
		Vector vettCM = null;
		if (DatiDiTest.TEST) {
			row = DatiDiTest.cm;
		} else {
			if (transExec != null) {
				row = (SourceBean) transExec.executeQuery("AMSTR_GET_ALL_AM_CM_ISCR_DISABILI_IMPATTI", params,
						"SELECT");
			} else {
				row = (SourceBean) QueryExecutor.executeQuery("AMSTR_GET_ALL_AM_CM_ISCR_DISABILI_IMPATTI", params,
						"SELECT", "SIL_DATI");
			}
			vettCM = row.getAttributeAsVector("ROW");
		}
		return vettCM;
	}

	/**
	 * Recupera il collocamento mirato
	 * 
	 * @param cdnlavoratore
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static SourceBean getCollocamentoMirato(Object cdnlavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[2];
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
		params[0] = encryptKey;
		params[1] = cdnlavoratore;
		SourceBean row = null;
		Vector vettCM = null;
		if (DatiDiTest.TEST) {
			row = DatiDiTest.cm;
		} else {
			if (transExec != null) {
				row = (SourceBean) transExec.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT");
			} else {
				row = (SourceBean) QueryExecutor.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT",
						"SIL_DATI");
			}
			vettCM = row.getAttributeAsVector("ROW");
			if (vettCM.size() > 1) {
				// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
				row = (SourceBean) vettCM.get(0);
			}
		}
		return getRowAttribute(row);
	}

	/**
	 * Recupera le iscrizioni disabili del lavoratore
	 * 
	 * @param cdnlavoratore
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static Vector getAllDisabiliCollocamentoMirato(Object cdnlavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[2];
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
		params[0] = encryptKey;
		params[1] = cdnlavoratore;
		SourceBean row = null;
		Vector vettCM = null;
		if (DatiDiTest.TEST) {
			row = DatiDiTest.cm;
		} else {
			if (transExec != null) {
				row = (SourceBean) transExec.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT");
			} else {
				row = (SourceBean) QueryExecutor.executeQuery("AMSTR_GET_AM_CM_ISCR_IMPATTI", params, "SELECT",
						"SIL_DATI");
			}
			vettCM = row.getAttributeAsVector("ROW");
		}
		return vettCM;
		// if (vettCM.size() > 1) {
		// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
		// row = (SourceBean) vettCM.get(0);
		// }

		// return getRowAttribute(row);
	}

	/**
	 * Legge dal db l'eventuale record della Dichiarazione di Immediata Disponibilita' stipulata dal lavoratore e
	 * protocollata. Nome dello statement "GET_DID_VALIDA"
	 */
	public static SourceBean getDID(SourceBean movimento, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = movimento.getAttribute("CDNLAVORATORE");
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.did;
		else
			row = (SourceBean) transExec.executeQuery("GET_DID_VALIDA", params, "SELECT");
		return getRowAttribute(row);
	}

	public static SourceBean getDID(Object prgDichDisponibilita, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgDichDisponibilita;
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.did;
		else
			row = (SourceBean) transExec.executeQuery("GET_DID_PER_PRG", params, "SELECT");
		return getRowAttribute(row);
	}

	public static Vector getUltimaDIDStoricizzata(SourceBean request, String dataRif,
			TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = request.getAttribute("CDNLAVORATORE");
		params[1] = dataRif;
		if (DatiDiTest.TEST)
			row = DatiDiTest.ultimaDIDStoricizzata;
		else
			row = (SourceBean) transExec.executeQuery("GET_ULTIME_DID_STORICIZZATE", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere l'ultima dichiarazione di immediata disponibilita' storicizzata");
		return row.getAttributeAsVector("ROW");
	}

	public static SourceBean getDeStatoOccupaz(SourceBean request, String cod, TransactionQueryExecutor transExec)
			throws Exception {
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = cod;
		if (DatiDiTest.TEST)
			row = DatiDiTest.deStatoOccupaz;
		else
			row = (SourceBean) transExec.executeQuery("GET_DE_STATO_OCCUPAZ", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere la tabella de_stato_occupaz");
		return getRowAttribute(row);
	}

	/**
	 * Legge dal db i limiti del reddito per l' anno in corso
	 */
	public static SourceBean getLimiteReddito() throws Exception {
		String dataOdierna = DateUtils.getNow();
		String anno = dataOdierna.substring(dataOdierna.lastIndexOf("/") + 1);
		Object params[] = new Object[] { anno };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_LIMITE_REDDITO", params, "SELECT", "SIL_DATI");
		return getRowAttribute(row);
	}

	public static SourceBean getLimiteReddito(String date) throws Exception {
		String anno = date.substring(date.lastIndexOf("/") + 1);
		Object params[] = new Object[] { anno };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_LIMITE_REDDITO", params, "SELECT", "SIL_DATI");
		return getRowAttribute(row);
	}

	public static SourceBean getMansioneConEsperienza(Object cdnLavoratore, TransactionQueryExecutor txExecutor)
			throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = (SourceBean) txExecutor.executeQuery("GET_MANSIONE_CON_ESPERIENZA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le mansione del lavoraotore");
		return getRowAttribute(row);
	}

	public static Vector getPattiStoricizzati(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_PATTO_INF_STORICHE_297", params, "SELECT");
		return row.getAttributeAsVector("ROW");
	}

	public static SourceBean getUltimoPattoStoricizzato(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_ULTIMO_PATTO_STORICIZZATO", params, "SELECT");
		return getRowAttribute(row);
	}

	public static Vector getPattiStoricizzati(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { cdnLavoratore, dataInizio };
		SourceBean row = null;
		if (DatiDiTest.TEST) {
			row = DatiDiTest.pattiStoricizzati;
		} else {
			row = (SourceBean) txExecutor.executeQuery("GET_PATTI_LAV_DA", params, "SELECT");
		}
		if (row == null)
			throw new Exception("impossibile leggere i patti del lavoraotore");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getAccordiGenerici(Object cdnLavoratore, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_ACCORDI_GENERICI_NON_297", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere gli accordi generici del lavoraotore");
		return row.getAttributeAsVector("ROW");
	}

	/**
	 * Questo metodo recupera TUTTE LE DID DEL LAVORATORE (NON SOLO QUELLE NON PROTOCOLLATE) E' SBAGLIATO IL NOME DELLA
	 * FUNZIONE
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static Vector getDichiarazioniDisponibilitaNonProtocollate(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { cdnLavoratore, dataInizio };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.dichiarazioniDisponibilita;
		else
			row = (SourceBean) txExecutor.executeQuery("GET_DID_LAVORATORE_NON_PROTOCOLLATE_DA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le dichiarazioni di immediata disponibilita' del lavoraotore");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getDichiarazioniDisponibilitaProtocollate(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { cdnLavoratore, dataInizio };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.dichiarazioniDisponibilita;
		else
			row = (SourceBean) txExecutor.executeQuery("GET_EVENTI_DID_PROTOCOLLATE_LAVORATORE", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le dichiarazioni di immediata disponibilita' del lavoraotore");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getDichiarazioniDisponibilita(Object cdnLavoratore, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { cdnLavoratore, dataInizio };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.dichiarazioniDisponibilita;
		else
			row = (SourceBean) txExecutor.executeQuery("GET_DID_LAVORATORE_DA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le dichiarazioni di immediata disponibilita' del lavoraotore");
		return row.getAttributeAsVector("ROW");
	}

	public static SourceBean getPattoAperto(Object cdnLavoratore, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.patto;
		else
			row = (SourceBean) transExec.executeQuery("GET_ULTIMOPATTOLAV", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il patto aperto");
		return getRowAttribute(row);
	}

	public static SourceBean getMovimento(Object prg, TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { prg };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.movimento;
		else
			row = (SourceBean) txExecutor.executeQuery("GET_MOVIMENTO", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il movimento");
		return getRowAttribute(row);
	}

	public static SourceBean getMovimento(Object prg) throws Exception {
		Object params[] = { prg };
		SourceBean row = null;
		if (DatiDiTest.TEST)
			row = DatiDiTest.movimento;
		else
			row = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTO", params, "SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere il movimento");
		return getRowAttribute(row);
	}

	public static SourceBean getMovimentoCollegato(Object prg, TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { prg, prg, prg, prg, prg, prg };
		SourceBean row = (SourceBean) txExecutor.executeQuery("GET_PRG_ULTIMO_MOV", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il movimento");
		row = getMovimento((BigDecimal) row.getAttribute("row." + MovimentoBean.DB_PRG_MOVIMENTO), txExecutor);
		if (row == null)
			throw new Exception("impossibile leggere il movimento");
		return getRowAttribute(row);
	}

	public static SourceBean getStatoOccAssociatoAlMotivoFineAtto(String codMotivoFineAtto, String codLstTab,
			String dataRif) throws Exception {
		Object params[] = { codMotivoFineAtto, codLstTab, dataRif, dataRif };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_STATO_OCC_ASSOCIATO_AL_MOTIVO_FINE_ATTO", params,
				"SELECT", Values.DB_SIL_DATI);
		return getRowAttribute(row);
	}

	/**
	 * restituisce il source bean degli attributi togliendo il nodo ROW
	 * 
	 * @param row
	 * @return
	 */
	public static SourceBean getRowAttribute(SourceBean row) {
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	/**
	 * Recupera tutti i tirocini di un dato lavoratore
	 * 
	 * @return Vector dei SourceBean estratti
	 * @param cdnLavoratore
	 */
	public static Vector getTirociniDaLavoratore(BigDecimal cdnLavoratore) throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		Vector rows = null;
		SourceBean tmp = null;
		tmp = (SourceBean) QueryExecutor.executeQuery("GET_TIROCINI_DA_LAV", params, "SELECT", Values.DB_SIL_DATI);
		rows = tmp.getAttributeAsVector("ROW");
		return rows;
	}

	/**
	 * Recupera il progressivo nell an_lavStoriaInf associato al lavoratore con datFine null
	 * 
	 * @return SourceBean contenete il progressivo prgLavStoriaInf aperto
	 * @param cdnLavoratore
	 */
	public static SourceBean getLavStoriaInfAperta(BigDecimal cdnLavoratore) throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("GET_AN_LAV_STORIA_INF", params, "SELECT", Values.DB_SIL_DATI);
		return getRowAttribute(row);
	}

	/**
	 * Recupera tutti i patti associati ad uno stato occupazionale
	 * 
	 * @param prg
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static Vector getPattoAssociatoStatoOccupaz(Object prgStatoOccupaz, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = { prgStatoOccupaz };
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("GET_PATTOSTATOOCCUPAZ", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere i patti associati allo stato occupazionale");
		return row.getAttributeAsVector("ROW");
	}

	/**
	 * Recupera tutti i patti associati ad un lavoratore
	 * 
	 * @param cdnLavoratore
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static Vector getPattiAssociatiLav(Object cdnLavoratore, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = { cdnLavoratore };
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("GET_TUTTI_PATTI_LAV", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere i patti associati al lavoratore");
		return row.getAttributeAsVector("ROW");
	}

	/**
	 * Recupera dalla ts_generale se fare o meno la commit del batch e se far scattare gli impatti (utilizzata PER I
	 * TEST), e se segnalare o meno le info sul movimento
	 */
	public static SourceBean getInfoGenerali() throws Exception {
		Object params[] = null;
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_TS_GENERALE", params, "SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere i flag commit batch e/o scattano impatti");
		return getRowAttribute(row);
	}

	public static Vector getFineMobilita(String dataRif) throws Exception {
		Object params[] = new Object[1];
		params[0] = dataRif;
		if (!DatiDiTest.TEST) {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_FINE_MOBILITA_BATCH", params, "SELECT",
					Values.DB_SIL_DATI);
			if (res != null)
				return res.getAttributeAsVector("ROW");
			else
				throw new Exception("impossibile leggere le mobilità terminate ");
		} else {
			return DatiDiTest.movimenti;
		}
	}

	public static StatoOccupazionaleBean getStatoOccupazionale(java.util.List statiOccupazionali, String dataRif)
			throws Exception {
		StatoOccupazionaleBean so = null;
		String dataInizioSo = "";
		String dataFineSo = "";
		SourceBean rowSOcc = null;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			rowSOcc = (SourceBean) statiOccupazionali.get(i);
			dataInizioSo = rowSOcc.containsAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO)
					? rowSOcc.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString()
					: "";
			dataFineSo = rowSOcc.containsAttribute(StatoOccupazionaleBean.DB_DAT_FINE)
					? rowSOcc.getAttribute(StatoOccupazionaleBean.DB_DAT_FINE).toString()
					: "";
			if (!dataInizioSo.equals("") && DateUtils.compare(dataInizioSo, dataRif) <= 0
					&& (dataFineSo.equals("") || DateUtils.compare(dataFineSo, dataRif) >= 0)) {
				so = new StatoOccupazionaleBean(rowSOcc);
				break;
			}
		}
		return so;
	}

	public static Vector getMotivoCessazione() throws Exception {
		Object params[] = null;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MV_CESSAZIONE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			return null;
	}

	/**
	 * Legge dal db did successive ad una did data
	 */
	public static Vector getDidSuccessive(BigDecimal prg, BigDecimal cdnLav, String dataDichiarazione,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[2];
		SourceBean row = null;
		params[0] = cdnLav;
		params[1] = prg;
		row = (SourceBean) transExec.executeQuery("GET_DID_PROTOCOLLATA_SUCCESSIVA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere did successive");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getDIDLavoratore(BigDecimal cdnLav, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		SourceBean row = null;
		params[0] = cdnLav;
		row = (SourceBean) transExec.executeQuery("GET_DID_LAVORATORE_PA_PR", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere did successive");
		return row.getAttributeAsVector("ROW");
	}

	public static Vector getIndisponibilitaLavoratore(Object cdnLav, String codIndisp,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[2];
		SourceBean row = null;
		params[0] = cdnLav;
		params[1] = codIndisp;
		row = (SourceBean) transExec.executeQuery("GET_INDISPONIBILTA_LAVORATORE", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le indisponibilità del lavoratore");
		return row.getAttributeAsVector("ROW");
	}

	/**
	 * 
	 * @param cdnLavoratore
	 *            identificativo del lavoratore coinvolto
	 * @param dataAnzianitaCM
	 *            data anzianità collocamento mirato
	 * @param dataSospensioneCM
	 *            data di riferimento dei mesi di sospensione che il lavoratore può aver maturato fuori provincia
	 * @param mesiSospCM
	 *            mesi di sospensione maturati fuori
	 * @param dataPubblicazione
	 * @return int (numMesiAnzianità >= 0 e < 0 quando il calcolo non si può fare)
	 * @throws Exception
	 */
	public static int calcolaAnzianitaCM(Object cdnLavoratore, String dataAnzianitaCM, String dataSospensioneCM,
			int mesiSospCM, String dataPubblicazione, String datAnzPregressaOrdinaria) throws Exception {
		SourceBean row = null;
		SourceBean ris = null;
		String dataAnzianita = "";
		String mesiAnz = "";
		String giorniAnz = "";
		int numGGAnzResidui = 0;
		String mesiRischioDisoccupazione = "";
		String mesiAnzPrec = "";
		String mesiSosp = "";
		String mesiSospPrec = "";
		int numMesiAnz = 0;
		int numMesiRischioDisocc = 0;
		int numMesiAnzPrec = 0;
		int numMesiSosp = 0;
		int numMesiSospPrec = 0;
		int numMesiTotAnz = -1;
		Object params[] = null;
		params = new Object[1];
		params[0] = cdnLavoratore;
		String DATA_NORMATIVA = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
		int lavNonIn297 = -1;
		int assenzaDateCalcolo = -2;
		int casoNonGestito = -3;
		int dateCMNonCorrette = -4;
		String datAnzianitaDisoccupazione = "";
		String mesiSospFornero = "";
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (dataNormativa297 != null && !dataNormativa297.equals("")) {
			DATA_NORMATIVA = dataNormativa297;
		}
		row = (SourceBean) QueryExecutor.executeQuery("GET_STATO_OCCUPAZ_APERTO", params, "SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale aperto del lavoratore");
		String codStatoOccupazRagg = row.containsAttribute("ROW.codstatooccupazragg")
				? row.getAttribute("ROW.codstatooccupazragg").toString()
				: "";
		if (codStatoOccupazRagg.equalsIgnoreCase("D") || codStatoOccupazRagg.equalsIgnoreCase("I")) {
			datAnzianitaDisoccupazione = row.containsAttribute("ROW.datanzianitadisoc")
					? row.getAttribute("ROW.datanzianitadisoc").toString()
					: "";
			if (datAnzPregressaOrdinaria.equals("")) {
				dataAnzianita = row.containsAttribute("ROW.datanzianitadisoc")
						? row.getAttribute("ROW.datanzianitadisoc").toString()
						: "";
			} else {
				dataAnzianita = datAnzPregressaOrdinaria;
			}
			if (dataAnzianita.equals("") || dataAnzianitaCM == null || dataAnzianitaCM.equals(""))
				return assenzaDateCalcolo;
			// La data sospensione CM non può essere precedente alla data anzianità CM
			if (dataSospensioneCM != null && !dataSospensioneCM.equals("")
					&& DateUtils.compare(dataSospensioneCM, dataAnzianitaCM) < 0) {
				return dateCMNonCorrette;
			}
			if (DateUtils.compare(dataAnzianita, dataAnzianitaCM) == 0) {
				if ((dataSospensioneCM == null || dataSospensioneCM.equals("")) && (mesiSospCM == 0)) {
					// MESI DI ANZIANITA' SONO GLI STESSI DEL COLLOCAMENTO ORDINARIO
					String dataPubblicazioneApp = dataPubblicazione;
					if (DateUtils.compare(dataPubblicazione, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
						dataPubblicazioneApp = DateUtils
								.giornoPrecedente(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
						mesiSospFornero = calcolaMesiSospFornero(cdnLavoratore, null);
					}
					params = new Object[88];
					for (int j = 0; j <= 49; j++) {
						params[j] = dataPubblicazioneApp;
					}
					params[50] = cdnLavoratore;
					params[51] = dataPubblicazioneApp;
					for (int j = 52; j <= 86; j++) {
						params[j] = dataPubblicazione;
					}
					params[87] = cdnLavoratore;
					ris = (SourceBean) QueryExecutor.executeQuery("GET_DATA_ANZIANITA_ORDINARIO", params, "SELECT",
							Values.DB_SIL_DATI);
					if (ris == null)
						throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
					mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString() : "";
					giorniAnz = ris.containsAttribute("ROW.giorni_anz") ? ris.getAttribute("ROW.giorni_anz").toString()
							: "";
					mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
							? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
							: "";
					mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
							? ris.getAttribute("ROW.mesi_anz_prec").toString()
							: "";
					mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP") ? ris.getAttribute("ROW.NUMMESISOSP").toString()
							: "";
					mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
							? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
							: "";
					if (!mesiAnz.equals(""))
						numMesiAnz = new Integer(mesiAnz).intValue();
					if (!giorniAnz.equals(""))
						numGGAnzResidui = new Integer(giorniAnz).intValue();
					if (!mesiAnzPrec.equals(""))
						numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
					if (!mesiSosp.equals(""))
						numMesiSosp = new Integer(mesiSosp).intValue();
					if (!mesiSospPrec.equals(""))
						numMesiSospPrec = new Integer(mesiSospPrec).intValue();
					if (!mesiRischioDisoccupazione.equals("")) {
						numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
					}
					int numGGRestantiRischioDisocc = 0;
					Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
					if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
						String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
						if (rischioDisocc.length == 2) {
							numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
						}
					}
					int numMesiSospFornero2014 = 0;
					int numGGRestantiSospFornero2014 = 0;
					if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
						String[] sospFornero = mesiSospFornero.split("-");
						if (sospFornero.length == 4) {
							numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
							numGGRestantiSospFornero2014 = new Integer(sospFornero[3]).intValue();
						}
					}
					int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
					int mesiAggiuntivi = ggSospResidui / 30;
					int meseDiffAnzianitaGiorni = 0;
					if (numGGAnzResidui >= (ggSospResidui % 30)) {
						numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
					} else {
						if ((ggSospResidui % 30) > 0) {
							numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
							meseDiffAnzianitaGiorni = 1;
						}
					}
					numMesiTotAnz = numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
							+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
					if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
						numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
					}
				} else {
					// (MESI DI ANZIANITA' DEL CM DALLA DATA ANZIANITA' CM FINO ALLA DATA X) - (MESI DI SOSPENSIONE
					// mesiSospCM)
					// + MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA X
					if (dataSospensioneCM != null && !dataSospensioneCM.equals("")) {
						params = new Object[72];
						params[0] = dataSospensioneCM;
						params[1] = dataAnzianitaCM;
						params[2] = dataSospensioneCM;
						params[3] = dataAnzianitaCM;
						params[4] = dataSospensioneCM;
						params[5] = dataAnzianitaCM;
						params[6] = dataSospensioneCM;
						params[7] = dataAnzianitaCM;
						params[8] = dataAnzianitaCM;
						params[9] = dataSospensioneCM;
						params[10] = dataSospensioneCM;
						params[11] = dataSospensioneCM;
						params[12] = dataSospensioneCM;
						params[13] = dataAnzianitaCM;
						params[14] = dataAnzianitaCM;
						params[15] = dataAnzianitaCM;
						params[16] = dataAnzianitaCM;
						params[17] = dataAnzianitaCM;
						params[18] = dataSospensioneCM;
						params[19] = dataAnzianitaCM;
						params[20] = dataSospensioneCM;
						params[21] = dataSospensioneCM;
						params[22] = dataAnzianitaCM;
						params[23] = dataSospensioneCM;
						params[24] = dataAnzianitaCM;
						params[25] = dataAnzianitaCM;
						params[26] = dataSospensioneCM;
						params[27] = dataAnzianitaCM;
						params[28] = dataSospensioneCM;
						params[29] = dataSospensioneCM;
						params[30] = dataAnzianitaCM;
						params[31] = dataSospensioneCM;
						params[32] = dataAnzianitaCM;
						params[33] = dataSospensioneCM;
						params[34] = dataSospensioneCM;
						params[35] = dataAnzianitaCM;
						params[36] = dataSospensioneCM;
						params[37] = dataAnzianitaCM;
						params[38] = dataAnzianitaCM;
						params[39] = dataSospensioneCM;
						params[40] = dataAnzianitaCM;
						params[41] = dataAnzianitaCM;
						params[42] = dataAnzianitaCM;
						params[43] = dataAnzianitaCM;
						params[44] = dataSospensioneCM;
						params[45] = dataAnzianitaCM;
						params[46] = dataSospensioneCM;
						params[47] = dataAnzianitaCM;
						params[48] = dataAnzianitaCM;
						params[49] = dataSospensioneCM;
						params[50] = dataSospensioneCM;
						params[51] = dataSospensioneCM;
						params[52] = dataSospensioneCM;
						params[53] = dataAnzianitaCM;
						params[54] = dataAnzianitaCM;
						params[55] = dataSospensioneCM;
						params[56] = dataAnzianitaCM;
						params[57] = dataSospensioneCM;
						params[58] = dataAnzianitaCM;
						params[59] = dataAnzianitaCM;
						params[60] = dataSospensioneCM;
						params[61] = dataAnzianitaCM;
						params[62] = dataSospensioneCM;
						params[63] = dataSospensioneCM;
						params[64] = dataAnzianitaCM;
						params[65] = dataSospensioneCM;
						params[66] = dataAnzianitaCM;
						params[67] = dataSospensioneCM;
						params[68] = dataSospensioneCM;
						params[69] = dataSospensioneCM;
						params[70] = dataAnzianitaCM;
						params[71] = cdnLavoratore;
						ris = (SourceBean) QueryExecutor.executeQuery("GET_ANZIANITA_COLLOCAMENTO_MIRATO", params,
								"SELECT", Values.DB_SIL_DATI);
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						// CALCOLO PARZIALE
						numMesiTotAnz = (numMesiAnz - mesiSospCM);
						// CALCOLO MESI ANZIANITA' ORDINARIO DALLA DATA X
						if (DateUtils.compare(dataSospensioneCM, DATA_NORMATIVA) < 0) {
							dataSospensioneCM = DATA_NORMATIVA;
						}
						// imposto i parametri
						params = settaParametriQueryAnzianitaCM(dataSospensioneCM, cdnLavoratore, dataPubblicazione);
						ris = (SourceBean) QueryExecutor.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA", params,
								"SELECT", Values.DB_SIL_DATI);
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						giorniAnz = ris.containsAttribute("ROW.giorni_anz")
								? ris.getAttribute("ROW.giorni_anz").toString()
								: "";
						mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
								? ris.getAttribute("ROW.mesi_anz_prec").toString()
								: "";
						mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
								? ris.getAttribute("ROW.NUMMESISOSP").toString()
								: "";
						mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
								? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
								: "";
						mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
								? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
								: "";

						int numGGRestantiRischioDisocc = 0;
						Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
						if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
							String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
							if (rischioDisocc.length == 2) {
								numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
							}
						}
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						if (!giorniAnz.equals(""))
							numGGAnzResidui = new Integer(giorniAnz).intValue();
						if (!mesiAnzPrec.equals(""))
							numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
						if (!mesiSosp.equals(""))
							numMesiSosp = new Integer(mesiSosp).intValue();
						if (!mesiSospPrec.equals(""))
							numMesiSospPrec = new Integer(mesiSospPrec).intValue();
						if (!mesiRischioDisoccupazione.equals("")) {
							numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
						}
						mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataSospensioneCM,
								dataPubblicazione, null);
						int numMesiSospFornero2014 = 0;
						int numGGRestantiSospFornero2014 = 0;
						if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
							String[] sospFornero = mesiSospFornero.split("-");
							if (sospFornero.length == 3) {
								numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
								numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
							}
						}
						int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
						int mesiAggiuntivi = ggSospResidui / 30;
						int meseDiffAnzianitaGiorni = 0;
						if (numGGAnzResidui >= (ggSospResidui % 30)) {
							numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
						} else {
							if ((ggSospResidui % 30) > 0) {
								numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
								meseDiffAnzianitaGiorni = 1;
							}
						}
						numMesiTotAnz = numMesiTotAnz + numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
								+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
						if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
							numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
						}
					}
				}
				if (!datAnzPregressaOrdinaria.equals("") && !datAnzianitaDisoccupazione.equals("")
						&& DateUtils.compare(datAnzPregressaOrdinaria, datAnzianitaDisoccupazione) < 0) {
					int mesiAddPregressi = DateUtils.monthsBetween(datAnzPregressaOrdinaria,
							datAnzianitaDisoccupazione);
					// i mesi iniziale e finale vanno considerati solo se i giorni commerciali lavorati sono almeno 16
					int ddInizio = Integer.parseInt(datAnzPregressaOrdinaria.substring(0, 2));
					int ddFine = Integer.parseInt(datAnzianitaDisoccupazione.substring(0, 2));
					if (mesiAddPregressi == 1) {
						if ((ddFine - ddInizio) + 1 < 16) {
							mesiAddPregressi = mesiAddPregressi - 1;
						}
					} else {
						if (((30 - ddInizio) + 1) < 16) {
							mesiAddPregressi = mesiAddPregressi - 1;
						}
						if (ddFine < 16) {
							mesiAddPregressi = mesiAddPregressi - 1;
						}
					}
					numMesiTotAnz = numMesiTotAnz + mesiAddPregressi;
				}
			} else {
				if (DateUtils.compare(dataAnzianita, dataAnzianitaCM) < 0) {
					if ((dataSospensioneCM == null || dataSospensioneCM.equals("")) && (mesiSospCM == 0)) {
						// CALCOLANO I MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA DI ANZIANITA' DEL CM
						// imposto i parametri
						params = settaParametriQueryAnzianitaCM(dataAnzianitaCM, cdnLavoratore, dataPubblicazione);
						ris = (SourceBean) QueryExecutor.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA", params,
								"SELECT", Values.DB_SIL_DATI);
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						giorniAnz = ris.containsAttribute("ROW.giorni_anz")
								? ris.getAttribute("ROW.giorni_anz").toString()
								: "";
						mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
								? ris.getAttribute("ROW.NUMMESISOSP").toString()
								: "";
						mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
								? ris.getAttribute("ROW.mesi_anz_prec").toString()
								: "";
						mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
								? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
								: "";
						mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
								? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
								: "";
						int numGGRestantiRischioDisocc = 0;
						Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
						if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
							String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
							if (rischioDisocc.length == 2) {
								numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
							}
						}
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						if (!giorniAnz.equals(""))
							numGGAnzResidui = new Integer(giorniAnz).intValue();
						if (!mesiSosp.equals(""))
							numMesiSosp = new Integer(mesiSosp).intValue();
						if (!mesiAnzPrec.equals(""))
							numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
						if (!mesiSospPrec.equals(""))
							numMesiSospPrec = new Integer(mesiSospPrec).intValue();
						if (!mesiRischioDisoccupazione.equals("")) {
							numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
						}

						mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataAnzianitaCM,
								dataPubblicazione, null);
						int numMesiSospFornero2014 = 0;
						int numGGRestantiSospFornero2014 = 0;
						if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
							String[] sospFornero = mesiSospFornero.split("-");
							if (sospFornero.length == 3) {
								numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
								numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
							}
						}
						int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
						int mesiAggiuntivi = ggSospResidui / 30;
						int meseDiffAnzianitaGiorni = 0;
						if (numGGAnzResidui >= (ggSospResidui % 30)) {
							numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
						} else {
							if ((ggSospResidui % 30) > 0) {
								numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
								meseDiffAnzianitaGiorni = 1;
							}
						}
						numMesiTotAnz = numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
								+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
						if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
							numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
						}
					} else {
						if (dataSospensioneCM != null && !dataSospensioneCM.equals("")) {
							// (MESI DI ANZIANITA' DEL CM DALLA DATA ANZIANITA' CM FINO ALLA DATA X)
							params = new Object[72];
							params[0] = dataSospensioneCM;
							params[1] = dataAnzianitaCM;
							params[2] = dataSospensioneCM;
							params[3] = dataAnzianitaCM;
							params[4] = dataSospensioneCM;
							params[5] = dataAnzianitaCM;
							params[6] = dataSospensioneCM;
							params[7] = dataAnzianitaCM;
							params[8] = dataAnzianitaCM;
							params[9] = dataSospensioneCM;
							params[10] = dataSospensioneCM;
							params[11] = dataSospensioneCM;
							params[12] = dataSospensioneCM;
							params[13] = dataAnzianitaCM;
							params[14] = dataAnzianitaCM;
							params[15] = dataAnzianitaCM;
							params[16] = dataAnzianitaCM;
							params[17] = dataAnzianitaCM;
							params[18] = dataSospensioneCM;
							params[19] = dataAnzianitaCM;
							params[20] = dataSospensioneCM;
							params[21] = dataSospensioneCM;
							params[22] = dataAnzianitaCM;
							params[23] = dataSospensioneCM;
							params[24] = dataAnzianitaCM;
							params[25] = dataAnzianitaCM;
							params[26] = dataSospensioneCM;
							params[27] = dataAnzianitaCM;
							params[28] = dataSospensioneCM;
							params[29] = dataSospensioneCM;
							params[30] = dataAnzianitaCM;
							params[31] = dataSospensioneCM;
							params[32] = dataAnzianitaCM;
							params[33] = dataSospensioneCM;
							params[34] = dataSospensioneCM;
							params[35] = dataAnzianitaCM;
							params[36] = dataSospensioneCM;
							params[37] = dataAnzianitaCM;
							params[38] = dataAnzianitaCM;
							params[39] = dataSospensioneCM;
							params[40] = dataAnzianitaCM;
							params[41] = dataAnzianitaCM;
							params[42] = dataAnzianitaCM;
							params[43] = dataAnzianitaCM;
							params[44] = dataSospensioneCM;
							params[45] = dataAnzianitaCM;
							params[46] = dataSospensioneCM;
							params[47] = dataAnzianitaCM;
							params[48] = dataAnzianitaCM;
							params[49] = dataSospensioneCM;
							params[50] = dataSospensioneCM;
							params[51] = dataSospensioneCM;
							params[52] = dataSospensioneCM;
							params[53] = dataAnzianitaCM;
							params[54] = dataAnzianitaCM;
							params[55] = dataSospensioneCM;
							params[56] = dataAnzianitaCM;
							params[57] = dataSospensioneCM;
							params[58] = dataAnzianitaCM;
							params[59] = dataAnzianitaCM;
							params[60] = dataSospensioneCM;
							params[61] = dataAnzianitaCM;
							params[62] = dataSospensioneCM;
							params[63] = dataSospensioneCM;
							params[64] = dataAnzianitaCM;
							params[65] = dataSospensioneCM;
							params[66] = dataAnzianitaCM;
							params[67] = dataSospensioneCM;
							params[68] = dataSospensioneCM;
							params[69] = dataSospensioneCM;
							params[70] = dataAnzianitaCM;
							params[71] = cdnLavoratore;
							ris = (SourceBean) QueryExecutor.executeQuery("GET_ANZIANITA_COLLOCAMENTO_MIRATO", params,
									"SELECT", Values.DB_SIL_DATI);
							if (ris == null)
								throw new Exception(
										"impossibile calcolare i mesi di anzianità del collocamento mirato");
							mesiAnz = ris.containsAttribute("ROW.mesi_anz")
									? ris.getAttribute("ROW.mesi_anz").toString()
									: "";
							if (!mesiAnz.equals(""))
								numMesiAnz = new Integer(mesiAnz).intValue();
							// CALCOLO PARZIALE
							numMesiTotAnz = (numMesiAnz - mesiSospCM);
							// CALCOLANO I MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA X
							// imposto i parametri
							params = settaParametriQueryAnzianitaCM(dataSospensioneCM, cdnLavoratore,
									dataPubblicazione);
							ris = (SourceBean) QueryExecutor.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA",
									params, "SELECT", Values.DB_SIL_DATI);
							if (ris == null)
								throw new Exception(
										"impossibile calcolare i mesi di anzianità del collocamento mirato");
							mesiAnz = ris.containsAttribute("ROW.mesi_anz")
									? ris.getAttribute("ROW.mesi_anz").toString()
									: "";
							giorniAnz = ris.containsAttribute("ROW.giorni_anz")
									? ris.getAttribute("ROW.giorni_anz").toString()
									: "";
							mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
									? ris.getAttribute("ROW.NUMMESISOSP").toString()
									: "";
							mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
									? ris.getAttribute("ROW.mesi_anz_prec").toString()
									: "";
							mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
									? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
									: "";
							mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
									? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
									: "";
							if (!mesiAnz.equals(""))
								numMesiAnz = new Integer(mesiAnz).intValue();
							if (!giorniAnz.equals(""))
								numGGAnzResidui = new Integer(giorniAnz).intValue();
							if (!mesiSosp.equals(""))
								numMesiSosp = new Integer(mesiSosp).intValue();
							if (!mesiAnzPrec.equals(""))
								numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
							if (!mesiSospPrec.equals(""))
								numMesiSospPrec = new Integer(mesiSospPrec).intValue();
							if (!mesiRischioDisoccupazione.equals("")) {
								numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
							}
							int numGGRestantiRischioDisocc = 0;
							Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
							if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
								String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
								if (rischioDisocc.length == 2) {
									numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
								}
							}
							mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataSospensioneCM,
									dataPubblicazione, null);
							int numMesiSospFornero2014 = 0;
							int numGGRestantiSospFornero2014 = 0;
							if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
								String[] sospFornero = mesiSospFornero.split("-");
								if (sospFornero.length == 3) {
									numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
									numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
								}
							}
							int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
							int mesiAggiuntivi = ggSospResidui / 30;
							int meseDiffAnzianitaGiorni = 0;
							if (numGGAnzResidui >= (ggSospResidui % 30)) {
								numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
							} else {
								if ((ggSospResidui % 30) > 0) {
									numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
									meseDiffAnzianitaGiorni = 1;
								}
							}
							numMesiTotAnz = numMesiAnzPrec + numMesiTotAnz + numMesiAnz - (numMesiSospPrec + numMesiSosp
									+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
							if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
								numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
							}
						}
					}
				} else {
					return casoNonGestito;
				}
			}
		} else {
			return lavNonIn297;
		}
		// Se arriva a questo punto significa che ha calcolato i mesi di anzianità.
		// Il valore di ritorno non può essere negativo.
		if (numMesiTotAnz < 0) {
			numMesiTotAnz = 0;
		}
		return numMesiTotAnz;
	}

	public static String calcolaMesiSospFornero(Object cdnLavoratore, TransactionQueryExecutor txExecutor)
			throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		String numMesiSops = "0";
		String numGGMesiSosp = "0";
		String risultatoDefault = numMesiSops + "-" + numGGMesiSosp;

		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			if (txExecutor != null) {
				conn = txExecutor.getDataConnection();
			} else {
				conn = dcm.getConnection("SIL_DATI");
			}
			String risultato = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_MOVIMENTI.MesiSospDecretoFornero2014(?,?,?) }");

			parameters = new ArrayList(4);
			// 1. Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. cdnLav
			parameters.add(conn.createDataField("cdnLav", java.sql.Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			// 3. dataStatoOccRif
			parameters.add(conn.createDataField("dataStatoOccRif", java.sql.Types.DATE, null));
			command.setAsInputParameters(paramIndex++);
			// 4. prgStatoOcc
			parameters.add(conn.createDataField("prgStatoOcc", java.sql.Types.BIGINT, null));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			// Reperisco i valori di output della stored
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			risultato = df.getStringValue();

			return risultato;

		} catch (Exception e) {
			return risultatoDefault;
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

	public static String calcolaMesiSospForneroDallaData(Object cdnLavoratore, String dataCalcolo, String dataRif,
			TransactionQueryExecutor txExecutor) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		String numMesiSops = "0";
		String numGGMesiSosp = "0";
		String risultatoDefault = numMesiSops + "-" + numGGMesiSosp;

		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			if (txExecutor != null) {
				conn = txExecutor.getDataConnection();
			} else {
				conn = dcm.getConnection("SIL_DATI");
			}
			String risultato = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_MOVIMENTI.MesiSospFornero2014DallaData(?,?,?) }");

			parameters = new ArrayList(4);
			// 1. Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. cdnLav
			parameters.add(conn.createDataField("cdnLav", java.sql.Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			// 3. dataCalcolo
			parameters.add(conn.createDataField("dataCalcolo", java.sql.Types.VARCHAR, dataCalcolo));
			command.setAsInputParameters(paramIndex++);
			// 4. dataRif
			parameters.add(conn.createDataField("dataRif", java.sql.Types.VARCHAR, dataRif));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			// Reperisco i valori di output della stored
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			risultato = df.getStringValue();

			return risultato;

		} catch (Exception e) {
			return risultatoDefault;
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

	/**
	 * 
	 * @param cdnLavoratore
	 *            identificativo del lavoratore coinvolto
	 * @param dataAnzianitaCM
	 *            data anzianità collocamento mirato
	 * @param dataSospensioneCM
	 *            data di riferimento dei mesi di sospensione che il lavoratore può aver maturato fuori provincia
	 * @param mesiSospCM
	 *            mesi di sospensione maturati fuori
	 * @param dataPubblicazione
	 * @param transExec
	 *            (connessione, quando la query deve essere eseguita in transazione)
	 * @return int (numMesiAnzianità >= 0 e < 0 quando il calcolo non si può fare)
	 * @throws Exception
	 */
	public static int calcolaAnzianitaCM(Object cdnLavoratore, String dataAnzianitaCM, String dataSospensioneCM,
			int mesiSospCM, String dataPubblicazione, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		SourceBean ris = null;
		String dataAnzianita = "";
		String mesiAnz = "";
		String giorniAnz = "";
		int numGGAnzResidui = 0;
		String mesiRischioDisoccupazione = "";
		String mesiAnzPrec = "";
		String mesiSosp = "";
		String mesiSospPrec = "";
		int numMesiAnz = 0;
		int numMesiRischioDisocc = 0;
		int numMesiAnzPrec = 0;
		int numMesiSosp = 0;
		int numMesiSospPrec = 0;
		int numMesiTotAnz = -1;
		Object params[] = null;
		params = new Object[1];
		params[0] = cdnLavoratore;
		String DATA_NORMATIVA = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
		int lavNonIn297 = -1;
		int assenzaDateCalcolo = -2;
		int casoNonGestito = -3;
		int dateCMNonCorrette = -4;
		String mesiSospFornero = "";
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (dataNormativa297 != null && !dataNormativa297.equals("")) {
			DATA_NORMATIVA = dataNormativa297;
		}
		row = (SourceBean) transExec.executeQuery("GET_STATO_OCCUPAZ_APERTO", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere lo stato occupazionale aperto del lavoratore");
		String codStatoOccupazRagg = row.containsAttribute("ROW.codstatooccupazragg")
				? row.getAttribute("ROW.codstatooccupazragg").toString()
				: "";
		if (codStatoOccupazRagg.equalsIgnoreCase("D") || codStatoOccupazRagg.equalsIgnoreCase("I")) {
			dataAnzianita = row.containsAttribute("ROW.datanzianitadisoc")
					? row.getAttribute("ROW.datanzianitadisoc").toString()
					: "";
			if (dataAnzianita.equals("") || dataAnzianitaCM == null || dataAnzianitaCM.equals(""))
				return assenzaDateCalcolo;
			// La data sospensione CM non può essere precedente alla data anzianità CM
			if (dataSospensioneCM != null && !dataSospensioneCM.equals("")
					&& DateUtils.compare(dataSospensioneCM, dataAnzianitaCM) < 0) {
				return dateCMNonCorrette;
			}
			if (DateUtils.compare(dataAnzianita, dataAnzianitaCM) == 0) {
				if ((dataSospensioneCM == null || dataSospensioneCM.equals("")) && (mesiSospCM == 0)) {
					// MESI DI ANZIANITA' SONO GLI STESSI DEL COLLOCAMENTO ORDINARIO
					String dataPubblicazioneApp = dataPubblicazione;
					if (DateUtils.compare(dataPubblicazione, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
						dataPubblicazioneApp = DateUtils
								.giornoPrecedente(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
						mesiSospFornero = calcolaMesiSospFornero(cdnLavoratore, transExec);
					}
					params = new Object[88];
					for (int j = 0; j <= 49; j++) {
						params[j] = dataPubblicazioneApp;
					}
					params[50] = cdnLavoratore;
					params[51] = dataPubblicazioneApp;
					for (int j = 52; j <= 86; j++) {
						params[j] = dataPubblicazione;
					}
					params[87] = cdnLavoratore;
					ris = (SourceBean) transExec.executeQuery("GET_DATA_ANZIANITA_ORDINARIO", params, "SELECT");
					if (ris == null)
						throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
					mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString() : "";
					giorniAnz = ris.containsAttribute("ROW.giorni_anz") ? ris.getAttribute("ROW.giorni_anz").toString()
							: "";
					mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
							? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
							: "";
					mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
							? ris.getAttribute("ROW.mesi_anz_prec").toString()
							: "";
					mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP") ? ris.getAttribute("ROW.NUMMESISOSP").toString()
							: "";
					mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
							? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
							: "";
					if (!mesiAnz.equals(""))
						numMesiAnz = new Integer(mesiAnz).intValue();
					if (!giorniAnz.equals(""))
						numGGAnzResidui = new Integer(giorniAnz).intValue();
					if (!mesiAnzPrec.equals(""))
						numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
					if (!mesiSosp.equals(""))
						numMesiSosp = new Integer(mesiSosp).intValue();
					if (!mesiSospPrec.equals(""))
						numMesiSospPrec = new Integer(mesiSospPrec).intValue();
					if (!mesiRischioDisoccupazione.equals("")) {
						numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
					}
					int numGGRestantiRischioDisocc = 0;
					Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
					if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
						String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
						if (rischioDisocc.length == 2) {
							numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
						}
					}
					int numMesiSospFornero2014 = 0;
					int numGGRestantiSospFornero2014 = 0;
					if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
						String[] sospFornero = mesiSospFornero.split("-");
						if (sospFornero.length == 4) {
							numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
							numGGRestantiSospFornero2014 = new Integer(sospFornero[3]).intValue();
						}
					}
					int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
					int mesiAggiuntivi = ggSospResidui / 30;
					int meseDiffAnzianitaGiorni = 0;
					if (numGGAnzResidui >= (ggSospResidui % 30)) {
						numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
					} else {
						if ((ggSospResidui % 30) > 0) {
							numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
							meseDiffAnzianitaGiorni = 1;
						}
					}
					numMesiTotAnz = numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
							+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
					if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
						numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
					}
				} else {
					// (MESI DI ANZIANITA' DEL CM DALLA DATA ANZIANITA' CM FINO ALLA DATA X) - (MESI DI SOSPENSIONE
					// mesiSospCM)
					// + MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA X
					if (dataSospensioneCM != null && !dataSospensioneCM.equals("")) {
						params = new Object[72];
						params[0] = dataSospensioneCM;
						params[1] = dataAnzianitaCM;
						params[2] = dataSospensioneCM;
						params[3] = dataAnzianitaCM;
						params[4] = dataSospensioneCM;
						params[5] = dataAnzianitaCM;
						params[6] = dataSospensioneCM;
						params[7] = dataAnzianitaCM;
						params[8] = dataAnzianitaCM;
						params[9] = dataSospensioneCM;
						params[10] = dataSospensioneCM;
						params[11] = dataSospensioneCM;
						params[12] = dataSospensioneCM;
						params[13] = dataAnzianitaCM;
						params[14] = dataAnzianitaCM;
						params[15] = dataAnzianitaCM;
						params[16] = dataAnzianitaCM;
						params[17] = dataAnzianitaCM;
						params[18] = dataSospensioneCM;
						params[19] = dataAnzianitaCM;
						params[20] = dataSospensioneCM;
						params[21] = dataSospensioneCM;
						params[22] = dataAnzianitaCM;
						params[23] = dataSospensioneCM;
						params[24] = dataAnzianitaCM;
						params[25] = dataAnzianitaCM;
						params[26] = dataSospensioneCM;
						params[27] = dataAnzianitaCM;
						params[28] = dataSospensioneCM;
						params[29] = dataSospensioneCM;
						params[30] = dataAnzianitaCM;
						params[31] = dataSospensioneCM;
						params[32] = dataAnzianitaCM;
						params[33] = dataSospensioneCM;
						params[34] = dataSospensioneCM;
						params[35] = dataAnzianitaCM;
						params[36] = dataSospensioneCM;
						params[37] = dataAnzianitaCM;
						params[38] = dataAnzianitaCM;
						params[39] = dataSospensioneCM;
						params[40] = dataAnzianitaCM;
						params[41] = dataAnzianitaCM;
						params[42] = dataAnzianitaCM;
						params[43] = dataAnzianitaCM;
						params[44] = dataSospensioneCM;
						params[45] = dataAnzianitaCM;
						params[46] = dataSospensioneCM;
						params[47] = dataAnzianitaCM;
						params[48] = dataAnzianitaCM;
						params[49] = dataSospensioneCM;
						params[50] = dataSospensioneCM;
						params[51] = dataSospensioneCM;
						params[52] = dataSospensioneCM;
						params[53] = dataAnzianitaCM;
						params[54] = dataAnzianitaCM;
						params[55] = dataSospensioneCM;
						params[56] = dataAnzianitaCM;
						params[57] = dataSospensioneCM;
						params[58] = dataAnzianitaCM;
						params[59] = dataAnzianitaCM;
						params[60] = dataSospensioneCM;
						params[61] = dataAnzianitaCM;
						params[62] = dataSospensioneCM;
						params[63] = dataSospensioneCM;
						params[64] = dataAnzianitaCM;
						params[65] = dataSospensioneCM;
						params[66] = dataAnzianitaCM;
						params[67] = dataSospensioneCM;
						params[68] = dataSospensioneCM;
						params[69] = dataSospensioneCM;
						params[70] = dataAnzianitaCM;
						params[71] = cdnLavoratore;
						ris = (SourceBean) transExec.executeQuery("GET_ANZIANITA_COLLOCAMENTO_MIRATO", params,
								"SELECT");
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						// CALCOLO PARZIALE
						numMesiTotAnz = (numMesiAnz - mesiSospCM);
						// CALCOLO MESI ANZIANITA' ORDINARIO DALLA DATA X
						if (DateUtils.compare(dataSospensioneCM, DATA_NORMATIVA) < 0) {
							dataSospensioneCM = DATA_NORMATIVA;
						}
						// imposto i parametri
						params = settaParametriQueryAnzianitaCM(dataSospensioneCM, cdnLavoratore, dataPubblicazione);
						ris = (SourceBean) transExec.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA", params,
								"SELECT");
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						giorniAnz = ris.containsAttribute("ROW.giorni_anz")
								? ris.getAttribute("ROW.giorni_anz").toString()
								: "";
						mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
								? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
								: "";
						mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
								? ris.getAttribute("ROW.mesi_anz_prec").toString()
								: "";
						mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
								? ris.getAttribute("ROW.NUMMESISOSP").toString()
								: "";
						mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
								? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
								: "";
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						if (!giorniAnz.equals(""))
							numGGAnzResidui = new Integer(giorniAnz).intValue();
						if (!mesiAnzPrec.equals(""))
							numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
						if (!mesiSosp.equals(""))
							numMesiSosp = new Integer(mesiSosp).intValue();
						if (!mesiSospPrec.equals(""))
							numMesiSospPrec = new Integer(mesiSospPrec).intValue();
						if (!mesiRischioDisoccupazione.equals("")) {
							numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
						}
						int numGGRestantiRischioDisocc = 0;
						Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
						if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
							String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
							if (rischioDisocc.length == 2) {
								numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
							}
						}
						mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataSospensioneCM,
								dataPubblicazione, transExec);
						int numMesiSospFornero2014 = 0;
						int numGGRestantiSospFornero2014 = 0;
						if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
							String[] sospFornero = mesiSospFornero.split("-");
							if (sospFornero.length == 3) {
								numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
								numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
							}
						}
						int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
						int mesiAggiuntivi = ggSospResidui / 30;
						int meseDiffAnzianitaGiorni = 0;
						if (numGGAnzResidui >= (ggSospResidui % 30)) {
							numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
						} else {
							if ((ggSospResidui % 30) > 0) {
								numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
								meseDiffAnzianitaGiorni = 1;
							}
						}
						numMesiTotAnz = numMesiTotAnz + numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
								+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
						if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
							numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
						}
					}
				}
			} else {
				if (DateUtils.compare(dataAnzianita, dataAnzianitaCM) < 0) {
					if ((dataSospensioneCM == null || dataSospensioneCM.equals("")) && (mesiSospCM == 0)) {
						// CALCOLANO I MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA DI ANZIANITA' DEL CM
						// imposto i parametri
						params = settaParametriQueryAnzianitaCM(dataAnzianitaCM, cdnLavoratore, dataPubblicazione);
						ris = (SourceBean) transExec.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA", params,
								"SELECT");
						if (ris == null)
							throw new Exception("impossibile calcolare i mesi di anzianità del collocamento mirato");
						mesiAnz = ris.containsAttribute("ROW.mesi_anz") ? ris.getAttribute("ROW.mesi_anz").toString()
								: "";
						giorniAnz = ris.containsAttribute("ROW.giorni_anz")
								? ris.getAttribute("ROW.giorni_anz").toString()
								: "";
						mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
								? ris.getAttribute("ROW.NUMMESISOSP").toString()
								: "";
						mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
								? ris.getAttribute("ROW.mesi_anz_prec").toString()
								: "";
						mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
								? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
								: "";
						mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
								? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
								: "";
						if (!mesiAnz.equals(""))
							numMesiAnz = new Integer(mesiAnz).intValue();
						if (!giorniAnz.equals(""))
							numGGAnzResidui = new Integer(giorniAnz).intValue();
						if (!mesiSosp.equals(""))
							numMesiSosp = new Integer(mesiSosp).intValue();
						if (!mesiAnzPrec.equals(""))
							numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
						if (!mesiSospPrec.equals(""))
							numMesiSospPrec = new Integer(mesiSospPrec).intValue();
						if (!mesiRischioDisoccupazione.equals("")) {
							numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
						}
						int numGGRestantiRischioDisocc = 0;
						Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
						if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
							String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
							if (rischioDisocc.length == 2) {
								numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
							}
						}
						mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataAnzianitaCM,
								dataPubblicazione, transExec);
						int numMesiSospFornero2014 = 0;
						int numGGRestantiSospFornero2014 = 0;
						if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
							String[] sospFornero = mesiSospFornero.split("-");
							if (sospFornero.length == 3) {
								numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
								numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
							}
						}
						int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
						int mesiAggiuntivi = ggSospResidui / 30;
						int meseDiffAnzianitaGiorni = 0;
						if (numGGAnzResidui >= (ggSospResidui % 30)) {
							numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
						} else {
							if ((ggSospResidui % 30) > 0) {
								numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
								meseDiffAnzianitaGiorni = 1;
							}
						}
						numMesiTotAnz = numMesiAnzPrec + numMesiAnz - (numMesiSospPrec + numMesiSosp
								+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
						if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
							numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
						}
					} else {
						if (dataSospensioneCM != null && !dataSospensioneCM.equals("")) {
							// (MESI DI ANZIANITA' DEL CM DALLA DATA ANZIANITA' CM FINO ALLA DATA X)
							params = new Object[72];
							params[0] = dataSospensioneCM;
							params[1] = dataAnzianitaCM;
							params[2] = dataSospensioneCM;
							params[3] = dataAnzianitaCM;
							params[4] = dataSospensioneCM;
							params[5] = dataAnzianitaCM;
							params[6] = dataSospensioneCM;
							params[7] = dataAnzianitaCM;
							params[8] = dataAnzianitaCM;
							params[9] = dataSospensioneCM;
							params[10] = dataSospensioneCM;
							params[11] = dataSospensioneCM;
							params[12] = dataSospensioneCM;
							params[13] = dataAnzianitaCM;
							params[14] = dataAnzianitaCM;
							params[15] = dataAnzianitaCM;
							params[16] = dataAnzianitaCM;
							params[17] = dataAnzianitaCM;
							params[18] = dataSospensioneCM;
							params[19] = dataAnzianitaCM;
							params[20] = dataSospensioneCM;
							params[21] = dataSospensioneCM;
							params[22] = dataAnzianitaCM;
							params[23] = dataSospensioneCM;
							params[24] = dataAnzianitaCM;
							params[25] = dataAnzianitaCM;
							params[26] = dataSospensioneCM;
							params[27] = dataAnzianitaCM;
							params[28] = dataSospensioneCM;
							params[29] = dataSospensioneCM;
							params[30] = dataAnzianitaCM;
							params[31] = dataSospensioneCM;
							params[32] = dataAnzianitaCM;
							params[33] = dataSospensioneCM;
							params[34] = dataSospensioneCM;
							params[35] = dataAnzianitaCM;
							params[36] = dataSospensioneCM;
							params[37] = dataAnzianitaCM;
							params[38] = dataAnzianitaCM;
							params[39] = dataSospensioneCM;
							params[40] = dataAnzianitaCM;
							params[41] = dataAnzianitaCM;
							params[42] = dataAnzianitaCM;
							params[43] = dataAnzianitaCM;
							params[44] = dataSospensioneCM;
							params[45] = dataAnzianitaCM;
							params[46] = dataSospensioneCM;
							params[47] = dataAnzianitaCM;
							params[48] = dataAnzianitaCM;
							params[49] = dataSospensioneCM;
							params[50] = dataSospensioneCM;
							params[51] = dataSospensioneCM;
							params[52] = dataSospensioneCM;
							params[53] = dataAnzianitaCM;
							params[54] = dataAnzianitaCM;
							params[55] = dataSospensioneCM;
							params[56] = dataAnzianitaCM;
							params[57] = dataSospensioneCM;
							params[58] = dataAnzianitaCM;
							params[59] = dataAnzianitaCM;
							params[60] = dataSospensioneCM;
							params[61] = dataAnzianitaCM;
							params[62] = dataSospensioneCM;
							params[63] = dataSospensioneCM;
							params[64] = dataAnzianitaCM;
							params[65] = dataSospensioneCM;
							params[66] = dataAnzianitaCM;
							params[67] = dataSospensioneCM;
							params[68] = dataSospensioneCM;
							params[69] = dataSospensioneCM;
							params[70] = dataAnzianitaCM;
							params[71] = cdnLavoratore;
							ris = (SourceBean) transExec.executeQuery("GET_ANZIANITA_COLLOCAMENTO_MIRATO", params,
									"SELECT");
							if (ris == null)
								throw new Exception(
										"impossibile calcolare i mesi di anzianità del collocamento mirato");
							mesiAnz = ris.containsAttribute("ROW.mesi_anz")
									? ris.getAttribute("ROW.mesi_anz").toString()
									: "";
							if (!mesiAnz.equals(""))
								numMesiAnz = new Integer(mesiAnz).intValue();
							// CALCOLO PARZIALE
							numMesiTotAnz = (numMesiAnz - mesiSospCM);
							// CALCOLANO I MESI DI ANZIANITA' DELL'ORDINARIO DALLA DATA X
							// imposto i parametri
							params = settaParametriQueryAnzianitaCM(dataSospensioneCM, cdnLavoratore,
									dataPubblicazione);
							ris = (SourceBean) transExec.executeQuery("GET_DATA_ANZIANITA_ORDINARIO_DALLA_DATA", params,
									"SELECT");
							if (ris == null)
								throw new Exception(
										"impossibile calcolare i mesi di anzianità del collocamento mirato");
							mesiAnz = ris.containsAttribute("ROW.mesi_anz")
									? ris.getAttribute("ROW.mesi_anz").toString()
									: "";
							giorniAnz = ris.containsAttribute("ROW.giorni_anz")
									? ris.getAttribute("ROW.giorni_anz").toString()
									: "";
							mesiSosp = ris.containsAttribute("ROW.NUMMESISOSP")
									? ris.getAttribute("ROW.NUMMESISOSP").toString()
									: "";
							mesiAnzPrec = ris.containsAttribute("ROW.mesi_anz_prec")
									? ris.getAttribute("ROW.mesi_anz_prec").toString()
									: "";
							mesiSospPrec = ris.containsAttribute("ROW.NUMMESISOSPPREC")
									? ris.getAttribute("ROW.NUMMESISOSPPREC").toString()
									: "";
							mesiRischioDisoccupazione = ris.containsAttribute("ROW.mesi_rischio_disocc")
									? ris.getAttribute("ROW.mesi_rischio_disocc").toString()
									: "";
							if (!mesiAnz.equals(""))
								numMesiAnz = new Integer(mesiAnz).intValue();
							if (!mesiSosp.equals(""))
								numMesiSosp = new Integer(mesiSosp).intValue();
							if (!giorniAnz.equals(""))
								numGGAnzResidui = new Integer(giorniAnz).intValue();
							if (!mesiAnzPrec.equals(""))
								numMesiAnzPrec = new Integer(mesiAnzPrec).intValue();
							if (!mesiSospPrec.equals(""))
								numMesiSospPrec = new Integer(mesiSospPrec).intValue();
							if (!mesiRischioDisoccupazione.equals("")) {
								numMesiRischioDisocc = new Integer(mesiRischioDisoccupazione).intValue();
							}
							int numGGRestantiRischioDisocc = 0;
							Object mesiRischioDisoccCompleto = ris.getAttribute("ROW.mesi_rischio_disocc_completo");
							if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
								String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
								if (rischioDisocc.length == 2) {
									numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
								}
							}
							mesiSospFornero = calcolaMesiSospForneroDallaData(cdnLavoratore, dataSospensioneCM,
									dataPubblicazione, transExec);
							int numMesiSospFornero2014 = 0;
							int numGGRestantiSospFornero2014 = 0;
							if (mesiSospFornero != null && !mesiSospFornero.equals("")) {
								String[] sospFornero = mesiSospFornero.split("-");
								if (sospFornero.length == 3) {
									numMesiSospFornero2014 = new Integer(sospFornero[0]).intValue();
									numGGRestantiSospFornero2014 = new Integer(sospFornero[2]).intValue();
								}
							}
							int ggSospResidui = numGGRestantiRischioDisocc + numGGRestantiSospFornero2014;
							int mesiAggiuntivi = ggSospResidui / 30;
							int meseDiffAnzianitaGiorni = 0;
							if (numGGAnzResidui >= (ggSospResidui % 30)) {
								numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
							} else {
								if ((ggSospResidui % 30) > 0) {
									numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
									meseDiffAnzianitaGiorni = 1;
								}
							}
							numMesiTotAnz = numMesiAnzPrec + numMesiTotAnz + numMesiAnz - (numMesiSospPrec + numMesiSosp
									+ numMesiSospFornero2014 + numMesiRischioDisocc + mesiAggiuntivi);
							if (numMesiTotAnz > 0 && meseDiffAnzianitaGiorni > 0) {
								numMesiTotAnz = numMesiTotAnz - meseDiffAnzianitaGiorni;
							}
						}
					}
				} else {
					return casoNonGestito;
				}
			}
		} else {
			return lavNonIn297;
		}
		// Se arriva a questo punto significa che ha calcolato i mesi di anzianità.
		// Il valore di ritorno non può essere negativo.
		if (numMesiTotAnz < 0) {
			numMesiTotAnz = 0;
		}
		return numMesiTotAnz;
	}

	public static Vector getMovimentiRiciclaggio(int giorni) throws Exception {
		Object params[] = new Object[1];
		params[0] = new Integer(giorni);
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_DA_APPOGGIO_A_RICICLAGGIO", params,
				"SELECT", Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di appoggio ");
	}

	public static Vector getMovimentiDaRiciclaggio(int numMovimenti) throws Exception {
		Object params[] = new Object[1];
		params[0] = new Integer(numMovimenti);
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_DA_RICICLAGGIO_A_APPOGGIO", params,
				"SELECT", Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di riciclaggio ");
	}

	public static Vector getMovimentiDaArchiviare(int user, int numGiorniDaUltimaValidazione) throws Exception {
		Object params[] = new Object[3];
		params[0] = new Integer(user);
		params[1] = new Integer(numGiorniDaUltimaValidazione);
		params[2] = new Integer(user);
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_DA_ARCHIVIARE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di appoggio ");
	}

	public static Object[] settaParametriQueryAnzianitaCM(String dataCalcolo, Object cdnLavoratore, String dataOdierna)
			throws Exception {
		String dataCalcoloApp = dataCalcolo;
		String dataOdiernaApp = dataOdierna;
		if (DateUtils.compare(dataCalcolo, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
			dataCalcoloApp = DateUtils.giornoPrecedente(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
		}
		if (DateUtils.compare(dataOdierna, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
			dataOdiernaApp = DateUtils.giornoPrecedente(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
		}
		Object params[] = new Object[177];
		params[0] = dataOdiernaApp;
		params[1] = dataCalcoloApp;
		params[2] = dataCalcoloApp;
		params[3] = dataOdiernaApp;
		params[4] = dataOdiernaApp;
		params[5] = dataOdiernaApp;
		params[6] = dataOdiernaApp;
		params[7] = dataCalcoloApp;
		params[8] = dataCalcoloApp;
		params[9] = dataCalcoloApp;
		params[10] = dataOdiernaApp;
		params[11] = dataCalcoloApp;
		params[12] = dataCalcoloApp;
		params[13] = dataOdiernaApp;
		params[14] = dataOdiernaApp;
		params[15] = dataOdiernaApp;
		params[16] = dataOdiernaApp;
		params[17] = dataCalcoloApp;
		params[18] = dataCalcoloApp;
		params[19] = dataCalcoloApp;
		params[20] = dataOdiernaApp;
		params[21] = dataCalcoloApp;
		params[22] = dataCalcoloApp;
		params[23] = dataOdiernaApp;
		params[24] = dataOdiernaApp;
		params[25] = dataOdiernaApp;
		params[26] = dataOdiernaApp;
		params[27] = dataCalcoloApp;
		params[28] = dataCalcoloApp;
		params[29] = dataCalcoloApp;
		params[30] = dataOdiernaApp;
		params[31] = dataCalcoloApp;
		params[32] = dataCalcoloApp;
		params[33] = dataOdiernaApp;
		params[34] = dataOdiernaApp;
		params[35] = dataOdiernaApp;
		params[36] = dataOdiernaApp;
		params[37] = dataCalcoloApp;
		params[38] = dataCalcoloApp;
		params[39] = dataCalcoloApp;
		params[40] = dataCalcoloApp;
		params[41] = dataCalcoloApp;
		params[42] = dataCalcoloApp;
		params[43] = dataCalcoloApp;
		params[44] = dataCalcoloApp;
		params[45] = dataCalcoloApp;
		params[46] = dataOdiernaApp;
		params[47] = dataCalcoloApp;
		params[48] = dataCalcoloApp;
		params[49] = dataOdiernaApp;
		params[50] = dataOdiernaApp;
		params[51] = dataOdiernaApp;
		params[52] = dataOdiernaApp;
		params[53] = dataOdiernaApp;
		params[54] = dataCalcoloApp;
		params[55] = dataCalcoloApp;
		params[56] = dataOdiernaApp;
		params[57] = dataOdiernaApp;
		params[58] = dataOdiernaApp;
		params[59] = dataOdiernaApp;
		params[60] = dataOdiernaApp;
		params[61] = dataCalcoloApp;
		params[62] = dataCalcoloApp;
		params[63] = dataOdiernaApp;
		params[64] = dataOdiernaApp;
		params[65] = dataOdiernaApp;
		params[66] = dataOdiernaApp;
		params[67] = dataCalcoloApp;
		params[68] = dataCalcoloApp;
		params[69] = dataCalcoloApp;
		params[70] = dataOdiernaApp;
		params[71] = dataCalcoloApp;
		params[72] = dataCalcoloApp;
		params[73] = dataOdiernaApp;
		params[74] = dataOdiernaApp;
		params[75] = dataOdiernaApp;
		params[76] = dataOdiernaApp;
		params[77] = dataCalcoloApp;
		params[78] = dataCalcoloApp;
		params[79] = dataCalcoloApp;
		params[80] = dataOdiernaApp;
		params[81] = dataCalcoloApp;
		params[82] = dataCalcoloApp;
		params[83] = dataOdiernaApp;
		params[84] = dataOdiernaApp;
		params[85] = dataOdiernaApp;
		params[86] = dataOdiernaApp;
		params[87] = dataCalcoloApp;
		params[88] = dataCalcoloApp;
		params[89] = dataCalcoloApp;
		params[90] = dataOdiernaApp;
		params[91] = dataCalcoloApp;
		params[92] = dataCalcoloApp;
		params[93] = dataOdiernaApp;
		params[94] = dataOdiernaApp;
		params[95] = dataOdiernaApp;
		params[96] = dataOdiernaApp;
		params[97] = dataCalcoloApp;
		params[98] = dataCalcoloApp;
		params[99] = dataCalcoloApp;
		params[100] = cdnLavoratore;
		params[101] = dataCalcoloApp;
		params[102] = dataCalcoloApp;
		params[103] = dataCalcoloApp;
		params[104] = dataOdiernaApp;
		params[105] = dataOdierna;
		params[106] = dataCalcolo;
		params[107] = dataOdierna;
		params[108] = dataCalcolo;
		params[109] = dataOdierna;
		params[110] = dataCalcolo;
		params[111] = dataOdierna;
		params[112] = dataCalcolo;
		params[113] = dataCalcolo;
		params[114] = dataOdierna;
		params[115] = dataOdierna;
		params[116] = dataOdierna;
		params[117] = dataOdierna;
		params[118] = dataCalcolo;
		params[119] = dataCalcolo;
		params[120] = dataCalcolo;
		params[121] = dataCalcolo;
		params[122] = dataCalcolo;
		params[123] = dataOdierna;
		params[124] = dataCalcolo;
		params[125] = dataOdierna;
		params[126] = dataOdierna;
		params[127] = dataCalcolo;
		params[128] = dataOdierna;
		params[129] = dataCalcolo;
		params[130] = dataCalcolo;
		params[131] = dataOdierna;
		params[132] = dataCalcolo;
		params[133] = dataOdierna;
		params[134] = dataOdierna;
		params[135] = dataCalcolo;
		params[136] = dataOdierna;
		params[137] = dataCalcolo;
		params[138] = dataOdierna;
		params[139] = dataOdierna;
		params[140] = dataCalcolo;
		params[141] = dataOdierna;
		params[142] = dataCalcolo;
		params[143] = dataCalcolo;
		params[144] = dataOdierna;
		params[145] = dataCalcolo;
		params[146] = dataCalcolo;
		params[147] = dataCalcolo;
		params[148] = dataCalcolo;
		params[149] = dataOdierna;
		params[150] = dataCalcolo;
		params[151] = dataOdierna;
		params[152] = dataCalcolo;
		params[153] = dataCalcolo;
		params[154] = dataOdierna;
		params[155] = dataOdierna;
		params[156] = dataOdierna;
		params[157] = dataOdierna;
		params[158] = dataCalcolo;
		params[159] = dataCalcolo;
		params[160] = dataOdierna;
		params[161] = dataCalcolo;
		params[162] = dataOdierna;
		params[163] = dataCalcolo;
		params[164] = dataCalcolo;
		params[165] = dataOdierna;
		params[166] = dataCalcolo;
		params[167] = dataOdierna;
		params[168] = dataOdierna;
		params[169] = dataCalcolo;
		params[170] = dataOdierna;
		params[171] = dataCalcolo;
		params[172] = dataOdierna;
		params[173] = dataOdierna;
		params[174] = dataOdierna;
		params[175] = dataCalcolo;
		params[176] = cdnLavoratore;

		return params;
	}

	public static void insertMigrazioneLogger(Object datInizioMov, Object codFiscLav, Object codFiscAz,
			Object codComunicazione, Object destinazione, Object txtMov, Object txtErrore) throws Exception {
		Object params[] = new Object[7];
		params[0] = datInizioMov;
		params[1] = codFiscLav;
		params[2] = codFiscAz;
		params[3] = codComunicazione;
		params[4] = destinazione;
		params[5] = txtMov;
		params[6] = txtErrore;
		Boolean res = (Boolean) QueryExecutor.executeQuery("INS_MIGRAZIONE_ERR", params, "INSERT", Values.DB_SIL_DATI);
		if (!res.booleanValue()) {
			throw new Exception("Inserimento TABELLA LOG errore migrazione fallito");
		}
	}

	public static Vector getLavoratoriRicalcolo() throws Exception {
		Object params[] = null;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_LAVORATORI_RICALCOLA_BATCH", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori che saranno trattati dal batch");
	}
	
	public static Vector getLavoratoriRicalcolo(int numeroLavoratoriDaProcessare) throws Exception {
		Object params[] = new Object[1];
		params[0] = numeroLavoratoriDaProcessare;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_LAVORATORI_RICALCOLA_BATCH_DA_PAGINA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori che saranno trattati dal batch");
	}
	
	public static Vector getMovimentiPre2008() throws Exception {
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_PRE_2008", null, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di appoggio ");
	}

	public static Vector getCessazioniOrfanePre2008() throws Exception {
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_CESSAZIONI_ORFANE_PRE_2008", null, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di appoggio ");
	}

	public static Vector getQueryByStatement(String statementCurr) throws Exception {
		SourceBean res = (SourceBean) QueryExecutor.executeQuery(statementCurr, null, "SELECT", Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i movimenti dalla tabella di appoggio ");
	}

	public static Vector getMovimentiFineSospensione(String dataRif) throws Exception {
		Object params[] = new Object[1];
		params[0] = dataRif;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTI_FINE_SOSPENSIONE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori che saranno trattati dal batch");
	}

	public static Vector getPeriodiIntermittenti(BigDecimal prgmovimento, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = { prgmovimento };
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("CERCA_PERIODI_LAVORATIVI_MOVIMENTO", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere i periodi di lavoro intermittenti");
		return row.getAttributeAsVector("ROW");
	}
}
