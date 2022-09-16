package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynListaCancellati implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select lav.CDNLAVORATORE CDNLAVORATORE, lav.STRCOGNOME, lav.STRNOME, to_char(lav.DATNASC,'dd/mm/yyyy') datnasc, "
			+ " com.strdenominazione comune, lav.strindirizzodom indirizzo, lav.strlocalitadom frazione, "
			+ " lav.strcapdom, lav.strteldom, to_char(mov.datiniziomov, 'dd/mm/yyyy') dataCess, "
			+ " azi.STRRAGIONESOCIALE, to_char(mob.DATINIZIO,'dd/mm/yyyy') datInizio, "
			+ " to_char(mob.DATFINE,'dd/mm/yyyy') datFine, tipo.STRDESCRIZIONE tipoMob, motFine.strdescrizione decadenza "
			+ " from am_mobilita_iscr mob" + " inner join an_lavoratore lav on (lav.CDNLAVORATORE = mob.CDNLAVORATORE) "
			+ " inner join de_comune com on (com.CODCOM = lav.CODCOMDOM) "
			+ " inner join de_mb_tipo tipo on (tipo.CODMBTIPO = mob.CODTIPOMOB) "
			+ " left join de_mb_motivo_fine motFine on (motFine.codMotivoFine = mob.codMotivoFine) "
			+ " left join am_movimento mov on (mov.PRGMOVIMENTO = mob.PRGMOVIMENTO) "
			+ " left join an_azienda azi on (azi.PRGAZIENDA = mov.PRGAZIENDA) "
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String dataInDA = StringUtils.getAttributeStrNotNull(req, "dataInDA");
		String dataInA = StringUtils.getAttributeStrNotNull(req, "dataInA");
		String tipoLista = StringUtils.getAttributeStrNotNull(req, "CodTipoLista");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CodCPI");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// se codCpi è vuoto, allora devo considerare solo i lavoratori la cui competenza è provinciale
		if (codCpi.equals("")) {
			buf.append(" inner join de_cpi on (de_cpi.codcpi=inf.codcpitit) ");
			buf.append(" inner join ts_generale on (ts_generale.codprovinciasil=de_cpi.codprovincia) ");
			buf.append(" where inf.codmonotipocpi = 'C' ");
		} else {
			buf.append(" where inf.codcpitit = '" + codCpi + "' ");
			buf.append(" and inf.codmonotipocpi = 'C' ");
		}

		if (!dataInDA.equals("")) {
			buf.append(" and trunc(mob.datFine) >= to_date('" + dataInDA + "', 'dd/mm/yyyy') ");
		}

		if (!dataInA.equals("")) {
			buf.append(" and trunc(mob.datFine) <= to_date('" + dataInA + "', 'dd/mm/yyyy') ");
		}

		if (!tipoLista.equals("")) {
			buf.append(" and mob.codtipoMob = '" + tipoLista + "' ");
		}

		buf.append(" order by lav.STRCOGNOME, lav.STRNOME ");
		query_totale.append(buf.toString());

		return query_totale.toString();
	}
}
