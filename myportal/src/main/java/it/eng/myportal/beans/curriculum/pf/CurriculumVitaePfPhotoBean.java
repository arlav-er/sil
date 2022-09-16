package it.eng.myportal.beans.curriculum.pf;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;

@ManagedBean(name = "curriculumVitaePfPhotoBean")
@ApplicationScoped
public class CurriculumVitaePfPhotoBean {
	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	public StreamedContent getImage() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {

			String paramId = context.getExternalContext().getRequestParameterMap().get("paramId");
			CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(new Integer(paramId));
			if (cvDatiPersonali.getFoto() != null)
				return new DefaultStreamedContent(new ByteArrayInputStream(cvDatiPersonali.getFoto()));
			else
				return new DefaultStreamedContent(new ByteArrayInputStream(new byte[0]));
		}
	}
}
