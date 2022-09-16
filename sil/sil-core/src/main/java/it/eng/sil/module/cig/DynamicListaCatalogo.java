package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicListaCatalogo implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListaCatalogo.class.getName());

	public DynamicListaCatalogo() {
	}

	private static final String SELECT_SQL_BASE = "select prop.numidproposta, sede.numrecid, "
			+ "        sede.strsede, sede.strambitoprovinciale, " + "        prop.strcodiceproposta "
			+ "from de_catalogo_proposta prop "
			+ "inner join de_catalogo_sede sede on (sede.numidproposta = prop.numidproposta ) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String strcodiceproposta = (String) req.getAttribute("strcodiceproposta");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (strcodiceproposta != null && !strcodiceproposta.equals("")) {
			buf.append(" where strcodiceproposta like upper('%" + strcodiceproposta + "%')");
		}

		buf.append(" order by prop.numidproposta asc, sede.strambitoprovinciale, sede.strindirizzo asc ");
		query_totale.append(buf);

		return query_totale.toString();

	}
}
