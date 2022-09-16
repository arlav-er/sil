package it.eng.sil.module.amministrazione.redditoAttivazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Crea la query utilizzata per la ricerca dei valori da mostrare nella lista relativa alla funzionalità nuovo RA
 * 
 * @author Giacomo Pandini
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class ListaNuovoRA implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListaNuovoRA.class.getName());

	private static final String SELECT_SQL_BASE = "" + "select nra.PRGNUOVORA," + " nra.CODICEFISCALE STRCODICEFISCALE,"
			+ "	nra.cognome," + " nra.nome,"
			+ " TO_CHAR(nra.DATAINIZIOPRESTAZIONEASDINRA ,'dd/mm/yyyy') DATAINIZIOPRESTAZIONEASDINRA ,"
			+ " TO_CHAR(nra.DATAFINEPRESTAZIONEASDINRA ,'dd/mm/yyyy') DATAFINEPRESTAZIONEASDINRA,"
			+ " decode(nra.codmonotipodomanda,'N','Nuova','S','Successiva' ) tipodomanda,"
			// + " nra.codmonotipodomanda,"
			+ " te.strdescrizione," + " TO_CHAR(nra.DATACOMUNICAZIONE,'dd/mm/yyyy') DATACOMUNICAZIONE,"
			+ " nra.IDDOMANDAWEB," + " nra.IDDOMANDAINTRANET," + " nra.CDNUTINS," + " nra.DTMINS," + " nra.CDNUTMOD,"
			+ " nra.DTMMOD,"
			+ " nra.NUMKLONUOVORA, de_stato_nra.strdescrizione statodomanda, nra.flgautorizzabile, nra.importocomplessivonra "
			+ " from am_nuovo_ra nra" + " LEFT JOIN de_tipo_evento_ra te ON nra.codtipoevento = te.codtipoevento "
			+ " LEFT JOIN an_lavoratore lav ON nra.codicefiscale = lav.STRCODICEFISCALE "
			+ " LEFT JOIN de_stato_nra ON nra.codstatodomanda = de_stato_nra.codstatodomanda ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		// SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		SourceBean req = requestContainer.getServiceRequest();

		// Ottiene i valori in ingresso
		String nome = (String) req.getAttribute("NOME");
		String cognome = (String) req.getAttribute("COGNOME");
		String cf = (String) req.getAttribute("CF");
		String dataPrestazioneDa = (String) req.getAttribute("dataPrestazioneDa");
		String dataPrestazioneA = (String) req.getAttribute("dataPrestazioneA");
		String dataInizioPrestazioneDa = (String) req.getAttribute("dataInizioPrestazioneDa");
		String dataInizioPrestazioneA = (String) req.getAttribute("dataInizioPrestazioneA");
		String dataFinePrestazioneDa = (String) req.getAttribute("dataFinePrestazioneDa");
		String dataFinePrestazioneA = (String) req.getAttribute("dataFinePrestazioneA");
		String dataComunicazioneDa = (String) req.getAttribute("dataComunicazioneDa");
		String dataComunicazioneA = (String) req.getAttribute("dataComunicazioneA");
		String IDComunicazione = (String) req.getAttribute("IDComunicazione");
		String NProvvedimento = (String) req.getAttribute("NProvvedimento");
		String dataProvvedimento = (String) req.getAttribute("dataProvvedimento");
		String TipoEvento = (String) req.getAttribute("TipoEvento");
		String TipoComunicazione = (String) req.getAttribute("TipoComunicazione");
		String MotivoComunicazione = (String) req.getAttribute("MotivoComunicazione");
		String StatoDomanda = (String) req.getAttribute("StatoDomanda");
		String Autorizzabile = (String) req.getAttribute("Autorizzabile");
		_logger.debug("Ottenuti parametri in ingresso");
		StringBuffer selectToFrom = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// Per ogni valore controlla la sua presenza
		if ((nome != null) && (!nome.equals(""))) {
			nome = StringUtils.replace(nome, "'", "''");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.nome) = '" + nome.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore nome nella query");

		if ((cognome != null) && (!cognome.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.cognome) = '" + cognome.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore cognome nella query");

		if ((cf != null) && (!cf.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.codiceFiscale) = '" + cf.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore cf nella query");

		if ((dataPrestazioneDa != null) && (!dataPrestazioneDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataPresentazioneAsdiNra >= to_date('" + dataPrestazioneDa + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataPrestazioneDa nella query");

		if ((dataPrestazioneA != null) && (!dataPrestazioneA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataPresentazioneAsdiNra <= to_date('" + dataPrestazioneA + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataPrestazioneA nella query");

		if ((dataInizioPrestazioneDa != null) && (!dataInizioPrestazioneDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataInizioPrestazioneAsdiNra >= to_date('" + dataInizioPrestazioneDa + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataInizioPrestazioneDa nella query");

		if ((dataInizioPrestazioneA != null) && (!dataInizioPrestazioneA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataInizioPrestazioneAsdiNra <= to_date('" + dataInizioPrestazioneA + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataInizioPrestazioneA nella query");

		if ((dataFinePrestazioneDa != null) && (!dataFinePrestazioneDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataFinePrestazioneAsdiNra >= to_date('" + dataFinePrestazioneDa + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataFinePrestazioneDa nella query");

		if ((dataFinePrestazioneA != null) && (!dataFinePrestazioneA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataFinePrestazioneAsdiNra <= to_date('" + dataFinePrestazioneA + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataFinePrestazioneA nella query");

		if ((dataComunicazioneDa != null) && (!dataComunicazioneDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.datacomunicazione >= to_date('" + dataComunicazioneDa + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataComunicazioneDa nella query");

		if ((dataComunicazioneA != null) && (!dataComunicazioneA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.datacomunicazione <= to_date('" + dataComunicazioneA + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataComunicazioneA nella query");

		if ((IDComunicazione != null) && (!IDComunicazione.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.identificativoComunicazione) = '" + IDComunicazione.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore IDComunicazione nella query");

		if ((NProvvedimento != null) && (!NProvvedimento.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.numeroprovvedimento) = '" + NProvvedimento.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore NProvvedimento nella query");

		if ((dataProvvedimento != null) && (!dataProvvedimento.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nra.dataprovvedimento = to_date('" + dataProvvedimento + "','dd/mm/yyyy' )");
		}
		_logger.debug("Gestito valore dataProvvedimento nella query");

		if ((TipoEvento != null) && (!TipoEvento.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.codtipoevento) = '" + TipoEvento.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore TipoEvento nella query");

		if ((TipoComunicazione != null) && (!TipoComunicazione.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.CODTIPOCOMUNICAZIONE) = '" + TipoComunicazione.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore TipoComunicazione nella query");

		if ((MotivoComunicazione != null) && (!MotivoComunicazione.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.CODMOTIVOSANZIONE) = '" + MotivoComunicazione.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore MotivoComunicazione nella query");

		if ((StatoDomanda != null) && (!StatoDomanda.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.codstatodomanda) = '" + StatoDomanda.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore StatoDomanda nella query");

		if ((Autorizzabile != null) && (!Autorizzabile.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(nra.flgautorizzabile) = '" + Autorizzabile.toUpperCase() + "'");
		}
		_logger.debug("Gestito valore Autorizzabile nella query");

		buf.append(" ORDER BY COGNOME,NOME,CODICEFISCALE");
		selectToFrom.append(buf);

		return selectToFrom.toString();

	}
}
