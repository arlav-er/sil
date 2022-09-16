package it.eng.sil.action.report.ido;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
/**
 *
 * @author donisi
 * Inserita la configurazione per il tipo graduatoria VDA.
 *
 *
 */

public class StampaDefinitiva extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaDefinitiva.class.getName());

	public void service(SourceBean request, SourceBean response) {
		try {

			super.service(request, response);

			AccessoSemplificato _db = new AccessoSemplificato(this);
			// per recuperare le informazioni utilizzate per la generazione del
			// report non e' necessario
			// l'utilizzo della transazione. Se necessaria nei passi successivi
			// verra' abilitata.
			_db.enableSimpleQuery();

			String reportpath = "";
			String reportpathVDA = "";

			String tipo = (String) request.getAttribute("tipo");
			if(tipo != null && !tipo.equals("") && tipo.equals("list")){
				reportpath = "psi";
				reportpathVDA = "qualifica";
			}


			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("st_get_config_tipograd_cm",null,"SELECT","SIL_DATI");
			String ConfigGraduatoria_cm = (String) rowsSourceBean.getAttribute("ROW.codmonotipogradcm");

			//ts_generale
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String codRegioneSil = sbGenerale.getAttribute("CODREGIONESIL").toString();

			String prgTipoIncrocio = (String) request.getAttribute("prgTipoIncrocio");
			//visualizzaCodiceAdesione false (attributo Nominativa presente per la regione RER e gestito nella pagina jsp/ido/CMMatchDettGraduatoria.jsp) 
			//allora la stampa viene fatta con nominativo lavoratore e senza codice adesione
			
			//Nominativa = 1 (solo per RER)allora stampa con nominativo lavoratore e codice adesione
			//Nominativa = 0 (solo per RER)allora stampa senza nominativo e codice adesione
			boolean visualizzaCodiceAdesione = request.containsAttribute("Nominativa");
			String nominativa = request.containsAttribute("Nominativa")?(String)request.getAttribute("Nominativa"):"";
			
			boolean isIncrocioCM = prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12");

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("StampaGraduatoria." + tipoFile);
				} else {
					setStrNomeDoc("StampaGraduatoria.pdf");
				}
				setStrDescrizione("Stampa Graduatoria");

			/*	if("3".equals(ConfigGraduatoria_cm)){

					if (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12"))
						setReportPath("pubb/StampaGraduatoria"+reportpath+"Def_CC.rpt");
					else
						setReportPath("pubb/StampaGraduatoria"+reportpath+"_CC.rpt");

				} else {
					if (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12"))
						setReportPath("pubb/O_StampaGraduatoriaDef_CC.rpt");
					else
						setReportPath("pubb/O_StampaGraduatoria_CC.rpt");


				}*/

				if("3".equals(ConfigGraduatoria_cm)){

					if (isIncrocioCM) {
						setReportPath("pubb/StampaGraduatoria"+reportpath+"Def_CC.rpt");
					} else {
						setReportPath("pubb/StampaGraduatoria"+reportpath+"_CC.rpt");
					}

				} else if ("4".equals(ConfigGraduatoria_cm)){

					if (isIncrocioCM) {
						setReportPath("pubb/StampaGraduatoriaVDA"+reportpathVDA+"Def_CC.rpt");
					} else {
						setReportPath("pubb/StampaGraduatoriaVDA"+reportpathVDA+"_CC.rpt");
					}

				} else if ("2".equals(ConfigGraduatoria_cm)){

					// attenzione ai nomi dei file che sono fuorvianti!
					if (isIncrocioCM) {
						// utilizzata per CM etc
						setReportPath("pubb/O_StampaGraduatoriaDef_CC.rpt");
					} else {
						// utilizzata per Art 16 etc
						setReportPath("pubb/StampaGraduatoriaDef_UMB_CC.rpt");
					}

				} else {
					
					if (isIncrocioCM) {
						if (visualizzaCodiceAdesione) {
							if (nominativa.equals("1")) {
								setReportPath("pubb/O_StampaGraduatoriaNominativaDef_CC.rpt");
							}
							else {
								setReportPath("pubb/O_StampaGraduatoriaAnonimaDef_CC.rpt");
							}
						}
						else {
							setReportPath("pubb/O_StampaGraduatoriaDef_CC.rpt");
						}
					} else {
						//Regione Calabria
						if(codRegioneSil.equalsIgnoreCase("18")){
							setReportPath("pubb/O_StampaGraduatoria_Cal_CC.rpt");
						}else{
							setReportPath("pubb/O_StampaGraduatoria_CC.rpt");
						}

					}

				}
							}

			String descrizione = (String) request.getAttribute("ConcatenaCpi");
			/*
			 * if (descrizione != null && !descrizione.equals("")) {
			 * setStrEnteRilascio(descrizione); }
			 */
			// recupero la descrizione del cpi
			_db.setSectionQuerySelect("GET_DESCRCPI_STAMPA");
			SourceBean beanRows = null;
			beanRows = (SourceBean) _db.doSelect(request, response);

			String strDescrizione = StringUtils.getAttributeStrNotNull(beanRows, "ROW.STRDESCRIZIONE");
			if (strDescrizione != null && !strDescrizione.equals("")) {
				setStrEnteRilascio(strDescrizione);
			}

			String codCpi = (String) request.getAttribute("CodCPI");
			if (codCpi != null && !codCpi.equals("")) {
				setCodCpi(codCpi);
			}
			/*
			 * Vector params = null; params = new Vector(2);
			 */
			// String prgTipoIncrocio=(String)
			// request.getAttribute("prgTipoIncrocio");
			/*
			 * if (prgTipoIncrocio != null && !prgTipoIncrocio.equals("")) {
			 * params.add(request.getAttribute("prgTipoIncrocio")); } else{
			 * params.add(""); }
			 */
			String prgRichiestaAz = (String) request.getAttribute("prgRichiestaAz");
			/*
			 * if (prgRichiestaAz != null && !prgRichiestaAz.equals("")) {
			 * params.add(request.getAttribute("prgRichiestaAz")); } else{
			 * params.add(""); }
			 */
			// setParams(params);
			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("prgRichiestaAz"));
			if (!strChiaveTabella.equals("")) {
				setStrChiavetabella(strChiaveTabella);
			}
			BigDecimal prgAzienda = new BigDecimal((String) request.getAttribute("prgAzienda"));
			BigDecimal prgUnita = new BigDecimal((String) request.getAttribute("prgUnita"));
			setCodCpi(codCpi);
			if (("10").equals(prgTipoIncrocio) || ("11").equals(prgTipoIncrocio) || ("12").equals(prgTipoIncrocio)) {
				setPagina("CMGestGraduatoriePage");
			} else {
				setPagina("ASGestGraduatoriePage");
			}
			setPrgAzienda(prgAzienda);
			setPrgUnita(prgUnita);

			String regione = "";
			String provTarga = "";
			try {
				// TODO controllo per regione reintrodotto per logo unione europea
				SourceBean righe = (SourceBean) QueryExecutor.executeQuery("GET_INFO_TARGA_PROVINCIA_REGIONE", null, "SELECT", Values.DB_SIL_DATI);
				regione = (String) righe.getAttribute("ROW.CODREGIONE");
				provTarga = (String) righe.getAttribute("ROW.STRTARGA");
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, "StampaDefinitiva:service():errore AccessoSemplificato", e);			
			}

			// setCodCpi(codCpi);
			// setStrEnteRilascio(descrCpi);
			// 16/02/2007 Savino: aggiunti i parametri della protocollazione. Li
			// passo (tutti) per nome e non per posizione
			Map prompts = new HashMap();
			prompts.put("prgTipoIncrocio", prgTipoIncrocio);
			prompts.put("prgRichiestaAz", prgRichiestaAz);
			prompts.put("regione", regione);
			// passo il tipo graduatoria
			prompts.put("ConfigGraduatoria_cm", ConfigGraduatoria_cm);
			if (visualizzaCodiceAdesione) {
				prompts.put("provTarga", provTarga);
			}
			
			if (isIncrocioCM) {		// per la gestione del collocamento mirato
				SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
				User user = (User) sessione.getAttribute(User.USERID);
				String cpi = user.getCodRif();
				if (cpi != null &&  !cpi.equals("")) {
					prompts.put("codCpiUser", cpi);           
				}			
				else{
					prompts.put("codCpiUser", "-1");             
				}
				
				// 0 config. default - 1 config. custom (RER)
				UtilsConfig utility = new UtilsConfig("CM_STAMP");
				String configStampeCustom = utility.getConfigurazioneDefault_Custom();
				if (configStampeCustom != null &&  !configStampeCustom.equals("")) {
					prompts.put("configStampeCustom", configStampeCustom);
				}			
				else{
					prompts.put("configStampeCustom", "0");           
				}
			}

			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			addPromptFieldsProtocollazione(prompts, request);
			setPromptFields(prompts);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				if (!insertDocument(request, response))
					throw new Exception("Stampa graduatoria definitiva fallita");
			} else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);

		} catch (Exception e) {
			setOperationFail(request, response, e);
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa della graduatoria", e);

		}
	}
}
