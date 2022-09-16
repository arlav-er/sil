package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class Proroga extends MovimentoBean {
	private MovimentoBean avviamento;

	public Proroga(SourceBean m) throws SourceBeanException {
		super(m);
	}

	public boolean modificato() {
		return (getAttribute(MovimentoBean.DB_FLG_MOD_REDDITO) != null)
				|| (getAttribute(MovimentoBean.DB_FLG_MOD_TEMPO) != null);
	}

	/**
	 * Questo metodo, chiamato su una proroga, restituisce il movimento di avviamento di partenza.
	 * 
	 * @return
	 * @throws Exception
	 */
	public MovimentoBean getAvviamento() throws Exception {
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
		MovimentoBean m = null;
		if (movimentoAvv != null)
			m = new MovimentoBean(movimentoAvv);
		else
			m = (MovimentoBean) this;
		return m;
	}

	public MovimentoBean getAvviamento(TransactionQueryExecutor transExec) throws Exception {
		Object prec = this.getAttribute("PRGMOVIMENTOPREC");
		SourceBean movimentoAvv = null;
		while (prec != null) {
			movimentoAvv = (SourceBean) DBLoad.getMovimento(prec, transExec);
			if (movimentoAvv.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).equals("AVV")) {
				prec = null;
			} else {
				prec = movimentoAvv.getAttribute("PRGMOVIMENTOPREC");
			}
		}
		MovimentoBean m = null;
		if (movimentoAvv != null)
			m = new MovimentoBean(movimentoAvv);
		else
			m = (MovimentoBean) this;
		return m;
	}

	public void setMovimentoBack(MovimentoBean m) {
		try {
			String dataFine = null;
			SourceBean sb = new SourceBean(getSource());
			String dataInizio = getDataInizio();
			sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
			if (sb.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).equals("D"))
				sb.updAttribute(MovimentoBean.DB_DATA_FINE, sb.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA));
			sb.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_ASSUNZIONE);
			avviamento = new MovimentoBean(sb);

			if (m.getTipoMovimento() == MovimentoBean.PROROGA)
				dataFine = DateUtils.giornoPrecedente(getDataInizio());
			if (m.getTipoMovimento() == MovimentoBean.PROROGA) {
				if (((Proroga) m).getAvviamento() != null) {
					((Proroga) m).getAvviamento().updAttribute(MovimentoBean.DB_DATA_FINE, dataFine);
					((Proroga) m).getAvviamento().updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFine);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public String toString() {
		String s = super.toString();
		// Commento in attesa di verifica
		// s+=" , movimento prorogato:: " + getMovimentoBack() + ", avviamento
		// corrispondente::" + avviamento;
		return s;
	}
}
