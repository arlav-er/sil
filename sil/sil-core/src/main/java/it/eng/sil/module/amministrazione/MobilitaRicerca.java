package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Sottosistema;

public class MobilitaRicerca implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaRicerca.class.getName());

	private static final String SELECT_SQL_BASE = " LAV.STRCOGNOME COGNOME," + " LAV.STRNOME NOME,"
			+ " LAV.STRCODICEFISCALE CF," + " MOB.PRGMOBILITAISCR," + " MOB.CDNLAVORATORE," + " case "
			+ "    when (nvl(mob.datfine, sysdate) < sysdate)" + "      then 'true' " + "      else 'false' "
			+ " end storicizzato, " + " MOB.CODTIPOMOB, STATO_RICH.STRDESCRIZIONE stato, "
			// + " to_char(MOB.DATCRT ,'DD/MM/YY') DATCRT,"
			+ " case " + " when ma.codlistespec is not null "
			+ " then (DE_MB_TIPO.STRDESCRIZIONE || ' (Min: ' || mn.des_listespec|| ')' ) "
			+ " else DE_MB_TIPO.STRDESCRIZIONE || ' (Min: nessuna corrispondenza)' " + " end as STRDESCRIZIONEMOB";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();

		DynamicStatementUtils dsu = new DynamicStatementUtils();

		String nome = (String) req.getAttribute("nome");
		String cognome = (String) req.getAttribute("cognome");
		String cf = (String) req.getAttribute("CF");
		String flgNonImprenditore = StringUtils.getAttributeStrNotNull(req, "flgNonImprenditore");
		String flgCasoDubbio = StringUtils.getAttributeStrNotNull(req, "flgCasoDubbio");
		String codTipoMob = "";
		Vector codStatoMob = new Vector();
		String soggettiCM = "";
		String datInizioMovDa = "";
		String datInizioMovA = "";
		String soggettiConMov = "";
		String flgMobAperte = "";
		String flgMobStor = "";
		String flgEscludiMotFineMob = "";
		String dataStoricizzata = "";

		dsu.addSelect(
				"  (to_char(MOB.DATINIZIO,'DD/MM/YYYY') || '\n' || to_char(MOB.DATFINE  ,'DD/MM/YYYY')) as DATES");

		if (req.containsAttribute("stampa")) {
			dsu.addSelect(
					" to_char(MOB.DATCRT ,'DD/MM/YYYY') DATCRT,  to_char(MOB.DATMAXDIFF ,'DD/MM/YYYY') DATMAXDIFF");
		} else {
			dsu.addSelect(
					" (to_char(MOB.DATCRT ,'DD/MM/YY') || '\n' || to_char(MOB.DATMAXDIFF ,'DD/MM/YYYY')) as DIFFCRT");
		}

		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
			codTipoMob = (String) req.getAttribute("CodTipoLista");
		} else {
			// END-PARTE-TEMP
			// valorizzazione di codContratto

			// Vector list = req.getAttributeAsVector("CodTipoLista");
			Vector list = new Vector();
			String codListaStr = "";
			String codStatoStr = "";
			StringTokenizer st = null;

			if (req.containsAttribute("stampa") || req.containsAttribute("aggiorna_stato")) {
				try {
					codListaStr = StringUtils.getAttributeStrNotNull(req, "CodTipoLista");
					st = new StringTokenizer(codListaStr, ",");
					for (; st.hasMoreTokens();) {
						list.add(st.nextToken());
					}
					codStatoStr = StringUtils.getAttributeStrNotNull(req, "codStatoMob");
					st = new StringTokenizer(codStatoStr, ",");
					while (st.hasMoreTokens()) {
						codStatoMob.add(st.nextToken());
					}

				} catch (Exception e) {
					codStatoMob = req.getAttributeAsVector("CodStatoMob");
					list = req.getAttributeAsVector("CodTipoLista");
				}
			} else {
				codStatoMob = req.getAttributeAsVector("CodStatoMob");
				list = req.getAttributeAsVector("CodTipoLista");
			}
			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (!list.elementAt(i).equals("")) {
						if (codTipoMob.length() > 0) {
							codTipoMob = codTipoMob + "," + "'" + list.elementAt(i) + "'";
						} else {
							codTipoMob += "'" + list.elementAt(i) + "'";
						}
					}
				}
			}

			soggettiCM = StringUtils.getAttributeStrNotNull(req, "SoggettiCM").trim();
			soggettiConMov = StringUtils.getAttributeStrNotNull(req, "SoggettiConMov").trim();
			datInizioMovDa = StringUtils.getAttributeStrNotNull(req, "datInizioMovDa");
			datInizioMovA = StringUtils.getAttributeStrNotNull(req, "datInizioMovA");
			flgMobAperte = StringUtils.getAttributeStrNotNull(req, "FlgMobAperte").trim();
			flgMobStor = StringUtils.getAttributeStrNotNull(req, "FlgMobStor").trim();
			try {
				if (flgMobStor.equals("S")) {
					dataStoricizzata = DateUtils.giornoPrecedente(DateUtils.getNow());
				}
			} catch (Exception exData) {
				dataStoricizzata = "";
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		String datDomandaDa = (String) req.getAttribute("DATDOMANDADA");
		String datDomandaA = (String) req.getAttribute("DATDOMANDAA");
		String datInizioDa = (String) req.getAttribute("DATINIZIODA");
		String datInizioA = (String) req.getAttribute("DATINIZIOA");
		String datFineDa = (String) req.getAttribute("DATFINEDA");
		String datFineA = (String) req.getAttribute("DATFINEA");
		String datCRTDa = (String) req.getAttribute("DATCRTDA");
		String datCRTA = (String) req.getAttribute("DATCRTA");
		String datMaxDiffDa = (String) req.getAttribute("DATMAXDIFFDA");
		String datMaxDiffA = (String) req.getAttribute("DATMAXDIFFA");

		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");

		dsu.addSelect(SELECT_SQL_BASE);
		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
		} else {
			// END-PARTE-TEMP
			dsu.addSelect("AN_AZIENDA.STRRAGIONESOCIALE");
			dsu.addSelect("AN_UNITA_AZIENDA.STRINDIRIZZO");
			dsu.addSelect("DE_COMUNE.STRDENOMINAZIONE AS COMUNE");
			dsu.addSelect("DECODE(MOB.CODMONOPROV,'P','Da Prolabor','I','Ins.\nmanuale','F','Da File',"
					+ "'A','Da \nProlabor/\nAgg.\nmanuale','M','Ins./\nAgg.\n manuale','C','Da File/\nAgg.\nmanuale') AS PROVENIENZA");
			dsu.addSelect("(LAV.STRCODICEFISCALE || '\n' || LAV.STRCOGNOME  || '\n' ||LAV.STRNOME) AS LAVORATORE ");
			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AM_MOBILITA_ISCR MOB");
		dsu.addFrom("DE_MB_TIPO");
		dsu.addFrom("ma_listespeciali ma");
		dsu.addFrom("mn_listespeciali mn");
		dsu.addFrom("DE_MB_STATO_RICH STATO_RICH");

		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
		} else {
			// END-PARTE-TEMP
			dsu.addFrom("AN_UNITA_AZIENDA");
			dsu.addFrom("AN_AZIENDA");
			dsu.addFrom("DE_COMUNE");
			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		dsu.addWhere("MOB.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO");
		dsu.addWhere("MOB.CDNLAVORATORE = LAV.CDNLAVORATORE");
		dsu.addWhere("DE_MB_TIPO.codmbtipo = ma.codmbtipo");
		dsu.addWhere("ma.codlistespec= mn.cod_listespec (+)");
		dsu.addWhere("MOB.CDNMBSTATORICH = STATO_RICH.CDNMBSTATORICH(+)");

		String wherePerStato = "";

		if (codStatoMob.size() != 0) {
			wherePerStato = "MOB.CDNMBSTATORICH IN (";
			for (int i = 0; i < codStatoMob.size(); i++) {
				if (i == 0)
					wherePerStato += codStatoMob.get(i);
				else
					wherePerStato += ", " + codStatoMob.get(i);
			}
			wherePerStato += ")";

			dsu.addWhere(wherePerStato);
		}

		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
		} else {
			// END-PARTE-TEMP
			dsu.addWhere(
					"(mob.prgazienda = an_unita_azienda.prgazienda(+) AND mob.PRGUNITA = an_unita_azienda.PRGUNITA(+))");
			dsu.addWhere("(mob.prgazienda = an_azienda.prgazienda(+))");
			dsu.addWhere("AN_UNITA_AZIENDA.CODCOM = DE_COMUNE.CODCOM(+)");
			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		if (!codCpi.equals("")) {
			dsu.addFrom("an_lav_storia_inf inf");
			// commentate il 16/05/2005
			// dsu.addFrom("an_lav_storia_inf_coll coll");
			// dsu.addFrom("am_elenco_anagrafico ea");

			dsu.addWhere("inf.cdnlavoratore = LAV.CDNLAVORATORE");
			// commentate il 16/05/2005
			// dsu.addWhere("inf.prgLavStoriaInf = coll.prgLavStoriaInf");
			// dsu.addWhere("coll.codlsttab = 'EA'");
			// dsu.addWhere("coll.strChiaveTabella = ea.prgElencoAnagrafico");

			dsu.addWhere("DECODE(inf.DATFINE,NULL,'S','N')='S'");
			// dsu.addWhere("nvl(inf.datFine,
			// to_date('01/01/2100','dd/mm/yyyy')) =
			// to_date('01/01/2100','dd/mm/yyyy') ");
			dsu.addWhere("inf.codCpiTit = '" + codCpi + "' ");
			dsu.addWhere("inf.codMonoTipoCpi = 'C' ");
		}

		if (tipoRic.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("LAV.STRNOME", nome);
			dsu.addWhereIfFilledStrUpper("LAV.STRCOGNOME", cognome);
			dsu.addWhereIfFilledStrUpper("LAV.STRCODICEFISCALE", cf);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRNOME", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCOGNOME", cognome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCODICEFISCALE", cf, DynamicStatementUtils.DO_LIKE_INIZIA);
		} // else

		// parametri per la ricerca per i nuovi moduli MO
		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
		} else {
			// END-PARTE-TEMP
			String codFiscLav = (String) req.getAttribute("codiceFiscaleLavoratore");
			String prgAzienda = (String) req.getAttribute("prgAzienda");
			String prgUnita = (String) req.getAttribute("prgUnita");
			String codMotMob = "";
			Vector list = new Vector();
			String codListaStr = "";
			if (req.containsAttribute("stampa") || req.containsAttribute("aggiorna_stato")) {
				try {
					codListaStr = StringUtils.getAttributeStrNotNull(req, "CodMotivoMob");
					StringTokenizer st = new StringTokenizer(codListaStr, ",");
					for (; st.hasMoreTokens();) {
						list.add(st.nextToken());
					}
				} catch (Exception e) {
					list = req.getAttributeAsVector("CodMotivoMob");
				}
			} else {
				list = req.getAttributeAsVector("CodMotivoMob");
			}
			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (!list.elementAt(i).equals("")) {
						if (codMotMob.length() > 0) {
							codMotMob = codMotMob + "," + "'" + list.elementAt(i) + "'";
						} else {
							codMotMob += "'" + list.elementAt(i) + "'";
						}
					}
				}
			}
			flgEscludiMotFineMob = StringUtils.getAttributeStrNotNull(req, "FlgEscludiFineMob").trim();
			String codMotScor = (String) req.getAttribute("CodMotivoScor");
			String codProv = (String) req.getAttribute("CodProv");
			String codMobInd = (String) req.getAttribute("mobInd");
			String codInizDa = (String) req.getAttribute("datinizioIndDa");
			String codInizA = (String) req.getAttribute("datinizioIndA");
			String codFineDa = (String) req.getAttribute("datfineIndDa");
			String codFineA = (String) req.getAttribute("datfineIndA");
			String numDelReg = (String) req.getAttribute("NumDelReg");

			// costruisco le clausole di WHERE
			if ((codFiscLav != null) && (!codFiscLav.equals(""))) {
				dsu.addWhereIfFilledStrUpper("LAV.STRCODICEFISCALE", codFiscLav);
			}
			if (((prgAzienda != null) && (!prgAzienda.equals(""))) && ((prgUnita != null) && (!prgUnita.equals("")))) {
				dsu.addWhereIfFilledStrUpper("AN_UNITA_AZIENDA.PRGAZIENDA", prgAzienda);
				dsu.addWhereIfFilledStrUpper("AN_UNITA_AZIENDA.PRGUNITA", prgUnita);
			}

			String sqlMotivoFine = "";
			if ((codMotMob != null) && (!codMotMob.equals(""))) {
				if (flgEscludiMotFineMob.equals("S")) {
					dsu.addWhere("MOB.CODMOTIVOFINE not in (" + codMotMob + ")");
				} else {
					dsu.addWhere("MOB.CODMOTIVOFINE in (" + codMotMob + ")");
				}
			}

			if ((codMotScor != null) && (!codMotScor.equals(""))) {
				dsu.addWhereIfFilledStrUpper("MOB.CODMOTIVODIFF", codMotScor);
			}
			if ((numDelReg != null) && (!numDelReg.equals(""))) {
				dsu.addWhereIfFilledStrUpper("MOB.STRNUMATTO", numDelReg);
			}
			if ((codProv != null) && (!codProv.equals(""))) {
				dsu.addWhereIfFilledStrUpper("MOB.CODMONOPROV", codProv);
			}
			if ((codMobInd != null) && (!codMobInd.equals(""))) {
				dsu.addWhereIfFilledStrUpper("MOB.FLGINDENNITA", codMobInd);
			}
			if ((codInizDa != null) && (!codInizDa.equals(""))) {
				dsu.addWhereIfFilledDateBetween("MOB.DATINIZIOINDENNITA", codInizDa, codInizA);
			}
			if ((codFineDa != null) && (!codFineDa.equals(""))) {
				dsu.addWhereIfFilledDateBetween("MOB.DATFINEINDENNITA", codFineDa, codFineA);
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		// tipo mobilità
		// INIT-PARTE-TEMP
		if (Sottosistema.MO.isOff()) {
			dsu.addWhereIfFilledStr("MOB.CODTIPOMOB", codTipoMob);
		} else {
			// END-PARTE-TEMP
			if ((codTipoMob != null) && (!codTipoMob.equals(""))) {
				dsu.addWhere("MOB.CODTIPOMOB in (" + codTipoMob + ")");
			}
			if (soggettiCM.equals("S")) {
				SessionContainer session = requestContainer.getSessionContainer();
				String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

				String sqlMobInCM = "";
				sqlMobInCM = sqlMobInCM + "MOB.CDNLAVORATORE IN (SELECT DECRYPT(AM_CM_ISCR.CDNLAVORATORE, '"
						+ encryptKey + "') FROM AM_CM_ISCR WHERE DECRYPT(AM_CM_ISCR.CDNLAVORATORE, '" + encryptKey
						+ "') = MOB.CDNLAVORATORE AND ";
				sqlMobInCM = sqlMobInCM + "( (TRUNC(AM_CM_ISCR.DATDATAINIZIO) < TRUNC(MOB.DATINIZIO) AND "
						+ "TRUNC(DECODE(AM_CM_ISCR.DATDATAFINE, NULL, MOB.DATINIZIO, AM_CM_ISCR.DATDATAFINE)) >= TRUNC(MOB.DATINIZIO)) OR ";
				sqlMobInCM = sqlMobInCM + "(TRUNC(AM_CM_ISCR.DATDATAINIZIO) >= TRUNC(MOB.DATINIZIO) AND "
						+ "TRUNC(AM_CM_ISCR.DATDATAINIZIO) <= TRUNC(DECODE(MOB.DATFINE, NULL, AM_CM_ISCR.DATDATAINIZIO, MOB.DATFINE))) )";
				sqlMobInCM = sqlMobInCM + ")";
				dsu.addWhere(sqlMobInCM);
			}
			if (soggettiConMov.equals("S")) {
				String sqlSoggettiMov = "";
				sqlSoggettiMov = sqlSoggettiMov
						+ "MOB.CDNLAVORATORE IN (SELECT MOV.CDNLAVORATORE FROM AM_MOVIMENTO MOV WHERE MOV.CDNLAVORATORE = MOB.CDNLAVORATORE AND MOV.CODSTATOATTO = 'PR' AND MOV.CODTIPOMOV <> 'CES' AND ";
				sqlSoggettiMov = sqlSoggettiMov + "TRUNC(MOV.DATINIZIOMOV) >= TRUNC(MOB.DATINIZIO) AND "
						+ "TRUNC(MOV.DATINIZIOMOV) <= TRUNC(DECODE(MOB.DATFINE, NULL, MOV.DATINIZIOMOV, MOB.DATFINE)) ";
				if (!datInizioMovDa.equals("")) {
					sqlSoggettiMov = sqlSoggettiMov + " AND TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('" + datInizioMovDa
							+ "','DD/MM/YYYY') ";
				}
				if (!datInizioMovA.equals("")) {
					sqlSoggettiMov = sqlSoggettiMov + " AND TRUNC(MOV.DATINIZIOMOV) <= TO_DATE('" + datInizioMovA
							+ "','DD/MM/YYYY') ";
				}
				sqlSoggettiMov = sqlSoggettiMov + ")";
				dsu.addWhere(sqlSoggettiMov);
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		dsu.addWhereIfFilledDateBetween("MOB.DATDOMANDA", datDomandaDa, datDomandaA);
		dsu.addWhereIfFilledDateBetween("MOB.DATINIZIO", datInizioDa, datInizioA);

		if (Sottosistema.MO.isOn()) {
			if (flgMobAperte.equals("S")) {
				dsu.addWhereIfFilledDateBetween("MOB.DATFINE", DateUtils.getNow(), "");
			} else {
				if (flgMobStor.equals("S") && !dataStoricizzata.equals("")) {
					dsu.addWhereIfFilledDateBetween("MOB.DATFINE", "", dataStoricizzata);
				} else {
					dsu.addWhereIfFilledDateBetween("MOB.DATFINE", datFineDa, datFineA);
				}
			}
		} else {
			dsu.addWhereIfFilledDateBetween("MOB.DATFINE", datFineDa, datFineA);
		}

		if (flgNonImprenditore.length() > 0) {
			dsu.addWhereIfFilledStrUpper("MOB.FLGNONIMPRENDITORE", flgNonImprenditore);
		}
		if (flgCasoDubbio.length() > 0) {
			dsu.addWhereIfFilledStrUpper("MOB.FLGCASODUBBIO", flgCasoDubbio);
		}
		dsu.addWhereIfFilledDateBetween("MOB.DATMAXDIFF", datMaxDiffDa, datMaxDiffA);
		dsu.addWhereIfFilledDateBetween("MOB.DATCRT", datCRTDa, datCRTA);

		dsu.addOrder("COGNOME");
		dsu.addOrder("NOME");
		dsu.addOrder("MOB.DATINIZIO");

		String query = dsu.getStatement();
		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}
}// class MobilitaRicerca
