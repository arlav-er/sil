package it.eng.sil.module.patto;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.InfoRegioneSingleton;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

public class PartecipanteGGDatiObb extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5276396864368479369L;
	
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PartecipanteGGDatiObb.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String cdnLavoratore = null;
		boolean datiObbGG = false;
		boolean azA01OR = false;
		boolean azA02OR = false;
		
		if (serviceRequest != null) {
			cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		} else {
			_logger.error("PartecipanteGG: impossibile recuperare cdnlavoratore");
			return;
		}
		if (cdnLavoratore == null) {
			_logger.error("PartecipanteGG: impossibile recuperare cdnlavoratore");
			return;
		}
		
		String codRegioneLocale = InfoRegioneSingleton.getInstance().getCodice();
		
		if (!codRegioneLocale.equals(Properties.TN)) {
			setSectionQuerySelect("GET_GG_COND_OBB");
		}
		else {
			setSectionQuerySelect("GET_COND_OBB_TN");
		}
		
		SourceBean condObbRows = doSelect(serviceRequest, serviceResponse, false);
		
		/* riporto il numero di azioni */
		if (condObbRows != null) {
			BigDecimal azioni = (BigDecimal) condObbRows.getAttribute("ROW.AZIONI");
			if (azioni != null && azioni.intValue() > 0) {
				serviceResponse.setAttribute("AZIONI", azioni);
				datiObbGG = true;
			}
		}
		
		if (!datiObbGG && codRegioneLocale.equals(Properties.UMB)) {
			setSectionQuerySelect("GET_GG_COND_OBB_UMBRIA");
			SourceBean condObbRowsUmbria = doSelect(serviceRequest, serviceResponse, false);
			if (condObbRowsUmbria != null) {
				Vector<SourceBean>azioniUmbria = condObbRowsUmbria.getAttributeAsVector("ROW");
				if (azioniUmbria != null) {
					for (int i = 0; i < azioniUmbria.size(); i++) {
						SourceBean azione = (SourceBean) azioniUmbria.elementAt(i);
						String codAzioneSifer = (String) azione.getAttribute("codazionesifer");
						if (codAzioneSifer != null) {
							if (codAzioneSifer.equalsIgnoreCase(PattoBean.AZ_SIFER_A01)) {
								azA01OR = true;
							}
							else {
								if (codAzioneSifer.equalsIgnoreCase(PattoBean.AZ_SIFER_A02)) {
									azA02OR = true;
								}
							}
						}
						
					}
					if (azA01OR && azA02OR) {
						serviceResponse.setAttribute("AZIONI", new BigDecimal(azioniUmbria.size()));
						datiObbGG = true;
					}
				}
			}
		}
		
		if (!datiObbGG && codRegioneLocale.equals(Properties.REGIONE_CALABRIA)) {
			setSectionQuerySelect("GET_DOTE_COND_OBB");
			SourceBean condObbDoteRows = doSelect(serviceRequest, serviceResponse, false);
			
			/* riporto il numero di azioni */
			if (condObbDoteRows != null) {
				BigDecimal azioni = (BigDecimal) condObbDoteRows.getAttribute("ROW.AZIONI");
				if (azioni != null && azioni.intValue() > 0) {
					serviceResponse.setAttribute("AZIONI", azioni);
					datiObbGG = true;
				}
			}		
		}
		
		if (!datiObbGG) {
			serviceResponse.setAttribute("AZIONI", new BigDecimal(0));
		}
		
	}
}
