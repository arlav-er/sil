/*
 * Creato il 16-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.sms;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.agenda.appuntamento.Constants;
import it.eng.sil.security.User;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutorObject;

/**
 * @author Girotti, Giuliani
 * 
 * Questa classe si occupa della creazione del contatto per l'invio dell'SMS. Il
 * metodo che si occupa della crazione vera e propria del contatto e dell'SMS e'
 * il metodo insert() che può essere utilizzato in transazione o meno. Tale
 * metodo e' volutamente privato, per cui chi dovesse implementare l'invio
 * dell'SMS deve creare un altro metodo (es.: creaContatto() ) che sara' poi reso
 * pubblic per essere utilizzato da tutti. In questo altro metodo
 * "creaContatto()" devono essere gestite le valorizzazioni dei campi da
 * inserire nel contatto e l'invio vero e proprio dell'sms tramite la classe
 * Gateway.
 * 
 */

public class ContattoSMS {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ContattoSMS.class.getName());
	private String className = this.getClass().getName();

	// *************variabili per inserimento in
	// AG_CONTATTO**********************
	private BigDecimal cdnlavoratore = null;
	private String codcpicontatto = "";
	private String datcontatto = "";
	private String stroracontatto = "";
	private BigDecimal prgspicontatto = null;
	private String txtcontatto = "";
	private String flgricontattare = "";
	private String strio = "O";
	private String datentroil = "";
	private BigDecimal prgmotcontatto = null;
	private BigDecimal prgtipocontatto = new BigDecimal("5");
	private String prgeffettocontatto = "";
	private String prgazienda = "";
	private String prgunita = "";
	private String cdnutins = "";
	private String cdnutmod = "";
	private BigDecimal coddisponibilitarosa = null;
	private String flginviatosms = "";
	private String strcellsmsinvio = "";
	// *************variabili per inserimento in
	// DO_DISPONIBILITA**********************
	private String prgrichiestaorig = "";
	private String codstatolavprec = "";
	private String coddisponibilitarosaGrezza = "F";
	private BigDecimal numcontanonrintracciato = null;
	private String coddisponibiltarosaprec = "";
	private String prgTipoRosa = "";
	private int maxLength = 160;
	
	private String flagInvioSMS = "";
	private String codServizio = "";

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	Sms sms = null;

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

//	private List<BigDecimal> prgcontattiaggiuntisudb = new ArrayList<BigDecimal>();
	
	
	/**
	 *
	 * inserisce i messaggi all'interno del gateway
	 * e salva le entry all'interno della tabella AG_CONTATTO
	 * */
	private int insert(Gateway gway) throws Exception {
		_logger.debug("::insert() CALLED...");

		int errorCode = 0;
		TransactionQueryExecutor txExecutor = null;
		try {
			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			errorCode = insert(txExecutor, gway);
			if (errorCode == 0 || errorCode == SmsFormatException.ERR_TESTO_TROPPO_LUNGO) {
				txExecutor.commitTransaction();
			} else {
				txExecutor.rollBackTransaction();
			}
		} catch (Exception e) {
			txExecutor.rollBackTransaction();
			throw e;
		}
		return errorCode;
	}
	
	private int insert(Gateway gway, TransactionQueryExecutor txExecutor) throws Exception {
		_logger.debug("::insert() CALLED...");

		int errorCode = 0;
		try {
			errorCode = insertSMS(txExecutor, gway);
		} catch (Exception e) {
			throw e;
		}
		return errorCode;
	}

	/**salva il contatto con
	 * con flginviatosms = 'S'
	 * @author rodi
	 */
	private int insert(TransactionQueryExecutor txExecutor, Gateway gway) throws Exception {

		Sms sms = null;

		int codeError = 0;

		BigDecimal prgContatto = null;
		Object[] obj = new Object[13];
		// Imposto gli oggetti per l'inserimento in DO_DISPONIBILITA
		Object[] obj2 = new Object[8];

		try {// Eseguo l'inserimento

			// Recupero il progressivo da inserire nella AG_CONTATTO
			SourceBean rowsD = (SourceBean) txExecutor.executeQuery("NEXT_S_AG_CONTATTO", null, "SELECT");
			prgContatto = (BigDecimal) rowsD.getAttribute("ROW.KEY");

			// Imposto i parametri
			obj[0] = prgContatto;
			obj[1] = codcpicontatto;
			obj[2] = datcontatto;
			obj[3] = stroracontatto;
			obj[4] = prgspicontatto;
			obj[5] = txtcontatto;
			obj[6] = strio;
			obj[7] = prgtipocontatto;
			obj[8] = cdnlavoratore;
			obj[9] = cdnutins;
			obj[10] = cdnutmod;
			obj[11] = strcellsmsinvio;
			obj[12] = "N";

			// Imposto i parametri per l'inserimento in DO_DISPONIBILITA dove il
			// tipo di SMS = ROSAGREZ
			obj2[0] = prgrichiestaorig;
			obj2[1] = cdnlavoratore;
			obj2[2] = coddisponibilitarosaGrezza;
			obj2[3] = codstatolavprec;
			obj2[4] = cdnutins;
			obj2[5] = cdnutmod;
			obj2[6] = numcontanonrintracciato;
			obj2[7] = coddisponibiltarosaprec;

			/*
			 * condiziono l'esecuzione delle query a seconda della tipologia di
			 * ROSA (GREZZA = 2) ciò si rende necessario per il corretto
			 * funzionamento dell'inserimento del contatto per le altre
			 * tipologia di SMS (PROMAPPU , INVIOROSA)
			 */
			if (!prgTipoRosa.equals("2")) {
				txExecutor.executeQuery("INSERT_AG_CONTATTO", obj, "INSERT");
			} else {
				txExecutor.executeQuery("INSERT_AG_CONTATTO", obj, "INSERT");
				txExecutor.executeQuery("INSERT_DO_DISPONIBILITA", obj2, "INSERT");
			}

		} catch (EMFInternalError e) {
			return MessageCodes.SMS.INSERT_CONTATTO_SMS_FALLITO;
		}

		try {
			sms = new Sms(strcellsmsinvio, txtcontatto, maxLength);
			
			_logger.debug("ContattoSMS Testo SMS: "+txtcontatto);
			
			gway.addSms(sms);

		} catch (SmsFormatException e) {
			if (e.getErrorCode() == SmsFormatException.ERR_TESTO_TROPPO_LUNGO) {
				// NOTA: non si esegue il rollBack
				// Il contatto viene comunque inserito ma con flginviatosms='N'
				/*
				 * Il flag a N viene inseerito nella prima query di insert try {
				 * obj = new Object[2]; obj[0] = "N"; obj[1] = prgContatto;
				 * 
				 * txExecutor.executeQuery("UPDATE_CONTATTO_SMS_INVIATO",obj,"UPDATE");
				 *  } catch (EMFInternalError e1) { //Se fallisce
				 * l'aggiornamento del contatto allora esegua anche il rollback
				 * txExecutor.rollBackTransaction(); }
				 */
				return MessageCodes.SMS.TESTO_TROPPO_LUNGO;

			} else if (e.getErrorCode() == SmsFormatException.ERR_N_CELL_NON_VALIDO) {
				return MessageCodes.SMS.CELL_NON_VALIDO;

			}
		}

		
		//l'SMS non a' stato aggiunto al gateway. 
		//E' solo stato accodato. GLi SMS inviati verranno aggiornati sucessivamente.
		//TODO
		try {// Se arrivo fino a qui considero l'SMS come inviato:
			// aggiorno il flag nella tabella

			obj = new Object[2];
			obj[0] = "S";
			obj[1] = prgContatto;

			txExecutor.executeQuery("UPDATE_CONTATTO_SMS_INVIATO", obj, "UPDATE");

		} catch (EMFInternalError e) {
			//txExecutor.rollBackTransaction();
			return MessageCodes.SMS.ERRORE_UPDATE_AG_CONTATTO;
			//return codeError;
		}

		return codeError;
	}
	
	private int insertSMS(TransactionQueryExecutor txExecutor, Gateway gway) throws Exception {
		Sms sms = null;
		int codeError = 0;
		BigDecimal prgContatto = null;
		Object[] obj = new Object[14];

		try {
			// Recupero il progressivo da inserire nella AG_CONTATTO
			SourceBean rowsD = (SourceBean) txExecutor.executeQuery("NEXT_S_AG_CONTATTO", null, "SELECT");
			prgContatto = (BigDecimal) rowsD.getAttribute("ROW.KEY");

			// Imposto i parametri
			obj[0] = prgContatto;
			obj[1] = codcpicontatto;
			obj[2] = datcontatto;
			obj[3] = stroracontatto;
			obj[4] = prgspicontatto;
			obj[5] = txtcontatto;
			obj[6] = strio;
			obj[7] = prgtipocontatto;
			obj[8] = cdnlavoratore;
			obj[9] = cdnutins;
			obj[10] = cdnutmod;
			obj[11] = strcellsmsinvio;
			obj[12] = "N";
			obj[13] = prgmotcontatto;
			
			txExecutor.executeQuery("INSERT_AG_CONTATTO_BATCH_SMS", obj, "INSERT");

		} catch (EMFInternalError e) {
			return MessageCodes.SMS.INSERT_CONTATTO_SMS_FALLITO;
		}

		try {
			sms = new Sms(strcellsmsinvio, txtcontatto, maxLength);
			
			_logger.debug("ContattoSMS Testo SMS: "+txtcontatto);
			
			// Accodo l'sms
			gway.addSms(sms);

		} catch (SmsFormatException e) {
			if (e.getErrorCode() == SmsFormatException.ERR_TESTO_TROPPO_LUNGO) {
				return MessageCodes.SMS.TESTO_TROPPO_LUNGO;

			} else if (e.getErrorCode() == SmsFormatException.ERR_N_CELL_NON_VALIDO) {
				return MessageCodes.SMS.CELL_NON_VALIDO;
			}
		}
		
		// Se arrivo fino a qui considero l'SMS come inviato e aggiorno il flag nella tabella
		try {
			obj = new Object[2];
			obj[0] = "S";
			obj[1] = prgContatto;

			txExecutor.executeQuery("UPDATE_CONTATTO_SMS_INVIATO", obj, "UPDATE");

		} catch (EMFInternalError e) {
			txExecutor.rollBackTransaction();
			return codeError;
		}
		return codeError;
	}
	
	public SourceBean creaPerPromemoria(SourceBean lavoratori, User user) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		int errorCode = 0;

		String pool;
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		String errorSPI = "";
		// Imposto i campi da inserire nel contatto comuni a tutti i lavoratori
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = Integer.toString(user.getCodut());
		this.cdnutmod = Integer.toString(user.getCodut());
		this.codcpicontatto = user.getCodRif();
		
		if (this.txtcontatto==null || "".equals(this.txtcontatto))
			this.txtcontatto = "";
		
		// Lo costruiamo dopo solo se non è sms personalizzato

		DataConnectionManager dataConnectionManager;
		DataConnection dataConnection = null;

		SourceBean nonInviatiSB = null;
		SourceBean lavoratoreNoSMS = null;
		SourceBean errorSpi = null;

		List inputParameter = null;
		Object result = null;
		
		try {
			// Recupero (e costruisco) il testo dell'SMS da inviare
			dataConnectionManager = DataConnectionManager.getInstance();
			
			
//			if (this.txtcontatto==null || "".equals(this.txtcontatto)) {
//				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
//				inputParameter = new ArrayList();
//				inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, "PROMAPPU"));
//				QueryExecutorObject queryExecObj1 = new QueryExecutorObject();
//				queryExecObj1.setTokenStatement("GET_TESTO_SMS");
//				queryExecObj1.setInputParameters(inputParameter);
//				queryExecObj1.setType(QueryExecutorObject.SELECT);
//				queryExecObj1.setDataConnection(dataConnection);
//				result = queryExecObj1.exec();
//	
//				if (result instanceof SourceBean) {
//					msg1 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG1");
//					msg2 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG2");
//					msg3 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG3");
//					msg4 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG4");
//				}
//			
//			}

			// Recupero il progressivo dell'utente che esegue il contatto PRGSPI
			// Occorre creare un altro QueryExecutorObject perche' il primo
			// chiude la connessione
			QueryExecutorObject queryExecObj2 = new QueryExecutorObject();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// parametri di input nello statement
			inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("CDNUT", java.sql.Types.BIGINT, new Integer(user
					.getCodut())));
			// inputParameter.add(dataConnection.createDataField("cognome",
			// java.sql.Types.VARCHAR, user.getCognome().toUpperCase()));
			// inputParameter.add(dataConnection.createDataField("nome",
			// java.sql.Types.VARCHAR, user.getNome().toUpperCase()));

			queryExecObj2.setTokenStatement("GET_SPI");
			queryExecObj2.setInputParameters(inputParameter);
			queryExecObj2.setType(QueryExecutorObject.SELECT);

			queryExecObj2.setDataConnection(dataConnection);
			result = queryExecObj2.exec();

			// Ora ci sono tutti i parametri si può procedere all'invio
			// Creo il SourceBean di risposta per gli eventuali SMS che non si
			// sono potuti inviare
			nonInviatiSB = new SourceBean("SMS_NONINVIATI");

			if (result instanceof SourceBean) {
				this.prgspicontatto = (BigDecimal) ((SourceBean) result).getAttribute("ROW.PRGSPI");
				if (prgspicontatto == null) {
					nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.SPI_MANCANTE_SMS_FALLITO)));
					return nonInviatiSB;
				}
			}

			Vector vet = lavoratori.getAttributeAsVector("ROW");
			String nomeCampo="";
			String valoreCampo = "";
			BigDecimal maxLen = null;
			
			HashMap<String, SourceBean> mapCodServizio = new HashMap<String, SourceBean>();
			SourceBean sbCodServizio = null;
			_logger.warn(className + " mod invio sms inizio recupera dati per creazione promemoria, numero lavoratori : " + vet.size() + " con data :" + dateFormat.format(new Date()));
			
			for (int i = 0; i < vet.size(); i++) {

				
				SourceBean lav = ((SourceBean) vet.get(i));
				this.cdnlavoratore = new BigDecimal((String) lav.getAttribute("CDNLAVORATORE"));
				this.strcellsmsinvio = StringUtils.getAttributeStrNotNull(lav, "STRCELL");
				this.codServizio = StringUtils.getAttributeStrNotNull(lav, "CODSERVIZIO");
				boolean invio = true;
				
				if (this.txtcontatto==null || "".equals(this.txtcontatto)) {
									
				  if(!mapCodServizio.containsKey(this.codServizio)) {
					  DataConnection dataConnectionTmp = null;
					  try {
						  dataConnectionTmp = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
    				  
					 inputParameter = new ArrayList();
					//inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR,StringUtils.getAttributeStrNotNull(lav, "CODSERVIZIO")));					
					inputParameter.add(dataConnectionTmp.createDataField("", java.sql.Types.VARCHAR,this.codServizio));
					
					QueryExecutorObject queryExecObj1 = new QueryExecutorObject();
					queryExecObj1.setTokenStatement("GET_TESTO_SMS_TEMPLATE");
					queryExecObj1.setInputParameters(inputParameter);
					queryExecObj1.setType(QueryExecutorObject.SELECT);
					queryExecObj1.setDataConnection(dataConnectionTmp);
					result = queryExecObj1.exec();
		
					if (result instanceof SourceBean) {
						msg1 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STRTEMPLATE");
						maxLen  =  (BigDecimal) ((SourceBean) result).getAttribute("ROW.MAX_LEN");
						mapCodServizio.put(this.codServizio, (SourceBean)result);
					}
					}catch(Exception ex) {
						 invio = false;
						_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");
					}finally {
						Utils.releaseResources(dataConnectionTmp, null, null);
					}
					
				  }else {
					  //System.out.println("CodServizio già acquisito : " + this.codServizio);
					  sbCodServizio = mapCodServizio.get(this.codServizio);
					  if(sbCodServizio != null) {
							msg1 = StringUtils.getAttributeStrNotNull((sbCodServizio), "ROW.STRTEMPLATE");
							maxLen  =  (BigDecimal) (sbCodServizio).getAttribute("ROW.MAX_LEN");
					  }
				   }
				
				}

				// reperisco il consenso all'invio del SMS
				/*				
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				inputParameter = new ArrayList();
				inputParameter.add(dataConnection.createDataField("", java.sql.Types.INTEGER, cdnlavoratore));
				QueryExecutorObject queryExecObj3 = new QueryExecutorObject();
				queryExecObj3.setTokenStatement("GET_CONSENSO_SMS");
				queryExecObj3.setInputParameters(inputParameter);
				queryExecObj3.setType(QueryExecutorObject.SELECT);
				queryExecObj3.setDataConnection(dataConnection);
				result = queryExecObj3.exec();
				*/
				flagInvioSMS = StringUtils.getAttributeStrNotNull(lav, "FLGINVIOSMS");
								
				String flgConsenso = "";
				
					if (invio && (result != null) && (result instanceof SourceBean)) {
						//flgConsenso = StringUtils.getAttributeStrNotNull((SourceBean) result, "ROW.FLGINVIOSMS");
						flgConsenso = flagInvioSMS;
						if (!flgConsenso.equalsIgnoreCase("S")) {
							// Il lavoratore non ha dato il consenso all'invio dei
							// SMS. Lo segnalo nel SourceBean di risposta
							lavoratoreNoSMS = new SourceBean("LAVORATORE");
							lavoratoreNoSMS.setAttribute("strCognome", StringUtils
									.getAttributeStrNotNull(lav, "strCognome"));
							lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
							lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
									"strCodiceFiscale"));
							lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
							lavoratoreNoSMS.setAttribute("errorCode", Integer
									.toString(MessageCodes.SMS.MANCATO_CONSENSO_SMS));
							nonInviatiSB.setAttribute(lavoratoreNoSMS);
	
						} else {
							// "assemblo" il testo
							if (this.txtcontatto==null || "".equals(this.txtcontatto)) {
	//							txtcontatto = msg1 + " il giorno " + StringUtils.getAttributeStrNotNull(lav, "DATA")
	//									+ " alle ore " + StringUtils.getAttributeStrNotNull(lav, "ORA") + " presso CPI di "
	//									+ StringUtils.getAttributeStrNotNull(lav, "STRDESCRIZIONE") + " " + msg2 + " " + msg3
	//									+ " " + msg4;
								
								/* Nel campo STRTEMPLATE della tabella TS_SMS
								 * sono contenuti i marcatori compresi tra @
								 * con gli stessi nomi dei campi del SourceBean
								 */
								String testoSMS=msg1;
								for(int j = 0; j < msg1.length(); j++){
									String lettera = msg1.charAt(j)+"";
									if(lettera!=null && lettera.equalsIgnoreCase("@")) {
										if(msg1.indexOf("@", j+1)>=1) {
											nomeCampo = msg1.substring(j+1, msg1.indexOf("@", j+1));
										}						
										valoreCampo = StringUtils.getAttributeStrNotNull(lav, nomeCampo);
										if (valoreCampo.startsWith("CPI")) 
											valoreCampo = valoreCampo.substring(4);
										testoSMS = testoSMS.replace("@" + nomeCampo +  "@", valoreCampo);
									}
								}
								
								testoSMS = testoSMS.replaceAll(" {2,}", " ");
								txtcontatto = testoSMS;
								setMaxLength(maxLen.intValue());
							}
							
							try {
								// Procedo all'inserimento del contatto e al
								// conseguente invio
								errorCode = insert(gway);
							} catch (Exception e1) {
								_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");
	
							}
							if (errorCode != 0) {
								// Se ci sono stati errori nell'invio lo segnalo nel
								// SourceBean di risposta
								lavoratoreNoSMS = new SourceBean("LAVORATORE");
								lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav,
										"strCognome"));
								lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
								lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
										"strCodiceFiscale"));
								lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
								lavoratoreNoSMS.setAttribute("errorCode", Integer.toString(errorCode));
								nonInviatiSB.setAttribute(lavoratoreNoSMS);
								errorCode = 0;
							}
						}
					} else {
						// Se ci sono stati errori nell'invio lo segnalo nel
						// SourceBean di risposta
						lavoratoreNoSMS = new SourceBean("LAVORATORE");
						lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav, "strCognome"));
						lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
						lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
								"strCodiceFiscale"));
						lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
						lavoratoreNoSMS.setAttribute("errorCode", Integer
								.toString(MessageCodes.SMS.IMPOSSIBILE_REPERIRE_CONSENSO));
						nonInviatiSB.setAttribute(lavoratoreNoSMS);
					}
				
				txtcontatto="";	// il campo txtcontatto deve essere sbiancato alla fine di ogni ciclo (quindi per ogni contatto) altrimenti non sara' elaborato al successivo giro
				
			}
			_logger.warn(className + " mod invio sms fine recupera dati per creazione promemoria con data :" + dateFormat.format(new Date()));
		} catch (SourceBeanException e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		} catch (EMFInternalError e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		}finally {
			Utils.releaseResources(dataConnection, null, null);
		}
		try {
			_logger.warn(className + " mod invio sms inizio invio SMS Gateway con data :" + dateFormat.format(new Date()));
			if (gway.smsList.size() > 0)
				gway.send();
			_logger.warn(className + " mod invio sms fine invio SMS Gateway con data :" + dateFormat.format(new Date()));			
		} catch (SmsException e1) {
			try {
				nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.ERRORE_INVIO_SERVER)));
			} catch (SourceBeanException e2) {
				// TODO Blocco catch generato automaticamente
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
		return nonInviatiSB;

	}// creaPerPromemoria

	public SourceBean creaPerRosacandidati(SourceBean lavoratori, User user) {
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		int errorCode = 0;

		String pool;
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		// Imposto i campi da inserire nel contatto comuni a tutti i lavoratori
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = Integer.toString(user.getCodut());
		this.cdnutmod = Integer.toString(user.getCodut());
		this.codcpicontatto = user.getCodRif();
		this.txtcontatto = "";// Lo costruiamo dopo

		DataConnectionManager dataConnectionManager;
		DataConnection dataConnection;

		SourceBean nonInviatiSB = null;
		SourceBean lavoratoreNoSMS = null;

		try {
			// Recupero (e costruisco) il testo dell'SMS da inviare
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, "INVIROSA"));

			QueryExecutorObject queryExecObj1 = new QueryExecutorObject();
			queryExecObj1.setTokenStatement("GET_TESTO_SMS");
			queryExecObj1.setInputParameters(inputParameter);
			queryExecObj1.setType(QueryExecutorObject.SELECT);
			queryExecObj1.setDataConnection(dataConnection);
			Object result = queryExecObj1.exec();

			if (result instanceof SourceBean) {
				msg1 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG1");
				msg2 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG2");
				msg3 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG3");
				msg4 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG4");
			}

			// Recupero il progressivo dell'utente che esegue il contatto PRGSPI
			// Occorre creare un altro QueryExecutorObject perche' il primo
			// chiude la connessione
			QueryExecutorObject queryExecObj2 = new QueryExecutorObject();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// parametri di input nello statement
			inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("CDNUT", java.sql.Types.BIGINT, new Integer(user
					.getCodut())));
			// inputParameter.add(dataConnection.createDataField("cognome",
			// java.sql.Types.VARCHAR, user.getCognome().toUpperCase()));
			// inputParameter.add(dataConnection.createDataField("nome",
			// java.sql.Types.VARCHAR, user.getNome().toUpperCase()));

			queryExecObj2.setTokenStatement("GET_SPI");
			queryExecObj2.setInputParameters(inputParameter);
			queryExecObj2.setType(QueryExecutorObject.SELECT);

			queryExecObj2.setDataConnection(dataConnection);
			result = queryExecObj2.exec();

			if (result instanceof SourceBean) {
				this.prgspicontatto = (BigDecimal) ((SourceBean) result).getAttribute("ROW.PRGSPI");
			}

			// Ora ci sono tutti i parametri si può procedere all'invio
			// Creo il SourceBean di risposta per gli eventuali SMS che non si
			// sono potuti inviare
			nonInviatiSB = new SourceBean("SMS_NONINVIATI");

			Vector vet = lavoratori.getAttributeAsVector("ROW");
			for (int i = 0; i < vet.size(); i++) {

				SourceBean lav = ((SourceBean) vet.get(i));
				this.cdnlavoratore = (BigDecimal) lav.getAttribute("CDNLAVORATORE");
				this.strcellsmsinvio = StringUtils.getAttributeStrNotNull(lav, "STRCELL");

				// reperisco il consenso all'invio del SMS
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				inputParameter = new ArrayList();
				inputParameter.add(dataConnection.createDataField("", java.sql.Types.INTEGER, cdnlavoratore));
				QueryExecutorObject queryExecObj3 = new QueryExecutorObject();
				queryExecObj3.setTokenStatement("GET_CONSENSO_SMS");
				queryExecObj3.setInputParameters(inputParameter);
				queryExecObj3.setType(QueryExecutorObject.SELECT);
				queryExecObj3.setDataConnection(dataConnection);
				result = queryExecObj3.exec();

				String flgConsenso = "";
				if ((result != null) && (result instanceof SourceBean)) {
					flgConsenso = StringUtils.getAttributeStrNotNull((SourceBean) result, "ROW.FLGINVIOSMS");
					if (!flgConsenso.equalsIgnoreCase("S")) {
						// Il lavoratore non ha dato il consenso all'invio dei
						// SMS. Lo segnalo nel SourceBean di risposta
						lavoratoreNoSMS = new SourceBean("LAVORATORE");
						lavoratoreNoSMS.setAttribute("strCognome", StringUtils
								.getAttributeStrNotNull(lav, "strCognome"));
						lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
						lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
								"strCodiceFiscale"));
						lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
						lavoratoreNoSMS.setAttribute("errorCode", Integer
								.toString(MessageCodes.SMS.MANCATO_CONSENSO_SMS));
						nonInviatiSB.setAttribute(lavoratoreNoSMS);

					} else {
						String strMansioni = StringUtils.getAttributeStrNotNull(lav, "strmansionepubb");
						
						strMansioni = strMansioni.replace("\r\n", ",");
						if (strMansioni.trim().endsWith(",")) {strMansioni =  strMansioni.substring(0,strMansioni.length()-1);}
						txtcontatto = "";
						// "assemblo" il testo
						txtcontatto += (msg1.length() > 0 ? msg1 : "");
						txtcontatto += (msg2.length() > 0 ? msg2 : "");
						txtcontatto += strMansioni;
						txtcontatto += (msg3.length() > 0 ? msg3 : "");
						txtcontatto += (msg4.length() > 0 ? msg4 : "");

						
						_logger.debug("Testo SMS: "+txtcontatto);
						
						try {
							// Procedo all'inserimento del contatto e al
							// conseguente invio
							errorCode = insert(gway);
						} catch (Exception e1) {
							_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

						}
						if (errorCode != 0) {
							// Se ci sono stati errori nell'invio lo segnalo nel
							// SourceBean di risposta
							lavoratoreNoSMS = new SourceBean("LAVORATORE");
							lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav,
									"strCognome"));
							lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
							lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
									"strCodiceFiscale"));
							lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
							lavoratoreNoSMS.setAttribute("errorCode", Integer.toString(errorCode));
							nonInviatiSB.setAttribute(lavoratoreNoSMS);
							errorCode = 0;
						}
					}
				} else {
					// Se ci sono stati errori nell'invio lo segnalo nel
					// SourceBean di risposta
					lavoratoreNoSMS = new SourceBean("LAVORATORE");
					lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav, "strCognome"));
					lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
					lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
							"strCodiceFiscale"));
					lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
					lavoratoreNoSMS.setAttribute("errorCode", Integer
							.toString(MessageCodes.SMS.IMPOSSIBILE_REPERIRE_CONSENSO));
					nonInviatiSB.setAttribute(lavoratoreNoSMS);
				}
			}
		} catch (SourceBeanException e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		} catch (EMFInternalError e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		}
		try {
			if (gway.smsList.size() > 0)
				gway.send();
		} catch (SmsException e1) {
			try {
				nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.ERRORE_INVIO_SERVER)));
			} catch (SourceBeanException e2) {
				// TODO Blocco catch generato automaticamente
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
		return nonInviatiSB;
	}// creaPerRosacandidati()

	// creaPerRosaGrezza()

	public SourceBean creaPerRosaGrezza(SourceBean lavoratori, User user) {
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		int errorCode = 0;

		String pool;
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		// Imposto i campi da inserire nel contatto comuni a tutti i lavoratori
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = Integer.toString(user.getCodut());
		this.cdnutmod = Integer.toString(user.getCodut());
		this.codcpicontatto = user.getCodRif();
		this.txtcontatto = "";// Lo costruiamo dopo

		DataConnectionManager dataConnectionManager;
		DataConnection dataConnection;

		SourceBean nonInviatiSB = null;
		SourceBean lavoratoreNoSMS = null;

		try {
			// Recupero (e costruisco) il testo dell'SMS da inviare
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, "ROSAGREZ"));

			QueryExecutorObject queryExecObj1 = new QueryExecutorObject();
			queryExecObj1.setTokenStatement("GET_TESTO_SMS");
			queryExecObj1.setInputParameters(inputParameter);
			queryExecObj1.setType(QueryExecutorObject.SELECT);
			queryExecObj1.setDataConnection(dataConnection);
			Object result = queryExecObj1.exec();

			if (result instanceof SourceBean) {
				msg1 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG1");
				msg2 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG2");
				msg3 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG3");
				msg4 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG4");
			}

			// Recupero il progressivo dell'utente che esegue il contatto PRGSPI
			// Occorre creare un altro QueryExecutorObject perche' il primo
			// chiude la connessione
			QueryExecutorObject queryExecObj2 = new QueryExecutorObject();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			inputParameter = new ArrayList();

			// parametri di input nello statement
			inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("CDNUT", java.sql.Types.BIGINT, new Integer(user
					.getCodut())));
			// inputParameter.add(dataConnection.createDataField("cognome",
			// java.sql.Types.VARCHAR, user.getCognome().toUpperCase()));
			// inputParameter.add(dataConnection.createDataField("nome",
			// java.sql.Types.VARCHAR, user.getNome().toUpperCase()));

			queryExecObj2.setTokenStatement("GET_SPI");
			queryExecObj2.setInputParameters(inputParameter);
			queryExecObj2.setType(QueryExecutorObject.SELECT);

			queryExecObj2.setDataConnection(dataConnection);
			result = queryExecObj2.exec();

			/*
			 * if (result instanceof SourceBean) { this.prgspicontatto =
			 * (BigDecimal) ((SourceBean)result).getAttribute("ROW.PRGSPI"); }
			 * 
			 * //Ora ci sono tutti i parametri si può procedere all'invio //Creo
			 * il SourceBean di risposta per gli eventuali SMS che non si sono
			 * potuti inviare nonInviatiSB = new SourceBean("SMS_NONINVIATI");
			 */

			// Ora ci sono tutti i parametri si può procedere all'invio
			// Creo il SourceBean di risposta per gli eventuali SMS che non si
			// sono potuti inviare
			nonInviatiSB = new SourceBean("SMS_NONINVIATI");

			if (result instanceof SourceBean) {
				this.prgspicontatto = (BigDecimal) ((SourceBean) result).getAttribute("ROW.PRGSPI");
				if (prgspicontatto == null) {
					nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.SPI_MANCANTE_SMS_FALLITO)));
					return nonInviatiSB;
				}
			}

			Vector vet = lavoratori.getAttributeAsVector("ROW");
			for (int i = 0; i < vet.size(); i++) {

				SourceBean lav = ((SourceBean) vet.get(i));
				this.prgrichiestaorig = StringUtils.getAttributeStrNotNull(lav, "PRGRICHIESTAORIG");
				this.cdnlavoratore = (BigDecimal) lav.getAttribute("CDNLAVORATORE");
				this.strcellsmsinvio = StringUtils.getAttributeStrNotNull(lav, "STRCELL");
				this.prgTipoRosa = StringUtils.getAttributeStrNotNull(lav, "PRGTIPOROSA");

				// reperisco il consenso all'invio del SMS
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				inputParameter = new ArrayList();
				inputParameter.add(dataConnection.createDataField("", java.sql.Types.INTEGER, cdnlavoratore));
				QueryExecutorObject queryExecObj3 = new QueryExecutorObject();
				queryExecObj3.setTokenStatement("GET_CONSENSO_SMS");
				queryExecObj3.setInputParameters(inputParameter);
				queryExecObj3.setType(QueryExecutorObject.SELECT);
				queryExecObj3.setDataConnection(dataConnection);
				result = queryExecObj3.exec();

				// reperisco l'esito della disponibilità se è già presente non
				// permetto l'invio dell'SMS
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				inputParameter = new ArrayList();
				inputParameter.add(dataConnection.createDataField("", java.sql.Types.INTEGER, prgrichiestaorig));
				inputParameter.add(dataConnection.createDataField("", java.sql.Types.INTEGER, cdnlavoratore));
				QueryExecutorObject queryExecObj4 = new QueryExecutorObject();
				queryExecObj4.setTokenStatement("GET_ESITO_DISPO");
				queryExecObj4.setInputParameters(inputParameter);
				queryExecObj4.setType(QueryExecutorObject.SELECT);
				queryExecObj4.setDataConnection(dataConnection);
				Object result2 = queryExecObj4.exec();

				String flgConsenso = "";
				String esitoDisp = "";
				esitoDisp = StringUtils.getAttributeStrNotNull((SourceBean) result2, "ROW.CODDISPONIBILITAROSA");
				if ((result != null) && (esitoDisp.equals("")) && (result instanceof SourceBean)) {
					flgConsenso = StringUtils.getAttributeStrNotNull((SourceBean) result, "ROW.FLGINVIOSMS");
					if (!flgConsenso.equalsIgnoreCase("S")) { // da mettere a
																// S
						// Il lavoratore non ha dato il consenso all'invio dei
						// SMS. Lo segnalo nel SourceBean di risposta
						lavoratoreNoSMS = new SourceBean("LAVORATORE");
						lavoratoreNoSMS.setAttribute("strCognome", StringUtils
								.getAttributeStrNotNull(lav, "strCognome"));
						lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
						lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
								"strCodiceFiscale"));
						lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
						lavoratoreNoSMS.setAttribute("errorCode", Integer
								.toString(MessageCodes.SMS.MANCATO_CONSENSO_SMS));
						nonInviatiSB.setAttribute(lavoratoreNoSMS);

					} else {
						// "assemblo" il testo

						txtcontatto = StringUtils.getAttributeStrNotNull(lav, "testosms");
						
						_logger.debug("Testo SMS: "+txtcontatto);
						
						try {
							// Procedo all'inserimento del contatto e al
							// conseguente invio
							errorCode = insert(gway);
						} catch (Exception e1) {
							_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

						}
						if (errorCode != 0) {
							// Se ci sono stati errori nell'invio lo segnalo nel
							// SourceBean di risposta
							lavoratoreNoSMS = new SourceBean("LAVORATORE");
							lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav,
									"strCognome"));
							lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
							lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
									"strCodiceFiscale"));
							lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
							lavoratoreNoSMS.setAttribute("errorCode", Integer.toString(errorCode));
							nonInviatiSB.setAttribute(lavoratoreNoSMS);
							errorCode = 0;
						}
					}
				} else {
					// Se ci sono stati errori nell'invio lo segnalo nel
					// SourceBean di risposta
					lavoratoreNoSMS = new SourceBean("LAVORATORE");
					lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav, "strCognome"));
					lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
					lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
							"strCodiceFiscale"));
					lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
					lavoratoreNoSMS.setAttribute("errorCode", Integer
							.toString(MessageCodes.SMS.IMPOSSIBILE_REPERIRE_CONSENSO));
					nonInviatiSB.setAttribute(lavoratoreNoSMS);
				}
			}
		} catch (SourceBeanException e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		} catch (EMFInternalError e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		}
		try {
			if (gway.smsList.size() > 0)
				gway.send();
		} catch (SmsException e1) {
			try {
				nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.ERRORE_INVIO_SERVER)));
			} catch (SourceBeanException e2) {
				// TODO Blocco catch generato automaticamente
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
		return nonInviatiSB;
	}// creaPerRosaGrezza()
	
	public SourceBean creaPerPromemoriaCIG(SourceBean lavoratori, User user) {
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		int errorCode = 0;

		// Imposto i campi da inserire nel contatto comuni a tutti i lavoratori
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = Integer.toString(user.getCodut());
		this.cdnutmod = Integer.toString(user.getCodut());
		this.codcpicontatto = user.getCodRif();
		this.txtcontatto = "";// Lo costruiamo dopo

		DataConnectionManager dataConnectionManager;
		DataConnection dataConnection = null;

		SourceBean nonInviatiSB = null;
		SourceBean lavoratoreNoSMS = null;

		try {
			// Recupero (e costruisco) il testo dell'SMS da inviare
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, "PROMCIG"));

			// Recupero il progressivo dell'utente che esegue il contatto PRGSPI
			// Occorre creare un altro QueryExecutorObject perche' il primo
			// chiude la connessione
			QueryExecutorObject queryExecObj2 = new QueryExecutorObject();
			
			inputParameter = new ArrayList();

			// parametri di input nello statement
			inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("CDNUT", java.sql.Types.BIGINT, new Integer(user
					.getCodut())));
			// inputParameter.add(dataConnection.createDataField("cognome",
			// java.sql.Types.VARCHAR, user.getCognome().toUpperCase()));
			// inputParameter.add(dataConnection.createDataField("nome",
			// java.sql.Types.VARCHAR, user.getNome().toUpperCase()));

			queryExecObj2.setTokenStatement("GET_SPI");
			queryExecObj2.setInputParameters(inputParameter);
			queryExecObj2.setType(QueryExecutorObject.SELECT);

			queryExecObj2.setDataConnection(dataConnection);
			Object result = queryExecObj2.exec();
			
			nonInviatiSB = new SourceBean("SMS_NONINVIATI");

			if (result instanceof SourceBean) {
				this.prgspicontatto = (BigDecimal) ((SourceBean) result).getAttribute("ROW.PRGSPI");
				if (prgspicontatto == null) {
					nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.SPI_MANCANTE_SMS_FALLITO)));
					return nonInviatiSB;
				}
			}

			SourceBean lav = (SourceBean)lavoratori.getAttribute("ROW");
			
			// "assemblo" il testo

			txtcontatto = StringUtils.getAttributeStrNotNull(lav, "textSms");
			this.strcellsmsinvio = StringUtils.getAttributeStrNotNull(lav, "strCell");
			this.cdnlavoratore = new BigDecimal(StringUtils.getAttributeStrNotNull(lav, "cdnLavoratore"));
			this.prgTipoRosa = "-1"; //setto un valore invalido in modo che la funzione insert non vada a 
									 //effettuare l'inserimento in DO_DISPONIBILTA
			
			try {
				// Procedo all'inserimento del contatto e al
				// conseguente invio
				errorCode = insert(gway);
			} catch (Exception e1) {
				_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

			}
			
			if (errorCode != 0) {
				// Se ci sono stati errori nell'invio lo segnalo nel
				// SourceBean di risposta
				lavoratoreNoSMS = new SourceBean("LAVORATORE");
				lavoratoreNoSMS.setAttribute("strCognome", StringUtils.getAttributeStrNotNull(lav,
						"strCognome"));
				lavoratoreNoSMS.setAttribute("strNome", StringUtils.getAttributeStrNotNull(lav, "strNome"));
				lavoratoreNoSMS.setAttribute("strCodiceFiscale", StringUtils.getAttributeStrNotNull(lav,
						"strCodiceFiscale"));
				lavoratoreNoSMS.setAttribute("cdnLavoratore", cdnlavoratore);
				lavoratoreNoSMS.setAttribute("errorCode", Integer.toString(errorCode));
				nonInviatiSB.setAttribute(lavoratoreNoSMS);
				errorCode = 0;
			}
		} catch (SourceBeanException e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		} catch (EMFInternalError e) {
			_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");

		}
		finally {
			//rilascia la connessione
			Utils.releaseResources(dataConnection, null, null);
		}
		
		try {
			if (gway.smsList.size() > 0)
				gway.send();
		} catch (SmsException e1) {
			try {
				nonInviatiSB.setAttribute("ERROR", (Integer.toString(MessageCodes.SMS.ERRORE_INVIO_SERVER)));
			} catch (SourceBeanException e2) {
				// TODO Blocco catch generato automaticamente
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
		return nonInviatiSB;
	}// creaPerPromemoriaCIG()
	
	/**
	 * 
	 * Attenzione che invia anche se l'utente non ha espresso consenso, 
	 * da verificare a monte, non effettua controlli come gli altri metodi.
	 * 
	 * @param cdnLavoratore
	 * @param strCellLavoratore
	 * @param strNomeLavoratore
	 * @param strCognomeLavoratore
	 * @param codCpi
	 * @param strDescrizioneCpi
	 * @param strIndirizzoCpi
	 * @param strTelCpi
	 * @param dataAppuntamento
	 * @param oraAppuntamento
	 * @param isAppuntamentoOk
	 * @return
	 */
	public boolean creaPerDidOnline(
			BigDecimal cdnLavoratore, 
			String strCellLavoratore,
			String strNomeLavoratore,
			String strCognomeLavoratore,
			String codCpi,
			String strDescrizioneCpi,
			String strIndirizzoCpi,
			String strTelCpi,
			String dataAppuntamento,
			String oraAppuntamento,
			String prgSpi,
			boolean isAppuntamentoOk,
			boolean isSpiDisponibile,
			String strNomeSpi,
			String strCognomeSpi,
			String strTelSpi) {
		
		int errorCode = 0;
		boolean success = false;
		
		//String codTipoSms = "PROMDIDO";
		//DataConnectionManager dataConnectionManager;
		//DataConnection dataConnection;
		
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		
		//String msg1 = "";
		//String msg2 = "";
		//String msg4 = "";
		
		///////////////////
		// DATI CONTATTO //
		///////////////////
		
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = "150"; // utente servizi portale
		this.cdnutmod = "150"; // utente servizi portale
		this.prgspicontatto = new BigDecimal(prgSpi); // utente associato a cooperazione applicativa
		this.codcpicontatto = codCpi;
		this.txtcontatto = "";
		this.cdnlavoratore = cdnLavoratore;
		this.strcellsmsinvio = strCellLavoratore;
		this.flgricontattare = null;
		this.strio = "O";
		this.prgtipocontatto = new BigDecimal("5"); // SMS
		
		//try {
		
			/*
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			*/
			//////////////////////////////////////////
			// LETTURA TESTO MESSAGGIO PRECOMPILATO //
			//////////////////////////////////////////
			/*
			List<DataField> inputParameter = new ArrayList<DataField>();
			inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, codTipoSms));
			QueryExecutorObject queryExecObj1 = new QueryExecutorObject(); // TODO: chiude la connessione?
			queryExecObj1.setTokenStatement("GET_TESTO_SMS");
			queryExecObj1.setInputParameters(inputParameter);
			queryExecObj1.setType(QueryExecutorObject.SELECT);
			queryExecObj1.setDataConnection(dataConnection);
			Object result = queryExecObj1.exec();
			if (result instanceof SourceBean) {
				msg1 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG1");
				msg2 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG2");
				msg4 = StringUtils.getAttributeStrNotNull(((SourceBean) result), "ROW.STR30MSG4");
			}
			*/
			////////////////////////////////
			// COMPOSIZIONE DEL MESSAGGIO //
			////////////////////////////////
			
			String smsTitle = "Appuntamento CPI";
			String smsTitleEnd = ": ";
			
			if (isAppuntamentoOk) {
				
				if (isSpiDisponibile) {
				
					this.txtcontatto = 
						"Salve "+strNomeLavoratore+" "+strCognomeLavoratore+", a seguito della DID rilasciata su " +
						"Lavoro per Te il "+strData+", l'operatore "+strNomeSpi+" "+strCognomeSpi+" del  CPI di "+strDescrizioneCpi+" ti aspetta per un colloquio con un suo operatore" +
						" in "+strIndirizzoCpi+" il giorno "+dataAppuntamento+" alle ore "+oraAppuntamento+". " +
						"In caso di impossibilità, telefonare al "+strTelSpi+" per fissare un nuovo appuntamento. Il colloquio " +
						"è obbligatorio per mantenere lo stato occupazionale.";
				
				} else {
					
					this.txtcontatto = 
							"Salve "+strNomeLavoratore+" "+strCognomeLavoratore+", a seguito della DID rilasciata su " +
							"Lavoro per Te il "+strData+", il CPI  di "+strDescrizioneCpi+" ti aspetta per un colloquio con un suo operatore" +
							" in "+strIndirizzoCpi+" il giorno "+dataAppuntamento+" alle ore "+oraAppuntamento+". " +
							"In caso di impossibilità, telefonare al "+strTelCpi+" per fissare un nuovo appuntamento. Il colloquio " +
							"è obbligatorio per mantenere lo stato occupazionale.";
					
				}
				
				
			} else {
				
				if (isSpiDisponibile) {
					
					this.txtcontatto =
							"Salve "+strNomeLavoratore+" "+strCognomeLavoratore+", a seguito della DID rilasciata su " +
							"Lavoro per Te il "+strData+", rivolgersi al CPI di "+strDescrizioneCpi+", telefonando al "+strTelSpi+" per " +
							"fissare un appuntamento per un colloquio. Il tuo operatore di riferimento è "+strNomeSpi+" "+strCognomeSpi+"."+
							" Il colloquio presso il CPI è obbligatorio per mantenere lo stato occupazionale.";
					
					
				} else {
					
					this.txtcontatto =
							"Salve "+strNomeLavoratore+" "+strCognomeLavoratore+", a seguito della DID rilasciata su " +
							"Lavoro per Te il "+strData+", rivolgersi al CPI di "+strDescrizioneCpi+", telefonando al "+strTelCpi+" per " +
							"fissare un appuntamento per un colloquio con un suo operatore." +
							" Il colloquio presso il CPI è obbligatorio per mantenere lo stato occupazionale.";
					
				}
				
			}
			
			//System.out.println("Testo sms: "+smsTitle+this.txtcontatto);
			//System.out.println("Lunghezza: "+(smsTitle+smsTitleEnd+this.txtcontatto).length());
			
			List<String> messages = new ArrayList<String>();
			int maxChars = 160;
			int overheadLength = 11+16+2; // <space>msg<space><number><space>di<space><total> (hyp: max 9 messages!)
			int theoreticalNumberOfMessages = (int)Math.ceil((smsTitle+smsTitleEnd+this.txtcontatto).length() / (double)maxChars);
			int physicalNumberOfMessages = (int)Math.ceil((this.txtcontatto).length() / (double)(maxChars - overheadLength));
			//System.out.println("Theoretical: "+theoreticalNumberOfMessages);
			//System.out.println("Physical: "+physicalNumberOfMessages);
			
			if (theoreticalNumberOfMessages > 1) {
				for (int i=0;i<physicalNumberOfMessages;i++) {
					String overhead = smsTitle+" msg "+(i+1)+" di "+physicalNumberOfMessages+smsTitleEnd;
					int minCharIndex = Math.max(i*(maxChars-overheadLength), 0);
					int maxCharIndex = Math.min((i+1)*(maxChars-overheadLength), this.txtcontatto.length());
					//System.out.println("minCharIndex: "+minCharIndex);
					//System.out.println("maxCharIndex: "+maxCharIndex);
					String currentMessage = overhead+this.txtcontatto.substring(minCharIndex,maxCharIndex);
					//System.out.println((i+1)+": "+currentMessage);
					//System.out.println("--> "+currentMessage.length());
					messages.add(currentMessage);
				}
			} else {
				// caso praticamente impossibile, ma previsto
				messages.add(smsTitle+smsTitleEnd+this.txtcontatto);
			}
			
			//////////////////////////////////////////////
			// INSERIMENTO CONTATTO SU DB E SMS GATEWAY //
			//////////////////////////////////////////////
			
			for (int i=0;i<messages.size();i++) {
				
				// cambia testo 
				this.txtcontatto = messages.get(i);
				
				// invio messaggi
				try {
					errorCode = insert(gway);
				} catch (Exception e1) {
					_logger.error("Errore nella creazione del contatto o nell'invio del SMS (a)");
				}
				
				if (errorCode != 0) {
					// TODO: problemi nell'invio
					_logger.error("Errore nella creazione del contatto o nell'invio del SMS (b) Error code: "+errorCode+" ["+MessageBundle.getMessage(Integer.toString(errorCode))+"]");
					//System.out.println("Problemi nell'invio. Error code: "+errorCode+" ["+MessageBundle.getMessage(Integer.toString(errorCode))+"]");
					break;
				}
			}
			
		//} catch (EMFInternalError e) {
		//	_logger.debug("Errore nella creazione del contatto o nell'invio del SMS");
		//	System.out.println("Errore creazione contatto o invio sms");
		//}
		
		////////////////////
		// INVIO CONCRETO //
		////////////////////
		
		try {
			//System.out.println("Sms da inviare: "+gway.smsList.size());
			if (gway.smsList.size() > 0) {
				gway.send();
				success = true;
				//System.out.println("Message sent.");
			}
		} catch (SmsException e1) {
			//e1.printStackTrace();
			_logger.error("Eccezione invio sms (server)");
			//System.out.println("Eccezione invio server");
		}
		
		return success;

	} // creaPerDidOnline
	
	public boolean creaPerAppuntamentoOnline(
			BigDecimal cdnLavoratore, 
			String strCellLavoratore,
			String strNomeLavoratore,
			String strCognomeLavoratore,
			String codCpi,
			String strDescrizioneCpi,
			String strAmbienteOIndirizzoCpi,
			String strTelCpi,
			String dataAppuntamento,
			String oraAppuntamento,
			String prgSpi,
			boolean isAppuntamentoOk,
			boolean isSpiDisponibile,
			String strNomeSpi,
			String strCognomeSpi,
			String strTelSpi,
			String codTipoSms) {
		
		int errorCode = 0;
		boolean success = false;
		
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		
		///////////////////
		// DATI CONTATTO //
		///////////////////
		
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = "150"; // utente servizi portale
		this.cdnutmod = "150"; // utente servizi portale
		this.prgspicontatto = new BigDecimal(prgSpi); // utente associato a cooperazione applicativa
		this.codcpicontatto = codCpi;
		this.txtcontatto = "";
		this.cdnlavoratore = cdnLavoratore;
		this.strcellsmsinvio = strCellLavoratore;
		this.flgricontattare = null;
		this.strio = "O";
		this.prgtipocontatto = new BigDecimal("5"); // SMS
		
		////////////////////////////////
		// COMPOSIZIONE DEL MESSAGGIO //
		////////////////////////////////
		
		String smsTitle = "";
		String smsTitleEnd = ": ";
		
		if (codTipoSms.equalsIgnoreCase(Constants.SMS.PATRONATO_SINDACATO)) {
			smsTitle = "Appuntamento CPI";
		}
		else {
			smsTitle = "Appuntamento per Adesione a GG";
		}
		
		String cognomeLavoratore = strCognomeLavoratore != null ? strCognomeLavoratore : "";
		String inizialeNomeLavoratore = (strNomeLavoratore != null && strNomeLavoratore.length() > 0)? strNomeLavoratore.substring(0, 1) : "";
		
		if (codTipoSms.equalsIgnoreCase(Constants.SMS.PATRONATO_SINDACATO)) {
			this.txtcontatto = "Ricorda l'appuntamento col Centro per l'Impiego il giorno " + dataAppuntamento + " alle " + oraAppuntamento;
		}
		else {
			this.txtcontatto = 
					cognomeLavoratore + " " + inizialeNomeLavoratore + ", " +
							"il CPI di " + strDescrizioneCpi + " le ricorda l'appunt per \"Garanzia Giovani\" il " + dataAppuntamento + " alle " + oraAppuntamento + " Ci contatti in caso d'impossibilità";
		}
		
		List<String> messages = new ArrayList<String>();
		
		if (this.txtcontatto.length() > 160) {
		
			int maxChars = 160;
			int overheadLength = 11+16+2; // <space>msg<space><number><space>di<space><total> (hyp: max 9 messages!)
			int theoreticalNumberOfMessages = (int)Math.ceil((smsTitle+smsTitleEnd+this.txtcontatto).length() / (double)maxChars);
			int physicalNumberOfMessages = (int)Math.ceil((this.txtcontatto).length() / (double)(maxChars - overheadLength));
			
			if (theoreticalNumberOfMessages > 1) {
				for (int i=0;i<physicalNumberOfMessages;i++) {
					String overhead = smsTitle+" msg "+(i+1)+" di "+physicalNumberOfMessages+smsTitleEnd;
					overheadLength = overhead.length();
					int minCharIndex = Math.max(i*(maxChars-overheadLength), 0);
					int maxCharIndex = Math.min((i+1)*(maxChars-overheadLength), this.txtcontatto.length());
					String currentMessage = overhead+this.txtcontatto.substring(minCharIndex,maxCharIndex);
					messages.add(currentMessage);
				}
			}
			
		} else {
			// caso praticamente impossibile, ma previsto
			messages.add(this.txtcontatto);
		}
		
		//////////////////////////////////////////////
		// INSERIMENTO CONTATTO SU DB E SMS GATEWAY //
		//////////////////////////////////////////////
		
		for (int i=0;i<messages.size();i++) {
			
			// cambia testo 
			this.txtcontatto = messages.get(i);
			
			// invio messaggi
			try {
				errorCode = insert(gway);
			} catch (Exception e1) {
				_logger.error("Errore nella creazione del contatto o nell'invio del SMS (a)", e1);
			}
			
			if (errorCode != 0) {
				_logger.error("Errore nella creazione del contatto o nell'invio del SMS (b) Error code: "+errorCode+" ["+MessageBundle.getMessage(Integer.toString(errorCode))+"]");
				break;
			}
		}
		
		////////////////////
		// INVIO CONCRETO //
		////////////////////
		
		try {
			if (gway.smsList.size() > 0) {
				gway.send();
				success = true;
			}
		} catch (SmsException e1) {
			_logger.error("Eccezione invio sms (server)", e1);
		}
		
		return success;

	} // creaPerAppuntamentoOnline
	
	public boolean creaContattoBatch(TransactionQueryExecutor txExec, 
			BigDecimal cdnLavoratore, 
			String strCellLavoratore,
			String strNomeLavoratore,
			String strCognomeLavoratore,
			String codCpi,
			String strDescrizioneCpi,
			String strIndirizzoCpi,
			String strTelCpi,
			String dataAppuntamento,
			String oraAppuntamento,
			BigDecimal prgSpi,
			String strNomeSpi,
			String strCognomeSpi,
			String strTelSpi,
			BigDecimal prgMotContatto,
			BigDecimal cdnUtente, String testoMessaggio) throws Exception {
		
		int errorCode = 0;
		boolean success = false;
		
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);
		Gateway gway = new Gateway();
		
		// DATI CONTATTO
		this.datcontatto = strData;
		this.stroracontatto = strOra;
		this.cdnutins = cdnUtente.toString();
		this.cdnutmod = cdnUtente.toString();
		this.prgspicontatto = prgSpi;
		this.codcpicontatto = codCpi;
		// COMPOSIZIONE DEL MESSAGGIO
		this.txtcontatto = testoMessaggio;
		this.cdnlavoratore = cdnLavoratore;
		this.strcellsmsinvio = strCellLavoratore;
		this.flgricontattare = null;
		this.strio = "O";
		this.prgmotcontatto = prgMotContatto;
		this.prgtipocontatto = new BigDecimal("5"); // SMS
		
		List<String> messages = new ArrayList<String>();
		
		if (this.txtcontatto.length() > 160) {
			int maxChars = 160;
			int physicalNumberOfMessages = (int)Math.ceil((this.txtcontatto).length() / (double)(maxChars));
			if (physicalNumberOfMessages > 1) {
				for (int i=0;i<physicalNumberOfMessages;i++) {
					String overhead = "msg "+(i+1)+" di "+ physicalNumberOfMessages;
					int overheadLength = overhead.length();
					int minCharIndex = Math.max(i*(maxChars-overheadLength), 0);
					int maxCharIndex = Math.min((i+1)*(maxChars-overheadLength), this.txtcontatto.length());
					String currentMessage = overhead+this.txtcontatto.substring(minCharIndex,maxCharIndex);
					messages.add(currentMessage);
				}
			}
		} 
		else {
			messages.add(this.txtcontatto);
		}
		
		//////////////////////////////////////////////
		// INSERIMENTO CONTATTO SU DB E SMS GATEWAY //
		//////////////////////////////////////////////
		
		for (int i=0;i<messages.size();i++) {
			
			// cambia testo 
			this.txtcontatto = messages.get(i);
			
			// invio messaggi
			try {
				errorCode = insert(gway, txExec);
			} catch (Exception e1) {
				_logger.error("Errore nella creazione del contatto o nell'invio del SMS (a)", e1);
			}
			
			if (errorCode != 0) {
				_logger.error("Errore nella creazione del contatto o nell'invio del SMS (b) Error code: "+errorCode+" ["+MessageBundle.getMessage(Integer.toString(errorCode))+"]");
				break;
			}
		}
		
		////////////////////
		// INVIO CONCRETO //
		////////////////////
		
		try {
			if (gway.smsList.size() > 0) {
				gway.send(txExec.getDataConnection());
				success = true;
			}
		} catch (SmsException e1) {
			_logger.error("Eccezione invio sms (server)", e1);
		}
		
		return success;

	} //creaContattoBatch
	
	public SourceBean getTesto(String tipoMessaggio) {
		DataConnectionManager dataConnectionManager = null;
		DataConnection dataConnection = null;
		SourceBean ret = null;
		try {
			// Recupero (e costruisco) il testo dell'SMS da inviare
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", java.sql.Types.VARCHAR, tipoMessaggio));
			QueryExecutorObject queryExecObj1 = new QueryExecutorObject();
			queryExecObj1.setTokenStatement("GET_TESTO_SMS_TEMPLATE");
			queryExecObj1.setInputParameters(inputParameter);
			queryExecObj1.setType(QueryExecutorObject.SELECT);
			queryExecObj1.setDataConnection(dataConnection);
			Object result = queryExecObj1.exec();

			if (result instanceof SourceBean) {
				ret = (SourceBean)result;
			}
			return ret;
		}
		catch (Exception e) {
			return ret;
		}
	}
	
	public static void main(String[] args) {
		String str = "pippo\r\n" + "paperino\r\n";
		System.out.println(str);
		str = str.replace("\r\n", ",");
		if (str.trim().endsWith(",")) {str =  str.substring(0,str.length()-1);}
		//str = str.replace("\r\n", ",");
		System.out.println(str);
	}

	public String getTxtcontatto() {
		return txtcontatto;
	}

	public void setTxtcontatto(String txtcontatto) {
		this.txtcontatto = txtcontatto;
	}
	
	
	
}
