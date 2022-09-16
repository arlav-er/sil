package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author roccetti
 * 
 *         Classe che fornisce la query per la selezione delle opzioni della comboTipoMov Se ha il codMonoTempo del mov
 *         prec e se si tratta di un TI lo utilizza per escludere la proroga dalle opzioni possibili
 */
public class DynSelectTipoTrasf implements IDynamicStatementProvider {

	private String SELECT_PROLOGO = "select codTipoTrasf as codice, "
			+ " 		strDescrizione || DECODE(GREATEST(SYSDATE, DATFINEVAL), SYSDATE, ' (scaduto)', '') as descrizione "
			+ "from de_tipo_trasf";

	private String SELECT_EPILOGO = " order by DATFINEVAL desc,codice asc ";

	public DynSelectTipoTrasf() {
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String context = "";
		String provenienza = StringUtils.getAttributeStrNotNull(req, "PROVENIENZA");

		if (req.containsAttribute("MostraTra")) {
			context = StringUtils.getAttributeStrNotNull(req, "MostraTra");
		}

		if (provenienza.equals("validazione")) {
			context = provenienza;
		}

		String query = SELECT_PROLOGO;

		if (context.equals("")) {
			query += " where codMonoTipo = 'R' ";
		}

		query += SELECT_EPILOGO;
		return query;
	}
}
