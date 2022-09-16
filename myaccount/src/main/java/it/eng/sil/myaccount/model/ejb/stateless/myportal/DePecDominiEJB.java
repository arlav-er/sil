package it.eng.sil.myaccount.model.ejb.stateless.myportal;

import it.eng.sil.myaccount.model.entity.myportal.DePecDomini;
import it.eng.sil.mycas.model.manager.AbstractTabellaDecodificaEJB;

import java.util.List;

import javax.ejb.Stateless;
import javax.transaction.NotSupportedException;

@Stateless
public class DePecDominiEJB extends AbstractTabellaDecodificaEJB<DePecDomini, String> {

	@Override
	public String getFriendlyName() {
		return "Pec Domini";
	}

	protected List<DePecDomini> findByCodPadre(String codicePadre) throws Exception {
		throw new NotSupportedException();
	}

	@Override
	public Class<DePecDomini> getEntityClass() {
		return DePecDomini.class;
	}

}
