/*
 * Creato il 24-giu-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.util.Utils;

/**
 * @author roccetti
 * 
 *         Classe che fornisce la query per la selezione delle opzioni della comboTipoMov Se ha il codMonoTempo del mov
 *         prec e se si tratta di un TI lo utilizza per escludere la proroga dalle opzioni possibili
 */
public class DynSelectTipoMov implements IDynamicStatementProvider {

	private String SELECT_PROLOGO = "select " + " codtipomov as codice, " + " strdescrizione as descrizione "
			+ " from de_mv_tipo_mov " + " where codtipomov != 'AVV' ";

	private String SELECT_EPILOGO = " order by strDescrizione";

	public DynSelectTipoMov() {
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String codMonoTipoAss = (String) req.getAttribute("CODMONOTIPOASS");
		String codMonoTempoMovPrec = (String) req.getAttribute("codMonoTempoMovPrec");
		String codTipoMovPrec = (String) req.getAttribute("CODTIPOMOVPREC");
		String codMVCessazione = (String) req.getAttribute("CODMVCESSAZIONEPREC");
		String codTipoAss = Utils.notNull(req.getAttribute("CODTIPOASS"));

		boolean tInd = (codMonoTempoMovPrec != null && codMonoTempoMovPrec.equalsIgnoreCase("I"));

		// Creo la parte dinamica della query e la ritorno
		String query = SELECT_PROLOGO;
		if ((codTipoMovPrec != null) && (codTipoMovPrec.equals("C")) && (codMVCessazione != null)
				&& (codMVCessazione.equals("SC"))) {
			query += " AND codtipomov = 'CES' ";
		} else {
			if (codMonoTipoAss != null && codMonoTipoAss.equals("N")
					&& !DeTipoContrattoConstant.mapContrattiAut_Tra.containsKey(codTipoAss)) {
				query += " AND codtipomov != 'TRA' ";
			} else {
				if (codMonoTipoAss != null && codMonoTipoAss.equals("T")) {
					query += " AND codtipomov != 'TRA' ";
				} else {
					query += (tInd ? " AND codtipomov != 'PRO' " : "");

				}
			}
		}
		query += SELECT_EPILOGO;
		return query;
	}
}
