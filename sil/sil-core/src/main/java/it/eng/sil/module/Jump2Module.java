package it.eng.sil.module;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.QueryString;

/**
 * Usato per tutte le PAGE di "Jump2Xxx". Non fa quasi nulla, riporta in RESPONSE i parametri ricevuti in REQUEST.
 * 
 * @author Luigi Antenucci
 */
public class Jump2Module extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Jump2Module.class.getName());

	private static final String className = StringUtils.getClassName(Jump2Module.class);

	private final String PREFIX_JUMP2 = "Jump2";
	// COSTANTE: è il PREFISSO con cui DEVONO cominciare tutte le PAGE.
	// Esso viene tolto e il nome rimanente sarà (a meno di ALIAS) la vera PAGE
	// da eseguire.

	public static final String PARAM_jump2page = "jump2page";
	public static final String PARAM_jump2clear = "jump2clear";

	public static final String ATTRIB_jump2url = "jump2url";

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + ".service() INIZIO");

		// Ottengo la queryString (ma senza parametro PAGE)
		String queryString = QueryString.getFromServiceRequest(request, false);

		String pageKey = Constants.PAGE;
		String pageValue = (String) request.getAttribute(pageKey);
		if (StringUtils.isEmpty(pageValue)) {
			pageKey = Constants.ACTION_NAME;
			pageValue = (String) request.getAttribute(pageKey);
		}
		_logger.debug("Riconosciuto " + pageKey + "=[" + pageValue + "]");

		if (StringUtils.isEmpty(pageValue)) {
			_logger.debug("ERRORE: NON TROVO NE' IL PAGE NE' L'ACTION DI LANCIO!");

		}

		// Se la PAGE/ACTION comincia con "Jump2", si rimuove tale prefisso
		// (ossia per default si andrebbe nella Page data dal resto del nome.
		if (pageValue.startsWith(PREFIX_JUMP2) || pageValue.toLowerCase().startsWith(PREFIX_JUMP2.toLowerCase())) {
			pageValue = pageValue.substring(PREFIX_JUMP2.length()); // tolgo
																	// parte
																	// iniziale
																	// (fissa)
		}

		// CLEARING: SVUOTO LA QUERYSTRING (UTILE PER CREARNE UNA DA ZERO)
		// ------------
		String jump2clear = (String) request.getAttribute(PARAM_jump2clear);
		if (StringUtils.isFilled(jump2clear)) {
			_logger.debug("- " + PARAM_jump2clear + "=[" + jump2clear + "]");

			queryString = "";
		}

		// AGGIUNGO DEI NUOVI PARAMETRI
		// --------------------------------------------------
		// NULLA DA FARE - tutto quento dato come PARAMETER nelle CONSEQUENCE
		// sarà già nella REQUEST di questo modulo e quindi verrà usato per
		// comporre il NUOVO URL!

		// ALIASING: POSSO ANCHE MODIFICARE LA PAGE VERA E PROPRIA DA ESEGUIRE
		// ------------
		String jump2page = (String) request.getAttribute(PARAM_jump2page);

		if (StringUtils.isFilled(jump2page)) {
			_logger.debug("- " + PARAM_jump2page + "=[" + jump2page + "]");

			pageValue = jump2page;
			// nota: è un parametro "mio": non lo riporto nell'url generato
			queryString = QueryString.removeParameter(queryString, PARAM_jump2page);
		}

		// CREAZIONE DEL NUOVO URL TRAMITE QUERYSTRING MODIFICATA!!!
		// -------------------
		String jump2url = "../../servlet/fv/AdapterHTTP?" + pageKey + "=" + pageValue + "&" + queryString;

		_logger.debug("==> " + ATTRIB_jump2url + "=[" + jump2url + "]");

		// E SUO INSERIMENTO IN RESPONSE DEL MODULO.
		try {
			response.setAttribute(ATTRIB_jump2url, jump2url);
		} catch (SourceBeanException e) {
			_logger.debug(className + " - response.setAttribute ERRORE!");

		}

		_logger.debug(className + ".service() FINE");

	}

}