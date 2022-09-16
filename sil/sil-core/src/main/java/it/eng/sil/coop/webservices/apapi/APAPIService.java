package it.eng.sil.coop.webservices.apapi;

public class APAPIService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(APAPIService.class.getName());

	public String getStatoOccupazionaleAPAPI(String inputXML) {

		String outputXml = "";

		StatoOccupazionaleAPAPI statoOccupazionaleAPAPI = new StatoOccupazionaleAPAPI();
		outputXml = statoOccupazionaleAPAPI.getStatoOccupazionaleAPAPI(inputXML);

		return outputXml;

	}

	public String getUltimoMovimentoAPAPI(String inputXML) {

		String outputXml = "";

		UltimoMovimentoAPAPI ultimoMovimentoAPAPI = new UltimoMovimentoAPAPI();
		outputXml = ultimoMovimentoAPAPI.getUltimoMovimentoAPAPI(inputXML);

		return outputXml;

	}

}
