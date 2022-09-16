package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynamicListaCorsiCig implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select decode(c_or.prgcorsoci,null,prop.strcodiceproposta,c_or.codRifPA) as idCorso,"
			+ " decode(c_or.prgcorsoci,null,prop.strdescrizionearea||'<br>'||prop.strtitoloqualifica,c_or.strDescrizioneRifPA) as descrizione,"
			+ "	decode(c_or.prgcorsoci,null,prop.strragionesocialeente,az_or.strRagioneSociale) as ente,"
			+ " decode(c_or.prgcorsoci,null,sede.strnominativo,decode(c_or.strReferenteCognome||' '||c_or.strReferenteNome||' '||c_or.strReferenteTel,'  ',null,c_or.strReferenteCognome||' '||c_or.strReferenteNome||' '||c_or.strReferenteTel)) as referente, "
			+ "	decode(c_or.prgcorsoci,null,'CA','OR') as tipo,"
			+ "	to_char(c.DATINIZIOPREV,'dd/mm/yyyy')||'<br>'||to_char(c.DATFINEPREV,'dd/mm/yyyy') as periodoPrevisto,"
			+ "	to_char(c.DATINIZIO,'dd/mm/yyyy')||'<br>'||to_char(c.DATFINE,'dd/mm/yyyy') as periodoEffettivo,"
			+ "	decode(c_or.prgcorsoci, null,'1','0') as orienter,"
			+ "	decode(c_or.prgcorsoci, null,c_cat.prgcorsoci,c_or.prgcorsoci) as prgcorso,"
			+ " to_char(isc.DATINIZIO,'dd/mm/yyyy')||'<br>'||to_char(isc.DATFINE,'dd/mm/yyyy')||'<br>'|| TIPO_ISCR.STRDESCRIZIONE ||'<br>'|| "
			+ " statoIscr.strdescrizione ||'<br>'|| acc.CODACCORDO as CigCollegata, "
			+ " decode(c.dtmcancellazione,null,'','SI') as cancellato, " + " c_cat.prgcorsoci, "
			+ " to_char(c.dtmcancellazione,'dd/mm/yyyy') as dataCancellazione, "
			+ " decode(c.dtmcancellazione,null,'1','0') as isDeleted, "
			+ " decode(c_or.prgcorsoci,null,decode(c.dtmcancellazione,null,'1','0') ,'0') as isCatalogo, "
			+ " decode(c_or.prgcorsoci,null,'0',decode(c.dtmcancellazione,null,'1','0') ) as isOrienter "
			+ "	from ci_corso c " + "	inner join am_altra_iscr isc on (isc.PRGALTRAISCR = c.PRGALTRAISCR)"
			+ "	inner join de_tipo_iscr tipo_iscr on (TIPO_ISCR.CODTIPOISCR = ISC.CODTIPOISCR )"
			+ " left join ci_corso_orienter c_or on ( c.PRGCORSOCI = c_or.PRGCORSOCI )"
			+ " left join ci_corso_catalogo c_cat on ( c.PRGCORSOCI = c_cat.PRGCORSOCI )"
			+ " left join de_catalogo_proposta prop on (prop.numidproposta = c_cat.NUMIDPROPOSTA)"
			+ " left join de_catalogo_sede sede on (sede.NUMRECID = c_cat.NUMRECID)"
			+ " left join an_azienda az_or on (c_or.prgazienda = az_or.prgazienda)"
			+ " left join de_stato_altra_iscr statoIscr on (statoIscr.codstato = isc.codstato)"
			+ " left join ci_accordo acc on (acc.prgaccordo = isc.prgaccordo) " + " where 1=1 ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!cdnLavoratore.equals("")) {
			buf.append(" and isc.CDNLAVORATORE ='" + cdnLavoratore + "' ");
		}

		buf.append(" order by prgcorsoci desc ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
