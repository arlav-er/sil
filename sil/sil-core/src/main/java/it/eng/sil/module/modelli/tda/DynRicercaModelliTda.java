package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaModelliTda implements IDynamicStatementProvider {

	public DynRicercaModelliTda() {
	}

	private static final String SELECT_SQL_BASE = "SELECT modello.PRGMODVOUCHER, "
			+ " DE_AZIONE.strdescrizione as OBIETTIVOMISURA, "
			+ " to_char(modello.DECVALTOT, '9999999.99') as STRVALOREMAX, "
			+ " DE_VCH_SEL_MODALITA.strdescrizione as STRMODALITA, "
			+ " to_char(modello.NUMNGMAXATTVCH) as STRGIORNIATTIVAZIONE, "
			+ " to_char(modello.NUMNGMAXEROGVCH) as STRGIORNIEROGAZIONE, "
			+ " decode(modello.FLGATTIVO, 'S','Si','N','No','') as MODELLOATTIVO, "
			+ " decode(modello.FLGCM, 'S','Si','N','No','No') as MODELLOCM " + " FROM VCH_MODELLO_VOUCHER modello "
			+ " LEFT JOIN DE_VCH_SEL_MODALITA on (modello.CODSELEZMODALITA = DE_VCH_SEL_MODALITA.CODSELMODALITA) "
			+ " LEFT JOIN DE_AZIONE on (modello.PRGAZIONI = DE_AZIONE.PRGAZIONI) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String obiettivoMisura = SourceBeanUtils.getAttrStrNotNull(req, "obiettivoMisura");
		String modelloAttivo = SourceBeanUtils.getAttrStrNotNull(req, "modelloAttivo");
		String modelloCM = SourceBeanUtils.getAttrStrNotNull(req, "modelloCM");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((obiettivoMisura != null) && (!obiettivoMisura.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" DE_AZIONE.PRGAZIONI = " + obiettivoMisura);
		}

		if ((modelloAttivo != null) && (!modelloAttivo.equals(""))) {
			modelloAttivo = StringUtils.replace(modelloAttivo, "'", "''");
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(modello.FLGATTIVO) = '" + modelloAttivo.toUpperCase() + "'");
		}

		if ((modelloCM != null) && (!modelloCM.equals(""))) {
			modelloCM = StringUtils.replace(modelloCM, "'", "''");
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" nvl(upper(modello.FLGCM), 'N') = '" + modelloCM.toUpperCase() + "'");
		}

		buf.append(" ORDER BY modello.PRGMODVOUCHER");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}