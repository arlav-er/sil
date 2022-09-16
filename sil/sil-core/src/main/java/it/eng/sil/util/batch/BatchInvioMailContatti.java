package it.eng.sil.util.batch;

import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.mail.SendMail;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

/**
 * Batch per l'invio della mail ai contatti presenti in AG_CONTATTO:
 * <ul>
 * <li>recupera le informazioni dei contatti da AG_CONTATTO;</li>
 * <li>imposta ed invia la email ai contatti con indirizzo email nel campo STRCELLSMSINVIO;</li>
 * <li>aggiorna il campo FLGINVIATOSMS per indicare l'avvenuto invio della email.</li>
 * </ul>
 * 
 * @author uberti
 *
 */
public class BatchInvioMailContatti implements BatchRunnable, IBatchMDBConsumer {
	public static final int MOTIVO_CONTATTO_ISCRIZIONE = 101;
	public static final int MOTIVO_CONTATTO_DISDETTA = 102;
	public static final int TIPO_CONTATTO = 3;
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;
	private String mittenteMailIscrizione = null;
	private String oggettoMailIscrizione = null;
	private String mittenteMailDisdetta = null;
	private String destCCMailIscrizione = null;
	private String destCCMailDisdetta = null;
	private String oggettoMailDisdetta = null;
	private SendMail sendMailIscr = null;
	private SendMail sendMailDisdetta = null;

	private boolean errore = false;

	public BatchInvioMailContatti(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		// nome del file di log
		String nomeFile = File.separator + "BatchInviaMailContatti";

		String data = DateUtils.getNow();
		data = data.replace('/', '-');

		nomeFile = nomeFile + data + ".log";
		// inizializza il file di log
		logBatch = new LogBatch(nomeFile, dir);

		inizializzaInvioMail();
	}

	public void start() {
		try {
			if (logBatch == null) {
				System.err.println("Log non inizializzzato");
				// System.exit(1);
				return;
			}
			TransactionQueryExecutor txExec = null;
			SourceBean row = null;
			String destinatario = null;
			String testoMessaggio = null;
			Object prgContatto = null;
			BigDecimal numKlo = null;

			SourceBean sbConfig = getInfoConfigurazionePolo();
			String valore = StringUtils.getAttributeStrNotNull(sbConfig, "strvalore");
			if (valore.equals("S")) {
				logBatch.writeLog("=========== AVVIO BATCH PER INVIARE LA MAIL AI CONTATTI  ===========");

				RequestContainer requestContainer = new RequestContainer();
				SessionContainer sessionContainer = new SessionContainer(true);
				sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[0]));
				sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
				requestContainer.setSessionContainer(sessionContainer);

				// MOTIVO MAIL = ISCRIZIONE
				Vector rowsIscrizione = getContattiCig(MOTIVO_CONTATTO_ISCRIZIONE, TIPO_CONTATTO);
				int contRows = rowsIscrizione.size();
				logBatch.writeLog("=========== INIZIO INVIO " + contRows + " MAIL DI ISCRIZIONI  ===========");
				String mailCCIscr = getMittenteMailIscrizione();
				String mailCCNIscr = getDestCCMailIscrizione();
				for (int i = 0; i < contRows; i++) {
					try {
						this.errore = false;
						txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						txExec.initTransaction();
						row = (SourceBean) rowsIscrizione.get(i);
						prgContatto = row.getAttribute("PRGCONTATTO");
						destinatario = StringUtils.getAttributeStrNotNull(row, "STRCELLSMSINVIO");
						testoMessaggio = StringUtils.getAttributeStrNotNull(row, "TXTCONTATTO");
						numKlo = new BigDecimal(row.getAttribute("numKloContatto").toString());
						logBatch.writeLog("Si sta elaborando il contatto con prg:" + prgContatto.toString());
						// invio mail al destinatario
						if (!destinatario.equals("")) {
							sendMailIscr.setToRecipient(destinatario);
							sendMailIscr.setBody(testoMessaggio);
							if (!mailCCIscr.equals("")) {
								sendMailIscr.setCcRecipient(mailCCIscr);
							}
							if (!mailCCNIscr.equals("")) {
								sendMailIscr.setBccRecipient(mailCCNIscr);
							}
							sendMailIscr.send();
						}
						logBatch.writeLog("Invio mail con destinatario " + destinatario + " avvenuto con successo");
						aggiornaContattoInvioMail("S", prgContatto, numKlo, txExec);
						logBatch.writeLog(
								"Elaborazione contatto con prg:" + prgContatto.toString() + " avvenuta con successo");
					} catch (Exception invioMail) {
						try {
							if (txExec != null) {
								aggiornaContattoInvioMail("N", prgContatto, numKlo, txExec);
							}
						} catch (Exception e) {
							this.errore = true;
						}
						logBatch.writeLog("L'elaborazione invio mail del contatto con prg = " + prgContatto.toString()
								+ " e destinatario = " + destinatario + " è terminata con degli errori.");
						logBatch.writeLog("ERRORE: " + invioMail.getMessage());
					} finally {
						if (txExec != null) {
							if (!this.errore) {
								txExec.commitTransaction();
							} else {
								txExec.rollBackTransaction();
							}
							txExec = null;
						}
					}
				}
				logBatch.writeLog("=========== FINE INVIO MAIL DI ISCRIZIONI  ===========");

				// MOTIVO MAIL = DISDETTA
				Vector rowsDisdetta = getContattiCig(MOTIVO_CONTATTO_DISDETTA, TIPO_CONTATTO);
				contRows = rowsDisdetta.size();
				logBatch.writeLog("=========== INIZIO INVIO " + contRows + " MAIL DI DISDETTA  ===========");
				String mailCCDisdetta = getMittenteMailDisdetta();
				String mailCCNDisdetta = getDestCCMailDisdetta();
				for (int i = 0; i < contRows; i++) {
					try {
						this.errore = false;
						txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						txExec.initTransaction();
						row = (SourceBean) rowsDisdetta.get(i);
						prgContatto = row.getAttribute("PRGCONTATTO");
						destinatario = StringUtils.getAttributeStrNotNull(row, "STRCELLSMSINVIO");
						testoMessaggio = StringUtils.getAttributeStrNotNull(row, "TXTCONTATTO");
						numKlo = new BigDecimal(row.getAttribute("numKloContatto").toString());
						logBatch.writeLog("Si sta elaborando il contatto con prg:" + prgContatto.toString());
						// invio mail al destinatario
						if (!destinatario.equals("")) {
							sendMailDisdetta.setToRecipient(destinatario);
							sendMailDisdetta.setBody(testoMessaggio);
							if (!mailCCDisdetta.equals("")) {
								sendMailDisdetta.setCcRecipient(mailCCDisdetta);
							}
							if (!mailCCNDisdetta.equals("")) {
								sendMailDisdetta.setBccRecipient(mailCCNDisdetta);
							}
							sendMailDisdetta.send();
						}
						logBatch.writeLog("Invio mail con destinatario " + destinatario + " avvenuto con successo");
						aggiornaContattoInvioMail("S", prgContatto, numKlo, txExec);
						logBatch.writeLog(
								"Elaborazione contatto con prg:" + prgContatto.toString() + " avvenuta con successo");
					}

					catch (Exception invioMail) {
						try {
							if (txExec != null) {
								aggiornaContattoInvioMail("N", prgContatto, numKlo, txExec);
							}
						} catch (Exception e) {
							this.errore = true;
						}
						logBatch.writeLog("L'elaborazione invio mail del contatto con prg = " + prgContatto.toString()
								+ " e destinatario = " + destinatario + " è terminata con degli errori.");
						logBatch.writeLog("ERRORE: " + invioMail.getMessage());
					} finally {
						if (txExec != null) {
							if (!this.errore) {
								txExec.commitTransaction();
							} else {
								txExec.rollBackTransaction();
							}
							txExec = null;
						}
					}
				}
				logBatch.writeLog("=========== FINE INVIO MAIL DI DISDETTA  ===========");

				logBatch.writeLog("====================================================");
				logBatch.writeLog("=========== FINE BATCH PER INVIARE LA MAIL AI CONTATTI  ===========");
			} else {
				logBatch.writeLog("=========== GESTIONE INVIO MAIL AI CONTATTI NON CONFIGURATA ===========");
			}
		} catch (SourceBeanException e1) {
			e1.printStackTrace();
			logBatch.writeLog("=========== EXCEPTION:  \n" + e1 + "===========");
		} catch (Exception e2) {
			e2.printStackTrace();
			logBatch.writeLog("=========== \n" + e2 + "  ===========");
		}
	}

	/**
	 * Imposta i parametri del batch.
	 * 
	 * @param args
	 *            array di parametri.
	 */
	public void setParametri() {
		parametri = new String[1];
		parametri[0] = this.batchObject.getParams()[0]; // user
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

	public void setMittenteMailIscrizione(String val) {
		this.mittenteMailIscrizione = val;
	}

	public void setOggettoMailIscrizione(String val) {
		this.oggettoMailIscrizione = val;
	}

	public void setMittenteMailDisdetta(String val) {
		this.mittenteMailDisdetta = val;
	}

	public void setOggettoMailDisdetta(String val) {
		this.oggettoMailDisdetta = val;
	}

	public String getMittenteMailIscrizione() {
		return this.mittenteMailIscrizione;
	}

	public String getOggettoMailIscrizione() {
		return this.oggettoMailIscrizione;
	}

	public String getMittenteMailDisdetta() {
		return this.mittenteMailDisdetta;
	}

	public String getOggettoMailDisdetta() {
		return this.oggettoMailDisdetta;
	}

	public void setDestCCMailIscrizione(String val) {
		this.destCCMailIscrizione = val;
	}

	public void setDestCCMailDisdetta(String val) {
		this.destCCMailDisdetta = val;
	}

	public String getDestCCMailIscrizione() {
		return this.destCCMailIscrizione;
	}

	public String getDestCCMailDisdetta() {
		return this.destCCMailDisdetta;
	}

	public Vector getContattiCig(int motivoContatto, int tipoContatto) throws Exception {
		Object params[] = new Object[2];
		params[0] = motivoContatto;
		params[1] = tipoContatto;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_CONTATTI_ISCR_DISDETTA_CIG", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i contatti per invio mail disdetta");
	}

	public SourceBean getInfoConfigMail(String codTipoMail) throws Exception {
		Object params[] = { codTipoMail };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_MAIL_CIG", params, "SELECT",
				Values.DB_SIL_DATI);
		return (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
	}

	public void aggiornaContattoInvioMail(String flgInviato, Object prg, BigDecimal numKlo,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object cdnUser = parametri[0];
		Object params[] = new Object[4];
		params[0] = flgInviato;
		params[1] = cdnUser;
		params[2] = newNumKlo;
		params[3] = prg;

		Boolean res = (Boolean) transExec.executeQuery("UPDATE_CONTATTO_PER_INVIO_MAIL", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiornare il contatto con prgMovimento = " + (BigDecimal) prg);
	}

	public SourceBean getInfoConfigurazionePolo() throws Exception {
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CONFIGURAZIONE_POLO_CIG", null, "SELECT",
				Values.DB_SIL_DATI);
		return (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
	}

	public void inizializzaInvioMail() throws Exception {
		SourceBean mailCOCIGISC = getInfoConfigMail("COCIGISC");
		SourceBean mailCOCIGDIS = getInfoConfigMail("COCIGDIS");

		setMittenteMailIscrizione(StringUtils.getAttributeStrNotNull(mailCOCIGISC, "STREMAILMITTENTE"));
		setOggettoMailIscrizione(StringUtils.getAttributeStrNotNull(mailCOCIGISC, "STROGGETTO"));

		setMittenteMailDisdetta(StringUtils.getAttributeStrNotNull(mailCOCIGDIS, "STREMAILMITTENTE"));
		setOggettoMailDisdetta(StringUtils.getAttributeStrNotNull(mailCOCIGDIS, "STROGGETTO"));

		setDestCCMailIscrizione(StringUtils.getAttributeStrNotNull(mailCOCIGISC, "STRCORPOEMAIL"));
		setDestCCMailDisdetta(StringUtils.getAttributeStrNotNull(mailCOCIGDIS, "STRCORPOEMAIL"));

		sendMailIscr = new SendMail();
		sendMailDisdetta = new SendMail();

		sendMailIscr.setSMTPServer(StringUtils.getAttributeStrNotNull(mailCOCIGISC, "STRSMTPSERVER"));
		sendMailIscr.setFromRecipient(getMittenteMailIscrizione());
		sendMailIscr.setSubject(getOggettoMailIscrizione());

		sendMailDisdetta.setSMTPServer(StringUtils.getAttributeStrNotNull(mailCOCIGDIS, "STRSMTPSERVER"));
		sendMailDisdetta.setFromRecipient(getMittenteMailDisdetta());
		sendMailDisdetta.setSubject(getOggettoMailDisdetta());
	}

}
