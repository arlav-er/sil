package it.eng.myportal.entity.home.nodto;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import it.eng.myportal.entity.AcContatto;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.home.InoDTOejb;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

@Stateless
@LocalBean
public class AcContattoHome extends AbstractHibernateHome<AcContatto, Integer> implements InoDTOejb<AcContatto> {

	@EJB
	protected PfPrincipalHome pfPrincipalHome;

	@Override
	public AcContatto findById(Integer id) {
		return findById(AcContatto.class, id);
	}

	public AcContatto findAcContattoByCvId(Integer cvId) throws MyPortalNoResultFoundException {
		AcContatto acContatto = new AcContatto(); 
		try {

			List<AcContatto> acListContatto = entityManager.createNamedQuery("findAcContattoByCVId", AcContatto.class)
					.setParameter("par", cvId).getResultList();
			
			if(acListContatto != null && !acListContatto.isEmpty()) {
				acContatto = 	acListContatto.get(0);
			}
			
			return acContatto;
		} catch (Exception e) {
			throw new MyPortalNoResultFoundException();
		}
	}

	@Override
	public AcContatto merge(AcContatto entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public AcContatto persist(AcContatto entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

}
