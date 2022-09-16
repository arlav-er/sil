package it.eng.sil.module.movimenti;

import java.util.ArrayList;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class M_MovControllaProrogheSomm extends AbstractSimpleModule {

	private String name;
	private String classname = this.getClass().getName();

	public M_MovControllaProrogheSomm(String name) {
		this.name = name;
	}

	ArrayList warnings = new ArrayList();
	ArrayList nested = new ArrayList();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(M_MovControllaProrogheSomm.class.getName());
	private String className = this.getClass().getName();

	public M_MovControllaProrogheSomm() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		ResponseContainer responseContainer = getResponseContainer();
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean esito = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
		// In caso di rettifica, le CO vengono inoltrate a tutti i poli provinciali. In caso di mancanza
		// di competenza amministrativa, la CO di rettifica viene cancellata dalla tabella am_movimento_appoggio
		if (esito != null) {
			String codTipoMis = StringUtils.getAttributeStrNotNull(esito, "CODTIPOMIS");
			String codTipoAzienda = StringUtils.getAttributeStrNotNull(esito, "CODTIPOAZIENDA");
			String flgAssPropria = StringUtils.getAttributeStrNotNull(esito, "FLGINTERASSPROPRIA");
			boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
			String datfineMov = StringUtils.getAttributeStrNotNull(esito, "DATFINEMOV");
			String datInizioMis = StringUtils.getAttributeStrNotNull(esito, "DATINIZIORAPLAV");
			String datFineMis = StringUtils.getAttributeStrNotNull(esito, "DATFINERAPLAV");
			String codTipoMov = StringUtils.getAttributeStrNotNull(esito, "CODTIPOMOV");

			try {
				if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria && !datInizioMis.equals("")) {
					if (codTipoMov.equals("AVV") && (codTipoMis.equals("") || codTipoMis.equals("PRO"))
							&& !datfineMov.equals("") && !datFineMis.equals("")) {
						if (DateUtils.compare(datFineMis, datfineMov) > 0) {
							SourceBean row = new SourceBean("ROW");
							row.setAttribute("CODTIPOMOV", "PRO");
							row.setAttribute("DATFINEMOV", datFineMis);
							row.setAttribute("DATFINEMOVPREC", datFineMis);
							row.setAttribute("controllaProroghe", "S");
							response.setAttribute((SourceBean) row);
						}
					}
				}
			} catch (Exception e) {
				ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
						warnings, nested);
			}
		}
	}
}
