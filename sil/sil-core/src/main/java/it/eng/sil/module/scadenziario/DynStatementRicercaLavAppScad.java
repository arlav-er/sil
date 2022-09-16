package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/**
 * Effettua la ricerca dinamica dei lavoratori per gli appuntamenti
 * 
 * @author Giuseppe De Simone
 * 
 */
public class DynStatementRicercaLavAppScad implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT ag_lavoratore.CDNLAVORATORE, an_lavoratore.STRCOGNOME, "
			+ "an_lavoratore.STRNOME, an_lavoratore.STRCODICEFISCALE, "
			+ "to_char(an_lavoratore.DATNASC, 'dd/mm/yyyy') as DATNASC, "
			+ "am_lps.PRGLAVPATTOSCELTA, to_char(am_patto_lavoratore.DATSTIPULA, 'dd/mm/yyyy') as data_stipula, "
			+ "nvl(de_codifica_patto.strdescrizione, decode(am_patto_lavoratore.FLGPATTO297, 'S', 'Patto 150', "
			+ "'N', 'Accordo Generico', '')) as patto297, " + "de_stato_atto.STRDESCRIZIONE as tipo_patto, "
			+ "de_motivo_fine_atto.STRDESCRIZIONE as mot_fine_atto " + "FROM ag_lavoratore "
			+ "left outer join an_lavoratore on (ag_lavoratore.CDNLAVORATORE=an_lavoratore.CDNLAVORATORE) "
			+ "left outer join am_lav_patto_scelta am_lps "
			+ "on (ag_lavoratore.CDNLAVORATORE = am_lps.STRCHIAVETABELLA and "
			+ "ag_lavoratore.CODCPI = am_lps.STRCHIAVETABELLA2 and "
			+ "ag_lavoratore.PRGAPPUNTAMENTO = am_lps.STRCHIAVETABELLA3 and am_lps.CODLSTTAB='AG_LAV') "
			+ "left outer join am_patto_lavoratore on (am_lps.PRGPATTOLAVORATORE = am_patto_lavoratore.PRGPATTOLAVORATORE) "
			+ "left join de_stato_atto on (am_patto_lavoratore.codStatoAtto = de_stato_atto.CODSTATOATTO) "
			+ "left outer join de_motivo_fine_atto on (am_patto_lavoratore.CODMOTIVOFINEATTO=de_motivo_fine_atto.CODMOTIVOFINEATTO) "
			+ "left outer join de_codifica_patto on (am_patto_lavoratore.CODCODIFICAPATTO=de_codifica_patto.CODCODIFICAPATTO)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();

		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";
		String prgAppuntamento = req.containsAttribute("PRGAPPUNTAMENTO")
				? req.getAttribute("PRGAPPUNTAMENTO").toString()
				: "";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" WHERE ag_lavoratore.PRGAPPUNTAMENTO = " + prgAppuntamento);

		/*
		 * Se il codCpi non è presente nella request, recupero il dato dalla sessione
		 */
		if (codCpi.equals("")) {
			codCpi = user.getCodRif();
		}
		buf.append(" AND ag_lavoratore.CODCPI = '" + codCpi + "'");

		buf.append(" order by an_lavoratore.strCognome, an_lavoratore.strNome");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}