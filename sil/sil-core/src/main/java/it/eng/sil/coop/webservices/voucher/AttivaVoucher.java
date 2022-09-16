package it.eng.sil.coop.webservices.voucher;

import java.math.BigDecimal;

import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.module.voucher.Voucher;

public class AttivaVoucher implements AttivaVoucherInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AttivaVoucher.class.getName());
	private static final BigDecimal userSP = new BigDecimal("150");

	public String attivaVoucher(String cfEnte, String sedeEnte, String cfLavoratore, String codiceAttivazione)
			throws java.rmi.RemoteException, Exception {
		TransactionQueryExecutor transExec = null;
		DataConnection conn = null;
		String dataAttivazione = DateUtils.getNow();
		_logger.info("Il servizio di attivazione voucher e' stato chiamato");

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			Voucher vch = new Voucher();
			conn = transExec.getDataConnection();

			String esito = vch.attivaAllaData(conn, cfEnte, sedeEnte, cfLavoratore, codiceAttivazione, userSP,
					dataAttivazione);

			transExec.commitTransaction();

			return esito;
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "AttivaVoucher:attivaVoucher", e);
			return Properties.ERRORE_GENERICO_WS;
		} finally {
			Utils.releaseResources(conn, null, null);
		}

	}

}
