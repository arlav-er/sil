/*
 * Creato il 30-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.InfoProvinciaSingleton;

public class DynRicercaMovimentiComputo implements IDynamicStatementProvider {
	private String SELECT_SQL_BASE_GESTISCI = " SELECT " + " MOV.prgMovimento PRGMOV, " + " MOV.NUMORESETT NUMORESETT, "
			+ " MOV.CODORARIO CODORARIO, " + " LAV.cdnLavoratore CDNLAV, " + " AZ.prgAzienda PRGAZ, "
			+ " UAZ.prgUnita PRGUAZ, " + " TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') DATAMOV, "
			+ " SUBSTR(MOV.codTipoMov, 1, 1 ) codTipoMov, " + " MOV.CODSTATOATTO as codstatoattomov, "
			+ " TO_CHAR(pg_anagrafica_professionale_rp.getdatafinemoveffforrp(MOV.prgMovimento), 'DD/MM/YYYY') DATFINEMOVEFFETTIVA, "
			+ " LAV.strCognome || ' ' || LAV.strNome COGNOMENOMELAV, " + " LAV.strCodiceFiscale CODFISCLAV, "
			+ " AZ.strRagioneSociale RAGSOCAZ, " + " AZ.strCodiceFiscale CODFISCAZ, "
			+ " UAZ.strIndirizzo || ', ' || COM.strDenominazione || '(' || RTRIM(PROV.strIstat) || ')' IndirAzienda "
			+ " FROM AM_MOVIMENTO MOV, " + " AN_UNITA_AZIENDA UAZ, " + " AN_AZIENDA AZ, "
			+ " AN_LAVORATORE LAV, DE_COMUNE COM, DE_PROVINCIA PROV "
			+ " WHERE uaz.CODCOM = COM.codCom AND COM.codProvincia = PROV.codProvincia "
			+ " AND MOV.cdnLavoratore = LAV.cdnLavoratore " + " AND mov.prgazienda = az.prgazienda "
			+ " AND MOV.prgAzienda = UAZ.prgAzienda  AND MOV.prgUnita = UAZ.prgUnita " + " AND mov.codTipoMov = 'AVV'"
			+ " AND MOV.codStatoAtto = 'PR'";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codProvinciaSil = InfoProvinciaSingleton.getInstance().getCodice();
		String flgPoloReg = InfoProvinciaSingleton.getInstance().getFlgPoloReg();
		String codiceReg = InfoProvinciaSingleton.getInstance().getCodiceRegione();

		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		String prgAzienda = (String) req.getAttribute("PRGAZIENDA");
		String prgUnita = (String) req.getAttribute("PRGUNITA");
		String datInizioComp = (String) req.getAttribute("DATINIZCOMP");

		StringBuffer buf_totale;
		buf_totale = new StringBuffer(SELECT_SQL_BASE_GESTISCI);

		StringBuffer buf = new StringBuffer();

		if (flgPoloReg != null && flgPoloReg.equalsIgnoreCase("S")) {
			buf.append(" AND PROV.codRegione = '" + codiceReg + "' ");
		} else {
			buf.append(" AND COM.codProvincia = '" + codProvinciaSil + "' ");
		}

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			buf.append(" AND MOV.cdnLavoratore = " + cdnLavoratore);
		}

		if ((prgAzienda != null) && (!prgAzienda.equals("")) && (prgUnita != null) && (!prgUnita.equals(""))) {
			buf.append(" AND MOV.prgAzienda = " + prgAzienda + " AND MOV.prgUnita = " + prgUnita);
		}

		if ((datInizioComp != null) && (!datInizioComp.equals(""))) {
			buf.append(" AND MOV.datInizioMov <= to_date('" + datInizioComp + "', 'DD/MM/YYYY')");
			buf.append(" AND (pg_anagrafica_professionale_rp.getdatafinemoveffforrp(mov.prgmovimento) IS NULL");
			buf.append(
					" 		OR trunc(pg_anagrafica_professionale_rp.getdatafinemoveffforrp(mov.prgmovimento)) > to_date('"
							+ datInizioComp + "', 'DD/MM/YYYY'))");
		}

		buf.append(" ORDER BY MOV.datInizioMov DESC, COGNOMENOMELAV, RAGSOCAZ, IndirAzienda");

		buf_totale.append(buf.toString());
		return buf_totale.toString();
	}
}
