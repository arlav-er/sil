package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class Reddito {
	private double reddito;
	private double redditoAnnoSuccessivo;
	private LimiteRedditoExt limiteReddito;
	private SourceBean did;

	public Reddito(LimiteRedditoExt limite, SourceBean did) {
		this.limiteReddito = limite;
		this.did = did;
	}

	public Reddito(double redditoAnno, double redditoAnnoSucc) {
		reddito = redditoAnno;
		redditoAnnoSuccessivo = redditoAnnoSucc;
	}

	/**
	 * aggiorna il reddito per i movimenti che sono impattanti nella stipula della did
	 * 
	 * @param rows
	 * @param cm
	 * @param dataRif
	 * @param listaMobilita
	 * @throws Exception
	 */
	public void aggiorna(Vector rows, SourceBean cm, String dataRif, List listaMobilita,
			Vector configurazioniDefaul_Custom, TransactionQueryExecutor transExec) throws Exception {
		/*
		 * 27/10/2004 Al metodo aggiungiProroghe viene anche passato come ultimo parametro la data fino a cui arrivare a
		 * considerare i movimenti a ritroso. Nel caso della DID viene passato null, in quanto la logica è già
		 * implementata. Il problema è per l'inserimento di proroghe
		 */
		String codTipoAvviamento = "";
		boolean ret = false;
		rows = Controlli.aggiungiProroghe(rows, null, dataRif, transExec);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean mov = (SourceBean) rows.get(i);
			ret = mov.containsAttribute("FLAG_IN_INSERIMENTO");
			if (!ret) {
				if (!(mov.getAttribute("codStatoAtto") != null && mov.getAttribute("codStatoAtto").equals("PR"))
						|| MovimentoBean.getTipoMovimento(mov) == MovimentoBean.CESSAZIONE) {
					continue;
				}
			}
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02 (vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per
			// contrazione)
			codTipoAvviamento = mov.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? mov.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (MovimentoBean.getTipoMovimento(mov) != MovimentoBean.CESSAZIONE
					&& codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}
			if (!mov.containsAttribute("FLAG_NON_IMPATTANTE")
					&& MovimentoBean.getTipoMovimento(mov) != MovimentoBean.CESSAZIONE
					&& ((!mov.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS) || (mov
							.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
							&& !mov.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equals("T"))))) {
				aggiorna(mov, cm, dataRif, listaMobilita);
			}
		}
	}

	/**
	 * aggiorna il reddito di movimenti a cavallo della did
	 * 
	 * @param mov
	 * @param cm
	 * @param dataRif
	 * @param listaMobilita
	 * @throws Exception
	 */
	public void aggiorna(SourceBean mov, SourceBean cm, String dataRif, List listaMobilita) throws Exception {
		String dataInizio = null;
		dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = (String) mov.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		Object o = mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE);
		BigDecimal retribuzione = null;
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		int annoInizioMov = 0;
		int annoRif = DateUtils.getAnno(dataRif);

		if (mov.getAttribute("MOVIMENTI_PROROGATI") != null) {
			Vector prec = (Vector) mov.getAttribute("MOVIMENTI_PROROGATI");
			SourceBean movimentoAvv = null;
			SourceBean movimentoSucc = null;
			String dataInizioPrec = "";
			String dataInizioSucc = "";
			String dataFinePrec = "";
			BigDecimal retribuzionePrec = null;
			String dataInizioPro = "";
			Object dataFinePro = "";
			BigDecimal retribuzionePro = null;
			int ggDiLavoroPro = 0;
			int precSize = prec.size();

			for (int k = 0; k < precSize; k++) {
				movimentoAvv = (SourceBean) prec.get(k);
				int kSucc = k + 1;
				dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
				if (movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
					dataFinePrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString();
				} else {
					dataFinePrec = null;
				}

				// Se il movimento successivo nel vettore dei movimenti prorogati ha la stessa data inizio
				// del movimento corrente nel vettore dei prorogati, allora al fine del reddito non lo considero
				if (kSucc < precSize) {
					movimentoSucc = (SourceBean) prec.get(kSucc);
					dataInizioSucc = movimentoSucc.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
					if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
						continue;
					}
				}

				// prendo la retribuzione
				retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
				annoInizioMov = DateUtils.getAnno(dataInizioPrec);
				if (annoInizioMov <= annoRif && (dataFinePrec == null || dataFinePrec.equals("")
						|| DateUtils.getAnno(dataFinePrec) >= annoRif)) {
					if (retribuzionePrec != null) {
						ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec, dataFinePrec, dataRif);
						ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec,
								dataRif);
						aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzionePrec.doubleValue());
						limiteReddito.aggiorna(mov, cm, this.did);
					} else {
						reddito = Double.MAX_VALUE;
						redditoAnnoSuccessivo = Double.MAX_VALUE;
						return;
					}
				}
			} // end for (int k = 0; k < precSize; k++)

		} else {
			retribuzione = Retribuzione.getRetribuzioneMen(mov);
			if (retribuzione == null) {
				reddito = Double.MAX_VALUE;
				redditoAnnoSuccessivo = Double.MAX_VALUE;
				return;
			}
			ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataRif);
			ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataRif);
			aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
			limiteReddito.aggiorna(mov, cm, this.did);
		}
	}

	public void aggiorna(int ggLavorati, int ggLavoratiAnnoSuccessivo, double retribuzione) {
		reddito += ((retribuzione * ggLavorati) / 30);
		redditoAnnoSuccessivo += ((retribuzione * ggLavoratiAnnoSuccessivo) / 30);
	}

	public double getRedditoAnnoSuccessivo() {
		return this.redditoAnnoSuccessivo;
	}

	public double getRedditoMax() {

		if (!ControlloReddito.REDDITO_SU_DUE_ANNI || this.reddito > this.redditoAnnoSuccessivo)
			return this.reddito;
		else
			return this.redditoAnnoSuccessivo;
	}

	public LimiteRedditoExt getLimiteReddito() {
		return this.limiteReddito;
	}

	public boolean minoreDelLimite(SourceBean movimento, String cdnlavoratore, SourceBean cm,
			TransactionQueryExecutor transExec) {
		boolean minoreDelLimite = false;
		boolean minoreAnnoDid = false;
		// boolean minoreAnnoSuccessivo = false;
		minoreAnnoDid = this.reddito <= limiteReddito.getLimite();
		// minoreAnnoSuccessivo = this.redditoAnnoSuccessivo < limiteReddito.getLimiteAnnoSuccessivo(movimento,
		// cdnlavoratore, cm, transExec);
		// minoreDelLimite = (minoreAnnoDid && minoreAnnoSuccessivo);
		minoreDelLimite = minoreAnnoDid;
		return minoreDelLimite;
	}

	public double getRedditoAnno() {
		return reddito;
	}
}