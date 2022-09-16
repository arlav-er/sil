package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

public class AtecoUtils {
	/*
	 * Effettua il controllo di validità del codice ateco. Il codice è valido se : - il codice è presente sulla
	 * DE_ATTIVITA - è di livello 2 - è di lunghezza 6
	 */
	public static boolean checkValiditaAteco(String codAteco, boolean obbligatorio) {
		if (!obbligatorio && codAteco.length() == 0)
			return true;

		SourceBean chk = (SourceBean) QueryExecutor.executeQuery("CONTROLLA_VALIDITA_ATECO", new Object[] { codAteco },
				"SELECT", Values.DB_SIL_DATI);
		BigDecimal livello = null;
		if (chk != null) {
			livello = (BigDecimal) chk.getAttribute("ROW.CDNLIVELLO");
		}
		if ((codAteco.length() == 6) && (livello != null) && (livello.intValue() == 2)) {
			return true;
		}

		return false;
	}
}
