/*
 * Creato il 3-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

public class DynamicIscrListeSpeciali implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicIscrListeSpeciali.class.getName());

	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " lav.STRCODICEFISCALE || '<BR/>' || lav.STRCOGNOME || ' ' ||  lav.STRNOME as Lavoratore, "
			+ " lav.cdnLavoratore, " + " art.PRGISCRART1, "
			+ " art.CDNUTPUNTEGGIO, PR.STRDENOMINAZIONE as PROVINCIA_ISCR, " + " PR.CODPROVINCIA as COD_PROVINCIA_ISCR,"
			+ " to_char(art.DATISCRALBO,'dd/mm/yyyy') as datIscrAlbo,"
			+ " to_char(art.DATISCRLISTAPROV,'dd/mm/yyyy') as datIscrListProv,"
			+ " lista.strdescrizione as tipoListaSpec," + " TO_CHAR (art.DATFINE,'DD/MM/YYYY') datFine,"
			+ " mot.STRDESCRIZIONE as motiviFine, " + " am_documento.CODSTATOATTO CODSTATOATTO, "
			+ " to_char(am_documento.DATINIZIO,'dd/mm/yyyy') as DatInizio, "
			+ " to_char(am_documento.DATACQRIL,'dd/mm/yyyy') as DatAcqRil, "
			+ " to_char(am_documento.DATPROTOCOLLO,'DD/MM/YYYY hh24:mi') as dataOraProt, "
			+ " am_documento.NUMPROTOCOLLO as NUMPROTOCOLLO, " + " am_documento.NUMANNOPROT as numAnnoProt, "
			+ " de_stato_atto.STRDESCRIZIONE as Stato, " + " art.prgspi as PRGSPI";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();

		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String datIscrAlboDa = (String) req.getAttribute("datIscrAlboDa");
		String datIscrAlboA = (String) req.getAttribute("datIscrAlboA");
		String datIscrListProvDa = (String) req.getAttribute("datIscrListProvDa");
		String datIscrListProvA = (String) req.getAttribute("datIscrListProvA");
		String tipoListaSpec = (String) req.getAttribute("tipoListaSpec");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codCpi = Utils.notNull((String) req.getAttribute("CodCPI"));
		String cdnLavoratore = Utils.notNull((String) req.getAttribute("CDNLAVORATORE"));
		String PROVINCIA_ISCR = Utils.notNull((String) req.getAttribute("PROVINCIA_ISCR"));

		dynamicStatementUt.addSelect(SELECT_SQL_BASE);

		dynamicStatementUt.addFrom("CM_ISCR_ART1 art");
		dynamicStatementUt.addFrom("DE_CM_LISTA_SPECIALE lista");
		dynamicStatementUt.addFrom("an_lavoratore lav");
		dynamicStatementUt.addFrom("an_lav_storia_inf inf");
		dynamicStatementUt.addFrom("de_motivo_fine_atto mot");
		dynamicStatementUt.addFrom("am_documento_coll");
		dynamicStatementUt.addFrom("am_documento");
		dynamicStatementUt.addFrom("de_stato_atto");
		dynamicStatementUt.addFrom("DE_PROVINCIA PR");

		dynamicStatementUt.addWhere("  lista.CODTIPOLISTA = art.CODTIPOLISTA ");
		dynamicStatementUt.addWhere("  lav.cdnlavoratore = art.CDNLAVORATORE ");
		dynamicStatementUt.addWhere("  inf.cdnlavoratore = lav.CDNLAVORATORE ");
		dynamicStatementUt.addWhere("  art.CODMOTIVOFINEATTO = mot.CODMOTIVOFINEATTO (+) ");
		dynamicStatementUt.addWhere("  inf.cdnlavoratore = lav.CDNLAVORATORE ");
		dynamicStatementUt.addWhere("  art.CODPROVINCIA = PR.CODPROVINCIA ");

		dynamicStatementUt.addWhere("  art.PRGISCRART1 = to_number(am_documento_coll.STRCHIAVETABELLA) ");
		dynamicStatementUt.addWhere("  art.PRGISCRART1 = am_documento_coll.STRCHIAVETABELLA ");
		dynamicStatementUt.addWhere("  am_documento.PRGDOCUMENTO = am_documento_coll.PRGDOCUMENTO ");
		dynamicStatementUt.addWhere("  am_documento.CODTIPODOCUMENTO = 'ILS' ");
		dynamicStatementUt.addWhere("  am_documento.CODSTATOATTO = de_stato_atto.CODSTATOATTO ");
		dynamicStatementUt.addWhere("  inf.datFine is null ");

		if (!("").equalsIgnoreCase(codCpi) && codCpi != null) {
			dynamicStatementUt.addWhereIfFilledStr("inf.codCpiTit", codCpi);
			dynamicStatementUt.addWhereIfFilledStr("inf.codMonoTipoCpi", "C");
		}

		dynamicStatementUt.addWhereIfFilledStr("lav.CDNLAVORATORE", cdnLavoratore);

		if (tipoRic.equalsIgnoreCase("esatta")) {
			dynamicStatementUt.addWhereIfFilledStrUpper("strnome", nome);
			dynamicStatementUt.addWhereIfFilledStrUpper("strcognome", cognome);
			dynamicStatementUt.addWhereIfFilledStrUpper("strCodiceFiscale", cf);
		} else {
			dynamicStatementUt.addWhereIfFilledStrLikeUpper("strnome", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dynamicStatementUt.addWhereIfFilledStrLikeUpper("strcognome", cognome,
					DynamicStatementUtils.DO_LIKE_INIZIA);
			dynamicStatementUt.addWhereIfFilledStrLikeUpper("strCodiceFiscale", cf,
					DynamicStatementUtils.DO_LIKE_INIZIA);
		}

		dynamicStatementUt.addWhereIfFilledStrUpper("art.CODTIPOLISTA", tipoListaSpec);
		dynamicStatementUt.addWhereIfFilledStrUpper("art.CODPROVINCIA", PROVINCIA_ISCR);

		dynamicStatementUt.addWhereIfFilledDateBetween("art.datiscralbo", datIscrAlboDa, datIscrAlboA);
		dynamicStatementUt.addWhereIfFilledDateBetween("art.datiscrlistaprov", datIscrListProvDa, datIscrListProvA);

		dynamicStatementUt.addOrder("art.DATISCRLISTAPROV desc, art.DATFINE desc");

		_logger.debug(className + "::Stringa di ricerca:" + dynamicStatementUt.toString());

		return (dynamicStatementUt.getStatement());
	}

}
