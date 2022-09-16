package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.clicLavoro.candidatura.DatiCurriculariLavoratore;
import it.eng.sil.module.movimenti.constant.Properties;

public class CLCandidaturaAnteprima extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void service(SourceBean request, SourceBean response)
			throws SourceBeanException, EMFInternalError, ParseException, EMFUserError {

		String dataInvio = request.getAttribute("dataInvio").toString();
		String cdnLavoratore = request.getAttribute("CDNLAVORATORE").toString();

		Object[] parameterConfig = new Object[1];
		parameterConfig[0] = "CUR_CLIC";
		SourceBean configurazione = (SourceBean) QueryExecutor.executeQuery("GET_NUM_CONFIG_LOC_PARAM", parameterConfig,
				TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);
		String numConfigCurr = (String) configurazione.getAttribute("ROW.NUM");

		if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
			// codice precedente
			Object[] inputParametersMans = new Object[2];
			inputParametersMans[0] = dataInvio;
			inputParametersMans[1] = cdnLavoratore;
			SourceBean retMans = (SourceBean) QueryExecutor.executeQuery("CL_GET_DIS_CONTRATTO_MANSIONI_DES",
					inputParametersMans, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

			Vector<SourceBean> vecMansioni = retMans.getAttributeAsVector("ROW");
			if (vecMansioni.size() != 0) {

				SourceBean professioniDesiderate = new SourceBean("professioniDesiderate");

				String mansioniDes = "";
				String desidDes = "";
				String orariDes = "";
				String contrattiDes = "";
				String dispTerr = "";
				String mezzi = "";

				for (int i = 0; i < vecMansioni.size(); i++) {

					SourceBean professioneDesiderata = new SourceBean("professioneDesiderata");

					SourceBean mansione = (SourceBean) vecMansioni.get(i);
					String prgMansione = ((BigDecimal) mansione.getAttribute("PRGMANSIONE")).toString();

					professioneDesiderata.setAttribute("MANSIONE", mansione.getAttribute("DESCRIZIONE"));

					if ("".equals(mansioniDes)) {
						mansioniDes = (String) mansione.getAttribute("DESCRIZIONE");
					} else {
						mansioniDes = mansioniDes + ", " + (String) mansione.getAttribute("DESCRIZIONE");
					}

					Object[] inputParametersDesid = new Object[3];
					inputParametersDesid[0] = dataInvio;
					inputParametersDesid[1] = cdnLavoratore;
					inputParametersDesid[2] = prgMansione;
					SourceBean retDesid = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_DES",
							inputParametersDesid, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecDesid = retDesid.getAttributeAsVector("ROW");

					if (vecDesid.size() != 0) {
						for (int j = 0; j < vecDesid.size(); j++) {
							SourceBean desid = (SourceBean) vecDesid.get(j);
							if ("".equals(desidDes)) {
								desidDes = (String) desid.getAttribute("DESCRIZIONE");
							} else {
								desidDes = desidDes + ", " + (String) desid.getAttribute("DESCRIZIONE");
							}

							if (desid.getAttribute("DESCRIZIONEPROFESSIONE") != null) {
								professioneDesiderata.setAttribute("DESCRIZIONEPROFESSIONE",
										desid.getAttribute("DESCRIZIONEPROFESSIONE"));
							}

						}

					}

					// breve descrizione esperienza e durata espressione

					Object[] inputDescrizioneEsperienze = new Object[1];
					inputDescrizioneEsperienze[0] = prgMansione;
					SourceBean descrizioneEsperienze = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_MANSIONE_DESCRIZIONEESPERIENZA", inputDescrizioneEsperienze,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);
					int numEsperienze = descrizioneEsperienze.getAttributeAsVector("ROW").size();
					if (numEsperienze > 0) {
						SourceBean descrizioneEsperienza = (new DatiCurriculariLavoratore(null, null, null, null))
								.groupByColumnWithSeparator(descrizioneEsperienze, "DESCRIZIONEESPERIENZA", 300, "; ");
						if (descrizioneEsperienza != null) {
							professioneDesiderata.setAttribute("DESCRIZIONEESPERIENZA",
									descrizioneEsperienza.getAttribute("ROW.DESCRIZIONEESPERIENZA"));
						}
					}

					Object[] inputParametersOrari = new Object[2];
					inputParametersOrari[0] = cdnLavoratore;
					inputParametersOrari[1] = prgMansione;
					SourceBean retOrari = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_ORARIO_DES",
							inputParametersOrari, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecOrari = retOrari.getAttributeAsVector("ROW");

					String orarioProfessioneDesiderata = "";
					if (vecOrari.size() != 0) {
						for (int j = 0; j < vecOrari.size(); j++) {
							SourceBean orari = (SourceBean) vecOrari.get(j);
							String orario = (String) orari.getAttribute("idmodalitalavorativa");
							if (orario != null && !"null".equals(orario)) {
								if ("".equals(orariDes)) {
									orariDes = orario;
								} else {
									orariDes = orariDes + ", " + orario;
								}

								if ("".equals(orarioProfessioneDesiderata)) {
									orarioProfessioneDesiderata = orario;
								} else {
									orarioProfessioneDesiderata += (", " + orario);
								}

							}
						}

					}

					professioneDesiderata.setAttribute("ORARIO", orarioProfessioneDesiderata);

					// rimosso il 12/11/2012 su indicazioni di Novella
					// Object[] inputParametersConratti = new Object[2];
					// inputParametersConratti[0] = cdnLavoratore;
					// inputParametersConratti[1] = prgMansione;
					// SourceBean retContratti = (SourceBean)
					// QueryExecutor.executeQuery("CL_GET_PROF_DESID_TIPI_CONTRATTO_DES", inputParametersConratti,
					// TransactionQueryExecutor.SELECT);
					//
					// Vector<SourceBean> vecContratti = retContratti.getAttributeAsVector("ROW");
					//
					// if (vecContratti.size() != 0) {
					// for (int j = 0; j < vecContratti.size(); j++) {
					// SourceBean contratti = (SourceBean)vecContratti.get(j);
					// String contratto = (String)contratti.getAttribute("descrizione");
					// if(contratto != null && !"null".equals(contratto)){
					// if("".equals(contrattiDes)){
					// contrattiDes = contratto;
					// }else{
					// contrattiDes = contrattiDes + ", " + contratto;
					// }
					// }
					// }
					//
					// }

					Object[] inputParametersMezzi = new Object[2];
					inputParametersMezzi[0] = cdnLavoratore;
					inputParametersMezzi[1] = prgMansione;
					SourceBean retMezzi = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_MEZZI_TRASPORTO",
							inputParametersMezzi, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					mezzi = (String) retMezzi.getAttribute("ROW.mezzitrasporto");

					if (mezzi == null) {
						mezzi = " ";
					}

					professioneDesiderata.setAttribute("MEZZI", mezzi);

					Object[] inputParametersTerritorio = new Object[2];
					inputParametersTerritorio[0] = cdnLavoratore;
					inputParametersTerritorio[1] = prgMansione;
					SourceBean retTerritorio = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_DISP_TERRIT",
							inputParametersTerritorio, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					String territorio = (String) retTerritorio.getAttribute("ROW.disponibilitaterritoriale");

					if ("00".equals(territorio)) {
						dispTerr = "NO";
					} else {
						dispTerr = "SI";
					}

					// raggruppamento delle informazioni in un solo sb
					if (professioneDesiderata != null) {
						professioniDesiderate.setAttribute(professioneDesiderata);
					}

				}
				response.setAttribute("MANSIONI", mansioniDes);
				response.setAttribute("TERRITORIO", dispTerr);
				response.setAttribute("MEZZI", mezzi);
				response.setAttribute("CONTRATTI", contrattiDes);
				response.setAttribute("PROFDESIDERATE", desidDes);
				response.setAttribute("ORARI", orariDes);

				if (professioniDesiderate != null) {
					response.setAttribute(professioniDesiderate);
				}

			}
		} else {
			// configurazione custom
			// aggiunto gennaio 2018

			Object[] inputParametersMans = new Object[2];
			inputParametersMans[0] = dataInvio;
			inputParametersMans[1] = cdnLavoratore;
			SourceBean retMans = (SourceBean) QueryExecutor.executeQuery("CL_GET_DIS_CONTRATTO_MANSIONI_DES",
					inputParametersMans, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

			Vector<SourceBean> vecMansioni = retMans.getAttributeAsVector("ROW");
			if (vecMansioni.size() != 0) {

				SourceBean professioniDesiderate = new SourceBean("professioniDesiderate");

				String mansioniDes = "";
				String desidDes = "";
				String orariDes = "";
				String contrattiDes = "";
				String dispTerr = "";
				String mezzi = "";

				for (int i = 0; i < vecMansioni.size(); i++) {

					SourceBean professioneDesiderata = new SourceBean("professioneDesiderata");

					SourceBean mansione = (SourceBean) vecMansioni.get(i);
					String prgMansione = ((BigDecimal) mansione.getAttribute("PRGMANSIONE")).toString();

					professioneDesiderata.setAttribute("MANSIONE", mansione.getAttribute("DESCRIZIONE"));

					if ("".equals(mansioniDes)) {
						mansioniDes = (String) mansione.getAttribute("DESCRIZIONE");
					} else {
						mansioniDes = mansioniDes + ", " + (String) mansione.getAttribute("DESCRIZIONE");
					}

					Object[] inputParametersDesid = new Object[3];
					inputParametersDesid[0] = dataInvio;
					inputParametersDesid[1] = cdnLavoratore;
					inputParametersDesid[2] = prgMansione;
					SourceBean retDesid = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_DES",
							inputParametersDesid, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecDesid = retDesid.getAttributeAsVector("ROW");

					if (vecDesid.size() != 0) {
						for (int j = 0; j < vecDesid.size(); j++) {
							SourceBean desid = (SourceBean) vecDesid.get(j);
							if ("".equals(desidDes)) {
								desidDes = (String) desid.getAttribute("DESCRIZIONE");
							} else {
								desidDes = desidDes + ", " + (String) desid.getAttribute("DESCRIZIONE");
							}

							if (desid.getAttribute("DESCRIZIONEPROFESSIONE") != null) {
								professioneDesiderata.setAttribute("DESCRIZIONEPROFESSIONE",
										desid.getAttribute("DESCRIZIONEPROFESSIONE"));
							}
							if (desid.getAttribute("esperienzasettore") != null) {
								professioneDesiderata.setAttribute("ESPERIENZA",
										desid.getAttribute("esperienzasettore"));
							}

						}

					}

					// breve descrizione esperienza e durata espressione

					Object[] inputDescrizioneEsperienze = new Object[1];
					inputDescrizioneEsperienze[0] = prgMansione;
					SourceBean descrizioneEsperienze = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_MANSIONE_DESCRIZIONEESPERIENZA", inputDescrizioneEsperienze,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);
					int numEsperienze = descrizioneEsperienze.getAttributeAsVector("ROW").size();
					if (numEsperienze > 0) {
						SourceBean descrizioneEsperienza = (new DatiCurriculariLavoratore(null, null, null, null))
								.groupByColumnWithSeparator(descrizioneEsperienze, "DESCRIZIONEESPERIENZA", 300, "; ");
						if (descrizioneEsperienza != null) {
							professioneDesiderata.setAttribute("DESCRIZIONEESPERIENZA",
									descrizioneEsperienza.getAttribute("ROW.DESCRIZIONEESPERIENZA"));
						}
					}

					Object[] inputParametersOrari = new Object[2];
					inputParametersOrari[0] = cdnLavoratore;
					inputParametersOrari[1] = prgMansione;
					SourceBean retOrari = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_ORARIO_DES_SIL",
							inputParametersOrari, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecOrari = retOrari.getAttributeAsVector("ROW");

					String orarioProfessioneDesiderata = "";
					if (vecOrari.size() != 0) {
						for (int j = 0; j < vecOrari.size(); j++) {
							SourceBean orari = (SourceBean) vecOrari.get(j);
							String orario = (String) orari.getAttribute("idmodalitalavorativa");
							if (orario != null && !"null".equals(orario)) {
								if ("".equals(orariDes)) {
									orariDes = orario;
								} else {
									orariDes = orariDes + ", " + orario;
								}

								if ("".equals(orarioProfessioneDesiderata)) {
									orarioProfessioneDesiderata = orario;
								} else {
									orarioProfessioneDesiderata += (", " + orario);
								}

							}
						}

					}

					professioneDesiderata.setAttribute("ORARIO", orarioProfessioneDesiderata);

					Object[] inputParametersConratti = new Object[2];
					inputParametersConratti[0] = cdnLavoratore;
					inputParametersConratti[1] = prgMansione;
					SourceBean retContratti = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_PROF_DESID_TIPI_CONTRATTO_DES", inputParametersConratti,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecContratti = retContratti.getAttributeAsVector("ROW");
					String contrattoProfessioneDesiderata = "";

					if (vecContratti.size() != 0) {
						for (int j = 0; j < vecContratti.size(); j++) {
							SourceBean contratti = (SourceBean) vecContratti.get(j);
							String contratto = (String) contratti.getAttribute("descrizione");
							if (contratto != null && !"null".equals(contratto)) {
								if ("".equals(contrattiDes)) {
									contrattiDes = contratto;
								} else {
									contrattiDes = contrattiDes + ", " + contratto;
								}
							}

							if ("".equals(contrattoProfessioneDesiderata)) {
								contrattoProfessioneDesiderata = contratto;
							} else {
								contrattoProfessioneDesiderata += (", " + contratto);
							}
						}

					}
					professioneDesiderata.setAttribute("TIPIRAPPORTO", contrattoProfessioneDesiderata);

					Object[] inputParametersMezzi = new Object[2];
					inputParametersMezzi[0] = cdnLavoratore;
					inputParametersMezzi[1] = prgMansione;
					SourceBean retMezzi = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_MEZZI_TRASPORTO",
							inputParametersMezzi, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					mezzi = (String) retMezzi.getAttribute("ROW.mezzitrasporto");

					if (mezzi == null) {
						mezzi = " ";
					}

					professioneDesiderata.setAttribute("MEZZI", mezzi);

					Object[] inputParametersTerritorio = new Object[2];
					inputParametersTerritorio[0] = cdnLavoratore;
					inputParametersTerritorio[1] = prgMansione;
					SourceBean retTerritorio = (SourceBean) QueryExecutor.executeQuery("CL_GET_PROF_DESID_DISP_TERRIT",
							inputParametersTerritorio, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					String territorio = (String) retTerritorio.getAttribute("ROW.disponibilitaterritoriale");

					if ("00".equals(territorio)) {
						dispTerr = "NO";
					} else {
						dispTerr = "SI";
					}

					Object[] inputParametersDesidTurni = new Object[2];
					inputParametersDesidTurni[0] = dataInvio;
					inputParametersDesidTurni[1] = prgMansione;
					SourceBean turniDes = (SourceBean) QueryExecutor.executeQuery("CL_GET_DISPONIBILITA_TURNI_DES",
							inputParametersDesidTurni, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecTurni = turniDes.getAttributeAsVector("ROW");
					String turniDesiderati = "";
					if (vecTurni.size() != 0) {
						for (int j = 0; j < vecTurni.size(); j++) {
							SourceBean turno = (SourceBean) vecTurni.get(j);
							if ("".equals(turniDesiderati)) {
								turniDesiderati = (String) turno.getAttribute("TURNO");
							} else {
								turniDesiderati = turniDesiderati + ", " + (String) turno.getAttribute("TURNO");
							}

						}

					}
					professioneDesiderata.setAttribute("TURNI", turniDesiderati);

					Object[] inputParameterComuni = new Object[2];
					inputParameterComuni[0] = dataInvio;
					inputParameterComuni[1] = prgMansione;
					SourceBean comuniDes = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_DISPONIBILITA_TERRITORIO_COMUNI_DES", inputParameterComuni,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecComuni = comuniDes.getAttributeAsVector("ROW");
					HashMap comuniNote = new HashMap();
					if (vecComuni.size() != 0) {
						for (int j = 0; j < vecComuni.size(); j++) {
							SourceBean comuneNota = (SourceBean) vecComuni.get(j);
							String comune = StringUtils.getAttributeStrNotNull(comuneNota, "strdenominazione");
							String nota = StringUtils.getAttributeStrNotNull(comuneNota, "strnote");
							comuniNote.put(comune, nota);
						}

					}
					professioneDesiderata.setAttribute("COMUNI", comuniNote);

					Object[] inputParameterProvince = new Object[2];
					inputParameterProvince[0] = dataInvio;
					inputParameterProvince[1] = prgMansione;
					SourceBean provinceDes = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_DISPONIBILITA_TERRITORIO_PROVINCE_DES", inputParameterProvince,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecProvince = provinceDes.getAttributeAsVector("ROW");
					String provinceDesiderate = "";
					if (vecProvince.size() != 0) {
						for (int j = 0; j < vecProvince.size(); j++) {
							SourceBean province = (SourceBean) vecProvince.get(j);
							if ("".equals(provinceDesiderate)) {
								provinceDesiderate = (String) province.getAttribute("strdenominazione");
							} else {
								provinceDesiderate = provinceDesiderate + ", "
										+ (String) province.getAttribute("strdenominazione");
							}
						}

					}
					professioneDesiderata.setAttribute("PROVINCE", provinceDesiderate);

					Object[] inputParameterRegioni = new Object[2];
					inputParameterRegioni[0] = dataInvio;
					inputParameterRegioni[1] = prgMansione;
					SourceBean regioniDes = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_DISPONIBILITA_TERRITORIO_REGIONE_DES", inputParameterRegioni,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecRegioni = regioniDes.getAttributeAsVector("ROW");
					String regioniDesiderate = "";
					if (vecRegioni.size() != 0) {
						for (int j = 0; j < vecRegioni.size(); j++) {
							SourceBean regione = (SourceBean) vecRegioni.get(j);
							if ("".equals(regioniDesiderate)) {
								regioniDesiderate = (String) regione.getAttribute("strdenominazione");
							} else {
								regioniDesiderate = regioniDesiderate + ", "
										+ (String) regione.getAttribute("strdenominazione");
							}
						}

					}
					professioneDesiderata.setAttribute("REGIONI", regioniDesiderate);

					Object[] inputParameterStati = new Object[2];
					inputParameterStati[0] = dataInvio;
					inputParameterStati[1] = prgMansione;
					SourceBean statiDes = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_DISPONIBILITA_TERRITORIO_STATO_DES", inputParameterStati,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					Vector<SourceBean> vecStati = statiDes.getAttributeAsVector("ROW");
					String statiDesiderati = "";
					if (vecStati.size() != 0) {
						for (int j = 0; j < vecStati.size(); j++) {
							SourceBean stati = (SourceBean) vecStati.get(j);
							if ("".equals(statiDesiderati)) {
								statiDesiderati = (String) stati.getAttribute("strdenominazione");
							} else {
								statiDesiderati = statiDesiderati + ", "
										+ (String) stati.getAttribute("strdenominazione");
							}
						}

					}
					professioneDesiderata.setAttribute("STATI", statiDesiderati);

					Object[] inputParameterMobilita = new Object[2];
					inputParameterMobilita[0] = dataInvio;
					inputParameterMobilita[1] = prgMansione;
					SourceBean mobilitaDes = (SourceBean) QueryExecutor.executeQuery(
							"CL_GET_DISPONIBILITA_MOBILITA_MANSIONE_DES", inputParameterMobilita,
							TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

					professioneDesiderata.setAttribute("MOBILITA", mobilitaDes);

					// raggruppamento delle informazioni in un solo sb
					if (professioneDesiderata != null) {
						professioniDesiderate.setAttribute(professioneDesiderata);
					}

				}
				response.setAttribute("MANSIONI", mansioniDes);
				response.setAttribute("TERRITORIO", dispTerr);
				response.setAttribute("MEZZI", mezzi);
				response.setAttribute("CONTRATTI", contrattiDes);
				response.setAttribute("PROFDESIDERATE", desidDes);
				response.setAttribute("ORARI", orariDes);

				if (professioniDesiderate != null) {
					response.setAttribute(professioniDesiderate);
				}

			}
		}

	}

}
