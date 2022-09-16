package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/**
 * 
 */
public class DynStatementRicercaRichAdesioniCM implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynStatementRicercaRichAdesioniCM.class.getName());

	private String className = this.getClass().getName();

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		String dataChiamata = (String) req.getAttribute("datChiamata");
		Object cdnLavoratore = req.getAttribute("cdnLavoratore");

		String SELECT_SQL_OPEN = "select " + "  prgrosa,	" + " 	prgrichiestaaz, " + "	prgtipoincrocio, "
				+ "  numrichiesta, " + "  numanno, " + "  prgalternativa, " + "	richiesta, " + "	ente, " + "	posti, "
				+ "  descrInc, " + "	rapp," + "  qual, " + "	dataChiam," + "  dataPubb, " + "	cpi, "
				+ "	datchiamatacm, " + "  CODMONOTIPOGRAD, " + "  checkAdesione, " + "  checkMovimento " + " from ( ";
		// CM_ISCR_ART1
		String SELECT_SQL_BASE = " select " + "	raz.prgrichiestaaz, inc.prgtipoincrocio, r.prgrosa, "
				+ "	raz.numrichiesta, raz.numanno, a.prgalternativa,   "
				+ "	raz.numrichiesta ||'/'|| raz.numanno ||'/'|| a.prgalternativa  as richiesta, "
				+ "	az.strragionesociale as ente,   " + " 	case   	   "
				+ "		when inc.prgtipoincrocio = 10 			" + "			then raz.numposticm 	   "
				+ "		when inc.prgtipoincrocio = 11 			" + "			then raz.numposticm 	   "
				+ "		else 0  " + "	end  as posti, "
				+ "	PG_IDO.WebStrContrattiProfilo(raz.prgrichiestaaz, a.prgalternativa) as rapp, "
				+ "  PG_IDO.WebStrMansioniProfilo(raz.prgrichiestaaz, a.prgalternativa) as qual, "
				+ "  0 as checkAdesione, " + "  0 as checkMovimento,   "
				+ "	TO_CHAR(raz.datchiamatacm,'DD/MM/YYYY') as dataChiam, "
				+ "  TO_CHAR(raz.datpubblicazione,'DD/MM/YYYY') as dataPubb, "
				+ "	(select de_cpi.strdescrizione from de_cpi where de_cpi.codcpi = raz.codcpi) as cpi, "
				+ "	raz.datchiamatacm, " + "  raz.CODMONOTIPOGRAD, " + "  dti.strdescrizione as descrInc    "
				+ "	from  do_richiesta_az raz   " + " inner join de_cpi cpiraz on raz.codcpi = cpiraz.codcpi  "
				+ " inner join de_comune comuneraz on cpiraz.codcom = comuneraz.codcom  "
				+ "	inner join do_incrocio inc on inc.prgrichiestaaz =raz.prgrichiestaaz "
				+ "	inner join do_rosa r on r.prgincrocio = inc.prgincrocio   "
				+ "	inner join an_azienda az on az.prgazienda = raz.prgazienda "
				+ "	inner join do_alternativa a on a.prgrichiestaaz = raz.prgrichiestaaz and a.prgalternativa = inc.prgalternativa   "
				+ "	inner join de_tipo_incrocio dti on dti.prgtipoincrocio = inc.prgtipoincrocio "
				+ "  inner join do_evasione de on de.prgrichiestaaz = raz.prgrichiestaaz " + "	where 1=1 "
				+ "  and a.prgalternativa = 1 "
				+ "  and (raz.datpubblicazione is null OR ((to_date(sysdate, 'dd/mm/yy') - to_date(add_months(raz.datpubblicazione, 6), 'dd/mm/yy')) < 0)) "
				+ "  and de.CDNSTATORICH != 4 " + "  and de.CDNSTATORICH != 5 " + "  and r.prgTipoRosa != 3 "
				+ "	and inc.prgtipoincrocio in  "
				+ "	(SELECT ti.prgTipoIncrocio AS prgTipoIncrocio FROM De_Tipo_Incrocio ti	WHERE ti.prgtipoincrocio IN (10,11)"
				+ "   MINUS " + "   (select incr.prgtipoincrocio " + "   from do_incrocio incr "
				+ "   inner join de_tipo_incrocio tinc on tinc.prgtipoincrocio = incr.prgtipoincrocio "
				+ "   inner join do_rosa rosa on rosa.prgincrocio = incr.prgincrocio "
				+ "   where incr.prgrichiestaaz = raz.prgrichiestaaz " + "   and rosa.prgrosafiglia is null "
				+ "   and rosa.prgtiporosa = 3) )  ";

		String SELECT_SQL_CLOSE = " ) " + "	where posti is not null " + " and posti > 0 "
				+ " and prgtipoincrocio = DECODE(CODMONOTIPOGRAD, 'D', 10, 'A', 11)"
				+ "	ORDER BY datchiamatacm DESC, ente, numanno DESC, numrichiesta, prgalternativa, prgtipoincrocio ";

		StringBuffer query_open = new StringBuffer(SELECT_SQL_OPEN);
		StringBuffer query_close = new StringBuffer(SELECT_SQL_CLOSE);

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (buf.length() == 0) {
			buf.append(" ");
		}

		buf.append(" AND raz.numstorico=0");

		buf.append(" AND r.prgrosa NOT IN (select t.prgrosa from do_nominativo t where t.cdnlavoratore = "
				+ cdnLavoratore + " ) ");

		// TODO
		String keyCifratura = System.getProperty("_ENCRYPTER_KEY_");
		buf.append(" and (exists  ( select 1 from am_cm_iscr I where ");
		buf.append(" DECRYPT(I.CDNLAVORATORE, '" + keyCifratura + "')  =  " + cdnLavoratore);
		buf.append(" AND I.CODSTATOATTO = 'PR' AND trunc(nvl(I.DATDATAFINE, sysdate)) >= trunc(sysdate)");
		buf.append(" AND I.CODPROVINCIA = comuneraz.CODPROVINCIA");
		buf.append(") OR exists ");
		buf.append(" ( select 1 from CM_ISCR_ART1 art1 ");
		buf.append(" inner join am_documento_coll dc on to_number(dc.strchiavetabella) = art1.prgiscrart1 ");
		buf.append(" inner join am_documento ad on ad.prgdocumento = dc.prgdocumento	");
		buf.append(" where trunc(nvl(art1.datfine, sysdate)) >= trunc(sysdate) ");
		buf.append(" AND ad.codtipodocumento = 'ILS' ");
		buf.append(" AND ad.CODSTATOATTO = 'PR'	");
		buf.append(" AND art1.CDNLAVORATORE =  " + cdnLavoratore);
		buf.append(" AND art1.CODPROVINCIA = comuneraz.CODPROVINCIA ) ) ");

		if ((dataChiamata != null) && (!dataChiamata.equals(""))) {
			buf.append(" AND raz.datchiamatacm = TO_DATE('" + dataChiamata + "', 'DD/MM/YYYY') ");
		}

		// buf.append(" order by az.strRagioneSociale, raz.numanno desc,
		// raz.numrichiesta desc");
		buf.append(" ORDER BY datchiamatacm DESC, strragionesociale");
		query_totale.append(buf.toString());

		query_open.append(query_totale);
		query_open.append(query_close);
		_logger.debug("Query ricerca: " + query_open.toString());

		return query_open.toString();

	}

}