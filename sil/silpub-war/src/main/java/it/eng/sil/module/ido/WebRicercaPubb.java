package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import java.util.Vector;

/**
 * Modifica Andrea 11/12/2004
 * aggiunto filtro per mansione-like (mansione, qualifica ISTAT e settore aziendale)
 */
public class WebRicercaPubb implements IDynamicStatementProvider
{

  private static final String SELECT_SQL_BASE =  "SELECT "
		  	 + "(case "
			 + "when paz.strtarga is not null then (nvl(dr.NUMRICHIESTAORIG, dr.NUMRICHIESTA) || '/' || dr.NUMANNO || '/' || paz.strtarga) "
			 + " else to_char(dr.NUMRICHIESTA) || '/' || to_char(dr.NUMANNO) "
			 + "end) as NUMRICH, "
             + "to_char(dr.NUMPROFRICHIESTI) as NUMPROFRICHIESTI, "
             + "dr.PRGRICHIESTAAZ, "
             + "dr.PRGAZIENDA, dr.PRGUNITA "
             + ",(PG_IDO.WEBSTRMANSIONI(dr.PRGRICHIESTAAZ)) AS MANSIONE "
             + ",(PG_IDO.WEBSTRCONTRATTI(dr.PRGRICHIESTAAZ)) AS RAPPORTO "
             + ",(PG_IDO.WEBSTRTERRITORIO(dr.PRGRICHIESTAAZ)) AS TERRITORIO "
             + ", (de_qualifica_pub.STRDESCRIZIONE) AS DESCQUALIFICA "
             + ", ev.CODEVASIONE " 
             + ", dr.codCpi "
             + ", dr.DATPUBBLICAZIONE "
			 + "FROM do_richiesta_az dr "
             + "inner join DO_EVASIONE ev on (dr.PRGRICHIESTAAZ=ev.PRGRICHIESTAAZ) "
             + "inner join DE_CPI on (dr.CODCPI=de_cpi.CODCPI) "
             + "left outer join de_qualifica_pub on (dr.CODQUALIFICA=de_qualifica_pub.CODQUALIFICA) "
  			 + "left outer join DE_PROVINCIA paz ON (dr.codprovinciaprov = paz.codprovincia) ";
  
	private static final String SELECT_SQL_BASE_CM =  "SELECT " 
			 + "(case "
			 + "when paz.strtarga is not null then (nvl(dr.NUMRICHIESTAORIG, dr.NUMRICHIESTA) || '/' || dr.NUMANNO || '/' || paz.strtarga) "
			 + " else to_char(dr.NUMRICHIESTA) || '/' || to_char(dr.NUMANNO) "
			 + "end) as NUMRICH, "
			 + "to_char(dr.NUMPROFRICHIESTI) as NUMPROFRICHIESTI, "
			 + "dr.PRGRICHIESTAAZ, "
			 + "dr.PRGAZIENDA, dr.PRGUNITA "
			 + ",(PG_IDO.WEBSTRMANSIONI(dr.PRGRICHIESTAAZ)) AS MANSIONE "
			 + ",(PG_IDO.WEBSTRCONTRATTI(dr.PRGRICHIESTAAZ)) AS RAPPORTO "
			 + ",(PG_IDO.WEBSTRTERRITORIO(dr.PRGRICHIESTAAZ)) AS TERRITORIO "
			 + ", (de_qualifica_pub.STRDESCRIZIONE) AS DESCQUALIFICA "
			 + ", ev.CODEVASIONE " 
			 + ", dr.codCpi "
			 + ", dr.DATPUBBLICAZIONE, "
			 + "case "
			 + "when ev.CODEVASIONE in ('MPP','MPA') "
			 + "then( "
			 + "case "
			 + "when dr.CODMONOCMCATPUBB = 'D' "
			 + "then 'Disabili'"
			 + "when dr.CODMONOCMCATPUBB = 'A' "
			 + "then 'Ex. Art. 18' "
			 + "when dr.CODMONOCMCATPUBB = 'E' "
			 + "then 'Entrambi' "
			 + "else '' "
			 + "end) " 
			 + "else '' " 
			 + "end CODMONOCMCATPUBB "
			 + "FROM do_richiesta_az dr "
			 + "inner join DO_EVASIONE ev on (dr.PRGRICHIESTAAZ=ev.PRGRICHIESTAAZ) "
			 + "inner join DE_CPI on (dr.CODCPI=de_cpi.CODCPI) "
			 + "left outer join de_qualifica_pub on (dr.CODQUALIFICA=de_qualifica_pub.CODQUALIFICA) "
			 + "left outer join DE_PROVINCIA paz ON (dr.codprovinciaprov = paz.codprovincia) ";
  
  private static final String sWhere = " WHERE dr.NUMSTORICO=0 and dr.FLGPUBBLICATA='S' "
  			 + " AND ( (de_cpi.CODPROVINCIA=(select codProvinciaSil from ts_generale where rownum=1)) " 
  			 + "and (de_cpi.DATFINEVAL >= sysdate) and length(trim(translate(de_cpi.CODCPI, ' +-.0123456789', ' '))) is null ) "
  			 + " and ev.FLGPUBBWEB = 'S' "
			 + " and ev.codevasione in ('DFA','DFD','DPR','DRA')";
			 
	private static final String sWhereCM = " WHERE dr.NUMSTORICO=0 and dr.FLGPUBBLICATA='S' "
			 + " AND ( (de_cpi.CODPROVINCIA=(select codProvinciaSil from ts_generale where rownum=1)) " 
			 + "and (de_cpi.DATFINEVAL >= sysdate) and length(trim(translate(de_cpi.CODCPI, ' +-.0123456789', ' '))) is null ) "
			 + " and ev.FLGPUBBWEB = 'S' "
			 + " AND ev.codevasione in('MPP','MPA') "  ;
			 //***************************************************************
  			 
                              

  public String getStatement(RequestContainer requestContainer, SourceBean config)
  {
	String codMonoCMcatPubb = null;
	
    SourceBean req = requestContainer.getServiceRequest();

    String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
    String prgUnita = StringUtils.getAttributeStrNotNull(req, "prgUnita");  
    String flgPubblicata = StringUtils.getAttributeStrNotNull(req, "FLGPUBBLICATA");
    String datPubblicazione =StringUtils.getAttributeStrNotNull(req, "DATPUBBLICAZIONE");
    String datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(req, "DATSCADENZAPUBBLICAZIONE");
    String numRich = StringUtils.getAttributeStrNotNull(req, "NUMRICHIESTA");
    String anno = StringUtils.getAttributeStrNotNull(req,"ANNO");
    String cdnut = StringUtils.getAttributeStrNotNull(req,"CDNUT");
    String utric = StringUtils.getAttributeStrNotNull(req,"UTRIC");
    String codMansione = StringUtils.getAttributeStrNotNull(req,"CODMANSIONE");
	String codCpi = StringUtils.getAttributeStrNotNull(req,"CODCPI");
    String codComLav = StringUtils.getAttributeStrNotNull(req,"CODCOMLAV");
    String codProvinciaLav = StringUtils.getAttributeStrNotNull(req,"CODPROVINCIALAV");
	String keyWord = StringUtils.getAttributeStrNotNull(req,"keyWord");
	
	String strCollocamentoMirato = StringUtils.getAttributeStrNotNull(req,"flagCM");
	boolean flagCM = strCollocamentoMirato.equals("true")?true:false;
	
	if(flagCM) codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(req,"codMonoCMcatPubb");
	
    String InCodContratto = "";
    String codQualifica = "";
    String strMan1 = "";
    String strMan2 = "";
	int i=0;
    
    // valorizzazione di codContratto
    Vector contr = req.getAttributeAsVector("CODCONTRATTO");
    if(contr.size()!=0) {
      for(i=0; i<contr.size(); i++) {
        if(InCodContratto.length()>0) 
        	InCodContratto += ","; 
        if(!contr.elementAt(i).equals("")) { 
        	InCodContratto += "'" + contr.elementAt(i) + "'"; 
        }
      }
    }
    // valorizzazione di codQualifica
	Vector qual = req.getAttributeAsVector("CODQUALIFICA");
	if(qual.size()!=0) {
	  for(i=0; i<qual.size(); i++) {
		if(codQualifica.length()>0) 			
			codQualifica += ","; 
		if(!qual.elementAt(i).equals("")) { 
			codQualifica += "'" + qual.elementAt(i) + "'"; 
		}
	  }
	}

	// Ricerca annunci pubblici/riservati
	Vector macroCategoriaVec = (Vector) req.getAttributeAsVector("MACROCATEGORIA");
	String codMacroCategoria = "";
	// Alessandro aggiunte MPP e MPA per il CM 03/03/2008
	if (macroCategoriaVec.size() > 0 && macroCategoriaVec.size()!=2){
		if (macroCategoriaVec.get(0).equals("1")) {
			codMacroCategoria += "('DFD', 'DPR', 'MPP' )";
		} else {
			codMacroCategoria += "('DFA', 'DRA', 'MPA' )";
		}
	}

	StringBuffer query_totale = null;
	    
    if( !flagCM ) query_totale = new StringBuffer(SELECT_SQL_BASE);
	else query_totale = new StringBuffer(SELECT_SQL_BASE_CM);
    StringBuffer buf = new StringBuffer();
    
    if(cdnut.equals("")) {
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
        if (!anno.equals("")) { buf.append(" AND (dr.NUMANNO = " + anno +") "); }
        if ((utric.equals("MIE")) || (utric.equals("GRUP")) ) {
            buf.append(" AND ");
            if (utric.equals("MIE")) { buf.append(" (dr.CDNUTINS = " + cdnut +") "); }
            if (utric.equals("GRUP")) {
              buf.append(" dr.cdnutins IN (  " +
                         "    SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A " +
                         "    WHERE cdngruppo=" +
                         "       (SELECT distinct cdngruppo " +
                         "        FROM TS_PROFILATURA_UTENTE B " +
                         "        WHERE B.CDNUT=" + cdnut + ")  " +
                         " )");
            }
        }
    }
    // Validi per entrambi
    if (!flgPubblicata.equals("")) {
    	// macrocategoria
    	if (!codMacroCategoria.equals("")){
			buf.append(" AND (ev.CODEVASIONE in " + codMacroCategoria + ")");
    	}    	
        buf.append(" AND ");
        if (flgPubblicata.equals("DAPUB")){
          buf.append("(dr.DATPUBBLICAZIONE > SYSDATE) ");
        }
        if (flgPubblicata.equals("INPUB")){
          buf.append("(dr.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <=dr.DATSCADENZAPUBBLICAZIONE) ");
        }
        if (flgPubblicata.equals("SCAD")){
          buf.append("(SYSDATE > dr.DATSCADENZAPUBBLICAZIONE)");
        }
    }
    if(!datPubblicazione.equals("")) { buf.append(" AND (dr.DATSCADENZAPUBBLICAZIONE >= TO_DATE('" + datPubblicazione + "', 'DD/MM/YYYY')) "); }
    if(!datScadenzaPubblicazione.equals("")) { buf.append(" AND (dr.DATPUBBLICAZIONE <= TO_DATE('" + datScadenzaPubblicazione + "', 'DD/MM/YYYY')) "); }

    // Mansione
    if(!codMansione.equals("")) {
        strMan1 = codMansione.substring(2,6);
        strMan2 = codMansione.substring(4,6);
        
        query_totale.append("inner join do_alternativa on (dr.prgRichiestaAz = do_alternativa.PRGRICHIESTAAZ and do_alternativa.PRGALTERNATIVA=1) ");
        buf.append("AND exists (select 1 from do_mansione man ");
        buf.append("where man.PRGRICHIESTAAZ=dr.prgRichiestaAz and man.prgAlternativa=1");
        
        if(strMan1.equals("0000")) {
          buf.append(" AND (substr(man.codMansione,1,2) = substr('" + codMansione + "',1,2)) ");
        } else {
            if(strMan2.equals("00")) {
                buf.append(" AND (substr(man.codMansione,1,4) = substr('" + codMansione + "',1,4)) ");
            } else {
                buf.append(" AND (man.codMansione = '" + codMansione + "') ");
            }
        }
        buf.append(") ");        
    }
    // Macroqualifica    
	if(!codQualifica.equals("")) { buf.append(" AND (dr.CODQUALIFICA in (" + codQualifica + "))"); }
	// CPI -- C'è solo se la richiesta proviene dalla griglia provinciale
	 if(!codCpi.equals("")) { buf.append(" AND (dr.CODCPI='" + codCpi + "')"); }
    // Territorio
    if(!codComLav.equals("") || !codProvinciaLav.equals("")) {
      buf.append(" and (");
      if(!codComLav.equals("")) {
        buf.append("exists (select 1 from do_comune where do_comune.prgRichiestaAz=dr.prgRichiestaAz and do_comune.CODCOM='" + codComLav +"')");
        // Savino 27/05/05
		buf.append("or exists (select 1 from do_richiesta_az ric ");
		buf.append(" inner join an_unita_azienda un on (ric.PRGAZIENDA=un.PRGAZIENDA and ric.PRGUNITA=un.PRGUNITA)");
		buf.append(" inner join de_comune on (un.CODCOM=de_comune.CODCOM)");
		buf.append(" inner join de_provincia on (de_comune.CODPROVINCIA=de_provincia.CODPROVINCIA)");
		buf.append(" where ric.PRGRICHIESTAAZ = dr.PrgRichiestaAz and de_comune.CODCOM='"+codComLav + "')");			
        if(!codProvinciaLav.equals("")) { buf.append(" OR "); }
      }
      if(!codProvinciaLav.equals("")) {
        buf.append("exists (select 1 from do_provincia where do_provincia.prgRichiestaAz=dr.prgRichiestaAz and do_provincia.CODPROVINCIA='" + codProvinciaLav +"')");
        buf.append(" or ");
        buf.append("exists (select 1 from do_comune, de_comune ");
        buf.append("where (do_comune.codCom=de_comune.codCom) and do_comune.prgRichiestaAz=dr.prgRichiestaAz ");
        buf.append("and de_comune.CODPROVINCIA='" + codProvinciaLav +"') ");
        // Savino 27/05/05
        buf.append("or exists (select 1 from do_richiesta_az ric ");
        buf.append(" inner join an_unita_azienda un on (ric.PRGAZIENDA=un.PRGAZIENDA and ric.PRGUNITA=un.PRGUNITA)");
		buf.append(" inner join de_comune on (un.CODCOM=de_comune.CODCOM)");
		buf.append(" inner join de_provincia on (de_comune.CODPROVINCIA=de_provincia.CODPROVINCIA)");
		buf.append(" where ric.PRGRICHIESTAAZ = dr.PrgRichiestaAz and de_comune.CODPROVINCIA='"+codProvinciaLav + "')");
        
      }
      buf.append(") ");
    }
    // CONTRATTI
    if(InCodContratto.length()>0) { 
      buf.append(" and (exists(");
      buf.append("select 1 from DO_CONTRATTO where do_contratto.PRGRICHIESTAAZ=dr.PRGRICHIESTAAZ and do_contratto.prgAlternativa=1 and do_contratto.CODCONTRATTO IN (");
      buf.append(InCodContratto + ")");
      buf.append(")) ");
    }
    // ricerca per mansione, qualifica ISTAT e settore aziendale
    if (!keyWord.equals("")) {
		keyWord = keyWord.toUpperCase();
		buf.append("      and (upper(dr.strMansionePubb) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.STRLUOGOLAVORO) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.STRFORMAZIONEPUBB) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.TXTCONDCONTRATTUALE) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.STRCONOSCENZEPUBB) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.STRNOTEORARIOPUBB) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.STRDATIAZIENDAPUBB) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.TXTFIGURAPROFESSIONALE) like '%"+keyWord+"%' ");
		buf.append("      or upper(dr.TXTCARATTERISTFIGPROF) like '%"+keyWord+"%') ");
		
    }
    
    //ricerca per Categoria CM
    if( flagCM ){
		
		
		if( codMonoCMcatPubb.equals("D")||codMonoCMcatPubb.equals("A") )
			if( buf.length()==0 )
				buf.append(" WHERE dr.CODMONOCMCATPUBB = '"+ codMonoCMcatPubb +"'");
			else
				buf.append(" AND dr.CODMONOCMCATPUBB = '"+ codMonoCMcatPubb +"'");
    }
    
    buf.append(" ORDER BY dr.CODCPI, DATPUBBLICAZIONE desc, dr.numanno desc, nvl(dr.numrichiestaorig, dr.numrichiesta) desc");
    
    if(!flagCM) query_totale.append(sWhere);
    else query_totale.append(sWhereCM);
    
    query_totale.append(buf.toString());
    
    return query_totale.toString();

  }
  
}