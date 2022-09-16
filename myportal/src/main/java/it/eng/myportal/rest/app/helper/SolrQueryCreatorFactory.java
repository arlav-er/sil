package it.eng.myportal.rest.app.helper;

import java.util.Date;
import java.util.List;

import it.eng.myportal.utils.ConstantsSingleton;

public class SolrQueryCreatorFactory {

	public static SolrQueryCreator getSolrQueryCreator(String cosa, String dove, boolean getFacet,
			String idVaDatiVacancy, List<String> listaCodMansione, List<String> listaCodContratto,
			List<String> listaCodOrario, List<String> listaCodSettore, List<String> listaCodLingua,
			List<String> listaCodTitoloStudio, List<String> listaCodPatente, String start, String rows, String dist,
			String lat, String lon, Date vacancyValideAllaData, Date vacancyModificateAllaData, String ordinamento) {
		SolrQueryCreator solr = null;

		if (ConstantsSingleton.App.NUOVO_IDO) {
			solr = new SolrQueryCreatorIdo(cosa, dove, getFacet, idVaDatiVacancy, listaCodMansione, listaCodContratto,
					listaCodOrario, listaCodSettore, listaCodLingua, listaCodTitoloStudio, listaCodPatente, start, rows,
					dist, lat, lon, vacancyValideAllaData, vacancyModificateAllaData, ordinamento);
		} else {
			solr = new SolrQueryCreator(cosa, dove, getFacet, idVaDatiVacancy, listaCodMansione, listaCodContratto,
					listaCodOrario, listaCodSettore, listaCodLingua, listaCodTitoloStudio, listaCodPatente, start, rows,
					dist, lat, lon, vacancyValideAllaData, vacancyModificateAllaData, ordinamento);
		}

		return solr;
	}
}
