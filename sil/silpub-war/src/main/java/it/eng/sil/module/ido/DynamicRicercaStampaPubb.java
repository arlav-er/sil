/*
 * Creato il Dec 9, 2004
 * 
 */
package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author savino 
 * 
 * Seleziona le offerte di lavoro da spampare in formato html. La classe e' stata ripresa da
 * it.eng.sil.module.ido.DynStatamentRicercaPubb (progetto sil).
 *
 */
public class DynamicRicercaStampaPubb implements IDynamicStatementProvider {
	
	private static final String SELECT_SQL_BASE =
			"SELECT  DISTINCT dr.CODCPI, TO_CHAR(dr.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE,"
				+ "		   dr.NUMANNO, dr.NUMRICHIESTA, nvl(dr.NUMRICHIESTAORIG, dr.NUMRICHIESTA) NUMRICHIESTAORIG,"
				+ "		   dr.FLGPUBBLICATA,"
				+ "		   dr.STRMANSIONEPUBB, EV.CDNSTATORICH,"
				+ "		   dr.STRDATIAZIENDAPUBB,"
				+ "		   dr.STRLUOGOLAVORO,"
				+ "		   dr.STRFORMAZIONEPUBB,"
				+ "		   dr.TXTCARATTERISTFIGPROF,"
				+ "		   dr.TXTCONDCONTRATTUALE,"
				+ "		   dr.STRNOTEORARIOPUBB,"
				+ "		   dr.STRCONOSCENZEPUBB,"
				+ "		   dr.STRRIFCANDIDATURAPUBB,"
				+ "		   TO_CHAR(dr.DATSCADENZA,'dd/mm/yyyy') DATSCADENZA, dr.PRGRICHIESTAAZ,"
				+ "		   dr.TXTFIGURAPROFESSIONALE,"
				+ "		   DE_PROVINCIA.STRDENOMINAZIONE, DE_CPI.STRDESCRIZIONE,"
				+ "		   DE_CPI.STRINDIRIZZO, DE_CPI.STRTEL, DE_CPI.STRFAX,"
				+ "		   DE_CPI.STREMAIL, dr.NUMSTORICO,"
				+ "		   TO_CHAR(dr.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE, "
				+ "        CODEVASIONE ," 
				+ "		   decode(ev.CODEVASIONE,'DFA',ts_gruppo.strdenominazione,'DRA',ts_gruppo.strdenominazione,null) as descrizioneAzienda,"
				+ "		   decode(ev.CODEVASIONE,'DFA',ts_gruppo.stremailpubbl,'DRA',ts_gruppo.stremailpubbl, null) as stremailpubbl,"
				+ "		   dr.DATPUBBLICAZIONE as dataPubSort "
				+ "FROM do_richiesta_az dr "
				+ "inner join DO_EVASIONE ev on (dr.PRGRICHIESTAAZ=ev.PRGRICHIESTAAZ) "
				+ "inner join DE_CPI on (dr.CODCPI=de_cpi.CODCPI) "
				+ "	inner join de_comune comunecpi on (de_cpi.CODCOM=comunecpi.CODCOM) "
				+ "	inner join DE_PROVINCIA on (comunecpi.CODPROVINCIA=DE_PROVINCIA.CODPROVINCIA) "
				+ "left outer join de_qualifica_pub on (dr.CODQUALIFICA=de_qualifica_pub.CODQUALIFICA) " 
				+ "left join ts_gruppo on ts_gruppo.cdngruppo = dr.cdngruppo ";
				//	+ "left join (ts_profilatura_utente prof inner join ts_gruppo on "
			  	//+ "prof.cdngruppo=ts_gruppo.cdngruppo) "
			  	//+ "on dr.CDNUTINS=prof.cdnut "
			  	//+ "and dr.CODCPI=ts_gruppo.strcodrif "
			  	
	private static final String SELECT_SQL_BASE_CM =
		"SELECT  DISTINCT dr.CODCPI, TO_CHAR(dr.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE,"
			+ "		   dr.NUMANNO, dr.NUMRICHIESTA, nvl(dr.NUMRICHIESTAORIG, dr.NUMRICHIESTA) NUMRICHIESTAORIG,"
			+ "		   dr.FLGPUBBLICATA,"
			+ "		   dr.STRMANSIONEPUBB, EV.CDNSTATORICH,"
			+ "		   dr.STRDATIAZIENDAPUBB,"
			+ "		   dr.STRLUOGOLAVORO,"
			+ "		   dr.STRFORMAZIONEPUBB,"
			+ "		   dr.TXTCARATTERISTFIGPROF,"
			+ "		   dr.TXTCONDCONTRATTUALE,"
			+ "		   dr.STRNOTEORARIOPUBB,"
			+ "		   dr.STRCONOSCENZEPUBB,"
			+ "		   dr.STRRIFCANDIDATURAPUBB,"
			+ "		   TO_CHAR(dr.DATSCADENZA,'dd/mm/yyyy') DATSCADENZA, dr.PRGRICHIESTAAZ,"
			+ "		   dr.TXTFIGURAPROFESSIONALE,"
			+ "		   DE_PROVINCIA.STRDENOMINAZIONE, DE_CPI.STRDESCRIZIONE,"
			+ "		   DE_CPI.STRINDIRIZZO, DE_CPI.STRTEL, DE_CPI.STRFAX,"
			+ "		   DE_CPI.STREMAIL, dr.NUMSTORICO,"
			+ "		   TO_CHAR(dr.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE, "
			+ "        CODEVASIONE ," 
			+ "		   decode(ev.CODEVASIONE,'DFA',ts_gruppo.strdenominazione,'DRA',ts_gruppo.strdenominazione,null) as descrizioneAzienda,"
			+ "		   decode(ev.CODEVASIONE,'DFA',ts_gruppo.stremailpubbl,'DRA',ts_gruppo.stremailpubbl, null) as stremailpubbl,"
			+ "		   dr.DATPUBBLICAZIONE as dataPubSort, "
			+ "		   case "
			+ "		   when ev.CODEVASIONE in ('MPP','MPA') "
			+ "		   then( "
			+ "		   case "
			+ "		   when dr.CODMONOCMCATPUBB = 'D' "
			+ "		   then 'Disabili' "
			+ "		   when dr.CODMONOCMCATPUBB = 'A' "
			+ "		   then 'Ex. Art. 18' "
			+ "		   when dr.CODMONOCMCATPUBB = 'E' "
			+ "		   then 'Entrambi' "
			+ "		   else '' "
			+ "		   end) " 
			+ "		   else '' " 
			+ "		   end CODMONOCMCATPUBB "
			+ "FROM do_richiesta_az dr "
			+ "inner join DO_EVASIONE ev on (dr.PRGRICHIESTAAZ=ev.PRGRICHIESTAAZ) "
			+ "inner join DE_CPI on (dr.CODCPI=de_cpi.CODCPI) "
			+ "	inner join de_comune comunecpi on (de_cpi.CODCOM=comunecpi.CODCOM) "
			+ "	inner join DE_PROVINCIA on (comunecpi.CODPROVINCIA=DE_PROVINCIA.CODPROVINCIA) "
			+ "left outer join de_qualifica_pub on (dr.CODQUALIFICA=de_qualifica_pub.CODQUALIFICA) " 
			+ "left join ts_gruppo on ts_gruppo.cdngruppo = dr.cdngruppo ";

	private static final String sWhere =
		" WHERE dr.NUMSTORICO=0 and dr.FLGPUBBLICATA='S' "
			+ " AND ev.FLGPUBBWEB='S' "
		    + " AND (ev.CODEVASIONE in ('DFA','DFD','DPR','DRA')) "
			+ " AND ( (de_cpi.CODPROVINCIA=(select codProvinciaSil from ts_generale where rownum=1)) " 
			+ " AND (de_cpi.DATFINEVAL >= sysdate) and length(trim(translate(de_cpi.CODCPI, ' +-.0123456789', ' '))) is null )";
	
	private static final String sWhereCM =
			" WHERE dr.NUMSTORICO=0 and dr.FLGPUBBLICATA='S' "
				+ " AND ev.FLGPUBBWEB='S' "
				+ " AND (ev.CODEVASIONE in ('MPP','MPA')) "
				+ " AND ( (de_cpi.CODPROVINCIA=(select codProvinciaSil from ts_generale where rownum=1)) " 
				+ " AND (de_cpi.DATFINEVAL >= sysdate) and length(trim(translate(de_cpi.CODCPI, ' +-.0123456789', ' '))) is null )";
	
		public String getStatement(RequestContainer requestContainer, SourceBean config) {
			SourceBean req = requestContainer.getServiceRequest();

			String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
			String prgUnita = StringUtils.getAttributeStrNotNull(req, "prgUnita");
			String flgPubblicata = StringUtils.getAttributeStrNotNull(req, "FLGPUBBLICATA");
			String datPubblicazione = StringUtils.getAttributeStrNotNull(req, "DATPUBBLICAZIONE");
			String datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(req, "DATSCADENZAPUBBLICAZIONE");
			String numRich = StringUtils.getAttributeStrNotNull(req, "NUMRICHIESTA");
			String anno = StringUtils.getAttributeStrNotNull(req, "ANNO");
			String cdnut = StringUtils.getAttributeStrNotNull(req, "CDNUT");
			String utric = StringUtils.getAttributeStrNotNull(req, "UTRIC");
			String codMansione = StringUtils.getAttributeStrNotNull(req, "CODMANSIONE");
			String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
			String codComLav = StringUtils.getAttributeStrNotNull(req, "CODCOMLAV");
			String codProvinciaLav = StringUtils.getAttributeStrNotNull(req, "CODPROVINCIALAV");
			
			String strFlagCM = StringUtils.getAttributeStrNotNull(req, "FLAGCM");
			boolean flagCM = false;
			if( strFlagCM.equals("true") ) flagCM = true;
			 
			// parametro necessario per la chiamata dalla pagina della ricerca, da non usare quando la chiamata
			// arriva dal quadrante 'di controllo'
			String prgRichiestaAz = StringUtils.getAttributeStrNotNull(req, "prgRichiestaAz");
			String InCodContratto = "";
			// String codQualifica = StringUtils.getAttributeStrNotNull(req,"CODQUALIFICA");
			String codQualifica = "";
			String strMan1 = "";
			String strMan2 = "";
			int i = 0;

			Vector contr = req.getAttributeAsVector("CODCONTRATTO");
			if (contr.size() != 0) {
				for (i = 0; i < contr.size(); i++) {
					if (InCodContratto.length() > 0) {
						InCodContratto += ",";
					}
					if (!contr.elementAt(i).equals("")) {
						InCodContratto += "'" + contr.elementAt(i) + "'";
					}
				}
			}

			Vector qual = req.getAttributeAsVector("CODQUALIFICA");
			if (qual.size() != 0) {
				for (i = 0; i < qual.size(); i++) {
					if (codQualifica.length() > 0) {
						codQualifica += ",";
					}
					if (!qual.elementAt(i).equals("")) {
						codQualifica += "'" + qual.elementAt(i) + "'";
					}
				}
			}

			// Ricerca annunci pubblici/riservati
			Vector macroCategoriaVec = (Vector)req.getAttributeAsVector("MACROCATEGORIA");
			String codMacroCategoria = "";

			if (macroCategoriaVec.size() > 0 && macroCategoriaVec.size() != 2) {
				if (macroCategoriaVec.get(0).equals("1")) {
					codMacroCategoria += "('DFD', 'DPR')";
				} else {
					codMacroCategoria += "('DFA', 'DRA')";
				}
			}
			
			StringBuffer query_totale = null;
			
			if( flagCM ) query_totale = new StringBuffer(SELECT_SQL_BASE_CM);
			else query_totale = new StringBuffer(SELECT_SQL_BASE);
			
			StringBuffer buf = new StringBuffer();

			if (cdnut.equals("")) {
				// Lato cittadino
				flgPubblicata = "INPUB";
				buf.append(" and (ev.cdnStatoRich <> 5) ");
			} else {
				// Lato operatore
				if (!prgAzienda.equals("") && !prgUnita.equals("")) {
					buf.append("AND (dr.PRGAZIENDA=" + prgAzienda);
					buf.append(" and dr.PRGUNITA=" + prgUnita + ") ");
				}
				if (!numRich.equals("")) {
					buf.append(" AND (NVL(dr.NUMRICHIESTAORIG, dr.NUMRICHIESTA) = " + numRich + ") ");
				}
				if (!anno.equals("")) {
					buf.append(" AND (dr.NUMANNO = " + anno + ") ");
				}
				if ((utric.equals("MIE")) || (utric.equals("GRUP"))) {
					buf.append(" AND ");
					if (utric.equals("MIE")) {
						buf.append(" (dr.CDNUTINS = " + cdnut + ") ");
					}
					if (utric.equals("GRUP")) {
						buf.append(
							" dr.cdnutins IN "
								+ "(SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A "
								+ "WHERE cdngruppo="
								+ "(SELECT distinct cdngruppo "
								+ "FROM TS_PROFILATURA_UTENTE B "
								+ "WHERE B.CDNUT="
								+ cdnut
								+ "))");
					}
				}
			}
			// Validi per entrambi
			if (!flgPubblicata.equals("")) {
				// macrocategoria
				if (!codMacroCategoria.equals("")) {
					buf.append(" AND (ev.CODEVASIONE in " + codMacroCategoria + ")");
				}
				buf.append(" AND ");
				if (flgPubblicata.equals("DAPUB")) {
					buf.append("(dr.DATPUBBLICAZIONE > SYSDATE) ");
				}
				if (flgPubblicata.equals("INPUB")) {
					buf.append("(dr.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <=dr.DATSCADENZAPUBBLICAZIONE) ");
				}
				if (flgPubblicata.equals("SCAD")) {
					buf.append("(SYSDATE > dr.DATSCADENZAPUBBLICAZIONE)");
				}
			}
			if (!datPubblicazione.equals("")) {
				buf.append(
					" AND (dr.DATSCADENZAPUBBLICAZIONE >= TO_DATE('" + datPubblicazione + "', 'DD/MM/YYYY')) ");
			}
			if (!datScadenzaPubblicazione.equals("")) {
				buf.append(
					" AND (dr.DATPUBBLICAZIONE <= TO_DATE('" + datScadenzaPubblicazione + "', 'DD/MM/YYYY')) ");
			}

			// Mansione
			if (!codMansione.equals("")) {
				strMan1 = codMansione.substring(2, 6);
				strMan2 = codMansione.substring(4, 6);

				query_totale.append(
					"inner join do_alternativa on (dr.prgRichiestaAz = do_alternativa.PRGRICHIESTAAZ and do_alternativa.PRGALTERNATIVA=1) ");
				buf.append("AND exists (select 1 from do_mansione man ");
				buf.append("where man.PRGRICHIESTAAZ=dr.prgRichiestaAz and man.prgAlternativa=1");

				if (strMan1.equals("0000")) {
					buf.append(" AND (substr(man.codMansione,1,2) = substr('" + codMansione + "',1,2)) ");
				} else {
					if (strMan2.equals("00")) {
						buf.append(" AND (substr(man.codMansione,1,4) = substr('" + codMansione + "',1,4)) ");
					} else {
						buf.append(" AND (man.codMansione = '" + codMansione + "') ");
					}
				}
				buf.append(") ");
				//buf.append(" AND (man.codMansione = '" + codMansione + "') ");
				//query_totale.append("left join (do_alternativa left join do_mansione man on (man.prgRichiestaAz = do_alternativa.prgRichiestaAz and man.prgAlternativa = do_alternativa.prgAlternativa)) ");
				//query_totale.append("on (dr.prgRichiestaAz = do_alternativa.PRGRICHIESTAAZ) ");
			}
			// Macroqualifica
			//if(!codQualifica.equals("")) { buf.append(" AND (dr.CODQUALIFICA='" + codQualifica + "')"); }
			if (!codQualifica.equals("")) {
				buf.append(" AND (dr.CODQUALIFICA in (" + codQualifica + "))");
			}
			// CPI -- C'è solo se la richiesta proviene dalla griglia provinciale
			if (!codCpi.equals("")) {
				buf.append(" AND (dr.CODCPI='" + codCpi + "')");
			}
			// Territorio
			if (!codComLav.equals("") || !codProvinciaLav.equals("")) {
				buf.append(" and (");
				if (!codComLav.equals("")) {
					buf.append(
						"exists (select 1 from do_comune where do_comune.prgRichiestaAz=dr.prgRichiestaAz and do_comune.CODCOM='"
							+ codComLav
							+ "')");
					if (!codProvinciaLav.equals("")) {
						buf.append(" OR ");
					}
				}
				if (!codProvinciaLav.equals("")) {
					buf.append(
						"exists (select 1 from do_provincia where do_provincia.prgRichiestaAz=dr.prgRichiestaAz and do_provincia.CODPROVINCIA='"
							+ codProvinciaLav
							+ "')");
					buf.append(" or ");
					buf.append("exists (select 1 from do_comune, de_comune ");
					buf.append(
						"where (do_comune.codCom=de_comune.codCom) and do_comune.prgRichiestaAz=dr.prgRichiestaAz ");
					buf.append("and de_comune.CODPROVINCIA='" + codProvinciaLav + "')");
				}
				buf.append(") ");
			}
			// CONTRATTI
			if (InCodContratto.length() > 0) {
				buf.append(" and (exists(");
				buf.append(
					"select 1 from DO_CONTRATTO where do_contratto.PRGRICHIESTAAZ=dr.PRGRICHIESTAAZ and do_contratto.prgAlternativa=1 and do_contratto.CODCONTRATTO IN (");
				buf.append(InCodContratto + ")");
				buf.append(")) ");
			}
			if (prgRichiestaAz.length()>0) {
				buf.append(" and dr.prgRichiestaAz = " + prgRichiestaAz);
			}
			buf.append(" ORDER BY dr.CODCPI, dataPubSort desc, NUMANNO desc, nvl(dr.numrichiestaorig, dr.numrichiesta) desc ");
			
			if( flagCM )
				query_totale.append(sWhereCM + buf.toString());
			else query_totale.append(sWhere + buf.toString());
			
			return query_totale.toString();

		}

	}

