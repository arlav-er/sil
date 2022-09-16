package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynLavObbligoIstruzione implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select   TO_CHAR (lav.datnasc, 'dd/mm/yyyy') datnasc,"
			+ " lav.strCognome as strCognome, " + " lav.strNome as strNome, " + " lav.strCodiceFiscale as strCF, "
			+ " com.strdenominazione || ' (' || prov.strtarga || ')'   as comNasc, " +
			// " com.strdenominazione || ' (' || prov.strtarga || ')' comnasc," +
			" citt.strdescrizione cittadinanza,lav.cdnlavoratore," + " cpi.strdescrizione cpi,"
			+ " decode(ob.FLGOBBLIGOFORMATIVO,'S','Assolto','Non assolto') flgObFormativo,"
			+ " decode(ob.FLGOBBLIGOSCOLASTICO,'S','Assolto','Non assolto') flgObScolastico,"
			+ " cond.STRDESCRIZIONE tipoCond " + " FROM am_obbligo_formativo ob"
			+ " inner join an_lavoratore lav on (ob.cdnlavoratore = lav.cdnlavoratore)"
			+ " inner join de_comune com ON (com.codcom = lav.codcomnas)"
			+ " left join am_obbligo_istruzione istr ON (istr.cdnlavoratore = ob.cdnlavoratore)"
			+ " left join de_tipo_condizione cond ON (cond.codtipocondizione = istr.codtipocondizione)"
			+ " left join de_provincia prov ON (prov.codprovincia = com.codprovincia )"
			+ " inner join de_cittadinanza citt ON (citt.codcittadinanza = lav.codcittadinanza)"
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) "
			+ " inner join de_cpi cpi on (cpi.codCpi=inf.codCpiTit) " + " where ob.CODMOTIVOARCHIVIAZIONE is null "
			+ "	and trunc (months_between(sysdate,lav.DATNASC)/12) > 0 and trunc(months_between(sysdate,lav.DATNASC)/12) < 18 ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String flgObbligoFormativo = StringUtils.getAttributeStrNotNull(req, "flgObbligoFormativo");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");
		String flgObbligoScolastico = StringUtils.getAttributeStrNotNull(req, "flgObbligoScolastico");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		StringBuffer buf = new StringBuffer();

		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and inf.CODCPITIT = '" + codCpi + "' ");
		}

		if ((flgObbligoFormativo != null) && (!flgObbligoFormativo.equals(""))) {
			buf.append(" and ob.FLGOBBLIGOFORMATIVO ='" + flgObbligoFormativo + "' ");
		}

		if ((flgObbligoScolastico != null) && (!flgObbligoScolastico.equals(""))) {
			buf.append(" and ob.FLGOBBLIGOSCOLASTICO ='" + flgObbligoScolastico + "' ");
		}

		buf.append(" and ( istr. datafineobbligo is null  or  istr. datafineobbligo > trunc(sysdate) ) ");

		buf.append(" order by lav.strcognome, lav.strnome ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
