package it.eng.sil.myauthservice.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import it.eng.sil.base.pojo.decodifiche.sil.DeContrattoSilPOJO;
import it.eng.sil.base.pojo.decodifiche.sil.DeOrarioSilPOJO;
import it.eng.sil.base.pojo.decodifiche.sil.DePatenteSilPOJO;
import it.eng.sil.base.pojo.decodifiche.sil.DeTurnoSilPOJO;
import it.eng.sil.mycas.model.entity.decodifiche.DeTurno;
import it.eng.sil.mycas.model.entity.decodifiche.sil.DeContrattoSil;
import it.eng.sil.mycas.model.entity.decodifiche.sil.DeOrarioSil;
import it.eng.sil.mycas.model.entity.decodifiche.sil.DePatenteSil;
import it.eng.sil.mycas.model.manager.decodifiche.DeTurnoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.sil.DeContrattoSilEJB;
import it.eng.sil.mycas.model.manager.decodifiche.sil.DeOrarioSilEJB;
import it.eng.sil.mycas.model.manager.decodifiche.sil.DePatenteSilEJB;
import it.eng.sil.mycas.utils.pojofactory.DecodifichePojoFactory;

@Path("/decodifiche/sil/")
public class DecodificheSilRESTServer {
	protected static Logger log = Logger.getLogger(DecodificheSilRESTServer.class.getName());

	@Inject
	DeContrattoSilEJB deContrattoSilEJB;

	@Inject
	DeOrarioSilEJB deOrarioSilEJB;

	@Inject
	DePatenteSilEJB dePatenteSilEJB;

	@Inject
	DeTurnoEJB deTurnoEJB;

	@GET
	@Produces("application/json; charset=UTF-8")
	public List<String> getList() {
		ArrayList<String> ret = new ArrayList<>();
		ret.add(DeContrattoSilPOJO.class.getSimpleName());
		ret.add(DeOrarioSilPOJO.class.getSimpleName());
		ret.add(DePatenteSilPOJO.class.getSimpleName());
		ret.add(DeTurnoSilPOJO.class.getSimpleName());
		return ret;
	}

	@GET
	@Path("DeContrattoSilPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeContrattoSilPOJO> getDecodificheDeContrattoSilPOJO() {
		List<DeContrattoSil> deContrList = deContrattoSilEJB.findAll();
		List<DeContrattoSilPOJO> pojoList = new ArrayList<DeContrattoSilPOJO>();
		for (DeContrattoSil cittadinanza : deContrList) {
			pojoList.add(DecodifichePojoFactory.makeDeContrattoSilPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DePatenteSilPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DePatenteSilPOJO> getDecodificheDePatenteSilPOJO() {
		List<DePatenteSil> deContrList = dePatenteSilEJB.findAll();
		List<DePatenteSilPOJO> pojoList = new ArrayList<DePatenteSilPOJO>();
		for (DePatenteSil cittadinanza : deContrList) {
			pojoList.add(DecodifichePojoFactory.makeDePatenteSilPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeOrarioSilPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeOrarioSilPOJO> getDecodificheDeOrarioSilPOJO() {
		List<DeOrarioSil> deContrList = deOrarioSilEJB.findAll();
		List<DeOrarioSilPOJO> pojoList = new ArrayList<DeOrarioSilPOJO>();
		for (DeOrarioSil cittadinanza : deContrList) {
			pojoList.add(DecodifichePojoFactory.makeDeOrarioSilPOJO(cittadinanza));
		}
		return pojoList;
	}

	@GET
	@Path("DeTurnoSilPOJO")
	@Produces("application/json; charset=UTF-8")
	public List<DeTurnoSilPOJO> getDecodificheDeTurnoSilPOJO() {
		List<DeTurno> deTurnoList = deTurnoEJB.findAll();
		List<DeTurnoSilPOJO> pojoList = new ArrayList<DeTurnoSilPOJO>();
		for (DeTurno turno : deTurnoList) {
			pojoList.add(DecodifichePojoFactory.makeDeTurnoSilPOJO(turno));
		}
		return pojoList;
	}

}
