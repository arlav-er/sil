package it.eng.myportal.rest.app.helper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.utils.ConstantsSingleton;

public class SolrQueryCreator {

	protected Log log = LogFactory.getLog(this.getClass());
	private static final String SOLR = "/core0/select/?";
	private static final String DUE_PUNTI = ":";
	private static final String FILTRO_TUTTO = "*" + DUE_PUNTI + "*";
	/*
	 * codmansione, mansione -> GRUPPO PROFESSIONALE codcontratto, contratto -> CONTRATTO codorario, orario -> ORARIO
	 * codsettore, settore -> SETTORE codlingua, lingua -> LINGUA codtitolo, titolo -> TITOLO DI STUDIO codpatente,
	 * patente -> PATENTE
	 */

	// Default latitudine e longitudine: Roma
	private static final String LATITUDINE_DEFAULT = "41.899686";
	private static final String LONGITUDINE_DEFAULT = "12.486684";

	private String urlSolr;
	private String cosa;
	private String dove;
	private boolean getFacet;
	private Integer idVaDatiVacancy;
	private List<String> listaCodMansione;
	private List<String> listaCodContratto;
	private List<String> listaCodOrario;;
	private List<String> listaCodSettore;
	private List<String> listaCodLingua;
	private List<String> listaCodTitoloStudio;
	private List<String> listaCodPatente;
	private String start;
	private String rows;
	private String dist;
	private String lat;
	private String lon;
	private Date vacancyValideAllaData;
	private Date vacancyModificateAllaData;
	private Integer ordinamento;

	// Lista dei field da ritornare
	private String fieldList;

	// Lista dei field da utilizzare come raggruppamento
	private String[] facetField;

	/*
	 * Mappa utilizzata per impostare i filtri sui raggruppamenti. Permette di gestire dinamicamente il nome dei field
	 * sui quali impostare il filtro passato. In ogni entry la chiave è sempre la costante NON IDO, il valore è quello
	 * da utilizzare per impostare il filtro. Per non IDO è uguale alla chiave, per IDO è il codice IDO.
	 */
	private HashMap<String, String> mapFieldFiltroRaggruppamenti;

	/*
	 * Mappa opzionale utilizzata eventualmente per codificare correttamente i campi ritornati al chiamante. Permette di
	 * gestire dinamicamente il nome del field da ritornare al chiamante. In ogni entry la chiave è la codifica
	 * ritornata da solr (sia per i field che per le facet), il valore è il corrispondente campo da mappare in uscita.
	 * Per non IDO non è necessario alcuna codifica. Tale codifica è utilizzata dal metodo codificaKeyJSON
	 */
	private HashMap<String, String> fieldListOut = null;

	String getFieldList() {
		return fieldList;
	}

	void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}

	String[] getFacetField() {
		return facetField;
	}

	void setFacetField(String[] facetField) {
		this.facetField = facetField;
	}

	HashMap<String, String> getMapFieldFiltroRaggruppamenti() {
		return mapFieldFiltroRaggruppamenti;
	}

	void setMapFieldFiltroRaggruppamenti(HashMap<String, String> mapFieldFiltroRaggruppamenti) {
		this.mapFieldFiltroRaggruppamenti = mapFieldFiltroRaggruppamenti;
	}

	HashMap<String, String> getFieldListOut() {
		return fieldListOut;
	}

	void setFieldListOut(HashMap<String, String> fieldListOut) {
		this.fieldListOut = fieldListOut;
	}

	public SolrQueryCreator(String cosa, String dove, boolean getFacet, String idVaDatiVacancy,
			List<String> listaCodMansione, List<String> listaCodContratto, List<String> listaCodOrario,
			List<String> listaCodSettore, List<String> listaCodLingua, List<String> listaCodTitoloStudio,
			List<String> listaCodPatente, String start, String rows, String dist, String lat, String lon,
			Date vacancyValideAllaData, Date vacancyModificateAllaData, String ordinamento) {
		super();

		initFields();

		this.urlSolr = ConstantsSingleton.getSolrUrl() + SOLR;
		this.cosa = cosa;
		this.dove = dove;
		this.getFacet = getFacet;

		try {
			this.idVaDatiVacancy = Integer.valueOf(idVaDatiVacancy);
		} catch (NumberFormatException e) {

		}

		this.listaCodMansione = listaCodMansione;
		this.listaCodContratto = listaCodContratto;
		this.listaCodOrario = listaCodOrario;
		this.listaCodSettore = listaCodSettore;
		this.listaCodLingua = listaCodLingua;
		this.listaCodTitoloStudio = listaCodTitoloStudio;
		this.listaCodPatente = listaCodPatente;
		this.start = start;
		this.rows = rows;
		this.dist = dist;
		this.lat = lat;
		this.lon = lon;
		this.vacancyValideAllaData = vacancyValideAllaData;
		this.vacancyModificateAllaData = vacancyModificateAllaData;

		try {
			this.ordinamento = Integer.valueOf(ordinamento);
		} catch (NumberFormatException e) {

		}

		// Default ordinamento per distanza
		if (this.ordinamento == null)
			this.ordinamento = 0;
	}

	/**
	 * Metodo di inizializzazione dei field SOLR: filtri di ricerca, raggruppamenti, filtri sui raggruppamenti
	 * 
	 */
	public void initFields() {
		this.setFieldList("id_va_dati_vacancy,ragione_sociale,attivita_principale,attivita_descrizione_estesa,"
				+ ConstantsSingleton.RvRicercaVacancy.CODMANSIONE + "," + ConstantsSingleton.RvRicercaVacancy.MANSIONE
				+ "," + ConstantsSingleton.RvRicercaVacancy.CONTRATTO + "," + ConstantsSingleton.RvRicercaVacancy.ORARIO
				+ "," + ConstantsSingleton.RvRicercaVacancy.SETTORE + "," + ConstantsSingleton.RvRicercaVacancy.LINGUA
				+ "," + ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO + ","
				+ ConstantsSingleton.RvRicercaVacancy.PATENTE + ","
				+ "comune,targa,data_pubblicazione,data_scadenza_pubblicazione,numero,anno,provenienza,punto_0_coordinate,punto_1_coordinate,descrizione,codiconaapp");

		this.setFacetField(new String[7]);
		this.getFacetField()[0] = ConstantsSingleton.RvRicercaVacancy.CODDESCMANSIONE;
		this.getFacetField()[1] = ConstantsSingleton.RvRicercaVacancy.CODDESCCONTRATTO;
		this.getFacetField()[2] = ConstantsSingleton.RvRicercaVacancy.CODDESCORARIO;
		this.getFacetField()[3] = ConstantsSingleton.RvRicercaVacancy.CODDESCSETTORE;
		this.getFacetField()[4] = ConstantsSingleton.RvRicercaVacancy.CODDESCLINGUA;
		this.getFacetField()[5] = ConstantsSingleton.RvRicercaVacancy.CODDESCTITOLO_STUDIO;
		this.getFacetField()[6] = ConstantsSingleton.RvRicercaVacancy.CODDESCPATENTE;

		this.setMapFieldFiltroRaggruppamenti(new HashMap<String, String>());
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODMANSIONE,
				ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO,
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODORARIO,
				ConstantsSingleton.RvRicercaVacancy.CODORARIO);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODSETTORE,
				ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODLINGUA,
				ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO,
				ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODPATENTE,
				ConstantsSingleton.RvRicercaVacancy.CODPATENTE);

		fieldListOut = null;
	}

	public String getUrlSolr() {
		return this.urlSolr;
	}

	/**
	 * Metodo di composizione della mappa relativa ai filtri da inviare a SOLR
	 * 
	 * @return
	 */
	public HashMap<String, Object> createParamsQuerySolr() {

		HashMap<String, Object> params = new HashMap<String, Object>();

		String filtro = "";
		String filtroGeo = null;

		// Filtro Id_Va_Dati_Vacancy: tutti gli altri filtri vengono esclusi
		if (this.idVaDatiVacancy != null) {
			filtro += "id_va_dati_vacancy:" + idVaDatiVacancy;
		} else {
			// Filtro cosa
			String filtroCosa = calcFiltroPerField(this.cosa, "cosa");
			// Filtro dove
			String filtroDove = calcFiltroPerField(this.dove, "dove");
			// Filtro geo
			filtroGeo = calcFiltroGeo(this.dist, this.lat, this.lon);

			if (filtroCosa.isEmpty() && filtroDove.isEmpty()) {
				filtro = FILTRO_TUTTO;
			} else if (!filtroCosa.isEmpty() && !filtroDove.isEmpty()) {
				filtro = filtroCosa + " AND " + filtroDove;
			} else {
				filtro = filtroCosa + filtroDove;
			}

			// Filtri su codici mansione, contratto, orario, settore, lingua, titolo di studio, patente
			filtro += componiFiltriRaggruppamenti();

			// Eventuale filtro su offerte temporalmente valide alla data (non scadute)
			filtro += calcFiltroScadenzaPubblicazione();
			// Eventuale filtro su offerte modificata alla data
			filtro += calcFiltroDataModifica();
		}

		// Parametro query (q)
		params.put("q", filtro);

		// Parametro filtro geo (fq)
		if (filtroGeo != null && !filtroGeo.isEmpty())
			params.put("fq", filtroGeo);

		// Parametro field (fl)
		params.put("fl", this.fieldList);
		// Parametri paginazione
		if (this.start != null && !this.start.isEmpty())
			params.put("start", this.start);
		if (this.rows != null && !this.rows.isEmpty())
			params.put("rows", this.rows);

		params.put("indent", "on");
		params.put("wt", "json");
		// Parametro rimozione responseHeader
		params.put("omitHeader", "true");

		// Parametro ordinamento (sort)
		params.put("sort", calcOrdinamento());

		// Parametri gestione facet
		if (this.getFacet) {
			params.put("facet", Boolean.TRUE);
			params.put("facet.mincount", 1);
			params.put("facet.field", this.facetField);
		}

		return params;
	}

	private String componiFiltriRaggruppamenti() {
		String filtro = "";

		if (this.listaCodMansione != null && !this.listaCodMansione.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODMANSIONE))
			filtro += calcFiltroPerListaValori(this.listaCodMansione,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODMANSIONE));
		if (this.listaCodContratto != null && !this.listaCodContratto.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO))
			filtro += calcFiltroPerListaValori(this.listaCodContratto,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO));
		if (this.listaCodOrario != null && !this.listaCodOrario.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODORARIO))
			filtro += calcFiltroPerListaValori(this.listaCodOrario,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODORARIO));
		if (this.listaCodSettore != null && !this.listaCodSettore.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODSETTORE))
			filtro += calcFiltroPerListaValori(this.listaCodSettore,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODSETTORE));
		if (this.listaCodLingua != null && !this.listaCodLingua.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODLINGUA))
			filtro += calcFiltroPerListaValori(this.listaCodLingua,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODLINGUA));
		if (this.listaCodTitoloStudio != null && !this.listaCodTitoloStudio.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO))
			filtro += calcFiltroPerListaValori(this.listaCodTitoloStudio,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO));
		if (this.listaCodPatente != null && !this.listaCodPatente.isEmpty() && this.getMapFieldFiltroRaggruppamenti().containsKey(ConstantsSingleton.RvRicercaVacancy.CODPATENTE))
			filtro += calcFiltroPerListaValori(this.listaCodPatente,
					this.getMapFieldFiltroRaggruppamenti().get(ConstantsSingleton.RvRicercaVacancy.CODPATENTE));

		return filtro;
	}

	private String calcFiltroGeo(String dist, String lat, String lon) {
		String filtroGeo = "";

		// Filtro GEO
		if (lat != null && !("").equalsIgnoreCase(lat) && lon != null && !("").equalsIgnoreCase(lon) && dist != null
				&& !("").equalsIgnoreCase(dist)) {
			filtroGeo = "{!geofilt pt=" + lat + "," + lon + " sfield=punto d=" + dist + "}";
		}
		return filtroGeo;
	}

	private String calcFiltroPerField(String cosa, String fieldName) {
		if (cosa != null && cosa.trim().length() > 0) {

			/**
			 * la query su solr deve essere del tipo: q=cosa:(*azienda* + *progetto*)
			 */

			String[] parole = cosa.trim().toLowerCase().split(" ");
			for (int i = 0; i < parole.length; i++) {
				parole[i] = "*" + parole[i] + "*";
			}
			String cosaCercare = StringUtils.join(parole, " + ");
			return fieldName + DUE_PUNTI + "(" + cosaCercare + ")*";

		}
		return "";
	}

	private String calcFiltroPerListaValori(List<String> listaValori, String fieldName) {
		StringBuffer sb = new StringBuffer();
		String ret = null;
		if (listaValori != null && !listaValori.isEmpty()) {
			for (String gruppo : listaValori) {
				if (gruppo != null && !gruppo.trim().isEmpty()) {
					if (sb.length() == 0)
						sb.append(" AND " + fieldName + DUE_PUNTI + "(");
					sb.append(gruppo).append(" OR ");
				}
			}
			ret = sb.toString();
			if (sb.length() > 0)
				ret = ret.substring(0, ret.lastIndexOf("OR")) + ")";
		}
		return ret;
	}

	private String calcFiltroScadenzaPubblicazione() {
		String ret = "";

		if (this.vacancyValideAllaData != null) {
			// Dalla data si considerano solo giorni, mesi, anni (non ore, minuti, secondi)
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dataInizio = dateFormat.format(this.vacancyValideAllaData);

			ret = " AND data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO 2100-01-01T00:00:00Z]";
		}

		return ret;
	}

	private String calcFiltroDataModifica() {
		String ret = "";

		if (this.vacancyModificateAllaData != null) {
			Date dataPostDueOre = addHoursToJavaUtilDate(vacancyModificateAllaData, 2);

			// data in formato UTC come richiesto da SOLR e si mantengono ore, minuti, secondi
			String dataInizio = DateFormatUtils.formatUTC(dataPostDueOre,
					DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());

			ret = " AND data_modifica:[" + dataInizio + "Z TO 2100-01-01T00:00:00Z]";
		}

		return ret;
	}

	private String calcOrdinamento() {

		String sort = null;

		String latOrder = this.lat;
		String lonOrder = this.lon;

		// Default latitudine e longitudine: Roma
		if ((latOrder == null || latOrder.isEmpty()) && (lonOrder == null || lonOrder.isEmpty())) {
			latOrder = LATITUDINE_DEFAULT;
			lonOrder = LONGITUDINE_DEFAULT;
		}

		String sortSeparator = ", ";
		String[] sortArr = { "geodist(punto," + latOrder + "," + lonOrder + ") asc", "data_pubblicazione desc",
				"data_modifica desc" };

		switch (this.ordinamento) {
		case 1:
			// Ordinamento per data pubblicazione decrescente, data modifica decrescente, distanza crescente
			sort = sortArr[1] + sortSeparator + sortArr[2] + sortSeparator + sortArr[0];
			break;
		case 2:
			// Ordinamento per data modifica decrescente, data pubblicazione decrescente, distanza crescente
			Collections.reverse(Arrays.asList(sortArr));
			sort = StringUtils.join(sortArr, sortSeparator);
			break;
		default:
			// Ordinamento per distanza crescente, data pubblicazione decrescente, data modifica decrescente
			sort = StringUtils.join(sortArr, sortSeparator);
			break;
		}

		return sort;
	}

	private Date addHoursToJavaUtilDate(Date date, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	/**
	 * Metodo che si preoccupa, partendo dal json ottenuto dall'invocazione SOLR, di mappare correttamente le chiavi dei
	 * json in uscita rispetto al fieldListOut
	 * 
	 * @param result
	 * @return
	 */
	public String parseResultJSON(String result) throws JSONException {
		String ret = null;

		if (this.getFieldListOut() != null && !this.getFieldList().isEmpty()) {
			JSONObject json = new JSONObject(result);
			ret = this.codificaKeyJSON(json).toString();
		} else {
			ret = result;
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	private JSONObject codificaKeyJSON(JSONObject inputObject) throws JSONException {
		JSONObject resultObject = new JSONObject();
		Iterator iterator = inputObject.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (inputObject.get(key) instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) inputObject.get(key);
				resultObject.put(codificaKey(key), codificaKeyJSON(jsonObject));
			} else if (inputObject.get(key) instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) inputObject.get(key);
				resultObject.put(codificaKey(key), codificaKeyJSON(jsonArray));
			} else {
				resultObject.put(codificaKey(key), inputObject.get(key));
			}
		}
		return resultObject;
	}

	private JSONArray codificaKeyJSON(JSONArray inputArray) throws JSONException {
		JSONArray resultArray = new JSONArray();
		for (int i = 0; i < inputArray.length(); i++) {
			if (inputArray.get(i) instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) inputArray.get(i);
				resultArray.put(i, codificaKeyJSON(jsonObject));
			} else if (inputArray.get(i) instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) inputArray.get(i);
				resultArray.put(i, codificaKeyJSON(jsonArray));
			} else {
				resultArray.put(i, inputArray.get(i));
			}
		}
		return resultArray;
	}

	private String codificaKey(String key) {
		String ret = key;

		if (this.getFieldListOut().containsKey(key)) {
			ret = this.getFieldListOut().get(key);
		}

		return ret;
	}

}
