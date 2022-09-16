/*
 * Created on Sep 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasfRamoAzienda;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.NavigationCache;

/**
 * Una volta completata l'operazione di trasferimento ramo azienda, vengono mostrati i movimenti di trasformazione
 * creati, con i dati di protocollazione. I progressivi movimento coinvolti si trovano nell' oggetto NavigationCache nel
 * session container. Questi movimenti referenziano il successivo, ovvero il mov. di trasformazione appena creato.
 * 
 * @author savino
 */
public class GetListaMovLavTrasferiti implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		StringBuffer enumPrg, stm;
		SourceBean request = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();
		NavigationCache checkedLavCache = (NavigationCache) session.getAttribute("TRASFRAMOAZIENDACACHE");
		if (checkedLavCache == null)
			return "select 1 from dual where 1=0";
		// il fatto che non ci sia l'oggetto in sessione dovrebbe essere un errore. Evito di ritornare null;
		Object checkedLavObj = checkedLavCache.getField("CHECKBOXMOV");
		Vector checkedLavVector = null;
		if (checkedLavObj != null) {
			if (checkedLavObj instanceof Vector) {
				checkedLavVector = (Vector) checkedLavObj;
			} else if (!"EMPTY".equalsIgnoreCase(checkedLavObj.toString())) {
				checkedLavVector = new Vector(1);
				checkedLavVector.addElement(checkedLavObj.toString());
			}
		} else
			return "select 1 from dual where 1=0";
		// anche in questo caso evito di generare un errore
		enumPrg = new StringBuffer();
		for (int i = 0; i < checkedLavVector.size(); i++) {
			// il formato del valore del checkbox e': prgMovimento_numKloMovimento
			String checkValue = (String) checkedLavVector.get(i);
			int posSep = checkValue.indexOf('_');
			if (posSep < 0)
				posSep = checkValue.length(); // il separatore non c'e' -> prendo tutto
			if (posSep == 0)
				continue; // il separatore e' il primo carattere oppure la stringa e' vuota
							// -> il prg movimento non c'e' quindi proseguo
			String prgMovimento = checkValue.substring(0, posSep);
			// controllo che sia un valore numerico
			try {
				Integer.parseInt(prgMovimento);
			} catch (NumberFormatException e) {
				// il valore estratto non e' numerico: proseguo senza generare errori
			}
			if (enumPrg.length() > 0)
				enumPrg.append(',');
			enumPrg.append(prgMovimento);
		}
		stm = new StringBuffer();

		stm.append("select ");
		stm.append("LAV.STRCODICEFISCALE STRCODICEFISCALE,  LAV.STRCOGNOME STRCOGNOME, ");
		stm.append(
				"LAV.STRNOME STRNOME, TRASF.CODTIPOCONTRATTO CODTIPOASS,  TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, ");
		stm.append("CASE ");
		stm.append(" WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL ");
		stm.append("  THEN DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO || ' ' ");
		stm.append(" WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL ");
		stm.append("  THEN DOC.NUMANNOPROT || ' ' ");
		stm.append(" WHEN DOC.NUMANNOPROT IS NULL AND DOC.NUMPROTOCOLLO IS NOT NULL ");
		stm.append("  THEN DOC.NUMPROTOCOLLO || ' ' ");
		stm.append("END ");
		stm.append("|| TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') ");
		stm.append("|| ' ' ");
		stm.append("|| ");
		stm.append("CASE ");
		stm.append(" WHEN UPPER(DOC.CODMONOIO) = 'I' ");
		stm.append("  THEN 'IN' ");
		stm.append(" WHEN UPPER(DOC.CODMONOIO) = 'O' ");
		stm.append("  THEN 'OUT' ");
		stm.append("END ");
		stm.append("AS INFOPROTOCOLLO , ");
		stm.append("DE_TIPO_CONTRATTO.STRDESCRIZIONE ");
		stm.append("FROM ");
		stm.append("AN_LAVORATORE LAV,  AM_MOVIMENTO MOV  , am_movimento trasf, ");
		stm.append("AM_DOCUMENTO DOC, am_documento_coll doc_coll, DE_TIPO_CONTRATTO ");
		stm.append("where ");
		stm.append("LAV.CDNLAVORATORE = TRASF.CDNLAVORATORE ");
		stm.append("and mov.PRGMOVIMENTOSUCC = trasf.PRGMOVIMENTO ");
		stm.append("AND DOC.PRGDOCUMENTO = DOC_COLL.PRGDOCUMENTO ");
		stm.append("AND mov.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+) ");
		stm.append("AND doc_coll.strChiaveTabella = TO_CHAR(TRASF.PRGMOVIMENTO) ");
		stm.append("and doc.CODTIPODOCUMENTO in ('MVTRA','MVPRO','MVCES','MVAVV') ");
		stm.append("and mov.PRGMOVIMENTO in (");
		stm.append(enumPrg);
		stm.append(") ");
		stm.append("ORDER BY STRCOGNOME, STRNOME, STRCODICEFISCALE");
		return stm.toString();
	}

	/**
	 * test veloce dello statement
	 */
	public static void main(String s[]) {
		try {
			GetListaMovLavTrasferiti d = new GetListaMovLavTrasferiti();
			RequestContainer rc = new RequestContainer();
			SessionContainer sc = new SessionContainer(false);
			rc.setSessionContainer(sc);
			SourceBean req = new SourceBean("SERVICE_REQUEST");
			req.setAttribute("CHECKBOXMOV", "0001_");
			req.setAttribute("CHECKBOXMOV", "");
			req.setAttribute("CHECKBOXMOV", "_0");
			req.setAttribute("CHECKBOXMOV", "25649_123");
			String[] fields = { "CHECKBOXMOV", "PRGAZIENDAPROVENIENZA", "PRGUNITAPROVENIENZA" };
			NavigationCache newCache = new NavigationCache(fields);
			newCache.enable();
			newCache.setFieldsFromSourceBean(req);
			sc.setAttribute("TRASFRAMOAZIENDACACHE", newCache);
			System.out.println(d.getStatement(rc, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
