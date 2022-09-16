package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class DynamicAzioniCruscotto implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE_1 = "select percorso.prgpercorso, percorso.prgcolloquio, "
			+ " de_azione.strdescrizione descAzione, de_esito.strdescrizione descEsito, to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') datAdesione, "
			+ " to_char(percorso.datstimata, 'dd/mm/yyyy') datStimata, to_char(or_colloquio.datcolloquio, 'dd/mm/yyyy') datColloquio "
			+ " from or_percorso_concordato percorso "
			+ " inner join or_colloquio on (percorso.PRGCOLLOQUIO = or_colloquio.PRGCOLLOQUIO) "
			+ " inner join de_azione on (percorso.prgazioni = de_azione.prgazioni) "
			+ " inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni and "
			+ "	trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval)) "
			+ " inner join de_esito on (percorso.codesito = de_esito.codesito) "
			+ " inner join or_percorso_concordato perAdesione on (percorso.prgpercorsoadesione = perAdesione.prgpercorso and "
			+ " percorso.prgcolloquioadesione = perAdesione.prgcolloquio) ";

	private static final String SELECT_SQL_BASE_2 = "select percorso.prgpercorso, percorso.prgcolloquio, "
			+ " de_azione.strdescrizione descAzione, de_esito.strdescrizione descEsito, null datAdesione, "
			+ " to_char(percorso.datstimata, 'dd/mm/yyyy') datStimata, to_char(or_colloquio.datcolloquio, 'dd/mm/yyyy') datColloquio "
			+ " from or_percorso_concordato percorso "
			+ " inner join or_colloquio on (percorso.PRGCOLLOQUIO = or_colloquio.PRGCOLLOQUIO) "
			+ " inner join de_azione on (percorso.prgazioni = de_azione.prgazioni) "
			+ " inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni and "
			+ "	trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval)) "
			+ " inner join de_esito on (percorso.codesito = de_esito.codesito) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		SourceBean req = requestContainer.getServiceRequest();
		String dataAdesione = "";
		String cdnLavoratore = Utils.notNull(req.getAttribute("CDNLAVORATORE"));
		BigDecimal cdnLav = null;
		if (!cdnLavoratore.equals("")) {
			cdnLav = new BigDecimal(cdnLavoratore);
		}
		dataAdesione = RequestContainer.getRequestContainer().getAttribute("YG_DATA_ADESIONE") != null
				? RequestContainer.getRequestContainer().getAttribute("YG_DATA_ADESIONE").toString()
				: "";
		if (dataAdesione.equals("")) {
			dataAdesione = RequestContainer.getRequestContainer().getAttribute("YG_DATA_ADESIONE_SISTEMA") != null
					? RequestContainer.getRequestContainer().getAttribute("YG_DATA_ADESIONE_SISTEMA").toString()
					: "";
		}

		String queryTotale = SELECT_SQL_BASE_1;
		String queryTotale2 = SELECT_SQL_BASE_2;

		if (!dataAdesione.equals("")) {
			queryTotale = queryTotale + " WHERE ";
			queryTotale = queryTotale + " or_colloquio.cdnlavoratore = " + cdnLav;
			queryTotale = queryTotale
					+ " and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU') ";
			queryTotale = queryTotale + " and trunc(perAdesione.datadesionegg) = to_date('" + dataAdesione
					+ "', 'dd/mm/yyyy') ";
			queryTotale = queryTotale + " union ";

			queryTotale = queryTotale + queryTotale2;

			queryTotale = queryTotale + " WHERE ";
			queryTotale = queryTotale + " or_colloquio.cdnlavoratore = " + cdnLav;
			queryTotale = queryTotale
					+ " and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU') ";
			queryTotale = queryTotale
					+ " and (percorso.prgpercorsoadesione is null or percorso.prgcolloquioadesione is null) ";

			return queryTotale;
		} else {
			queryTotale2 = queryTotale2 + " WHERE ";
			queryTotale2 = queryTotale2 + " or_colloquio.cdnlavoratore = " + cdnLav;
			queryTotale2 = queryTotale2
					+ " and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU') ";
			queryTotale2 = queryTotale2
					+ " and (percorso.prgpercorsoadesione is null or percorso.prgcolloquioadesione is null) ";

			return queryTotale2;
		}
	}
}
