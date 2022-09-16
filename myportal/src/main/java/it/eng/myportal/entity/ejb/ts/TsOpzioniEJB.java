package it.eng.myportal.entity.ejb.ts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.ts.TsOpzioneEnum;
import it.eng.myportal.entity.ts.TsOpzioni;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

@Stateless
public class TsOpzioniEJB extends AbstractHibernateHome<TsOpzioni, Integer> {

	private static final Log log = LogFactory.getLog(TsOpzioniEJB.class);

	@Override
	public TsOpzioni findById(Integer id) {
		return findById(TsOpzioni.class, id);
	}

	@Override
	public String getEntityName() {
		return TsOpzioni.class.getName();
	}

	private TsOpzioni findByCodiceOpzione(TsOpzioneEnum which) throws MyPortalNoResultFoundException {
		TsOpzioni ret = null;
		try {
			ret = entityManager.createNamedQuery("findByCodOpz", TsOpzioni.class).setParameter("parOpz", which)
					.getSingleResult();
			ret.getCodOpzione();
			return ret;
		} catch (NoResultException | NullPointerException e) {
			throw new MyPortalNoResultFoundException(e);
		}
	}

	protected void saveLongOption(Long val, TsOpzioneEnum opt) {
		try {
			TsOpzioni found = findByCodiceOpzione(opt);
			if (found != null) {
				found.setNumValore(val);

				super.merge(found);
			} else {
				found = new TsOpzioni();
				found.setNumValore(val);
				found.setStrValore(null);
				found.setDtValore(null);
				super.persist(found);
			}
		} catch (Exception e) {
			log.error("Error merging/persit option Long: " + e.getMessage());
		}
	}

	protected void saveStringOption(String val, TsOpzioneEnum opt) {
		try {
			TsOpzioni found = findByCodiceOpzione(opt);
			if (found != null) {
				found.setStrValore(val);
				super.merge(found);
			} else {
				found = new TsOpzioni();
				found.setNumValore(null);
				found.setDtValore(null);
				found.setStrValore(val);
				super.persist(found);
			}
		} catch (Exception e) {
			log.error("Error merging/persist option String: " + e.getMessage());
		}
	}

	protected void saveDateTimeOption(TsOpzioneEnum opt, Date dateOpz) {
		try {
			TsOpzioni found = findByCodiceOpzione(opt);

			if (found != null) {

				found.setDtValore(dateOpz);
				super.merge(found);
			} else {
				found = new TsOpzioni();
				found.setStrValore(null);
				found.setNumValore(null);
				found.setDtValore(dateOpz);
				super.persist(found);
			}
		} catch (Exception e) {
			log.error("Error merging/persit option String: " + e.getMessage());
		}
	}

	protected void saveBooleanOption(TsOpzioneEnum opt, Boolean dateOpz) {
		try {
			TsOpzioni found = findByCodiceOpzione(opt);
			if (found != null) {
				found.setNumValore(dateOpz ? 1l : 0l);
				super.merge(found);
			} else {
				found = new TsOpzioni();
				found.setStrValore(null);
				found.setNumValore(dateOpz ? 1l : 0l);
				found.setDtValore(null);
				super.persist(found);
			}
		} catch (Exception e) {
			log.error("Error merging/persit option Boolean : " + e.getMessage());
		}
	}

	public Date retrieveDateTimeOption(TsOpzioneEnum which) throws OpzioneNonPresenteException {
		try {
			TsOpzioni found = findByCodiceOpzione(which);
			return new Date(found.getNumValore());
		} catch (MyPortalNoResultFoundException | NullPointerException n) {
			log.warn("Opzione DATE non presente:" + which.name());
			throw new OpzioneNonPresenteException(which);
		}
	}

	public String retrieveStringOption(TsOpzioneEnum which) throws OpzioneNonPresenteException {
		try {
			TsOpzioni found = findByCodiceOpzione(which);
			return found.getStrValore();
		} catch (MyPortalNoResultFoundException | NullPointerException e) {
			log.warn("Opzione STRING non presente:" + which.name());
			throw new OpzioneNonPresenteException(which);
		}
	}
	
	public Boolean retrieveBooleanOption(TsOpzioneEnum which) throws OpzioneNonPresenteException {
		try {
			TsOpzioni found = findByCodiceOpzione(which);
			return found.getNumValore().equals(1);
		} catch (MyPortalNoResultFoundException | NullPointerException e) {
			log.warn("Opzione BOOL non presente:" + which.name());
			throw new OpzioneNonPresenteException(which);
		}
	}

	public Long retrieveLongOption(TsOpzioneEnum which) throws OpzioneNonPresenteException {
		try {
			TsOpzioni found = findByCodiceOpzione(which);
			return found.getNumValore();
		} catch (MyPortalNoResultFoundException | NullPointerException e) {
			log.warn("Opzione LONG non presente:" + which.name());
			throw new OpzioneNonPresenteException(which);
		}
	}

	public List<TsOpzioni> retrieveAllOptions() {
		try {
			return findAll();
		} catch (Exception e) {
			log.error("GRAVE: opzione non prevista?!? " + e.getMessage());
			return new ArrayList<>();
		}
	}

}
