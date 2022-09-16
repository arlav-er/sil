/*
 *
 */
package it.eng.sil.module.amministrazione;

import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author Girotti
 *
 */
public class DynamicRicercaPercorsoLavoratoreModular implements IDynamicStatementProvider {

	// vista per collocamento mirato
	private static final String STM_R = "SELECT vee.status," + " vee.servizi," + " vee.movimenti," + " vee.missioni,"
			+ " vee.url," + " vee.cdnlavoratore," + "vee.descrizionepercorso," + " vee.chiavedettaglio,"
			+ " vee.codmonotipoinf," + " vee.cpirif, vee.datdatainizio, vee.datasort2" +
			// ", " +
			// "vee.cdnlavoratore_crypt" +
			"  FROM vw_ric_perc_lav_r vee where vee.cdnlavoratore = ";

	// vista per mobilità
	private static final String STM_E = "SELECT * FROM vw_ric_perc_lav_e_uni vei WHERE vei.cdnlavoratore = ";

	private static final String STM_A = "SELECT * FROM vw_ric_perc_lav_a va WHERE va.cdnlavoratore = ";
	private static final String STM_B = "SELECT * FROM %s vb WHERE vb.cdnlavoratore = ";
	private static final String STM_C = "SELECT * FROM vw_ric_perc_lav_c vc WHERE vc.cdnlavoratore = ";
	private static final String STM_D = "SELECT * FROM vw_ric_perc_lav_d vd WHERE vd.cdnlavoratore = ";
	private static final String STM_F = "SELECT * FROM vw_ric_perc_lav_f vf WHERE vf.cdnlavoratore = ";
	private static final String STM_G = "SELECT * FROM vw_ric_perc_lav_g vg WHERE vg.cdnlavoratore = ";
	private static final String STM_H = "SELECT * FROM vw_ric_perc_lav_h vh WHERE vh.cdnlavoratore = ";
	private static final String STM_I = "SELECT * FROM vw_ric_perc_lav_i vi WHERE vi.cdnlavoratore = ";
	private static final String STM_L = "SELECT * FROM vw_ric_perc_lav_l vl WHERE vl.cdnlavoratore = ";
	private static final String STM_M = "SELECT * FROM vw_ric_perc_lav_m vm WHERE vm.cdnlavoratore = ";
	private static final String STM_N = "SELECT * FROM vw_ric_perc_lav_n vn WHERE vn.cdnlavoratore = ";
	private static final String STM_O = "SELECT * FROM %s vo WHERE vo.cdnlavoratore = ";
	private static final String STM_P = "SELECT * FROM vw_ric_perc_lav_p vp WHERE vp.cdnlavoratore = ";
	private static final String STM_Q = "SELECT * FROM vw_ric_perc_lav_q vq WHERE vq.cdnlavoratore = ";
	private static final String STM_S = "SELECT * FROM vw_ric_perc_lav_s vs WHERE vs.cdnlavoratore = ";

	private static final Map<String, String> tipo2Stm = new HashMap<String, String>();

	static {
		tipo2Stm.put("A", STM_A);

		tipo2Stm.put("C", STM_C);
		tipo2Stm.put("D", STM_D);
		tipo2Stm.put("E", STM_E);
		tipo2Stm.put("F", STM_F);
		tipo2Stm.put("G", STM_G);
		tipo2Stm.put("H", STM_H);
		tipo2Stm.put("I", STM_I);
		tipo2Stm.put("L", STM_L);
		tipo2Stm.put("M", STM_M);
		tipo2Stm.put("N", STM_N);

		tipo2Stm.put("P", STM_P);
		tipo2Stm.put("Q", STM_Q);
		tipo2Stm.put("R", STM_R);
		// Liste speciali
		tipo2Stm.put("S", STM_S);
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) request.getAttribute("cdnlavoratore");
		Object tipoInfo = request.getAttribute("TIPO_INFO");
		// String dataInizio2add = "";
		// String dataFine2add = "";
		//
		//
		// if (dataInizio != null && !dataInizio.equals("")) {
		// dataInizio2add = " and datdatainizio >= to_date('" + dataInizio + "','dd/mm/yyyy') ";
		// }
		// if (dataFine != null && !dataFine.equals("")) {
		// dataFine2add = " and datdatainizio <= to_date('" + dataFine + "','dd/mm/yyyy') ";
		// }

		// SessionContainer session = requestContainer.getSessionContainer();
		// String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");
		// String stmRis = null;
		// String cod2vis = null;

		// StringBuffer stm = new StringBuffer();
		String stmFinal = null;
		if (tipoInfo != null) {
			Vector infoDaVisualizzare = null;
			infoDaVisualizzare = request.getAttributeAsVector("TIPO_INFO");
			// stm.append(" and lav.codmonotipoinf in (");
			// final int info2VisSize = infoDaVisualizzare.size();
			String stmBshowQual = StringUtils.getAttributeStrNotNull(request, "movAmmComObbligatoria");
			String stmOshowQual = StringUtils.getAttributeStrNotNull(request, "movAmmDichLav");
			String dataInizio = (String) request.getAttribute("datainizio");
			String dataFine = (String) request.getAttribute("datafine");
			// Map<String, String> _tipo2Stm = new HashMap<String, String>();
			// _tipo2Stm.putAll(tipo2Stm);
			stmFinal = popolaStmRicercaPercLav(cdnLavoratore, dataInizio, dataFine, infoDaVisualizzare, stmBshowQual,
					stmOshowQual);

		}

		return stmFinal;
	}

	public static String popolaStmRicercaPercLav(String cdnLavoratore, String dataInizio, String dataFine,
			Collection infoDaVisualizzare, String stmBshowQual, String stmOshowQual) {
		Map<String, String> _tipo2Stm = new HashMap<String, String>();
		_tipo2Stm.putAll(tipo2Stm);
		String dataInizio2add = "";
		if (dataInizio != null && !dataInizio.equals("")) {
			dataInizio2add = " and datdatainizio >= to_date('" + dataInizio + "','dd/mm/yyyy') ";
		}
		String dataFine2add = "";
		if (dataFine != null && !dataFine.equals("")) {
			dataFine2add = " and datdatainizio <= to_date('" + dataFine + "','dd/mm/yyyy') ";
		}
		String stmRis;
		String cod2vis;

		/**
		 * Visualizzare qualifica per query movimenti (cod. B e O) INIZIO
		 */
		Formatter format = new Formatter();

		String stmB = null;
		if ("".equals(stmBshowQual) || "S".equals(stmBshowQual))
			stmB = format.format(STM_B, "vw_ric_perc_lav_b").toString();
		else if ("N".equals(stmBshowQual))
			stmB = format.format(STM_B, "vw_ric_perc_lav_b_nq").toString();

		format.close();

		format = new Formatter();

		String stmO = null;
		if ("".equals(stmOshowQual) || "S".equals(stmOshowQual))
			stmO = format.format(STM_O, "vw_ric_perc_lav_o_sq").toString();
		else if ("N".equals(stmOshowQual))
			stmO = format.format(STM_O, "vw_ric_perc_lav_o").toString();

		format.close();

		_tipo2Stm.put("B", stmB);
		_tipo2Stm.put("O", stmO);
		/** FINE */

		Iterator it = infoDaVisualizzare.iterator();
		StringBuffer stm = new StringBuffer();
		stm.append("SELECT * FROM (");
		while (it.hasNext()) {
			cod2vis = it.next().toString();

			stmRis = _tipo2Stm.get(cod2vis);
			stm.append(stmRis);
			stm.append("'");
			stm.append(cdnLavoratore);
			stm.append("'");

			stm.append(dataInizio2add);
			stm.append(dataFine2add);
			if (it.hasNext()) {
				// c'è altro da aggiungere...
				// connetto le string con union
				stm.append(" UNION ");
			}
		}
		stm.append(") order by DATDATAINIZIO desc, dataSort2 desc");
		return stm.toString();
	}

}
