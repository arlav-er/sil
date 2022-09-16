package it.eng.myportal.entity.home;

import it.eng.myportal.entity.MsgSoggettoCl;
import it.eng.myportal.entity.PfPrincipal;

import java.util.Date;

import javax.ejb.Stateless;

/**
 * @author Rodi A.
 */
@Stateless
public class MsgSoggettoClHome extends AbstractHibernateHome<MsgSoggettoCl, Integer> {

	@Override
	public MsgSoggettoCl findById(Integer id) {		
		return findById(MsgSoggettoCl.class, id);
	}
	
	public MsgSoggettoCl copy(MsgSoggettoCl msgSoggettoCl, PfPrincipal pfPrincipal) {
		Date now = new Date();
		MsgSoggettoCl msgSoggettoClRet = new MsgSoggettoCl();
		msgSoggettoClRet.setCodiceFiscale(msgSoggettoCl.getCodiceFiscale());
		msgSoggettoClRet.setDenominazione(msgSoggettoCl.getDenominazione());
		msgSoggettoClRet.setIndirizzo(msgSoggettoCl.getIndirizzo());
		msgSoggettoClRet.setDeComune(msgSoggettoCl.getDeComune());
		msgSoggettoClRet.setCodComunicazione(msgSoggettoCl.getCodComunicazione());				
		msgSoggettoClRet.setTelefono(msgSoggettoCl.getTelefono());
		msgSoggettoClRet.setEmail(msgSoggettoCl.getEmail());				
		msgSoggettoClRet.setDtmIns(now);
		msgSoggettoClRet.setDtmMod(now);
		msgSoggettoClRet.setPfPrincipalIns(pfPrincipal);
		msgSoggettoClRet.setPfPrincipalMod(pfPrincipal);
		entityManager.persist(msgSoggettoClRet);
		return msgSoggettoClRet;
	}

}
