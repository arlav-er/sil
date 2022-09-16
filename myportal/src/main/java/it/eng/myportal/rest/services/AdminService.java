package it.eng.myportal.rest.services;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.ejb.TextSingletonEjb;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PtPortletHome;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless
@Path("/rest/admin")
public class AdminService {

	protected static Log log = LogFactory.getLog(AdminService.class);

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	PtPortletHome ptPortletHome;

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	YgAdesioneHome ygAdesioneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	TextSingletonEjb textMap;

	private final static String OK = "ok";
	private final static String KO = "ko";
	private final static String STATUS = "status";
	private final static String INFO = "info";
	private final static String ERROR = "error";

	@POST
	@Path("/reloadTexts/{tokenSecurity}")
	@Produces("application/json")
	public String reloadTexts(@PathParam("tokenSecurity") String tokenSecurity) {

		JSONObject obj = new JSONObject();

		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".reloadTexts");

		try {

			String completeUrl = ConstantsSingleton.BASE_URL + "/secure/rest/admin/reloadTexts/";

			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {

				if (Utils.isTokenSecurityCorrect(tokenSecurity, completeUrl)) {
					textMap.refillTesti();
				}

				log.info("Messaggi ricaricati correttamente");
				try {
					obj.put(STATUS, OK);
					obj.put(INFO, "Messaggi ricaricati correttamente");
				} catch (JSONException e1) {
					log.error(e1);
				}

			}

		} catch (Exception e) {

			log.error("Errore interno: " + e);
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e1);
			}

		} finally {

			jamonMonitor.stop();

		}

		return obj.toString();

	}

	@POST
	@Path("/addPortlets/{tokenSecurity}")
	@Produces("application/json")
	public String addPortlets(@PathParam("tokenSecurity") String tokenSecurity,
			@FormParam("idPfPrincipal") Integer idPfPrincipal, @FormParam("codTipoGruppo") String codTipoGruppo,
			@FormParam("idPfPrincipalIns") Integer idPfPrincipalIns) {

		JSONObject obj = new JSONObject();

		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".addPortlets");

		try {

			String completeUrl = ConstantsSingleton.BASE_URL + "/secure/rest/admin/addPortlets/";

			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {

				if (Utils.isTokenSecurityCorrect(tokenSecurity, completeUrl)) {

					PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);

					if (pfPrincipal != null) {

						PfPrincipal pfPrincipalIns = pfPrincipalHome.findById(idPfPrincipalIns);

						if (pfPrincipalIns != null) {

							List<PtPortlet> portlets = null;
							portlets = ptPortletHome.findByCodRuoloPortale(codTipoGruppo);

							if (portlets != null) {
								for (int i = 0; i < portlets.size(); i++) {
									PtPortlet iesimaPortlet = portlets.get(i);

									if (!ConstantsSingleton.PtPortlet.PORTLET_NASCOSTE
											.contains(iesimaPortlet.getNome())) {
										PtScrivania iesimaScrivania = new PtScrivania();
										iesimaScrivania.setPfPrincipal(pfPrincipal);
										iesimaScrivania.setFlagRidotta(false);
										iesimaScrivania.setFlagVisualizza(true);
										iesimaScrivania.setPtPortlet(iesimaPortlet);
										iesimaScrivania.setDtmIns(new Date());
										iesimaScrivania.setDtmMod(new Date());
										iesimaScrivania.setPfPrincipalIns(pfPrincipalIns);
										iesimaScrivania.setPfPrincipalMod(pfPrincipalIns);

										if (("_portlet_yg").equalsIgnoreCase(iesimaPortlet.getNome())) {
											iesimaScrivania.setPosizione(0);
											iesimaScrivania.setOptColonna("L");
										} else {
											iesimaScrivania.setPosizione((i + 1) % 5);
											iesimaScrivania.setOptColonna(((i + 1) % 2 == 0) ? "L" : "R");
										}

										ptScrivaniaHome.persist(iesimaScrivania);
									}
									// TODO: perchè chiama anche questa?
									ptScrivaniaHome.findPortletsScrivania(idPfPrincipal);

								}

							}

							log.info("Portlets inserite correttamente");
							try {
								obj.put(STATUS, OK);
								obj.put(INFO, "Portlets inserite correttamente");
							} catch (JSONException e1) {
								log.error(e1);
							}

						} else {

							// pfPrincipal per inserimento non trovato
							log.error("Errore pfPrincipal per inserimento non trovato.");
							try {
								obj.put(STATUS, KO);
								obj.put(ERROR, "parametri errati (pfPrincipal per inserimento non trovato)");
							} catch (JSONException e1) {
								log.error(e1);
							}

						}

					} else {

						// pfPrincipal non trovato
						log.error("Errore pfPrincipal non trovato.");
						try {
							obj.put(STATUS, KO);
							obj.put(ERROR, "parametri errati (pfPrincipal non trovato)");
						} catch (JSONException e1) {
							log.error(e1);
						}
					}

				} else {

					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					try {
						obj.put(STATUS, KO);
						obj.put(ERROR, "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}

				}

			} else {

				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				try {
					obj.put(STATUS, KO);
					obj.put(ERROR, "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}

			}

		} catch (Exception e) {

			log.error("Errore interno: " + e);
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e1);
			}

		} finally {

			jamonMonitor.stop();

		}

		return obj.toString();

	}

	@POST
	@Path("/updateAdesioneYG/{tokenSecurity}")
	@Produces("application/json")
	public String addPortlets(@PathParam("tokenSecurity") String tokenSecurity,
			@FormParam("idPfPrincipal") Integer idPfPrincipal, @FormParam("codProvinciaRif") String codProvinciaRif) {

		JSONObject obj = new JSONObject();

		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".updateAdesioneYG");

		try {
			boolean update = false;
			String completeUrl = ConstantsSingleton.BASE_URL + "/secure/rest/admin/updateAdesioneYG/";

			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {

				if (Utils.isTokenSecurityCorrect(tokenSecurity, completeUrl)) {

					PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
					if (pfPrincipal != null) {
						YgAdesioneDTO ygAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipal);
						if (ygAdesioneDTO != null && ygAdesioneDTO.getDtPresaInCarico() == null) {
							if (codProvinciaRif != null) {
								DeProvinciaDTO provRif = deProvinciaHome.findDTOById(codProvinciaRif);
								ygAdesioneDTO.setDeProvinciaNotifica(provRif);
								ygAdesioneDTO.setDeCpiAssegnazione(null);
								ygAdesioneHome.mergeDTO(ygAdesioneDTO, idPfPrincipal);

								update = true;
							}
						}

						if (update) {
							log.info("Adesione aggiornata correttamente");
							try {
								obj.put(STATUS, OK);
								obj.put(INFO, "Adesione aggiornata correttamente");
							} catch (JSONException e1) {
								log.error(e1);
							}
						} else {
							log.info("Adesione non trovata");
							try {
								obj.put(STATUS, OK);
								obj.put(INFO, "Adesione non trovata");
							} catch (JSONException e1) {
								log.error(e1);
							}
						}

					} else {
						// pfPrincipal non trovato
						log.error("Errore pfPrincipal non trovato.");
						try {
							obj.put(STATUS, KO);
							obj.put(ERROR, "parametri errati (pfPrincipal non trovato)");
						} catch (JSONException e1) {
							log.error(e1);
						}
					}
				} else {

					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					try {
						obj.put(STATUS, KO);
						obj.put(ERROR, "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}

				}

			} else {

				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				try {
					obj.put(STATUS, KO);
					obj.put(ERROR, "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}

			}

		} catch (Exception e) {

			log.error("Errore interno: " + e);
			try {
				obj.put(STATUS, KO);
				obj.put(ERROR, "Errore interno");
			} catch (JSONException e1) {
				log.error(e1);
			}

		} finally {

			jamonMonitor.stop();

		}

		return obj.toString();

	}

}
