package it.eng.myportal.ejb.stateless.app;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.base.utils.DateUtils;

@Stateless
public class CandidatureUtenteEjb {

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	private final static String OK = "ok";
	private final static String STATUS = "status";
	private final static String CANDIDATURA_PRESENTE = "candidatura_presente";

	private final static String ID = "id";
	private final static String ID_CANDIDATURA = "id_candidatura";
	private final static String POSIZIONE_CANDIDATURA = "posizioneCandidatura";
	private final static String TOTALE_CANDIDATI = "totaleCandidature";

	public String checkCandidaturaVacancy(String username, String idVaDatiVacancy) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {

				List<AcCandidatura> list = acCandidaturaHome.checkCandidaturaUtente(pfPrincipal.getIdPfPrincipal(),
						null, new Integer(idVaDatiVacancy));

				// om20200219: aggiunto il ritorno dell'id della prima candidatura dell'utente per quella vacancy (non
				// dovvrebbe esserci più di una candidatura)
				Integer idAcCandidatura = null;
				if (list != null && !list.isEmpty()) {
					idAcCandidatura = list.get(0).getIdAcCandidatura();
				}

				JSONObject obj = new JSONObject();
				obj.put(STATUS, OK);
				obj.put(CANDIDATURA_PRESENTE, (list != null && !list.isEmpty()));
				obj.put(ID, idVaDatiVacancy);
				obj.put(ID_CANDIDATURA, idAcCandidatura != null ? idAcCandidatura.toString() : null);

				// Per nuovo IDO Rer si ritorna il numero totale di candidati e nel caso in cui l'utente si è già
				// candidato anche la posizione della sua candidatura
				if (ConstantsSingleton.App.NUOVO_IDO) {
					obj.put(TOTALE_CANDIDATI,
							this.checkTotalNumberOrderedVacancyToCandidate(new Integer(idVaDatiVacancy)));

					Integer posCandidatura = null;
					if (idAcCandidatura != null) {
						int posCandidaturaInt = this.checkNumberOrderedVacancyToCandidate(new Integer(idVaDatiVacancy),
								idAcCandidatura);
						if (posCandidaturaInt > 0) {
							posCandidatura = posCandidaturaInt;
						}
					}
					obj.put(POSIZIONE_CANDIDATURA, posCandidatura);
				}

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

	public int checkNumberOrderedVacancyToCandidate(Integer vacancyId, Integer acCandidaturaId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		for (int j = 0; j < listcandidature.size(); j++) {
			if (listcandidature.get(j).getIdAcCandidatura() == acCandidaturaId.intValue()) {
				i = j;
				break;
			}
		}
		return ++i;
	}

	public int checkTotalNumberOrderedVacancyToCandidate(Integer vacancyId) {
		int i = 0;
		List<AcCandidatura> listcandidature = acCandidaturaHome.findCandidatureByVacancyId(vacancyId);
		if (listcandidature != null && !listcandidature.isEmpty()) {
			i = listcandidature.size();
		}
		return i;
	}

	public boolean validaCvPerCandidatura(Integer idCvDatiPersonali) {
		Boolean ret = false;

		CvDatiPersonali cv = cvDatiPersonaliHome.findById(idCvDatiPersonali);

		Date currentDate = DateUtils.getToday();

		if (cv.getDtScadenza() == null
				|| (DateUtils.dateWithoutHourMinuteSecond(cv.getDtScadenza())).compareTo(currentDate) >= 0) {
			ret = true;
		}

		return ret;
	}

}
