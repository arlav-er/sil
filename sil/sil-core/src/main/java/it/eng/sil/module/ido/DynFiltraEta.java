package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.tags.Util;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynFiltraEta implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynFiltraEta.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String query = "";

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		String prgRosa = (String) req.getAttribute("prgRosa");
		String dataNascMin = (String) req.getAttribute("dataNascMin");
		String dataNascMax = (String) req.getAttribute("dataNascMax");
		String strValidaCanc = (String) req.getAttribute("strValidaCanc");
		strValidaCanc = Util.replace(strValidaCanc, "'", "''");
		String where_in = "";

		// Creo la subquery che filtra per età a seconda che siano vlaorizzati o
		// meno i campi dataNascMin e dataNascMax
		// 13/07/2005 - Aggiunto il join con la rosa per limitare il
		// sottoinsieme nella clausola IN (Stefy)
		String subQuery = " ( select an_lavoratore.cdnlavoratore from an_lavoratore, do_nominativo ";
		subQuery += "where (an_lavoratore.CDNLAVORATORE=do_nominativo.CDNLAVORATORE) and prgrosa=" + prgRosa + " and ";
		if (dataNascMin != null && !dataNascMin.equals("")) {
			if (dataNascMax != null && !dataNascMax.equals("")) {
				subQuery += " an_lavoratore.DATNASC between to_date(\'" + dataNascMax + "\',\'dd/mm/yyyy\')"
						+ "                           and to_date(\'" + dataNascMin + "\',\'dd/mm/yyyy\')" + ")";
				where_in = " NOT IN ";
			} else {
				subQuery += " an_lavoratore.DATNASC >= to_date(\'" + dataNascMin + "\',\'dd/mm/yyyy\')" + ")";
				where_in = " IN ";
			}
		} else {
			if (dataNascMax != null && !dataNascMax.equals("")) {
				subQuery += " an_lavoratore.DATNASC <= to_date(\'" + dataNascMax + "\',\'dd/mm/yyyy\')" + ")";
				where_in = " IN ";
			}
		}

		// Costruisco la query inserendo la subquery
		// 13/07/2005 - Stefy LA CLAUSOLA "NOT IN" SI ACCORDA CON LA QUERY
		// COSTRUITA
		// SOLO QUANDO CI SONO SIA DATANASCMIN SIA DATANASCMAX
		// INOLTRE NON SI DISCRIMINAVA IL CASO IN CUI CODTIPOCANC FOSSE GIA'
		// VALORIZZATO ED E' UN ERRORE
		/*
		 * query = "update do_nominativo" + " set codTipoCanc = \'D\'," + " strMotivoCanc =
		 * \'" + strValidaCanc +"\'," + " cdnUtCanc = "+ user.getCodut() +"," + " dtmCanc = SYSDATE," + "
		 * NUMKLONOMINATIVO = NUMKLONOMINATIVO+1 " + " where do_nominativo.cdnlavoratore not in " + subQuery +
		 * " and do_nominativo.PRGROSA =" + prgRosa;
		 */
		query = "update do_nominativo " + "set codTipoCanc = \'D\', " + "strMotivoCanc = \'" + strValidaCanc + "\', "
				+ "cdnUtCanc = " + user.getCodut() + ", " + "dtmCanc = SYSDATE, "
				+ "NUMKLONOMINATIVO = NUMKLONOMINATIVO+1  " + "where do_nominativo.cdnlavoratore " + where_in + subQuery
				+ " and do_nominativo.PRGROSA =" + prgRosa + " and codTipoCanc is null ";

		_logger.debug(className + "::Filtra per eta: " + query);

		return query;

	}// getStatement

}// class DynFiltraEta
