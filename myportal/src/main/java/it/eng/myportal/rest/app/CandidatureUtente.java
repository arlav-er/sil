package it.eng.myportal.rest.app;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.ejb.stateless.app.CandidatureUtenteEjb;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.App;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per recuperare la lista dei curriculum dell'utente collegato
 * 
 * @author
 *
 */
@Stateless
@Path("/nocas/app/candidatura")
public class CandidatureUtente {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	CandidatureUtenteEjb candidatureUtenteEjb;

	protected static Log log = LogFactory.getLog(CurriculumUtente.class);

	private final static String OK = "ok";
	private final static String KO = "ko";
	private final static String STATUS = "status";
	private final static String ERROR = "error";

	private final static String LISTACANDIDATURE = "listaCandidature";
	private final static String CANDIDATURA = "candidatura";
	private final static String POSIZIONE_CANDIDATURA = "posizioneCandidatura";

	private final static String ID = "id";

	private static final String JSON_CHECK_CANDIDATURA_VACANCY = "checkCandidaturaVacancy/";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/candidatura/";
	private static final String PATH_CHECK_CANDIDATURA_VACANCY = ROOT_APP_PATH + JSON_CHECK_CANDIDATURA_VACANCY;

	@GET
	@Path("/list/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getListaCandidature(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".getListaCandidature");
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/candidatura/list/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					JSONObject obj = new JSONObject();
					String username = Utils.getUsernameFromTokenUtente(token);
					PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
					if (pfPrincipal != null) {
						List<AcVisualizzaCandidaturaDTO> listaCandidature = acCandidaturaHome
								.findDtosByIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
						obj.put(STATUS, OK);
						JSONArray arrCandidature = new JSONArray();
						for (AcVisualizzaCandidaturaDTO acVisualizzaCandidaturaDTO : listaCandidature) {

							if (acVisualizzaCandidaturaDTO.getIdVaDatiVacancy() != null) {
								JSONObject candidaturaIesima = new JSONObject();

								candidaturaIesima.put(ID, acVisualizzaCandidaturaDTO.getId());
								candidaturaIesima.put("nome", acVisualizzaCandidaturaDTO.getNomeCandidato());
								candidaturaIesima.put("cognome", acVisualizzaCandidaturaDTO.getCognomeCandidato());
								candidaturaIesima.put("idVaDatiVacancy",
										acVisualizzaCandidaturaDTO.getIdVaDatiVacancy());
								candidaturaIesima.put("descrizioneVacancy",
										acVisualizzaCandidaturaDTO.getDescrizioneVacancy());
								candidaturaIesima.put("ragioneSociale",
										acVisualizzaCandidaturaDTO.getRagioneSocialeAz());
								candidaturaIesima.put("idCurriculum",
										acVisualizzaCandidaturaDTO.getIdCvDatiPersonali());
								candidaturaIesima.put("nomeCv", acVisualizzaCandidaturaDTO.getDescrizioneCurriculum());
								candidaturaIesima.put("idLettera", acVisualizzaCandidaturaDTO.getIdCvLetteraAcc());
								candidaturaIesima.put("nomeLettera", acVisualizzaCandidaturaDTO.getNomeLetteraAcc());
								candidaturaIesima.put("data", acVisualizzaCandidaturaDTO.getDtmMod());
								candidaturaIesima.put("commento", acVisualizzaCandidaturaDTO.getCommento());
								
								arrCandidature.put(candidaturaIesima);
							}
						}
						obj.putOpt(LISTACANDIDATURE, arrCandidature);
					} else {
						obj.put(STATUS, KO);
						obj.put(ERROR, "parametri errati");
					}
					return obj.toString();
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			log.error("Errore recupero curriculum.");
			JSONObject obj = new JSONObject();
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e.getMessage());
			}
			return obj.toString();
		} finally {
			jamonMonitor.stop();
		}
	}

	@GET
	@Path("/dettaglio/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getCandidatura(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("idCandidatura") String idCandidatura) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".getListaCandidature");
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();
			// IdCandidatura
			validator.setParam(idCandidatura);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/candidatura/dettaglio/";
			// TODO: per la validazione del tokenSecurity usare la nuova classe CheckerSec
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					JSONObject obj = new JSONObject();
					String username = Utils.getUsernameFromTokenUtente(token);
					PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
					if (pfPrincipal != null) {
						AcCandidatura candidatura = acCandidaturaHome.findById(new Integer(idCandidatura));
						JSONArray arrCandidature = new JSONArray();
						if (candidaturaPresente(candidatura, pfPrincipal)) {
							AcVisualizzaCandidaturaDTO acVisualizzaCandidaturaDTO = acCandidaturaHome
									.toVisualizzaDTO(candidatura);
							obj.put(STATUS, OK);
							JSONObject candidaturaIesima = new JSONObject();
							if (acVisualizzaCandidaturaDTO.getIdVaDatiVacancy() != null) {
								candidaturaIesima.put(ID, acVisualizzaCandidaturaDTO.getId());
								candidaturaIesima.put("nome", acVisualizzaCandidaturaDTO.getNomeCandidato());
								candidaturaIesima.put("cognome", acVisualizzaCandidaturaDTO.getCognomeCandidato());
								candidaturaIesima.put("idVaDatiVacancy",
										acVisualizzaCandidaturaDTO.getIdVaDatiVacancy());
								candidaturaIesima.put("descrizioneVacancy",
										acVisualizzaCandidaturaDTO.getDescrizioneVacancy());
								candidaturaIesima.put("ragioneSociale",
										acVisualizzaCandidaturaDTO.getRagioneSocialeAz());
								candidaturaIesima.put("idCurriculum",
										acVisualizzaCandidaturaDTO.getIdCvDatiPersonali());
								candidaturaIesima.put("nomeCv", acVisualizzaCandidaturaDTO.getDescrizioneCurriculum());
								candidaturaIesima.put("idLettera", acVisualizzaCandidaturaDTO.getIdCvLetteraAcc());
								candidaturaIesima.put("nomeLettera", acVisualizzaCandidaturaDTO.getNomeLetteraAcc());
								candidaturaIesima.put("data", acVisualizzaCandidaturaDTO.getDtmMod());
								candidaturaIesima.put("commento", acVisualizzaCandidaturaDTO.getCommento());

								arrCandidature.put(candidaturaIesima);
							}
							obj.putOpt(LISTACANDIDATURE, arrCandidature);
						} else {
							obj.put(STATUS, KO);
							obj.put(ERROR, "candidatura non presente");
						}
					} else {
						obj.put(STATUS, KO);
						obj.put(ERROR, "parametri errati");
					}
					return obj.toString();
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			log.error("Errore recupero");
			JSONObject obj = new JSONObject();
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e.getMessage());
			}
			return obj.toString();
		} finally {
			jamonMonitor.stop();
		}
	}

	@POST
	@Path("/add/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String addCandidatura(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("idVaDatiVacancy") String idVaDatiVacancy,
			@FormParam("idCvDatiPersonali") String idCvDatiPersonali,
			@FormParam("idCvLetteraAcc") String idCvLetteraAcc, @FormParam("commento") String commento) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".addCandidatura");
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();
			// IdVaDatiVacancy
			validator.setParam(idVaDatiVacancy);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// IdCvDatiPersonali
			validator.setParam(idCvDatiPersonali);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// IdCvLetteraAcc
			validator.setParam(idCvLetteraAcc);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// Commento
			validator.setParam(commento);
			// om20200221:rimosso limitazione pattern
			//validator.checkMaxLengthMessaggio().checkPatternMessaggio();
			validator.checkMaxLengthMessaggio();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/candidatura/add/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					JSONObject obj = new JSONObject();
					String username = Utils.getUsernameFromTokenUtente(token);
					PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
					if (pfPrincipal != null) {
						AcCandidaturaDTO candidatura = acCandidaturaHome.findDTOByUtenteAndAziendaIdPfPrincipal(
								pfPrincipal.getIdPfPrincipal(), null, new Integer(idVaDatiVacancy));
						if (candidatura == null) {
							/*
							 * E' necessario validare il curriculum che si vuole utilizzare per la candidatura per
							 * verificare che non sia scaduto
							 */
							if (candidatureUtenteEjb.validaCvPerCandidatura(new Integer(idCvDatiPersonali))) {
								AcCandidaturaDTO acCandidaturaDTO = new AcCandidaturaDTO();
								acCandidaturaDTO.setIdCvDatiPersonali(new Integer(idCvDatiPersonali));
								if (idCvLetteraAcc != null && !("").equalsIgnoreCase(idCvLetteraAcc)) {
									acCandidaturaDTO.setIdCvLetteraAcc(new Integer(idCvLetteraAcc));
								} else {
									acCandidaturaDTO.setIdCvLetteraAcc(null);
								}
								acCandidaturaDTO.setIdVaDatiVacancy(new Integer(idVaDatiVacancy));
								if (commento != null || !("").equalsIgnoreCase(commento)) {
									acCandidaturaDTO.setCommento(commento);
								} else {
									acCandidaturaDTO.setCommento("Candidatura effettuata da LavoroPerTeMobile");
								}

								VaDatiVacancy vacancy = vaDatiVacancyHome.findById(new Integer(idVaDatiVacancy));
								PfPrincipal princ = vacancy.getPfPrincipal();
								PfPrincipal palese = vacancy.getPfPrincipalPalese();
								if (palese != null) {
									acCandidaturaDTO.setIdPfPrincipalAzienda(palese.getIdPfPrincipal());
								} else {
									acCandidaturaDTO.setIdPfPrincipalAzienda(princ.getIdPfPrincipal());
								}

								// Provenienza candidatura App
								acCandidaturaDTO.setCodProvenienzaCandidatura(ConstantsSingleton.DeProvenienza.COD_APP);

								Boolean checkIns = acCandidaturaHome.inviaCandidatura(acCandidaturaDTO,
										pfPrincipal.getIdPfPrincipal());
								if (!checkIns) {
									obj.put(STATUS, KO);
									obj.put(ERROR, "errore inserimento candidatura");
								} else {
									AcCandidaturaDTO candInserita = acCandidaturaHome
											.findDTOByUtenteAndAziendaIdPfPrincipal(pfPrincipal.getIdPfPrincipal(),
													null, new Integer(idVaDatiVacancy));
									Integer idCandidatura = candInserita.getId();

									/*
									 * Nel caso di IDO Rer è necessario recuperare l'informazione relativa alla
									 * posizione di classifica
									 */
									if (App.NUOVO_IDO) {
										int number = candidatureUtenteEjb.checkNumberOrderedVacancyToCandidate(
												new Integer(idVaDatiVacancy), idCandidatura);
										obj.put(POSIZIONE_CANDIDATURA, number);
									}

									obj.put(STATUS, OK);
									obj.put(CANDIDATURA, "candidatura inserita correttamente");
									obj.put(ID, idCandidatura);
								}
							} else {
								obj.put(STATUS, KO);
								obj.put(ERROR, "Curriculum non valido per candidarsi");
							}
						} else {
							Integer idCandidatura = candidatura.getId();

							obj.put(STATUS, KO);
							obj.put(ERROR, "candidatura già presente");
							obj.put(ID, idCandidatura);
						}
					} else {
						obj.put(STATUS, KO);
						obj.put(ERROR, "parametri errati");
					}
					return obj.toString();
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			log.error("Errore interno" + e);
			JSONObject obj = new JSONObject();
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e.getMessage());
			}
			return obj.toString();
		} finally {
			jamonMonitor.stop();
		}
	}

	@GET
	@Path("/checkCandidaturaVacancy/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String checkCandidaturaVacancy(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("idVaDatiVacancy") String idVaDatiVacancy) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_CHECK_CANDIDATURA_VACANCY);

		String ret = null;

		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();
			// IdVaDatiVacancy
			validator.setParam(idVaDatiVacancy);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = candidatureUtenteEjb.checkCandidaturaVacancy(username, idVaDatiVacancy);
			} else {
				ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
				builder.entity(checkerSec.renderTokenSec().toString());
				Response response = builder.build();

				throw new WebApplicationException(response);
			}
		} catch (DecoderException e) {
			ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
			builder.entity(checkerSec.renderWrongParameters().toString());
			Response response = builder.build();

			throw new WebApplicationException(response);
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} finally {
			jamonMonitor.stop();
		}
		return ret;
	}

	private boolean candidaturaPresente(AcCandidatura candidatura, PfPrincipal pfPrincipal) {
		boolean ret = false;
		if (candidatura != null) {
			if ((candidatura.getCvDatiPersonali().getPfPrincipal() != null && candidatura.getCvDatiPersonali()
					.getPfPrincipal().getIdPfPrincipal().equals(pfPrincipal.getIdPfPrincipal()))
					|| (candidatura.getCvDatiPersonali().getPfPrincipalPalese() != null
							&& candidatura.getCvDatiPersonali().getPfPrincipalPalese().getIdPfPrincipal()
									.equals(pfPrincipal.getIdPfPrincipal()))) {
				// Deve trattarsi di una candidatura dell'utente loggato...
				ret = true;
			}
		}
		return ret;
	}
}
