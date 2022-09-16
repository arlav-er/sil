package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/**
 * Effettua la ricerca dinamica sui contatti
 * 
 * @author Giovanni Landi
 * 
 */
public class DynStatementRicercaContatti implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select PRGCONTATTO, CODCPICONTATTO, "
			+ "to_char(DATCONTATTO,'dd/mm/yyyy') AS DATCONTATTO, " + "STRORACONTATTO, NUMKLOCONTATTO, "
			+ "PRGSPICONTATTO, " + "substr(TXTCONTATTO,1,30) as TXTCONTATTO, " + "FLGRICONTATTARE, STRIO, "
			+ "to_char(DATENTROIL,'dd/mm/yyyy') as DATENTROIL, "
			+ "PRGMOTCONTATTO, PRGTIPOCONTATTO, PRGEFFETTOCONTATTO, " + "ag_contatto.CDNUTINS, "
			+ "to_char(ag_contatto.DTMINS,'dd/mm/yyyy') as DTMINS, " + "ag_contatto.CDNUTMOD, "
			+ "to_char(ag_contatto.DTMMOD,'dd/mm/yyyy') as DTMMOD, "
			+ "concat(concat(an_spi.strcognome,' '),an_spi.strnome) as STRNOMEOPERATORE, "
			+ "strdescrizione as disprosa, " + "NVL(an_spi.prgspi,0) as PRGSPI " + "from ag_contatto "
			+ "left outer join an_spi on (ag_contatto.prgspicontatto = an_spi.prgspi) "
			+ "left outer join de_disponibilita_rosa on (de_disponibilita_rosa.CODDISPONIBILITAROSA = ag_contatto.CODDISPONIBILITAROSA) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();

		String prgAzienda = req.containsAttribute("PRGAZIENDA") ? req.getAttribute("PRGAZIENDA").toString() : "";
		String prgUnita = req.containsAttribute("PRGUNITA") ? req.getAttribute("PRGUNITA").toString() : "";
		String cdnLavoratore = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString()
				: "";
		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (cdnLavoratore.compareTo("") != 0) {
			buf.append(" WHERE ag_contatto.CDNLAVORATORE = " + cdnLavoratore);
		} else {
			buf.append(" WHERE ag_contatto.PRGAZIENDA = " + prgAzienda + " AND ag_contatto.PRGUNITA = " + prgUnita);
		}

		if (codCpi.equals("")) {
			codCpi = user.getCodRif();
		}

		String tuttiCPI = req.containsAttribute("tuttiCPI") ? req.getAttribute("tuttiCPI").toString() : "N";

		if ("N".equals(tuttiCPI)) {
			buf.append(" AND AG_CONTATTO.CODCPICONTATTO = '" + codCpi + "'");
		}

		buf.append(" order by AG_CONTATTO.DATCONTATTO desc, AG_CONTATTO.STRORACONTATTO desc");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}