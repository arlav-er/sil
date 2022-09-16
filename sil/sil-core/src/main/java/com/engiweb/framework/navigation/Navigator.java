package com.engiweb.framework.navigation;

import java.util.ArrayList;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * La classe <code>Navigator</code> implementa i servizi per la gestione della navigazione tra le diverse richieste
 * effettuate nell'ambito della stessa sessione. Il contenitore <code>RequestContainer</code> contiene tutti i parametri
 * di tutte le richieste di servizio e il riferimento a <code>SessionContainer</code>. I metodi statici della classe
 * <code>Navigator</code> operano su istanze di <code>RequestContainer</code> per poter gestire la navigazione.
 * 
 * @author Luigi Bellio
 * @see RequestContainer
 */
public abstract class Navigator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Navigator.class.getName());
	public static final String NAVIGATOR_RESET = "NAVIGATOR_RESET";
	public static final String NAVIGATOR_DISABLED = "NAVIGATOR_DISABLED";
	public static final String NAVIGATOR_RELOAD = "NAVIGATOR_RELOAD";
	public static final String NAVIGATOR_BACK = "NAVIGATOR_BACK";
	public static final String NAVIGATOR_FORCE_BACK = "NAVIGATOR_FORCE_BACK";
	public static final String NAVIGATOR_BACK_TO = "NAVIGATOR_BACK_TO";
	public static final String NAVIGATOR_BACK_TO_MARK = "NAVIGATOR_BACK_TO_MARK";
	public static final String NAVIGATOR_MARK = "NAVIGATOR_MARK";
	public static final String NAVIGATOR_FREEZE = "NAVIGATOR_FREEZE";
	// Modifica Monica 26/01/2004 - inizio
	public static final String NAVIGATOR_SERVICE = "NAVIGATOR_SERVICE";
	public static final String NAVIGATOR_SERVICE_ALIAS = "NAVIGATOR_SERVICE_ALIAS";
	public static final String NAVIGATOR_BACK_TO_SERVICE_LABEL = "NAVIGATOR_BACK_TO_SERVICE_LABEL";
	public static final String NAVIGATOR_BACK_TO_SERVICE_ID = "NAVIGATOR_BACK_TO_SERVICE_ID";

	// Modifica Monica 26/01/2004 - fine

	/**
	 * Il navigatore può essere abilitato o disabilitato configurandolo da file XML. Questo metodo legge il file XML di
	 * configurazione e ritorna lo stato del navigatore.
	 * 
	 * @return <code>boolean</code> stato del navigatore.
	 */
	public static boolean isNavigatorEnabled() {
		String isNavigatorEnabled = (String) ConfigSingleton.getInstance().getAttribute("COMMON.NAVIGATOR_ENABLED");
		if ((isNavigatorEnabled != null) && isNavigatorEnabled.equalsIgnoreCase("TRUE"))
			return true;
		return false;
	} // public static boolean isNavigatorEnabled()

	/**
	 * &egrave; possibile cancellare la storia delle richieste inserendo nella richiesta del servizio il parametro
	 * <code>NAVIGATOR_RESET</code>. Questo metodo cerca tra i parametri della richiesta l'attributo
	 * <code>NAVIGATOR_RESET</code>.
	 * 
	 * @return <code>boolean</code> indica la presenza del parametro di reset.
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 */
	private static boolean navigatorReset(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return false;
		String navigatorReset = (String) serviceRequest.getAttribute(NAVIGATOR_RESET);
		if (navigatorReset != null) {
			_logger.debug("Navigator::navigatorReset: navigatorReset [" + navigatorReset + "]");

			if (navigatorReset.equalsIgnoreCase("TRUE"))
				return true;
		} // if (navigatorReset != null)
		return false;
	} // private static boolean navigatorReset(SourceBean serviceRequest)

	/**
	 * &egrave; possibile disabilitare il navigatore per una specifica richiesta di servizio, questo si attua inserendo
	 * tra i parametri della richiesta l'attributo <code>NAVIGATOR_DISABLED</code> Questo metodo cerca tra i parametri
	 * della richiesta l'attributo <code>NAVIGATOR_DISABLED</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>boolean</code> indica la presenza del parametro di disabilitazione.
	 */
	private static boolean navigatorDisabled(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return false;
		String navigatorDisabled = (String) serviceRequest.getAttribute(NAVIGATOR_DISABLED);
		if (navigatorDisabled != null) {
			_logger.debug("Navigator::navigatorDisabled: navigatorDisabled [" + navigatorDisabled + "]");

			if (navigatorDisabled.equalsIgnoreCase("TRUE"))
				return true;
		} // if (navigatorDisabled != null)
		return false;
	} // private static boolean navigatorDisabled(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare riproporre l'ultima richiesta inserendo tra i parametri l'attributo
	 * <code>NAVIGATOR_RELOAD</code> .
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>boolean</code> indica la presenza del parametro di reload.
	 */
	private static boolean navigatorReload(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return false;
		String navigatorReload = (String) serviceRequest.getAttribute(NAVIGATOR_RELOAD);
		if (navigatorReload != null) {
			_logger.debug("Navigator::navigatorReload: navigatorReload [" + navigatorReload + "]");

			if (navigatorReload.equalsIgnoreCase("TRUE"))
				return true;
		} // if (navigatorReload != null)
		return false;
	} // private static boolean navigatorReload(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri della richiesta
	 * l'attributo <code>NAVIGATOR_BACK</code> specificando il numero di steps.In questo caso non vengono considerate le
	 * richieste che erano state disabilitate. Questo metodo cerca tra i parametri della richiesta l'attributo
	 * <code>NAVIGATOR_BACK</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>int</code> il numero di steps.
	 */
	private static int navigatorBack(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return 0;
		int navigatorBackSteps = 0;
		String navigatorBack = (String) serviceRequest.getAttribute(NAVIGATOR_BACK);
		if (navigatorBack != null) {
			_logger.debug("Navigator::navigatorBack: navigatorBack [" + navigatorBack + "]");

			navigatorBackSteps = 1;
			try {
				navigatorBackSteps = Integer.parseInt(navigatorBack);
				if (navigatorBackSteps < 0)
					navigatorBackSteps = 0;
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"Navigator::navigatorBack: (Integer.valueOf(navigatorBack)).parseInt()", ex);

			} // catch (Exception ex) try
		} // if (navigatorBack != null)
		return navigatorBackSteps;
	} // private static int navigatorBack(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri della richiesta
	 * l'attributo <code>NAVIGATOR_FORCE_BACK</code> specificando il numero di steps. In questo caso vengono considerate
	 * anche le richieste che erano state disabilitate. Questo metodo cerca tra i parametri della richiesta l'attributo
	 * <code>NAVIGATOR_FORCE_BACK</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>int</code> il numero di steps.
	 */
	private static int navigatorForceBack(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return 0;
		int navigatorForceBackSteps = 0;
		String navigatorForceBack = (String) serviceRequest.getAttribute(NAVIGATOR_FORCE_BACK);
		if (navigatorForceBack != null) {
			_logger.debug("Navigator::navigatorForceBack: navigatorForceBack [" + navigatorForceBack + "]");

			navigatorForceBackSteps = 1;
			try {
				navigatorForceBackSteps = Integer.parseInt(navigatorForceBack);
				if (navigatorForceBackSteps < 0)
					navigatorForceBackSteps = 0;
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"Navigator::navigatorForceBack: (Integer.valueOf(navigatorForceBack)).parseInt()", ex);

			} // catch (Exception ex) try
		} // if (navigatorForceBack != null)
		return navigatorForceBackSteps;
	} // private static int navigatorForceBack(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri l'attributo
	 * <code>NAVIGATOR_BACK_TO</code> specificando il nome logico della richiesta a cui tornare. Questo metodo cerca tra
	 * i parametri della richiesta l'attributo <code>NAVIGATOR_BACK_TO</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>String</code> il nome logico della richiesta.
	 */
	private static String navigatorBackTo(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return null;
		String navigatorBackTo = (String) serviceRequest.getAttribute(NAVIGATOR_BACK_TO);
		if (navigatorBackTo != null)
			_logger.debug("Navigator::navigatorBackTo: navigatorBackTo [" + navigatorBackTo + "]");

		return navigatorBackTo;
	} // private static String navigatorBackTo(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri l'attributo
	 * <code>NAVIGATOR_BACK_TO_MARK</code> specificando il numero di steps .In questo caso verranno considerate solo le
	 * richieste che precedentemente erano state marcate. Questo metodo cerca tra i parametri della richiesta
	 * l'attributo <code>NAVIGATOR_BACK_TO_MARK</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>int</code> il numero di steps.
	 */
	private static int navigatorBackToMark(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return 0;
		int navigatorBackToMarkSteps = 0;
		String navigatorBackToMark = (String) serviceRequest.getAttribute(NAVIGATOR_BACK_TO_MARK);
		if (navigatorBackToMark != null) {
			_logger.debug("Navigator::navigatorBackToMark: navigatorBackToMark [" + navigatorBackToMark + "]");

			navigatorBackToMarkSteps = 1;
			try {
				navigatorBackToMarkSteps = Integer.parseInt(navigatorBackToMark);
				if (navigatorBackToMarkSteps < 0)
					navigatorBackToMarkSteps = 0;
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"Navigator::navigatorBackToMark: (Integer.valueOf(navigatorBackToMark)).parseInt()", ex);

			} // catch (Exception ex) try
		} // if (navigatorBackToMark != null)
		return navigatorBackToMarkSteps;
	} // private static int navigatorBackToMark(SourceBean serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri l'attributo
	 * <code>NAVIGATOR_BACK_TO_SERVICE_LABEL</code> specificando l'id del servizio a cui tornare.In questo caso verranno
	 * considerate solo le richieste che precedentemente erano state marcate. Questo metodo cerca tra i parametri della
	 * richiesta l'attributo <code>NAVIGATOR_BACK_TO_SERVICE_LABEL</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>String</code> il valore dell'id.
	 */
	private static String navigatorBackToServiceLabel(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return null;
		String navigatorBackToServiceLabel = (String) serviceRequest.getAttribute(NAVIGATOR_BACK_TO_SERVICE_LABEL);
		if (navigatorBackToServiceLabel != null && !navigatorBackToServiceLabel.equals("")) {
			_logger.debug("Navigator::navigatorBackToService: navigatorBackToServiceLabel ["
					+ navigatorBackToServiceLabel + "]");

		} // if (navigatorBackToServiceLabel != null &&
			// !navigatorBackToServiceLabel.equals(""))
		else
			return null;
		return navigatorBackToServiceLabel;
	} // private static String navigatorBackToServiceLabel(SourceBean
		// serviceRequest)

	/**
	 * &egrave; possibile navigare a ritroso tra le varie richieste effettuate inserendo tra i parametri l'attributo
	 * <code>NAVIGATOR_BACK_TO_SERVICE_ID</code> specificando l'id del servizio a cui tornare.In questo caso verranno
	 * considerate solo le richieste che precedentemente erano state marcate. Questo metodo cerca tra i parametri della
	 * richiesta l'attributo <code>NAVIGATOR_BACK_TO_SERVICE_ID</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>String</code> il valore dell'id.
	 */
	private static String navigatorBackToServiceId(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return null;
		String navigatorBackToServiceId = (String) serviceRequest.getAttribute(NAVIGATOR_BACK_TO_SERVICE_ID);
		if (navigatorBackToServiceId != null && !navigatorBackToServiceId.equals("")) {
			_logger.debug(
					"Navigator::navigatorBackToService: navigatorBackToServiceId [" + navigatorBackToServiceId + "]");

		} // if (navigatorBackToServiceId != null &&
			// !navigatorBackToServiceId.equals(""))
		else
			return null;
		return navigatorBackToServiceId;
	} // private static String navigatorBackToServiceId(SourceBean
		// serviceRequest)

	/**
	 * &egrave; possibile congelare il navigatore per una specifica richiesta di servizio, questo si attua inserendo tra
	 * i parametri della richiesta l'attributo <code>NAVIGATOR_FREEZE</code> Questo metodo cerca tra i parametri della
	 * richiesta l'attributo <code>NAVIGATOR_FREEZE</code>.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 * @return <code>boolean</code> indica la presenza del parametro di disabilitazione.
	 */
	private static boolean navigatorFreeze(SourceBean serviceRequest) {
		if (serviceRequest == null)
			return false;
		String navigatorFreeze = (String) serviceRequest.getAttribute(NAVIGATOR_FREEZE);
		if (navigatorFreeze != null) {
			_logger.debug("Navigator::navigatorFreeze: navigatorFreeze [" + navigatorFreeze + "]");

			if (navigatorFreeze.equalsIgnoreCase("TRUE"))
				return true;
		} // if (navigatorFreeze != null)
		return false;
	} // private static boolean navigatorFreeze(SourceBean serviceRequest)

	/**
	 * Questo metodo cancella dalla richiesta tutti i comandi di navigazione.
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> contenente i parametri della richiesta.
	 */
	private static void removeNavigationCommands(SourceBean serviceRequest) {
		try {
			serviceRequest.delAttribute(NAVIGATOR_RESET);
			serviceRequest.delAttribute(NAVIGATOR_DISABLED);
			serviceRequest.delAttribute(NAVIGATOR_RELOAD);
			serviceRequest.delAttribute(NAVIGATOR_BACK);
			serviceRequest.delAttribute(NAVIGATOR_FORCE_BACK);
			serviceRequest.delAttribute(NAVIGATOR_BACK_TO);
			serviceRequest.delAttribute(NAVIGATOR_BACK_TO_MARK);
			serviceRequest.delAttribute(NAVIGATOR_FREEZE);
			serviceRequest.delAttribute(NAVIGATOR_BACK_TO_SERVICE_LABEL);
			serviceRequest.delAttribute(NAVIGATOR_BACK_TO_SERVICE_ID);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"Navigator::removeNavigationCommands: serviceRequest.delAttribute(...)", ex);

		} // catch (SourceBeanException ex)
	} // private static void removeNavigationCommands(SourceBean
		// serviceRequest)

	/**
	 * Questo metodo sulla base dell'esistenza dei comandi di navigazione è in grado di modificare l'istanza di
	 * <code>RequestContainer</code> riproponendo lo stato di una richiesta effettuata precedentemente.
	 * 
	 * @param request
	 *            <code>RequestContainer</code> istanza per la sessione in uso.
	 */
	public static void checkNavigation(RequestContainer request) throws NavigationException {
		_logger.debug("Navigator::checkNavigation: invocato");

		if (!isNavigatorEnabled()) {
			_logger.debug("Navigator::checkNavigation: navigazione applicativa disabilitata !");

			return;
		} // if (!isNavigatorEnabled())
		SourceBean serviceRequest = request.getServiceRequest();
		if (navigatorReset(serviceRequest)) {
			request.delParent();
			// request.updServiceRequest(clonedServiceRequest);
			return;
		} // if (navigatorReset(serviceRequest))
		SourceBean clonedServiceRequest = (SourceBean) (serviceRequest.cloneObject());
		removeNavigationCommands(clonedServiceRequest);
		it.eng.sil.util.TraceWrapper.debug(_logger,
				"Navigator::checkNavigation: serviceRequest dopo removeNavigationCommands", clonedServiceRequest);

		if (navigatorReload(serviceRequest)) {
			RequestContainer container = request;
			RequestContainer parent = (RequestContainer) container.getParent();
			SourceBean parentServiceRequest = null;
			if (parent != null)
				parentServiceRequest = parent.getServiceRequest();
			while ((parent != null) && navigatorDisabled(parentServiceRequest)) {
				container = parent;
				parent = (RequestContainer) container.getParent();
				if (parent != null)
					parentServiceRequest = parent.getServiceRequest();
			} // while (parent != null)
			if (parent == null) {
				_logger.fatal("Navigator::checkNavigation: [RELOAD] parent nullo");

				throw new NavigationException("Navigator::checkNavigation: [RELOAD] parent nullo");
			} // if (parent == null)
			parent.updRequestContext(request);
			request.setContainer(parent);
			request.updServiceRequest(clonedServiceRequest);
			return;
		} // if (navigatorReload(serviceRequest))
		int navigatorBackSteps = navigatorBack(serviceRequest);
		if (navigatorBackSteps > 0) {
			RequestContainer container = request;
			RequestContainer parent = (RequestContainer) container.getParent();
			int index = 0;
			while ((parent != null) && (index < (navigatorBackSteps + 1))) {
				SourceBean parentServiceRequest = parent.getServiceRequest();
				if (!navigatorDisabled(parentServiceRequest))
					index++;
				container = parent;
				parent = (RequestContainer) container.getParent();
			} // while ((parent != null) && (index < (navigatorBackSteps +
				// 1)))
			if (index < (navigatorBackSteps + 1)) {
				_logger.fatal("Navigator::checkNavigation: [BACK] parent nullo");

				throw new NavigationException("Navigator::checkNavigation: [BACK] parent nullo");
			} // if (index < (navigatorBackSteps + 1))
			container.updRequestContext(request);
			request.setContainer(container);
			request.updServiceRequest(clonedServiceRequest);
			return;
		} // if (navigatorBackSteps > 0)
		int navigatorForceBackSteps = navigatorForceBack(serviceRequest);
		if (navigatorForceBackSteps > 0) {
			RequestContainer container = request;
			RequestContainer parent = (RequestContainer) container.getParent();
			int index = 0;
			while ((parent != null) && (index < (navigatorForceBackSteps + 1))) {
				index++;
				container = parent;
				parent = (RequestContainer) container.getParent();
			} // while ((parent != null) && (index < (navigatorBackSteps +
				// 1)))
			if (container == null) {
				_logger.fatal("Navigator::checkNavigation: [FORCE_BACK] container nullo");

				throw new NavigationException("Navigator::checkNavigation: [FORCE_BACK] container nullo");
			} // if (container == null)
			container.updRequestContext(request);
			request.setContainer(container);
			request.updServiceRequest(clonedServiceRequest);
			return;
		} // if (navigatorForceBackSteps > 0)
		String navigatorBackTo = navigatorBackTo(serviceRequest);
		if (navigatorBackTo != null) {
			RequestContainer container = request;
			RequestContainer parent = (RequestContainer) container.getParent();
			boolean serviceFound = false;
			while (parent != null) {
				SourceBean localServiceRequestBean = parent.getServiceRequest();
				if (localServiceRequestBean == null) {
					_logger.fatal("Navigator::checkNavigation: [BACK_TO] localServiceRequestBean nullo");

					return;
				} // if (localServiceRequestBean == null)
				String actionName = (String) localServiceRequestBean.getAttribute(Constants.ACTION_NAME);
				_logger.debug("Navigator::checkNavigation: [BACK_TO] actionName [" + actionName + "]");

				if (navigatorBackTo.equalsIgnoreCase(actionName))
					serviceFound = true;
				else {
					String pageName = (String) localServiceRequestBean.getAttribute(Constants.PAGE);
					_logger.debug("Navigator::checkNavigation: [BACK_TO] pageName [" + pageName + "]");

					if (navigatorBackTo.equalsIgnoreCase(pageName))
						serviceFound = true;
					else if (serviceFound) {
						container.updRequestContext(request);
						request.setContainer(container);
						request.updServiceRequest(clonedServiceRequest);
						return;
					} // if (actionFound)
				} // if (navigatorBackTo.equalsIgnoreCase(actionName)) else
				_logger.debug("Navigator::checkNavigation: [BACK_TO] serviceFound [" + serviceFound + "]");

				container = parent;
				parent = (RequestContainer) container.getParent();
			} // while (parent != null)
			if (serviceFound) {
				container.updRequestContext(request);
				request.setContainer(container);
				request.updServiceRequest(clonedServiceRequest);
				return;
			} // if (serviceFound)
			_logger.warn("Navigator::checkNavigation: [BACK_TO] servizio non trovato");

			throw new NavigationException("Navigator::checkNavigation: [BACK_TO] servizio non trovato");
		} // if (navigatorBackTo != null)
		int navigatorBackToMarkSteps = navigatorBackToMark(serviceRequest);
		if (navigatorBackToMarkSteps > 0) {
			RequestContainer container = (RequestContainer) request.getParent();
			if (container != null)
				container = (RequestContainer) container.getParent();
			int found = 0;
			while (container != null) {
				String signed = (String) container.getAttribute(NAVIGATOR_MARK);
				if ((signed != null) && signed.equalsIgnoreCase("TRUE")) {
					found++;
					if (found == navigatorBackToMarkSteps) {
						_logger.debug("Navigator::checkNavigation: [BACK_TO_MARK] action trovata");

						container.updRequestContext(request);
						request.setContainer(container);
						request.updServiceRequest(clonedServiceRequest);
						return;
					} // if (found == navigatorBackToMarkSteps)
				} // if ((signed != null) && signed.equalsIgnoreCase("TRUE"))
				container = (RequestContainer) container.getParent();
			} // while (container != null)
			_logger.fatal("Navigator::checkNavigation: [BACK_TO_MARK] action non trovata");

			throw new NavigationException("Navigator::checkNavigation: [BACK_TO_MARK] action non trovata");
		} // if (navigatorBackToMarkSteps > 0)
			// Modifica Monica 26/01/2004 - inizio
		String navigatorBackToServiceLabel = navigatorBackToServiceLabel(serviceRequest);
		if (navigatorBackToServiceLabel != null) {
			RequestContainer container = (RequestContainer) request.getParent();
			while (container != null) {
				LabelID labelID = (LabelID) container.getAttribute(NAVIGATOR_SERVICE);
				if (labelID != null) {
					ArrayList navigatorServiceAlias = (ArrayList) request.getAttribute(NAVIGATOR_SERVICE_ALIAS);
					if ((navigatorServiceAlias == null) || (navigatorServiceAlias.size() == 0))
						navigatorServiceAlias = new ArrayList();
					else
						navigatorServiceAlias = new ArrayList(navigatorServiceAlias);
					navigatorServiceAlias.add(labelID);
					for (int i = 0; i < navigatorServiceAlias.size(); i++) {
						labelID = (LabelID) navigatorServiceAlias.get(i);
						if ((labelID != null) && (labelID.getLabel() != null)
								&& (labelID.getLabel()).equalsIgnoreCase(navigatorBackToServiceLabel)) {
							_logger.debug("Navigator::checkNavigation: [BACK_TO_SERVICE_LABEL] servizio "
									+ navigatorBackToServiceLabel + " trovato");

							container.updRequestContext(request);
							request.setContainer(container);
							request.updServiceRequest(clonedServiceRequest);
							return;
						} // if ((labelID != null) && (labelID.getLabel() !=
							// null) &&
							// (labelID.getLabel()).equalsIgnoreCase(navigatorBackToServiceLabel))
					} // for (int i = 0; i < navigatorServiceAlias(); i++)
				} // if (labelID != null)
				container = (RequestContainer) container.getParent();
			} // while (container != null)
			_logger.fatal("Navigator::checkNavigation: [BACK_TO_SERVICE_LABEL] servizio " + navigatorBackToServiceLabel
					+ " non trovato");

			throw new NavigationException("Navigator::checkNavigation: [BACK_TO_SERVICE_LABEL] servizio "
					+ navigatorBackToServiceLabel + " non trovato");
		} // if (navigatorBackToServiceLabel != null)
		String navigatorBackToServiceId = navigatorBackToServiceId(serviceRequest);
		if (navigatorBackToServiceId != null) {
			RequestContainer container = (RequestContainer) request.getParent();
			while (container != null) {
				LabelID labelID = (LabelID) container.getAttribute(NAVIGATOR_SERVICE);
				if (labelID != null) {
					ArrayList navigatorServiceAlias = (ArrayList) request.getAttribute(NAVIGATOR_SERVICE_ALIAS);
					if ((navigatorServiceAlias == null) || (navigatorServiceAlias.size() == 0))
						navigatorServiceAlias = new ArrayList();
					else
						navigatorServiceAlias = new ArrayList(navigatorServiceAlias);
					navigatorServiceAlias.add(labelID);
					for (int i = 0; i < navigatorServiceAlias.size(); i++) {
						labelID = (LabelID) navigatorServiceAlias.get(i);
						if ((labelID != null) && (labelID.getId() != null)
								&& (labelID.getId()).equalsIgnoreCase(navigatorBackToServiceId)) {
							_logger.debug("Navigator::checkNavigation: [BACK_TO_SERVICE_ID] servizio "
									+ navigatorBackToServiceId + " trovato");

							container.updRequestContext(request);
							request.setContainer(container);
							request.updServiceRequest(clonedServiceRequest);
							return;
						} // if ((labelID != null) && (labelID.getLabel() !=
							// null) &&
							// (labelID.getLabel()).equalsIgnoreCase(navigatorBackToServiceLabel))
					} // for (int i = 0; i < navigatorServiceAlias(); i++)
				} // if (labelID != null)
				container = (RequestContainer) container.getParent();
			} // while (container != null)
			_logger.fatal("Navigator::checkNavigation: [BACK_TO_SERVICE_ID] servizio " + navigatorBackToServiceId
					+ " non trovato");

			throw new NavigationException("Navigator::checkNavigation: [BACK_TO_SERVICE_ID] servizio "
					+ navigatorBackToServiceId + " non trovato");
		} // if (navigatorBackToServiceId != null)
			// Modifica Monica 26/01/2004 - fine
		if (navigatorFreeze(serviceRequest)) {
			RequestContainer container = request;
			RequestContainer parent = (RequestContainer) container.getParent();
			if (parent == null)
				return;
			parent.setServiceRequest(serviceRequest);
			parent.updRequestContext(request);
			request.setContainer(parent);
			return;
		} // if (navigatorFreeze(serviceRequest))
		_logger.debug("Navigator::checkNavigation: nessun controllo di navigazione");

	} // public static void checkNavigation(RequestContainer request)

	/**
	 * Questo metodo permette di marcare la richiesta per poterla eventualmente riproporre utilizzando l'opportuno
	 * parametro <code>NAVIGATOR_BACK_TO_MARK</code>.
	 * 
	 * @param request
	 *            istanza per la sessione in uso.
	 * @see RequestContainer
	 */
	public static void signService(RequestContainer request) {
		_logger.debug("Navigator::signService: invocato");

		if (request == null)
			return;
		SourceBean serviceRequest = request.getServiceRequest();
		if (serviceRequest == null)
			return;
		if (navigatorDisabled(serviceRequest)) {
			_logger.fatal("Navigator::signService: non è possibile marcare un servizio disabilitato");

			return;
		} // if (navigatorDisabled(serviceRequest))
		request.setAttribute(NAVIGATOR_MARK, "TRUE");
	} // public static void signService(RequestContainer request)

	/**
	 * Questo metodo permette di marcare la richiesta per poterla eventualmente riproporre utilizzando l'opportuno
	 * parametro <code>NAVIGATOR_BACK_TO_SERVICE</code>.
	 * 
	 * @param request
	 *            istanza per la sessione in uso.
	 * @param serviceLabel
	 *            stringa che contiene il valore della label, attribuito alla costante <code>NAVIGATOR_SERVICE</code>.
	 * @see RequestContainer
	 */
	// Modifica Monica 26/01/2004 - inizio
	public static void signService(RequestContainer request, String serviceLabel) {
		_logger.debug("Navigator::signService: invocato");

		if (request == null)
			return;
		SourceBean serviceRequest = request.getServiceRequest();
		if (serviceRequest == null)
			return;
		if (navigatorDisabled(serviceRequest)) {
			_logger.fatal("Navigator::signService: non è possibile marcare un servizio disabilitata");

			return;
		} // if (navigatorDisabled(serviceRequest))
			// gestione id
			// costruisco oggetto LabelID
		LabelID labelID = new LabelID();
		labelID.setLabel(serviceLabel);
		labelID.setId(String.valueOf(System.currentTimeMillis()));
		// assegno al valore di NAVIGATOR_SERVICE l'oggetto labelID
		LabelID currentLabelID = (LabelID) request.getAttribute(NAVIGATOR_SERVICE);
		if (currentLabelID == null)
			request.setAttribute(NAVIGATOR_SERVICE, labelID);
		else {
			ArrayList navigatorServiceAlias = (ArrayList) request.getAttribute(NAVIGATOR_SERVICE_ALIAS);
			if (navigatorServiceAlias == null)
				navigatorServiceAlias = new ArrayList();
			navigatorServiceAlias.add(labelID);
			request.setAttribute(NAVIGATOR_SERVICE_ALIAS, navigatorServiceAlias);
		} // if (currentLabelID == null)
	} // public static void signService(RequestContainer request)

	// Modifica Monica 26/01/2004 - fine
	/**
	 * Questo metodo permette di eliminare la marcare la richiesta per poterla eventualmente riproporre utilizzando
	 * l'opportuno parametro <code>NAVIGATOR_BACK_TO_MARK</code>.
	 * 
	 * @param request
	 *            istanza per la sessione in uso.
	 * @see RequestContainer
	 */
	public static void unsignService(RequestContainer request) {
		_logger.debug("Navigator::unsignService: invocato");

		if (request == null)
			return;
		SourceBean serviceRequest = request.getServiceRequest();
		if (serviceRequest == null)
			return;
		if (navigatorDisabled(serviceRequest)) {
			_logger.fatal("Navigator::unsignService: non è possibile smarcare una action disabilitata");

			return;
		} // if (navigatorDisabled(serviceRequest))
		request.setAttribute(NAVIGATOR_MARK, "FALSE");
	} // public static void unsignService(RequestContainer request)
} // public abstract class Navigator
