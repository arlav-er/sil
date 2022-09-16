package it.eng.sil.util.batch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.myportal.InviaVacancy;

/**
 * Batch per l'invio richieste personale
 * 
 * @author
 *
 */
public class BatchInvioRichiestePersonale implements BatchRunnable {

	private static Logger logger = Logger.getLogger(BatchInvioRichiestePersonale.class.getName());

	public static void main(String[] args) {
		BatchInvioRichiestePersonale app = null;
		try {
			app = new BatchInvioRichiestePersonale();
			if (args.length == 0) {
				app.start();
			} else {
				String paramAvvio = args[0];
				if ("avvio".equalsIgnoreCase(paramAvvio)) {
					app.start(paramAvvio);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
		}
	}

	public BatchInvioRichiestePersonale() {

	}

	@SuppressWarnings("unchecked")
	public void start(String paramAvvio) {

		logger.debug("=========== Avvio Batch ===========");
		logger.info("INVIO RICHIESTE PERSONALE PREAVVIO: Avvio Batch", null);

		Context ctx;
		Connection conn = null;
		CallableStatement command = null;
		String prgListaVacancy = null;
		String msgLog = "";

		try {
			ctx = new InitialContext();
			Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
			if (objs instanceof DataSource) {
				DataSource ds = (DataSource) objs;
				conn = ds.getConnection();

				String statement = "{ call ?:= PG_PORTALE.getVacancy }";
				command = conn.prepareCall(statement);

				command.registerOutParameter(1, Types.VARCHAR);

				command.executeQuery();
				byte[] risultato = command.getBytes(1);

				prgListaVacancy = new String(risultato);

				if (prgListaVacancy != null && !prgListaVacancy.equals("")) {

					String[] vettPrg = prgListaVacancy.split("-");

					int numTotRichieste = vettPrg.length;
					msgLog = "INVIO RICHIESTE PERSONALE PREAVVIO: totali da inviare: " + numTotRichieste;
					logger.info(msgLog, null);

					for (int i = 0; i < numTotRichieste; i++) {

						InviaVacancy iv = new InviaVacancy();
						String prgRich = vettPrg[i];
						iv.batchServiceAvvio(prgRich, conn);

						int numcur = i + 1;

						msgLog = "INVIO RICHIESTE PERSONALE PREAVVIO: prgrichiestaaz=" + prgRich + " - fine numero "
								+ numcur + " di " + numTotRichieste;
						logger.info(msgLog, null);
					}
				}
			} else {
				logger.info("ERRORE DATASOURCE", null);
			}

		} catch (Exception e1) {
			msgLog = "ERRORE NEL BATCH INVIO RICHIESTE PERSONALE PREAVVIO";
			logger.error(msgLog, e1);
		} finally {
			try {
				conn.close();
			} catch (Exception e1) {
				msgLog = "ERRORE CHIUSURA CONNESSIONE";
				logger.error(msgLog, e1);
			}
		}

		msgLog = "INVIO RICHIESTE PERSONALE PREAVVIO: Fine Batch";
		logger.info(msgLog, null);
	}

	@SuppressWarnings("unchecked")
	public void start() {

		logger.debug("=========== Avvio Batch ===========");
		logger.info("INVIO RICHIESTE PERSONALE: Avvio Batch", null);

		Context ctx;
		Connection conn = null;
		CallableStatement command = null;
		String prgListaVacancy = null;
		String msgLog = "";
		try {
			ctx = new InitialContext();
			Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
			if (objs instanceof DataSource) {
				DataSource ds = (DataSource) objs;

				conn = ds.getConnection();

				String statement = "{ call ?:= PG_PORTALE.getVacancyModify }";
				command = conn.prepareCall(statement);

				command.registerOutParameter(1, Types.VARCHAR);

				command.executeQuery();
				byte[] risultato = command.getBytes(1);

				prgListaVacancy = new String(risultato);

				if (prgListaVacancy != null && !prgListaVacancy.equals("")) {

					String[] vettPrg = prgListaVacancy.split("-");

					int numTotRichieste = vettPrg.length;
					msgLog = "INVIO RICHIESTE PERSONALE: totali da inviare: " + numTotRichieste;
					logger.info(msgLog, null);

					for (int i = 0; i < numTotRichieste; i++) {

						InviaVacancy iv = new InviaVacancy();
						String prgRich = vettPrg[i];
						iv.batchServiceAvvio(prgRich, conn);

						int numcur = i + 1;
						msgLog = "INVIO RICHIESTE PERSONALE: prgrichiestaaz=" + prgRich + " - fine numero " + numcur
								+ " di " + numTotRichieste;
						logger.info(msgLog, null);
					}
				}

				String statementUpd = "{ call ? := PG_PORTALE.updateDATA_MYP_VAC }";
				command = conn.prepareCall(statementUpd);

				command.registerOutParameter(1, Types.NUMERIC);

				command.executeQuery();
			} else {
				logger.info("ERRORE DATASOURCE", null);
			}

		} catch (Exception e1) {
			msgLog = "ERRORE NEL BATCH INVIO RICHIESTE PERSONALE";
			logger.error(msgLog, e1);
		} finally {
			try {
				conn.close();
			} catch (Exception e1) {
				msgLog = "ERRORE CHIUSURA CONNESSIONE";
				logger.error(msgLog, e1);
			}
		}

		msgLog = "INVIO RICHIESTE PERSONALE: Fine Batch";
		logger.info(msgLog, null);
	}

}
