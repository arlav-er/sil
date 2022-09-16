package it.eng.sil.module.cig;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynComboEsito implements IDynamicStatementProvider2 {

	private String SELECT_ESITO = " select codEsito as codice, strDescrizione || DECODE(SYSDATE,GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL), '(scaduto)',"
			+ "		LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', '') as descrizione " + " from de_esito  ";

	public DynComboEsito() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		String scadenziarioCig = StringUtils.getAttributeStrNotNull(req, "SCADENZIARIO");

		if (scadenziarioCig.equals("CIG2")) {

		}
		StringBuffer query_totale = new StringBuffer(SELECT_ESITO);
		StringBuffer buf = new StringBuffer();

		if (scadenziarioCig.equals("CIG2")) {
			buf.append(" where codEsito in ('NP1','NP2','AGA','AGD','AGS') ");
		}

		if (scadenziarioCig.equals("CIG4")) {
			buf.append(" where codEsito in ('2X1','2X2','7Y1','7Y2','LZ1','LZ2') ");
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
