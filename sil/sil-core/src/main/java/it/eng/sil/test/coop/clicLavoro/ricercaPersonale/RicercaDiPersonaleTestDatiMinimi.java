package it.eng.sil.test.coop.clicLavoro.ricercaPersonale;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.junit.BaseTestCase;
import junit.framework.TestCase;

/**
 * Il test inserisce a DB i dati minimi per la creazione di un xml e ne tenta la costruzione.
 * 
 * @author rodi
 *
 */
public class RicercaDiPersonaleTestDatiMinimi extends TestCase {

	static Logger _logger = Logger.getLogger(RicercaDiPersonaleTestDatiMinimi.class.getName());

	private Random randomGenerator = new Random(System.currentTimeMillis());

	DateFormat simple = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void runTest() {
		System.setProperty("CONTEXT_NAME", "sil");
		BaseTestCase.getInstance();

		testRichiestaMinima();
	}

	/**
	 * Costruisce una nuova richiesta di personale con le informazioni minime necessarie affinché possa essere generato
	 * un xml da spedire a clic lavoro. Il prgRichiestaAz viene generato a partire dalla sequence
	 * 
	 */

	public void testRichiestaMinima() {
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("SELECT_DO_RICHIESTA_AZ_SEQUENCE", null, "SELECT",
				Values.DB_SIL_DATI);
		BigDecimal prgRichiestaAz = (BigDecimal) res.getAttribute("ROW.prgRichiestaAz");

		res = (SourceBean) QueryExecutor.executeQuery("RP_GET_CPI", null, "SELECT", Values.DB_SIL_DATI);
		String codCpi = (String) res.getAttribute("ROW.CODCPI");

		res = (SourceBean) QueryExecutor.executeQuery("RP_SELECT_RANDOM_PRGAZIENDA_PRGUNITA", null, "SELECT",
				Values.DB_SIL_DATI);
		BigDecimal prgAzienda = (BigDecimal) res.getAttribute("ROW.PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) res.getAttribute("ROW.PRGUNITA");

		DoRichiestaAz doRichiestaAz = new DoRichiestaAz();
		doRichiestaAz.setPrgRichiestaAz(prgRichiestaAz);
		doRichiestaAz.setPrgAzienda(prgAzienda);
		doRichiestaAz.setPrgUnita(prgUnita);
		doRichiestaAz.setCodCpi(codCpi);

		BigDecimal numAnno = new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
		doRichiestaAz.setNumAnno(numAnno);

		res = (SourceBean) QueryExecutor.executeQuery("SELECT_DO_NUMERO_RICHIESTA", new Object[] { numAnno }, "SELECT",
				Values.DB_SIL_DATI);

		BigDecimal numeroRIchiesta = (BigDecimal) res.getAttribute("ROW.NUMERORICHIESTA");
		doRichiestaAz.setNumRichiesta(numeroRIchiesta);
		doRichiestaAz.setNumStorico(new BigDecimal(0));
		doRichiestaAz.setDatRichiesta(simple.format(new Date()));
		doRichiestaAz.setDatScadenza(simple.format(new Date()));
		doRichiestaAz.setDatPubblicazione(simple.format(new Date()));
		doRichiestaAz.setNumProfRichiesti(new BigDecimal(randomGenerator.nextInt(50) + 1));
		doRichiestaAz.setCdnUtins(new BigDecimal(100));
		doRichiestaAz.setCdnUtmod(new BigDecimal(100));

		Boolean inseritaRichiesta = (Boolean) QueryExecutor.executeQuery("INSERT_DO_RICHIESTA_AZ",
				doRichiestaAz.toArray(), "INSERT", Values.DB_SIL_DATI);

		assertTrue(inseritaRichiesta);
		_logger.info("Inserita una nuova richiesta di personale con prgrichiesta = " + prgRichiestaAz);

		DoAlternativa doAlternativa = new DoAlternativa();

		res = (SourceBean) QueryExecutor.executeQuery("SELECT_IDO_PRGALTERNATIVA", new Object[] { prgRichiestaAz },
				"SELECT", Values.DB_SIL_DATI);
		BigDecimal prgAlternativa = (BigDecimal) res.getAttribute("ROW.DO_NEXTVAL");

		doAlternativa.setPrgRichiestaAz(prgRichiestaAz);
		doAlternativa.setPrgAlternativa(prgAlternativa);
		doAlternativa.setCdnUtIns(new BigDecimal(100));
		doAlternativa.setCdnUtmod(new BigDecimal(100));

		Boolean inseritaAlternativa = (Boolean) QueryExecutor.executeQuery("INSERT_IDO_ALTERNATIVA",
				doAlternativa.toArray(), "INSERT", Values.DB_SIL_DATI);
		assertTrue(inseritaAlternativa);

		deleteAll(prgRichiestaAz, prgAlternativa);

	}

	private void deleteAll(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa) {
		// TODO Auto-generated method stub

	}

	class DoAlternativa {
		BigDecimal prgRichiestaAz, prgAlternativa;
		String flgEsperienza;
		BigDecimal numAnniEsperienza, numda, numa;
		String codMotEta, strMotiveta, flgFormazioneprof, strNote;
		BigDecimal cdnUtIns, cdnUtmod;

		public BigDecimal getPrgRichiestaAz() {
			return prgRichiestaAz;
		}

		public void setPrgRichiestaAz(BigDecimal prgRichiestaAz) {
			this.prgRichiestaAz = prgRichiestaAz;
		}

		public BigDecimal getPrgAlternativa() {
			return prgAlternativa;
		}

		public void setPrgAlternativa(BigDecimal prgAlternativa) {
			this.prgAlternativa = prgAlternativa;
		}

		public String getFlgEsperienza() {
			return flgEsperienza;
		}

		public void setFlgEsperienza(String flgEsperienza) {
			this.flgEsperienza = flgEsperienza;
		}

		public BigDecimal getNumAnniEsperienza() {
			return numAnniEsperienza;
		}

		public void setNumAnniEsperienza(BigDecimal numAnniEsperienza) {
			this.numAnniEsperienza = numAnniEsperienza;
		}

		public BigDecimal getNumda() {
			return numda;
		}

		public void setNumda(BigDecimal numda) {
			this.numda = numda;
		}

		public BigDecimal getNuma() {
			return numa;
		}

		public void setNuma(BigDecimal numa) {
			this.numa = numa;
		}

		public String getCodMotEta() {
			return codMotEta;
		}

		public void setCodMotEta(String codMotEta) {
			this.codMotEta = codMotEta;
		}

		public String getStrMotiveta() {
			return strMotiveta;
		}

		public void setStrMotiveta(String strMotiveta) {
			this.strMotiveta = strMotiveta;
		}

		public String getFlgFormazioneprof() {
			return flgFormazioneprof;
		}

		public void setFlgFormazioneprof(String flgFormazioneprof) {
			this.flgFormazioneprof = flgFormazioneprof;
		}

		public String getStrNote() {
			return strNote;
		}

		public void setStrNote(String strNote) {
			this.strNote = strNote;
		}

		public BigDecimal getCdnUtIns() {
			return cdnUtIns;
		}

		public void setCdnUtIns(BigDecimal cdnUtIns) {
			this.cdnUtIns = cdnUtIns;
		}

		public BigDecimal getCdnUtmod() {
			return cdnUtmod;
		}

		public void setCdnUtmod(BigDecimal cdnUtmod) {
			this.cdnUtmod = cdnUtmod;
		}

		public Object[] toArray() {
			return new Object[] { prgRichiestaAz, prgAlternativa, flgEsperienza, numAnniEsperienza, numda, numa,
					codMotEta, strMotiveta, flgFormazioneprof, strNote, cdnUtIns, cdnUtmod };
		}

	}

	class DoRichiestaAz {
		BigDecimal prgRichiestaAz;
		BigDecimal prgAzienda;
		BigDecimal prgUnita;
		String codCpi;
		BigDecimal numAnno;
		BigDecimal numRichiesta;
		BigDecimal numStorico;
		String datRichiesta;
		String flagArt16; // null
		String datScadenza;
		String datPubblicazione;

		BigDecimal numProfRichiesti;
		String strLocalita; // null
		BigDecimal prgSpi; // null
		String strCognomeRiferimento; // null
		String strNomeRiferimento; // null
		String strTelRiferimento; // null
		String strFaxRiferimento; // null
		String strEmailRiferimento; // null
		String flgAutomunito; // null
		String flgMilite; // null
		String codTrasferta; // null
		String flgFuoriSede; // null
		String txtNoteOperatore; // null
		String flgMotoMunito; // null
		String flgVittoAlloggio; // null
		String flgVitto, // null
				flgTurismo, // null
				strSesso, // null
				codMotGenere, // null
				strMotivSesso, // null
				codArea; // null
		BigDecimal cdnUtins;
		// DTMINS,
		BigDecimal cdnUtmod,
				// DTMMOD,
				// NUMKLORICHIESTAAZ,
				CDNGRUPPO, // null
				NUMPOSTOAS, // null
				NUMPOSTOLSU, // null
				NUMPOSTOMILITARE, // null
				NUMPOSTOMB; // null
		String FLGRIUSOGRADUATORIA; // null
		String DATCHIAMATA; // null
		String CODTIPOLSU; // null
		String CODMONOTIPOGRAD; // null
		BigDecimal NUMPOSTICM, // null
				NUMANNOREDDITOCM; // null
		String DATCHIAMATACM; // null
		String CODTIPOLISTA; // null
		//
		//

		public Object[] toArray() {
			return new Object[] { prgRichiestaAz, prgAzienda, prgUnita, codCpi, numAnno, numRichiesta, numStorico,
					datRichiesta, flagArt16, datScadenza, numProfRichiesti, strLocalita, prgSpi, strCognomeRiferimento,
					strNomeRiferimento, strTelRiferimento, strFaxRiferimento, strEmailRiferimento, flgAutomunito,
					flgMilite, codTrasferta, flgFuoriSede, txtNoteOperatore, flgMotoMunito, flgVittoAlloggio, flgVitto,
					flgTurismo, strSesso, codMotGenere, strMotivSesso, codArea, cdnUtins, cdnUtmod, CDNGRUPPO,
					NUMPOSTOAS, NUMPOSTOLSU, NUMPOSTOMILITARE, NUMPOSTOMB, FLGRIUSOGRADUATORIA, DATCHIAMATA,
					CODTIPOLSU };
		}

		public String getDatPubblicazione() {
			return datPubblicazione;
		}

		public void setDatPubblicazione(String datPubblicazione) {
			this.datPubblicazione = datPubblicazione;
		}

		public BigDecimal getPrgRichiestaAz() {
			return prgRichiestaAz;
		}

		public void setPrgRichiestaAz(BigDecimal prgRichiestaAz) {
			this.prgRichiestaAz = prgRichiestaAz;
		}

		public BigDecimal getPrgAzienda() {
			return prgAzienda;
		}

		public void setPrgAzienda(BigDecimal prgAzienda) {
			this.prgAzienda = prgAzienda;
		}

		public BigDecimal getPrgUnita() {
			return prgUnita;
		}

		public void setPrgUnita(BigDecimal prgUnita) {
			this.prgUnita = prgUnita;
		}

		public String getCodCpi() {
			return codCpi;
		}

		public void setCodCpi(String codCpi) {
			this.codCpi = codCpi;
		}

		public BigDecimal getNumAnno() {
			return numAnno;
		}

		public void setNumAnno(BigDecimal numAnno) {
			this.numAnno = numAnno;
		}

		public BigDecimal getNumRichiesta() {
			return numRichiesta;
		}

		public void setNumRichiesta(BigDecimal numRichiesta) {
			this.numRichiesta = numRichiesta;
		}

		public BigDecimal getNumStorico() {
			return numStorico;
		}

		public void setNumStorico(BigDecimal numStorico) {
			this.numStorico = numStorico;
		}

		public String getDatRichiesta() {
			return datRichiesta;
		}

		public void setDatRichiesta(String datRichiesta) {
			this.datRichiesta = datRichiesta;
		}

		public String getFlagArt16() {
			return flagArt16;
		}

		public void setFlagArt16(String flagArt16) {
			this.flagArt16 = flagArt16;
		}

		public String getDatScadenza() {
			return datScadenza;
		}

		public void setDatScadenza(String datScadenza) {
			this.datScadenza = datScadenza;
		}

		public BigDecimal getNumProfRichiesti() {
			return numProfRichiesti;
		}

		public void setNumProfRichiesti(BigDecimal numProfRichiesti) {
			this.numProfRichiesti = numProfRichiesti;
		}

		public String getStrLocalita() {
			return strLocalita;
		}

		public void setStrLocalita(String strLocalita) {
			this.strLocalita = strLocalita;
		}

		public BigDecimal getPrgSpi() {
			return prgSpi;
		}

		public void setPrgSpi(BigDecimal prgSpi) {
			this.prgSpi = prgSpi;
		}

		public String getStrCognomeRiferimento() {
			return strCognomeRiferimento;
		}

		public void setStrCognomeRiferimento(String strCognomeRiferimento) {
			this.strCognomeRiferimento = strCognomeRiferimento;
		}

		public String getStrNomeRiferimento() {
			return strNomeRiferimento;
		}

		public void setStrNomeRiferimento(String strNomeRiferimento) {
			this.strNomeRiferimento = strNomeRiferimento;
		}

		public String getStrTelRiferimento() {
			return strTelRiferimento;
		}

		public void setStrTelRiferimento(String strTelRiferimento) {
			this.strTelRiferimento = strTelRiferimento;
		}

		public String getStrFaxRiferimento() {
			return strFaxRiferimento;
		}

		public void setStrFaxRiferimento(String strFaxRiferimento) {
			this.strFaxRiferimento = strFaxRiferimento;
		}

		public String getStrEmailRiferimento() {
			return strEmailRiferimento;
		}

		public void setStrEmailRiferimento(String strEmailRiferimento) {
			this.strEmailRiferimento = strEmailRiferimento;
		}

		public String getFlgAutomunito() {
			return flgAutomunito;
		}

		public void setFlgAutomunito(String flgAutomunito) {
			this.flgAutomunito = flgAutomunito;
		}

		public String getFlgMilite() {
			return flgMilite;
		}

		public void setFlgMilite(String flgMilite) {
			this.flgMilite = flgMilite;
		}

		public String getCodTrasferta() {
			return codTrasferta;
		}

		public void setCodTrasferta(String codTrasferta) {
			this.codTrasferta = codTrasferta;
		}

		public String getFlgFuoriSede() {
			return flgFuoriSede;
		}

		public void setFlgFuoriSede(String flgFuoriSede) {
			this.flgFuoriSede = flgFuoriSede;
		}

		public String getTxtNoteOperatore() {
			return txtNoteOperatore;
		}

		public void setTxtNoteOperatore(String txtNoteOperatore) {
			this.txtNoteOperatore = txtNoteOperatore;
		}

		public String getFlgMotoMunito() {
			return flgMotoMunito;
		}

		public void setFlgMotoMunito(String flgMotoMunito) {
			this.flgMotoMunito = flgMotoMunito;
		}

		public String getFlgVittoAlloggio() {
			return flgVittoAlloggio;
		}

		public void setFlgVittoAlloggio(String flgVittoAlloggio) {
			this.flgVittoAlloggio = flgVittoAlloggio;
		}

		public String getFlgVitto() {
			return flgVitto;
		}

		public void setFlgVitto(String flgVitto) {
			this.flgVitto = flgVitto;
		}

		public String getFlgTurismo() {
			return flgTurismo;
		}

		public void setFlgTurismo(String flgTurismo) {
			this.flgTurismo = flgTurismo;
		}

		public String getStrSesso() {
			return strSesso;
		}

		public void setStrSesso(String strSesso) {
			this.strSesso = strSesso;
		}

		public String getCodMotGenere() {
			return codMotGenere;
		}

		public void setCodMotGenere(String codMotGenere) {
			this.codMotGenere = codMotGenere;
		}

		public String getStrMotivSesso() {
			return strMotivSesso;
		}

		public void setStrMotivSesso(String strMotivSesso) {
			this.strMotivSesso = strMotivSesso;
		}

		public String getCodArea() {
			return codArea;
		}

		public void setCodArea(String codArea) {
			this.codArea = codArea;
		}

		public BigDecimal getCdnUtins() {
			return cdnUtins;
		}

		public void setCdnUtins(BigDecimal cdnUtins) {
			this.cdnUtins = cdnUtins;
		}

		public BigDecimal getCdnUtmod() {
			return cdnUtmod;
		}

		public void setCdnUtmod(BigDecimal cdnUtmod) {
			this.cdnUtmod = cdnUtmod;
		}

		public BigDecimal getCDNGRUPPO() {
			return CDNGRUPPO;
		}

		public void setCDNGRUPPO(BigDecimal cDNGRUPPO) {
			CDNGRUPPO = cDNGRUPPO;
		}

		public BigDecimal getNUMPOSTOAS() {
			return NUMPOSTOAS;
		}

		public void setNUMPOSTOAS(BigDecimal nUMPOSTOAS) {
			NUMPOSTOAS = nUMPOSTOAS;
		}

		public BigDecimal getNUMPOSTOLSU() {
			return NUMPOSTOLSU;
		}

		public void setNUMPOSTOLSU(BigDecimal nUMPOSTOLSU) {
			NUMPOSTOLSU = nUMPOSTOLSU;
		}

		public BigDecimal getNUMPOSTOMILITARE() {
			return NUMPOSTOMILITARE;
		}

		public void setNUMPOSTOMILITARE(BigDecimal nUMPOSTOMILITARE) {
			NUMPOSTOMILITARE = nUMPOSTOMILITARE;
		}

		public BigDecimal getNUMPOSTOMB() {
			return NUMPOSTOMB;
		}

		public void setNUMPOSTOMB(BigDecimal nUMPOSTOMB) {
			NUMPOSTOMB = nUMPOSTOMB;
		}

		public String getFLGRIUSOGRADUATORIA() {
			return FLGRIUSOGRADUATORIA;
		}

		public void setFLGRIUSOGRADUATORIA(String fLGRIUSOGRADUATORIA) {
			FLGRIUSOGRADUATORIA = fLGRIUSOGRADUATORIA;
		}

		public String getDATCHIAMATA() {
			return DATCHIAMATA;
		}

		public void setDATCHIAMATA(String dATCHIAMATA) {
			DATCHIAMATA = dATCHIAMATA;
		}

		public String getCODTIPOLSU() {
			return CODTIPOLSU;
		}

		public void setCODTIPOLSU(String cODTIPOLSU) {
			CODTIPOLSU = cODTIPOLSU;
		}

		public String getCODMONOTIPOGRAD() {
			return CODMONOTIPOGRAD;
		}

		public void setCODMONOTIPOGRAD(String cODMONOTIPOGRAD) {
			CODMONOTIPOGRAD = cODMONOTIPOGRAD;
		}

		public BigDecimal getNUMPOSTICM() {
			return NUMPOSTICM;
		}

		public void setNUMPOSTICM(BigDecimal nUMPOSTICM) {
			NUMPOSTICM = nUMPOSTICM;
		}

		public BigDecimal getNUMANNOREDDITOCM() {
			return NUMANNOREDDITOCM;
		}

		public void setNUMANNOREDDITOCM(BigDecimal nUMANNOREDDITOCM) {
			NUMANNOREDDITOCM = nUMANNOREDDITOCM;
		}

		public String getDATCHIAMATACM() {
			return DATCHIAMATACM;
		}

		public void setDATCHIAMATACM(String dATCHIAMATACM) {
			DATCHIAMATACM = dATCHIAMATACM;
		}

		public String getCODTIPOLISTA() {
			return CODTIPOLISTA;
		}

		public void setCODTIPOLISTA(String cODTIPOLISTA) {
			CODTIPOLISTA = cODTIPOLISTA;
		}

	}

}
