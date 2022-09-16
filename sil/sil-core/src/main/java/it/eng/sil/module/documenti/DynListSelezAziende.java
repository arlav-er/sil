package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;

/**
 * Ricerca dinamica di un'azienda.
 * 
 * @author Luigi Antenucci
 */
public class DynListSelezAziende implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf_ric");
		String piva = (String) req.getAttribute("piva_ric");
		String ragsoc = (String) req.getAttribute("ragsoc_ric");
		String prov = (String) req.getAttribute("prov_ric");

		DynamicStatementUtils dsu = new DynamicStatementUtils();
		dsu.addSelect(" an_unita_azienda.PRGAZIENDA," + " an_unita_azienda.PRGUNITA,"
				+ " InitCap(AN_AZIENDA.STRRAGIONESOCIALE) as STRRAGIONESOCIALE," + " AN_AZIENDA.STRPARTITAIVA,"
				+ " AN_AZIENDA.STRCODICEFISCALE," + " AN_UNITA_AZIENDA.STRTEL," + " AN_UNITA_AZIENDA.STRINDIRIZZO,"
				+ " InitCap(DE_COMUNE.STRDENOMINAZIONE) as COMUNE_AZ");
		dsu.addFrom(
				" AN_UNITA_AZIENDA" + " INNER JOIN AN_AZIENDA on (an_unita_azienda.PRGAZIENDA = an_azienda.PRGAZIENDA)"
						+ " LEFT  JOIN VW_INDIRIZZI_COM_PROV on VW_INDIRIZZI_COM_PROV.codcom = AN_UNITA_AZIENDA.codcom"
						+ " INNER JOIN de_comune on (an_unita_azienda.CODCOM = de_comune.CODCOM)");

		dsu.addWhereIfFilledStrLikeUpper("an_azienda.STRCODICEFISCALE", cf, DynamicStatementUtils.DO_LIKE_INIZIA);
		dsu.addWhereIfFilledStrLikeUpper("an_azienda.STRPARTITAIVA", piva, DynamicStatementUtils.DO_LIKE_INIZIA);
		// su "ragione sociale" uso una ricerca per "contiene" la stringa
		dsu.addWhereIfFilledStrLikeUpper("an_azienda.STRRAGIONESOCIALE", ragsoc,
				DynamicStatementUtils.DO_LIKE_CONTIENE);

		dsu.addWhereIfFilledStr("VW_INDIRIZZI_COM_PROV.codProvincia", prov);

		dsu.addOrder("AN_AZIENDA.STRRAGIONESOCIALE");

		String query = dsu.getStatement();

		return query;
	}

}