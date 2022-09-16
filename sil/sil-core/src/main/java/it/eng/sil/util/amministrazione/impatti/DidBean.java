package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.AggiornaCompetenzaIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class DidBean extends SourceBean implements EventoAmministrativo, Comparable {
	public static final String DB_DAT_INIZIO = "DATDICHIARAZIONE";
	public static final String DB_DAT_FINE = "DATFINE";
	public static final String DB_COD_STATO_ATTO = "CODSTATOATTO";
	public static final String CDNLAVORATORE = "CDNLAVORATORE";
	public static final String DB_PRG_DID = "PRGDICHDISPONIBILITA";
	private final static String CODMONOCPICOMP = "C";
	public static final String STATO_PROTOCOLLATO = "PR";
	private StatoOccupazionaleBean statoOccupazionale;

	public DidBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	public StatoOccupazionaleBean getStatoOccupazionale() {
		return this.statoOccupazionale;
	}

	public void setStatoOccupazionale(StatoOccupazionaleBean newStatoOccupazionale) {
		this.statoOccupazionale = newStatoOccupazionale;
	}

	public BigDecimal getPrgDichDisponibilita() {
		Object obj = getAttribute("PRGDICHDISPONIBILITA");
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		} else {
			return new BigDecimal((String) obj);
		}
	}

	public BigDecimal getPrgStatoOccupaz() {
		Object obj = getAttribute("PRGSTATOOCCUPAZ");
		if (obj == null) {
			return null;
		} else {
			if (obj instanceof BigDecimal) {
				return (BigDecimal) obj;
			} else {
				return new BigDecimal((String) obj);
			}
		}
	}

	public BigDecimal getCdnLavoratore() {
		Object c = getAttribute(CDNLAVORATORE);
		if (c instanceof String)
			return new BigDecimal((String) c);
		else if (c instanceof BigDecimal)
			return (BigDecimal) c;
		else
			return null;
	}

	public String toString() {
		return " DID: prg=" + getPrgDichDisponibilita() + ", datDich=" + getDataInizio() + ", prgStOcc="
				+ getPrgStatoOccupaz() + ", codStatoAtto=" + getCodStatoAtto();
	}

	public int getTipoEventoAmministrativo() {
		return EventoAmministrativo.DID;
	}

	public String getDataInizio() {
		return (String) getAttribute(DidBean.DB_DAT_INIZIO);
	}

	public String getDataFine() {
		return (String) getAttribute(DidBean.DB_DAT_FINE);
	}

	public String getCodStatoAtto() {
		String codStatoAtto = containsAttribute(DidBean.DB_COD_STATO_ATTO)
				? (String) getAttribute(DidBean.DB_COD_STATO_ATTO)
				: "";
		return codStatoAtto;
	}

	public boolean isProtocollato() {
		String codStatoAtto = getCodStatoAtto();
		if (codStatoAtto.toUpperCase().equals("PR"))
			return true;
		else
			return false;
	}

	public boolean isDataFutura() throws Exception {
		String dataDichiarazione = getDataInizio();
		String oggi = DateUtils.getNow();
		int resultCompare = DateUtils.compare(dataDichiarazione, oggi);
		if (resultCompare > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPrecNormativa(String dataRifNormativa) throws Exception {
		String dataDichiarazione = getDataInizio();
		int resultCompare = DateUtils.compare(dataDichiarazione, dataRifNormativa);
		if (resultCompare < 0) {
			return true;
		} else {
			return false;
		}
	}

	public int compareTo(Object obj) {
		int ret = 0;
		boolean eventoChiusuraMob = false;
		try {
			EventoAmministrativo evento = (EventoAmministrativo) obj;
			int tipoEvento = evento.getTipoEventoAmministrativo();
			int tipoEventoThis = this.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.CESSAZIONE
					|| tipoEvento == EventoAmministrativo.PROROGA
					|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
				String dataInizio = ((MovimentoBean) evento).getDataInizio();
				String dataInizioDid = null;
				if (tipoEventoThis == EventoAmministrativo.DID)
					dataInizioDid = this.getDataInizio();
				else
					dataInizioDid = this.getDataFine();

				ret = DateUtils.compare(dataInizioDid, dataInizio);
				if (ret == 0) { // la did e la chiusura della did precedono i movimenti
					ret = -1;
				}
			} else {

				String dataInizio2 = null;
				String dataInizio1 = null;

				if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA
						|| tipoEvento == EventoAmministrativo.MOBILITA) {
					MobilitaBean mobilita = (MobilitaBean) evento;
					if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA) {
						eventoChiusuraMob = true;
						dataInizio2 = mobilita.getDataFine();
						dataInizio2 = DateUtils.giornoPrecedente(dataInizio2);
					} else {
						dataInizio2 = mobilita.getDataInizio();
					}

					if (tipoEventoThis == EventoAmministrativo.CHIUSURA_DID) {
						dataInizio1 = this.getDataFine();
					} else {
						dataInizio1 = this.getDataInizio();
					}

					ret = DateUtils.compare(dataInizio1, dataInizio2);
					if (ret == 0) {
						if (eventoChiusuraMob) {
							// La did e la chiusura della did precedono gli eventi di CHIUSURA_MOBILITA.
							// Se la mobilità finisce il giorno X, l'evento CHIUSURA_MOBILITA
							// scatta il giorno X + 1.
							ret = -1;
						}
					}
				} else {
					DidBean did = (DidBean) evento;
					if (tipoEvento == EventoAmministrativo.CHIUSURA_DID)
						dataInizio2 = evento.getDataFine();
					else
						dataInizio2 = did.getDataInizio();
					if (tipoEventoThis == EventoAmministrativo.CHIUSURA_DID)
						dataInizio1 = this.getDataFine();
					else
						dataInizio1 = this.getDataInizio();

					ret = DateUtils.compare(dataInizio1, dataInizio2);
					if (ret == 0) {
						if (this.getPrgDichDisponibilita().intValue() == did.getPrgDichDisponibilita().intValue()) {
							if (tipoEventoThis == EventoAmministrativo.CHIUSURA_DID)
								return 1;
							else
								return -1;
						} else {
							if (tipoEventoThis == EventoAmministrativo.DID
									&& tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
								return 1;
							} else {
								return 0;
							}
						}
					} else {
						return ret;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getDescrizione() {
		return "Did";
	}

	/**
	 * Cerca un evento amministrativo in una lista di eventi presa in input
	 * 
	 * @param prgEvento
	 *            progressivo dell'evento da ricercare
	 * @param eventi
	 *            eventi dai quali cercare
	 * @return un SourceBean che rappresenta l'evento trovato
	 * @throws ControlliException
	 *             lanciata nel caso in cui il movimento da cercare non è presente nella lista
	 * @author Togna Cosimo
	 */
	public SourceBean cercaEventoAmministrativo(Object prgEvento, java.util.Collection eventi)
			throws ControlliException {
		DidBean did = null;
		for (java.util.Iterator iterator = eventi.iterator(); iterator.hasNext();) {
			Object tmpEvento = iterator.next();
			if (tmpEvento instanceof DidBean) {
				did = (DidBean) tmpEvento;
				BigDecimal prgEventoCorrente = (BigDecimal) did.getAttribute(DidBean.DB_PRG_DID);
				if (prgEventoCorrente.intValue() == ((BigDecimal) prgEvento).intValue()) {
					return (did);
				}
			}
		}
		// lancio l'eccezione se non trovo l'evento desiderato
		throw new ControlliException(MessageCodes.EventoAmministrativo.SEARCH_ERROR);
	}

	/**
	 * @param object
	 * @param executor
	 * @return
	 */
	public static List getDid(Object cdnLavoratore, TransactionQueryExecutor executor) throws Exception {

		Object params[] = { cdnLavoratore, "01/01/0001" };
		SourceBean row = null;
		row = (SourceBean) executor.executeQuery("GET_DID_LAVORATORE_VALIDE", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere le dichiarazioni di immediata disponibilita' del lavoraotore");
		return new ArrayList(row.getAttributeAsVector("ROW"));

	}

	public static void aggiornaNoteDid(User user, Object prgDichDisp) throws Exception {
		TransactionQueryExecutor txExecutor = null;
		try {
			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			SourceBean didBean = DBLoad.getDID(prgDichDisp, txExecutor);

			BigDecimal numklodid = (BigDecimal) didBean.getAttribute("numKloDichDisp");
			numklodid = numklodid.add(new BigDecimal("1"));
			BigDecimal cdnUser = new BigDecimal(user.getCodut());
			String strNote = didBean.containsAttribute("strnote") ? didBean.getAttribute("strnote").toString() : "";
			if (strNote.equals("")) {
				strNote = "SAP inviata in data " + DateUtils.getNow();
			} else {
				strNote = strNote + " SAP inviata in data " + DateUtils.getNow();
			}
			if (strNote.length() > 1000) {
				strNote = strNote.substring(0, 1000);
			}
			Object params[] = { strNote, numklodid, cdnUser, prgDichDisp };
			Boolean res = (Boolean) txExecutor.executeQuery("SAVE_NOTE_DID_GESTIONE_SAP", params, "UPDATE");
			if (res == null || !res.booleanValue()) {
				txExecutor.rollBackTransaction();
			} else {
				txExecutor.commitTransaction();
			}
		} catch (Exception e) {
			if (txExecutor != null) {
				txExecutor.rollBackTransaction();
			}
		}
	}

	public static void aggiornaCompetenzaIR(User user, Object cdnLavoratore) throws Exception {
		String cdnGruppo = "" + user.getCdnGruppo() + "";
		String cdnProfilo = "" + user.getCdnProfilo() + "";
		String strMittente = user.getNome() + " " + user.getCognome();
		String codCpi = user.getCodRif();
		String poloMittente = InfoProvinciaSingleton.getInstance().getCodice();
		int cdnUt = user.getCodut();
		Integer cdnUtente = new Integer(cdnUt);
		String datTrasferimento = DateUtils.getNow();
		String codiceFiscale = "";
		String cognome = "";
		String nome = "";
		String datNasc = "";
		String codComNas = "";

		Object[] params = new Object[1];
		params[0] = cdnLavoratore;

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_DATI_LAV", params, "SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			codiceFiscale = SourceBeanUtils.getAttrStrNotNull(row, "STRCODICEFISCALE");
			cognome = SourceBeanUtils.getAttrStrNotNull(row, "STRCOGNOME");
			nome = SourceBeanUtils.getAttrStrNotNull(row, "STRNOME");
			datNasc = SourceBeanUtils.getAttrStrNotNull(row, "DATNASC");
			codComNas = SourceBeanUtils.getAttrStrNotNull(row, "CODCOMNAS");

			TestataMessageTO testataMessaggio = new TestataMessageTO();
			testataMessaggio.setPoloMittente(poloMittente);
			testataMessaggio.setCdnUtente(cdnUtente.toString());
			testataMessaggio.setCdnGruppo(cdnGruppo);
			testataMessaggio.setCdnProfilo(cdnProfilo);
			testataMessaggio.setStrMittente(strMittente);

			AggiornaCompetenzaIRMessage aggiornaCompetenzaIRMessage = new AggiornaCompetenzaIRMessage();

			aggiornaCompetenzaIRMessage.setTestata(testataMessaggio);

			// imposto i parametri applicativi
			aggiornaCompetenzaIRMessage.setCodCpi(codCpi);
			aggiornaCompetenzaIRMessage.setCodiceFiscale(codiceFiscale);
			aggiornaCompetenzaIRMessage.setCodMonoTipoCpi(CODMONOCPICOMP);
			aggiornaCompetenzaIRMessage.setCpiComp(codCpi);
			aggiornaCompetenzaIRMessage.setdatTrasferimento(datTrasferimento);
			aggiornaCompetenzaIRMessage.setCognome(cognome);
			aggiornaCompetenzaIRMessage.setNome(nome);
			aggiornaCompetenzaIRMessage.setDatNasc(datNasc);
			aggiornaCompetenzaIRMessage.setCodComNas(codComNas);

			try {
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();

				aggiornaCompetenzaIRMessage.setDataSourceJndi(dataSourceJndiName);

				OutQ outQ = new OutQ();

				aggiornaCompetenzaIRMessage.send(outQ);

			} catch (Exception exc) {
				throw new Exception("Errore aggiornamento competenza lavoratore indice regionale");
			}
		}
	}

	public static boolean aggiornaCPI(Object cdnLavoratore, String codCpiRif, BigDecimal cdnUser,
			TransactionQueryExecutor txExecutor) throws Exception {
		boolean cambioCPICompetenza = false;
		SourceBean rowLav = (SourceBean) txExecutor.executeQuery("SELECT_AN_LAV_STORIA_INF",
				new Object[] { cdnLavoratore }, "SELECT");
		if (rowLav != null) {
			rowLav = rowLav.containsAttribute("ROW") ? (SourceBean) rowLav.getAttribute("ROW") : rowLav;
			String codCpiTit = StringUtils.getAttributeStrNotNull(rowLav, "CODCPITIT");
			String codMonoTipoCpi = StringUtils.getAttributeStrNotNull(rowLav, "CODMONOTIPOCPI");
			String codCpiOrig = StringUtils.getAttributeStrNotNull(rowLav, "CODCPIORIG");
			BigDecimal numkloLavStoriaInf = (BigDecimal) rowLav.getAttribute("NUMKLOLAVSTORIAINF");
			String strNote = "Cambio Competenza a seguito DID";
			if (!codMonoTipoCpi.equalsIgnoreCase(CODMONOCPICOMP) || !codCpiTit.equalsIgnoreCase(codCpiRif)) {
				numkloLavStoriaInf = numkloLavStoriaInf.add(new BigDecimal("1"));
				Object params[] = { codCpiRif, cdnUser, CODMONOCPICOMP, null, codCpiOrig, strNote, numkloLavStoriaInf,
						cdnLavoratore };
				Boolean res = (Boolean) txExecutor.executeQuery("SAVE_AN_LAV_STORIA_INF_INSERT_DID", params, "UPDATE");
				if (res == null || !res.booleanValue()) {
					throw new Exception("Errore aggiornamento competenza lavoratore");
				}
				cambioCPICompetenza = true;
			}
		} else {
			throw new Exception("Impossibile verificare la competenza amministrativa");
		}
		return cambioCPICompetenza;
	}
}