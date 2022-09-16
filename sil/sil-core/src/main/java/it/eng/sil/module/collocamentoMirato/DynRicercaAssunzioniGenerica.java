/*
 * Creato il 23-ott-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynRicercaAssunzioniGenerica implements IDynamicStatementProvider {

	public DynRicercaAssunzioniGenerica() {
	}

	private static final String SELECT_SQL_BASE = " SELECT " + "	CM_CONVENZIONE.PRGCONV, "
			+ " AN_AZIENDA.STRRAGIONESOCIALE, " + " CM_CONV_DETTAGLIO.NUMLAVORATORI, "
			+ " DECODE(CM_CONV_DETTAGLIO.CODMONOCATEGORIA,'D','Disabile','A','Altra categoria protetta') AS CATEGORIA, "
			+ " DECODE(CM_CONV_DETTAGLIO.CODMONOTIPO,'M','Nominativa','R','Numerica') AS TIPO, "
			+ " TO_CHAR(CM_CONV_DETTAGLIO.DATSCADENZA,'DD/MM/YYYY') DATASCADASS, "
			+ " TO_CHAR(CM_CONVENZIONE.DATCONVENZIONE,'DD/MM/YYYY') DATACONV, "
			+ " TO_CHAR(CM_CONVENZIONE.DATSCADENZA,'DD/MM/YYYY') DATASCADCONV, "
			+ " DE_PROVINCIA.STRDENOMINAZIONE AS PROVINCIA_ISCR " + "FROM CM_CONV_DETTAGLIO "
			+ "INNER JOIN CM_CONVENZIONE ON( CM_CONV_DETTAGLIO.PRGCONV = CM_CONVENZIONE.PRGCONV) "
			+ "INNER JOIN DE_PROVINCIA ON( CM_CONVENZIONE.CODPROVINCIA = DE_PROVINCIA.CODPROVINCIA) "
			+ "INNER JOIN DE_CONV_STATO_ASS ON( CM_CONV_DETTAGLIO.CODSTATO = DE_CONV_STATO_ASS.CODSTATO) "
			+ "INNER JOIN AN_AZIENDA ON( CM_CONVENZIONE.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ "WHERE CM_CONVENZIONE.CODSTATORICHIESTA = 'DE'";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
		String prgAziendaApp = StringUtils.getAttributeStrNotNull(req, "prgAziendaApp");
		if (prgAzienda.equals("") && !prgAziendaApp.equals("")) {
			prgAzienda = prgAziendaApp;
		}
		String datPrevDa = StringUtils.getAttributeStrNotNull(req, "datPrevista_Da");
		String datPrevA = StringUtils.getAttributeStrNotNull(req, "datPrevista_A");
		String codStatoAss = StringUtils.getAttributeStrNotNull(req, "codStatoAss");
		String codMonoTipo = StringUtils.getAttributeStrNotNull(req, "codMonoTipo");
		String codMonoCategoria = StringUtils.getAttributeStrNotNull(req, "codMonoCategoria");
		String ambitoTerritoriale = StringUtils.getAttributeStrNotNull(req, "PROVINCIA_ISCR");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			buf.append(" AND (CM_CONVENZIONE.PRGAZIENDA) = '" + prgAzienda.toUpperCase() + "'");
		}

		if ((codStatoAss != null) && (!codStatoAss.equals(""))) {
			buf.append(" AND (CM_CONV_DETTAGLIO.CODSTATO) = '" + codStatoAss.toUpperCase() + "'");
		}

		if ((datPrevDa != null) && (!datPrevDa.equals(""))) {
			buf.append(
					" AND (CM_CONV_DETTAGLIO.DATSCADENZA) >= TO_DATE('" + datPrevDa.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((datPrevA != null) && (!datPrevA.equals(""))) {
			buf.append(
					" AND (CM_CONV_DETTAGLIO.DATSCADENZA) <= TO_DATE('" + datPrevA.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((codMonoTipo != null) && (!codMonoTipo.equals(""))) {
			buf.append(" AND (CM_CONV_DETTAGLIO.CODMONOTIPO) = '" + codMonoTipo.toUpperCase() + "'");
		}

		if ((codMonoCategoria != null) && (!codMonoCategoria.equals(""))) {
			buf.append(" AND (CM_CONV_DETTAGLIO.CODMONOCATEGORIA) = '" + codMonoCategoria.toUpperCase() + "'");
		}

		if ((ambitoTerritoriale != null) && (!ambitoTerritoriale.equals(""))) {
			buf.append(" AND (CM_CONVENZIONE.CODPROVINCIA) = '" + ambitoTerritoriale + "'");
		}

		buf.append(" ORDER BY CM_CONV_DETTAGLIO.DATSCADENZA ASC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}