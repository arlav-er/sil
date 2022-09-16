package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.exception.MyPortalException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class ReinvioAdesioniYGBean {

	protected static Log log = LogFactory.getLog(ReinvioAdesioniYGBean.class);

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	private Integer idYgAdesione;
	private YgAdesioneDTO ygAdesioneDTO;
	private String errMsg;
	private Date invioMassivoDa;
	private Date invioMassivoA;
	private String invioMassivoDaH;
	private String invioMassivoDaM;
	private String invioMassivoAH;
	private String invioMassivoAM;
	private List<YgAdesioneDTO> ygAdesioneErroreList;
	private boolean invioEseguito;
	private boolean invioOK;
	private boolean invioMassivoEseguito;
	private boolean invioMassivoOK;

	public Integer getIdYgAdesione() {
		return idYgAdesione;
	}

	public void setIdYgAdesione(Integer idYgAdesione) {
		this.idYgAdesione = idYgAdesione;
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Date getInvioMassivoDa() {
		return invioMassivoDa;
	}

	public void setInvioMassivoDa(Date invioMassivoDa) {
		this.invioMassivoDa = invioMassivoDa;
	}

	public Date getInvioMassivoA() {
		return invioMassivoA;
	}

	public void setInvioMassivoA(Date invioMassivoA) {
		this.invioMassivoA = invioMassivoA;
	}

	public String getInvioMassivoDaH() {
		return invioMassivoDaH;
	}

	public void setInvioMassivoDaH(String invioMassivoDaH) {
		this.invioMassivoDaH = invioMassivoDaH;
	}

	public String getInvioMassivoDaM() {
		return invioMassivoDaM;
	}

	public void setInvioMassivoDaM(String invioMassivoDaM) {
		this.invioMassivoDaM = invioMassivoDaM;
	}

	public String getInvioMassivoAH() {
		return invioMassivoAH;
	}

	public void setInvioMassivoAH(String invioMassivoAH) {
		this.invioMassivoAH = invioMassivoAH;
	}

	public String getInvioMassivoAM() {
		return invioMassivoAM;
	}

	public void setInvioMassivoAM(String invioMassivoAM) {
		this.invioMassivoAM = invioMassivoAM;
	}

	public List<YgAdesioneDTO> getYgAdesioneErroreList() {
		return ygAdesioneErroreList;
	}

	public void setYgAdesioneErroreList(List<YgAdesioneDTO> ygAdesioneErroreList) {
		this.ygAdesioneErroreList = ygAdesioneErroreList;
	}

	public boolean isInvioOK() {
		return invioOK;
	}

	public void setInvioOK(boolean invioOK) {
		this.invioOK = invioOK;
	}

	public boolean isInvioMassivoOK() {
		return invioMassivoOK;
	}

	public void setInvioMassivoOK(boolean invioMassivoOK) {
		this.invioMassivoOK = invioMassivoOK;
	}

	public boolean isInvioEseguito() {
		return invioEseguito;
	}

	public void setInvioEseguito(boolean invioEseguito) {
		this.invioEseguito = invioEseguito;
	}

	public boolean isInvioMassivoEseguito() {
		return invioMassivoEseguito;
	}

	public void setInvioMassivoEseguito(boolean invioMassivoEseguito) {
		this.invioMassivoEseguito = invioMassivoEseguito;
	}

	private void resetValuesInvio() {
		idYgAdesione = null;
		ygAdesioneDTO = null;
		errMsg = null;
		ygAdesioneErroreList = new ArrayList<YgAdesioneDTO>();
		invioOK = false;
		invioEseguito = false;
	}

	private void resetValuesInvioMassivo() {
		invioMassivoDa = null;
		invioMassivoA = null;
		invioMassivoDaH = null;
		invioMassivoDaM = null;
		invioMassivoAH = null;
		invioMassivoAM = null;
		ygAdesioneErroreList = new ArrayList<YgAdesioneDTO>();
		invioMassivoOK = false;
		invioMassivoEseguito = false;
	}

	@PostConstruct
	protected void postConstruct() {
		resetValuesInvio();
		resetValuesInvioMassivo();
	}

	public void cercaAdesione() {
		Integer localIdYgAdesione = idYgAdesione;
		resetValuesInvioMassivo();
		resetValuesInvio();
		idYgAdesione = localIdYgAdesione;
		if (idYgAdesione != null) {
			List<YgAdesioneDTO> listaReinvii = ygAdesioneHome.findDTOByIdDateRange(idYgAdesione, null, null);
			if (listaReinvii.size() == 1) {
				ygAdesioneDTO = listaReinvii.get(0);
			}
		}
	}

	private boolean invioAdesione(YgAdesioneDTO ygAdesioneDTO) {
		UtenteCompletoDTO utenteCompletoDTO = utenteInfoHome.findDTOCompletoByUsername(ygAdesioneDTO.getPfPrincipal()
				.getUsername());
		//Date currentDate = new Date();
		try {
			log.info("Reinvio adesione idYgAdesione = " + ygAdesioneDTO.getId());
			return ygAdesioneHome.invioAdesioneYgSAP( ygAdesioneDTO.getDtAdesione(), utenteCompletoDTO,
					ygAdesioneDTO.getDeCpiAdesione());
		} catch (MyPortalException e) {
			return false;
		}
	}

	public void invio() {
		resetValuesInvioMassivo();
		invioEseguito = true;
		invioOK = invioAdesione(ygAdesioneDTO);

		/* popolo la lista di adesioni andate in errore */
		if (!invioOK) {
			ygAdesioneErroreList.add(ygAdesioneHome.findDTOById(idYgAdesione));
		}
	}

	public void invioMassivo() {
		resetValuesInvio();
		invioMassivoEseguito = true;
		invioMassivoOK = true;

		String da = new SimpleDateFormat("dd/MM/yyyy").format(invioMassivoDa) + " " + invioMassivoDaH + ":"
				+ invioMassivoDaM;
		String a = new SimpleDateFormat("dd/MM/yyyy").format(invioMassivoA) + " " + invioMassivoAH + ":"
				+ invioMassivoAM;
		List<YgAdesioneDTO> listaReinvii = ygAdesioneHome.findDTOByIdDateRange(null, da, a);
		boolean ok = false;
		for (YgAdesioneDTO ygAdesioneDTO : listaReinvii) {
			ok = invioAdesione(ygAdesioneDTO);
			invioMassivoOK &= ok;
			/* popolo la lista di adesioni andate in errore */
			if (!ok) {
				ygAdesioneErroreList.add(ygAdesioneHome.findDTOById(ygAdesioneDTO.getId()));
			}

		}
	}
}
