package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un lavoratore.
 * 
 * @author Luigi Antenucci
 */
public class DynListSelezLavoratori implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("strNome_ric");
		String cognome = (String) req.getAttribute("strCognome_ric");
		String cf = (String) req.getAttribute("strCodiceFiscale_ric");
		String datnasc = (String) req.getAttribute("datnasc");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		DynamicStatementUtils dsu = new DynamicStatementUtils();
		dsu.addSelect(" l.CDNLAVORATORE," + " STRCODICEFISCALE," + " STRCOGNOME," + " STRNOME,"
				+ " STRSESSO, TO_CHAR(DATNASC, 'DD/MM/YYYY') DATNASC," + " CODCOMNAS, CODCITTADINANZA,"
				+ " CODCITTADINANZA2, CODSTATOCIVILE," + " CODCOMRES, NUMFIGLI, STRINDIRIZZORES,"
				+ " STRLOCALITARES, STRCAPRES, l.CODCOMDOM," + " STRINDIRIZZODOM, STRLOCALITADOM, STRCAPDOM,"
				+ " STRTELRES, STRTELDOM, STRTELALTRO, STRCELL," + " l.STREMAIL, l.STRFAX, l.STRNOTE, l.CDNUTINS,"
				+ " TO_CHAR(l.DTMINS, 'DD/MM/YYYY') DTMINS,"
				+ " l.CDNUTMOD, TO_CHAR(l.DTMMOD, 'DD/MM/YYYY') DTMMOD, NUMKLOLAVORATORE," + " FLGCFOK,"
				+ " decode(gen.codProvinciaSil, null, 'Fuori Provincia', cpi.strDescrizione) as CpiTit");
		dsu.addFrom(" AN_LAVORATORE l"
				+ " INNER JOIN an_lav_storia_inf ai on (l.cdnLavoratore=ai.cdnLavoratore and ai.datFine is null)"
				+ " LEFT  JOIN de_cpi cpi on (ai.codCpiTit=cpi.codCpi)"
				+ " LEFT  JOIN ts_generale gen on (cpi.codProvincia=gen.codProvinciaSil)");

		if (tipoRic.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("l.strnome", nome);
			dsu.addWhereIfFilledStrUpper("l.strcognome", cognome);
			dsu.addWhereIfFilledStrUpper("l.strCodiceFiscale", cf);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("l.strnome", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("l.strcognome", cognome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("l.strCodiceFiscale", cf, DynamicStatementUtils.DO_LIKE_INIZIA);
		}

		if (StringUtils.isFilled(datnasc)) {
			dsu.addWhere("l.datnasc = TO_DATE('" + datnasc + "','DD/MM/YYYY')");
		}

		dsu.addOrder("l.STRCOGNOME, l.STRNOME");

		String query = dsu.getStatement();

		return query;
	}

}