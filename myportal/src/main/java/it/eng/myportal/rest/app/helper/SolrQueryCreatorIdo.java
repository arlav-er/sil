package it.eng.myportal.rest.app.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import it.eng.myportal.utils.ConstantsSingleton;

public class SolrQueryCreatorIdo extends SolrQueryCreator {

	public SolrQueryCreatorIdo(String cosa, String dove, boolean getFacet, String idVaDatiVacancy,
			List<String> listaCodMansione, List<String> listaCodContratto, List<String> listaCodOrario,
			List<String> listaCodSettore, List<String> listaCodLingua, List<String> listaCodTitoloStudio,
			List<String> listaCodPatente, String start, String rows, String dist, String lat, String lon,
			Date vacancyValideAllaData, Date vacancyModificateAllaData, String ordinamento) {

		super(cosa, dove, getFacet, idVaDatiVacancy, listaCodMansione, listaCodContratto, listaCodOrario,
				listaCodSettore, listaCodLingua, listaCodTitoloStudio, listaCodPatente, start, rows, dist, lat, lon,
				vacancyValideAllaData, vacancyModificateAllaData, ordinamento);
	}

	@Override
	public void initFields() {
		this.setFieldList("id_va_dati_vacancy,ragione_sociale,attivita_principale,attivita_descrizione_estesa,"
				+ ConstantsSingleton.RvRicercaVacancy.CODMANSIONEISTAT + ","
				+ ConstantsSingleton.RvRicercaVacancy.MANSIONEISTAT + ","
				+ ConstantsSingleton.RvRicercaVacancy.CONTRATTOSIL + "," + ConstantsSingleton.RvRicercaVacancy.ORARIOSILIDO 
				+ "," + ConstantsSingleton.RvRicercaVacancy.SETTORE + "," + ConstantsSingleton.RvRicercaVacancy.LINGUA
				+ "," + ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO + ","
				+ ConstantsSingleton.RvRicercaVacancy.PATENTESIL + ","
				+ "comune,targa,data_pubblicazione,data_scadenza_pubblicazione,numero,anno,provenienza,punto_0_coordinate,punto_1_coordinate,descrizione,codiconaapp");

		this.setFacetField(new String[7]);
		this.getFacetField()[0] = ConstantsSingleton.RvRicercaVacancy.CODDESCMANSIONEISTAT;
		this.getFacetField()[1] = ConstantsSingleton.RvRicercaVacancy.CODDESCCONTRATTOSIL;
		this.getFacetField()[2] = ConstantsSingleton.RvRicercaVacancy.CODDESCORARIOSILIDO;
		this.getFacetField()[3] = ConstantsSingleton.RvRicercaVacancy.CODDESCSETTORE;
		this.getFacetField()[4] = ConstantsSingleton.RvRicercaVacancy.CODDESCLINGUA;
		this.getFacetField()[5] = ConstantsSingleton.RvRicercaVacancy.CODDESCTITOLO_STUDIO;
		this.getFacetField()[6] = ConstantsSingleton.RvRicercaVacancy.CODDESCPATENTESIL;

		this.setMapFieldFiltroRaggruppamenti(new HashMap<String, String>());
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODMANSIONE,
				ConstantsSingleton.RvRicercaVacancy.CODMANSIONEISTAT);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO,
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODORARIO,
				ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODSETTORE,
				ConstantsSingleton.RvRicercaVacancy.CODSETTORE);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODLINGUA,
				ConstantsSingleton.RvRicercaVacancy.CODLINGUA);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO,
				ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO);
		this.getMapFieldFiltroRaggruppamenti().put(ConstantsSingleton.RvRicercaVacancy.CODPATENTE,
				ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL);

		this.setFieldListOut(new HashMap<String, String>());
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODMANSIONEISTAT,
				ConstantsSingleton.RvRicercaVacancy.CODMANSIONE);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.MANSIONEISTAT,
				ConstantsSingleton.RvRicercaVacancy.MANSIONE);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CONTRATTOSIL,
				ConstantsSingleton.RvRicercaVacancy.CONTRATTO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.ORARIOSILIDO,
				ConstantsSingleton.RvRicercaVacancy.ORARIO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.SETTORE,
				ConstantsSingleton.RvRicercaVacancy.SETTORE);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.LINGUA,
				ConstantsSingleton.RvRicercaVacancy.LINGUA);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO,
				ConstantsSingleton.RvRicercaVacancy.TITOLO_STUDIO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.PATENTESIL,
				ConstantsSingleton.RvRicercaVacancy.PATENTE);

		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCMANSIONEISTAT,
				ConstantsSingleton.RvRicercaVacancy.CODDESCMANSIONE);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCCONTRATTOSIL,
				ConstantsSingleton.RvRicercaVacancy.CODDESCCONTRATTO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCORARIOSILIDO,
				ConstantsSingleton.RvRicercaVacancy.CODDESCORARIO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCLINGUA,
				ConstantsSingleton.RvRicercaVacancy.CODDESCLINGUA);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCTITOLO_STUDIO,
				ConstantsSingleton.RvRicercaVacancy.CODDESCTITOLO_STUDIO);
		this.getFieldListOut().put(ConstantsSingleton.RvRicercaVacancy.CODDESCPATENTESIL,
				ConstantsSingleton.RvRicercaVacancy.CODDESCPATENTE);
	}
}
