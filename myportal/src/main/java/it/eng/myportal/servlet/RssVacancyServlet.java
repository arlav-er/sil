package it.eng.myportal.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.RvRicercaVacancyDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.rss.Guid;
import it.eng.myportal.rss.ObjectFactory;
import it.eng.myportal.rss.Rss;
import it.eng.myportal.rss.RssChannel;
import it.eng.myportal.rss.RssItem;
import it.eng.myportal.utils.ConstantsSingleton;

@WebServlet(urlPatterns = { "/rssVacancy" })
public class RssVacancyServlet extends HttpServlet {

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;
	
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;

	private static final long serialVersionUID = -5097434221630945679L;

	protected Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        String linkViewVacancy = "/faces/secure/azienda/vacancies/visualizza.xhtml?id=";
		String siglaProvincia = request.getParameter("siglaProvincia");
		String maxRows = request.getParameter("maxRows");

		response.setContentType("application/atom+xml;charset=UTF-8");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		OutputStreamWriter w = new OutputStreamWriter(response.getOutputStream());
		ObjectFactory o = new ObjectFactory();
		Rss a = o.createRss();
		a.setVersion(new BigDecimal("2.0"));
		RssChannel channel = o.createRssChannel();
		channel.getTitleOrLinkOrDescription().add(0, o.createRssChannelTitle("Ultime vacancy disponibili"));
		channel.getTitleOrLinkOrDescription().add(1,
				o.createRssChannelDescription("Elenco delle ultime vacancy inserite sul portale"));
		channel.getTitleOrLinkOrDescription().add(2, o.createRssItemLink(ConstantsSingleton.BASE_URL));

		List<RvRicercaVacancyDTO> lista = findDTOVacancy(siglaProvincia, maxRows);

		for (Iterator<RvRicercaVacancyDTO> iterator = lista.iterator(); iterator.hasNext();) {
			RvRicercaVacancyDTO vacancy = (RvRicercaVacancyDTO) iterator.next();
			VaDatiVacancyDTO vaDatiVacancyDTO = getVaDatiVacancyDTO(vacancy.getIdVaDatiVacancy());
			if(vaDatiVacancyDTO.getFlgIdo()) {
				linkViewVacancy = "/faces/secure/azienda/vacancies/view_pf.xhtml?id=";
			}

			RssItem item = o.createRssItem();
			item.getTitleOrDescriptionOrLink().add(0,
					o.createRssItemTitle(vacancy.getDatoreLavoro() + " - " + vacancy.getStrComLavoro()));
			String descrEstesa = vaDatiVacancyDTO.getDescrizione();
			if (descrEstesa.length() > 200) {
				descrEstesa = descrEstesa.substring(0, 200) + "...";
			}
			String contratto = "";
			String descrizioneVacancy = "";
			if (vacancy.getStrTipoContratto() != null) {
				contratto = vacancy.getStrTipoContratto();
				descrizioneVacancy = vaDatiVacancyDTO.getAttivitaPrincipale() + " - " + descrEstesa + "  - " + contratto
						+ " - " + dateFormat.format(vacancy.getDataModifica());
			} else {
				descrizioneVacancy = vaDatiVacancyDTO.getAttivitaPrincipale() + " - " + descrEstesa + "  -  "
						+ dateFormat.format(vacancy.getDataModifica());
			}

			item.getTitleOrDescriptionOrLink().add(1, o.createRssItemDescription(descrizioneVacancy));
			item.getTitleOrDescriptionOrLink().add(2, o.createRssItemLink(ConstantsSingleton.BASE_URL
					+ linkViewVacancy + vacancy.getIdVaDatiVacancy()));

			Guid linkGuid = new Guid();
			linkGuid.setValue(ConstantsSingleton.BASE_URL + linkViewVacancy
					+ vacancy.getIdVaDatiVacancy());
			item.getTitleOrDescriptionOrLink().add(3, o.createRssItemGuid(linkGuid));

			/*
			 * Enclosure imgSettore = new Enclosure(); imgSettore.setType("image/jpeg"); imgSettore.setLength(new
			 * BigInteger("1")); imgSettore.setUrl(ConstantsSingleton.BASE_URL+"/resources/images/news_icon.jpg");
			 * 
			 * item.getTitleOrDescriptionOrLink().add(4, o.createRssItemEnclosure(imgSettore));
			 */

			channel.getItem().add(item);
		}

		a.setChannel(channel);

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		try {
			Class<RssVacancyServlet> classe = RssVacancyServlet.class;
			ClassLoader loader = classe.getClassLoader();
			InputStream is = loader
					.getResourceAsStream("xsd" + File.separator + "rss" + File.separator + "rss-2_0.xsd");
			StreamSource streamSource = new StreamSource(is);
			schema = sf.newSchema(streamSource);

			JAXBContext jc = JAXBContext.newInstance(Rss.class);

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			marshaller.setSchema(schema);
			marshaller.setEventHandler(new ValidationEventHandler() {

				@Override
				public boolean handleEvent(ValidationEvent event) {

					log.error(event.getMessage());

					return true;
				}
			});

			marshaller.marshal(a, w);

		} catch (SAXException e) {
			log.error(e);
		} catch (JAXBException e) {
			log.error(e);
		}
	}

	public List<RvRicercaVacancyDTO> findDTOVacancy(String targa, String maxRows) {
		List<RvRicercaVacancyDTO> ret = new ArrayList<RvRicercaVacancyDTO>();
		try {
			/**
			 * Aggiungo filtro sulla data di scadenza Le vacancy sono valide se la data scadenza > oggi.
			 * 
			 */
			String filtro = "";
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			/* UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA
			  c.add(Calendar.DATE, +1);
			*/
			String dataInizio = dateFormat.format(c.getTime());
			c.add(Calendar.YEAR, 100);
			String dataFine = dateFormat.format(c.getTime());
			filtro += "+AND+data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO " + dataFine + "T00:00:00Z]";

			/*
			 * Filtro provincia: in SolR si imposta la condizione sul field targa; nel caso in cui la targa passata non
			 * sia valida non si produrrĂ  alcun risultato.
			 */
			if (targa != null) {
				filtro += it.eng.myportal.utils.URL.escapeSolr("+AND+targa:" + targa);
			}

			/*
			 * Filtro per gestire il massimo numero di rows da ritornare
			 */
			Integer maxRowsFilter = 10;
			if (maxRows != null) {
				try {
					Integer val = Integer.valueOf(maxRows);
					if (val >= 0 && val <= 50)
						maxRowsFilter = val;
				} catch (NumberFormatException e) {
					// Parametro non valido, si lascia il default
				}
			}

			String baseDominio = ConstantsSingleton.getSolrUrl();
			String url = baseDominio + "/core0/select/?q=*%3A*" + it.eng.myportal.utils.URL.escape(filtro)
					+ "&start=0&rows=" + maxRowsFilter + "&indent=on&wt=xml&sort=data_modifica%20desc";

			Document document = documentSOLR(url);
			if (document != null) {
				NodeList nodeResult = document.getElementsByTagName("result");
				Node result = (Node) nodeResult.item(0);
				NodeList nodeList = document.getElementsByTagName("doc");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node doc = (Node) nodeList.item(i);
					Element elementDoc = (Element) doc.getChildNodes();
					NodeList nodeListStr = elementDoc.getElementsByTagName("str"),
							nodeListDouble = elementDoc.getElementsByTagName("double"),
							nodeListInt = elementDoc.getElementsByTagName("int"),
							nodeListDate = elementDoc.getElementsByTagName("date");
					// Aggiungo il contenuto delle stringhe
					Map<String, String> mapStr = new HashMap<String, String>();
					for (int y = 0; y < nodeListStr.getLength(); y++) {
						Node node = nodeListStr.item(y);
						String name = "", value = "";
						if (node.hasAttributes()) {
							name = nodeListStr.item(y).getAttributes().item(0).getNodeValue();
						} else {
							name = nodeListStr.item(y).getParentNode().getAttributes().item(0).getNodeValue();
						}
						if (nodeListStr.item(y).getFirstChild() != null) {
							value = nodeListStr.item(y).getFirstChild().getNodeValue();
						}
						mapStr.put(name, value);
					}
					Map<String, java.sql.Date> mapDate = popolaMapDate(nodeListDate);
					Map<String, Double> mapDouble = popolaMapDouble(nodeListDouble);
					Map<String, Integer> mapInt = popolaMapInt(nodeListInt);
					ret.add(rvRicercaVacancyHome.toDTO(mapStr, mapDouble, mapInt, mapDate, new Integer("0")));
				}
			}

		} catch (Exception e) {
			log.error("Exception " + e.getMessage());
		}

		return ret;
	}

	private Document documentSOLR(String url) {
		Document document = null;
		HttpClient httpClient = new HttpClient();
		// Create a method instance.
		GetMethod method = new GetMethod(url);
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = IOUtils.toInputStream(IOUtils.toString(new URL(url)));

				Reader reader = new InputStreamReader(is, "UTF-8");

				InputSource inpsource = new InputSource(reader);
				inpsource.setEncoding("UTF-8");

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(inpsource);
			}

		} catch (HttpException e) {
			log.error("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			log.error("IOException " + e);
		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException " + e);
		} catch (SAXException e) {
			log.error("SAXException " + e);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return document;
	}

	/**
	 * Popola tutti i double presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListDouble
	 * @return Map<String, Double>
	 */
	private Map<String, Double> popolaMapDouble(NodeList nodeListDouble) {
		// Aggiungo il contenuto dei double
		Map<String, Double> mapDouble = new HashMap<String, Double>();
		for (int y = 0; y < nodeListDouble.getLength(); y++) {
			String name = nodeListDouble.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListDouble.item(y).getFirstChild() != null) {
				value = nodeListDouble.item(y).getFirstChild().getNodeValue();
			}
			mapDouble.put(name, Double.valueOf(value));
		}
		return mapDouble;
	}

	/**
	 * Popola tutti gli Integer presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListInt
	 * @return Map<String, Integer>
	 */
	private Map<String, Integer> popolaMapInt(NodeList nodeListInt) {
		// Aggiungo il contenuto degli int
		Map<String, Integer> mapInt = new HashMap<String, Integer>();
		for (int y = 0; y < nodeListInt.getLength(); y++) {
			String name = nodeListInt.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListInt.item(y).getFirstChild() != null) {
				value = nodeListInt.item(y).getFirstChild().getNodeValue();
			}
			mapInt.put(name, Integer.valueOf(value));
		}
		return mapInt;
	}

	/**
	 * Popola tutte le date presenti nel nodo del Document passato come paramentro
	 * 
	 * @param nodeListDouble
	 * @return Map<String, Double>
	 */
	private Map<String, java.sql.Date> popolaMapDate(NodeList nodeListDate) {
		// Aggiungo il contenuto dei double
		Map<String, java.sql.Date> mapDate = new HashMap<String, java.sql.Date>();
		for (int y = 0; y < nodeListDate.getLength(); y++) {
			String name = nodeListDate.item(y).getAttributes().item(0).getNodeValue(), value = "";
			if (nodeListDate.item(y).getFirstChild() != null) {
				value = nodeListDate.item(y).getFirstChild().getNodeValue();
			}
			String dateStr = value.substring(0, value.indexOf("T")),
					timeStr = value.substring(value.indexOf("T") + 1, value.indexOf("T") + 9);
			java.sql.Date date = new java.sql.Date(
					java.sql.Date.valueOf(dateStr).getTime() + (Time.valueOf(timeStr).getTime() + 3600000));
			mapDate.put(name, date);
		}
		return mapDate;
	}

	public VaDatiVacancyDTO getVaDatiVacancyDTO(Integer idVaDatiVacancy) {
		try {
			VaDatiVacancyDTO vacancyDTO = vaDatiVacancyHome.findDTOById(idVaDatiVacancy);
			return vacancyDTO;
		} catch (Exception e) {
			log.error("recupera attivita per rss " + e);
		}

		return null;
	}
}
