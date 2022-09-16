package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class MobilitaRicercaApprovazione implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MobilitaRicercaApprovazione.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT LAV.STRCOGNOME COGNOME," + " LAV.STRNOME NOME, "
			+ " LAV.STRCODICEFISCALE CF," + " MOB.PRGMOBILITAISCR, MOB.CDNLAVORATORE, AN_AZIENDA.STRRAGIONESOCIALE, "
			+ " AN_UNITA_AZIENDA.STRINDIRIZZO, DE_COMUNE.STRDENOMINAZIONE AS COMUNE, "
			+ " (LAV.STRCODICEFISCALE || '\n' || LAV.STRCOGNOME  || '\n' ||LAV.STRNOME) AS LAVORATORE, "
			+ " to_char(MOB.DATINIZIO,'DD/MM/YYYY') DATAINIZIO, to_char(MOB.DATFINE,'DD/MM/YYYY') DATAFINE, "
			+ " (to_char(MOB.DATINIZIO,'DD/MM/YYYY') || '\n' || to_char(MOB.DATFINE  ,'DD/MM/YYYY')) as DATES, "
			+ " DE_MB_TIPO.STRDESCRIZIONE AS STRDESCRIZIONEMOB, DE_MB_STATO_RICH.STRDESCRIZIONE stato "
			+ " FROM AM_MOBILITA_ISCR MOB, AN_LAVORATORE LAV, DE_MB_TIPO, DE_MB_STATO_RICH, AN_AZIENDA, AN_UNITA_AZIENDA, DE_COMUNE, an_lav_storia_inf inf "
			+ " WHERE MOB.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO AND MOB.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND MOB.CDNMBSTATORICH = DE_MB_STATO_RICH.CDNMBSTATORICH(+) "
			+ " AND MOB.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA(+) AND MOB.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA(+) "
			+ " AND MOB.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA(+) AND AN_UNITA_AZIENDA.CODCOM = DE_COMUNE.CODCOM(+) "
			+ " AND inf.cdnlavoratore = LAV.CDNLAVORATORE AND INF.DATFINE IS NULL "
			+ " AND DATCRT IS NULL AND STRNUMATTO IS NULL ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String codTipoMob = "";
		String codStatoMobStr = "";
		Vector codStatoMob = new Vector();
		Vector list = new Vector();
		int sizeTipoLista = 0;
		int sizeCodStato = 0;
		if (req.containsAttribute("validaApprov")) {
			String statiMob = StringUtils.getAttributeStrNotNull(req, "CodStatoMob");
			String listTipi = StringUtils.getAttributeStrNotNull(req, "CodTipoLista");
			codStatoMob = StringUtils.split(statiMob, ",");
			list = StringUtils.split(listTipi, ",");
			sizeTipoLista = list.size();
			sizeCodStato = codStatoMob.size();
		} else {
			codStatoMob = req.getAttributeAsVector("CodStatoMob");
			list = req.getAttributeAsVector("CodTipoLista");
			sizeTipoLista = list.size();
			sizeCodStato = codStatoMob.size();
		}

		if (sizeTipoLista > 0) {
			for (int i = 0; i < sizeTipoLista; i++) {
				if (!list.elementAt(i).equals("")) {
					if (codTipoMob.length() > 0) {
						codTipoMob = codTipoMob + "," + "'" + list.elementAt(i) + "'";
					} else {
						codTipoMob += "'" + list.elementAt(i) + "'";
					}
				}
			}
		}

		if (sizeCodStato > 0) {
			for (int i = 0; i < sizeCodStato; i++) {
				if (!codStatoMob.elementAt(i).equals("")) {
					if (codStatoMobStr.length() > 0) {
						codStatoMobStr = codStatoMobStr + "," + "'" + codStatoMob.elementAt(i) + "'";
					} else {
						codStatoMobStr += "'" + codStatoMob.elementAt(i) + "'";
					}
				}
			}
		}

		String datDomandaDa = (String) req.getAttribute("DATDOMANDADA");
		String datDomandaA = (String) req.getAttribute("DATDOMANDAA");
		String datInizioDa = (String) req.getAttribute("DATINIZIODA");
		String datInizioA = (String) req.getAttribute("DATINIZIOA");
		String datFineDa = (String) req.getAttribute("DATFINEDA");
		String datFineA = (String) req.getAttribute("DATFINEA");

		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((datDomandaDa != null) && (!datDomandaDa.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATDOMANDA) >= TO_DATE('" + datDomandaDa + "', 'dd/mm/yyyy') ");
		}

		if ((datDomandaA != null) && (!datDomandaA.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATDOMANDA) <= TO_DATE('" + datDomandaA + "', 'dd/mm/yyyy') ");
		}

		if ((datInizioDa != null) && (!datInizioDa.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATINIZIO) >= TO_DATE('" + datInizioDa + "', 'dd/mm/yyyy') ");
		}

		if ((datInizioA != null) && (!datInizioA.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATINIZIO) <= TO_DATE('" + datInizioA + "', 'dd/mm/yyyy') ");
		}

		if ((datFineDa != null) && (!datFineDa.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATFINE) >= TO_DATE('" + datFineDa + "', 'dd/mm/yyyy') ");
		}

		if ((datFineA != null) && (!datFineA.equals(""))) {
			buf.append(" AND TRUNC(MOB.DATFINE) <= TO_DATE('" + datFineA + "', 'dd/mm/yyyy') ");
		}

		if (!codCpi.equals("")) {
			buf.append(" and inf.codcpitit = '" + codCpi + "' ");
			buf.append(" and inf.codmonotipocpi = 'C' ");
		}

		if ((codTipoMob != null) && (!codTipoMob.equals(""))) {
			buf.append(" AND MOB.CODTIPOMOB in (" + codTipoMob + ") ");
		} else {
			String subQueryTipoMb = "(SELECT CODMBTIPO FROM DE_MB_TIPO TI "
					+ " WHERE CODMBTIPO IN (SELECT TC.CODICE FROM TS_CONFIG_CODIFICA TC "
					+ "WHERE TC.NOMETABELLA='DE_MB_TIPO' AND TC.CODTIPOCONFIG = '_MBTPLST' AND TC.CONFIGURAZIONE = NVL("
					+ "(SELECT TL.NUM FROM TS_CONFIG_LOC TL WHERE TL.STRCODRIF = (SELECT TS_GENERALE.CODPROVINCIASIL FROM TS_GENERALE) "
					+ " AND TL.CODTIPOCONFIG = '_MBTPLST'),0)) )";
			buf.append(" AND MOB.CODTIPOMOB in " + subQueryTipoMb);
		}

		if ((codStatoMobStr != null) && (!codStatoMobStr.equals(""))) {
			buf.append(" AND MOB.CDNMBSTATORICH in (" + codStatoMobStr + ") ");
		}
		buf.append(" order by COGNOME, NOME, MOB.DATINIZIO ");
		query_totale.append(buf.toString());

		return query_totale.toString();
	}
}
