package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author savino
 */
public class DynamicRicercaCpiMigrazioni implements IDynamicStatementProvider {

	private static final String STM = "SELECT distinct cpi.CODCPI, cpi.STRDESCRIZIONE, "
			+ " decode(cpi.codMonoTipoFile,'T','file TXT','P','file PDF','C','Cooperazione Applicativa','E','file PDF e TXT','') as tipoFile, "
			+ " com.strdenominazione as comune, cpi.codMonoTipoFile, "
			+ " pr.STRDENOMINAZIONE as provincia , cpi.strEMailMigrazione, " + " cpi.stremailpec, cpi.strresponsabile "
			+ "FROM de_cpi cpi, ts_generale gen, de_provincia pr, de_comune com "
			+ "WHERE cpi.codcom=com.codcom and com.codprovincia=pr.codprovincia "
			+ "AND EXISTS (SELECT C.CODCOM FROM DE_COMUNE C WHERE C.CODCPI = CPI.CODCPI)";

	/**
	 * 
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String ricerca = (String) serviceRequest.getAttribute("tipoRicerca");
		String descrizione = (String) serviceRequest.getAttribute("descrizione");
		String territorio = (String) serviceRequest.getAttribute("territorio");
		String codProvincia = (String) serviceRequest.getAttribute("codProvincia");
		String codRegione = (String) serviceRequest.getAttribute("codRegione");
		String tipoFile = (String) serviceRequest.getAttribute("tipo_file");
		//
		StringBuffer stm = new StringBuffer(STM);
		if (!descrizione.equals("")) {
			if ("esatta".equals(ricerca))
				stm.append(" and upper(cpi.strDescrizione) = upper('" + descrizione + "') ");
			else
				stm.append(" and upper(cpi.strDescrizione) like upper('%" + descrizione + "%') ");
		}
		if ("fuori_pro".equals(territorio)) {
			stm.append(" and  com.CODPROVINCIA <> gen.CODPROVINCIASIL ");
		} else if ("fuori_reg".equals(territorio)) { // fuori regione
			// stm.append(" and pr2.CODPROVINCIA = gen.CODPROVINCIASIL ");
			stm.append(" and pr.CODREGIONE <> gen.CODREGIONESIL ");
		}
		if (codProvincia != null && !codProvincia.equals("")) { // provincia
			stm.append(" and com.codprovincia = '" + codProvincia + "' ");
		}
		if (codRegione != null && !codRegione.equals("")) { // regione
			stm.append(" and pr.codRegione = '" + codRegione + "' ");
		}
		if (tipoFile.equals("S")) {
			stm.append(" and cpi.codMonoTipoFile is not null ");
		} else if (tipoFile.equals("N")) {
			stm.append(" and cpi.codMonoTipoFile is null ");
		}
		stm.append(" order by strdescrizione asc");
		return stm.toString();
	}

	/**
	 * per provare la query generata lanciare il main impostanto gli opportuni valori della variabile locale request
	 */
	public static void main(String[] args) throws Exception {
		RequestContainer rc = new RequestContainer();
		SourceBean request = new SourceBean("row");
		rc.setServiceRequest(request);
		request.setAttribute("tipoRicerca", "contiene");
		request.setAttribute("descrizione", "erg");
		request.setAttribute("territorio", "fuori_reg");
		request.setAttribute("codProvincia", "8");
		request.setAttribute("codRegione", "");
		request.setAttribute("tipo_file", "");
		DynamicRicercaCpiMigrazioni d = new DynamicRicercaCpiMigrazioni();
		System.out.println(d.getStatement(rc, null));
	}
}
