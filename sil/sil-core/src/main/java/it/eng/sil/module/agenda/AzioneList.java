package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * @author: Fabio Spadaro 
 */

public class AzioneList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AzioneList.class.getName());

	public AzioneList() {
	}

	private static final String SELECT_SQL_BASE = ""
			+ " select de_azione_ragg.STRDESCRIZIONE      as  DESCOBBIETTIVOMISURAYEI, "
			+ "       de_azione.PRGAZIONI                 as  PRGAZIONI , "
			+ "       de_azione.STRDESCRIZIONE            as  AZIONE, "
			+ "       de_azione.DATINIZIOVAL              as  DATA_INIZIO_VALIDITA, "
			+ "       de_azione.DATFINEVAL                as  DATA_FINE_VALIDITA, "
			+ "       de_prestazione.PRGPRESTAZIONE       as  COMBOPRESTAZIONE, "
			+ "       de_prestazione.STRDESCRIZIONE       as  PRESTAZIONE, "
			+ "       mn_yg_tipo_attivita.CODTIPOATTIVITA as  COMBOTIPOATTIVITA, "
			+ "       mn_yg_tipo_attivita.STRDESCRIZIONE  as  TIPO_ATTIVITA " + "  from " + " DE_AZIONE_RAGG "
			+ "    JOIN " + " DE_AZIONE " + "   ON de_azione_ragg.PRGAZIONIRAGG = de_azione.PRGAZIONERAGG "
			+ "    LEFT JOIN " + " MA_AZIONE_TIPOATTIVITA "
			+ "   ON (ma_azione_tipoattivita.PRGAZIONI = de_azione.PRGAZIONI and "
			+ " trunc(sysdate) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval)) "
			+ "    LEFT JOIN " + " MN_YG_TIPO_ATTIVITA "
			+ "   ON mn_yg_tipo_attivita.codtipoattivita = ma_azione_tipoattivita.CODTIPOATTIVITA " + "    LEFT JOIN "
			+ " MA_AZIONE_PRESTAZIONE " + "   ON de_azione.prgazioni = ma_azione_prestazione.prgazioni "
			+ "    LEFT JOIN " + " DE_PRESTAZIONE "
			+ "   ON de_prestazione.prgprestazione = ma_azione_prestazione.prgprestazione ";

	public String getStatement(final RequestContainer requestContainer, final SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query_totale = SELECT_SQL_BASE;

		query_totale += " where 1=1 ";

		String comboTipoAttivita, comboPrestazione, comboObbiettivoMisuraYei, azione, flagMisuraYei, flagPolAttiva;

		comboTipoAttivita = req.getAttribute("comboTipoAttivita") != null
				? req.getAttribute("comboTipoAttivita").toString()
				: null;
		comboPrestazione = req.getAttribute("comboPrestazione") != null
				? req.getAttribute("comboPrestazione").toString()
				: null;
		comboObbiettivoMisuraYei = req.getAttribute("comboObbiettivoMisuraYei") != null
				? req.getAttribute("comboObbiettivoMisuraYei").toString()
				: null;
		azione = req.getAttribute("azione") != null ? req.getAttribute("azione").toString() : null;

		flagMisuraYei = req.getAttribute("flagMisuraYei") != null ? req.getAttribute("flagMisuraYei").toString() : null;
		flagPolAttiva = req.getAttribute("flagPolAttiva") != null ? req.getAttribute("flagPolAttiva").toString() : null;

		// Obiettivo/Misura YEI
		if (comboObbiettivoMisuraYei != null && !comboObbiettivoMisuraYei.equals("")) {
			query_totale += " and de_azione_ragg.PRGAZIONIRAGG= " + comboObbiettivoMisuraYei;
		}

		// Azione
		if (azione != null && !azione.equals("")) {
			query_totale += " and Upper(de_azione.STRDESCRIZIONE) like '%" + azione.toUpperCase() + "%'";
		}

		// Tipo attività
		if (comboTipoAttivita != null && !comboTipoAttivita.equals("")) {
			query_totale += " and Upper(mn_yg_tipo_attivita.codtipoattivita) = '" + comboTipoAttivita.toUpperCase()
					+ "'";
		}

		// Prestazione
		if (comboPrestazione != null && !comboPrestazione.equals("")) {
			query_totale += " and  DE_PRESTAZIONE.prgprestazione = " + comboPrestazione;
		}

		// Flag misura YEI
		if (flagMisuraYei != null && (flagMisuraYei.equalsIgnoreCase("on") || flagMisuraYei.equalsIgnoreCase("S")))
			query_totale += " and de_azione_ragg.FLG_MISURAYEI = 'S'";

		// Flag politica attiva
		if (flagPolAttiva != null && (flagPolAttiva.equalsIgnoreCase("on") || flagPolAttiva.equalsIgnoreCase("S")))
			query_totale += " and ma_azione_prestazione.FLGPOLATTIVA = 'S'";

		query_totale += " order by DATA_INIZIO_VALIDITA DESC";

		_logger.debug("sil.module.agenda.AzioneList " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;

	}

}