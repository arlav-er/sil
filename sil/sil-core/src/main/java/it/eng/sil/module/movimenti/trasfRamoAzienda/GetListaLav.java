/*
 * Creato il 27-ago-04
 * @author: Paolo Roccetti
 */
package it.eng.sil.module.movimenti.trasfRamoAzienda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Questa classe restituisce la query per la ricerca dei lavoratori nel traferimento del ramo aziendale: Restituisce
 * tutti i lavoratori con un movimento aperto (non di cessazione e senza successivo)
 * <p>
 * 
 * @author: Paolo Roccetti
 * 
 */

public class GetListaLav implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE,  "
			+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, " + " LAV.STRNOME STRNOME,"
			+ " MOV.PRGMOVIMENTO || '_' || (MOV.NUMKLOMOV + 1) PRGMOVIMENTO, " + " MOV.CODTIPOCONTRATTO CODTIPOASS, "
			+ " MOV.DATINIZIOMOV, " + " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, "
			+ " TO_CHAR(MOV.DATINIZIOAVV, 'DD/MM/YYYY') DATINIZIOAVV " + " FROM AN_LAVORATORE LAV, "
			+ " AM_MOVIMENTO MOV "
			+ " LEFT JOIN DE_TIPO_CONTRATTO ON( MOV.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO)"
			+ " WHERE LAV.CDNLAVORATORE = MOV.CDNLAVORATORE " + " AND NVL(DE_TIPO_CONTRATTO.codmonotipo,' ') <> 'T' "
			+ " AND MOV.CODTIPOMOV != 'CES' AND MOV.PRGMOVIMENTOSUCC IS NULL AND CODSTATOATTO = 'PR'"
			+ " and nvl(mov.datfinemov, to_date('31/12/2500','dd/mm/yyyy')) >= sysdate";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String PRGAZIENDAPROVENIENZA = StringUtils.getAttributeStrNotNull(req, "PRGAZIENDAPROVENIENZA");
		String PRGUNITAPROVENIENZA = StringUtils.getAttributeStrNotNull(req, "PRGUNITAPROVENIENZA");
		String FILTROCFLAV = StringUtils.getAttributeStrNotNull(req, "FILTROCFLAV");
		String FILTRONOMELAV = StringUtils.getAttributeStrNotNull(req, "FILTRONOMELAV");
		String FILTROCOGNOMELAV = StringUtils.getAttributeStrNotNull(req, "FILTROCOGNOMELAV");
		String FILTRODATAINIZIOASS = StringUtils.getAttributeStrNotNull(req, "FILTRODATAINIZIOASS");
		String FILTRODATAFINEASS = StringUtils.getAttributeStrNotNull(req, "FILTRODATAFINEASS");
		String FILTROCODTIPOASS = StringUtils.getAttributeStrNotNull(req, "FILTROCODTIPOASS");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		// Parametri sempre presenti
		buf.append(" AND PRGAZIENDA = " + PRGAZIENDAPROVENIENZA + " AND PRGUNITA = " + PRGUNITAPROVENIENZA);

		// Parametri opzionali
		if (!FILTROCFLAV.equals("")) {
			buf.append(" AND upper(STRCODICEFISCALE) = '" + FILTROCFLAV.toUpperCase() + "'");
		}

		if (!FILTRONOMELAV.equals("")) {
			FILTRONOMELAV = StringUtils.replace(FILTRONOMELAV, "'", "''");
			buf.append(" AND upper(STRNOME) = '" + FILTRONOMELAV.toUpperCase() + "'");
		}

		if (!FILTROCOGNOMELAV.equals("")) {
			FILTROCOGNOMELAV = StringUtils.replace(FILTROCOGNOMELAV, "'", "''");
			buf.append(" AND upper(STRCOGNOME) = '" + FILTROCOGNOMELAV.toUpperCase() + "'");
		}

		if (!FILTRODATAINIZIOASS.equals("")) {
			buf.append(" AND DATINIZIOAVV >= TO_DATE('" + FILTRODATAINIZIOASS + "', 'DD/MM/YYYY')");
		}

		if (!FILTRODATAFINEASS.equals("")) {
			buf.append(" AND DATINIZIOAVV <= TO_DATE('" + FILTRODATAFINEASS + "', 'DD/MM/YYYY')");
		}

		if (!FILTROCODTIPOASS.equals("")) {
			buf.append(" AND upper(MOV.CODTIPOCONTRATTO) = '" + FILTROCODTIPOASS.toUpperCase() + "'");
		}

		buf.append(" ORDER BY STRCOGNOME, STRNOME, STRCODICEFISCALE");
		return buf.toString();
	}
}
