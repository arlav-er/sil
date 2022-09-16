package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class LimiteRedditoExt {
	//
	public static final boolean TEST = false;
	private double quotaBaseFissa;
	private double limiteCM;
	private double limiteLD;
	private double limiteLA;
	private String annoRif = null;
	private int anno;
	private double limite;
	private double limiteAnnoSuccessivo;
	private double limiteAnnoSuccessivoCM;
	private double limiteAnnoSuccessivoLD;
	private double limiteAnnoSuccessivoLA;

	/**
	 * 
	 */
	public LimiteRedditoExt(String annoRif) throws Exception {
		// data di riferimento
		this.annoRif = annoRif;

		SourceBean row = null;

		if (!TEST) {
			row = DBLoad.getLimiteReddito(annoRif);
		} else {
			row = new SourceBean("TEST");
			row.setAttribute("NUMLIMITEREDDITOCM", new BigDecimal("10000"));
			row.setAttribute("NUMLIMITEREDDITOLA", new BigDecimal("3500"));
			row.setAttribute("NUMLIMITEREDDITOLD", new BigDecimal("7500"));
			row.setAttribute("NUMQUOTABASEFISSA", new BigDecimal("2000"));
			row.setAttribute("NUMANNO", new BigDecimal("2004"));
		}

		limiteCM = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOCM")).doubleValue();
		limiteLA = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOLA")).doubleValue();
		limiteLD = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOLD")).doubleValue();
		quotaBaseFissa = ((BigDecimal) row.getAttribute("NUMQUOTABASEFISSA")).doubleValue();
		limite = limiteLD;

		// Setto il limite per l'anno successivo uguale al limite anno corrente
		// (non viene più controllato, ma lo setto ugualmente per evitare degli errori nel recupero dei valori)
		limiteAnnoSuccessivoCM = limiteCM;
		limiteAnnoSuccessivoLA = limiteLA;
		limiteAnnoSuccessivoLD = limiteLD;
		limiteAnnoSuccessivo = limiteAnnoSuccessivoLD;

		// conviene valorizzare l' anno per ultimo
		anno = ((BigDecimal) row.getAttribute("NUMANNO")).intValue();
	}

	/**
	 * Calcola il limite a seconda del tipo contratto (alla fine fa il riproporzionamento del limite).
	 * 
	 * @param tipo
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public double calcola(int tipo, String dataInizio) throws Exception {
		double limite = 0;
		double ret = 0;

		switch (tipo) {
		case LimiteReddito.AUTONOMO:
			limite = limiteLA;

			break;

		case LimiteReddito.DIPENDENTE:
			limite = limiteLD;

			break;

		case LimiteReddito.CM:
			limite = limiteCM;

			break;
		}

		double quotaVar = limite - quotaBaseFissa;
		if (quotaVar < 0) {
			quotaVar = 0;
		}
		ret = quotaBaseFissa + ((quotaVar) * getPercentualeGG(dataInizio));

		return ret;
	}

	public double get(int tipo) throws Exception {
		double limite = 0;

		switch (tipo) {
		case LimiteReddito.AUTONOMO:
			limite = limiteLA;

			break;

		case LimiteReddito.DIPENDENTE:
			limite = limiteLD;

			break;

		case LimiteReddito.CM:
			limite = limiteCM;

			break;
		}

		return limite;
	}

	/**
	 * Calcolo il numero di giorni di lavoro in base al calendario
	 * 
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public double getPercentualeGG(String dataInizio) throws Exception {
		int gg = DateUtils.daysBetween(dataInizio, "31/12/" + anno) + 1;
		gg = (gg >= 365) ? 365 : gg;
		return ((double) (gg)) / 365;
	}

	/**
	 * 
	 * @param newLimite
	 */
	public void setLimiteReddito(double newLimite) {
		this.limite = newLimite;
	}

	/**
	 * Aggiorna il limite a seconda del tipo contratto del movimento
	 * 
	 * @param mov
	 * @param cm
	 * @param did
	 * @throws Exception
	 */
	public void aggiorna(SourceBean mov, SourceBean cm, SourceBean did_O_Mobilita) throws Exception {
		String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataDichiarazione = (String) did_O_Mobilita.getAttribute("datDichiarazione");
		// dataDichiarazione risulta null quando mi trovo a gestire una mobilità
		// con tipo "S";
		// in questo caso dataDichiarazione deve essere la data inizio della
		// mobilità
		if (dataDichiarazione == null || dataDichiarazione.equals("")) {
			dataDichiarazione = (String) did_O_Mobilita.getAttribute("datInizio");
		}

		String cdnlavoratore = did_O_Mobilita.getAttribute("cdnlavoratore").toString();
		String dataRif = null;
		if (DateUtils.getAnno(dataInizio) == DateUtils.getAnno(dataDichiarazione)) {
			dataRif = dataDichiarazione;
		} else {
			dataRif = dataInizio;
		}

		switch (Contratto.getTipoContratto(mov)) {
		case Contratto.AUTONOMO:

			// come minimo ricalcola il limite
		case Contratto.COCOCO:
			boolean esisteMovimentoDip = false;
			if (Controlli.inCollocamentoMirato(cm, dataRif)) {
				setLimiteReddito(get(LimiteReddito.CM));
			} else {
				try {
					Vector movimenti = DBLoad.getMovimentiApertiAnno(dataInizio, cdnlavoratore);
					movimenti = MovimentoBean.gestisciTuttiPeriodiIntermittentiApertiAnno(movimenti, dataInizio, null);
					if (movimenti.size() > 0) {
						esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataInizio, movimenti);
					}
				} catch (Exception e) {
					esisteMovimentoDip = false;
				}

				if (esisteMovimentoDip) {
					if (Controlli.annoDIDeqAnnoMov(dataInizio, did_O_Mobilita)) {
						setLimiteReddito(calcola(LimiteReddito.DIPENDENTE, dataDichiarazione));
					} else {
						setLimiteReddito(get(LimiteReddito.DIPENDENTE));
					}
				} else {
					setLimiteReddito(get(LimiteReddito.AUTONOMO));
				}
			}

			break;

		case Contratto.DIP_TD:

			if (Controlli.inCollocamentoMirato(cm, dataInizio)) {
				setLimiteReddito(get(LimiteReddito.CM));
			} else {
				if (Controlli.annoDIDeqAnnoMov(dataInizio, did_O_Mobilita)) {
					setLimiteReddito(calcola(LimiteReddito.DIPENDENTE, dataDichiarazione));
				} else {
					setLimiteReddito(get(LimiteReddito.DIPENDENTE));
				}
			}

			break;

		case Contratto.DIP_TI:

			if (Controlli.inCollocamentoMirato(cm, dataInizio)) {
				if (Controlli.annoDIDeqAnnoMov(dataInizio, did_O_Mobilita)) {
					setLimiteReddito(calcola(LimiteReddito.CM, dataDichiarazione));
				} else {
					setLimiteReddito(get(LimiteReddito.CM));
				}
			} else {
				if (Controlli.annoDIDeqAnnoMov(dataInizio, did_O_Mobilita)) {
					setLimiteReddito(calcola(LimiteReddito.DIPENDENTE, dataDichiarazione));
				} else {
					setLimiteReddito(get(LimiteReddito.DIPENDENTE));
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getLimite() {
		return this.limite;
	}

	/**
	 * 
	 * @param mov
	 * @param cdnlavoratore
	 * @param cm
	 * @return
	 */
	public double getLimiteAnnoSuccessivo(SourceBean mov, String cdnlavoratore, SourceBean cm,
			TransactionQueryExecutor transExec) throws Exception {
		int tipo = 0;
		boolean errors = false;
		String dataRif = "";
		int tipoContratto = -1;
		if (mov == null) {
			tipoContratto = Contratto.DIP_TD; // 13/10/2004 valore di default se si registra una DID
		} else {
			tipoContratto = Contratto.getTipoContratto(mov);
			dataRif = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		}

		if (cdnlavoratore.equals("")) {
			if (mov != null) {
				cdnlavoratore = mov.containsAttribute(MovimentoBean.DB_CDNLAVORATORE)
						? mov.getAttribute(MovimentoBean.DB_CDNLAVORATORE).toString()
						: "";
			}
		}

		if (mov == null && !cdnlavoratore.equals("") && cm == null) {
			try {
				cm = DBLoad.getCollocamentoMirato(cdnlavoratore, transExec);
			} catch (Exception ex) {
				errors = true;
			}
		} else {
			if (cm == null) {
				try {
					cm = DBLoad.getCollocamentoMirato(mov, transExec);
				} catch (Exception ex) {
					errors = true;
				}
			}
		}

		if (dataRif == null || dataRif.equals("")) {
			if (cm != null && !cm.containsAttribute("DATINIZIO")) {
				dataRif = cm.getAttribute("DATINIZIO").toString();
			}
		}

		if (Controlli.inCollocamentoMirato(cm, dataRif) && !errors)
			tipo = LimiteReddito.CM;
		else if (tipoContratto == Contratto.AUTONOMO || tipoContratto == Contratto.COCOCO)
			tipo = LimiteReddito.AUTONOMO;
		else if (tipoContratto == Contratto.DIP_TD || tipoContratto == Contratto.DIP_TI)
			tipo = LimiteReddito.DIPENDENTE;
		switch (tipo) {
		case LimiteReddito.AUTONOMO:
			boolean esisteMovimentoDip = false;
			if (mov != null && mov.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
				try {
					int annoMov = DateUtils.getAnno(mov.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString());
					int annoSuccMov = annoMov + 1;
					String dataInizio = "01/01/" + annoSuccMov;
					Vector movimenti = DBLoad.getMovimentiApertiAnno(dataInizio, cdnlavoratore);
					movimenti = MovimentoBean.gestisciTuttiPeriodiIntermittentiApertiAnno(movimenti, dataInizio, null);
					if (movimenti.size() > 0) {
						esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataInizio, movimenti);
					}
				} catch (Exception e) {
					esisteMovimentoDip = false;
				}
			}
			if (esisteMovimentoDip) {
				setLimiteAnnoSuccessivo(limiteAnnoSuccessivoLD);
			} else {
				setLimiteAnnoSuccessivo(limiteAnnoSuccessivoLA);
			}

			break;

		case LimiteReddito.DIPENDENTE:
			setLimiteAnnoSuccessivo(limiteAnnoSuccessivoLD);
			break;

		case LimiteReddito.CM:
			setLimiteAnnoSuccessivo(limiteAnnoSuccessivoCM);
			break;
		}

		return limiteAnnoSuccessivo;
	}

	/**
	 * 
	 * @param d
	 */
	public void setLimiteAnnoSuccessivo(double d) {
		limiteAnnoSuccessivo = d;
	}
}
