package it.eng.sil.module.voucher;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class DynRicercaTDA implements IDynamicStatementProvider {

	public DynRicercaTDA() {
	}

	private static final String SELECT_SQL_BASE = "SELECT VCH.PRGVOUCHER PRGVOUCHER, LAV.CDNLAVORATORE CDNLAVORATORE, "
			+ " VCH.STRCFENTEACCREDITATO || AN_VCH_ENTE.STRDENOMINAZIONE ENTEATTIVATO, "
			+ " CPICOMP.STRDESCRIZIONE || '-' || CPICOMP.CODCPI CPICOMPTIT, "
			+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, LAV.STRCOGNOME STRCOGNOME, LAV.STRNOME STRNOME, "
			+ " AZ.STRDESCRIZIONE DESCAZIONE, TO_CHAR(VCH.DATMAXEROGAZIONE, 'DD/MM/YYYY') DATMAXEROGAZIONE, "
			+ " TO_CHAR(VCH.DATATTIVAZIONE, 'DD/MM/YYYY') DATATTIVAZIONE, STATOVCH.STRDESCRIZIONE STATOVOUCHER, VCH.CODATTIVAZIONE CODATTIVAZIONE, ANNULLVCH.STRDESCRIZIONE DESCRANNULL "
			+ " FROM OR_VCH_VOUCHER VCH INNER JOIN OR_PERCORSO_CONCORDATO PER ON (VCH.PRGPERCORSO = PER.PRGPERCORSO AND VCH.PRGCOLLOQUIO = PER.PRGCOLLOQUIO) "
			+ " LEFT JOIN AN_VCH_ENTE ON (VCH.STRCFENTEACCREDITATO = AN_VCH_ENTE.STRCODICEFISCALE AND VCH.CODSEDE = AN_VCH_ENTE.CODSEDE) "
			+ " INNER JOIN DE_VCH_STATO STATOVCH ON (VCH.CODSTATOVOUCHER = STATOVCH.CODSTATOVOUCHER) "
			+ " LEFT JOIN DE_VCH_MOTIVO_ANNULLAMENTO ANNULLVCH ON (VCH.CODVCHMOTIVOANNULLAMENTO = ANNULLVCH.CODVCHMOTIVOANNULLAMENTO) "
			+ " LEFT JOIN DE_VCH_STATO_PAGAMENTO STATOPAG ON (VCH.CODVCHSTATOPAGAMENTO = STATOPAG.CODVCHSTATOPAGAMENTO) "
			+ " INNER JOIN DE_AZIONE AZ ON (AZ.PRGAZIONI = PER.PRGAZIONI) "
			+ " INNER JOIN OR_COLLOQUIO COLL ON (PER.PRGCOLLOQUIO = COLL.PRGCOLLOQUIO) "
			+ " INNER JOIN DE_CPI CPICOMP ON (COLL.CODCPI = CPICOMP.CODCPI) "
			+ " INNER JOIN AN_LAVORATORE LAV ON (COLL.CDNLAVORATORE = LAV.CDNLAVORATORE) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();
		User user = (User) session.getAttribute(User.USERID);
		String tipoGruppoCollegato = user.getCodTipo();

		String codiceAttivazione = SourceBeanUtils.getAttrStrNotNull(req, "strCodAttivazione");
		String nome = SourceBeanUtils.getAttrStrNotNull(req, "strNome");
		String cognome = SourceBeanUtils.getAttrStrNotNull(req, "strCognome");
		String cf = SourceBeanUtils.getAttrStrNotNull(req, "strCodiceFiscale");
		String tipoRic = SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
		String statoVch = SourceBeanUtils.getAttrStrNotNull(req, "statoTDA");
		String dataStatoDal = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoDa");
		String dataStatoAl = SourceBeanUtils.getAttrStrNotNull(req, "dataStatoA");
		String azione = SourceBeanUtils.getAttrStrNotNull(req, "azioneTDA");
		String motivoAnull = SourceBeanUtils.getAttrStrNotNull(req, "codAnnullTDA");
		String statoPagamento = SourceBeanUtils.getAttrStrNotNull(req, "statoPagamentoTDA");
		String assegnatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "assegnatiScaduti");
		String attivatiScaduti = SourceBeanUtils.getAttrStrNotNull(req, "attivatiScaduti");
		String codCpi = SourceBeanUtils.getAttrStrNotNull(req, "codCPI");
		String cfEnte = SourceBeanUtils.getAttrStrNotNull(req, "cfEnteAtt");
		String sedeEnte = SourceBeanUtils.getAttrStrNotNull(req, "sedeEnteAtt");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

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

		if (tipoGruppoCollegato.equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
			if (!sedeEnte.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(VCH.codsede) = '" + sedeEnte.toUpperCase() + "'");
			}
		} else {
			if (!sedeEnte.equals("")) {
				String[] sede = sedeEnte.split("!");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(vch.strcfenteaccreditato) = '" + sede[0].toUpperCase()
						+ "' and upper(vch.codsede) = '" + sede[1].toUpperCase() + "' ");
			} else {
				if (!cfEnte.equals("")) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(VCH.strcfenteaccreditato) = '" + cfEnte.toUpperCase() + "'");
				}
			}
		}

		if ((codiceAttivazione != null) && (!codiceAttivazione.equals(""))) {
			codiceAttivazione = StringUtils.replace(codiceAttivazione, "'", "''");
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(vch.codattivazione) = '" + codiceAttivazione.toUpperCase() + "'");
		}

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
			buf.append(" upper(VCH.CODSTATOVOUCHER) = '" + statoVch.toUpperCase() + "'");

			if ((dataStatoDal != null) && (!dataStatoDal.equals(""))) {
				buf.append(" AND ");
				buf.append(" TRUNC(VCH." + nomeCampoData + ") >= to_date('" + dataStatoDal + "', 'dd/mm/yyyy') ");
			}
			if ((dataStatoAl != null) && (!dataStatoAl.equals(""))) {
				buf.append(" AND ");
				buf.append(" TRUNC(VCH." + nomeCampoData + ") <= to_date('" + dataStatoAl + "', 'dd/mm/yyyy') ");
			}
		}

		if ((motivoAnull != null) && (!motivoAnull.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" VCH.CODVCHMOTIVOANNULLAMENTO = '" + motivoAnull + "'");
		}

		if ((statoPagamento != null) && (!statoPagamento.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" VCH.CODVCHSTATOPAGAMENTO = '" + statoPagamento + "'");
		}

		if ((assegnatiScaduti != null) && (!assegnatiScaduti.equals("")) && (assegnatiScaduti.equalsIgnoreCase("on"))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			/* codstatovoucher = 'ASS' and datMaxattivazione < sysdate */
			buf.append(" upper(VCH.CODSTATOVOUCHER) = 'ASS'");
			buf.append(" AND TRUNC(VCH.DATMAXATTIVAZIONE) < TRUNC(SYSDATE)");
		}

		if ((attivatiScaduti != null) && (!attivatiScaduti.equals("")) && (attivatiScaduti.equalsIgnoreCase("on"))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			/* codstatovoucher = 'ASS' and datMaxattivazione < sysdate */
			buf.append(" upper(VCH.CODSTATOVOUCHER) = 'ATT'");
			buf.append(" AND TRUNC(VCH.DATMAXEROGAZIONE) < TRUNC(SYSDATE)");
		}

		if ((azione != null) && (!azione.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" PER.PRGAZIONI = " + azione);
		}

		if ((codCpi != null) && (!codCpi.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" COLL.CODCPI = '" + codCpi + "'");
		}

		buf.append(" ORDER BY VCH.DATMAXEROGAZIONE");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}