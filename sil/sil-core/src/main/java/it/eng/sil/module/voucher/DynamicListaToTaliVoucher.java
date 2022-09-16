/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;

/**
 * @author Fatale
 *
 */
public class DynamicListaToTaliVoucher implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT "
			+ " EXTRACT(year FROM nvl(datAttivazione, datAssegnazione)) ANNO, "
			+ "  az.strdescrizione AZIONE,              " + "  st.strdescrizione STATO, count(*) CONTEGGIO,   "
			+ "   SUM(CASE WHEN  vou.codstatovoucher= 'CHI'     " + "       and vou.codvchstatopagamento ='INA'       "
			+ "       then decspesaeffettiva                    " + "       else 0                                    "
			+ "        end) tot_euro_attesa,                    " + "   SUM(CASE WHEN  vou.codstatovoucher= 'CHI'     "
			+ "   and vou.codvchstatopagamento in ('PAG','NPAG')" + "   then decpagato else 0 end )  Tot_euro_Pagati  "
			+ "   from OR_VCH_VOUCHER Vou                       " + "   left outer join or_percorso_concordato per    "
			+ "   on vou.prgcolloquio= per.prgcolloquio         " + "    and vou.prgpercorso =per.prgpercorso         "
			+ "   left outer join or_colloquio cl               " + "    on per.prgcolloquio = cl.prgcolloquio        "
			+ "   left outer join an_lavoratore lav             " + "    on cl.cdnlavoratore=lav.cdnlavoratore        "
			+ "    left outer join de_azione az                 " + "      on per.prgazioni=az.prgazioni              "
			+ "    left outer join de_vch_stato st              " + "      on vou.codstatovoucher=st.codstatovoucher  "
			+ "   WHERE 1=1                                      ";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaToTaliVoucher.class.getName());

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug("Sono dentro l'azione della ricerca per i totali della lista");

		// String statement = SQLStatements.getStatement("GET_LISTA_TOTALI_VOUCHER_TEST");
		// String statement = SQLStatements.getStatement("GET_LISTA_TOTALI_VOUCHER");
		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		SourceBean req = requestContainer.getServiceRequest();
		// Aggiunta nuovi parametri
		SessionContainer session = requestContainer.getSessionContainer();
		User user = (User) session.getAttribute(User.USERID);
		String tipoGruppoCollegato = user.getCodTipo();

		// SourceBean req = reqCont.getServiceRequest();

		String nome = SourceBeanUtils.getAttrStrNotNull(req, "nomeSel");
		String cognome = SourceBeanUtils.getAttrStrNotNull(req, "cognomeSel");
		String cf = SourceBeanUtils.getAttrStrNotNull(req, "cf");
		String tipoRic = SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
		String statoVch = SourceBeanUtils.getAttrStrNotNull(req, "PRGSTATO");
		String azione = SourceBeanUtils.getAttrStrNotNull(req, "PRGAZIONE");

		String codCpi = SourceBeanUtils.getAttrStrNotNull(req, "codCPI");
		String cfEnte = SourceBeanUtils.getAttrStrNotNull(req, "cfEnte");
		String sedeEnte = SourceBeanUtils.getAttrStrNotNull(req, "cfSedeEnte");

		// Modifica 03/10/2016 aggiunti nuovi parametri di nella ricerca aggiunti
		String codiceAttivazione = SourceBeanUtils.getAttrStrNotNull(req, "strCodAttivazione");
		String dataStatoDal = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoDa");
		String dataStatoAl = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoA");
		String motivoAnull = SourceBeanUtils.getAttrStrNotNull(req, "codAnnullTDA");
		String statoPagamento = SourceBeanUtils.getAttrStrNotNull(req, "statoPagamentoTDA");
		String assegnatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "assegnatiScaduti");
		String attivatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "attivatiScaduti");

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}

		// //Modifica 03/10/2016 aggiunti nuovi parametri di nella ricerca
		// 1. aggiunto il codice di attivazione
		if ((codiceAttivazione != null) && (!codiceAttivazione.equals(""))) {
			codiceAttivazione = StringUtils.replace(codiceAttivazione, "'", "''");
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(vou.codattivazione) = '" + codiceAttivazione.toUpperCase() + "'");
		}

		// 2. modifica del campo stato con aggiunta delle date

		if ((statoVch != null) && (!statoVch.equals(""))) {

			String nomeCampoData = "";

			if (statoVch.equalsIgnoreCase("ANN")) {
				nomeCampoData = "DTMUTANN";
			} else if (statoVch.equalsIgnoreCase("ASS")) {
				nomeCampoData = "DTMUTASS";
			} else if (statoVch.equalsIgnoreCase("ATT")) {
				nomeCampoData = "DTMUTATT";
			} else if (statoVch.equalsIgnoreCase("CHI")) {
				nomeCampoData = "DTMUTCONC";
			}
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(VOU.CODSTATOVOUCHER) = '" + statoVch.toUpperCase() + "'");

			if ((dataStatoDal != null) && (!dataStatoDal.equals(""))) {
				buf.append(" AND ");
				buf.append(" TRUNC(VOU." + nomeCampoData + ") >= to_date('" + dataStatoDal + "', 'dd/mm/yyyy') ");
			}
			if ((dataStatoAl != null) && (!dataStatoAl.equals(""))) {
				buf.append(" AND ");
				buf.append(" TRUNC(VOU." + nomeCampoData + ") <= to_date('" + dataStatoAl + "', 'dd/mm/yyyy') ");
			}
		}

		// 3. modifica del campo stato pagamento
		if ((statoPagamento != null) && (!statoPagamento.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" VOU.CODVCHSTATOPAGAMENTO = '" + statoPagamento + "'");
		}

		// 4. modifica del campo Motivo annullamento
		if ((motivoAnull != null) && (!motivoAnull.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" VOU.CODVCHMOTIVOANNULLAMENTO = '" + motivoAnull + "'");
		}

		// 5. modifica del campo Assegnati scaduti AND (vou.codstatovoucher ='ASS' AND trunc(vou.datMaxattivazione) <
		// trunc(sysdate))
		if ((assegnatiScaduti != null) && (!assegnatiScaduti.equals("")) && (assegnatiScaduti.equalsIgnoreCase("on"))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}

			buf.append(" upper(VOU.CODSTATOVOUCHER) = 'ASS'");
			buf.append(" AND TRUNC(VOU.DATMAXATTIVAZIONE) < TRUNC(SYSDATE)");
		}

		// 6. modifica del campo Attivati Scaduti AND (vou.codstatovoucher ='ATT' AND trunc(vou.datMAxErogazione) <
		// trunc(sysdate))

		if ((attivatiScaduti != null) && (!attivatiScaduti.equals("")) && (attivatiScaduti.equalsIgnoreCase("on"))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}

			buf.append(" upper(VOU.CODSTATOVOUCHER) = 'ATT'");
			buf.append(" AND TRUNC(VOU.DATMAXEROGAZIONE) < TRUNC(SYSDATE)");
		}

		if ((azione != null) && (!azione.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" per.prgazioni = " + azione);
		}

		// La parte se ente o sede
		// Modificato
		if (!sedeEnte.equals("")) {

			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(Vou.codsede) = '" + sedeEnte.toUpperCase() + "'");
		} else {
			if (!cfEnte.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(Vou.strcfenteaccreditato) = '" + cfEnte.toUpperCase() + "'");
			}
		}

		// Codice cpi
		if ((codCpi != null) && (!codCpi.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" cl.CODCPI = '" + codCpi + "'");
		}

		buf.append(
				" group by EXTRACT(year FROM nvl(datAttivazione, datAssegnazione)) , az.strdescrizione, st.strdescrizione");
		buf.append(
				" order by EXTRACT(year FROM nvl(datAttivazione, datAssegnazione)) desc, az.strdescrizione, st.strdescrizione");

		_logger.debug("Query ottenuta per la lista dettaglio ::: " + buf.toString());

		return buf.toString();
	}

}
