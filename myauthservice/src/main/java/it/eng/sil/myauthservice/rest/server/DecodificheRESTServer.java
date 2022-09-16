package it.eng.sil.myauthservice.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import it.eng.sil.base.pojo.decodifiche.DeAlboPOJO;
import it.eng.sil.base.pojo.decodifiche.DeAttivitaPOJO;
import it.eng.sil.base.pojo.decodifiche.DeBpCdvPOJO;
import it.eng.sil.base.pojo.decodifiche.DeCittadinanzaPOJO;
import it.eng.sil.base.pojo.decodifiche.DeComunePOJO;
import it.eng.sil.base.pojo.decodifiche.DeContrattoPOJO;
import it.eng.sil.base.pojo.decodifiche.DeCpiPOJO;
import it.eng.sil.base.pojo.decodifiche.DeLinguaPOJO;
import it.eng.sil.base.pojo.decodifiche.DeMansionePOJO;
import it.eng.sil.base.pojo.decodifiche.DeOrarioPOJO;
import it.eng.sil.base.pojo.decodifiche.DePatentePOJO;
import it.eng.sil.base.pojo.decodifiche.DePatentinoPOJO;
import it.eng.sil.base.pojo.decodifiche.DeProvinciaPOJO;
import it.eng.sil.base.pojo.decodifiche.DeRegionePOJO;
import it.eng.sil.base.pojo.decodifiche.DeServizioDigitalePOJO;
import it.eng.sil.base.pojo.decodifiche.DeSistemaPOJO;
import it.eng.sil.base.pojo.decodifiche.DeTitoloPOJO;
import it.eng.sil.base.pojo.decodifiche.DeTrasfertaPOJO;
import it.eng.sil.mycas.model.entity.decodifiche.DeAlbo;
import it.eng.sil.mycas.model.entity.decodifiche.DeAttivita;
import it.eng.sil.mycas.model.entity.decodifiche.DeBpCdv;
import it.eng.sil.mycas.model.entity.decodifiche.DeCittadinanza;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.entity.decodifiche.DeContratto;
import it.eng.sil.mycas.model.entity.decodifiche.DeCpi;
import it.eng.sil.mycas.model.entity.decodifiche.DeLingua;
import it.eng.sil.mycas.model.entity.decodifiche.DeMansione;
import it.eng.sil.mycas.model.entity.decodifiche.DeOrario;
import it.eng.sil.mycas.model.entity.decodifiche.DePatente;
import it.eng.sil.mycas.model.entity.decodifiche.DePatentino;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.decodifiche.DeRegione;
import it.eng.sil.mycas.model.entity.decodifiche.DeServizioDigitale;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.decodifiche.DeTitolo;
import it.eng.sil.mycas.model.entity.decodifiche.DeTrasferta;
import it.eng.sil.mycas.model.manager.decodifiche.DeAlboEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeAttivitaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeBpCdvEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeContrattoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCpiEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeLinguaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeMansioneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeOrarioEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DePatenteEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DePatentinoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeServizioDigitaleEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTrasfertaEJB;
import it.eng.sil.mycas.utils.pojofactory.DecodifichePojoFactory;

@Path("/decodifiche")
public class DecodificheRESTServer {
	protected static Logger log = Logger.getLogger(DecodificheRESTServer.class.getName());

	@Inject
	DeSistemaEJB deSistemaEJB;

	@Inject
	DeComuneEJB deComuneEJB;

	@Inject
	DeServizioDigitaleEJB deServizioDigitaleEJB;

	@Inject
	DeProvinciaEJB deProvinciaEJB;

	@Inject
	DeRegioneEJB deRegioneEJB;

	@Inject
	DeCittadinanzaEJB deCittadinanzaEJB;

	@Inject
	DeCpiEJB deCpiEJB;

	@Inject
	DeTitoloEJB deTitoloEJB;

	@Inject
	DeMansioneEJB deMansioneEJB;

	@Inject
	DeAttivitaEJB deAttivitaEJB;

	@Inject
	DeLinguaEJB deLinguaEJB;

	@Inject
	DeBpCdvEJB deBpCdvEJB;

	@Inject
	DePatenteEJB dePatenteEJB;

	@Inject
	DePatentinoEJB dePatentinoEJB;
	
	@Inject
	DeOrarioEJB deOrarioEJB;

	@Inject
	DeAlboEJB deAlboEJB;
	
	@Inject
	DeContrattoEJB deContrattoEJB;

	@Inject
	DeTrasfertaEJB deTrasfertaEJB;

	/**
	 * Metodo di default, espone la lista di tutte le decodifiche disponibili
	 * 
	 * @return
	 */
	@GET
	@Produces("application/json; charset=UTF-8")
	public List<String> getList() {
		ArrayList<String> ret = new ArrayList<>();
		ret.add(DeCittadinanzaPOJO.class.getSimpleName());
		ret.add(DeSistemaPOJO.class.getSimpleName());
		ret.add(DeCpiPOJO.class.getSimpleName());
		ret.add(DeComunePOJO.class.getSimpleName());
		ret.add(DeProvinciaPOJO.class.getSimpleName());
		ret.add(DeRegionePOJO.class.getSimpleName());
		ret.add(DeServizioDigitalePOJO.class.getSimpleName());
		ret.add(DeMansionePOJO.class.getSimpleName());
		ret.add(DeTitoloPOJO.class.getSimpleName());
		ret.add(DeAttivitaPOJO.class.getSimpleName());
		ret.add(DeLinguaPOJO.class.getSimpleName());
		ret.add(DeBpCdvPOJO.class.getSimpleName());
		ret.add(DePatentePOJO.class.getSimpleName());
		ret.add(DePatentinoPOJO.class.getSimpleName());
		ret.add(DeAlboPOJO.class.getSimpleName());
		ret.add(DeTrasfertaPOJO.class.getSimpleName());
		ret.add(DeOrarioPOJO.class.getSimpleName());
		ret.add(DeContrattoPOJO.class.getSimpleName());
		return ret;
	}
	
	@GET
	@Path("DeOrarioPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeOrarioPOJO> getDecodificheDeOrarioPOJO() {
		List<DeOrario> deCittList = deOrarioEJB.findAll();
		List<DeOrarioPOJO> pojoList = new ArrayList<DeOrarioPOJO>();
		for (DeOrario cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeOrarioPOJO(cittadinanza));
		}
		return pojoList;
	}
	
	@GET
	@Path("DeContrattoPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeContrattoPOJO> getDecodificheDeContrattoPOJO() {
		List<DeContratto> deCittList = deContrattoEJB.findAll();
		List<DeContrattoPOJO> pojoList = new ArrayList<DeContrattoPOJO>();
		for (DeContratto cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeContrattoPOJO(cittadinanza));
		}
		return pojoList;
	}
	
	@GET
	@Path("DePatentePOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DePatentePOJO> getDecodificheDePatentePOJO() {
		List<DePatente> dePatenteList = dePatenteEJB.findAll();
		List<DePatentePOJO> pojoList = new ArrayList<DePatentePOJO>();
		for (DePatente patente : dePatenteList) {
			pojoList.add(DecodifichePojoFactory.makeDePatentePOJO(patente));
		}
		return pojoList;
	}

	@GET
	@Path("DeCittadinanzaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeCittadinanzaPOJO> getDecodificheDeCittadinanzaPOJO() {
		List<DeCittadinanza> deCittList = deCittadinanzaEJB.findAll();
		List<DeCittadinanzaPOJO> pojoList = new ArrayList<DeCittadinanzaPOJO>();
		for (DeCittadinanza cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeCittadinanzaPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeTitoloPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeTitoloPOJO> getDecodificheDeTitoloPOJO() {
		List<DeTitolo> deCittList = deTitoloEJB.findAll();
		List<DeTitoloPOJO> pojoList = new ArrayList<DeTitoloPOJO>();
		for (DeTitolo cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeTitoloPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeAttivitaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeAttivitaPOJO> getDecodificheDeAttivitaPOJO() {
		List<DeAttivita> deCittList = deAttivitaEJB.findAll();
		List<DeAttivitaPOJO> pojoList = new ArrayList<DeAttivitaPOJO>();
		for (DeAttivita cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeAttivitaPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeMansionePOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeMansionePOJO> getDecodificheDeMansionePOJO() {
		List<DeMansione> deCittList = deMansioneEJB.findAll();
		List<DeMansionePOJO> pojoList = new ArrayList<DeMansionePOJO>();
		for (DeMansione cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeMansionePOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeLinguaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeLinguaPOJO> getDecodificheDeLinguaPOJO() {
		List<DeLingua> deCittList = deLinguaEJB.findAll();
		List<DeLinguaPOJO> pojoList = new ArrayList<DeLinguaPOJO>();
		for (DeLingua cittadinanza : deCittList) {
			pojoList.add(DecodifichePojoFactory.makeDeLinguaPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeSistemaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeSistemaPOJO> getDecodificheDeSistemaPOJO() {
		List<DeSistema> deSistemaList = deSistemaEJB.findAll();
		List<DeSistemaPOJO> pojoList = new ArrayList<DeSistemaPOJO>();
		for (DeSistema sistema : deSistemaList) {
			pojoList.add(DecodifichePojoFactory.makeDeSistemaPOJO(sistema));
		}
		return pojoList;
	}

	@GET
	@Path("DeComunePOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeComunePOJO> getDecodificheDeComunePOJO() {
		List<DeComune> deComuneList = deComuneEJB.findAll();
		List<DeComunePOJO> pojoList = new ArrayList<DeComunePOJO>();
		for (DeComune comune : deComuneList) {
			pojoList.add(DecodifichePojoFactory.makeDeComunePOJO(comune));
		}
		return pojoList;
	}

	@GET
	@Path("DeBpCdvPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeBpCdvPOJO> getDecodificheDeBpCdvPOJO() {
		List<DeBpCdv> deComuneList = deBpCdvEJB.findAll();
		List<DeBpCdvPOJO> pojoList = new ArrayList<DeBpCdvPOJO>();
		for (DeBpCdv comune : deComuneList) {
			pojoList.add(DecodifichePojoFactory.makeDeBpCdvPOJO(comune));
		}
		return pojoList;
	}

	@GET
	@Path("DeServizioDigitalePOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeServizioDigitalePOJO> getDecodificheDeServizioDigitalePOJO() {
		List<DeServizioDigitale> deServizioDigitaleList = deServizioDigitaleEJB.findAll();
		List<DeServizioDigitalePOJO> pojoList = new ArrayList<DeServizioDigitalePOJO>();
		for (DeServizioDigitale servizio : deServizioDigitaleList) {
			pojoList.add(DecodifichePojoFactory.makeDeServizioDigitalePOJO(servizio));
		}
		return pojoList;
	}

	@GET
	@Path("DeProvinciaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeProvinciaPOJO> getDecodificheDeProvinciaPOJO() {
		List<DeProvincia> deProvinciaList = deProvinciaEJB.findAll();
		List<DeProvinciaPOJO> pojoList = new ArrayList<DeProvinciaPOJO>();
		for (DeProvincia provincia : deProvinciaList) {
			pojoList.add(DecodifichePojoFactory.makeDeProvinciaPOJO(provincia));
		}
		return pojoList;
	}

	@GET
	@Path("DeRegionePOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeRegionePOJO> getDecodificheDeRegionePOJO() {
		List<DeRegione> deRegioneList = deRegioneEJB.findAll();
		List<DeRegionePOJO> pojoList = new ArrayList<DeRegionePOJO>();
		for (DeRegione regione : deRegioneList) {
			pojoList.add(DecodifichePojoFactory.makeDeRegionePOJO(regione));
		}
		return pojoList;
	}

	@GET
	@Path("DeCpiPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeCpiPOJO> getDecodificheDeCpiPOJO() {
		List<DeCpi> deCpiList = deCpiEJB.findAll();
		List<DeCpiPOJO> pojoList = new ArrayList<DeCpiPOJO>();
		for (DeCpi cpi : deCpiList) {
			pojoList.add(DecodifichePojoFactory.makeDeCpiPOJO(cpi));
		}
		return pojoList;
	}

	@GET
	@Path("DePatentinoPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DePatentinoPOJO> getDecodificheDePatentinoPOJO() {
		List<DePatentino> dePatentinoList = dePatentinoEJB.findAll();
		List<DePatentinoPOJO> pojoList = new ArrayList<DePatentinoPOJO>();
		for (DePatentino patentino : dePatentinoList) {
			pojoList.add(DecodifichePojoFactory.makeDePatentinoPOJO(patentino));
		}
		return pojoList;
	}

	@GET
	@Path("DeAlboPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeAlboPOJO> getDecodificheDeAlboPOJO() {
		List<DeAlbo> deAlboList = deAlboEJB.findAll();
		List<DeAlboPOJO> pojoList = new ArrayList<DeAlboPOJO>();
		for (DeAlbo deAlbo : deAlboList) {
			pojoList.add(DecodifichePojoFactory.makeDeAlboPOJO(deAlbo));
		}
		return pojoList;
	}

	@GET
	@Path("DeTrasfertaPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeTrasfertaPOJO> getDeTrasfertaPOJO() {
		List<DeTrasferta> deTrasferta = deTrasfertaEJB.findAll();
		List<DeTrasfertaPOJO> deTrasfertaPojoList = new ArrayList<DeTrasfertaPOJO>();
		for (DeTrasferta trasferta : deTrasferta) {
			deTrasfertaPojoList.add(DecodifichePojoFactory.makeDeTrasfertaPOJO(trasferta));
		}
		return deTrasfertaPojoList;
	}

}
