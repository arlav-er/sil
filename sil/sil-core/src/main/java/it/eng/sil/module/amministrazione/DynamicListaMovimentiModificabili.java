package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicListaMovimentiModificabili implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaMovimentiModificabili.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT " + "  mov.prgmovimento," + "    mov.numklomov,"
			+ "   mov.prgmovimentoprec," + "   mov.prgmovimentosucc," + "  lav.cdnlavoratore,"
			+ "  az.prgazienda                  prgaz," + "  uaz.prgunita                   prguaz,"
			+ "  to_char(mov.datfinemov, 'dd/mm/yyyy') datfinemov,"
			+ " to_char(mov.datiniziomov, 'DD/MM/YYYY') datamov," + "  mov.codtipomov,"
			+ "  mov.codtipocontratto           codtipoass," + "  mov.codmonotipofine            codmonotipofine,"
			+ "  mov.codmonotempo               codmonotempo," + "  mov.codstatoatto,"
			+ "  mov.codmotannullamento, mov.DATINIZIOMOV, "
			+ "  to_char(mov.datfinemoveffettiva, 'DD/MM/YYYY') datfineeffettiva," + "  mov.codtipotrasf,"
			+ "  de_tipo_trasf.strdescrizione   strtrasferimento," + "  lav.strcognome" + "  || ' '"
			+ "  || lav.strnome cognomenomelav," + "  lav.strcodicefiscale           codfisclav,"
			+ "  de_tipo_trasf.strdescrizione as tipoTrasformazione," + "  az.strragionesociale           ragsocaz"
			+ "  FROM" + "  am_movimento       mov," + "  de_mv_tipo_mov     tmov," + "  an_unita_azienda   uaz,"
			+ "  an_azienda         az," + "  an_lavoratore      lav," + "  de_comune          com,"
			+ "  de_provincia       prov," + "  de_tipo_contratto," + "  de_tipo_trasf" + "  WHERE"
			+ "  mov.codtipomov = tmov.codtipomov"
			+ "  AND mov.codtipocontratto = de_tipo_contratto.codtipocontratto (+)"
			+ "  AND mov.codtipotrasf = de_tipo_trasf.codtipotrasf (+)" + "  AND mov.cdnlavoratore = lav.cdnlavoratore"
			+ "  AND mov.prgazienda = az.prgazienda" + "  AND mov.prgazienda = uaz.prgazienda"
			+ "  AND mov.prgunita = uaz.prgunita" + "  AND uaz.codcom = com.codcom"
			+ "  AND com.codprovincia = prov.codprovincia (+)";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();

		Vector prgMov = request.getAttributeAsVector("ckeckboxMovimenti");
		if (prgMov != null && !prgMov.isEmpty()) {
			StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);
			if (prgMov.size() == 1) {
				buf.append(" AND mov.prgmovimento = ");
			} else {
				buf.append(" AND mov.prgmovimento IN ( ");
			}
			BigDecimal prgMovAppTemp = null;
			for (int i = 0; i < prgMov.size(); i++) {
				prgMovAppTemp = new BigDecimal((String) prgMov.get(i));
				buf.append(prgMovAppTemp);
				if (i != prgMov.size() - 1) {
					buf.append(" ,");
				}
			}
			if (prgMov.size() > 1) {
				buf.append(")");
			}
			buf.append(" ORDER BY DATINIZIOMOV asc");
			return buf.toString();
		}
		return null;
	}

}