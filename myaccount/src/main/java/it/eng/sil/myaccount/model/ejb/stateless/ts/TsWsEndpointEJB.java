//package it.eng.sil.myaccount.model.ejb.stateless.ts;
//
//import it.eng.sil.myaccount.model.utils.TsWsEndpoint;
//import it.eng.sil.mycas.model.manager.AbstractTabellaSistemaEJB;
//
//import javax.ejb.Stateless;
//import javax.persistence.TypedQuery;
//
//@Stateless
//public class TsWsEndpointEJB extends AbstractTabellaSistemaEJB<TsWsEndpoint, Integer> {
//
//	@Override
//	public String getFriendlyName() {
//		return "Ts ws endpoint";
//	}
//
//	@Override
//	public Class<TsWsEndpoint> getEntityClass() {
//		return TsWsEndpoint.class;
//	}
//
//	public TsWsEndpoint findById(Integer id) {
//		if (log.isDebugEnabled())
//			log.debug("getting " + TsWsEndpoint.class.getSimpleName() + " instance with id: " + id);
//
//		TsWsEndpoint instance = entityMan.find(TsWsEndpoint.class, id);
//
//		if (log.isDebugEnabled())
//			log.debug(TsWsEndpoint.class.getName() + ": get successful for id: " + id);
//		return instance;
//	}
//
//	public TsWsEndpoint findByTipoServizio(String tipoServizio) {
//		TypedQuery<TsWsEndpoint> query = entityMan.createNamedQuery("findEndpointByTipoServizio", TsWsEndpoint.class);
//		query.setParameter("tipoServizio", tipoServizio);
//		return query.getSingleResult();
//	}
//
//	public TsWsEndpoint findByTipoServizioProvincia(String tipoServizio, String codProvincia) {
//		TypedQuery<TsWsEndpoint> query = entityMan.createNamedQuery("findEndpointByTipoServizioProvincia",
//				TsWsEndpoint.class);
//		query.setParameter("tipoServizio", tipoServizio);
//		query.setParameter("codProvincia", codProvincia);
//		return query.getSingleResult();
//	}
//}