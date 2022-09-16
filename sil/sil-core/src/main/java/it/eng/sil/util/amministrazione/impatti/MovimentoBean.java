package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.MovimentoNonCollegatoException;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.util.Utils;

public class MovimentoBean extends SourceBean implements EventoAmministrativo, Comparable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovimentoBean.class.getName());
	public static final int ASSUNZIONE = 0;
	public static final int TRASFORMAZIONE = 1;
	public static final int PROROGA = 2;
	public static final int CESSAZIONE = 3;
	public static final String DB_DATA_INIZIO = "DATINIZIOMOV";
	public static final String DB_DATA_INIZIO_MOV_PREC = "DATINIZIOMOVPREC";
	public static final String DB_DATA_FINE = "DATFINEMOV";
	public static final String DB_DATA_FINE_EFFETTIVA = "DATFINEMOVEFFETTIVA";
	public static final String DB_COD_STATO_ATTO = "CODSTATOATTO";
	public static final String DB_COD_MOVIMENTO = "CODTIPOMOV";
	public static final String DB_PRG_STATO_OCCUPAZ = "PRGSTATOOCCUPAZ";
	public static final String DB_PRG_MOVIMENTO = "PRGMOVIMENTO";
	public static final String DB_PRG_MOVIMENTO_PREC = "PRGMOVIMENTOPREC";
	public static final String DB_CDNLAVORATORE = "CDNLAVORATORE";
	public static final String DB_COD_MONO_TEMPO = "CODMONOTEMPO";
	public static final String DB_COD_MONO_TIPO_FINE = "CODMONOTIPOFINE";
	public static final String DB_PRG_UNITA = "PRGUNITA";
	public static final String DB_PRG_AZIENDA = "PRGAZIENDA";
	public static final String FLAG_IN_INSERIMENTO = "FLAG_IN_INSERIMENTO";
	public static final String FLAG_VIRTUALE = "FLAG_VIRTUALE";
	public static final String COD_ASSUNZIONE = "AVV";
	public static final String COD_TRASFORMAZIONE = "TRA";
	public static final String COD_PROROGA = "PRO";
	public static final String COD_CESSAZIONE = "CES";
	public static final String DB_STATO_OCCUPAZ = "PRGSTATOOCCUPAZ";
	public static final String DB_NUM_K_LOCK = "NUMKLOMOV";
	public static final String DB_RETRIBUZIONE = "DECRETRIBUZIONEMEN";
	public static final String DB_RETRIBUZIONE_SANATA = "DECRETRIBUZIONEMENSANATA";
	public static final String DB_COD_TIPO_DICH = "CODTIPODICH";
	public static final String DB_PRG_MOVIMENTO_SUCC = "PRGMOVIMENTOSUCC";
	public static final String DB_DATA_SIT_SANATA = "DATSITSANATA";
	public static final String DB_TIPO_DICH_SANATA = "TIPODICHSANATA";
	public static final String DB_PRG_DICH_LAV = "PRGDICHLAV";
	public static final String DB_DATA_INIZIO_MOV_REDDITO = "DATINIZIOMOVSUPREDDITO";
	public static final String REQ_NUM_K_LO_MOV_PREC_CAMBIATO = "NUMKLOMOVPREC_CAMBIATO";
	public static final String REQ_NUM_K_LO_MOV_PREC = "NUMKLOMOVPREC";
	public static final String DB_FLG_MOD_REDDITO = "FLGMODREDDITO";
	public static final String DB_FLG_MOD_TEMPO = "FLGMODTEMPO";
	public static final String DB_COD_MOTIVO_CESSAZIONE = "CODMVCESSAZIONE";
	public static final int MOBILITA = 4;
	public static final int DIMISSIONI = 5;
	public static final String DB_DATA_COMUNICAZIONE = "DATCOMUNICAZ";
	public static final String REQ_DATA_INIZIO_AVV = "DATINIZIOMOVPREC";
	public static final String REQ_DATA_FINE_PREC = "DATAFINEMOVPREC";
	public static final String DB_PRG_UNITA_VAL_MAS = "PRGUNITAPRODUTTIVA";
	public static final String DB_COD_CONTRATTO = "CODCONTRATTO";
	public static final String DB_COD_ORARIO = "CODORARIO";
	public static final String DB_COD_MONO_TIPO_ASS = "CODMONOTIPO";
	public static final String DB_FLG_SOSPENSIONE_2014 = "FLGSOSPENSIONE2014";
	public static final String SI = "S";
	public static final String NO = "N";

	public static final String UNILAV = "0";
	public static final String UNISOMM = "3";
	public static final String VARDATORI = "2";
	public static final String VERSIONE_TRACCIATO_05_11 = "4,0,0";
	/**
	 * Indica se l'operatore ha confermato la forzatura dell'operazione per sanara la sit. amm.
	 */
	public static final String FLAG_OP_SANARE_FORZATURA = "FLAG_OP_SANARE_FORZATURA";
	/**
	 * Indica il movimento che non deve causare impatti nei controlli dell'operazione di sanamento situazione
	 */

	/**
	 * Indica se l'operazione per sanare la sit. amm. e' di dettaglio o generica. Puo' valere 'S' o 'N'
	 */
	public static final String FLAG_OP_SANARE_DETTAGLIO = "FLAG_OP_SANARE_DETTAGLIO";
	/**
	 * Indica se l'operazione per sanare la situazione amm. e' di superamento o mancato superamento del limite. Puo'
	 * valere 'S' o 'N'
	 */
	public static final String FLAG_OP_SANARE_SUPERAMENTO = "FLAG_OP_SANARE_SUPERAMENTO";
	public static final String FLAG_OP_SANARE_NON_GESTIRE = "FLAG_OP_SANARE_NON_GESTIRE";
	public static final String DB_COD_ASSUNZIONE = "CODTIPOASS";
	public static final String CONTESTO_OPERATIVO = "CONTEXT";
	public static final String CONTESTO_VALIDAZIONE_MASSIVA = "validazioneMassiva";

	/**
	 * Indica se e' in corso l'operazione per sanare la situazione amministrativa del lavoratore
	 */
	public static final String FLAG_OP_SANARE = "FLAG_OP_SANARE";
	public static final String CODMONOTIPOAUTONOMO = "N";
	public static final String RAPPORTOAUTONOMO = "AUTONOMO";
	public static final String RAPPORTOSUBORDINATO = "SUBORDINATO";
	public static final String RAPPORTOPARASUBORDINATO = "PARASUBORDINATO";

	private StatoOccupazionaleBean statoOccupazionale;
	private Reddito reddito;
	private boolean cessato;
	private MovimentoBean movimentoBack;
	private MovimentoBean movimentoNext;
	private static Map movimenti = null;

	static {
		movimenti = new HashMap();
		movimenti.put(MovimentoBean.COD_ASSUNZIONE, new BigInteger(String.valueOf(MovimentoBean.ASSUNZIONE)));
		movimenti.put(MovimentoBean.COD_CESSAZIONE, new BigInteger(String.valueOf(MovimentoBean.CESSAZIONE)));
		movimenti.put(MovimentoBean.COD_PROROGA, new BigInteger(String.valueOf(MovimentoBean.PROROGA)));
		movimenti.put(MovimentoBean.COD_TRASFORMAZIONE, new BigInteger(String.valueOf(MovimentoBean.TRASFORMAZIONE)));
	}

	public MovimentoBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	public int getTipoEventoAmministrativo() {
		int codiceMovimento = 0;
		String tipoMovimento = getAttribute("CODTIPOMOV").toString().toUpperCase();
		if (tipoMovimento.equals(MovimentoBean.COD_ASSUNZIONE)) {
			codiceMovimento = EventoAmministrativo.AVVIAMENTO;
		} else {
			if (tipoMovimento.equals(MovimentoBean.COD_TRASFORMAZIONE)) {
				codiceMovimento = EventoAmministrativo.TRASFORMAZIONE;
			} else {
				if (tipoMovimento.equals(MovimentoBean.COD_PROROGA)) {
					codiceMovimento = EventoAmministrativo.PROROGA;
				} else {
					if (tipoMovimento.equals(MovimentoBean.COD_CESSAZIONE)) {
						codiceMovimento = EventoAmministrativo.CESSAZIONE;
					}
				}
			}
		}
		return codiceMovimento;
	}

	public int getTipoContratto() {
		return Contratto.getTipoContratto(this);
	}

	protected String getStringAttribute(String a) {
		return this.containsAttribute(a) ? (String) this.getAttribute(a) : null;
	}

	protected String getStringAttributeNotNull(String a) {
		String ret = getStringAttribute(a);
		return (ret == null) ? "" : ret;
	}

	public int getTipoMovimento() {
		int ret = -1;
		String tipoMov = (String) getAttribute(DB_COD_MOVIMENTO);
		if (tipoMov != null) {
			ret = ((BigInteger) movimenti.get(tipoMov)).intValue();
		}
		return ret;
	}

	public static int getTipoMovimento(SourceBean movimento) {
		int ret = -1;
		String tipoMov = (String) movimento.getAttribute("CODTIPOMOV");
		if (tipoMov != null) {
			ret = ((BigInteger) movimenti.get(tipoMov)).intValue();
		}
		return ret;
	}

	public void setStatoOccupazionale(StatoOccupazionaleBean newSO) throws Exception {
		this.statoOccupazionale = newSO;
		updAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, newSO.getPrgStatoOccupaz());
	}

	public StatoOccupazionaleBean getStatoOccupazionale() {
		return this.statoOccupazionale;
	}

	public void updStatoOccupazionale(StatoOccupazionaleBean newSO) {
		if (this.statoOccupazionale == null) {
			this.statoOccupazionale = newSO;
		}
	}

	public Reddito getReddito() {
		return this.reddito;
	}

	public void setReddito(Reddito newReddito) {
		this.reddito = newReddito;
	}

	public String getDataInizio() {
		return getStringAttribute(MovimentoBean.DB_DATA_INIZIO);
	}

	public String getDataInizioAvv() {
		return getStringAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC);
	}

	public String getDataFine() {
		return getStringAttribute(MovimentoBean.DB_DATA_FINE);
	}

	public String getDataFineEffettiva() {
		return getStringAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
	}

	public boolean isProtocollato() {
		String codStatoAtto = containsAttribute(MovimentoBean.DB_COD_STATO_ATTO)
				? (String) getAttribute(MovimentoBean.DB_COD_STATO_ATTO)
				: "";
		if (codStatoAtto.toUpperCase().equals("PR"))
			return true;
		else
			return false;
	}

	public static boolean protocollato(SourceBean m) throws Exception {
		String codStatoAtto = (String) m.getAttribute(MovimentoBean.DB_COD_STATO_ATTO);
		return codStatoAtto != null && codStatoAtto.equals("PR");
	}

	public boolean isDataFutura() throws Exception {
		String dataInizio = getDataInizio();
		String oggi = DateUtils.getNow();
		int resultCompare = DateUtils.compare(dataInizio, oggi);
		if (resultCompare > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPrecNormativa(String dataRifNormativa) throws Exception {
		String dataInizio = getDataInizio();
		int resultCompare = DateUtils.compare(dataInizio, dataRifNormativa);
		if (resultCompare < 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCessato() throws Exception {
		return cessato;
	}

	public void setCessato(boolean c) throws Exception {
		this.cessato = c;
	}

	public MovimentoBean getMovimentoBack() {
		return this.movimentoBack;
	}

	public MovimentoBean getMovimentoNext() {
		return this.movimentoNext;
	}

	public void setMovimentoBack(MovimentoBean newMov) {
		this.movimentoBack = newMov;
		newMov.setMovimentoNext(this);
	}

	public void setMovimentoNext(MovimentoBean newMov) {
		this.movimentoNext = newMov;
	}

	public MovimentoBean getCollegato() {
		return this.movimentoBack;
	}

	public String toString() {
		return " MOVIMENTO " + getAttribute(MovimentoBean.DB_COD_MOVIMENTO) + ", prg=" + getPrgMovimento()
				+ ", datInizioMov=" + getDataInizio() + ", datFineMov=" + getDataFine() + ", datFineEffetiva="
				+ getDataFineEffettiva() + ", prgStOcc=" + getPrgStatoOccupaz();
	}

	public BigDecimal getPrgStatoOccupaz() {
		Object obj = getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
		if (obj == null)
			return null;
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	public BigDecimal getPrgMovimento() {
		Object obj = getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (obj == null)
			return null;
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	public boolean virtuale() {
		return containsAttribute(MovimentoBean.FLAG_VIRTUALE);
	}

	public String getCodMonoTipoFine() {
		return (String) getAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
	}

	public String getCodMonoTempo() {
		return containsAttribute(MovimentoBean.DB_COD_MONO_TEMPO)
				? (String) getAttribute(MovimentoBean.DB_COD_MONO_TEMPO)
				: null;
	}

	public BigDecimal getCdnLavoratore() throws Exception {
		Object obj = getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	public static String getCdnLavoratore(SourceBean m) throws Exception {
		Object obj = m.getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		if (obj instanceof BigDecimal)
			return ((BigDecimal) obj).toString();
		else
			return (String) obj;
	}

	/**
	 * Si tratta del movimento che si sta inserendo manualmente e che non e' ancora presente nel db
	 * 
	 * @return
	 */
	public boolean inInserimento() {
		return (getSource().containsAttribute(MovimentoBean.FLAG_IN_INSERIMENTO));
	}

	/**
	 * E' il movimento collegato a quello che si sta inserendo. Quindi nel caso di una cessazione si trattera' del
	 * movimento cessato.
	 * 
	 * @return
	 */
	public boolean precInInserimento() {
		return (getMovimentoNext() != null
				&& getMovimentoNext().getSource().containsAttribute(MovimentoBean.FLAG_IN_INSERIMENTO));
	}

	public boolean esisteMovimentoNext(MovimentoBean m)
			throws ControlliException, Exception, MovimentoNonCollegatoException {
		String dataInizioAvv = m.getDataInizioAvv();
		String dataFine = null;

		// prima tento con i progressivi
		BigDecimal prgBack = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		BigDecimal prg = (BigDecimal) getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (prg != null && prgBack != null && prg.equals(prgBack))
			return true;

		// se uno dei progressivi e' null significa che sto
		// inserendo uno dei due (ovvero una cessazione)
		switch (m.getTipoMovimento()) {
		case MovimentoBean.CESSAZIONE:
			dataFine = m.getDataInizio();
			break;

		case MovimentoBean.TRASFORMAZIONE:
		case MovimentoBean.PROROGA:
			dataFine = DateUtils.giornoPrecedente(m.getDataInizio());
			break;

		default:
			throw new ControlliException(MessageCodes.StatoOccupazionale.ERRORE_GENERICO);
		}
		try {
			String dataInizioMov = getDataInizio();
			String dataFineMov = getDataFine();
			String dataFineMovEff = getDataFineEffettiva();
			return (dataInizioMov != null && !dataInizioMov.equals("") && dataInizioAvv != null
					&& !dataInizioAvv.equals("") && DateUtils.compare(dataInizioMov, dataInizioAvv) == 0)
					&& ((dataFineMov != null && !dataFineMov.equals("")
							&& DateUtils.compare(dataFineMov, dataFine) == 0)
							&& (dataFineMovEff != null && !dataFineMovEff.equals("")
									&& DateUtils.compare(dataFineMovEff, dataFine) == 0)
							&& (getAttribute(MovimentoBean.DB_PRG_AZIENDA)
									.equals(m.getAttribute(MovimentoBean.DB_PRG_AZIENDA))
									&& getAttribute(MovimentoBean.DB_PRG_UNITA)
											.equals(m.getAttribute(MovimentoBean.DB_PRG_UNITA))));
		} catch (Exception e) {
			throw new MovimentoNonCollegatoException(55555);
		}
	}

	public boolean cessazioneAnticipata() throws Exception {
		String dataFineEffettiva = (String) getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		if (dataFineEffettiva == null) {
			MovimentoBean mn = getMovimentoNext();
			if (mn == null) {
				return false;
			}
			if (mn.getTipoMovimento() == MovimentoBean.CESSAZIONE) {
				dataFineEffettiva = mn.getDataInizio();
			} else if (mn.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE) {
				dataFineEffettiva = DateUtils.giornoPrecedente(mn.getDataInizio());
			} else {
				return false;
			}
		}
		String codMonoTempo = (String) getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		if (codMonoTempo == null || codMonoTempo.equals("I"))
			return true;
		String dataFine = (String) getAttribute(MovimentoBean.DB_DATA_FINE);
		return DateUtils.compare(dataFineEffettiva, dataFine) < 0;
	}

	public int compareTo(Object obj) {
		int ret = 0;
		EventoAmministrativo m = null;
		try {
			m = (EventoAmministrativo) obj;
			int tipoEvento = m.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.DID) {
				DidBean did = (DidBean) m;
				String dataDichiarazione = (String) did.getAttribute("datDichiarazione");
				ret = DateUtils.compare(getDataInizio(), dataDichiarazione);
				if (ret == 0) {
					ret = 1;
				}
			} else if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
				DidBean did = (DidBean) m;
				String dataDichiarazione = (String) did.getAttribute("datFine");
				ret = DateUtils.compare(getDataInizio(), dataDichiarazione);
				if (ret == 0) {
					ret = 1;
				}
			} else if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA) {
				ChiusuraMobilitaBean chMobilita = (ChiusuraMobilitaBean) m;
				String dataDichiarazione = chMobilita.getDataInizio();
				dataDichiarazione = DateUtils.giornoPrecedente(dataDichiarazione);
				ret = DateUtils.compare(getDataInizio(), dataDichiarazione);
				// I movimenti precedono gli eventi di CHIUSURA_MOBILITA.
				// Se la mobilità finisce il giorno X, l'evento CHIUSURA_MOBILITA
				// scatta il giorno X + 1.
				if (ret == 0) {
					ret = -1;
				}
			} else if (tipoEvento == EventoAmministrativo.MOBILITA) {
				MobilitaBean mobilita = (MobilitaBean) m;
				String dataDichiarazione = mobilita.getDataInizio();
				ret = DateUtils.compare(getDataInizio(), dataDichiarazione);
				if (ret == 0) {
					ret = 1;
				}
			} else {
				MovimentoBean mb = (MovimentoBean) m;
				String dataInizio = mb.getDataInizio();
				ret = DateUtils.compare(getDataInizio(), dataInizio);
				if (ret == 0) {
					if (getMovimentoBack() != null && getMovimentoBack().equals(mb)) {
						ret = 1;
					} else {
						if (mb.getMovimentoNext() != null && mb.getMovimentoNext().equals(this))
							return -1;
						if ((virtuale() || inInserimento()) && getTipoMovimento() == MovimentoBean.CESSAZIONE)
							return 1;
						if ((mb.virtuale() || mb.inInserimento()) && mb.getTipoMovimento() == MovimentoBean.CESSAZIONE)
							return -1;
						if (getTipoMovimento() == MovimentoBean.ASSUNZIONE && inInserimento()
								&& mb.getTipoMovimento() == MovimentoBean.CESSAZIONE)
							return -1;
						if (getTipoMovimento() == MovimentoBean.CESSAZIONE
								&& mb.getTipoMovimento() == MovimentoBean.ASSUNZIONE && mb.inInserimento())
							return 1;
						if (statoOccupazionale == null || statoOccupazionale.equals(mb.getStatoOccupazionale())) {
							if (getTipoMovimento() == MovimentoBean.CESSAZIONE
									&& (mb.getTipoMovimento() == MovimentoBean.ASSUNZIONE
											|| mb.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE
											|| mb.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE))
								return 1;
							else if (mb.getTipoMovimento() == MovimentoBean.CESSAZIONE
									&& (getTipoMovimento() == MovimentoBean.ASSUNZIONE
											|| getTipoMovimento() == MovimentoBean.TRASFORMAZIONE
											|| getTipoMovimento() == MovimentoBean.TRASFORMAZIONE))
								return -1;
							else
								return 0;
						}
						StatoOccupazionaleBean sOcc = mb.getStatoOccupazionale();
						for (; sOcc != null && (statoOccupazionale.getPrgStatoOccupaz().intValue() != sOcc
								.getPrgStatoOccupaz().intValue() || sOcc.getPrgStatoOccupaz().intValue() == -1);) {
							sOcc = sOcc.getStatoOccupazionaleNext();
						}
						if (sOcc != null)
							return 1;
						sOcc = mb.getStatoOccupazionale();
						for (; sOcc != null && (statoOccupazionale.getPrgStatoOccupaz().intValue() != sOcc
								.getPrgStatoOccupaz().intValue() || sOcc.getPrgStatoOccupaz().intValue() == -1);) {
							sOcc = sOcc.getStatoOccupazionaleBack();
						}
						if (sOcc != null && sOcc.getPrgStatoOccupaz().intValue() == -1)
							return -1;
						if (sOcc != null)
							return -1;
						else
							return 0;
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "MovimentoBean.compareTo():" + m + ", " + this, e);

		}
		return ret;
	}

	public String getDescrizione() {

		String descrizione = "";

		switch (getTipoEventoAmministrativo()) {
		case EventoAmministrativo.AVVIAMENTO:
			descrizione = "Avviamento";
			break;
		case EventoAmministrativo.TRASFORMAZIONE:
			descrizione = "Trasformazione";
			break;
		case EventoAmministrativo.PROROGA:
			descrizione = "Proroga";
			break;
		case EventoAmministrativo.CESSAZIONE:
			descrizione = "Cessazione";
			break;
		}

		return (descrizione);
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
		MovimentoBean movimento = null;
		for (Iterator iterator = eventi.iterator(); iterator.hasNext();) {
			Object tmpEvento = iterator.next();
			if (tmpEvento instanceof MovimentoBean) {
				movimento = (MovimentoBean) tmpEvento;
				BigDecimal prgEventoCorrente = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				if (prgEventoCorrente.intValue() == ((BigDecimal) prgEvento).intValue()) {
					return (movimento);
				}
			}
		}
		// lancio l'eccezione se non trovo l'evento desiderato
		throw new ControlliException(MessageCodes.EventoAmministrativo.SEARCH_ERROR);

	}

	/**
	 * restituisce i movimenti associati al lavoratore protocollati e non in data futura
	 * 
	 * @param cdnLavoratore
	 */
	public static List getMovimenti(Object cdnLavoratore, TransactionQueryExecutor transactionQueryExecutor)
			throws Exception {

		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		Vector movimenti = null;

		SourceBean res = (SourceBean) transactionQueryExecutor.executeQuery("GET_TUTTI_MOVIMENTI_LAVORATORE", params,
				"SELECT");
		if (res != null)
			movimenti = res.getAttributeAsVector("ROW");

		if (movimenti == null) {
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		}
		return new ArrayList(movimenti);

	}

	/**
	 * restituisce solo i movimenti data la lista degli eventi amministrativi
	 */
	public static Vector getMovimenti(List eventi) throws Exception {
		Vector movimenti = new Vector();
		for (int j = 0; j < eventi.size(); j++) {
			SourceBean tmpEvento = (SourceBean) eventi.get(j);
			if (tmpEvento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)) {
				movimenti.add(tmpEvento);
			}
		}
		return (movimenti);
	}

	public static Vector gestisciTuttiPeriodiIntermittenti(java.util.List rows, TransactionQueryExecutor txExecutor)
			throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		Vector periodi = null;
		Vector movimentiIntermittenti = new Vector();
		boolean aggiuntoPeriodoIntermittente = false;
		String dataOggi = DateUtils.getNow();
		for (int i = 0; i < rowsSize; i++) {
			boolean isRapportoIntermittente = false;
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();

			Object prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			Object prgMovimentoIntermittente = prgMovimento;

			Vector periodiAvv = MovimentoBean.caricaPeriodiLavorativi(new BigDecimal(prgMovimento.toString()), null);
			if (periodiAvv != null && periodiAvv.size() > 0) {
				int periodiSize = periodiAvv.size();
				boolean exit = false;
				for (int iPer = 0; iPer < periodiSize && !exit; iPer++) {
					SourceBean mPer = (SourceBean) periodiAvv.get(iPer);
					if (mPer.containsAttribute("VALIDO")
							&& mPer.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
						isRapportoIntermittente = true;
						prgMovimentoIntermittente = (BigDecimal) mPer.getAttribute("PRGMOVIMENTO");
						exit = true;
					}
				}
			}

			if (isRapportoIntermittente) {
				if (!movimentiIntermittenti.contains(prgMovimentoIntermittente)) {
					SourceBean res = null;
					Object params[] = new Object[1];
					params[0] = prgMovimentoIntermittente;
					if (txExecutor != null) {
						res = (SourceBean) txExecutor
								.executeQuery("GET_TUTTI_PERIODI_MOVIMENTI_LAVORATORE_INTERMITTENTI", params, "SELECT");
					} else {
						res = (SourceBean) QueryExecutor.executeQuery(
								"GET_TUTTI_PERIODI_MOVIMENTI_LAVORATORE_INTERMITTENTI", params, "SELECT",
								Values.DB_SIL_DATI);
					}
					if (res != null) {
						periodi = res.getAttributeAsVector("ROW");
						if (periodi != null && periodi.size() > 0) {
							int nsizePeriodi = periodi.size();
							boolean addPeriodi = true;
							int numGiorniIntermittenti = 0;
							String dataFineSosp = null;
							String dataFinePeriodoIntermittente = null;
							for (int j = 0; j < nsizePeriodi; j++) {
								SourceBean periodo = (SourceBean) periodi.get(j);
								if (periodo.containsAttribute("GIORNI")) {
									numGiorniIntermittenti = numGiorniIntermittenti
											+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
								}
								dataFinePeriodoIntermittente = periodo.containsAttribute("datFineMov")
										? periodo.getAttribute("datFineMov").toString()
										: null;
							}
							if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
									&& dataFinePeriodoIntermittente != null) {
								if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
									int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
									dataFineSosp = DateUtils.giornoSuccessivo(DateUtils
											.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
									if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
										// Gestione decadenza inizio rapporto per intermittenti che superano i 180
										// giorni
										addPeriodi = false;
									}
								} else {
									// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
									addPeriodi = false;
								}
							}
							if (addPeriodi) {
								aggiuntoPeriodoIntermittente = true;
								movimentiIntermittenti.add(prgMovimentoIntermittente);
								if (!prgMovimentoIntermittente.equals(prgMovimento)) {
									if (!movimentiIntermittenti.contains(prgMovimento)) {
										movimentiIntermittenti.add(prgMovimento);
									}
								}
								for (int j = 0; j < nsizePeriodi; j++) {
									Object op = periodi.get(j);
									SourceBean mp = null;
									if (op instanceof SourceBean)
										mp = (SourceBean) op;
									else
										mp = ((MovimentoBean) op).getSource();

									v.add(mp);
								}
							} else {
								// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
								v.add(m);
							}
						}
					}
				}
			} else {
				v.add(m);
			}
		}

		if (aggiuntoPeriodoIntermittente) {
			// la lista deve essere riordinata
			v = ordinaListaMovimenti(v);
		}
		return v;
	}

	// Decreto 05/11/2019
	public static SourceBean recuperaMovimento(BigDecimal prgMovimento) throws Exception {
		SourceBean res = null;
		Object params[] = new Object[1];
		params[0] = prgMovimento;
		res = (SourceBean) QueryExecutor.executeQuery("GET_MOVIMENTO_DA_SANARE", params, "SELECT", Values.DB_SIL_DATI);
		return res;
	}

	public static Vector gestisciTuttiPeriodiIntermittentiApertiDa(java.util.List rows, String dataRif,
			TransactionQueryExecutor txExecutor) throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		Vector periodi = null;
		Vector movimentiIntermittenti = new Vector();
		boolean aggiuntoPeriodoIntermittente = false;
		String dataOggi = DateUtils.getNow();
		for (int i = 0; i < rowsSize; i++) {
			boolean isRapportoIntermittente = false;
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();

			Object prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			Object prgMovimentoIntermittente = prgMovimento;

			Vector periodiAvv = MovimentoBean.caricaPeriodiLavorativi(new BigDecimal(prgMovimento.toString()), null);
			if (periodiAvv != null && periodiAvv.size() > 0) {
				int periodiSize = periodiAvv.size();
				boolean exit = false;
				for (int iPer = 0; iPer < periodiSize && !exit; iPer++) {
					SourceBean mPer = (SourceBean) periodiAvv.get(iPer);
					if (mPer.containsAttribute("VALIDO")
							&& mPer.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
						isRapportoIntermittente = true;
						prgMovimentoIntermittente = (BigDecimal) mPer.getAttribute("PRGMOVIMENTO");
						exit = true;
					}
				}
			}

			if (isRapportoIntermittente) {
				if (!movimentiIntermittenti.contains(prgMovimentoIntermittente)) {
					SourceBean res = null;
					Object params[] = new Object[3];
					params[0] = prgMovimentoIntermittente;
					params[1] = dataRif;
					params[2] = dataRif;
					if (txExecutor != null) {
						res = (SourceBean) txExecutor.executeQuery("GET_PERIODI_INTERMITTENTI_APERTI_DA", params,
								"SELECT");
					} else {
						res = (SourceBean) QueryExecutor.executeQuery("GET_PERIODI_INTERMITTENTI_APERTI_DA", params,
								"SELECT", Values.DB_SIL_DATI);
					}
					if (res != null) {
						periodi = res.getAttributeAsVector("ROW");
						if (periodi != null && periodi.size() > 0) {
							int nsizePeriodi = periodi.size();
							boolean addPeriodi = true;
							int numGiorniIntermittenti = 0;
							String dataFineSosp = null;
							String dataFinePeriodoIntermittente = null;
							for (int j = 0; j < nsizePeriodi; j++) {
								SourceBean periodo = (SourceBean) periodi.get(j);
								if (periodo.containsAttribute("GIORNI")) {
									numGiorniIntermittenti = numGiorniIntermittenti
											+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
								}
								dataFinePeriodoIntermittente = periodo.containsAttribute("datFineMov")
										? periodo.getAttribute("datFineMov").toString()
										: null;
							}
							if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
									&& dataFinePeriodoIntermittente != null) {
								if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
									int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
									dataFineSosp = DateUtils.giornoSuccessivo(DateUtils
											.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
									if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
										// Gestione decadenza inizio rapporto per intermittenti che superano i 180
										// giorni
										addPeriodi = false;
									}
								} else {
									// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
									addPeriodi = false;
								}
							}
							if (addPeriodi) {
								aggiuntoPeriodoIntermittente = true;
								movimentiIntermittenti.add(prgMovimentoIntermittente);
								if (!prgMovimentoIntermittente.equals(prgMovimento)) {
									if (!movimentiIntermittenti.contains(prgMovimento)) {
										movimentiIntermittenti.add(prgMovimento);
									}
								}
								for (int j = 0; j < nsizePeriodi; j++) {
									Object op = periodi.get(j);
									SourceBean mp = null;
									if (op instanceof SourceBean)
										mp = (SourceBean) op;
									else
										mp = ((MovimentoBean) op).getSource();

									v.add(mp);
								}
							} else {
								// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
								v.add(m);
							}
						}
					}
				}
			} else {
				v.add(m);
			}
		}

		if (aggiuntoPeriodoIntermittente) {
			// la lista deve essere riordinata
			v = ordinaListaMovimenti(v);
		}
		return v;
	}

	public static Vector gestisciTuttiPeriodiIntermittentiApertiAnno(java.util.List rows, String dataRif,
			TransactionQueryExecutor txExecutor) throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		Vector periodi = null;
		Vector movimentiIntermittenti = new Vector();
		boolean aggiuntoPeriodoIntermittente = false;
		String dataOggi = DateUtils.getNow();
		for (int i = 0; i < rowsSize; i++) {
			boolean isRapportoIntermittente = false;
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();

			Object prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			Object prgMovimentoIntermittente = prgMovimento;

			Vector periodiAvv = MovimentoBean.caricaPeriodiLavorativi(new BigDecimal(prgMovimento.toString()), null);
			if (periodiAvv != null && periodiAvv.size() > 0) {
				int periodiSize = periodiAvv.size();
				boolean exit = false;
				for (int iPer = 0; iPer < periodiSize && !exit; iPer++) {
					SourceBean mPer = (SourceBean) periodiAvv.get(iPer);
					if (mPer.containsAttribute("VALIDO")
							&& mPer.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
						isRapportoIntermittente = true;
						prgMovimentoIntermittente = (BigDecimal) mPer.getAttribute("PRGMOVIMENTO");
						exit = true;
					}
				}
			}

			if (isRapportoIntermittente) {
				if (!movimentiIntermittenti.contains(prgMovimentoIntermittente)) {
					SourceBean res = null;
					Object params[] = new Object[2];
					params[0] = prgMovimentoIntermittente;
					params[1] = dataRif;
					if (txExecutor != null) {
						res = (SourceBean) txExecutor.executeQuery("GET_PERIODI_INTERMITTENTI_APERTI_IN_DATA", params,
								"SELECT");
					} else {
						res = (SourceBean) QueryExecutor.executeQuery("GET_PERIODI_INTERMITTENTI_APERTI_IN_DATA",
								params, "SELECT", Values.DB_SIL_DATI);
					}
					if (res != null) {
						periodi = res.getAttributeAsVector("ROW");
						if (periodi != null && periodi.size() > 0) {
							int nsizePeriodi = periodi.size();
							boolean addPeriodi = true;
							int numGiorniIntermittenti = 0;
							String dataFineSosp = null;
							String dataFinePeriodoIntermittente = null;
							for (int j = 0; j < nsizePeriodi; j++) {
								SourceBean periodo = (SourceBean) periodi.get(j);
								if (periodo.containsAttribute("GIORNI")) {
									numGiorniIntermittenti = numGiorniIntermittenti
											+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
								}
								dataFinePeriodoIntermittente = periodo.containsAttribute("datFineMov")
										? periodo.getAttribute("datFineMov").toString()
										: null;
							}
							if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
									&& dataFinePeriodoIntermittente != null) {
								if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
									int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
									dataFineSosp = DateUtils.giornoSuccessivo(DateUtils
											.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
									if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
										// Gestione decadenza inizio rapporto per intermittenti che superano i 180
										// giorni
										addPeriodi = false;
									}
								} else {
									// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
									addPeriodi = false;
								}
							}
							if (addPeriodi) {
								aggiuntoPeriodoIntermittente = true;
								movimentiIntermittenti.add(prgMovimentoIntermittente);
								if (!prgMovimentoIntermittente.equals(prgMovimento)) {
									if (!movimentiIntermittenti.contains(prgMovimento)) {
										movimentiIntermittenti.add(prgMovimento);
									}
								}
								for (int j = 0; j < nsizePeriodi; j++) {
									Object op = periodi.get(j);
									SourceBean mp = null;
									if (op instanceof SourceBean)
										mp = (SourceBean) op;
									else
										mp = ((MovimentoBean) op).getSource();

									v.add(mp);
								}
							} else {
								// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
								v.add(m);
							}
						}
					}
				}
			} else {
				v.add(m);
			}
		}

		if (aggiuntoPeriodoIntermittente) {
			// la lista deve essere riordinata
			v = ordinaListaMovimenti(v);
		}
		return v;
	}

	public static Vector ordinaListaMovimenti(java.util.List rows) throws Exception {
		int rowsSize = rows.size();
		for (int i = 0; i < rowsSize; i++) {
			Object o1 = rows.get(i);
			SourceBean m1 = null;
			if (o1 instanceof SourceBean)
				m1 = (SourceBean) o1;
			else
				m1 = ((MovimentoBean) o1).getSource();

			String dataInizio1 = m1.getAttribute(MovimentoBean.DB_DATA_INIZIO) != null
					? (String) m1.getAttribute(MovimentoBean.DB_DATA_INIZIO)
					: "";
			for (int j = i + 1; j < rowsSize; j++) {
				Object o2 = rows.get(j);
				SourceBean m2 = null;
				if (o2 instanceof SourceBean)
					m2 = (SourceBean) o2;
				else
					m2 = ((MovimentoBean) o2).getSource();

				String dataInizio2 = m2.getAttribute(MovimentoBean.DB_DATA_INIZIO) != null
						? (String) m2.getAttribute(MovimentoBean.DB_DATA_INIZIO)
						: "";

				if (!dataInizio1.equals("") && !dataInizio2.equals("")
						&& DateUtils.compare(dataInizio2, dataInizio1) < 0) {
					Object app = rows.get(i);
					SourceBean mapp = null;
					if (app instanceof SourceBean)
						mapp = (SourceBean) app;
					else
						mapp = ((MovimentoBean) app).getSource();

					dataInizio1 = dataInizio2;

					rows.remove(i);
					rows.add(i, m2); // indice j al posto indice i
					rows.remove(j);
					rows.add(j, mapp); // indice i al posto indice j
				}
			}
		}
		Vector v = new Vector(rowsSize);
		for (int i = 0; i < rowsSize; i++) {
			Object o1 = rows.get(i);
			v.add(i, o1);
		}
		return v;
	}

	public static Vector caricaPeriodiLavorativi(BigDecimal prgmovimento, TransactionQueryExecutor trans) {
		Vector rows = null;
		SourceBean row = null;
		try {
			Object params[] = new Object[1];
			params[0] = prgmovimento;
			if (trans != null) {
				row = (SourceBean) trans.executeQuery("CERCA_PERIODI_LAVORATIVI_MOVIMENTO_INIZIALE", params, "SELECT");
			} else {
				row = (SourceBean) QueryExecutor.executeQuery("CERCA_PERIODI_LAVORATIVI_MOVIMENTO_INIZIALE", params,
						"SELECT", Values.DB_SIL_DATI);
			}
			rows = row.getAttributeAsVector("ROW");
			return rows;
		} catch (Exception e) {
			return null;
		}
	}

	public static Boolean aggiornaPeriodoLavorativo(BigDecimal prgPeriodo, BigDecimal numklo, String dataInizioPeriodo,
			String dataFinePeriodo, BigDecimal userId, TransactionQueryExecutor trans) {
		try {
			// CALCOLO GIORNI INTERMITTENTI
			int giorniIntermittentiCalc = MovimentoBean.calcolaGGPeriodo(dataInizioPeriodo, dataFinePeriodo);
			BigDecimal ggIntermittenti = new BigDecimal(giorniIntermittentiCalc);
			Object params[] = new Object[6];
			params[0] = dataInizioPeriodo;
			params[1] = dataFinePeriodo;
			params[2] = numklo;
			params[3] = userId;
			params[4] = ggIntermittenti;
			params[5] = prgPeriodo;
			Boolean res = (Boolean) trans.executeQuery("AGGIORNA_DATE_PERIODI_LAVORATIVI", params, "UPDATE");
			return res;
		} catch (Exception e) {
			return null;
		}
	}

	public static Boolean cancellaPeriodoLavorativo(BigDecimal prgPeriodo, TransactionQueryExecutor trans) {
		Vector rows = null;
		try {
			Object params[] = new Object[1];
			params[0] = prgPeriodo;
			Boolean res = (Boolean) trans.executeQuery("CANCELLA_PERIODO_LAVORATIVO", params, "DELETE");
			return res;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean riallineamenoPeriodiIntermittenti(BigDecimal prgMovimento, String dataInizioMovRett,
			String dataFineMovRett, String codTipoMov, ArrayList warnings, BigDecimal userId, String operazione,
			TransactionQueryExecutor trans, Map record) {
		try {
			Vector periodi = MovimentoBean.caricaPeriodiLavorativi(prgMovimento, trans);
			if (periodi != null && periodi.size() > 0) {
				int sizePeriodi = periodi.size();
				boolean fattoRiallineamento = false;
				String dataCheckPeriodoInizio = "";
				String dataCheckPeriodoFine = "";
				boolean cancellaPeriodi = false;
				boolean exit = false;
				if (codTipoMov.equalsIgnoreCase("AVV")) {
					if (operazione.equalsIgnoreCase("ANNULLA")) {
						cancellaPeriodi = true;
						fattoRiallineamento = true;
					} else {
						dataCheckPeriodoInizio = Utils.notNull(dataInizioMovRett);
						dataCheckPeriodoFine = Utils.notNull(dataFineMovRett);
						record.put("PRGMOVRETTPERIODO", prgMovimento);
					}
				} else {
					if ((codTipoMov.equalsIgnoreCase("CES"))
							|| (codTipoMov.equalsIgnoreCase("TRA") && operazione.equalsIgnoreCase("RETTIFICA"))) {
						// se viene chiamata con operazione = ANNULLA i periodi sono ok, perché al massimo la data fine
						// rapporto diventa successiva
						// se viene chiamata con operazione = RETTIFICA, i periodi vengono riallineati quando si
						// inserisce la nuova cessazione in ControllaCessaz
						// se viene chiamata con operazione = RETTIFICA e tipo mov = TRA, allora la data fine rapporto
						// non cambia e quindi i periodi sono OK
						exit = true;
					} else {
						if (codTipoMov.equalsIgnoreCase("PRO") && operazione.equalsIgnoreCase("RETTIFICA")) {
							dataCheckPeriodoFine = Utils.notNull(dataFineMovRett);
						} else {
							if ((codTipoMov.equalsIgnoreCase("PRO") || codTipoMov.equalsIgnoreCase("TRA"))
									&& operazione.equalsIgnoreCase("ANNULLA")) {
								// Devo recuperare la data fine del rapporto precedente la proroga
								SourceBean rowPrec = (SourceBean) trans.executeQuery("GET_INFO_MOVIMENTO_PRECEDENTE",
										new Object[] { prgMovimento }, TransactionQueryExecutor.SELECT);
								if (rowPrec != null) {
									rowPrec = rowPrec.containsAttribute("ROW")
											? (SourceBean) rowPrec.getAttribute("ROW")
											: rowPrec;
									dataCheckPeriodoFine = rowPrec.containsAttribute("datfinemov")
											? rowPrec.getAttribute("datfinemov").toString()
											: "";
								}
							} else {
								exit = true;
							}
						}
					}
				}
				for (int i = 0; i < sizePeriodi && !exit; i++) {
					SourceBean periodo = (SourceBean) periodi.get(i);
					BigDecimal prgPeriodo = (BigDecimal) periodo.getAttribute("PRGPERIODOLAV");
					Boolean resInterm = null;
					if (cancellaPeriodi) {
						resInterm = MovimentoBean.cancellaPeriodoLavorativo(prgPeriodo, trans);
						periodo.setAttribute("FLGCANCELLATO", "S");
						if (resInterm == null || !resInterm.booleanValue()) {
							throw new Exception(
									"Errore nella gestione dei periodi intermittenti in fase di riallineamento date.");
						}
					} else {
						String dataInizioPer = StringUtils.getAttributeStrNotNull(periodo, "DATAINIZIO");
						String dataFinePer = StringUtils.getAttributeStrNotNull(periodo, "DATAFINE");
						if (dataCheckPeriodoFine.equals("")) {
							exit = true;
						} else {
							if (DateUtils.compare(dataFinePer, dataCheckPeriodoFine) <= 0) {
								exit = true;
							} else {
								if (DateUtils.compare(dataInizioPer, dataCheckPeriodoFine) <= 0) {
									BigDecimal numkloPeriodo = (BigDecimal) periodo.getAttribute("NUMKLOPERIODOLAV");
									numkloPeriodo = numkloPeriodo.add(new BigDecimal(1));
									resInterm = MovimentoBean.aggiornaPeriodoLavorativo(prgPeriodo, numkloPeriodo,
											dataInizioPer, dataCheckPeriodoFine, userId, trans);
									periodo.updAttribute("NUMKLOPERIODOLAV", numkloPeriodo);
									periodo.updAttribute("DATAFINE", dataCheckPeriodoFine);
									fattoRiallineamento = true;
									exit = true;
								} else {
									// cancellazione periodo
									resInterm = MovimentoBean.cancellaPeriodoLavorativo(prgPeriodo, trans);
									periodo.setAttribute("FLGCANCELLATO", "S");
									fattoRiallineamento = true;
								}
								if (resInterm == null || !resInterm.booleanValue()) {
									throw new Exception(
											"Errore nella gestione dei periodi intermittenti in fase di riallineamento date.");
								}
							}
						}
					}
				}
				// in caso solo di rettifica avviamento può cambiare anche la data inizio movimento
				boolean exitInit = false;
				if (!dataCheckPeriodoInizio.equals("")) {
					for (int i = sizePeriodi - 1; i >= 0 && !exitInit; i--) {
						SourceBean periodo = (SourceBean) periodi.get(i);
						if (!periodo.containsAttribute("FLGCANCELLATO")) {
							BigDecimal prgPeriodo = (BigDecimal) periodo.getAttribute("PRGPERIODOLAV");
							String dataInizioPer = StringUtils.getAttributeStrNotNull(periodo, "DATAINIZIO");
							String dataFinePer = StringUtils.getAttributeStrNotNull(periodo, "DATAFINE");
							Boolean resIntermInit = null;
							if (DateUtils.compare(dataInizioPer, dataCheckPeriodoInizio) >= 0) {
								exitInit = true;
							} else {
								if (DateUtils.compare(dataFinePer, dataCheckPeriodoInizio) < 0) {
									// cancellazione periodo
									resIntermInit = MovimentoBean.cancellaPeriodoLavorativo(prgPeriodo, trans);
									periodo.setAttribute("FLGCANCELLATO", "S");
									fattoRiallineamento = true;
								} else {
									BigDecimal numkloPeriodo = (BigDecimal) periodo.getAttribute("NUMKLOPERIODOLAV");
									numkloPeriodo = numkloPeriodo.add(new BigDecimal(1));
									resIntermInit = MovimentoBean.aggiornaPeriodoLavorativo(prgPeriodo, numkloPeriodo,
											dataCheckPeriodoInizio, dataFinePer, userId, trans);
									periodo.updAttribute("NUMKLOPERIODOLAV", numkloPeriodo);
									periodo.updAttribute("DATAINIZIO", dataCheckPeriodoInizio);
									fattoRiallineamento = true;
								}
								if (resIntermInit == null || !resIntermInit.booleanValue()) {
									throw new Exception(
											"Errore nella gestione dei periodi intermittenti in fase di riallineamento date.");
								}
							}
						}
					}
				}
				if (fattoRiallineamento) {
					if (operazione.equalsIgnoreCase("ANNULLA")) {
						warnings.add(
								new Warning(MessageCodes.ImportMov.WAR_ANN_RIALLINEAMENTO_PERIODI_INTERMITTENTI, ""));
					} else {
						warnings.add(
								new Warning(MessageCodes.ImportMov.WAR_RETT_RIALLINEAMENTO_PERIODI_INTERMITTENTI, ""));
					}
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static int calcolaGGPeriodo(String dataInizioPeriodo, String dataFinePeriodo) {
		int giorniIntermittentiCalc = 0;
		int giornoInizio = Integer.parseInt(dataInizioPeriodo.substring(0, 2));
		int meseInizio = Integer.parseInt(dataInizioPeriodo.substring(3, 5));
		int annoInizio = Integer.parseInt(dataInizioPeriodo.substring(6, 10));
		int giornoFine = Integer.parseInt(dataFinePeriodo.substring(0, 2));
		int meseFine = Integer.parseInt(dataFinePeriodo.substring(3, 5));
		int annoFine = Integer.parseInt(dataFinePeriodo.substring(6, 10));
		if (annoFine > annoInizio) {
			// anno fine maggiore anno inizio
			int mesiDiff = (annoFine - annoInizio - 1) * 12;
			giorniIntermittentiCalc = giorniIntermittentiCalc + (mesiDiff * 30);

			int mesiDiffInizio = 12 - meseInizio;
			if (mesiDiffInizio > 0) {
				giorniIntermittentiCalc = giorniIntermittentiCalc + (mesiDiffInizio * 30);
			}
			if (giornoInizio == DateUtils.daysInMonth(meseInizio, annoInizio)) {
				giorniIntermittentiCalc = giorniIntermittentiCalc + 1;
			} else {
				giorniIntermittentiCalc = giorniIntermittentiCalc + (30 - giornoInizio + 1);
			}

			int mesiDiffFine = meseFine - 1;
			if (mesiDiffFine > 0) {
				giorniIntermittentiCalc = giorniIntermittentiCalc + (mesiDiffFine * 30);
			}
			if (giornoFine >= 30) {
				giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
			} else {
				if ((meseFine == 2) && (giornoFine == DateUtils.daysInMonth(2, annoFine))) {
					giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
				} else {
					giorniIntermittentiCalc = giorniIntermittentiCalc + giornoFine;
				}
			}
		} else {
			// anno inizio uguale anno fine
			if (meseInizio == meseFine) {
				if ((meseInizio == 2) && (((giornoFine - giornoInizio) + 1) == DateUtils.daysInMonth(2, annoInizio))) {
					giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
				} else {
					if (((giornoFine - giornoInizio) + 1) > 30) {
						giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
					} else {
						giorniIntermittentiCalc = giorniIntermittentiCalc + ((giornoFine - giornoInizio) + 1);
					}
				}
			} else {
				int mesiDiff = ((meseFine - 1) - (meseInizio + 1) + 1);
				if (mesiDiff > 0) {
					giorniIntermittentiCalc = giorniIntermittentiCalc + (mesiDiff * 30);
				}
				if (giornoFine >= 30) {
					giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
				} else {
					if ((meseFine == 2) && (giornoFine == DateUtils.daysInMonth(2, annoFine))) {
						giorniIntermittentiCalc = giorniIntermittentiCalc + 30;
					} else {
						giorniIntermittentiCalc = giorniIntermittentiCalc + giornoFine;
					}
				}
				if (giornoInizio == DateUtils.daysInMonth(meseInizio, annoInizio)) {
					giorniIntermittentiCalc = giorniIntermittentiCalc + 1;
				} else {
					giorniIntermittentiCalc = giorniIntermittentiCalc + (30 - giornoInizio + 1);
				}
			}
		}
		return giorniIntermittentiCalc;
	}

}