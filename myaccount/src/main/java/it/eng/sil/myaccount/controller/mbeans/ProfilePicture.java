package it.eng.sil.myaccount.controller.mbeans;

import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omnifaces.util.Faces;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@RequestScoped
@URLMapping(id = "profilePicture", viewId = "/empty.xhtml", pattern = "/profilePicture/#{profilePicture.type}/#{profilePicture.id}/#{profilePicture.username}.jpg")
public class ProfilePicture {
	// parameters to get the picture
	private String type;
	private String username;
	private String id;

	private static final String UTENTE_PHOTO = "utente";
	private static final String AZIENDA_PHOTO = "azienda";

	protected Log log = LogFactory.getLog(this.getClass());

	@EJB
	UtenteInfoEJB utenteInfoEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@URLAction
	public void render() throws IOException {

		if (this.id == null && type == null && username == null)
			return;

		byte[] foto = null;
		int pfId = Integer.parseInt(this.id);

		if (UTENTE_PHOTO.equals(type)) {
			UtenteInfo utenteInfo = null;
			try {
				utenteInfo = utenteInfoEJB.findById(pfId);
				foto = utenteInfo.getFoto();
			} catch (MyCasNoResultException e) {
				log.error(e);
			}

		} else if (AZIENDA_PHOTO.equals(type)) {
			AziendaInfo aziendaInfo = null;
			try {
				aziendaInfo = aziendaInfoEJB.findById(pfId);
				foto = aziendaInfo.getLogo();
			} catch (MyCasNoResultException e) {
				log.error(e);
			}

		}

		Faces.sendFile(new ByteArrayInputStream(foto), username + ".jpg", true);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}