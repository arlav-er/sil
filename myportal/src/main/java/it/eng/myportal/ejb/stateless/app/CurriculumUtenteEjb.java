package it.eng.myportal.ejb.stateless.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.app.exception.AllegatoCurriculumUtenteNotFoundException;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.CurriculumUtenteNotFoundException;
import it.eng.myportal.rest.app.exception.EmptyParameterException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.exception.WrongParameterException;
import it.eng.myportal.rest.app.helper.AppUtils;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class CurriculumUtenteEjb implements Serializable {

	private static final long serialVersionUID = -6842625237307271829L;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	protected static Log log = LogFactory.getLog(CurriculumUtenteEjb.class);

	private final static String STATUS = "status";
	private final static String OK = "ok";

	private final static String LISTACURRICULUM = "listaCurriculum";
	private final static String ID = "id";
	private final static String DESCRIZIONE = "descrizione";
	private final static String DATAMODIFICA = "dataModifica";
	private final static String FILENAME = "filename";
	private final static String CONTENT = "content";
	private final static String CAN_CREATE = "canCreate";

	public String getAllCurriculumUtente(String username) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			// Json di ritorno
			JSONObject obj = new JSONObject();

			if (pfPrincipal != null) {
				List<CvDatiPersonali> listaCv = new ArrayList<CvDatiPersonali>();

				listaCv.addAll(this.findAllCurriculaLive(pfPrincipal.getIdPfPrincipal()));
				listaCv.addAll(this.findAllCurriculaIntermediatiNoMinScaduti(pfPrincipal.getIdPfPrincipal()));

				// Ordinamento della lista dei CV per data modifica decrescente
				Collections.sort(listaCv, Collections.reverseOrder(new CvDatiPersonaliComparator()));

				obj.put(STATUS, OK);
				JSONArray arrCv = new JSONArray();
				if (!listaCv.isEmpty()) {
					for (CvDatiPersonali cvDatiPersonali : listaCv) {
						JSONObject cvIesimo = getJsonObject(cvDatiPersonali);
						arrCv.put(cvIesimo);
					}
				}
				obj.putOpt(LISTACURRICULUM, arrCv);

				ret = obj.toString();

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			log.error("Errore durante il recupero dei curriculum utente: " + e.getMessage());
			throw new GenericException("Errore durante il recupero dei curriculum utente");
		}
		return ret;
	}
	
	private List<CvDatiPersonali> findAllCurriculaLive(Integer idPfPrincipal) {
		List<CvDatiPersonali> list = null;
		if (ConstantsSingleton.App.NUOVO_IDO) {
			list = cvDatiPersonaliHome.findAllCurriculaLiveFlgIdo(idPfPrincipal);
		} else {
			list = cvDatiPersonaliHome.findAllCurriculaLive(idPfPrincipal);
		}
		return list;
	}

	private List<CvDatiPersonali> findAllCurriculaIntermediatiNoMinScaduti(Integer idPfPrincipal) {
		List<CvDatiPersonali> list = null;
		if (ConstantsSingleton.App.NUOVO_IDO) {
			list = cvDatiPersonaliHome.findAllCurriculaIntermediatiNoMinScadutiFlgIdo(idPfPrincipal);
		} else {
			list = cvDatiPersonaliHome.findAllCurriculaIntermediatiNoMinScaduti(idPfPrincipal);
		}
		return list;
	}
	
	public String dettaglioCurriculumUtente(String username, Integer idCv) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			// Json di ritorno
			JSONObject obj = new JSONObject();

			if (pfPrincipal != null) {
				CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(idCv);

				if (curriculumPresente(cvDatiPersonali, pfPrincipal)) {
					// Recupero dell'eventuale allegato
					CvAllegato cvAllegato = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());

					obj.put(STATUS, OK);
					JSONArray arrCv = new JSONArray();

					JSONObject cvIesimo = getJsonObjectDettaglio(cvDatiPersonali, cvAllegato);
					arrCv.put(cvIesimo);

					obj.putOpt(LISTACURRICULUM, arrCv);

					ret = obj.toString();
				} else {
					throw new CurriculumUtenteNotFoundException(idCv);
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero del dettaglio curriculum utente");
		}
		return ret;
	}

	private JSONObject getJsonObject(CvDatiPersonali cvDatiPersonali) throws JSONException {
		JSONObject cvIesimo = new JSONObject();
		cvIesimo.put(ID, cvDatiPersonali.getIdCvDatiPersonali());
		cvIesimo.put(DESCRIZIONE, cvDatiPersonali.getDescrizione());
		cvIesimo.put(DATAMODIFICA, cvDatiPersonali.getDtmMod());
		cvIesimo.put("dataNascita", cvDatiPersonali.getDtNascita());
		cvIesimo.put("indirizzoDomicilio", cvDatiPersonali.getIndirizzoDomicilio());
		cvIesimo.put("comuneDomicilio", cvDatiPersonali.getDeComuneDomicilio().getDenominazione());
		cvIesimo.put("telefono", cvDatiPersonali.getTel1());
		cvIesimo.put("cellulare", cvDatiPersonali.getTel2());
		cvIesimo.put("email", cvDatiPersonali.getEmail());
		cvIesimo.put("fax", cvDatiPersonali.getFax());
		cvIesimo.put("flgInviato", cvDatiPersonali.getFlagInviato());
		cvIesimo.put("dataScadenza", cvDatiPersonali.getDtScadenza());
		
		/*
		 * Per discriminare se considerare le decodifiche SIL o le altre non è possibile utilizzare opzTipoDecodifiche
		 * perchè ad esempio l'umbria ha questo campo valorizzato a S e non utilizza le decodifiche SIL.
		 * Di conseguenza il discriminante è il flagIdo
		 */
		
		boolean flagIdo = cvDatiPersonali.getFlagIdo() != null ? cvDatiPersonali.getFlagIdo(): false;
		
		JSONArray arrIstruzione = new JSONArray();
		Set<CvIstruzione> datiIstruzioni = cvDatiPersonali.getCvIstruziones();
		for (CvIstruzione cvIstruzione : datiIstruzioni) {
			JSONObject istrIesimo = new JSONObject();
			istrIesimo.put("titolo", cvIstruzione.getDeTitolo().getDescrizione());
			// istrIesimo.put("nomeIstituto",
			// cvIstruzione.getNomeIstituto());
			istrIesimo.put("anno", cvIstruzione.getNumAnno());
			// istrIesimo.put("tematicheTrattate",
			// cvIstruzione.getTematicheTrattate());
			// istrIesimo.put("votazione",
			// cvIstruzione.getVotazione());

			arrIstruzione.put(istrIesimo);
		}
		cvIesimo.put("listaIstruzione", arrIstruzione);

		JSONArray arrFormazione = new JSONArray();
		Set<CvFormazione> datiFormazione = cvDatiPersonali.getCvFormaziones();
		for (CvFormazione cvFormazione : datiFormazione) {
			JSONObject formIesimo = new JSONObject();
			formIesimo.put("titolo", cvFormazione.getTitoloCorso());
			// formIesimo.put("corso",
			// (cvFormazione.getDeCorso() ==
			// null ? null :
			// cvFormazione.getDeCorso().getDescrizione()));
			// formIesimo.put("nomeIstituto",
			// cvFormazione.getNomeIstituto());
			formIesimo.put("anno", cvFormazione.getNumAnno());
			formIesimo.put("tematicheTrattate", cvFormazione.getPrincipaliTematiche());
			// formIesimo.put("livelloClassificazione",
			// cvFormazione.getLivelloClassificazione());

			arrFormazione.put(formIesimo);
		}
		cvIesimo.put("listaFormazione", arrFormazione);

		JSONArray arrEsperienze = new JSONArray();
		Set<CvEsperienzeProf> datiEsperienze = cvDatiPersonali.getCvEsperienzeProfs();
		for (CvEsperienzeProf cvEsperienze : datiEsperienze) {
			JSONObject espIesimo = new JSONObject();
			espIesimo.put("da", cvEsperienze.getDa());
			espIesimo.put("a", cvEsperienze.getA());
			espIesimo.put("datoreLavoro", cvEsperienze.getDatoreLavoro());
			// espIesimo.put("attivitaDatore",
			// (cvEsperienze.getDeAttivita() ==
			// null ? null :
			// cvEsperienze.getDeAttivita().getDescrizione()));
			// espIesimo.put("mansioneMin",
			// cvEsperienze.getDeMansioneMin()
			// == null ? null :
			// cvEsperienze.getDeMansioneMin().getDescrizione());
			
			if (flagIdo) {
				espIesimo.put("mansione",
						cvEsperienze.getDeBpMansione() == null ? null : cvEsperienze.getDeBpMansione().getDescrizione());
			} else {
				espIesimo.put("mansione",
					cvEsperienze.getDeMansione() == null ? null : cvEsperienze.getDeMansione().getDescrizione());
			}
			// espIesimo.put("contratto",
			// cvEsperienze.getDeContratto() ==
			// null ? null :
			// cvEsperienze.getDeContratto().getDescrizione());
			// espIesimo.put("attivita",
			// cvEsperienze.getAttivitaResponsabilita());

			arrEsperienze.put(espIesimo);
		}
		cvIesimo.put("listaEsperienze", arrEsperienze);

		JSONArray arrLingua = new JSONArray();
		Set<CvLingua> datiLingua = cvDatiPersonali.getCvLinguas();
		for (CvLingua cvLingua : datiLingua) {
			JSONObject linIesimo = new JSONObject();
			linIesimo.put("lingua", cvLingua.getDeLingua() == null ? null : cvLingua.getDeLingua().getDenominazione());

			arrLingua.put(linIesimo);
		}
		cvIesimo.put("listaLingua", arrLingua);

		JSONArray arrProfDes = new JSONArray();
		Set<CvProfDesiderate> datiProfDes = cvDatiPersonali.getCvProfDesiderates();
		for (CvProfDesiderate cvProfDesiderate : datiProfDes) {
			JSONObject profIesimo = new JSONObject();

			if (flagIdo) {
				/*
				 * Per la professione desiderata la nuova gestione IDO valorizza solo descrizioneProfessione mentre la
				 * vecchia entrambe. Si è deciso di ritornare una o l'altra mantenendo la stessa chiave
				 */
				profIesimo.put("breveDescrMansione", cvProfDesiderate.getDescrizioneProfessione());

				profIesimo.put("mansioneMin", cvProfDesiderate.getDeBpMansione() == null ? null
						: cvProfDesiderate.getDeBpMansione().getDescrizione());
			} else {
				profIesimo.put("breveDescrMansione", cvProfDesiderate.getBreveDescrProfessione());

				profIesimo.put("mansioneMin", cvProfDesiderate.getDeMansioneMin() == null ? null
						: cvProfDesiderate.getDeMansioneMin().getDescrizione());
			}
			// profIesimo.put("descrizioneMansione",
			// cvProfDesiderate.getDescrizioneProfessione());
			profIesimo.put("flagDispMezzoProprio", cvProfDesiderate.getFlagDispMezzoProprio());
			profIesimo.put("flagDispTrasferte", cvProfDesiderate.getFlagDispTrasferte());
			profIesimo.put("flagEspSettore", cvProfDesiderate.getFlagEspSettore());
			
			if (cvProfDesiderate.getDeTrasferta() != null) {
				profIesimo.put("tipoDisponibilita", cvProfDesiderate.getDeTrasferta().getDescrizione());
			}

			arrProfDes.put(profIesimo);
		}
		cvIesimo.put("listaProfDesiderate", arrProfDes);

		JSONArray arrCompTrasv = new JSONArray();
		Set<CvCompetenzeTrasv> datiCompTras = cvDatiPersonali.getCvCompetenzeTrasvs();
		for (CvCompetenzeTrasv cvCompetenzeTrasv : datiCompTras) {
			JSONObject trasvIesimo = new JSONObject();
			trasvIesimo.put("capCompOrganizzative", cvCompetenzeTrasv.getCapCompOrganizzative());
			trasvIesimo.put("capCompRelInterpersonali", cvCompetenzeTrasv.getCapCompRelInterpersonali());
			trasvIesimo.put("capCompTecniche", cvCompetenzeTrasv.getCapCompTecniche());
			trasvIesimo.put("capCompAltre", cvCompetenzeTrasv.getCapCompAltre());

			arrCompTrasv.put(trasvIesimo);
		}
		cvIesimo.put("listaCompetenzeTrasv", arrCompTrasv);

		JSONArray arrConInfo = new JSONArray();
		CvInformatica datiInfo = cvDatiPersonali.getCvInformatica();
		if (datiInfo != null) {
			JSONObject infoIesimo = new JSONObject();
			infoIesimo.put("conoscenzeInformatiche", datiInfo.getDescrizione());

			arrConInfo.put(infoIesimo);
		}
		cvIesimo.put("listaConoscenzeInfo", arrConInfo);

		JSONArray arrPatenti = new JSONArray();
		Set<CvPatente> datiPatente = cvDatiPersonali.getCvPatentes();
		for (CvPatente cvPatente : datiPatente) {
			JSONObject patenteIesimo = new JSONObject();
			
			if (flagIdo) {
				patenteIesimo.put("patente", cvPatente.getDePatenteSil().getDescrizione());
			} else {			
				patenteIesimo.put("patente", cvPatente.getDePatente().getDescrizione());
			}
			arrPatenti.put(patenteIesimo);
		}
		Set<CvPatentino> datiPatentini = cvDatiPersonali.getCvPatentinos();
		for (CvPatentino cvPatentino : datiPatentini) {
			JSONObject patentinoIesimo = new JSONObject();
			if (flagIdo) {
				patentinoIesimo.put("patentino", cvPatentino.getDePatentinoSil().getDescrizione());
			} else {
				patentinoIesimo.put("patentino", cvPatentino.getDePatentino().getDescrizione());
			}
			arrPatenti.put(patentinoIesimo);
		}
		cvIesimo.put("listaPatenti", arrPatenti);

		JSONArray arrAlbo = new JSONArray();
		Set<CvAlbo> datiAlbo = cvDatiPersonali.getCvAlbos();
		for (CvAlbo cvAlbo : datiAlbo) {
			JSONObject alboIesimo = new JSONObject();
			
			if (flagIdo) {
				alboIesimo.put("albo", cvAlbo.getDeAlboSil().getDescrizione());
			} else {
				alboIesimo.put("albo", cvAlbo.getDeAlbo().getDescrizione());
			}
			arrAlbo.put(alboIesimo);
		}
		cvIesimo.put("listaAlbi", arrAlbo);

		JSONArray arrAltre = new JSONArray();
		CvAltreInfo altreInfo = cvDatiPersonali.getCvAltreInfo();
		if (altreInfo != null) {
			JSONObject altreIesimo = new JSONObject();
			altreIesimo.put("automunito", altreInfo.getFlagAutomunito());
			altreIesimo.put("motomunito", altreInfo.getFlagMotomunito());
			altreIesimo.put("trasferta",
					(altreInfo.getDeTrasferta() == null ? "" : altreInfo.getDeTrasferta().getDescrizione()));
			altreIesimo.put("ulteriotiInfo", altreInfo.getUlterioriInfo());

			arrAltre.put(altreIesimo);
		}
		cvIesimo.put("listaAltreInfo", arrAltre);

		return cvIesimo;
	}

	private JSONObject getJsonObjectDettaglio(CvDatiPersonali cvDatiPersonali, CvAllegato cvAllegato) throws JSONException {
		JSONObject cvIesimo = new JSONObject();
		cvIesimo.put(ID, cvDatiPersonali.getIdCvDatiPersonali());
		cvIesimo.put(DESCRIZIONE, cvDatiPersonali.getDescrizione());
		cvIesimo.put(DATAMODIFICA, cvDatiPersonali.getDtmMod());
		cvIesimo.put("dataNascita", cvDatiPersonali.getDtNascita());
		cvIesimo.put("indirizzoDomicilio", cvDatiPersonali.getIndirizzoDomicilio());
		cvIesimo.put("comuneDomicilio", cvDatiPersonali.getDeComuneDomicilio().getDenominazione());
		cvIesimo.put("telefono", cvDatiPersonali.getTel1());
		cvIesimo.put("cellulare", cvDatiPersonali.getTel2());
		cvIesimo.put("email", cvDatiPersonali.getEmail());
		cvIesimo.put("fax", cvDatiPersonali.getFax());
		cvIesimo.put("flgInviato", cvDatiPersonali.getFlagInviato());
		cvIesimo.put("dataScadenza", cvDatiPersonali.getDtScadenza());
		cvIesimo.put("hasAttachment", cvAllegato!=null);
		cvIesimo.put(FILENAME, cvAllegato!=null?cvAllegato.getFilename():null);
		/*
		 * Per discriminare se considerare le decodifiche SIL o le altre non è possibile utilizzare opzTipoDecodifiche
		 * perchè ad esempio l'umbria ha questo campo valorizzato a S e non utilizza le decodifiche SIL.
		 * Di conseguenza il discriminante è il flagIdo
		 */
		
		boolean flagIdo = cvDatiPersonali.getFlagIdo() != null ? cvDatiPersonali.getFlagIdo(): false;

		JSONArray arrIstruzione = new JSONArray();
		Set<CvIstruzione> datiIstruzioni = cvDatiPersonali.getCvIstruziones();
		for (CvIstruzione cvIstruzione : datiIstruzioni) {
			JSONObject istrIesimo = new JSONObject();
			istrIesimo.put("titolo", cvIstruzione.getDeTitolo().getDescrizione());
			// istrIesimo.put("nomeIstituto",
			// cvIstruzione.getNomeIstituto());
			istrIesimo.put("anno", cvIstruzione.getNumAnno());
			// istrIesimo.put("tematicheTrattate",
			// cvIstruzione.getTematicheTrattate());
			// istrIesimo.put("votazione",
			// cvIstruzione.getVotazione());

			arrIstruzione.put(istrIesimo);
		}
		cvIesimo.put("listaIstruzione", arrIstruzione);

		JSONArray arrFormazione = new JSONArray();
		Set<CvFormazione> datiFormazione = cvDatiPersonali.getCvFormaziones();
		for (CvFormazione cvFormazione : datiFormazione) {
			JSONObject formIesimo = new JSONObject();
			formIesimo.put("titolo", cvFormazione.getTitoloCorso());
			formIesimo.put("corso",
					(cvFormazione.getDeCorso() == null ? null : cvFormazione.getDeCorso().getDescrizione()));
			// formIesimo.put("nomeIstituto",
			// cvFormazione.getNomeIstituto());
			formIesimo.put("anno", cvFormazione.getNumAnno());
			formIesimo.put("tematicheTrattate", cvFormazione.getPrincipaliTematiche());
			// formIesimo.put("livelloClassificazione",
			// cvFormazione.getLivelloClassificazione());

			arrFormazione.put(formIesimo);
		}
		cvIesimo.put("listaFormazione", arrFormazione);

		JSONArray arrEsperienze = new JSONArray();
		Set<CvEsperienzeProf> datiEsperienze = cvDatiPersonali.getCvEsperienzeProfs();
		for (CvEsperienzeProf cvEsperienze : datiEsperienze) {
			JSONObject espIesimo = new JSONObject();
			espIesimo.put("da", cvEsperienze.getDa());
			espIesimo.put("a", cvEsperienze.getA());
			espIesimo.put("datoreLavoro", cvEsperienze.getDatoreLavoro());
			// espIesimo.put("attivitaDatore",
			// (cvEsperienze.getDeAttivita() == null
			// ? null :
			// cvEsperienze.getDeAttivita().getDescrizione()));
			// espIesimo.put("mansioneMin",
			// cvEsperienze.getDeMansioneMin() ==
			// null ? null :
			// cvEsperienze.getDeMansioneMin().getDescrizione());

			if (flagIdo) {
				espIesimo.put("mansione",
						cvEsperienze.getDeBpMansione() == null ? null : cvEsperienze.getDeBpMansione().getDescrizione());
			} else {
				espIesimo.put("mansione",
					cvEsperienze.getDeMansione() == null ? null : cvEsperienze.getDeMansione().getDescrizione());
			}
			// espIesimo.put("contratto",
			// cvEsperienze.getDeContratto() == null
			// ? null :
			// cvEsperienze.getDeContratto().getDescrizione());
			// espIesimo.put("attivita",
			// cvEsperienze.getAttivitaResponsabilita());

			arrEsperienze.put(espIesimo);
		}
		cvIesimo.put("listaEsperienze", arrEsperienze);

		JSONArray arrLingua = new JSONArray();
		Set<CvLingua> datiLingua = cvDatiPersonali.getCvLinguas();
		for (CvLingua cvLingua : datiLingua) {
			JSONObject linIesimo = new JSONObject();
			linIesimo.put("lingua", cvLingua.getDeLingua() == null ? null : cvLingua.getDeLingua().getDenominazione());

			arrLingua.put(linIesimo);
		}
		cvIesimo.put("listaLingua", arrLingua);

		JSONArray arrProfDes = new JSONArray();
		Set<CvProfDesiderate> datiProfDes = cvDatiPersonali.getCvProfDesiderates();
		for (CvProfDesiderate cvProfDesiderate : datiProfDes) {
			JSONObject profIesimo = new JSONObject();

			if (flagIdo) {
				/*
				 * Per la professione desiderata la nuova gestione IDO valorizza solo descrizioneProfessione mentre la
				 * vecchia entrambe. Si è deciso di ritornare una o l'altra mantenendo la stessa chiave
				 */
				profIesimo.put("breveDescrMansione", cvProfDesiderate.getDescrizioneProfessione());

				profIesimo.put("mansioneMin", cvProfDesiderate.getDeBpMansione() == null ? null
						: cvProfDesiderate.getDeBpMansione().getDescrizione());
			} else {
				profIesimo.put("breveDescrMansione", cvProfDesiderate.getBreveDescrProfessione());

				profIesimo.put("mansioneMin", cvProfDesiderate.getDeMansioneMin() == null ? null
						: cvProfDesiderate.getDeMansioneMin().getDescrizione());
			}
			// profIesimo.put("descrizioneMansione",
			// cvProfDesiderate.getDescrizioneProfessione());
			profIesimo.put("flagDispMezzoProprio", cvProfDesiderate.getFlagDispMezzoProprio());
			profIesimo.put("flagDispTrasferte", cvProfDesiderate.getFlagDispTrasferte());
			profIesimo.put("flagEspSettore", cvProfDesiderate.getFlagEspSettore());

			if (cvProfDesiderate.getDeTrasferta() != null) {
				profIesimo.put("tipoDisponibilita", cvProfDesiderate.getDeTrasferta().getDescrizione());
			}

			arrProfDes.put(profIesimo);
		}
		cvIesimo.put("listaProfDesiderate", arrProfDes);

		JSONArray arrCompTrasv = new JSONArray();
		Set<CvCompetenzeTrasv> datiCompTras = cvDatiPersonali.getCvCompetenzeTrasvs();
		for (CvCompetenzeTrasv cvCompetenzeTrasv : datiCompTras) {
			JSONObject trasvIesimo = new JSONObject();
			trasvIesimo.put("capCompOrganizzative", cvCompetenzeTrasv.getCapCompOrganizzative());
			trasvIesimo.put("capCompRelInterpersonali", cvCompetenzeTrasv.getCapCompRelInterpersonali());
			trasvIesimo.put("capCompTecniche", cvCompetenzeTrasv.getCapCompTecniche());
			trasvIesimo.put("capCompAltre", cvCompetenzeTrasv.getCapCompAltre());

			arrCompTrasv.put(trasvIesimo);
		}
		cvIesimo.put("listaCompetenzeTrasv", arrCompTrasv);

		JSONArray arrConInfo = new JSONArray();
		CvInformatica datiInfo = cvDatiPersonali.getCvInformatica();
		if (datiInfo != null) {
			JSONObject infoIesimo = new JSONObject();
			infoIesimo.put("conoscenzeInformatiche", datiInfo.getDescrizione());

			arrConInfo.put(infoIesimo);
		}
		cvIesimo.put("listaConoscenzeInfo", arrConInfo);

		JSONArray arrPatenti = new JSONArray();
		Set<CvPatente> datiPatente = cvDatiPersonali.getCvPatentes();
		for (CvPatente cvPatente : datiPatente) {
			JSONObject patenteIesimo = new JSONObject();

			if (flagIdo) {
				patenteIesimo.put("patente", cvPatente.getDePatenteSil().getDescrizione());
			} else {			
				patenteIesimo.put("patente", cvPatente.getDePatente().getDescrizione());
			}
			arrPatenti.put(patenteIesimo);
		}
		Set<CvPatentino> datiPatentini = cvDatiPersonali.getCvPatentinos();
		for (CvPatentino cvPatentino : datiPatentini) {
			JSONObject patentinoIesimo = new JSONObject();
			if (flagIdo) {
				patentinoIesimo.put("patentino", cvPatentino.getDePatentinoSil().getDescrizione());
			} else {
				patentinoIesimo.put("patentino", cvPatentino.getDePatentino().getDescrizione());
			}
			arrPatenti.put(patentinoIesimo);
		}
		cvIesimo.put("listaPatenti", arrPatenti);

		JSONArray arrAlbo = new JSONArray();
		Set<CvAlbo> datiAlbo = cvDatiPersonali.getCvAlbos();
		for (CvAlbo cvAlbo : datiAlbo) {
			JSONObject alboIesimo = new JSONObject();
			
			if (flagIdo) {
				alboIesimo.put("albo", cvAlbo.getDeAlboSil().getDescrizione());
			} else {
				alboIesimo.put("albo", cvAlbo.getDeAlbo().getDescrizione());
			}
			arrAlbo.put(alboIesimo);
		}
		cvIesimo.put("listaAlbi", arrAlbo);

		JSONArray arrAltre = new JSONArray();
		CvAltreInfo altreInfo = cvDatiPersonali.getCvAltreInfo();
		if (altreInfo != null) {
			JSONObject altreIesimo = new JSONObject();
			altreIesimo.put("automunito", altreInfo.getFlagAutomunito());
			altreIesimo.put("motomunito", altreInfo.getFlagMotomunito());
			altreIesimo.put("trasferta",
					(altreInfo.getDeTrasferta() == null ? "" : altreInfo.getDeTrasferta().getDescrizione()));
			altreIesimo.put("ulteriotiInfo", altreInfo.getUlterioriInfo());

			arrAltre.put(altreIesimo);
		}
		cvIesimo.put("listaAltreInfo", arrAltre);

		return cvIesimo;
	}

	private boolean curriculumPresente(CvDatiPersonali cvDatiPersonali, PfPrincipal pfPrincipal) {
		boolean ret = false;

		if (cvDatiPersonali != null && ((cvDatiPersonali.getPfPrincipal() != null
				&& cvDatiPersonali.getPfPrincipal().getIdPfPrincipal().equals(pfPrincipal.getIdPfPrincipal()))
				|| (cvDatiPersonali.getPfPrincipalPalese() != null && cvDatiPersonali.getPfPrincipalPalese()
						.getIdPfPrincipal().equals(pfPrincipal.getIdPfPrincipal())))) {
			// Deve trattarsi di un curriculum dell'utente loggato...
			ret = true;
		}
		return ret;
	}
	
	public String canCreate(String username) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {
				List<CvDatiPersonali> list = this.findAllCurriculaLive(pfPrincipal.getIdPfPrincipal());
				
				int numeroLiveCurricula = list != null ? list.size() : 0;
				
				boolean canCreate = false;
				if (numeroLiveCurricula < ConstantsSingleton.CVMAXCURR) {
					canCreate = true;
				}
				
				JSONObject obj = new JSONObject();
				obj.put(STATUS, OK);
				obj.put(CAN_CREATE, canCreate);

				ret = obj.toString();
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante la verifica di candidatura rispetto alla vacancy");
		}
		return ret;
	}

	public String addCurriculumUtente(String username, String nomeCv, String codComNascita, String codComDomicilio,
			String email, String telefono, String cellulare) throws AppEjbException {

		String ret = null;
		String msgError = "";

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			Date now = new Date();

			if (pfPrincipal != null) {

				/*
				 * ------------------------------- Validazione ----------------------------------------
				 */
				validaCurriculumUtente(username, nomeCv, codComNascita, codComDomicilio, email, telefono, cellulare);

				AppUtils appUtils = new AppUtils();

				CvDatiPersonali cvDatiPersonali = appUtils.initCV();

				cvDatiPersonali.setPfPrincipal(pfPrincipal);
				cvDatiPersonali.setPfPrincipalIns(pfPrincipal);
				cvDatiPersonali.setPfPrincipalMod(pfPrincipal);
				// fixme todo verificare i set principal

				DeComune deComuneByCodComNascita = deComuneHome.findById(codComNascita);
				cvDatiPersonali.setDeComuneNascita(deComuneByCodComNascita);
				if (deComuneByCodComNascita == null) {
					msgError = " codComNascita errato : " + codComNascita;
					throw new Exception(msgError);
				}
				// fixme todo impostare codComNascita

				// Domicilio domicilio = domici
				DeComune deComuneByCodComDomicilio = deComuneHome.findById(codComDomicilio);
				if (deComuneByCodComDomicilio == null) {
					msgError = " codComDomicilio errato : " + codComDomicilio;
					throw new Exception(msgError);
				}
				cvDatiPersonali.setDeComuneDomicilio(deComuneByCodComDomicilio);

				cvDatiPersonali.setDescrizione(nomeCv);
				cvDatiPersonali.setEmail(email);
				cvDatiPersonali.setTel1(telefono);
				if (cellulare != null)
					cvDatiPersonali.setTel2(cellulare);

				cvDatiPersonali.setFlagInviato(false);
				cvDatiPersonali.setDeAmbitoDiffusione(
						deAmbitoDiffusioneHome.findById(ConstantsSingleton.DeAmbitoDiffusione.REGIONALE));
				cvDatiPersonali.setProvenienzaCurriculum(
						deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
				cvDatiPersonali.setVisibilita(true);
				cvDatiPersonali.setFlagEliminato(false);
				cvDatiPersonali.setFlagApp(true);
				cvDatiPersonali.setDtmIns(now);
				cvDatiPersonali.setDtmMod(now);

				// Data di scadenza del curriculum: 30 per regione != TN, 60 per regione = TN
				cvDatiPersonali.setDtScadenza(appUtils.calcolaDtaScadenzaCV(new Date()));

				/*
				 * ------------------------------- Persist -------------------------------
				 */

				cvDatiPersonaliHome.persist(cvDatiPersonali);
				// cvDatiPersonaliHome.persistFlush(cvDatiPersonali);

				/*
				 * -------------------------------Ret Json object -------------------------------------
				 */
				JSONObject obj = new JSONObject();
				obj.put(CheckerSec.STATUS, CheckerSec.OK);
				obj.put(ID, cvDatiPersonali.getIdCvDatiPersonali());

				ret = obj.toString();

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il salvataggio del curriculum " + msgError);
		}
		return ret;
	}

	private void validaCurriculumUtente(String username, String nomeCv, String codComNascita, String codComDomicilio,
			String email, String telefono, String cellulare) throws AppEjbException {

		if (StringUtils.isBlank(username))
			throw new EmptyParameterException("username");
		else if (StringUtils.isBlank(nomeCv))
			throw new EmptyParameterException("nomeCv");
		else if (StringUtils.isBlank(codComNascita))
			throw new EmptyParameterException("codComNascita");
		else if (StringUtils.isBlank(codComDomicilio))
			throw new EmptyParameterException("codComDomicilio");
		else if (StringUtils.isBlank(email))
			throw new EmptyParameterException("email");
		else if (StringUtils.isBlank(telefono))
			throw new EmptyParameterException("telefono");

	}

	public String delCurriculumUtente(String username, Integer idCv) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			// Json di ritorno
			JSONObject obj = new JSONObject();

			if (pfPrincipal != null) {
				CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(idCv);

				if (curriculumPresente(cvDatiPersonali, pfPrincipal)) {
					obj.put(STATUS, OK);

					cvDatiPersonaliHome.deleteCurriculum(idCv, pfPrincipal.getIdPfPrincipal());

					ret = obj.toString();
				} else {
					throw new CurriculumUtenteNotFoundException(idCv);
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore in fase di eliminazione del curriculum utente");
		}
		return ret;

	}

	public String uploadFileCurriculumVitae(String username, Integer idCvDatiPersonali, String filename, byte[] data)
			throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {

				// Validazione informazioni obbligatorie
				validaUploadFileCurriculumVitae(idCvDatiPersonali, filename, data);

				// Lettura di CvDatiPersonali al quale agganciare l'allegato
				CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(idCvDatiPersonali);

				if (curriculumPresente(cvDatiPersonali, pfPrincipal)) {

					CvAllegato cvAllegato = cvAllegatoHome
							.findEntityByCurriculumId(cvDatiPersonali.getIdCvDatiPersonali());

					// Fill CvAllegato
					Date now = new Date();
					if (cvAllegato == null) {
						cvAllegato = new CvAllegato();
						cvAllegato.setCvDatiPersonali(cvDatiPersonali);
						cvAllegato.setDtmIns(now);
						cvAllegato.setPfPrincipalIns(pfPrincipal);
					}

					cvAllegato.setContenuto(data);
					cvAllegato.setFilename(filename);
					cvAllegato.setDtmMod(now);
					cvAllegato.setPfPrincipalMod(pfPrincipal);

					cvAllegato = cvAllegatoHome.merge(cvAllegato);

					// Json Ritorno
					JSONObject obj = new JSONObject();
					obj.put(STATUS, OK);
					ret = obj.toString();

				} else {
					// IdCvDatiPersonali non trovato
					throw new CurriculumUtenteNotFoundException(idCvDatiPersonali);
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di inserimento dell'allegato al curriculum vitae");
		}
		return ret;
	}

	public String downloadFileCurriculumVitae(String username, Integer idCvDatiPersonali) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {

				// Validazione informazioni obbligatorie
				if (idCvDatiPersonali == null) {
					throw new EmptyParameterException("idCvDatiPersonali");
				}

				// Lettura dell'allegato da ritornare
				CvAllegato cvAllegato = cvAllegatoHome.findEntityByCurriculumId(idCvDatiPersonali);

				if (cvAllegato != null && curriculumPresente(cvAllegato.getCvDatiPersonali(), pfPrincipal)) {
					// Json Ritorno
					JSONObject obj = new JSONObject();
					obj.put(STATUS, OK);
					obj.put(CONTENT, Base64.encodeBase64String(cvAllegato.getContenuto()));
					obj.put(FILENAME, cvAllegato.getFilename());
					ret = obj.toString();

				} else {
					// Allegato non trovato
					throw new AllegatoCurriculumUtenteNotFoundException(idCvDatiPersonali);
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di recupero dell'allegato al curriculum vitae");
		}
		return ret;
	}

	private void validaUploadFileCurriculumVitae(Integer idCvDatiPersonali, String fileName, byte[] fileData)
			throws AppEjbException {
		if (idCvDatiPersonali == null) {
			throw new EmptyParameterException("idCvDatiPersonali");
		} else if (fileName == null || fileName.isEmpty()) {
			throw new EmptyParameterException("filename");
		} else if (fileData == null) {
			throw new EmptyParameterException("data");
		}
	}

	public String delCvAllegato(String username, Integer idCvDatiPersonali) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null && pfPrincipal.getUtenteInfo() != null) {

				cvAllegatoHome.removeByCurriculumId(idCvDatiPersonali);

				JSONObject obj = new JSONObject();
				// Json Ritorno
				obj.put(STATUS, OK);
				ret = obj.toString();

			} else {
				// IdCvDatiPersonali non trovato
				throw new WrongParameterException("idCvDatiPersonali");
			}

		} catch (

		JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di cancellazione dell'allegato al curriculum vitae");
		}
		return ret;
	}

}

class CvDatiPersonaliComparator implements Comparator<CvDatiPersonali> {

	@Override
	public int compare(CvDatiPersonali o1, CvDatiPersonali o2) {
		return o1.getDtmMod().compareTo(o2.getDtmMod());
	}

}
