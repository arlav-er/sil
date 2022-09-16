package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaLavDichConservazione implements IDynamicStatementProvider {
	public DynRicercaLavDichConservazione() {
	}

	private String PARTEWHERE_SELECT_SQL_NO_DICH = " WHERE MOV.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND INF.CDNLAVORATORE = LAV.CDNLAVORATORE " + " AND DECODE(INF.DATFINE, NULL, 'S', 'N') = 'S' "
			+ " AND MOV.CODSTATOATTO = 'PR' " + " AND MOV.CODTIPOMOV <> 'CES' "
			+ " AND NVL(MOV.DATFINEMOVEFFETTIVA, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) <>  TO_DATE ('01/01/1900', 'DD/MM/YYYY') "
			+ " AND LAV.CDNLAVORATORE NOT IN (SELECT ST_DICH_ATT.CDNLAVORATORE "
			+ " FROM DE_OGGETTO_MODELLO, ST_MODELLO_STAMPA, ST_DICH_ATT "
			+ " WHERE DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = ST_MODELLO_STAMPA.CODOGGETTOMODELLO "
			+ " AND ST_MODELLO_STAMPA.PRGMODELLOSTAMPA = ST_DICH_ATT.PRGMODELLOSTAMPA "
			+ " AND DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = 'DIC_CONS') "
			+ " AND TRUNC(SYSDATE) > TRUNC(MOV.DATFINEMOVEFFETTIVA) + 30 ";

	private String PARTEWHERE_SELECT_SQL_CON_DICH_ENTRO_30GG = " WHERE MOV.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND INF.CDNLAVORATORE = LAV.CDNLAVORATORE " + " AND DECODE(INF.DATFINE, NULL, 'S', 'N') = 'S' "
			+ " AND DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = ST_MODELLO_STAMPA.CODOGGETTOMODELLO "
			+ " AND ST_MODELLO_STAMPA.PRGMODELLOSTAMPA = ST_DICH_ATT.PRGMODELLOSTAMPA "
			+ " AND ST_DICH_ATT.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = 'DIC_CONS' " + " AND MOV.CODSTATOATTO = 'PR' "
			+ " AND MOV.CODTIPOMOV <> 'CES' "
			+ " AND NVL(MOV.DATFINEMOVEFFETTIVA, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) <>  TO_DATE ('01/01/1900', 'DD/MM/YYYY') "
			+ " AND TRUNC(ST_DICH_ATT.DATINIZIO) > TRUNC(MOV.DATFINEMOVEFFETTIVA) + 30 ";

	private String PARTEWHERE_SELECT_SQL_CON_DICH_SENZA_DATFINE = " WHERE MOV.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND INF.CDNLAVORATORE = LAV.CDNLAVORATORE " + " AND DECODE(INF.DATFINE, NULL, 'S', 'N') = 'S' "
			+ " AND DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = ST_MODELLO_STAMPA.CODOGGETTOMODELLO "
			+ " AND ST_MODELLO_STAMPA.PRGMODELLOSTAMPA = ST_DICH_ATT.PRGMODELLOSTAMPA "
			+ " AND ST_DICH_ATT.CDNLAVORATORE = LAV.CDNLAVORATORE "
			+ " AND DE_OGGETTO_MODELLO.CODOGGETTOMODELLO = 'DIC_CONS' " + " AND MOV.CODSTATOATTO = 'PR' "
			+ " AND MOV.CODTIPOMOV <> 'CES' "
			+ " AND NVL(MOV.DATFINEMOVEFFETTIVA, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) <>  TO_DATE ('01/01/1900', 'DD/MM/YYYY') "
			+ " AND (st_dich_att.datfine IS NULL " + " OR TRUNC (st_dich_att.datfine) <> "
			+ " (SELECT MAX (TRUNC (mov1.datinizioavv))" + "    FROM am_movimento mov1 "
			+ "   WHERE mov1.codstatoatto = 'PR' " + "     AND mov1.cdnlavoratore = mov.cdnlavoratore "
			+ "     AND mov1.codtipomov <> 'CES' " + "     AND NVL (mov1.datfinemoveffettiva, "
			+ "              TO_DATE ('01/01/1900', 'DD/MM/YYYY') "
			+ "             ) <> TO_DATE ('01/01/1900', 'DD/MM/YYYY'))) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String dataInizioRichDa = StringUtils.getAttributeStrNotNull(req, "dataInizioRicerca");
		String dataInizioRichA = StringUtils.getAttributeStrNotNull(req, "dataInizioRicercaA");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CodCPI");
		Vector vTipoRicerca = new Vector();
		String codListaStr = "";
		StringTokenizer st = null;
		String descTipologia = "";

		// filtro su tipologia ricerca (A mancata dichiarazione, B data fine non valida
		if (req.containsAttribute("stampa")) {
			try {
				codListaStr = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
				st = new StringTokenizer(codListaStr, ",");
				for (; st.hasMoreTokens();) {
					vTipoRicerca.add(st.nextToken().trim());
				}
			} catch (Exception e) {
				vTipoRicerca = req.getAttributeAsVector("tipoRicerca");
			}
		} else {
			vTipoRicerca = req.getAttributeAsVector("tipoRicerca");
		}

		if (vTipoRicerca.size() > 0) {
			for (int i = 0; i < vTipoRicerca.size(); i++) {
				if (vTipoRicerca.elementAt(i).equals("A")) {
					if (descTipologia.equals("")) {
						descTipologia = "Mancata dichiarazione";
					} else {
						descTipologia = descTipologia + "/Mancata dichiarazione";
					}
				} else {
					if (vTipoRicerca.elementAt(i).equals("B")) {
						if (descTipologia.equals("")) {
							descTipologia = "Data fine non valida";
						} else {
							descTipologia = descTipologia + "/Data fine non valida";
						}
					}
				}
			}
		} else {
			descTipologia = "Mancata dichiarazione/Data fine non valida";
		}

		String SELECT_SQL_NO_DICH = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE, '" + descTipologia + "' tipologia, "
				+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, " + " LAV.STRNOME STRNOME,"
				+ " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, "
				+ " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATCESSAZIONE "
				+ " FROM AN_LAVORATORE LAV, AM_MOVIMENTO MOV, AN_LAV_STORIA_INF INF ";

		String SELECT_SQL_CON_DICH_ENTRO_30GG = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE, '" + descTipologia
				+ "' tipologia, " + " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, "
				+ " LAV.STRNOME STRNOME," + " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, "
				+ " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATCESSAZIONE "
				+ " FROM AN_LAVORATORE LAV, AM_MOVIMENTO MOV, AN_LAV_STORIA_INF INF, "
				+ " DE_OGGETTO_MODELLO, ST_MODELLO_STAMPA, ST_DICH_ATT ";

		String SELECT_SQL_CON_DICH_SENZA_DATFINE = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE, '" + descTipologia
				+ "' tipologia, " + " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, "
				+ " LAV.STRNOME STRNOME," + " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, "
				+ " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATCESSAZIONE "
				+ " FROM AN_LAVORATORE LAV, AN_LAV_STORIA_INF INF, AM_MOVIMENTO MOV, "
				+ " DE_OGGETTO_MODELLO, ST_MODELLO_STAMPA, ST_DICH_ATT ";

		if (codCpi.equals("")) {
			SELECT_SQL_NO_DICH = SELECT_SQL_NO_DICH + ", TS_GENERALE, DE_CPI ";
			SELECT_SQL_CON_DICH_ENTRO_30GG = SELECT_SQL_CON_DICH_ENTRO_30GG + ", TS_GENERALE, DE_CPI ";
			SELECT_SQL_CON_DICH_SENZA_DATFINE = SELECT_SQL_CON_DICH_SENZA_DATFINE + ", TS_GENERALE, DE_CPI ";
			PARTEWHERE_SELECT_SQL_NO_DICH = PARTEWHERE_SELECT_SQL_NO_DICH
					+ " AND INF.CODCPITIT = DE_CPI.CODCPI AND DE_CPI.CODPROVINCIA = TS_GENERALE.CODPROVINCIASIL AND INF.CODMONOTIPOCPI = 'C' ";
			PARTEWHERE_SELECT_SQL_CON_DICH_ENTRO_30GG = PARTEWHERE_SELECT_SQL_CON_DICH_ENTRO_30GG
					+ " AND INF.CODCPITIT = DE_CPI.CODCPI AND DE_CPI.CODPROVINCIA = TS_GENERALE.CODPROVINCIASIL AND INF.CODMONOTIPOCPI = 'C' ";
			PARTEWHERE_SELECT_SQL_CON_DICH_SENZA_DATFINE = PARTEWHERE_SELECT_SQL_CON_DICH_SENZA_DATFINE
					+ " AND INF.CODCPITIT = DE_CPI.CODCPI AND DE_CPI.CODPROVINCIA = TS_GENERALE.CODPROVINCIASIL AND INF.CODMONOTIPOCPI = 'C' ";
		}

		String SELECT_SQL_NO_DICH_FINAL = SELECT_SQL_NO_DICH + PARTEWHERE_SELECT_SQL_NO_DICH;
		String SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL = SELECT_SQL_CON_DICH_ENTRO_30GG
				+ PARTEWHERE_SELECT_SQL_CON_DICH_ENTRO_30GG;
		String SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL = SELECT_SQL_CON_DICH_SENZA_DATFINE
				+ PARTEWHERE_SELECT_SQL_CON_DICH_SENZA_DATFINE;

		// filtro su data inizio richiesta da
		if (!dataInizioRichDa.equals("")) {
			SELECT_SQL_NO_DICH_FINAL = SELECT_SQL_NO_DICH_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) >= TO_DATE('" + dataInizioRichDa
					+ "', 'DD/MM/YYYY') ";
			SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL = SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) >= TO_DATE('" + dataInizioRichDa
					+ "', 'DD/MM/YYYY') ";
			SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL = SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) >= TO_DATE('" + dataInizioRichDa
					+ "', 'DD/MM/YYYY') ";
		}

		// filtro su data inizio richiesta a
		if (!dataInizioRichA.equals("")) {
			SELECT_SQL_NO_DICH_FINAL = SELECT_SQL_NO_DICH_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) <= TO_DATE('" + dataInizioRichA
					+ "', 'DD/MM/YYYY') ";
			SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL = SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) <= TO_DATE('" + dataInizioRichA
					+ "', 'DD/MM/YYYY') ";
			SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL = SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL
					+ " AND TRUNC(NVL(MOV.DATINIZIOAVV, MOV.DATINIZIOMOV)) <= TO_DATE('" + dataInizioRichA
					+ "', 'DD/MM/YYYY') ";
		}

		// filtro su cpi
		if (!codCpi.equals("")) {
			SELECT_SQL_NO_DICH_FINAL = SELECT_SQL_NO_DICH_FINAL + " AND INF.CODCPITIT = '" + codCpi
					+ "' AND INF.CODMONOTIPOCPI = 'C' ";
			SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL = SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL + " AND INF.CODCPITIT = '"
					+ codCpi + "' AND INF.CODMONOTIPOCPI = 'C' ";
			SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL = SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL + " AND INF.CODCPITIT = '"
					+ codCpi + "' AND INF.CODMONOTIPOCPI = 'C' ";
		}

		// esecuzione query
		StringBuffer query_totale = null;
		if (vTipoRicerca.size() > 0) {
			for (int i = 0; i < vTipoRicerca.size(); i++) {
				if (vTipoRicerca.elementAt(i).equals("A")) {
					if (query_totale == null) {
						query_totale = new StringBuffer(SELECT_SQL_NO_DICH_FINAL);
						query_totale.append(" UNION ");
						query_totale.append(SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL);
					} else {
						query_totale.append(" UNION ");
						query_totale.append(SELECT_SQL_NO_DICH_FINAL);
						query_totale.append(" UNION ");
						query_totale.append(SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL);
					}
				} else {
					if (vTipoRicerca.elementAt(i).equals("B")) {
						if (query_totale == null) {
							query_totale = new StringBuffer(SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL);
						} else {
							query_totale.append(" UNION ");
							query_totale.append(SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL);
						}
					}
				}
			}
		} else {
			query_totale = new StringBuffer(SELECT_SQL_NO_DICH_FINAL);
			query_totale.append(" UNION ");
			query_totale.append(SELECT_SQL_CON_DICH_ENTRO_30GG_FINAL);
			query_totale.append(" UNION ");
			query_totale.append(SELECT_SQL_CON_DICH_SENZA_DATFINE_FINAL);
		}

		String query_ordinata_start = "SELECT CDNLAVORATORE, tipologia, STRCODICEFISCALE, "
				+ "STRCOGNOME, STRNOME, DATNASC, " + " DATCESSAZIONE ";
		StringBuffer query_totale_ordinata = new StringBuffer(query_ordinata_start);
		query_totale_ordinata.append(" FROM (");
		query_totale_ordinata.append(query_totale);
		query_totale_ordinata.append(") ");
		query_totale_ordinata.append(" ORDER BY TO_DATE(DATCESSAZIONE,'DD/MM/YYYY') DESC");

		return query_totale_ordinata.toString();
	}

}