package it.eng.sil.module.iscrizioni;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaLavConservazioneLSU implements IDynamicStatementProvider {
	public DynRicercaLavConservazioneLSU() {
	}

	private static final String SELECT_SQL_BASE = " select lav.CDNLAVORATORE CDNLAVORATORE, "
			+ " lav.STRCOGNOME || ' ' ||  lav.STRNOME || '\n' || lav.strcodicefiscale as datiLav, "
			+ " to_char(am_movimento.datiniziomov,'dd/mm/yyyy') as datInizioMov, "
			+ " to_char(am_movimento.datfinemoveffettiva,'dd/mm/yyyy') as datFineMov, "
			+ " to_char(iscr.DATINIZIO,'dd/mm/yyyy') as datInizio, "
			+ " to_char(iscr.DATFINE,'dd/mm/yyyy') as datFine, "
			+ " decode(azi.strcodicefiscale, null, '', azi.STRRAGIONESOCIALE || '\n' || azi.strcodicefiscale) as datiAz, "
			+ " tipo.STRDESCRIZIONE tipoIscr " + " from am_altra_iscr iscr "
			+ " inner join an_lavoratore lav on (lav.CDNLAVORATORE = iscr.CDNLAVORATORE) "
			+ " inner join am_movimento on (am_movimento.cdnlavoratore = lav.CDNLAVORATORE) "
			+ " inner join de_tipo_iscr tipo on (tipo.CODTIPOISCR = iscr.CODTIPOISCR) "
			+ " left join an_azienda azi on (azi.PRGAZIENDA = iscr.PRGAZIENDA) "
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String dataInizioDa = StringUtils.getAttributeStrNotNull(req, "datinizioda");
		String dataInizioA = StringUtils.getAttributeStrNotNull(req, "datinizioa");
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
		if (!dataInizioDa.equals("")) {
			buf.append(" and trunc(iscr.datinizio) >= to_date('" + dataInizioDa + "', 'dd/mm/yyyy') ");
		}
		if (!dataInizioA.equals("")) {
			buf.append(" and trunc(iscr.datinizio) <= to_date('" + dataInizioA + "', 'dd/mm/yyyy') ");
		}
		buf.append(
				" and am_movimento.codstatoatto = 'PR' and am_movimento.codtipomov <> 'CES' and am_movimento.codMonoTempo = 'D' ");
		buf.append(" and trunc(am_movimento.datiniziomov) >= trunc(iscr.datinizio) ");
		buf.append(
				" and trunc(am_movimento.datiniziomov) <= trunc(decode(iscr.DATCHIUSURAISCR, null, iscr.datfine, iscr.DATCHIUSURAISCR)) ");
		buf.append(" and iscr.codtipoiscr in ('IA', 'IB', 'IC', 'ID') ");
		buf.append(" and iscr.codstato is null ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}