/*
 * Creato il 21-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.batch.BatchRunnable;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * @author olivieri
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class BatchInizioValiditaCurriculum implements BatchRunnable, IBatchMDBConsumer {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(BatchInizioValiditaCurriculum.class.getName());

	public static final String APP_NAME = "BatchInizioValiditaCurriculum";
	private BatchObject batchObject;
	private String[] parametri;
	private boolean isLanciatoDaBatch = true;

	public BatchInizioValiditaCurriculum(BatchObject batchObject) {
		this.batchObject = batchObject;
	}

	public void start() {
		ArrayList<SourceBean> lavoratori;
		SourceBean row = null;
		String msg = "";
		int count = 0;
		BigDecimal user = null;
		String sysdate = "";

		PrintWriter pw = null;
		try {
			if (isLanciatoDaBatch) {
				user = new BigDecimal("1");
				sysdate = DateUtils.getNow();
			} else {
				user = new BigDecimal(parametri[3]);
				sysdate = parametri[0];
			}

			String dir = ConfigSingleton.getLogBatchPath();
			String nomeFile = dir + File.separator + APP_NAME;

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			String oggi = df.format(new Date());
			oggi = oggi.replace('/', '-');
			nomeFile = nomeFile + "_" + oggi + ".log";

			pw = new PrintWriter(new FileOutputStream(new File(nomeFile), true));

			pw.println("========= Avvio batch Inizio validità curriculum =========");

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", user);
			requestContainer.setSessionContainer(sessionContainer);
			SourceBean s = new SourceBean("SERVICE_REQUEST");
			requestContainer.setServiceRequest(s);
			RequestContainer.setRequestContainer(requestContainer);

			// Stampa delle variabili java

			_logger.debug("====== Variabili di configurazione java ======");

			Properties p = java.lang.System.getProperties();
			Enumeration<?> e = p.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = p.getProperty(key);
				_logger.debug(key + ": " + val + ";");

			}
			_logger.debug("====== Fine sezione di configurazione ======");

			ArrayList<SourceBean> lavCurrDaValidare = getCurriculumDaValidare("DL", sysdate);

			lavoratori = new ArrayList<SourceBean>(lavCurrDaValidare.size());
			for (int i = 0; i < lavCurrDaValidare.size(); i++) {
				count++;
				lavoratori.add(i, lavCurrDaValidare.get(i));
			}

			pw.println("Sono stati letti i lavoratori con validità di curriculum da aprire e tipologia DL.");
			pw.println("Numero lavoratori letti: " + lavoratori.size());

			// per ognuno di questi
			pw.println("Avvio batch sui lavoratori letti.");

			for (int i = 0; i < lavoratori.size(); i++) {
				StatoOccupazionaleBean statoOcc = null;
				StatoOccupazionaleBean statoOccPrec = null;
				Vector statiOccupazionali = null;
				TransactionQueryExecutor txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				txExec.initTransaction();
				try {
					row = (SourceBean) lavoratori.get(i);

					BigDecimal cdnLavoratore = (BigDecimal) row.getAttribute("CDNLAVORATORE");
					msg = "Lavoratore con identificativo " + cdnLavoratore + " : Cognome "
							+ row.getAttribute("STRCOGNOME") + " Nome " + row.getAttribute("STRNOME");
					msg = msg + " Codice fiscale : " + row.getAttribute("STRCODICEFISCALE") + " Data di nascita : "
							+ row.getAttribute("DATNASC");
					pw.println(msg);

					// statoOcc = DBLoad.getStatoOccupazionale(cdnLavoratore,
					// txExec);

					statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, txExec);
					int size = statiOccupazionali.size();
					statoOcc = new StatoOccupazionaleBean((SourceBean) statiOccupazionali.get(size - 1));
					if (size > 1) {
						statoOccPrec = new StatoOccupazionaleBean((SourceBean) statiOccupazionali.get(size - 2));
					}
					msg = "Stato occupazionale attuale: " + statoOcc.getDescrizioneCompleta();
					pw.println(msg);

					if (statoOcc.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
						if (DateUtils.compare(statoOcc.getDataInizio(), sysdate) == 0) {
							setAggiornaStatoOccupazionale(statoOcc, statoOccPrec, user, txExec);
						} else {
							setNuovoStatoOccupazionale(statoOcc, user, sysdate, txExec);
						}
						statoOcc.setStatoOccupazionale(StatoOccupazionaleBean.A1);
						msg = "Il nuovo stato occupazionale è: " + statoOcc.getDescrizioneCompleta();
						pw.println(msg);

					}
					txExec.commitTransaction();
				} catch (ArrayIndexOutOfBoundsException ee) {
					txExec.rollBackTransaction();
					msg = "Il lavoratore è privo di stato occupazionale!!!";
					_logger.fatal("ERRORE: " + msg);

				} catch (Exception ee) {
					txExec.rollBackTransaction();
					// TracerSingleton.log(it.eng.sil.Values.APP_NAME,
					// TracerSingleton.CRITICAL, "BatchInizioValiditaCurriculum:
					// start()", ee);
					_logger.fatal("ERRORE: " + ee.getMessage());

				} finally {
					Utils.releaseResources(txExec.getDataConnection(), null, null);

				}
			} // end for
			pw.println("Il batch BatchInizioValiditaCurriculum ha concluso il lavoro e sta per terminare.");
			pw.println("========= Terminato batch  Inizio validità curriculum =========");

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "BatchInizioValiditaCurriculum: start()", e);
			_logger.fatal("ERRORE: ", e);
			pw.println("Il batch è terminato con degli errori.");

		} finally {
			if (pw != null)
				pw.close();
		}
	}

	public void setParametri() {
		this.isLanciatoDaBatch = !this.batchObject.getAvvioManuale();

		if (this.batchObject.getAvvioManuale()) {
			parametri = new String[4];

			String[] args = this.batchObject.getParams();
			if (args.length > 3) {
				parametri[0] = args[0];
				parametri[1] = args[1];
				parametri[2] = args[2];
				parametri[3] = args[3];// user //Se avviati da .bat impostarlo ad 1
			} else {
				parametri[0] = "";
				parametri[1] = args[0];
				parametri[2] = args[1];
				parametri[3] = args[2];// user //Se avviati da .bat impostarlo ad 1
			}
		}
	}// end method

	private void setNuovoStatoOccupazionale(StatoOccupazionaleBean statoOcc, BigDecimal user, String dataInizio,
			TransactionQueryExecutor txExec) throws Exception {
		Object params[] = new Object[15];

		params[0] = statoOcc.getCdnLavoratore();
		params[1] = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A1); // stato
																				// occupazionale
		params[2] = dataInizio;

		if (DateUtils.compare((String) params[2], statoOcc.getDataInizio()) <= 0)
			throw new Exception("Lavoratore con identificativo " + statoOcc.getCdnLavoratore()
					+ " non modificato: la data di inizio del nuovo stato occupazionale è antecedente alla data inizio del vecchio.");
		params[3] = null; // data fine
		params[4] = statoOcc.getDataAnzianita();
		params[5] = statoOcc.getIndennizzato();
		params[6] = "A"; // codmonoprovenienza
		params[7] = statoOcc.getNumAnzianitaPrec297();
		params[8] = null; // note
		params[9] = user;
		params[10] = user;
		params[11] = statoOcc.getPensionato();
		params[12] = statoOcc.getNumMesiSosp();
		params[13] = statoOcc.getDataCalcoloMesiSosp();
		params[14] = statoOcc.getDataCalcoloAnzianita();

		Boolean res = (Boolean) txExec.executeQuery("INS_STATO_OCCUPAZ", params, "INSERT");

		if (!res.booleanValue())
			throw new Exception(
					"Impossibile modificare lo stato occupazionale del lavoratore " + statoOcc.getCdnLavoratore());
	}

	private void setAggiornaStatoOccupazionale(StatoOccupazionaleBean statoOcc, StatoOccupazionaleBean statoOccPrec,
			BigDecimal user, TransactionQueryExecutor txExec) throws Exception {
		Object params1[] = new Object[1];
		Object params2[] = new Object[6];
		Boolean res = false;

		params2[0] = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A1); // stato
																				// occupazionale
		params2[1] = "A"; // codmonoprovenienza
		params2[2] = user;
		params2[3] = statoOcc.getNumKlo().add(new BigDecimal("1"));
		params2[4] = null;
		params2[5] = statoOcc.getPrgStatoOccupaz();

		if ((statoOccPrec != null) && (statoOccPrec.getStatoOccupazionale() == StatoOccupazionaleBean.A1)) {
			params1[0] = statoOcc.getPrgStatoOccupaz();
			res = (Boolean) txExec.executeQuery("DELETE_STATO_OCCUPAZ_DA_VALIDITA_CURR", params1, "DELETE");
			if (!res.booleanValue())
				throw new Exception(
						"Impossibile modificare lo stato occupazionale del lavoratore " + statoOcc.getCdnLavoratore());

			params2[1] = statoOccPrec.getCodMonoProvenienza();
			params2[3] = statoOccPrec.getNumKlo().add(new BigDecimal("1"));
			params2[5] = statoOccPrec.getPrgStatoOccupaz();
		}

		res = (Boolean) txExec.executeQuery("UPDATE_STATO_OCCUPAZ_DA_VALIDITA_CURR", params2, "UPDATE");

		if (!res.booleanValue())
			throw new Exception(
					"Impossibile modificare lo stato occupazionale del lavoratore " + statoOcc.getCdnLavoratore());
	}

	private ArrayList<SourceBean> getCurriculumDaValidare(String codTipoValidita, String dataInizio) throws Exception {
		ArrayList<SourceBean> lavoratori;
		Object params[] = new Object[4];

		params[0] = codTipoValidita;
		params[1] = dataInizio;
		params[2] = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.B);
		params[3] = dataInizio;

		Vector rows = null;
		SourceBean row = null;

		row = (SourceBean) QueryExecutor.executeQuery("GET_CURRICULUM_DA_VALIDARE", params, "SELECT",
				Values.DB_SIL_DATI);

		rows = row.getAttributeAsVector("ROW");
		if ((rows != null) && !rows.isEmpty()) {
			lavoratori = new ArrayList<SourceBean>(rows.size());
			for (int i = 0; i < rows.size(); i++) {
				row = (SourceBean) rows.get(i);
				lavoratori.add(i, row);
			}
		} else
			lavoratori = new ArrayList<SourceBean>(0);

		return lavoratori;
	}

	@Override
	public void execBatch() {
		try {
			this.setParametri(); // per i test da avviaBatch.jsp
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}