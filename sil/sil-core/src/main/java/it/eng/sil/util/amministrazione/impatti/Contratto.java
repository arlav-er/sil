package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;

public class Contratto {
	public static final int AUTONOMO = 1;
	public static final int COCOCO = 2;
	public static final int DIP_TD = 3;
	public static final int DIP_TI = 4;

	public static final String COD_DIP_TD = "LT";
	public static final String COD_DIP_TI = "LP";
	public static final String COD_COCOCO = "CO";
	public static final String COD_AUTONOMO = "PI";

	public static final String FIELD_FLG_SOSPENSIONE = "FLGCONTRATTOSOSPENSIONE";

	/**
	 * Estrae il tipo contratto associato al movimento.
	 * 
	 * @param movimento
	 * @return
	 */
	public static int getTipoContratto(SourceBean movimento) {
		String codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
				? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
				: "";
		// modifica gestione contratto autonomo (de_mv_tipo_ass.CodMonoTipo:
		// il valore corrispondente per i lavori autonomi è N
		if (codMonoTipoAss.toUpperCase().equals("N")) {
			return Contratto.AUTONOMO;
		}

		String codTipoAss = (String) movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE);
		// modifica 16/11/2004 codContratto viene preso dinamicamente
		// dal movimento
		String codContratto = null;
		codContratto = movimento.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
				? movimento.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
				: "";
		if (codContratto.equals("")) {
			return -1;
		}

		String flgDurata = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		if (codContratto.equalsIgnoreCase(Contratto.COD_DIP_TD)) {
			if (flgDurata != null && flgDurata.equalsIgnoreCase("D"))
				return Contratto.DIP_TD;
			else
				return Contratto.DIP_TI;
		}

		if (codContratto.equalsIgnoreCase(Contratto.COD_DIP_TI)) {
			if (flgDurata != null && flgDurata.equalsIgnoreCase("D"))
				return Contratto.DIP_TD;
			else
				return Contratto.DIP_TI;
		}
		if (flgDurata != null && flgDurata.equalsIgnoreCase("D"))
			return Contratto.DIP_TD;
		else
			return Contratto.DIP_TI;
	}
}