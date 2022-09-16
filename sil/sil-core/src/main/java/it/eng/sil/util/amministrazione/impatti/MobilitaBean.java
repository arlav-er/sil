package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class MobilitaBean extends SourceBean implements EventoAmministrativo, Comparable {
	public static final String DB_DAT_INIZIO = "DATINIZIO";
	public static final String DB_DAT_FINE = "DATFINE";
	public static final String DB_DAT_FINE_ORIG = "DATFINEORIG";
	public static final String DB_DAT_MAX_DIFF = "DATMAXDIFF";
	public static final String DB_CDNLAVORATORE = "CDNLAVORATORE";
	public static final String DB_PRG_STATO_OCCUPAZ = "PRGSTATOOCCUPAZ";
	public static final String DB_PRG_MOBILITA_ISCR = "PRGMOBILITAISCR";
	public static final String MOTIVO_DECADENZA_MOB_SOCC_MANUALE = "Z2";
	public static final String motivoDecadenzaTI = "G";
	public static final String motivoDecadenzaReddito = "G1";
	public static final String motivoDecadenza = "A";

	private StatoOccupazionaleBean statoOccupazionale;

	public MobilitaBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public String getDataInizio() {
		return (String) getAttribute(MobilitaBean.DB_DAT_INIZIO);
	}

	protected String getStringAttribute(String a) {
		return this.containsAttribute(a) ? (String) this.getAttribute(a) : "";
	}

	public String getDataFine() {
		return getStringAttribute(MobilitaBean.DB_DAT_FINE);
	}

	public String getDataFineOrig() {
		return getStringAttribute(MobilitaBean.DB_DAT_FINE_ORIG);
	}

	public String getDataMaxDiff() {
		return getStringAttribute(MobilitaBean.DB_DAT_MAX_DIFF);
	}

	public StatoOccupazionaleBean getStatoOccupazionale() {
		return this.statoOccupazionale;
	}

	public void setStatoOccupazionale(StatoOccupazionaleBean newStatoOccupazionale) {
		this.statoOccupazionale = newStatoOccupazionale;
	}

	public int getTipoEventoAmministrativo() {
		return EventoAmministrativo.MOBILITA;
	}

	public boolean isProtocollato() {
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

	public BigDecimal getPrgStatoOccupaz() {
		Object obj = getAttribute(MobilitaBean.DB_PRG_STATO_OCCUPAZ);
		if (obj == null)
			return null;
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	public BigDecimal getPrgMobilitaIscr() {
		Object obj = getAttribute("PRGMOBILITAISCR");
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		} else {
			return new BigDecimal((String) obj);
		}
	}

	public BigDecimal getCdnLavoratore() throws Exception {
		Object obj = getAttribute(MobilitaBean.DB_CDNLAVORATORE);
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	public void aggiornaStatoOccupaz(BigDecimal prgStatoOccupaz, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgMobIscr = (BigDecimal) this.getAttribute("prgMobilitaIscr");
		BigDecimal numKlo = (BigDecimal) this.getAttribute("numKloMobIscr");
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object cdnUser = null;
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");

		Object params[] = new Object[3];
		params[0] = prgStatoOccupaz;
		params[1] = cdnUser;
		params[2] = prgMobIscr;
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_STATOOCCUPAZ_MOBILITA", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiornare la mobilita con prgMobilitaIscr = " + prgMobIscr);
		this.updAttribute("numKloMobIscr", newNumKlo);
	}

	public int compareTo(Object obj) {
		int ret = 0;
		String dataInizio = "";
		boolean eventoChiusuraMob = false;
		try {
			EventoAmministrativo evento = (EventoAmministrativo) obj;
			int tipoEvento = evento.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.CESSAZIONE
					|| tipoEvento == EventoAmministrativo.PROROGA
					|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {

				dataInizio = ((MovimentoBean) evento).getDataInizio();
			} else {
				if (tipoEvento == EventoAmministrativo.DID) {
					dataInizio = evento.getDataInizio();
				} else {
					if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
						dataInizio = evento.getDataFine();
					} else {
						if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA) {
							dataInizio = evento.getDataFine();
							dataInizio = DateUtils.giornoPrecedente(dataInizio);
							eventoChiusuraMob = true;
						} else {
							if (tipoEvento == EventoAmministrativo.MOBILITA) {
								dataInizio = evento.getDataInizio();
							}
						}
					}
				}
			}

			String dataInizioMobilita = this.getDataInizio();
			ret = DateUtils.compare(dataInizioMobilita, dataInizio);
			if (ret == 0) {
				if (eventoChiusuraMob) {
					ret = -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public String getDescrizione() {
		return "Mobilità";
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
		MobilitaBean mobilita = null;
		for (java.util.Iterator iterator = eventi.iterator(); iterator.hasNext();) {
			Object tmpEvento = iterator.next();
			if (tmpEvento instanceof MobilitaBean) {
				mobilita = (MobilitaBean) tmpEvento;
				BigDecimal prgEventoCorrente = (BigDecimal) mobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
				if (prgEventoCorrente.intValue() == ((BigDecimal) prgEvento).intValue()) {
					return (mobilita);
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
	public static List getMobilita(Object cdnLavoratore, TransactionQueryExecutor executor) throws Exception {

		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, executor);
		List listaMobilita = new ArrayList();
		listaMobilita = mobilita.getMobilita();
		return (listaMobilita);
	}

}