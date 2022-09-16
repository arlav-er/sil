package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.beans.amministrazione.pojo.UtentePojo;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.entity.PfIdentityDevice;
import it.eng.myportal.entity.PfIdentityDevice_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.rest.app.helper.AppUtils;

@Stateless
public class PfIdentityDeviceHome extends AbstractHibernateHome<PfPrincipal, Integer> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@Override
	public PfPrincipal findById(Integer idPfPrincipal) {
		return findById(PfPrincipal.class, idPfPrincipal);
	}

	public List<Integer> findAllIdPfPrincipal() {
		TypedQuery<Integer> query = entityManager.createNamedQuery("pfIdentityDevice.findAllIdPfPrincipal",
				Integer.class);

		List<Integer> ret = query.getResultList();

		return ret;
	}

	/**
	 * Metodo di creazione PfIdentityDevice
	 * 
	 * @param principalId
	 */
	public PfIdentityDevice create(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		PfIdentityDevice device = new PfIdentityDevice();
		device.setDtmIns(new Date());
		device.setDtmMod(new Date());
		device.setPfPrincipal(pfPrincipal);
		device.setPfPrincipalIns(pfPrincipal);
		device.setPfPrincipalMod(pfPrincipal);
		entityManager.persist(device);
		return device;
	}

	public List<PfIdentityDevice> findDevice(Integer idPfPrincipal) {
		TypedQuery<PfIdentityDevice> query = entityManager.createNamedQuery("FIND_BY_UTENTE", PfIdentityDevice.class);
		query.setParameter("id", pfPrincipalHome.findById(idPfPrincipal));
		List<PfIdentityDevice> devices = query.getResultList();
		return devices;
	}

	public List<PfIdentityDevice> findUsersApp(String email, String codProvincia) {
		TypedQuery<PfIdentityDevice> query = entityManager.createNamedQuery("pfIdentityDevice.findUsersApp",
				PfIdentityDevice.class);

		query.setParameter("email", email.toUpperCase());
		query.setParameter("codProvincia", codProvincia);

		List<PfIdentityDevice> ret = query.getResultList();

		return ret;
	}

	public Long getCountUsersApp(String email, String codProvincia) {
		TypedQuery<Long> query = entityManager.createNamedQuery("pfIdentityDevice.getCountUsersApp", Long.class);

		query.setParameter("email", email.toUpperCase());
		query.setParameter("codProvincia", codProvincia);

		Long ret = query.getSingleResult();

		return ret;
	}

	public List<Object[]> suggestEmailUsersApp(String email, String codProvincia) {
		Query query = entityManager.createNamedQuery("pfIdentityDevice.suggestEmailUsersApp");

		query.setParameter("email", StringUtils.trimToEmpty(email + "%").toUpperCase());
		query.setParameter("codProvincia", codProvincia);

		@SuppressWarnings("unchecked")
		List<Object[]> ret = query.getResultList();

		return ret;
	}

	public List<PfIdentityDevice> findUsersApp(Integer idPfPrincipal, String username, String nome, String cognome,
			String email, boolean soloAbilitato, Integer start, Integer rows) {
		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfIdentityDevice> query = cb.createQuery(PfIdentityDevice.class);

		// Creo la clausola FROM della query.
		Root<PfIdentityDevice> pfIdentityDevice = query.from(PfIdentityDevice.class);
		Join<PfIdentityDevice, PfPrincipal> pfPrincipal = pfIdentityDevice.join(PfIdentityDevice_.pfPrincipal,
				JoinType.INNER);

		// Creo la clausola SELECT della query
		query.select(pfIdentityDevice);

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(idPfPrincipal, username, nome, cognome, email,
				soloAbilitato, pfPrincipal);

		// Order by idPfIdentityDevice
		query.orderBy(cb.asc(pfIdentityDevice.get(PfIdentityDevice_.idPfIdentityDevice)));

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<PfIdentityDevice> tquery = entityManager.createQuery(query);

		if (start != null && rows != null) {
			tquery.setFirstResult(start);
			tquery.setMaxResults(rows);
		}

		// Eseguo la query e restituisco il risultato
		return tquery.getResultList();
	}

	public List<UtentePojo> findUsersAppPojo(Integer idPfPrincipal, String username, String nome, String cognome,
			String email, boolean soloAbilitato, Integer start, Integer rows) {

		List<UtentePojo> ret = new ArrayList<UtentePojo>();

		List<PfIdentityDevice> list = this.findUsersApp(idPfPrincipal, username, nome, cognome, email, soloAbilitato,
				start, rows);

		if (list != null) {
			for (PfIdentityDevice item : list) {
				UtentePojo pojo = new UtentePojo();
				pojo.setEmail(item.getPfPrincipal().getEmail());
				pojo.setDtScadenza(item.getPfPrincipal().getDtScadenza());
				pojo.setFlagAbilitato(item.getPfPrincipal().getFlagAbilitato());
				pojo.setIdPfPrincipal(item.getPfPrincipal().getIdPfPrincipal());
				DeProvincia deProvincia = AppUtils.getDeProvinciaRiferimento(item.getPfPrincipal());
				if (deProvincia != null)
					pojo.setProvinciaRiferimento(deProvincia.getDenominazione());
				pojo.setUsername(item.getPfPrincipal().getUsername());

				ret.add(pojo);
			}
		}

		return ret;
	}

	public Long getCountUsersApp(Integer idPfPrincipal, String username, String nome, String cognome, String email,
			boolean soloAbilitato) {
		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Creo la clausola FROM della query.
		Root<PfIdentityDevice> pfIdentityDevice = query.from(PfIdentityDevice.class);
		Join<PfIdentityDevice, PfPrincipal> pfPrincipal = pfIdentityDevice.join(PfIdentityDevice_.pfPrincipal,
				JoinType.INNER);

		// Creo la clausola SELECT della query
		query.select(cb.count(pfIdentityDevice));

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(idPfPrincipal, username, nome, cognome, email,
				soloAbilitato, pfPrincipal);

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);

		// Eseguo la query e restituisco il risultato
		return tquery.getSingleResult();
	}

	private List<Predicate> createWhereConditions(Integer idPfPrincipal, String username, String nome, String cognome,
			String email, boolean soloAbilitato, Join<PfIdentityDevice, PfPrincipal> pfPrincipal) {

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		if (idPfPrincipal != null)
			whereConditions.add(cb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));
		else {
			if (StringUtils.isNotBlank(username))
				whereConditions.add(cb.equal(cb.upper(pfPrincipal.get(PfPrincipal_.username)), username.toUpperCase()));
			if (StringUtils.isNotBlank(nome))
				whereConditions.add(cb.equal(cb.lower(pfPrincipal.get(PfPrincipal_.nome)), nome.toLowerCase()));
			if (StringUtils.isNotBlank(cognome))
				whereConditions.add(cb.equal(cb.lower(pfPrincipal.get(PfPrincipal_.cognome)), cognome.toLowerCase()));
			if (StringUtils.isNotBlank(email))
				whereConditions.add(cb.equal(cb.upper(pfPrincipal.get(PfPrincipal_.email)), email.toUpperCase()));
		}

		if (soloAbilitato) {
			whereConditions.add(cb.equal(pfPrincipal.get(PfPrincipal_.flagAbilitato), soloAbilitato));
		}

		return whereConditions;
	}

}
