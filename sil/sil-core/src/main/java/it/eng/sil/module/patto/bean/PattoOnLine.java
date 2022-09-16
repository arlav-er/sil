package it.eng.sil.module.patto.bean;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.coop.webservices.xsd.pattoonline.ObjectFactory;


public class PattoOnLine {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PattoOnLine.class.getName());

	private static final BigDecimal userSP = new BigDecimal("190");
	private static final BigDecimal userBatch = new BigDecimal("100");
	
	public static final String ACCETTATO = "A";
	public static final String DA_ACCETTARE = "D";
	public static final String SCADENZA_TERMINI = "S";
	
	
	private static final String CONFIG_ORE_SCADENZA=" SELECT "+
			   " nvl((   SELECT ts_config_loc.num "+
			    "        FROM ts_config_loc "+
			    "            INNER JOIN de_tipo_config ON(ts_config_loc.codtipoconfig = de_tipo_config.codtipoconfig)"+
			     "       WHERE   ts_config_loc.strcodrif =( "+
			    "                SELECT ts_generale.codprovinciasil "+
			    "                FROM  ts_generale "+
			      "              WHERE prggenerale = 1 "+
			   "            ) "+
			     "           AND de_tipo_config.codtipoconfig = 'PTOL_ORE' "+
			    "            AND trunc(ts_config_loc.datinizioval) <= trunc(sysdate) "+
			     "           AND trunc(ts_config_loc.datfineval) > trunc(sysdate) "+
			     "  		), '0') AS numValoreConfig, "+
			    "	    (  SELECT  ts_generale.codprovinciasil "+
			    "	        FROM  ts_generale"+
			    "	        WHERE   prggenerale = 1 "+
			    "	    ) AS codprovinciasil "+
			   "  FROM dual";
	
	private static final String GET_DATI_PATTO_RICHIESTA =
					" SELECT lav.strcodicefiscale   codiceFiscale,                  "+
					"	               to_char(patto.datStipula, 'yyyy-mm-dd hh24:mi:ss')  dataPatto,                    "+
					"	               to_char(patto.DTMINVIOPORTALE, 'yyyy-mm-dd hh24:mi:ss')  dataInvioPortale,                    "+
					"			       patto.strcodabiportale codServiziAmministrativi,      "+
					"			       to_char(am_doc.numprotocollo)   numProtocollo,          "+       
					"			       to_char(am_doc.numannoprot)     annoProtocollo,       "+         
					"			       com.codprovincia       codProvinciaProv             "+
					"			  FROM am_patto_lavoratore patto                             "+
					"			 INNER JOIN an_lavoratore lav                                "+
					"			    ON patto.cdnlavoratore = lav.cdnlavoratore               "+
					"			 INNER JOIN am_documento_coll doc_coll                       "+
					"			    ON (to_number(doc_coll.strchiavetabella) = patto.prgpattolavoratore)   "+
					"			 INNER JOIN am_documento am_doc                              "+
					"			    ON (am_doc.prgdocumento = doc_coll.prgdocumento AND      "+
					"			       am_doc.cdnlavoratore = patto.cdnlavoratore)           "+
					"			  INNER JOIN de_cpi ON (patto.codcpi = de_cpi.codcpi)        "+
					"			  INNER JOIN de_comune com on (com.codcom = de_cpi.codcom)  "+ 
					"			 WHERE patto.prgpattolavoratore = ?	";	
	
	private static final String GET_BLOB_DOCUMENTO =" SELECT  am_documento_blob.blbfile "+
			 " FROM am_patto_lavoratore patto   "+                                     
			" INNER JOIN am_documento_coll doc_coll   "+                    
			"    ON (to_number(doc_coll.strchiavetabella) = patto.prgpattolavoratore)   "+
			" INNER JOIN am_documento am_doc        "+                      
			"    ON (am_doc.prgdocumento = doc_coll.prgdocumento AND    "+  
			"       am_doc.cdnlavoratore = patto.cdnlavoratore)     "+      
            "  INNER JOIN am_documento_blob ON am_doc.prgdocumento = am_documento_blob.prgdocumento "+
			" WHERE am_doc.prgdocumento = ? AND am_doc.codtipodocumento IN ('PT297', 'ACLA')    "+     
			"  AND am_doc.codstatoatto = 'PR'  ";
	
	private static final String GET_PTONLINE_ACCETTAZIONE=
	    " SELECT patto.prgpattolavoratore, patto.numklopattolavoratore "+
		"		  FROM am_patto_lavoratore patto    "+                         
		"		 INNER JOIN an_lavoratore lav       "+                         
		"		    ON patto.cdnlavoratore = lav.cdnlavoratore  "+             
		"		 INNER JOIN am_documento_coll doc_coll    "+                   
		"		    ON (to_number(doc_coll.strchiavetabella) = patto.prgpattolavoratore)   "+
		"		 INNER JOIN am_documento am_doc             "+                 
		"		    ON (am_doc.prgdocumento = doc_coll.prgdocumento AND   "+   
		"		       am_doc.cdnlavoratore = patto.cdnlavoratore)       "+   
		" 			INNER JOIN de_cpi ON (patto.codcpi = de_cpi.codcpi)       "+
        " 			INNER JOIN de_comune com on (com.codcom = de_cpi.codcom)       "+
		"		 WHERE  am_doc.codtipodocumento IN ('PT297', 'ACLA')    "+     
        "         AND patto.datfine is null " +
		"		  AND patto.codmonoaccettazione = 'D' "+
		" 		  AND upper(patto.strcodabiportale) = upper(?) " +
	    "         AND upper(lav.strcodicefiscale) = upper(?)"+
		"         AND am_doc.codstatoatto = 'PR'       "+
		"		  AND am_doc.numannoprot = to_number(?) "+ 
		"		  AND am_doc.numprotocollo = to_number(?) " +
		"		  AND upper(com.codprovincia) = upper(?) ";
	
	private static final String UPDATE_PTONLINE_ACCETTAZIONE=" UPDATE am_patto_lavoratore "+
			" SET DTMACCETTAZIONE = to_date(?,'dd/mm/yyyy hh24:mi:ss'),  CODMONOACCETTAZIONE = ?, "+
			" STRTIPOACCETTAZIONE = ? , NUMKLOPATTOLAVORATORE = ? + 1, CDNUTMOD = ?, DTMMOD = sysdate ";
	
	private static final String UPDATE_PTONLINE_SCADENZA= ", DATFINE = DATSTIPULA, CODMOTIVOFINEATTO='DEMAC' ";
	
	private static final String UPDATE_PTONLINE_ACCETTAZIONE_WHERE=" WHERE  prgpattolavoratore = ?";
	
	public int aggiornaAccettazionePatto( Connection connection, it.eng.sil.coop.webservices.pattoonline.PattoAccettatoType pattoAccettato,
			String codMonoAccettazione) throws EMFInternalError, SQLException {
		
		it.eng.sil.coop.webservices.pattoonline.PattoType patto = pattoAccettato.getPatto();
		it.eng.sil.coop.webservices.pattoonline.AccettazionePattoType accettazione = pattoAccettato.getAccettazionePatto();
		
		return doUpdate(connection, userSP, patto.getCodiceFiscale(),patto.getCodServiziAmministrativi(),
				patto.getAnnoProtocollo().toString(), patto.getNumProtocollo(), patto.getCodProvinciaProv(),
				getStringDate(accettazione.getDtmAccettazione()),accettazione.getTipoAccettazione().getValue(), codMonoAccettazione);
	}
	
	public int aggiornaAccettazionePatto( Connection connection, it.eng.sil.coop.webservices.pattoonlinenew.PattoAccettatoType pattoAccettato,
			String codMonoAccettazione) throws EMFInternalError, SQLException {
		
		it.eng.sil.coop.webservices.pattoonlinenew.PattoType patto = pattoAccettato.getPatto();
		it.eng.sil.coop.webservices.pattoonlinenew.AccettazionePattoType accettazione = pattoAccettato.getAccettazionePatto();
		
		return doUpdate(connection, userSP, patto.getCodiceFiscale(),patto.getCodServiziAmministrativi(),
				patto.getAnnoProtocollo()!=null? patto.getAnnoProtocollo().toString() : null, patto.getNumProtocollo(), patto.getCodProvinciaProv(),
						accettazione!=null &&accettazione.getDtmAccettazione()!=null?getStringDate(accettazione.getDtmAccettazione()):null,
							accettazione!=null &&accettazione.getTipoAccettazione()!=null?accettazione.getTipoAccettazione().getValue():null, 
					codMonoAccettazione);
	}

	private String getStringDate(
			Calendar cal) {
		String formatted = null;
		if(cal!=null){
			SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		    formatted = format1.format(cal.getTime());
		}
		return formatted;
	}

	public int aggiornaAccettazionePatto(Connection connection, it.eng.myportal.ws.pattoonline.PattoPortaleType pattoPortale, String codMonoAccettazione ) throws EMFInternalError, SQLException {
		it.eng.myportal.ws.pattoonline.PattoType patto = pattoPortale.getPatto();
		it.eng.myportal.ws.pattoonline.AccettazionePattoType accettazione = pattoPortale.getAccettazionePatto();
		if(!codMonoAccettazione.equalsIgnoreCase(SCADENZA_TERMINI)){
			return doUpdate( connection, userBatch, patto.getCodiceFiscale(),patto.getCodServiziAmministrativi(),
					patto.getAnnoProtocollo().toString(), patto.getNumProtocollo(), patto.getCodProvinciaProv(),
					getStringDate(accettazione.getDtmAccettazione()), accettazione.getTipoAccettazione().getValue(), codMonoAccettazione);
		}else{
			return doUpdate( connection, userBatch, patto.getCodiceFiscale(),patto.getCodServiziAmministrativi(),
					patto.getAnnoProtocollo().toString(), patto.getNumProtocollo(),patto.getCodProvinciaProv(), null, null, codMonoAccettazione);
		}
	}

	private int doUpdate(Connection connection, BigDecimal cdnParUtente, String cfLav, 
			String codServiziAmministrativi,String annoProt, String numProt, String codProvincia,
			String dtmAccettazione, String tipoAccettazione, String codMonoAccettazione)   throws EMFInternalError, SQLException {

		ResultSet dataResult = null;
		//trova il patto da aggiornare
		BigDecimal numKLo = null;
		BigDecimal prgPatto = null;
		PreparedStatement preparedStatement = connection.prepareStatement(GET_PTONLINE_ACCETTAZIONE);
		preparedStatement.setString(1, codServiziAmministrativi);
		preparedStatement.setString(2, cfLav); 
		preparedStatement.setString(3, annoProt);
		preparedStatement.setString(4, numProt);
		preparedStatement.setString(5, codProvincia);
		_logger.debug("Cerca il patto da aggiornare. Query: " + GET_PTONLINE_ACCETTAZIONE);
		dataResult = preparedStatement.executeQuery();
		if (dataResult.next()) {
			numKLo = dataResult.getBigDecimal("numklopattolavoratore");
			prgPatto = dataResult.getBigDecimal("prgpattolavoratore");
		}else {
			return 40;
		}
		
		//aggiorna dati accettazione patto 
 		String queryUpdate = UPDATE_PTONLINE_ACCETTAZIONE;
		if(codMonoAccettazione.equals(SCADENZA_TERMINI)){
			queryUpdate += UPDATE_PTONLINE_SCADENZA;
		}
		queryUpdate += UPDATE_PTONLINE_ACCETTAZIONE_WHERE;

		PreparedStatement preparedStatementUpdate = connection.prepareStatement(queryUpdate);
		preparedStatementUpdate.setString(1, dtmAccettazione);  
		preparedStatementUpdate.setString(2, codMonoAccettazione);
		preparedStatementUpdate.setString(3, tipoAccettazione);
		preparedStatementUpdate.setBigDecimal(4, numKLo);
		preparedStatementUpdate.setBigDecimal(5, cdnParUtente);
		preparedStatementUpdate.setBigDecimal(6, prgPatto);
		
		dataResult = preparedStatementUpdate.executeQuery();
		if(dataResult.next()){
			return 0;
		}else {
			return 50;
		}
		
		
	}

	public ResultSet getDatiPattoPerRichiesta(Connection connection, BigDecimal prgPattoLavoratore) throws SQLException{
		PreparedStatement selectStatement = connection.prepareStatement(GET_DATI_PATTO_RICHIESTA);
		selectStatement.setBigDecimal(1, prgPattoLavoratore);
		
		return selectStatement.executeQuery();
	}
	
	public int getConfigOreScadenzaAccettazione(Connection connection) throws SQLException{
		int oreConfig = -1;
		ResultSet dataResult = null;
		PreparedStatement configStatement = connection.prepareStatement(CONFIG_ORE_SCADENZA);
		dataResult = configStatement.executeQuery();
		if(dataResult.next()){
			oreConfig = dataResult.getBigDecimal("numValoreConfig").intValue();
		}
		return oreConfig;
	}
	
	public byte[]  getBlobDocumento(Connection connection, BigDecimal prgDocumento) throws SQLException{
		ResultSet dataResult = null;
		byte[] blobAsBytes = null;
		PreparedStatement blobStatement = connection.prepareStatement(GET_BLOB_DOCUMENTO);
		blobStatement.setBigDecimal(1, prgDocumento);
		dataResult = blobStatement.executeQuery();
		if(dataResult.next()){
			Blob blob = dataResult.getBlob("blbfile");
			int blobLength = (int) blob.length();  
			blobAsBytes = blob.getBytes(1, blobLength);
			//release the blob and free up memory. (since JDBC 4.0)
			blob.free();
		}
		return blobAsBytes;
	}
	
    public  String requestToXML(it.eng.sil.coop.webservices.pattoonline.PattoAccettatoType richiesta) throws ParseException, DatatypeConfigurationException {
        String xmlString = "";
        try {
        	
        	it.eng.sil.coop.webservices.xsd.pattoonline.PattoAccettatoType pattoTemp = new it.eng.sil.coop.webservices.xsd.pattoonline.PattoAccettatoType();
        	it.eng.sil.coop.webservices.xsd.pattoonline.PattoType pt = new it.eng.sil.coop.webservices.xsd.pattoonline.PattoType();
        	pt.setAnnoProtocollo(richiesta.getPatto().getAnnoProtocollo());
        	pt.setNumProtocollo(richiesta.getPatto().getNumProtocollo());
        	pt.setCodiceFiscale(richiesta.getPatto().getCodiceFiscale());
        	pt.setCodProvinciaProv(richiesta.getPatto().getCodProvinciaProv());
        	pt.setCodServiziAmministrativi(richiesta.getPatto().getCodServiziAmministrativi());
        	
        	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			String dateDid = format1.format(richiesta.getPatto().getDataPatto());
			
 
        	pt.setDataPatto(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateDid));
        	pattoTemp.setPatto(pt);
        	it.eng.sil.coop.webservices.xsd.pattoonline.AccettazionePattoType acc = new it.eng.sil.coop.webservices.xsd.pattoonline.AccettazionePattoType();
        	acc.setTipoAccettazione(richiesta.getAccettazionePatto().getTipoAccettazione().getValue());
        	SimpleDateFormat format12 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        	Date accDate= richiesta.getAccettazionePatto().getDtmAccettazione().getTime();
        	String dddd = format12.format(accDate);
        	acc.setDtmAccettazione(DatatypeFactory.newInstance().newXMLGregorianCalendar(dddd));
        	pattoTemp.setAccettazionePatto(acc);
        	it.eng.sil.coop.webservices.xsd.pattoonline.ObjectFactory obj = new ObjectFactory();
        	JAXBElement<it.eng.sil.coop.webservices.xsd.pattoonline.PattoAccettatoType> request = obj.createRequestAggiornaPatto(pattoTemp);
        	
        	 
            JAXBContext context = JAXBContext.newInstance(it.eng.sil.coop.webservices.xsd.pattoonline.ObjectFactory.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(request, sw);
            xmlString = sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlString;
    }
    
    public  String requestToXML(it.eng.sil.coop.webservices.pattoonlinenew.PattoAccettatoType richiesta) throws ParseException, DatatypeConfigurationException {
        String xmlString = "";
        try {
        	
        	it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoAccettatoType pattoTemp = new it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoAccettatoType();
        	it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoType pt = new it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoType();
        	pt.setAnnoProtocollo(richiesta.getPatto().getAnnoProtocollo());
        	pt.setNumProtocollo(richiesta.getPatto().getNumProtocollo());
        	pt.setCodiceFiscale(richiesta.getPatto().getCodiceFiscale());
        	pt.setCodProvinciaProv(richiesta.getPatto().getCodProvinciaProv());
        	pt.setCodServiziAmministrativi(richiesta.getPatto().getCodServiziAmministrativi());
        	
        	if(richiesta.getPatto().getDataPatto()!=null)
        	{
        		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            	Date pattoDate= richiesta.getPatto().getDataPatto().getTime();
    			String dataPatto = format1.format(pattoDate);
            	pt.setDataPatto(DatatypeFactory.newInstance().newXMLGregorianCalendar(dataPatto));
        	}
        	
        	pattoTemp.setPatto(pt);
        	it.eng.sil.coop.webservices.xsd.pattoonlinenew.AccettazionePattoType acc = new it.eng.sil.coop.webservices.xsd.pattoonlinenew.AccettazionePattoType();
        	if(richiesta.getAccettazionePatto()!=null){
        		if(richiesta.getAccettazionePatto().getTipoAccettazione()!=null)
        			acc.setTipoAccettazione(richiesta.getAccettazionePatto().getTipoAccettazione().getValue());
            	SimpleDateFormat format12 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            	if(richiesta.getAccettazionePatto().getDtmAccettazione()!=null)
            	{
            		Date accDate= richiesta.getAccettazionePatto().getDtmAccettazione().getTime();
                	String dataAccett = format12.format(accDate);
                	acc.setDtmAccettazione(DatatypeFactory.newInstance().newXMLGregorianCalendar(dataAccett));
            	}
            	pattoTemp.setAccettazionePatto(acc);
        	}
        	
//        	it.eng.sil.coop.webservices.xsd.pattoonlinenew.ObjectFactory obj = new it.eng.sil.coop.webservices.xsd.pattoonlinenew.ObjectFactory();
//        	JAXBElement<it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoAccettatoType> request = obj.createPattoAccettatoType();
//        	
 
            JAXBContext context = JAXBContext.newInstance(it.eng.sil.coop.webservices.xsd.pattoonlinenew.PattoAccettatoType.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(pattoTemp, sw);
            xmlString = sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlString;
    }
      
	public String requestToXML(it.eng.myportal.ws.pattoonline.PattoType richiesta) {
		String xmlString = "";
		try {
			JAXBContext context = JAXBContext.newInstance(it.eng.myportal.ws.pattoonline.PattoType.class);
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

			StringWriter sw = new StringWriter();
			m.marshal(richiesta, sw);
			xmlString = sw.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return xmlString;
	}
	
	public String responseToXML(it.eng.myportal.ws.pattoonline.PattoPortaleType res) throws DatatypeConfigurationException, ParseException, Exception {
		String xml = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(it.eng.myportal.ws.pattoonline.PattoPortaleType.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// format the XML output
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(res, sw);
			xml = sw.toString();
		} catch (JAXBException e) {
			_logger.error("Errore creazione output XML", e);
			throw new Exception(e);
		}
		return xml;			
	}

}
