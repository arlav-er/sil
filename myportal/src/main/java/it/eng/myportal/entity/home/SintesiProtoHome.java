package it.eng.myportal.entity.home;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.SintesiProto;

@Stateless
@LocalBean
public class SintesiProtoHome extends AbstractHibernateHome< SintesiProto, Integer>
		implements InoDTOejb< SintesiProto> {

    @EJB
    protected PfPrincipalHome pfPrincipalHome;
    @Override
    public SintesiProto findById(Integer id) {
        return findById(SintesiProto.class, id);
    }


    @Override
    public SintesiProto merge(SintesiProto entity, Integer actingUser) {
        PfPrincipal usr = pfPrincipalHome.findById(actingUser);
        entity.setDtmMod(new Date());
        entity.setPfPrincipalMod(usr);
        return entityManager.merge(entity);
    }

    @Override
    public SintesiProto persist(SintesiProto entity, Integer actingUser) {
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
