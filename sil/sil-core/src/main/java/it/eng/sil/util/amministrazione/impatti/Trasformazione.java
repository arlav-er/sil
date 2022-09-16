package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;

public class Trasformazione extends MovimentoBean {
	private Cessazione cessazione;
	private MovimentoBean avviamento;

	public Trasformazione(SourceBean m) throws SourceBeanException {
		super(m);
	}

	public boolean modificato() {
		return (getAttribute(MovimentoBean.DB_FLG_MOD_REDDITO) != null)
				|| (getAttribute(MovimentoBean.DB_FLG_MOD_TEMPO) != null);
	}

	public Cessazione getCessazione() {
		return this.cessazione;
	}

	public MovimentoBean getAvviamento() {
		return this.avviamento;
	}

	public void setMovimentoBack(MovimentoBean m) {
		try {
			String dataFine = null;
			SourceBean sb = new SourceBean(getSource());
			String dataInizio = getDataInizio();
			sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
			if (sb.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).equals("D")) {
				sb.updAttribute(MovimentoBean.DB_DATA_FINE, sb.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA));
			}
			sb.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_ASSUNZIONE);
			avviamento = new MovimentoBean(sb);
			String dataCessazione = null;
			String dataFineMovPrec = m.getDataFineEffettiva();
			if (dataFineMovPrec != null && !dataFineMovPrec.equals("")) {
				if (DateUtils.compare(dataInizio, dataFineMovPrec) == 0) {
					dataCessazione = dataInizio;
				} else {
					dataCessazione = DateUtils.giornoPrecedente(dataInizio);
				}
			} else {
				dataCessazione = DateUtils.giornoPrecedente(dataInizio);
			}
			sb = new SourceBean(getSource());
			sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataCessazione);
			sb.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_CESSAZIONE);
			sb.updAttribute(MovimentoBean.FLAG_VIRTUALE, "0");
			cessazione = new Cessazione(sb);
			cessazione.setMovimentoBack(m);
			super.setMovimentoBack(m);
			if (m.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE) {
				if (dataFineMovPrec != null && !dataFineMovPrec.equals("")) {
					if (DateUtils.compare(dataInizio, dataFineMovPrec) == 0) {
						dataFine = dataInizio;
					} else {
						dataFine = DateUtils.giornoPrecedente(dataInizio);
					}
				} else {
					dataFine = DateUtils.giornoPrecedente(dataInizio);
				}
				Trasformazione mTra = new Trasformazione((SourceBean) m);
				if (mTra.getAvviamento() != null) {
					mTra.getAvviamento().updAttribute(MovimentoBean.DB_DATA_FINE, dataFine);
					mTra.getAvviamento().updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFine);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Questo metodo, chiamato su una trasformazione, restituisce il movimento di partenza della catena delle
	 * trasformazioni
	 * 
	 * @return
	 * @throws Exception
	 */
	public SourceBean getAvviamentoStart(SourceBean movPrec, Vector movimenti) throws Exception {

		SourceBean sbApp = movPrec;
		SourceBean sbReturn = movPrec;
		BigDecimal prgMovimentoPrec = (BigDecimal) movPrec.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		BigDecimal prgMovimentoApp = null;
		boolean trovato;
		while (prgMovimentoPrec != null) {
			trovato = false;
			for (int k = 0; k < movimenti.size(); k++) {
				sbApp = (SourceBean) movimenti.get(k);
				prgMovimentoApp = sbApp.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						? (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						: null;
				if (prgMovimentoApp != null && prgMovimentoApp.equals(prgMovimentoPrec)) {
					prgMovimentoPrec = (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
					trovato = true;
					sbReturn = sbApp;
					break;
				}
			}
			if (!trovato)
				prgMovimentoPrec = null;
		}
		return sbReturn;
	}

	/**
	 * Questo metodo, chiamato su una trasformazione, restituisce il movimento di avviamento di partenza (primo della
	 * catena delle trasformazioni).
	 * 
	 * @return
	 * @throws Exception
	 */
	public MovimentoBean getAvvDaTrasformazione() throws Exception {
		Object prec = this.getAttribute("PRGMOVIMENTOPREC");
		SourceBean movimentoAvv = null;
		while (prec != null) {
			movimentoAvv = (SourceBean) DBLoad.getMovimento(prec);
			if (movimentoAvv.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).equals("AVV")) {
				prec = null;
			} else {
				prec = movimentoAvv.getAttribute("PRGMOVIMENTOPREC");
			}
		}
		MovimentoBean m = new MovimentoBean(movimentoAvv);
		return m;
	}

	public String toString() {
		String s = super.toString();
		// Commento in attesa di verifica
		// s+=" , movimento trasformato::" + getMovimentoBack() + " , cessazione
		// virtuale::" + cessazione + " , avviamento corrispondente::" +
		// avviamento;
		return s;
	}
}
