package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DateUtils;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;

public class RisultatiProsettiDaCopiare implements IDynamicStatementProvider {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RisultatiProsettiDaCopiare.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = "select p.prgProspettoInf, az.strcodicefiscale CFAz, "
			+ "p.prgazienda, p.prgunita, az.strragionesociale strRagioneSociale, "
			+ "uaz.strindirizzo || ' - ' || uaz.strcap || ' ' || com.strdenominazione || ' (' || dpaz.strtarga || ')' strIndirizzoUA, "
			+ "case " + "when az.flgobbligol68 = 'S' then 'Sì' " + "when az.flgobbligol68 = 'N' then 'No' "
			+ " else '&nbsp;' " + " end flgObbligoL68, " + "dp.strdenominazione provinciaProsp," + "case "
			+ "    when p.codmonocategoria = 'A' " + "	     then 'più di 50 dipendenti' "
			+ "    when p.codmonocategoria = 'B'     " + "      then 'da 36 a 50 dipendenti' "
			+ "    when p.codmonocategoria = 'C' " + "      then 'da 15 a 35 dipendenti' " + "	else '&nbsp;' "
			+ " end codMonoCategoria " + " from cm_prospetto_inf p "
			+ " inner join an_azienda az on (p.prgazienda = az.prgazienda) "
			+ " inner join an_unita_azienda uaz on (az.prgAzienda = uaz.prgAzienda and uaz.prgunita = p.prgunita) "
			+ " inner join de_comune com on (uaz.codcom = com.codcom) "
			+ " left join de_provincia dpAz on dpAz.codprovincia = com.codprovincia "
			+ " left join de_provincia dp on dp.codprovincia = p.codprovincia ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();
		String annoNuovo = (String) req.getAttribute("annoNew");
		String annoOld = (String) req.getAttribute("annoOld");
		String codMonoCategoria = (String) req.getAttribute("codMonoCategoria");

		if (annoNuovo == null || annoNuovo.equals("") || annoOld == null || annoOld.equals("")) {
			return null;
		}

		int numAnnoCorrente = DateUtils.getAnno();
		int numAnnoNuovo = new Integer(annoNuovo).intValue();
		int numAnnoRifInitCopia = ProspettiConstant.ANNO_INIZIO_COPIA_IN_CORSO;
		int numAnnoVecchio = new Integer(annoOld).intValue();

		if ((numAnnoNuovo < numAnnoRifInitCopia) || (numAnnoNuovo > numAnnoCorrente)
				|| (numAnnoVecchio < numAnnoRifInitCopia) || (numAnnoVecchio > (numAnnoCorrente - 1))
				|| (numAnnoVecchio >= numAnnoNuovo)) {
			return null;
		}

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String prospAnnoNuovo = " left join cm_prospetto_inf prosp on "
				+ " (prosp.prgazienda = p.prgazienda and prosp.codprovincia = p.codprovincia "
				+ " and prosp.prgProspettoInf <> p.prgProspettoInf and prosp.codmonostatoprospetto = 'A' "
				+ " and prosp.numannorifprospetto = to_number('" + annoNuovo + "') ) ";

		buf.append(prospAnnoNuovo);

		buf.append(" where p.codmonostatoprospetto = '" + ProspettiConstant.STATO_IN_CORSO_ANNO + "' ");

		buf.append(" and prosp.prgProspettoInf is null ");

		buf.append(" and p.numannorifprospetto = to_number('" + annoOld + "') ");

		if ((codMonoCategoria != null) && (!codMonoCategoria.equals(""))) {
			if (codMonoCategoria.equals(ProspettiConstant.CATEGORIA_NULLA)) {
				buf.append(" and p.codmonocategoria is null ");
			} else {
				buf.append(" and p.codmonocategoria = '" + codMonoCategoria + "' ");
			}
		}

		buf.append(" ORDER BY az.strcodicefiscale, az.strragionesociale, dp.strdenominazione, p.codmonocategoria ");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}