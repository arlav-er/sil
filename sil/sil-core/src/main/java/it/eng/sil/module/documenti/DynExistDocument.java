package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author savino
 */
public class DynExistDocument implements IDynamicStatementProvider {

	private static final String STM = "SELECT AM_DOCUMENTO.PRGDOCUMENTO," + "AM_DOCUMENTO.CODTIPODOCUMENTO,"
			+ "AM_DOCUMENTO_BLOB.PRGDOCUMENTO PRGBLOB," + "AM_DOCUMENTO.STRNOMEDOC," + "AM_DOCUMENTO.NUMPROTOCOLLO,"
			+ "AM_DOCUMENTO.NUMANNOPROT " + "FROM AM_DOCUMENTO, AM_DOCUMENTO_BLOB "
			+ "WHERE AM_DOCUMENTO.PRGDOCUMENTO = AM_DOCUMENTO_BLOB.PRGDOCUMENTO "
			+ "AND nvl(dbms_lob.getlength(AM_DOCUMENTO_BLOB.BLBFILE), 0) > 0 ";

	/**
	 * 
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
		String strChiaveTabella = (String) serviceRequest.getAttribute("strChiaveTabella");
		String tipoDocumento = (String) serviceRequest.getAttribute("tipoDoc");
		//
		StringBuffer stm = new StringBuffer(STM);
		if (strChiaveTabella != null && !strChiaveTabella.equals("")) {
			stm.append("and AM_DOCUMENTO.PRGDOCUMENTO = ");
			stm.append("(select max(amd_.PRGDOCUMENTO) ");
			stm.append("from AM_DOCUMENTO amd_, AM_DOCUMENTO_COLL amdc_ ");
			stm.append("where amd_.prgdocumento = amdc_.prgdocumento ");
			stm.append("and amd_.CDNLAVORATORE = " + cdnLavoratore.toString() + " ");
			stm.append("and UPPER(amd_.CODTIPODOCUMENTO) = UPPER('" + tipoDocumento + "') ");
			stm.append("AND amdc_.STRCHIAVETABELLA = '" + strChiaveTabella + "')");
		} else {
			stm.append("and AM_DOCUMENTO.PRGDOCUMENTO = ");
			stm.append("(select max(amd_.PRGDOCUMENTO) ");
			stm.append("from AM_DOCUMENTO amd_ ");
			stm.append("where amd_.CDNLAVORATORE = " + cdnLavoratore.toString() + " ");
			stm.append("and UPPER(amd_.CODTIPODOCUMENTO) = UPPER('" + tipoDocumento + "')) ");
		}
		return stm.toString();

	}
}
