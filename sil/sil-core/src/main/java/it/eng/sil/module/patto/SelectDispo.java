package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class SelectDispo implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "SELECT prgDichDisponibilita,  " + "    am.codCPI,  "
			+ " to_char(am.datDichiarazione, 'dd/mm/yyyy') as datDich, "
			+ "                    am.prgElencoAnagrafico, " + "                 am.codTipoDichDisp, "
			+ "              am.codTipoAttivitaLavPrec, " + "           am.strNote, " + "        am.cdnUtIns, "
			+ "     to_char(am.dtmIns, 'dd/mm/yyyy') as datIns," + "  am.cdnUtMod, "
			+ "                    to_char(am.dtmMod, 'dd/mm/yyyy') as datMod," + "                 am.numKloDichDisp  "
			+ "              FROM AM_DICH_DISPONIBILITA am ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		// String nome =(String) req.getAttribute("NOME");
		// String cognome =(String) req.getAttribute("COGNOME");
		// String cf =(String) req.getAttribute("CF");
		// String attPrec =(String) req.getAttribute("ATT_PREC");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		// if ( (nome!=null) && (!nome.equals("")) )
		// {
		// nome = StringUtils.replace(nome, "'", "''");
		// buf.append(" AND upper(an.strnome) = upper('" + nome + "')");
		// }

		// if ( (cognome!=null) && (!cognome.equals("")) )
		// {
		// cognome = StringUtils.replace(cognome, "'", "''");
		// buf.append(" AND upper(an.strcognome) = upper('" + cognome + "')");
		// }

		// if ( (cf!=null) && (!cf.equals("")) )
		// {
		// buf.append(" AND upper(an.strCodiceFiscale) = upper('" + cf + "')");
		// }
		//
		// if ( (attPrec!=null) && (!attPrec.equals("")) )
		// {
		// buf.append(" AND upper(tipo_att_lav.strDescrizione) = upper('" +
		// attPrec + "')");
		// }
		// buf.append(" )" );

		return buf.toString();

	}
}
