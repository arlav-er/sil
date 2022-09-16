package it.eng.sil.security;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.util.JavaScript;

import it.eng.sil.Values;

/**
 * @author Franco Vuoto Classe utilizzata per caricare gli attributi di una pagina in relazione al profilo dell'utente
 *         che ha acceduto al sistema. Per attribbuto si intende un bottone (SALVA, NUOVO, STAMPA) o un collegamento
 *         ipertestuale.
 */
public class PageAttribs {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PageAttribs.class.getName());
	private List links = new ArrayList();
	private List sezioni = new ArrayList();
	private List goToes = new ArrayList(1);
	private Set bottoni = new HashSet();
	private String page;
	private User user = null;

	/**
	 * Unico costruttore.
	 * 
	 * @param user
	 *            Description of the Parameter
	 * @param _page
	 *            Description of the Parameter
	 */
	public PageAttribs(User user, String _page) {
		this.page = _page.toUpperCase();
		this.user = user;
		caricaAttribs();
	}

	/**
	 * Non ammesso
	 */
	private PageAttribs() {
	}

	/**
	 * Restituisce la lista delle sezioni della pagina
	 */
	public List getSectionList() {
		return sezioni;
	}

	/**
	 * Description of the Method
	 * 
	 * @param buttonName
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean containsButton(String buttonName) {
		return bottoni.contains(buttonName.trim().toUpperCase());
	}

	/**
	 * Fornisce il dump dell'oggetto, utile in fase di debug
	 * 
	 * @return Dump dell'oggetto
	 * @author Corrado Vaccari
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ User [");
		sb.append(this.user.toString());
		sb.append("] Page [");
		sb.append(this.page);
		sb.append("] Buttons [");
		sb.append(this.bottoni);
		sb.append("] Links [");
		sb.append(this.links);
		sb.append("] Goto [");
		sb.append(this.goToes);
		sb.append("] Sezioni [");
		sb.append(this.sezioni);
		sb.append("] }");

		return sb.toString();
	}

	/**
	 * Description of the Method
	 */
	private void caricaAttribs() {
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		StringBuffer buf = new StringBuffer();

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_ATTRIBUTI");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.page));

			inputParameter
					.add(dataConnection.createDataField("", Types.NUMERIC, new Integer(this.user.getCdnProfilo())));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");

			for (int i = 0; i < righe.size(); i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				String tipo = (String) riga.getAttribute("TIPO_ATTRIBUTO");

				switch (tipo.charAt(0)) {
				case 'B': {
					// BOTTONE
					String azione = (String) riga.getAttribute("AZIONE");
					// String targetPage = (String)
					// riga.getAttribute("PAGE_TARGET");
					bottoni.add(azione.trim().toUpperCase());
				}

					break;

				case 'S': {
					// SEZIONE
					String azione = (String) riga.getAttribute("AZIONE");
					sezioni.add(azione.trim().toUpperCase());
				}

					break;

				case 'L': {
					// COLLEGAMENTO

					// Estraggo dalla riga i dati di cui ho bisogno
					String denominazione = (String) riga.getAttribute("strdenominazione");
					String targetpage = (String) riga.getAttribute("PAGE_TARGET");

					BigDecimal targetAttrib = (BigDecimal) riga.getAttribute("cdnfunzione_TARGET");

					String uri = (String) riga.getAttribute("URI");

					// Li inserisco in un nuovo oggetto HyperLink usando
					// il costruttore di default.
					HyperLink hl = new HyperLink(denominazione, targetpage, targetAttrib, uri);

					// Aggiungo l'HyperLink alla lista.
					links.add(hl);
				}

					break;

				case 'G': {
					// GOTO: salto incondizionato ad altra funzione-componente

					// Estraggo dalla riga i dati di cui ho bisogno
					// String denominazione = (String)
					// riga.getAttribute("strdenominazione");
					String targetpage = (String) riga.getAttribute("PAGE_TARGET");

					BigDecimal targetAttrib = (BigDecimal) riga.getAttribute("cdnfunzione_TARGET");

					// String uri = (String) riga.getAttribute("URI");

					// Li inserisco in un nuovo oggetto HyperLink usando
					// il costruttore di default.
					GoTo goTo = new GoTo(targetpage, targetAttrib);

					// Aggiungo l'HyperLink alla lista.
					goToes.add(goTo);
				}

					break;

				case 'C':
					// Attributo di tipo colonna
					// da non fare niente
					break;

				default:

					// Errore
					_logger.fatal("PageAttribs::caricaAttribs - Tipo attributo '" + tipo.charAt(0) + "' non valido");

					break;
				}
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
	}

	/**
	 * Restituisce il Javascript che genera i link del footer e il bottone del ritorno
	 */
	public void showHyperLinks(JspWriter out, RequestContainer requestContainer, ResponseContainer responseContainer,
			String specificAttribs) throws IOException {

		// Recupero oggetti del framework
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean serviceRequest = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		Stack backstack = (Stack) sessionContainer.getAttribute("BackStack");

		boolean premutoRitorno = serviceRequest.containsAttribute("IS_RET_JUMP");
		boolean saltoDaFooter = serviceRequest.containsAttribute("IS_FOOT_JUMP");
		// Se il backstack non esiste lo crea
		if (backstack == null) {
			backstack = new Stack();
			sessionContainer.setAttribute("BackStack", backstack);
		}

		// Gestione dello stack

		if (!saltoDaFooter && !backstack.empty()) {
			// Levo un link memorizzato inutilmente
			backstack.pop();
		}

		if (premutoRitorno && !backstack.empty()) {
			// Levo il link a cui sto tornando
			backstack.pop();
		}

		// Genero i link
		if (links != null) {

			out.println("function mostraHyperLink(){");
			// Creo la linea di Javascript necessaria all'inserimento dei links
			out.print("window.top.footer.document.getElementById('sezLinks')" + ".innerHTML=\"");

			// Itero sulla lista di links da creare
			Iterator iter = links.iterator();
			while (iter.hasNext()) {
				HyperLink link = (HyperLink) iter.next();

				// Stabilisco se il link è esterno o interno
				String href;
				String boldopen = "";
				String boldclose = "";
				if (link.isExternal()) {
					// Link esterno
					href = link.getHref();
				} else {
					// Link interno, aggiungo gli attributi necessari
					href = "AdapterHTTP?PAGE=" + link.getHref() + "&CDNFUNZIONE=" + link.getTargetattrib().toString()
							+ "&IS_FOOT_JUMP=Y";
					if (!(specificAttribs.equals("") || specificAttribs == null)) {
						href = href + "&" + specificAttribs;
					}
					boldopen = "<b>";
					boldclose = "</b>";
				}

				// Creo il codice per ogni link
				out.print("<a href='" + href + "'" + " target='" + link.getTarget() + "'>" + boldopen
						+ link.getDescrizione() + boldclose + "</a>&nbsp;&nbsp;");
			}
			// Chiudo la linea javascript
			out.println("\";");
			out.println("}");
		}

		// Se lo stack non è vuoto faccio una peek e creo il link di back
		if (!backstack.empty()) {
			HyperLink back = (HyperLink) backstack.peek();
			out.println("function mostraBackLink(){" + "window.top.footer.document.getElementById('sezBack')"
					+ ".innerHTML=\"<a href='" + back.getHref() + "' target='" + back.getTarget()
					+ "'><img src='../../img/ritorna.gif'> " + back.getDescrizione() + "</a>\"};");
		}

		// Aggiungo la pagina precedente nel backstack

		String httpQueryString = (String) requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");

		serviceRequest = (SourceBean) requestContainer.getServiceRequest();
		String targetpage = null;

		// Se si viene da una POST non va bene...
		if ((httpQueryString == null) || (httpQueryString.equals(""))) {
			// SourceBean serviceRequest= requestContainer.getServiceRequest();
			String cdnF = (String) serviceRequest.getAttribute("CDNFUNZIONE");
			String page = (String) serviceRequest.getAttribute("PAGE");
			targetpage = "PAGE=" + page + "&CDNFUNZIONE=" + cdnF + "&" + specificAttribs;

		} else {
			// E' una GET!
			// va bene prendersi tutti i parametri passati
			targetpage = getQueryString(serviceRequest);
		}

		targetpage = "AdapterHTTP?" + targetpage + "&IS_RET_JUMP=Y";
		HyperLink thislink = new HyperLink("Ritorna", targetpage, null, "");
		backstack.push(thislink);

	}

	public List getGoToes() {
		return goToes;
	}

	protected String getQueryString(SourceBean _serviceRequest) {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if (parameterKey.equalsIgnoreCase("IS_FOOT_JUMP") || parameterKey.equalsIgnoreCase("IS_RET_JUMP"))
				continue;
			String parameterValue = parameter.getValue().toString();
			queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
			queryStringBuffer.append("=");
			queryStringBuffer.append(JavaScript.escape(parameterValue));
			queryStringBuffer.append("&");
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	} // private String getQueryString()

}