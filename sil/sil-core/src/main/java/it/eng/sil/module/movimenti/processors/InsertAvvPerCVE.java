/*
 * Creato il 28-dic-04
 */
package it.eng.sil.module.movimenti.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;

/**
 * @author roccetti su PC di giuliani
 * 
 *         Questo processor serve per inserire sulla tabella AM_MOVIMENTO_APPOGGIO un movimento di avviamento collegato
 *         ad una cessazione oppure una proroga oppure una trasformazione. I dati necessari vengono estratti dal
 *         tracciato del movimento di CES o PRO o TRA. Per l'inserimento del record utilizza un processor InsertData
 *         configurato con il tracciato XML contenuto nel file
 *         /WEB-INF/conf/import/processors/insertMovimentoAppPerCVE.xml
 */
public class InsertAvvPerCVE implements RecordProcessor {

	/** RecordProcessor utilizzato per l'inserimento */
	RecordProcessor inserimentoAvviamento = null;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;

	/**
	 * Costruisce il processor e crea il processor InsertData utilizzato per l'inserimento
	 */
	public InsertAvvPerCVE(String name, TransactionQueryExecutor transexec, String configFileName, String insertName,
			BigDecimal user) throws SAXException, FileNotFoundException, IOException, ParserConfigurationException,
			NullPointerException {
		this.trans = transexec;
		inserimentoAvviamento = new InsertData("InserimentoAvvimentoPerCVE", transexec, configFileName, insertName,
				user);
	}

	/**
	 * Controlla se si sta inserendo una cessazione e se sono presenti i dati dell'avviamento, in questo caso esegue
	 * l'inserimento dell'avviamento e ritorna il risultato dell'inserimento.
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {
		String codMonoTempoTra = null;
		String codTipoContrattoTra = null;
		String codComUAZTra = null;
		String strIndirizzoUAZTra = null;
		Vector res = null;
		try {
			boolean changeCodTempo = false;
			boolean changeCodTipoContratto = false;
			boolean changeOrario = false;
			boolean trasfLavoratore = false;
			int nSizeRes = 0;
			String codtipomov = (String) record.get("evento");
			String codTempo = (String) record.get("CodTempo");
			String codTipoTrasf = record.get("CODTIPOTRASF") != null ? record.get("CODTIPOTRASF").toString() : "";

			// avviamento veloce lo si deve fare a partire da CES, PRO e TRA
			if (!("PRO".equalsIgnoreCase(codtipomov) || "CES".equalsIgnoreCase(codtipomov)
					|| "TRA".equalsIgnoreCase(codtipomov)))
				return null;

			if (codtipomov.equalsIgnoreCase("TRA")) {
				// Controllo gestione avviamento veloce (AvvVeloceDaTrasf oppure FLGAPPRENDTI settato se la proroga
				// è stata trasformata in una trasformazione in fase di acquisizione dal processor TransformCodTempo in
				// caso di apprendistato)
				if ((!record.containsKey("AvvVeloceDaTrasf")) && (!record.containsKey("FLGAPPRENDTI"))) {
					return null;
				}
				SourceBean result = null;
				if ((!codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.TRASFERIMENTO_DEL_LAVORATORE.getCodice()))
						&& (!codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROGRESSIONE_VERTICALE_PA.getCodice()))
						&& (!codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice()))) {
					Object[] args = new Object[1];
					args[0] = codTipoTrasf;
					result = (SourceBean) QueryExecutor.executeQuery("GET_REL_TRA_AVV_VELOCE", args, "SELECT",
							Values.DB_SIL_DATI);
					res = result.getAttributeAsVector("ROW");
					nSizeRes = res.size();
					if (nSizeRes == 0) {
						// Non sono riuscito a mappare la relazione tra l'avviamento veloce e il tipo di trasformazione
						return null;
					}
				}
			} else {
				if (codtipomov.equalsIgnoreCase("PRO")) {
					if (!record.containsKey("AvvVeloceDaProroga")) {
						return null;
					}
				}
			}

			// Calcolo sulla base del codice di tempo la data di inizio dell'avviamento relativo
			String datInizioAvvCVE = null;
			if (codTempo != null) {
				if ("D".equalsIgnoreCase(codTempo)) {
					datInizioAvvCVE = (String) record.get("DataAvvTempoDet");
				} else if ("I".equalsIgnoreCase(codTempo)) {
					datInizioAvvCVE = (String) record.get("DataAvvTempoInd");
				}
			}

			// Mi basta la data di inizio per fare il movimento di avviamento collegato
			if (datInizioAvvCVE != null && !"".equalsIgnoreCase(datInizioAvvCVE)) {
				// Inserisco nel record i dati necessari all'avviamento
				record.put("datInizioAvvCVE", datInizioAvvCVE);
				// Gestione Orario e Ore settimanali decret0 5 Novembre 2019

				String numOreSett = null;
				String codOrario = null;
				if (record.get("codOrario") != null) {
					codOrario = (String) record.get("codOrario");
				}
				if (record.get("numOreSett") != null) {
					numOreSett = (String) record.get("numOreSett");
				}

				if (codtipomov.equalsIgnoreCase("PRO")) {
					String datCessazTempoDetAvv = (String) record.get("DataCessazTempoDetAvv");
					if (datCessazTempoDetAvv == null || datCessazTempoDetAvv.equals("")) {
						String dataFineMov = (String) record.get("dataEvento");
						if (dataFineMov != null && !dataFineMov.equals("")) {
							record.put("DataCessazTempoDetAvv", DateUtils.giornoPrecedente(dataFineMov));
						}
					}
				} else {
					if (codtipomov.equalsIgnoreCase("TRA")) {
						String datCessazTempoDetAvv = (String) record.get("DataCessazTempoDetAvv");
						String dataInizioTrasf = (String) record.get("DataInizioMov");
						codMonoTempoTra = (String) record.get("CodTempo");
						codTipoContrattoTra = (String) record.get("CodTipoAvv");
						codComUAZTra = (String) record.get("CodComAz");
						strIndirizzoUAZTra = (String) record.get("IndirAz");
						String orarioAvv = "";
						String codTipoContrattoAvv = "";
						// Gestione codTipoTrasf = 'TL' (TRASFERIMENTO DEL LAVORATORE)
						if (codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.TRASFERIMENTO_DEL_LAVORATORE.getCodice())) {
							trasfLavoratore = true;
							String comAzPrec = (String) record.get("CODCOMAZPREC");
							String indirizzoAzPrec = (String) record.get("STRINDIRIZZOAZPREC");
							if (comAzPrec != null && !comAzPrec.equals("")) {
								record.put("CodComAz", comAzPrec);
							}
							if (indirizzoAzPrec != null && !indirizzoAzPrec.equals("")) {
								record.put("IndirAz", indirizzoAzPrec);
							}
						} else {
							// Gestione codTipoTrasf in ('DI', 'FI', 'II', 'TP', 'PP', 'PV', 'ZZ', 'DP')
							if (nSizeRes > 1) {
								// Gestione codTipoTrasf = 'DI'
								for (int j = 0; j < nSizeRes; j++) {
									SourceBean row = (SourceBean) res.get(j);
									String codTipoContrattoDB = row.getAttribute("codtipocontratto") != null
											? row.getAttribute("codtipocontratto").toString()
											: "";
									if (codTipoContrattoDB.equalsIgnoreCase(codTipoContrattoTra)) {
										if (datCessazTempoDetAvv == null || datCessazTempoDetAvv.equals("")) {
											if (dataInizioTrasf != null && !dataInizioTrasf.equals("")) {
												record.put("DataCessazTempoDetAvv",
														DateUtils.giornoPrecedente(dataInizioTrasf));
											}
										}
										record.put("CodTempo", "D");
										changeCodTempo = true;
										codTipoContrattoAvv = row.getAttribute("codtipocontrattoavv") != null
												? row.getAttribute("codtipocontrattoavv").toString()
												: "";
										record.put("CodTipoAvv", codTipoContrattoAvv);
										changeCodTipoContratto = true;
										j = nSizeRes; // uscita dal ciclo for
									}
								}
							} else {
								// Gestione codTipoTrasf in ('FI', 'II', 'TP', 'PP', 'PV', 'ZZ', 'DP')
								if (!codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROGRESSIONE_VERTICALE_PA.getCodice())
										&& !codTipoTrasf.equalsIgnoreCase(
												TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice())) {
									// Gestione codTipoTrasf in ('FI', 'II', 'TP', 'PP', 'DP')
									if (codTipoTrasf
											.equalsIgnoreCase(TipoTrasfEnum.TRASFORMAZIONE_FORMAZIONE_TI.getCodice())
											|| codTipoTrasf.equalsIgnoreCase(
													TipoTrasfEnum.TRASFORMAZIONE_INSERIMENTO_TI.getCodice())) {
										if (datCessazTempoDetAvv == null || datCessazTempoDetAvv.equals("")) {
											if (dataInizioTrasf != null && !dataInizioTrasf.equals("")) {
												record.put("DataCessazTempoDetAvv",
														DateUtils.giornoPrecedente(dataInizioTrasf));
											}
										}
									}
									SourceBean row = (SourceBean) res.get(0);
									orarioAvv = row.getAttribute("codorario") != null
											? row.getAttribute("codorario").toString()
											: "";
									codTipoContrattoAvv = row.getAttribute("codtipocontrattoavv") != null
											? row.getAttribute("codtipocontrattoavv").toString()
											: "";
									// Per codTipoTrasf = 'DP' non è previsto cambio orario e cambio codmonotempo ma
									// solo tipologia contrattuale
									if (!codTipoTrasf.equalsIgnoreCase(
											TipoTrasfEnum.TRASFORMAZIONE_APPRENDISTATO_APP_PROFESS.getCodice())) {
										if (!orarioAvv.equals("")) {
											changeOrario = true;
											record.put("codOrario", orarioAvv);
											record.remove("numOreSett");
										} else {
											if (!codTipoContrattoAvv.equals("")) {
												record.put("CodTempo", "D");
												changeCodTempo = true;
												record.put("CodTipoAvv", codTipoContrattoAvv);
												changeCodTipoContratto = true;
											}
										}
									} else {
										record.put("CodTipoAvv", codTipoContrattoAvv);
										changeCodTipoContratto = true;
									}
								}
							}
						}
					}
				}
				// Inserimento del movimento di avviamento
				SourceBean insertAvvVeloce = inserimentoAvviamento.processRecord(record);
				// Inserimento eventuali benefici del movimento di avviamento
				RecordProcessor insertBenefici = new InsertAgevolazioniApp("Inserisci_Benefici_Appoggio", trans, true);
				insertBenefici.processRecord(record);

				if (codtipomov.equalsIgnoreCase("TRA")) {
					if (changeCodTempo && codMonoTempoTra != null) {
						record.put("CodTempo", codMonoTempoTra);
					}
					if (changeCodTipoContratto && codTipoContrattoTra != null) {
						record.put("CodTipoAvv", codTipoContrattoTra);
					}
					if (changeOrario) {
						if (codOrario != null && !codOrario.equals("")) {
							record.put("codOrario", codOrario);
						}
						if (numOreSett != null && !numOreSett.equals("")) {
							record.put("numOreSett", numOreSett);
						}
					}
					if (trasfLavoratore) {
						if (codComUAZTra != null && !codComUAZTra.equals("")) {
							record.put("CodComAz", codComUAZTra);
						}
						if (strIndirizzoUAZTra != null && !strIndirizzoUAZTra.equals("")) {
							record.put("IndirAz", strIndirizzoUAZTra);
						}
					}
				}
				return insertAvvVeloce;
			} else
				return null;
		} catch (Exception e) {
			return ProcessorsUtils.createResponse("Inserimento_Avviamento_Veloce", classname,
					new Integer(MessageCodes.General.OPERATION_FAIL), "Importazione movimento fallito. ", null, null);
		}
	}

}
