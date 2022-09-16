package it.eng.sil.module.condizionalita.rdc;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class RicercaDomandeCondizionalita implements IDynamicStatementProvider {

	private static final String QUERY_AM_RDC = "SELECT PRGRDC as chiave, STRPROTOCOLLOINPS as protocolloInps, "
			+ " trunc(DATDOMANDA) as dataDomanda, " + " TO_CHAR(DATDOMANDA, 'dd/mm/yyyy') as strDataDomanda, "
			+ " CODSTATOINPS as statoDomanda, " + " CODSTATOINPS as codiceStatoDomanda" + " FROM AM_RDC "
			+ " WHERE upper(STRCODICEFISCALE) =trim(upper(' $CFLAV$ '))" + " ORDER BY DATDOMANDA DESC";

	private static final String QUERY_AM_DID_INPS = "SELECT PRGDIDINPS as chiave, STRPROTOCOLLOINPS as protocolloInps, "
			+ " trunc(DATDICHIARAZIONE) as dataDomanda, "
			+ " TO_CHAR(DATDICHIARAZIONE, 'dd/mm/yyyy') as strDataDomanda, "
			+ " CODMONOTIPOOPERAZIONE as codiceStatoDomanda, "
			+ " DECODE(CODMONOTIPOOPERAZIONE, 'D', 'Cancellazione','I', 'Inserimento','U','Modifica', CODMONOTIPOOPERAZIONE) as statoDomanda "
			+ " FROM AM_DID_INPS " + " WHERE upper(STRCODICEFISCALELAV) =trim(upper(' $CFLAV$ '))"
			+ " ORDER BY DATDICHIARAZIONE DESC";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nomeTabella = SourceBeanUtils.getAttrStrNotNull(req, "NOMETABELLA");
		String cfBeneficiario = SourceBeanUtils.getAttrStrNotNull(req, "CFLAV");
		// String tipoDomanda = SourceBeanUtils.getAttrStrNotNull(req, "TIPODOMANDA");

		StringBuffer query = null;

		if (nomeTabella.equals("AM_RDC")) {
			query = new StringBuffer(QUERY_AM_RDC);
		} else if (nomeTabella.equals("AM_DID_INPS")) {
			query = new StringBuffer(QUERY_AM_DID_INPS);
		}

		String queryStr = query.toString().replaceAll("\\$CFLAV\\$", cfBeneficiario);

		query = new StringBuffer(queryStr);

		return query.toString();
	}

}
