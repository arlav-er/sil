package it.eng.sil.module.fs;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.security.User;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class MenuCompleto extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MenuCompleto.class.getName());
	private String className = this.getClass().getName();

	public MenuCompleto() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;
		BigDecimal bCdnLavoratore = null;

		boolean reloadTopPage = false;

		try {
			if (request.getAttribute("PRG_PROF_UT") != null) {
				// *****************************************************
				// *** Caricamento del profilo utente nell'oggetto USER
				// ******************************************************
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY1");

				rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "SELECT");

				// TracerSingleton.log( it.eng.sil.Values.APP_NAME,
				// TracerSingleton.DEBUG,// className + "::service()
				// rowsSourceBean", rowsSourceBean);
				BigDecimal bPrgProfiloUtente = (BigDecimal) rowsSourceBean.getAttribute("ROW.Prg_Profilo_Utente");

				if (bPrgProfiloUtente != null) {
					user.setPrgProfilo(bPrgProfiloUtente.intValue());
				}

				BigDecimal bCdnProfilo = (BigDecimal) rowsSourceBean.getAttribute("ROW.Cdn_Profilo");

				if (bCdnProfilo != null) {
					user.setCdnProfilo(bCdnProfilo.intValue());
				}

				BigDecimal bCdnGruppo = (BigDecimal) rowsSourceBean.getAttribute("ROW.Cdn_gruppo");

				if (bCdnGruppo != null) {
					user.setCdnGruppo(bCdnGruppo.intValue());
				}

				BigDecimal bCdnTipoGruppo = (BigDecimal) rowsSourceBean.getAttribute("ROW.CDN_TIPO_GRUPPO");

				if (bCdnTipoGruppo != null) {
					user.setCdnTipoGruppo(bCdnTipoGruppo.intValue());
				}

				String descProfilo = (String) rowsSourceBean.getAttribute("ROW.desc_profilo");
				user.setDescProfilo(descProfilo);

				String descGruppo = (String) rowsSourceBean.getAttribute("ROW.desc_gruppo");
				user.setDescGruppo(descGruppo);

				String descTipoGruppo = (String) rowsSourceBean.getAttribute("ROW.desc_tipo_gruppo");
				user.setDescTipoGruppo(descTipoGruppo);

				String codRif = (String) rowsSourceBean.getAttribute("ROW.COD_RIF");
				user.setCodRif(codRif);

				String codRif2 = (String) rowsSourceBean.getAttribute("ROW.COD_RIF2");
				user.setCodRif2(codRif2);

				bCdnLavoratore = (BigDecimal) rowsSourceBean.getAttribute("ROW.CDNLAVORATORE");

				if ((bCdnLavoratore != null) && user.getCdnTipoGruppo() == 10) {
					request.setAttribute("CDNLAVORATORE", bCdnLavoratore.toString());

					reloadTopPage = true;
				}

				if (user.getCdnTipoGruppo() == 20 && user.getCodRif() != null && user.getCodRif2() != null

				) {

					request.setAttribute("PRGAZIENDA", user.getCodRif());
					request.setAttribute("PRGUNITA", user.getCodRif2());

					reloadTopPage = true;

				}

				String codTipoProfilo = (String) rowsSourceBean.getAttribute("ROW.COD_TIPO");
				if (codTipoProfilo != null) {
					user.setCodTipo(codTipoProfilo);
				} else {
					user.setCodTipo("");
				}

				String cfUtCollegato = (String) rowsSourceBean.getAttribute("ROW.CF_TIPO_GRUPPO");
				if (cfUtCollegato != null) {
					user.setCfUtenteCollegato(cfUtCollegato);
				} else {
					user.setCfUtenteCollegato("");
				}
			}

			// *****************************************************
			// *** Recupero del menu generale
			// ******************************************************

			boolean limitaAperturaMenuGen = (user.getCdnTipoGruppo() == 10 && bCdnLavoratore == null)
					|| (user.getCdnTipoGruppo() == 20 && user.getCodRif2() == null);

			if (!limitaAperturaMenuGen) {

				sessionContainer.setAttribute("PRG_PROF_UT", String.valueOf(user.getPrgProfilo()));
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY2");

				rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "SELECT");

				rowsSourceBean = filtraMenu(rowsSourceBean);

				response.setAttribute("MENU_GEN", rowsSourceBean);

			}

			// *****************************************************
			// *** Recupero del menu del lavoratore
			// ******************************************************
			if (request.getAttribute("CDNLAVORATORE") != null) {

				// identità del lavoratore
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY0");
				rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "SELECT");

				boolean esisteLav = rowsSourceBean.containsAttribute("ROW");

				if (esisteLav) {

					response.setAttribute("IDENTITA_LAV", rowsSourceBean);

					// Menu del lavoratore
					statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY3");

					rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
							getResponseContainer(), pool, statement, "SELECT");

					rowsSourceBean = filtraMenu(rowsSourceBean);
					response.setAttribute("MENU_LAVORATORE", rowsSourceBean);
				}
			}

			// *****************************************************
			// *** Recupero del menu dell'azienda
			// ******************************************************
			if ((request.getAttribute("PRGAZIENDA") != null) && (request.getAttribute("PRGUNITA") != null)) {
				// identità dell'azienda
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY5");

				rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "SELECT");

				boolean esisteAz = rowsSourceBean.containsAttribute("ROW");

				if (esisteAz) {

					response.setAttribute("IDENTITA_AZ", rowsSourceBean);

					// Menu del lavoratore
					statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY4");

					rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
							getResponseContainer(), pool, statement, "SELECT");

					rowsSourceBean = filtraMenu(rowsSourceBean);
					response.setAttribute("MENU_AZIENDA", rowsSourceBean);
				}
			}

			if (reloadTopPage) {
				response.setAttribute("RELOAD_TOP_PAGE", "true");
			}

			// Si buttano nella serviceResponse i paramentri che potrebbero
			// essere

			String prgAzienda = (String) request.getAttribute("PRGAZIENDA");
			String prgUnita = (String) request.getAttribute("PRGUNITA");
			String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");

			if (prgAzienda != null)
				response.setAttribute("PRGAZIENDA", prgAzienda);

			if (prgUnita != null)
				response.setAttribute("PRGUNITA", prgUnita);

			if (cdnLavoratore != null)
				response.setAttribute("CDNLAVORATORE", cdnLavoratore);

			// Fine
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}
	}

	private SourceBean filtraMenu(SourceBean rows) {
		SourceBean newRows = null;

		try {
			newRows = new SourceBean("ROWS");

			Set paths = recuperaPaths(rows);

			List righe = rows.getAttributeAsVector("ROW");

			for (int i = 0; i < righe.size(); i++) {
				SourceBean row = (SourceBean) righe.get(i);
				String path = (String) row.getAttribute("PATH");

				if (startsWith(paths, path + "/")) {
					newRows.setAttribute(row);
				}
			}
		} catch (SourceBeanException ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::filtraMenu()", ex);

		}

		return newRows;
	}

	private Set recuperaPaths(SourceBean rows) {
		Set paths = new HashSet();

		List righe = rows.getAttributeAsVector("ROW");

		for (int i = 0; i < righe.size(); i++) {
			SourceBean row = (SourceBean) righe.get(i);
			String pagina = (String) row.getAttribute("STRPAGE");

			if (pagina != null) {
				String path = (String) row.getAttribute("PATH");

				paths.add(path + "/");
			}
		}

		return paths;
	}

	private boolean startsWith(Set paths, String path) {
		Iterator iter = paths.iterator();

		while (iter.hasNext()) {
			String pezzo = (String) iter.next();

			if (pezzo.startsWith(path)) {
				return true;
			}
		}

		return false;
	}
}