package it.eng.sil.util.amministrazione.impatti;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.UtilityNumGGTraDate;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * Classe per il batch dei tirocini senza contratto
 */
public class BatchSenzaContratto implements IBatchMDBConsumer {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BatchSenzaContratto.class.getName());
	public static final String LOG_NAME = "BATCH";
	public static final String LOG_ERR_NAME = "BATCH_ERR";
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;

	public BatchSenzaContratto(BatchObject batchObject) {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchTirociniSenzaContratto";
		// String strTime = (new SimpleDateFormat("HH_mm_ss")).format(new Date());
		String data = !this.batchObject.getParams()[0].equals("") ? this.batchObject.getParams()[0]
				: this.batchObject.getParams()[1];
		data = data.replace('/', '-');
		// data = data + "_" + strTime;
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
	}

	@Override
	public void execBatch() {
		try {
			this.setParametri();
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo che implementa la logica del batch per i tirocini senza contartto
	 */
	public void start() {// Da modificare
		boolean isRangeDate = false;
		// Lista dei lavoratori che hanno come S.O. A213 o A223(è un'ArrayList
		// di SourceBean)
		ArrayList lavoratori;
		StatoOccupazionaleBean statoOcc = null;
		SourceBean row = null;
		String msg = "";
		Vector tirocini = null;
		BigDecimal meseFine = new BigDecimal(0);
		BigDecimal annoFine = new BigDecimal(0);
		int count = 0;
		boolean hasTirociniFuturi = false;
		BigDecimal user = new BigDecimal(parametri[3]);
		String dataInizio = "";
		try {
			logBatch.writeLog("========= Avvio batch Tirocini senza contratto =========");
			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", user);
			requestContainer.setSessionContainer(sessionContainer);
			SourceBean s = new SourceBean("SERVICE_REQUEST");
			requestContainer.setServiceRequest(s);
			RequestContainer.setRequestContainer(requestContainer);

			// Stampa delle variabili java
			logBatch.writeLogVarJava(DateUtils.getNow());
			logBatch.writeLogVarJava("====== Variabili di configurazione java ======");
			Properties p = java.lang.System.getProperties();
			Enumeration e = p.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = p.getProperty(key);
				logBatch.writeLogVarJava(key + ": " + val + ";");
			}
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ======");

			// Recupero i lavoratori con codStatoOccupaz = A213 e A223
			ArrayList lavA213 = DBLoad.getLavoratoriDaCodStatoOccupaz("A213");
			ArrayList lavA223 = DBLoad.getLavoratoriDaCodStatoOccupaz("A223");

			lavoratori = new ArrayList(lavA213.size() + lavA223.size());
			for (int i = 0; i < lavA213.size(); i++) {
				count++;
				lavoratori.add(i, lavA213.get(i));
			}
			for (int j = 0; j < lavA223.size(); j++) {
				lavoratori.add(count, lavA223.get(j));
				count++;
			}

			String dataFine1 = parametri[0];
			String dataFine2 = parametri[1];

			if (!dataFine1.equals("") && !dataFine2.equals(""))
				isRangeDate = true;

			logBatch.writeLog("Sono stati letti i lavoratori con stato occupazionale A213 e A223.");
			logBatch.writeLog("Numero lavoratori letti: " + lavoratori.size());

			// per ognuno di questi
			logBatch.writeLog("Avvio batch sui lavoratori letti.");
			for (int i = 0; i < lavoratori.size(); i++) {
				TransactionQueryExecutor txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				try {
					row = (SourceBean) lavoratori.get(i);
					// Recupero i tirocini per il lavoratore che terminano nel
					// mese/anno specificati
					if (!isRangeDate) {
						if (dataFine2.length() == 10) {
							meseFine = new BigDecimal(dataFine2.substring(3, 5));
							annoFine = new BigDecimal(dataFine2.substring(6, 10));
						}
					}
					tirocini = DBLoad
							.getTirociniDaLavoratore((BigDecimal) row.getAttribute(MovimentoBean.DB_CDNLAVORATORE));
					if (tirocini.isEmpty()) {
						msg = "Il lavoratore: " + row.getAttribute("STRCOGNOME") + " " + row.getAttribute("STRNOME")
								+ " non ha tirocini associati.";
						logBatch.writeLog(msg);
					} else {
						msg = "Si sta elaborando il lavoratore: " + row.getAttribute("STRCOGNOME") + " "
								+ row.getAttribute("STRNOME");
						msg = msg + "; codice stato occupazionale: " + (String) row.getAttribute("CODSTATOOCCUPAZ");
						logBatch.writeLog(msg);
						for (int j = 0; j < tirocini.size(); j++) {
							SourceBean sb = (SourceBean) tirocini.get(j);
							if (sb.getAttribute("NUMMESEFINE") == null)
								continue;
							if ((new BigDecimal(sb.getAttribute("NUMMESEFINE").toString()).compareTo(meseFine) == 1)
									&& (new BigDecimal(sb.getAttribute("NUMANNOFINE").toString())
											.compareTo(annoFine) == 1))
								hasTirociniFuturi = true;
						}

						if (!hasTirociniFuturi) {
							// Aggiorno lo stato occupazionale
							GregorianCalendar gc = null;
							int gMax = 0;

							txExec.initTransaction();
							if (!isRangeDate) {
								gc = UtilityNumGGTraDate.trasformaInGregorian(dataFine2);
								gMax = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
								dataInizio = gMax + "/" + meseFine + "/" + annoFine;
							}
							String dataChiusura = (gMax - 1) + "/" + meseFine + "/" + annoFine;
							DBStore.chiudiStatoOcc(new BigDecimal(row.getAttribute("PRGSTATOOCCUPAZ").toString()),
									new BigDecimal(row.getAttribute("NUMKLOSTATOOCCUPAZ").toString()), dataChiusura,
									user, txExec);

							// Inserire nuovo stato Occupazionale a partire dal
							// prec.
							String codStatoOccupazRag = "";
							String codStatoOccupaz = "";
							if (row.getAttribute("CODSTATOOCCUPAZ").equals("A213")) {
								codStatoOccupazRag = "D";
								codStatoOccupaz = "A21";
							}
							if (row.getAttribute("CODSTATOOCCUPAZ").equals("A223")) {
								codStatoOccupazRag = "I";
								codStatoOccupaz = "A22";
							}

							SourceBean so = setNuovoStatoOccupaz(codStatoOccupaz, dataInizio, row);
							StatoOccupazionaleBean newStatoOcc = new StatoOccupazionaleBean(so);
							DBStore.creaNuovoStatoOccPerBatch(newStatoOcc, dataInizio, requestContainer, txExec);
						}
					}

					msg = "Il nuovo stato occupazionale è stato creato.";
					logBatch.writeLog(msg);
					txExec.commitTransaction();
				} catch (Exception ee) {
					txExec.rollBackTransaction();
					it.eng.sil.util.TraceWrapper.debug(_logger, "BatchSenzaContratto: start()", ee);

					logBatch.writeLog("ERRORE: " + ee.toString());
					logBatch.writeLog("Il batch è terminato con degli errori.");
				}
			} // end for
			logBatch.writeLog("Il batch BatchSenzaContratto ha finito e sta' per terminare.");
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "BatchSenzaContratto: start()", e);

			logBatch.writeLog("ERRORE: " + e.toString());
			logBatch.writeLog("Il batch è terminato con degli errori.");
		}
	}// end method

	/**
	 * Metodo per settare i parametri passati da linea di comando
	 */
	public void setParametri() {
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
	}// end method

	public SourceBean setNuovoStatoOccupaz(String codSO, String dataInizio, SourceBean oldStatoOccupaz)
			throws Exception {
		SourceBean so = new SourceBean("StatoOccupaz");
		BigDecimal cdnLavoratore = new BigDecimal(oldStatoOccupaz.getAttribute("CDNLAVORATORE").toString());
		SourceBean storiaInf = DBLoad.getLavStoriaInfAperta(cdnLavoratore);

		if ((storiaInf != null) && !storiaInf.getAttribute("ROW.PRGLAVSTORIAINF").equals("")) {
			so.setAttribute("PRGLAVSTORIAINF", storiaInf.getAttribute("ROW.PRGLAVSTORIAINF").toString());
		} else {
			so.setAttribute("PRGLAVSTORIAINF", "");
		}
		so.setAttribute("CDNLAVORATORE", cdnLavoratore);
		so.setAttribute("DATINIZIO", dataInizio);
		so.setAttribute("CODSTATOOCCUPAZ", codSO);
		so.setAttribute("CODMONOPROVENIENZA", "B");
		so.setAttribute("DATANZIANITADISOC", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATANZIANITADISOC"));
		if (oldStatoOccupaz.getAttribute("NUMANZIANITAPREC297") != null)
			so.setAttribute("NUMANZIANITAPREC297", oldStatoOccupaz.getAttribute("NUMANZIANITAPREC297"));
		else
			so.setAttribute("NUMMESISOSP", "");

		if (oldStatoOccupaz.getAttribute("NUMMESISOSP") != null)
			so.setAttribute("NUMMESISOSP", oldStatoOccupaz.getAttribute("NUMMESISOSP"));
		else
			so.setAttribute("NUMMESISOSP", "");

		so.setAttribute("DATCALCOLOANZIANITA",
				StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATCALCOLOANZIANITA"));
		so.setAttribute("DATCALCOLOMESISOSP",
				StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATCALCOLOMESISOSP"));
		if (oldStatoOccupaz.getAttribute("NUMREDDITO") != null)
			so.setAttribute("NUMREDDITO", oldStatoOccupaz.getAttribute("NUMREDDITO"));
		else
			so.setAttribute("NUMREDDITO", "");

		so.setAttribute("STRNUMATTO", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "STRNUMATTO"));
		so.setAttribute("DATATTO", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATATTO"));
		so.setAttribute("DATRICHREVISIONE", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATRICHREVISIONE"));
		so.setAttribute("CODSTATOATTO", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "CODSTATOATTO"));
		so.setAttribute("DATRICORSOGIURISDIZ",
				StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "DATRICORSOGIURISDIZ"));
		so.setAttribute("FLGINDENNIZZATO", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "FLGINDENNIZZATO"));
		so.setAttribute("FLGPENSIONATO", StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "FLGPENSIONATO"));
		so.setAttribute("DATFINE", "");
		so.setAttribute("CODMONOCALCOLOANZIANITAPREC297",
				StringUtils.getAttributeStrNotNull(oldStatoOccupaz, "CODMONOCALCOLOANZIANITAPREC297"));

		return so;
	}
}