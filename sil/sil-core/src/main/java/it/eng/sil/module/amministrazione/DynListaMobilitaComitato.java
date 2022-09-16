package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynListaMobilitaComitato implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select lav.CDNLAVORATORE CDNLAVORATORE, lav.STRCOGNOME, lav.STRNOME, "
			+ " to_char(lav.DATNASC,'dd/mm/yyyy') datnasc, "
			+ " com.strdenominazione comune, lav.strindirizzores indirizzo, mans.strdescrizione mansione, "
			+ " to_char(mob.DATINIZIO,'dd/mm/yyyy') datInizio, tipo.STRDESCRIZIONE tipoMob, "
			+ " to_char(mob.DATFINE,'dd/mm/yyyy') datFine, azi.STRRAGIONESOCIALE, destatoocc.strdescrizione descStatoOccupaz "
			+ " from am_mobilita_iscr mob "
			+ " inner join an_lavoratore lav on (lav.CDNLAVORATORE = mob.CDNLAVORATORE) "
			+ " inner join de_comune com on (com.CODCOM = lav.CODCOMRES) "
			+ " inner join de_mb_tipo tipo on (tipo.CODMBTIPO = mob.CODTIPOMOB) "
			+ " left join an_azienda azi on (azi.PRGAZIENDA = mob.PRGAZIENDA) "
			+ " left join de_mansione mans on (mans.codmansione = mob.codmansione) "
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) "
			+ " left join am_stato_occupaz occupaz on (lav.cdnLavoratore=occupaz.cdnLavoratore and occupaz.datFine is null) "
			+ " left join de_stato_occupaz destatoocc on (occupaz.codstatooccupaz = destatoocc.codstatooccupaz) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String datRiunone = StringUtils.getAttributeStrNotNull(req, "dataRiunone");
		// String dataRiunoneA = StringUtils.getAttributeStrNotNull(req, "dataRiunoneA");
		Vector vTipoLista = new Vector();
		String codEsito = StringUtils.getAttributeStrNotNull(req, "codEsito");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CodCPI");
		String codTipoLista = "";
		String codListaStr = "";
		StringTokenizer st = null;

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

		// flitro per le mobilità aperte ad oggi
		buf.append(" and trunc(nvl(mob.datFine, sysdate)) >= sysdate ");

		if (!datRiunone.equals("")) {
			buf.append(" and trunc(mob.datcrt) <= to_date('" + datRiunone + "', 'dd/mm/yyyy') ");
		}

		/*
		 * Commentato il giorno 08/01/2010 perché nella ricerca non compare più
		 * 
		 * if (!dataRiunoneA.equals("")) { buf.append(" and trunc(mob.datdomanda) <= to_date('" + dataRiunoneA +
		 * "', 'dd/mm/yyyy') "); }
		 */

		if (req.containsAttribute("stampa")) {
			try {
				codListaStr = StringUtils.getAttributeStrNotNull(req, "CodTipoLista");
				st = new StringTokenizer(codListaStr, ",");
				for (; st.hasMoreTokens();) {
					vTipoLista.add(st.nextToken().trim());
				}
			} catch (Exception e) {
				vTipoLista = req.getAttributeAsVector("CodTipoLista");
			}
		} else {
			vTipoLista = req.getAttributeAsVector("CodTipoLista");
		}

		if (vTipoLista.size() > 0) {
			for (int i = 0; i < vTipoLista.size(); i++) {
				if (!vTipoLista.elementAt(i).equals("")) {
					if (codTipoLista.length() > 0) {
						codTipoLista = codTipoLista + "," + "'" + vTipoLista.elementAt(i) + "'";
					} else {
						codTipoLista += "('" + vTipoLista.elementAt(i) + "'";
					}
				}
			}
			codTipoLista = codTipoLista + ")";
		}

		if (!codTipoLista.equals("")) {
			buf.append(" and mob.codtipoMob in " + codTipoLista + " ");
		}

		if (!codEsito.equals("")) {
			buf.append(" and mob.cdnmbstatorich = to_number(" + codEsito + ") ");
		}

		buf.append(" order by lav.STRCOGNOME, lav.STRNOME ");
		query_totale.append(buf.toString());

		return query_totale.toString();
	}
}
