package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class DynStatementRicercaEvidenze implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String stm = "SELECT l.strcodicefiscale AS strcodicefiscale, l.strcognome AS strcognome, l.strnome AS strnome,"
				+ "       an_evidenza.prgEvidenza, an_evidenza.cdnLavoratore,                                      "
				+ "       strevidenza AS strevidenza, an_evidenza.datDataScad as ord1                              "
				+ "  FROM an_evidenza, an_lavoratore l, de_tipo_evidenza, ts_vis_evidenza, ts_utente               "
				+ " WHERE l.cdnlavoratore = an_evidenza.cdnlavoratore                                              "
				+ "   AND an_evidenza.prgtipoevidenza = de_tipo_evidenza.prgtipoevidenza                           "
				+ "   AND an_evidenza.cdnutins = ts_utente.cdnut                                                   "
				+ "   AND ts_vis_evidenza.prgtipoevidenza = de_tipo_evidenza.prgtipoevidenza                       ";

		SourceBean request = requestContainer.getServiceRequest();
		String prgtipoevidenza = Utils.notNull(request.getAttribute("prgtipoevidenzaRic"));
		String codiceFiscale = Utils.notNull(request.getAttribute("strCodiceFiscaleRic"));
		String cognome = Utils.notNull(request.getAttribute("strcognomeRic"));
		String nome = Utils.notNull(request.getAttribute("strnomeRic"));
		String messaggio = Utils.notNull(request.getAttribute("messaggioRic"));
		String tipoRicerca = Utils.notNull(request.getAttribute("tipoRicerca"));
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
		SessionContainer sexCont = requestContainer.getSessionContainer();
		User user = (User) sexCont.getAttribute(User.USERID);
		String cdnGruppo = Integer.toString(user.getCdnGruppo());
		String cdnProfilo = Integer.toString(user.getCdnProfilo());
		StringBuffer where = new StringBuffer(" and trunc(datDataScad) >= trunc(sysdate)");
		StringBuffer orderBy = new StringBuffer(" order by ord1 desc, strcognome, strnome");
		if (!prgtipoevidenza.equals("")) {
			where.append(" and an_evidenza.prgtipoevidenza = ");
			where.append(prgtipoevidenza);
		}
		if (!cdnGruppo.equals("")) {
			where.append(" and  ts_vis_evidenza.cdnGruppo = ");
			where.append(cdnGruppo);
		}
		if (!cdnProfilo.equals("")) {
			where.append(" and ts_vis_evidenza.cdnProfilo = ");
			where.append(cdnProfilo);
		}
		if (tipoRicerca.equals("esatta")) {
			if (!cognome.equals("")) {
				where.append(" and l.strcognome = '");
				where.append(cognome.toUpperCase());
				where.append("'");
			}
			if (!nome.equals("")) {
				where.append(" and l.strnome = '");
				where.append(nome.toUpperCase());
				where.append("'");
			}
			if (!codiceFiscale.equals("")) {
				where.append(" and l.strcodicefiscale = '");
				where.append(codiceFiscale.toUpperCase());
				where.append("'");
			}
		} else {
			if (!cognome.equals("")) {
				where.append(" and l.strcognome like '");
				where.append(cognome.toUpperCase());
				where.append("%'");
			}
			if (!nome.equals("")) {
				where.append(" and l.strnome like '");
				where.append(nome.toUpperCase());
				where.append("%'");
			}
			if (!codiceFiscale.equals("")) {
				where.append(" and l.strcodicefiscale like '");
				where.append(codiceFiscale.toUpperCase());
				where.append("%'");
			}
		}
		if (!messaggio.equals("")) {
			where.append(" and upper(an_evidenza.strevidenza) like '%");
			if (messaggio.indexOf("%") >= 0 || messaggio.indexOf("_") >= 0) {
				messaggio = StringUtils.replace(messaggio, "/", "//");
				messaggio = StringUtils.replace(messaggio, "_", "/_");
				messaggio = StringUtils.replace(messaggio, "%", "/%");
				where.append(StringUtils.formatValue4Sql(messaggio.toUpperCase()));
				where.append("%' escape '/'");
			} else {
				where.append(StringUtils.formatValue4Sql(messaggio.toUpperCase()));
				where.append("%'");
			}
		}
		return stm + where.toString() + orderBy.toString();
	}

}
