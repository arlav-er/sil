package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;

/**
 * Utilizzo questa classe per impostare alcuni dati che andrebbero letti dal db. Questo per il test con JUnit
 */
public class DatiDiTest {
	public static final boolean TEST = false;
	public static SourceBean ultimaDIDStoricizzata;
	public static SourceBean did;
	public static SourceBean cm;
	public static SourceBean deStatoOccupaz;
	public static Vector movimenti;
	public static SourceBean statoOccupazionale;
	public static SourceBean movimento;
	public static SourceBean cat181;
	public static SourceBean statiOccupazionali;
	public static SourceBean dichiarazioniDisponibilita;
	public static SourceBean pattiStoricizzati;
	public static SourceBean patto;
	public static SourceBean statoOccupazionaleUltimo;
	public static int prg = 111111111;
	public static int numKlo = 2222222;

	/**
	 * 
	 */
	public static void setMovimento(Vector m) {
		DatiDiTest.movimenti = m;
	}

	public static SourceBean getStatoOccupazionale(String prgStatoOccupazionale, String cdnLavoratore,
			String dataInizio, String dataInizioAnzianita, String dataFine, String stato, String flgPensionato)
			throws Exception {
		SourceBean row = new SourceBean("TEST");
		SourceBean sb = getStatoOccupazionaleNoRow(prgStatoOccupazionale, cdnLavoratore, dataInizio,
				dataInizioAnzianita, dataFine, stato, flgPensionato);
		row.setAttribute(sb);
		return row;
	}

	public static SourceBean getStatoOccupazionaleNoRow(String prgStatoOccupazionale, String cdnLavoratore,
			String dataInizio, String dataInizioAnzianita, String dataFine, String stato, String flgPensionato)
			throws Exception {
		SourceBean sb = new SourceBean("ROW");
		sb.setAttribute("CDNLAVORATORE", new BigDecimal(cdnLavoratore));
		sb.setAttribute("prgstatooccupaz", new BigDecimal(prgStatoOccupazionale));
		sb.setAttribute("datInizio", dataInizio);
		if (dataInizioAnzianita != null)
			sb.setAttribute("datanzianitadisoc", dataInizioAnzianita);
		if (dataFine != null)
			sb.setAttribute("datFine", dataFine);
		sb.setAttribute("codstatooccupaz", stato);
		int ragg = StatoOccupazionaleBean.deStatoOccRagg[StatoOccupazionaleBean.encode(stato)];
		sb.setAttribute("codstatooccupazRagg", StatoOccupazionaleBean.mapStatoOccRagg[ragg]);
		if (flgPensionato != null)
			sb.setAttribute("flgPensionato", flgPensionato);
		sb.setAttribute("numKloStatoOccupaz", new BigDecimal(DatiDiTest.numKlo++));
		return sb;
	}

	public static SourceBean getStatiOccupazionali(SourceBean v[]) throws Exception {
		SourceBean rows = new SourceBean("ROWS");
		for (int i = 0; i < v.length; i++)
			rows.setAttribute(v[i]);
		return rows;
	}

	public static SourceBean getPattiStoricizzati(SourceBean[] v) throws Exception {
		return getStatiOccupazionali(v);
	}

	public static SourceBean makeCat181(String dataNascita, String flgObbScolastico, String flgLaurea)
			throws Exception {
		SourceBean row = new SourceBean("TEST");
		row.setAttribute("DATNASC", dataNascita);
		if (flgObbScolastico != null)
			row.setAttribute("FLGOBBLIGOSCOLASTICO", flgObbScolastico);
		// avro' cumunque il flag della laurea (ho messo nella query
		// nvl(flgLaurea, 'N'))
		if (flgLaurea != null)
			row.setAttribute("FLGLAUREA", flgLaurea);
		return row;
	}

	/**
	 * 
	 */
	public static SourceBean getMovimento(String cdnLavoratore, String prgMovimento, String datInizioMov,
			String datFineMov, String datFineMovEffettiva, String codTipoMov, String codMonoTempo,
			String decRetribuzioneMen, String codContratto, String codMvCessazione, String codTipoAss,
			String prgStatoOccupaz) throws Exception {
		SourceBean mov = new SourceBean("ROW");

		if (cdnLavoratore != null) {
			mov.setAttribute(MovimentoBean.DB_CDNLAVORATORE, cdnLavoratore);
		}

		if (prgMovimento != null) {
			mov.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(prgMovimento));
		}

		if (datInizioMov != null) {
			mov.setAttribute(MovimentoBean.DB_DATA_INIZIO, datInizioMov);
		}
		if (datInizioMov != null) {
			mov.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, datInizioMov);
		}
		if (datFineMov != null) {
			mov.setAttribute(MovimentoBean.DB_DATA_FINE, datFineMov);
		}
		if (datFineMovEffettiva != null) {
			mov.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, datFineMovEffettiva);
		}
		if (codTipoMov != null) {
			mov.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, codTipoMov);
		}

		if (decRetribuzioneMen != null) {
			mov.setAttribute(MovimentoBean.DB_RETRIBUZIONE, new BigDecimal(decRetribuzioneMen));
		}

		if (codContratto != null) {
			mov.setAttribute(MovimentoBean.DB_COD_CONTRATTO, codContratto);
		}

		if (codMonoTempo != null) {
			mov.setAttribute(MovimentoBean.DB_COD_MONO_TEMPO, codMonoTempo);
		}

		if (codMvCessazione != null) {
			mov.setAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE, codMvCessazione);
		}
		if (codTipoAss != null) {
			mov.setAttribute(MovimentoBean.DB_COD_ASSUNZIONE, codTipoAss);
		}
		if (prgStatoOccupaz != null)
			mov.setAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, new BigDecimal(prgStatoOccupaz));
		mov.setAttribute(MovimentoBean.DB_NUM_K_LOCK, new BigDecimal(DatiDiTest.numKlo++));
		return mov;
	}

	public static SourceBean getMovimento(String cdnLavoratore, String prgMovimento, String datInizioMov,
			String DATFINEMOV, String CODTIPOMOV, String DECRETRIBUZIONEMEN, String CODCONTRATTO,
			String CODMVCESSAZIONE) throws Exception {
		return getMovimento(cdnLavoratore, prgMovimento, datInizioMov, DATFINEMOV, DATFINEMOV, CODTIPOMOV, null,
				DECRETRIBUZIONEMEN, CODCONTRATTO, CODMVCESSAZIONE, "NOK", null);
	}

	public static SourceBean getMovimento(String datInizioMov, String datFineMov, String CODTIPOMOV,
			String CODCONTRATTO, String codMonoTempo, String DECRETRIBUZIONEMEN) throws Exception {
		return getMovimento(null, null, datInizioMov, datFineMov, datFineMov, CODTIPOMOV, codMonoTempo,
				DECRETRIBUZIONEMEN, CODCONTRATTO, null, null, null);
	}

	public static SourceBean getMovimento(String datInizioMov, String datFineMov, String CODTIPOMOV,
			String CODCONTRATTO, String DECRETRIBUZIONEMEN) throws Exception {
		return getMovimento(null, null, datInizioMov, datFineMov, datFineMov, CODTIPOMOV, "D", DECRETRIBUZIONEMEN,
				CODCONTRATTO, null, "NOK", null);
	}

	public static SourceBean getMovimento(String datInizioMov, String CODTIPOMOV, String CODCONTRATTO,
			String DECRETRIBUZIONEMEN) throws Exception {
		return getMovimento(null, null, datInizioMov, null, null, CODTIPOMOV, "I", DECRETRIBUZIONEMEN, CODCONTRATTO,
				null, "NOK", null);
	}

	public static SourceBean getCessazione(SourceBean cessato) throws Exception {
		String dataInizioCes = (String) cessato.getAttribute(MovimentoBean.DB_DATA_FINE);
		SourceBean row = getCessazioneAnticipata(cessato, dataInizioCes);
		return row;
	}

	public static SourceBean getCessazioneAnticipata(SourceBean cessato, String dataFine) throws Exception {

		String dataInizioAvv = (String) cessato.getAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC);
		SourceBean row = getMovimento(dataInizioAvv, MovimentoBean.COD_CESSAZIONE, null, null);
		row.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataFine);
		row.updAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC,
				cessato.getAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC));
		row.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_CESSAZIONE);
		cessato.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, row.getAttribute(MovimentoBean.DB_DATA_INIZIO));
		cessato.updAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE, "C");
		row.setAttribute(MovimentoBean.DB_CDNLAVORATORE, cessato.getAttribute(MovimentoBean.DB_CDNLAVORATORE));
		return row;
	}

	public static SourceBean getPatto(String dataInizio) throws Exception {
		SourceBean row = new SourceBean("PATTO");
		SourceBean sb = new SourceBean("ROW");
		sb.setAttribute("datStipula", dataInizio);
		sb.setAttribute("prgPattoLavoratore", new BigDecimal("1"));
		row.setAttribute(sb);
		return row;
	}

	public static SourceBean getProroga(SourceBean mov, String dataFine) throws Exception {
		String dataInizioAvv = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC);
		String dataFineMov = (String) mov.getAttribute(MovimentoBean.DB_DATA_FINE);
		String codContratto = (String) mov.getAttribute(MovimentoBean.DB_COD_CONTRATTO);
		String dataInizio = DateUtils.giornoSuccessivo(dataFineMov);
		SourceBean row = getMovimento(dataInizio, dataFine, MovimentoBean.COD_PROROGA, codContratto, "600");
		row.updAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, dataInizioAvv);
		mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFine);
		mov.updAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE, "P");
		return row;
	}

	public static SourceBean getTrasformazione(SourceBean mov, String dataFine) throws Exception {
		SourceBean row = getProroga(mov, dataFine);
		mov.updAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE, "T");
		row.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_TRASFORMAZIONE);
		return row;
	}

	public static SourceBean getDID(String dataDichiarazione) throws Exception {
		return getDID(dataDichiarazione, null);
	}

	public static SourceBean getDIDs(SourceBean[] v) throws Exception {
		return getStatiOccupazionali(v);
	}

	public static SourceBean getDID(String dataDichiarazione, String codMotivoFineAtto) throws Exception {
		SourceBean did = new SourceBean("TEST");
		SourceBean row = new SourceBean("ROW");
		row.setAttribute("datdichiarazione", dataDichiarazione);
		row.setAttribute("prgDichDisponibilita", "1");
		if (codMotivoFineAtto != null)
			row.setAttribute("codMotivoFineAtto", codMotivoFineAtto);
		did.setAttribute(row);
		return did;
	}

	public static SourceBean getDID(String dataDichiarazione, String codMotivoFineAtto, int prg) throws Exception {
		SourceBean did = new SourceBean("TEST");
		SourceBean row = new SourceBean("ROW");
		row.setAttribute("datdichiarazione", dataDichiarazione);
		row.setAttribute("prgDichDisponibilita", new BigDecimal(prg));
		if (codMotivoFineAtto != null)
			row.setAttribute("codMotivoFineAtto", codMotivoFineAtto);
		did.setAttribute(row);
		return did;
	}

	public static SourceBean getCMsenzaRow(String codCM) throws Exception {
		SourceBean row = new SourceBean("ROW");
		row.setAttribute("CODCMTIPOISCR", codCM);

		return row;
	}

	public static SourceBean getCM(String codCM) throws Exception {
		SourceBean row = new SourceBean("TEST");
		row.setAttribute(getCMsenzaRow(codCM));
		return row;
	}

	public static SourceBean getMesiBlocco(String n) throws Exception {
		SourceBean row = new SourceBean("TEST");
		row.setAttribute("numMesiBlocco", new BigDecimal(n));
		return row;
	}

	public static void reset() {
		DatiDiTest.cat181 = null;
		DatiDiTest.deStatoOccupaz = null;
		DatiDiTest.movimenti = null;
		DatiDiTest.ultimaDIDStoricizzata = null;
		DatiDiTest.cm = null;
		DatiDiTest.dichiarazioniDisponibilita = null;
		DatiDiTest.did = null;
		DatiDiTest.movimento = null;
		DatiDiTest.pattiStoricizzati = null;
		DatiDiTest.patto = null;
		DatiDiTest.statiOccupazionali = null;
		DatiDiTest.statoOccupazionale = null;
		DatiDiTest.statoOccupazionaleUltimo = null;
	}
}
