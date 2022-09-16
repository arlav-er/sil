package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.services.CigException;
import it.eng.sil.coop.utils.Converter;

/**
 * Bean contenente tutti i dati che riguardano una domanda CIG
 * 
 * @author Esposito,Rodi
 *
 */
@SuppressWarnings("unchecked")
public class CIDomandaCIGBean {
	private static final Logger _logger = Logger.getLogger(CIDomandaCIGBean.class.getName());

	private CIAccordoBean ciAccBean; // dati sull'accordo
	private BigDecimal prgAccordo; // identificativo dell'accordo
	private CIEsameCongiuntoBean ciEsameCong; // esame congiunto che fa parte dell'accordo
	private AziendaBean ciAzienda; // azienda madre di cui fanno parte le singole unita' azienda
	private UnitaAziendaBean uAzSedeLegale;
	private List<CISindacato> listaSindacati;
	private List<CIRelAccordoAzBean> listaRelAccordo;

	private SourceBean accordo;
	private SourceBean esameCongiunto;
	private SourceBean azienda;
	private Vector<SourceBean> relAzienda;
	private Vector sindacati;

	private BigDecimal prgAzienda;

	private int ggTolleranza = 0;

	private TransactionQueryExecutor tex;

	public CIDomandaCIGBean(SourceBean iscrizioneCIG, String cdnUtIns, String cdnUtMod, TransactionQueryExecutor _tex)
			throws EMFInternalError, SourceBeanException, ParseException {
		_logger.info("Inizio la costruzione del bean domanda cig.");
		this.tex = _tex;

		this.ggTolleranza = getGgTolleranza();
		_logger.info("giorni di tolleranza: " + this.ggTolleranza);
		accordo = (SourceBean) iscrizioneCIG.getAttribute("DATIACCORDO");

		ciAccBean = new CIAccordoBean(accordo, cdnUtIns, cdnUtMod, tex);

		esameCongiunto = (SourceBean) iscrizioneCIG.getAttribute("ESAMECONGIUNTO");

		if (esameCongiunto != null)
			ciEsameCong = new CIEsameCongiuntoBean(esameCongiunto, cdnUtIns, cdnUtMod, tex);

		azienda = (SourceBean) iscrizioneCIG.getAttribute("AZIENDA");

		// String codCom = null;
		// controlliamo l'esistenza dell' azienda
		String strCodiceFiscale = (String) azienda.getCharacters("STRCODICEFISCALE");
		SourceBean row = (SourceBean) tex.executeQuery("GET_PRG_AZIENDA", new Object[] { strCodiceFiscale }, "SELECT");
		prgAzienda = (BigDecimal) row.getAttribute("ROW.PRGAZIENDA"); // recupero il prg

		boolean aziendaSuDb = (prgAzienda != null); // variabile che determina se l'azienda era su db prima dell'arrivo
													// della comunicazione.

		// se il prg non e' stato recuperato significa che l'azienda non esiste
		if (!aziendaSuDb) {
			_logger.info("L'azienda " + strCodiceFiscale + " ancora non esiste. La creo nuova.");
			// la creo ex-novo
			String strRagioneSociale = (String) azienda.getCharacters("STRRAGIONESOCIALE");
			String codTipoAzienda = (String) azienda.getCharacters("CODTIPOAZIENDA");
			String codNatGiuridica = (String) azienda.getCharacters("CODNATGIURIDICA");

			ciAzienda = new AziendaBean(strCodiceFiscale, strRagioneSociale, codTipoAzienda, codNatGiuridica, cdnUtIns,
					cdnUtMod, tex);
			ciAzienda.setNumDipendenti((String) azienda.getCharacters("NUMDIPENDENTI"));
			ciAzienda.setNumCollaboratori((String) azienda.getCharacters("NUMCOLLABORATORI"));
			ciAzienda.setNumAltraPosizione((String) azienda.getCharacters("NUMALTRAPOSIZIONE"));
			ciAzienda.setDatInizio(
					Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) azienda.getCharacters("DATINIZIO")));

			ciAzienda.setFlagNuova(true); // setto il flag di controllo a true per indicare che non e' gia' presente su
											// DB
		}

		/*********************************/
		/** Inizio Gestione sede legale **/
		/*********************************/

		SourceBean sedeLegale = (SourceBean) azienda.getAttribute("SEDELEGALE");

		BigDecimal prgUnitaSL = null;

		String codComSL = (String) sedeLegale.getCharacters("CODCOM");
		String strIndirizzoSL = (String) sedeLegale.getCharacters("STRINDIRIZZO");
		String codAtecoSL = (String) sedeLegale.getCharacters("CODATECO");
		String codCCNLSL = (String) sedeLegale.getCharacters("CODCCNL");
		String codAzStatoSL = (String) sedeLegale.getCharacters("CODAZSTATO");

		_logger.info("Indirizzo Sede legale dell'azienda:" + strIndirizzoSL);

		@SuppressWarnings("unused")
		String codComUaz_dbSL = "";

		// se l'azienda e' presente su DB
		if (aziendaSuDb) {
			// recupero la sede legale della domanda da DB, facendo match con il comune e l'indirizzo
			row = (SourceBean) tex.executeQuery("GET_SEDE_LEGALE",
					new Object[] { prgAzienda, codComSL, strIndirizzoSL }, "SELECT");
			Vector<SourceBean> righe = row.getAttributeAsVector("row");
			String flgSedeDB = "N";
			if (righe.size() == 1) {
				prgUnitaSL = (BigDecimal) row.getAttribute("row.prgUnita");
				codComUaz_dbSL = (String) row.getAttribute("row.CODCOM");
				flgSedeDB = (String) row.getAttribute("row.flgsede");
			}
			/*
			 * bugfix-potrebbero esserci più unità azienda con lo stesso comune e stesso indirizzo in tal caso mi
			 * aggancio a quella che è sede legale, se presente, o a quella più recente, in caso contrario
			 */
			else if (righe.size() > 1) {
				for (SourceBean unitaSB : righe) {
					prgUnitaSL = (BigDecimal) unitaSB.getAttribute("prgUnita");
					codComUaz_dbSL = (String) unitaSB.getAttribute("CODCOM");
					flgSedeDB = (String) unitaSB.getAttribute("flgsede");
					if ("S".equalsIgnoreCase(flgSedeDB))
						break; // uno dei risultati è SL. mi aggancio a questa
				}
			}

			boolean esisteSLcomeUO = (prgUnitaSL != null);

			// se ho fatto match, ovvero ho trovato la sede legale su DB come unità legata all'azienda
			if (esisteSLcomeUO) {
				uAzSedeLegale = new UnitaAziendaBean(prgAzienda, codComSL, codAzStatoSL, codAtecoSL, strIndirizzoSL,
						codCCNLSL, cdnUtIns, cdnUtMod, tex);
				uAzSedeLegale.setPrgUnita(prgUnitaSL.toString());
				uAzSedeLegale.setFlgSede((String) sedeLegale.getCharacters("FLGSEDE"));
				uAzSedeLegale.setFlagNuova(false); // setto il flag di controllo

				boolean coincideSedeLegale = "S".equalsIgnoreCase(flgSedeDB);

				// se corrisponde già alla sede legale per quell'azienda
				if (coincideSedeLegale) {
					// CASO C
					_logger.info("La sede legale coincide con quella sul DB. Non verrà aggiornato alcun dato.");

				} else {
					// CASO B
					_logger.info("La sede legale è diversa da quella trovata sul DB.");

					uAzSedeLegale.setFlagUpdate(true); // setto il flag di controllo
				}
			}

		}

		if (prgUnitaSL == null) {
			// CASO A
			// se non sono riuscito a trovare la sede legale allora la costruisco per il successivo inserimento
			// se l'azienda non esiste mi comporto allo stesso modo
			_logger.info("La sede legale ancora non esiste o è differente da quella su DB. La creo nuova.");
			codAzStatoSL = "1";// in attivita'

			if (prgAzienda != null)
				uAzSedeLegale = new UnitaAziendaBean(prgAzienda, codComSL, codAzStatoSL, codAtecoSL, strIndirizzoSL,
						codCCNLSL, cdnUtIns, cdnUtMod, tex);
			else
				uAzSedeLegale = new UnitaAziendaBean(codComSL, codAzStatoSL, codAtecoSL, strIndirizzoSL, codCCNLSL,
						cdnUtIns, cdnUtMod, tex);

			uAzSedeLegale.setStrLocalita((String) sedeLegale.getCharacters("STRLOCALITA"));
			uAzSedeLegale.setStrCap((String) sedeLegale.getCharacters("STRCAP"));
			uAzSedeLegale.setFlgSede((String) sedeLegale.getCharacters("FLGSEDE"));
			uAzSedeLegale.setStrResponsabile((String) sedeLegale.getCharacters("STRRESPONSABILE"));
			uAzSedeLegale.setStrReferente((String) sedeLegale.getCharacters("STRREFERENTE"));
			uAzSedeLegale.setStrTel((String) sedeLegale.getCharacters("STRTEL"));
			uAzSedeLegale.setStrFax((String) sedeLegale.getCharacters("STRFAX"));
			uAzSedeLegale.setStrEmail((String) sedeLegale.getCharacters("STREMAIL"));
			uAzSedeLegale.setStrnumeroinps((String) sedeLegale.getCharacters("STRNUMEROINPS"));
			uAzSedeLegale.setStrRepartoInps((String) sedeLegale.getCharacters("STRREPARTOINPS"));
			uAzSedeLegale.setStrDenominazione((String) sedeLegale.getCharacters("STRDENOMINAZIONE"));
			//
			uAzSedeLegale.setFlagNuova(true);
		}

		/* Fine gestione sede legale */

		/*******************************/
		/** Inizio Gestione sindacati **/
		/*******************************/

		sindacati = iscrizioneCIG.getAttributeAsVector("SINDACATI");
		listaSindacati = new ArrayList<CISindacato>();
		for (int i = 0; i < sindacati.size(); i++) {
			String strCognome = (String) ((SourceBean) sindacati.get(i)).getCharacters("STRCOGNOME");
			String strNome = (String) ((SourceBean) sindacati.get(i)).getCharacters("STRNOME");
			String strSindacato = (String) ((SourceBean) sindacati.get(i)).getCharacters("STRSINDACATO");
			CISindacato tmp = new CISindacato(strCognome, strNome, strSindacato, cdnUtIns, cdnUtMod, tex);

			listaSindacati.add(tmp);
		}
		/* Fine gestione sindacati */

		/*********************************************/
		/** Gestione relazioni Accordo <--> Azienda **/
		/*********************************************/

		relAzienda = iscrizioneCIG.getAttributeAsVector("RELAZIENDA");
		listaRelAccordo = new ArrayList<CIRelAccordoAzBean>();

		// scorri tutti gli accordi
		for (int i = 0; i < relAzienda.size(); i++) {

			SourceBean relazione = (SourceBean) relAzienda.get(i);

			String flgCoinvolta = (String) relazione.getAttribute("FLGCOINVOLTA");
			String numLavForza = (String) relazione.getAttribute("NUMLAVFORZA");

			CIRelAccordoAzBean tmpRelAccordo = new CIRelAccordoAzBean(flgCoinvolta, numLavForza, cdnUtIns, cdnUtMod,
					tex);

			tmpRelAccordo.setStrnumeroinps((String) relazione.getAttribute("STRNUMEROINPS"));
			tmpRelAccordo.setStrsedeinps((String) relazione.getAttribute("STRSEDEINPS"));
			tmpRelAccordo.setCodaztipo((String) relazione.getAttribute("CODAZTIPO"));
			tmpRelAccordo.setStrorgdatoriale((String) relazione.getAttribute("STRORGDATORIALE"));
			tmpRelAccordo.setStrconsulentelav((String) relazione.getAttribute("STRCONSULENTELAV"));
			// tmp.setPrgaccordo(prgAccordo.toString());

			// unita' aziendale
			BigDecimal prgUnita = null;
			UnitaAziendaBean tmpUAz = null;

			// cerchiamo l'unita'
			SourceBean UAzSBean = new SourceBean((SourceBean) relazione.getAttribute("SEDE"));

			String codComUaz = (String) UAzSBean.getCharacters("CODCOM");
			String strIndirizzoUaz = (String) UAzSBean.getCharacters("STRINDIRIZZO");
			String codAzStato = (String) UAzSBean.getCharacters("CODAZSTATO");
			String codAtecoUaz = (String) UAzSBean.getCharacters("CODATECO");
			String codCCNLUAz = (String) UAzSBean.getCharacters("CODCCNL");

			String codComUaz_db = "";

			// l'azienda corrisponde con la sede legale se appartengono allo stesso comune e hanno lo stesso indirizzo
			boolean matchSL;
			if (codComUaz.equalsIgnoreCase(uAzSedeLegale.getCodCom())) {
				if (strIndirizzoUaz == null) {
					matchSL = (uAzSedeLegale.getStrIndirizzo() == null);
				} else {
					matchSL = (strIndirizzoUaz.equalsIgnoreCase(uAzSedeLegale.getStrIndirizzo()));
				}
			} else {
				matchSL = false;
			}

			if (matchSL) {
				// CASO C - se l'unità è anche la sede legale imposto il flag
				_logger.info("L'unità " + codComUaz + " - " + strIndirizzoUaz + " coincide con la sede legale.");
				tmpUAz = new UnitaAziendaBean(codComUaz, codAzStato, codAtecoUaz, strIndirizzoUaz, codCCNLUAz, cdnUtIns,
						cdnUtMod, tex);
				if (prgAzienda != null) {
					tmpUAz.setPrgAzienda(prgAzienda.toString());
					tmpRelAccordo.setPrgazienda(prgAzienda.toString());
				}
				if (uAzSedeLegale.getPrgUnita() != null)
					tmpRelAccordo.setPrgunita(uAzSedeLegale.getPrgUnita());

				tmpUAz.setSedeLegale(true);
				tmpRelAccordo.setSede(tmpUAz);
			} else {
				// l'unità non coincide con la sede legale

				// se l'azienda è già presente su DB
				if (aziendaSuDb) {

					tmpRelAccordo.setPrgazienda(prgAzienda.toString());

					// verifico se l'unità è già presente su DB
					// cerca sulla an_unita_azienda l'azienda facendo match di comune ed indirizzo.
					row = (SourceBean) tex.executeQuery("GET_SEDE_LEGALE",
							new Object[] { prgAzienda, codComUaz, strIndirizzoUaz }, "SELECT");
					Vector<SourceBean> righe = row.getAttributeAsVector("row");
					if (righe.size() == 1) {
						prgUnita = (BigDecimal) row.getAttribute("row.prgUnita");
						codComUaz_db = (String) row.getAttribute("row.CODCOM");
					}
					/*
					 * bugfix-potrebbero esserci più unità azienda con lo stesso comune e stesso indirizzo in tal caso
					 * mi aggancio a quella più recente che non è sede legale
					 */
					else if (righe.size() > 1) {
						for (SourceBean unitaSB : righe) {
							String flgSede = (String) unitaSB.getAttribute("flgsede");
							if ("N".equalsIgnoreCase(flgSede)) {
								prgUnita = (BigDecimal) unitaSB.getAttribute("prgUnita");
								codComUaz_db = (String) unitaSB.getAttribute("CODCOM");
							}
						}

					}

					// se prgunita e' null, non ho fatto match su DB, cerco allora di fare match solo con il comune.
					if (prgUnita == null) {
						row = (SourceBean) tex.executeQuery("GET_SEDE_LEGALE_BYCOMUNE",
								new Object[] { prgAzienda, codComUaz }, "SELECT");
						Vector rows = row.getAttributeAsVector("ROW");
						if (rows.size() > 0) {
							SourceBean primaUnita = (SourceBean) rows.get(0);
							prgUnita = (BigDecimal) primaUnita.getAttribute("prgUnita");
							codComUaz_db = (String) row.getAttribute("row.CODCOM");
							_logger.warn("L'unità operativa " + codComUaz + " - " + strIndirizzoUaz
									+ " è stata agganciata ad un differente indirizzo: "
									+ (String) primaUnita.getAttribute("STRINDIRIZZO") + ".");
						}
					}

					boolean esisteUOsuDb = prgUnita != null;

					if (esisteUOsuDb) {
						_logger.info("L'unità " + prgUnita + " - " + codComUaz + " è già presente su DB.");

						// sono riuscito a trovare l'unità sul database
						// CASO B
						tmpUAz = new UnitaAziendaBean(prgAzienda, codComUaz, codAzStato, codAtecoUaz, strIndirizzoUaz,
								codCCNLUAz, cdnUtIns, cdnUtMod, tex);
						tmpUAz.setPrgUnita(prgUnita.toString());
						tmpUAz.setFlgSede((String) UAzSBean.getCharacters("FLGSEDE"));
						tmpUAz.setFlagNuova(false);
						tmpRelAccordo.setPrgunita(prgUnita.toString());
						tmpRelAccordo.setSede(tmpUAz);
					}

				}

				// se non ho trovato l'unità su DB o l'azienda non esiste
				if (prgUnita == null) {
					// CASO A
					// non ho fatto match e non sono riuscito a trovare l'unità su database.
					_logger.info("non ho trovato l'unità " + codComUaz + " sul DB. La creo.");
					codAzStato = "1";// in attivita'

					if (prgAzienda != null)
						tmpUAz = new UnitaAziendaBean(prgAzienda, codComUaz, codAzStato, codAtecoUaz, strIndirizzoUaz,
								codCCNLUAz, cdnUtIns, cdnUtMod, tex);
					else
						tmpUAz = new UnitaAziendaBean(codComUaz, codAzStato, codAtecoUaz, strIndirizzoUaz, codCCNLUAz,
								cdnUtIns, cdnUtMod, tex);

					tmpUAz.setStrLocalita((String) UAzSBean.getCharacters("STRLOCALITA"));
					tmpUAz.setStrCap((String) UAzSBean.getCharacters("STRCAP"));
					tmpUAz.setFlgSede((String) UAzSBean.getCharacters("FLGSEDE"));
					tmpUAz.setStrResponsabile((String) UAzSBean.getCharacters("STRRESPONSABILE"));
					tmpUAz.setStrReferente((String) UAzSBean.getCharacters("STRREFERENTE"));
					tmpUAz.setStrTel((String) UAzSBean.getCharacters("STRTEL"));
					tmpUAz.setStrFax((String) UAzSBean.getCharacters("STRFAX"));
					tmpUAz.setStrEmail((String) UAzSBean.getCharacters("STREMAIL"));
					tmpUAz.setStrnumeroinps((String) UAzSBean.getCharacters("STRNUMEROINPS"));
					tmpUAz.setStrRepartoInps((String) UAzSBean.getCharacters("STRREPARTOINPS"));
					tmpUAz.setStrDenominazione((String) UAzSBean.getCharacters("STRDENOMINAZIONE"));
					//
					tmpUAz.setFlagNuova(true);
					tmpRelAccordo.setSede(tmpUAz);
				} // fine unità non trovata
			} // fine !matchSL

			Vector<SourceBean> lavoratori = ((SourceBean) relAzienda.get(i)).getAttributeAsVector("LAVORATORI");

			for (int j = 0; j < lavoratori.size(); j++) {

				SourceBean lavBean = lavoratori.get(j);

				CILavoratoreBean tmpLav = new CILavoratoreBean(lavBean, cdnUtIns, cdnUtMod, tex);

				/* tmpLav.setPrgaccordo(prgAccordo.toString()); */
				tmpLav.setCodTipoAccordo(ciAccBean.getCodtipoaccordo());

				if (prgAzienda != null)
					tmpLav.setPrgazienda(prgAzienda.toString());

				if (prgUnita != null) {
					tmpLav.setPrgunita(prgUnita.toString());
					tmpLav.setCodComSede(codComUaz_db);
				} else
					tmpLav.setCodComSede(tmpUAz.getCodCom());

				tmpLav.calcolaCompetenza();
				if (tmpLav.isCompetente()) {
					_logger.info("Lavoratore " + tmpLav.getStrcodicefiscale() + " competente "
							+ tmpLav.getCodmonotipocompetenza() + ".");
				} else {
					_logger.info("Lavoratore " + tmpLav.getStrcodicefiscale() + " non competente.");
				}

				Vector<SourceBean> accordiLav = lavBean.getAttributeAsVector("DATILAVORATOREACCORDO");

				for (int k = 0; k < accordiLav.size(); k++) {
					CILavAccordoBean tmpLavAcc = new CILavAccordoBean((SourceBean) accordiLav.get(k), cdnUtIns,
							cdnUtMod, tex);
					tmpLav.addLavAccordo(tmpLavAcc);
				}

				tmpLav.calcolaDateCIG(ciAccBean.getNumconcessione(), ciAccBean.getDatconcessione(),
						ciAccBean.getCodtipoconcessione(), ciAccBean.getCodtipoaccordo());

				tmpRelAccordo.addLavoratore(tmpLav);
			}

			listaRelAccordo.add(tmpRelAccordo);
		}

		/* Fine Gestione relazioni Accordo <--> Azienda */

	}

	/**
	 * Recupera da DB il parametro per i giorni di tolleranza nel matching tra periodi
	 * 
	 * @return numero di giorni di tolleranza
	 * @throws EMFInternalError
	 */
	private int getGgTolleranza() throws EMFInternalError {

		SourceBean row = (SourceBean) tex.executeQuery("GET_GG_TOLLERANZA", new Object[] {}, "SELECT");

		return Integer.parseInt((String) row.getAttribute("ROW.GG_TOLLERANZA"));

	}

	/**
	 * Procedura per l'inserimento della sede legale su DB.
	 * 
	 * @throws EMFInternalError
	 * @throws CigException
	 */
	private void insertSedeLegaleProcedure() throws EMFInternalError, CigException {
		if (this.uAzSedeLegale.isFlagUpdate()) {
			// CASO B
			uAzSedeLegale.reimpostaFlag();
		}

		else if (this.uAzSedeLegale.isFlagNuova()) {
			// CASO A
			/**
			 * FIXME eliminare reimpostaFlag all'intenrod ella funzione e tirarlo fuori.
			 */
			uAzSedeLegale.insert();
		} else { // altrimenti la aggiorno
			// CASO C
			// FIXME controllo inutile a mio parere, come tutto quello che ci sta dentro.
			if ("S".equalsIgnoreCase(this.uAzSedeLegale.getFlgSede())) {

				_logger.info("aggiorno la sede legale " + uAzSedeLegale.getPrgAzienda());

				DataConnection conn = tex.getDataConnection();
				StoredProcedureCommand command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
						"{ call ? := PG_GESTIONE_CIG.pdAggFlgSedeLegaleAzienda(?,?,?,?) }");
				DataResult dr = null;

				ArrayList parameters = new ArrayList(5);
				parameters.add(conn.createDataField("result", Types.BIGINT, null));
				command.setAsOutputParameters(0);
				parameters.add(conn.createDataField("prgParAzienda", Types.BIGINT, prgAzienda));
				command.setAsInputParameters(1);
				parameters.add(conn.createDataField("prgParUnita", Types.BIGINT, this.uAzSedeLegale.getPrgUnita()));
				command.setAsInputParameters(2);
				parameters.add(conn.createDataField("pCodAccordo", Types.VARCHAR, ciAccBean.getCodaccordo()));
				command.setAsInputParameters(3);
				parameters.add(conn.createDataField("pCdnUt", Types.BIGINT, this.uAzSedeLegale.getCdnUtMod()));
				command.setAsInputParameters(4);

				dr = command.execute(parameters);
				PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();
				DataField df = cdr.getPunctualDatafield();
				String codiceRit = df.getStringValue();
				if (!codiceRit.equals("0"))
					throw new CigException(codiceRit, "Errore di allineamento del flag sede legale sul DB.");
			}
		}
	}

	/**
	 * Salva l'azienda
	 * 
	 * @throws CigException
	 * @throws EMFInternalError
	 * 
	 */
	public void insertAzienda() throws CigException, EMFInternalError {
		if (ciAzienda == null) {
			/* Inserisco solo la sede legale */
			_logger.info("ciAzienda è null, quindi l'azienda è già presente su database.");
			// se è nuova la inserisco

			// insertAziendaProcedure();

			// return;
		}

		// se si tratta di una nuova azienda
		else if (ciAzienda.isFlagNuova()) {
			_logger.info("L'azienda " + ciAzienda.getStrCodiceFiscale() + " è nuova");
			try {
				// la inserisco su DB e mi salvo il prgAzienda assegnatole
				ciAzienda.insert();
				prgAzienda = new BigDecimal(ciAzienda.getPrgAzienda());
			} catch (EMFInternalError e) {
				SourceBean rowApp = (SourceBean) tex.executeQuery("GET_PRG_AZIENDA",
						new Object[] { ciAzienda.getStrCodiceFiscale() }, "SELECT");
				prgAzienda = (BigDecimal) rowApp.getAttribute("ROW.PRGAZIENDA");
				if (prgAzienda == null) {
					throw new CigException("-1", "Errore nell'inserimento dell'azienda.");
				}
			}
			// infine setto il prgAzienda alla sede legale
			this.uAzSedeLegale.setPrgAzienda(prgAzienda.toString());
		}

		insertSedeLegaleProcedure();
	}

	public void insertSindacati(String prgAccordo) throws EMFInternalError {
		for (int i = 0; i < listaSindacati.size(); i++) {
			listaSindacati.get(i).setPrgaccordo(prgAccordo);
			listaSindacati.get(i).insert();
		}
	}

	/**
	 * Inserisce i record all'interno della tabella CI_REL_ACCORDO. Se più relazioni fanno capo ad una stessa unità,
	 * utilizzano tutti la prima. Della lista, inserisco solo quelli nuovi.
	 * 
	 * @param prgAccordo
	 * @throws EMFInternalError
	 */
	public void insertRelAccordi(String prgAccordo) throws EMFInternalError {
		for (int i = 0; i < listaRelAccordo.size(); i++) {
			CIRelAccordoAzBean relaccordo = listaRelAccordo.get(i);
			relaccordo.setPrgaccordo(prgAccordo);
			// controllo prima che la tripletta non sia già stata inserita
			if (relaccordo.canBeInserted())
				relaccordo.insert();
		}
	}

	public void insertSedi() throws EMFInternalError {
		UnitaAziendaBean uAz = null;

		// scorri tutte le sedi coinvolte
		for (int i = 0; i < listaRelAccordo.size(); i++) {

			CIRelAccordoAzBean relAccordo = listaRelAccordo.get(i);

			uAz = relAccordo.getSede();

			if (uAz == null)
				continue;

			BigDecimal prgUnita = null;
			if (uAz.isSedeLegale()) {
				prgUnita = new BigDecimal(this.uAzSedeLegale.getPrgUnita());
				uAz.setPrgUnita(this.uAzSedeLegale.getPrgUnita());
			} else if (uAz.isFlagNuova()) {

				uAz.setPrgAzienda(prgAzienda.toString());
				uAz.insert();
				prgUnita = new BigDecimal(uAz.getPrgUnita());
			} else {
				continue;
			}

			// 06072010 RODI : bugfix
			// in fase di rettifica, se l'unita non è presente su DB, dopo averla inserita bisogna aggiornare
			// i dati dentro ai bean dei lavoratori poiché questi verranno utilizzati per l'aggiornamento.
			for (CILavoratoreBean lavoratore : relAccordo.getListaLavoratori()) {
				lavoratore.setPrgunita(prgUnita.toString());
			}

			relAccordo.setPrgunita(prgUnita.toString());
			relAccordo.setPrgazienda(prgAzienda.toString());

		}
	}

	/**
	 * 
	 * @param codStatoIscr
	 * @param prgAccordo
	 * @param insAnag_Iscr
	 *            i lavoratori sono da inserire in anagrafica?
	 * @throws EMFInternalError
	 */
	public void insertLavoratori(String codStatoIscr, String prgAccordo, boolean insAnag_Iscr) throws EMFInternalError {
		for (int i = 0; i < listaRelAccordo.size(); i++) {
			for (int j = 0; j < listaRelAccordo.get(i).getListaLavoratori().size(); j++) {
				CIRelAccordoAzBean relAccordo = listaRelAccordo.get(i);
				CILavoratoreBean lavb = relAccordo.getListaLavoratori().get(j);
				lavb.setPrgaccordo(prgAccordo);
				lavb.setPrgazienda(this.prgAzienda.toString());
				lavb.setPrgunita(relAccordo.getPrgunita());
				insertLavoratore(lavb, codStatoIscr, false, insAnag_Iscr);
			}
		}
	}

	/**
	 * Inserisce un lavoratore nella tabella CI_LAVORATORE ed eventualmente nella AN_LAVORATORE.
	 * 
	 * @param lav
	 *            bean contenente i dati del lavoratore
	 * @param codTipoConc
	 * @param rettifica
	 *            stiamo trattando una rettifica?
	 * @param insAnag_Iscr
	 *            il lavoratore è da inserire in anagrafica?
	 * @throws EMFInternalError
	 */
	public void insertLavoratore(CILavoratoreBean lav, String codTipoConc, boolean rettifica, boolean insAnag_Iscr)
			throws EMFInternalError {
		String codStatoIscr = "";

		if (rettifica) {
			if (codTipoConc.equals(CigConst.CONC_NO)) {
				codStatoIscr = CigConst.ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE;
			}
		}
		// se il lavoratore deve essere inserito e non è presente in anagrafica
		if (!lav.isEsisteInAnag() && lav.isCompetente() && insAnag_Iscr) {
			try {
				lav.insertAnagrafica();
			} catch (Exception e) {
				_logger.info("Non e' stato possibile inserire il lavoratore con cf " + lav.getStrcodicefiscale()
						+ " nell'anagrafica.");
				return;
			}
			try {
				lav.insertLavoratore_IR();
			} catch (Exception e) {
				_logger.info("Non e' stato possibile inserire il lavoratore con cdnlavoratore " + lav.getCdnlavoratore()
						+ " nell'indice regionale.");
			}
		}
		// 22-03-11 se invece il lavoratore deve essere inserito ed è già presente in anagrafica, verifico unicamente se
		// aggiornare il numero di cellulare
		else if (lav.isEsisteInAnag() && lav.isCompetente() && insAnag_Iscr) {
			lav.updateCellulare();
		} else {
			_logger.info("Non inserisco il lavoratore " + lav.getStrcodicefiscale()
					+ " nell'anagrafica, nè nell'Indice Regionale");
		}
		// inserisco il lavoratore nella tabella ci_lavoratore. SEMPRE. Anche se già c'è.
		_logger.info("Inserisco lavoratore " + lav.getStrcodicefiscale() + " in CI_LAVORATORE.");
		lav.insert();

		for (int i = 0; i < lav.getListaLavAccordo().size(); i++) {
			lav.getListaLavAccordo().get(i).setPrgaccordo(lav.getPrgaccordo());
			lav.getListaLavAccordo().get(i).setPrglavoratore(lav.getPrglavoratore());
			lav.getListaLavAccordo().get(i).insert();
		}
		// se il lavoratore è competente, è da inserire in anagrafica e altri casi lo inserisco in am_altra_iscr
		if (insAnag_Iscr && lav.isCompetente() && (rettifica || (!rettifica && !codTipoConc.equals(CigConst.CONC_NO)))
				&& (lav.getDatInizioCig() != null || lav.getDatFineCig() != null)) {
			BigDecimal[] altraIscrComp = lav.cercaIscrizioneCompatibile(this.ggTolleranza);
			if (altraIscrComp == null) {
				lav.insertAmAltraIscr(codStatoIscr, ciAccBean.getCodaccordo());
			} else {
				BigDecimal prgAltraIscrComp = altraIscrComp[0];
				BigDecimal numkloAltraIscrComp = altraIscrComp[1];
				if ((prgAltraIscrComp != null) && (numkloAltraIscrComp != null)) {
					lav.updateAmAltraIscr(prgAltraIscrComp, numkloAltraIscrComp, this.prgAccordo.toString());
				}
			}
		} else {
			_logger.info("Non inserisco il lavoratore " + lav.getStrcodicefiscale() + " in am_altra_iscr.\n"
					+ "insAnag = " + insAnag_Iscr + "\n" + "isCompetente = " + lav.isCompetente() + "\n"
					+ "rettifica = " + rettifica + "\n" + "codTipoConc = " + codTipoConc + "\n" + "datInizioCig = "
					+ lav.getDatInizioCig() + "\n" + "datFineCig = " + lav.getDatFineCig());
		}
	}

	public void insertLavAccordi() throws EMFInternalError {
		for (int i = 0; i < listaRelAccordo.size(); i++) {
			for (int j = 0; j < listaRelAccordo.get(i).getListaLavoratori().size(); j++) {
				for (int k = 0; k < listaRelAccordo.get(i).getListaLavoratori().get(j).getListaLavAccordo()
						.size(); k++) {
					insertLavAccordo(listaRelAccordo.get(i).getListaLavoratori().get(j).getListaLavAccordo().get(k));
				}
			}
		}
	}

	public void insertLavAccordo(CILavAccordoBean lavAcc) throws EMFInternalError {
		lavAcc.insert();
	}

	/**
	 * Inserisce l'accordo nel DB e ne recupera il prg generato
	 * 
	 * @throws EMFInternalError
	 */
	public void insertAccordo() throws EMFInternalError {
		ciAccBean.insert();
		prgAccordo = new BigDecimal(ciAccBean.getPrgaccordo());
	}

	public void insertEsameCong() throws EMFInternalError {
		if (ciEsameCong != null)
			ciEsameCong.insert();
	}

	/**
	 * Inserisce la nuova domanda nel sistema. Se la domanda è un annullamento il parametro è FALSE.
	 * 
	 * @param insAnag_Iscr
	 *            devo inserire i lavoratori presenti nella domanda in anagrafica?
	 * @throws Exception
	 */
	public void insertAll(boolean insAnag_Iscr) throws Exception {
		insertAccordo();

		if (this.getCiEsameCong() != null) {
			this.getCiEsameCong().setPrgaccordo(this.prgAccordo.toString());

			insertEsameCong();
		}

		// allora l'azienda è nuova
		if (this.prgAzienda == null) {
			insertAzienda();
		} else {
			// 01-07-10
			// nel caso in cui l'azienda esiste non esclude il fatto che la sede legale non sia presente.
			// se è da inserire la inserisco.
			if (uAzSedeLegale.isFlagUpdate()) {
				uAzSedeLegale.reimpostaFlag();
			} else if (uAzSedeLegale.isFlagNuova()) {
				uAzSedeLegale.insert();
			}
		}

		insertSindacati(this.prgAccordo.toString());

		insertSedi();

		insertRelAccordi(this.prgAccordo.toString());

		/*
		 * if(ciAccBean.getCodtipoconcessione().equals("S")|| ciAccBean.getCodtipoconcessione().equals("P")){
		 * codStatoIscr = ""; }
		 */

		insertLavoratori(ciAccBean.getCodtipoconcessione(), this.prgAccordo.toString(), insAnag_Iscr);
	}

	public void annullaDomanda(String cdnUt) throws CigException, SQLException {
		_logger.info("annullamento domanda precedentemente acquisita");
		String stmProcedure = "{ call ? := PG_GESTIONE_CIG.annullaDomanda(?,?) }";
		CallableStatement command = null;
		ResultSet dr = null;
		command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
		command.registerOutParameter(1, Types.VARCHAR);
		command.setString(2, ciAccBean.getCodaccordo());
		command.setString(3, cdnUt);
		dr = command.executeQuery();
		String codiceRit = command.getString(1);
		dr.close();
		command.close();
		if ("-20001".equals(codiceRit))
			throw new CigException(codiceRit,
					"Impossibile trovare l'accordo " + ciAccBean.getCodaccordo() + " da annullare.");
		if ("-20002".equals(codiceRit))
			throw new CigException(codiceRit,
					"Trovati troppi accordi con codice " + ciAccBean.getCodaccordo() + " da annullare.");
		if (!"0".equals(codiceRit))
			throw new CigException(codiceRit, "Errore nell'annullamento della domanda.");
	}

	/**
	 * Annulla i lavoratori, impostandone lo stato a 'stato'.<br/>
	 * Che lavoratori annulla? Annulla tutti i lavoratori collegati alla domanda su DB.<br/>
	 * Ovviamente devono già essere stati inseriti i nuovi lavoratori presenti nella nuova domanda.<br/>
	 * Nel caso in cui lo stato da impostare sia
	 * 
	 * @param cdnUt
	 * @param stato
	 *            nuovo stato dei lavoratori
	 * @throws Exception
	 */
	public void annullaLavoratori(String cdnUt, String stato) throws Exception {
		String codAccordoOrig = "";

		if (stato.equals(CigConst.ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE)) {
			codAccordoOrig = ciAccBean.getCodaccordo();
		} else if (stato.equals(CigConst.ISCR_ANNULLAMENTO_PER_RETTIFICA)) {
			codAccordoOrig = ciAccBean.getCodaccordoorig();
		} else if (stato.equals(CigConst.ISCR_ANNULLAMENTO_PER_ANNULLAMENTO_DOMANDA)) {
			codAccordoOrig = ciAccBean.getCodaccordo();
		}

		/* 27052010 Rodi - voglio vedere quali lavoratori sta annullando. */
		/* 06072010 Rodi - corretto meccanismo ricerca iscrizioni da annullare */
		Object[] parametri_ricerca = new Object[2];
		parametri_ricerca[0] = codAccordoOrig; // devo annullare quelli collegati alla domanda con questo codice
		parametri_ricerca[1] = prgAccordo; // me con prgaccordo diverso da quello della domanda attuale
		SourceBean lavs = (SourceBean) tex.executeQuery("CI_LAVDAANNULLARE", parametri_ricerca, "SELECT");

		Vector<SourceBean> lista = lavs.getAttributeAsVector("ROW");
		if (!lista.isEmpty()) {
			for (SourceBean lavoratore : lista) {
				String cf = (String) lavoratore.getAttribute("STRCODICEFISCALE");
				BigDecimal prgAltraIscr = (BigDecimal) lavoratore.getAttribute("PRGALTRAISCR");

				_logger.info("annullo l'iscrizione " + prgAltraIscr + " per il lavoratore " + cf);

				Object[] parametri_annullamento = new Object[3];
				parametri_annullamento[0] = stato; // stato da inserire.
				parametri_annullamento[1] = cdnUt; // utente
				parametri_annullamento[2] = prgAltraIscr; // prg iscrizione da annullare.

				tex.executeQuery("CI_ANNULLAISCR", parametri_annullamento, "UPDATE");

			}
		}
	}

	/**
	 * Rettifica la domanda collegata alla presente impostandole il nuovo codStatoAtto.
	 * 
	 * @param cdnUt
	 * @param codStato
	 *            nuovo codStatoAtto da impostare
	 * @throws Exception
	 *             nel caso in cui qualcosa vada storto
	 */
	public void rettificaDomanda(String cdnUt, String codStato) throws Exception {
		String stmProcedure = "{ call ? := PG_GESTIONE_CIG.rettificaDomanda(?,?,?,?) }";
		CallableStatement command = null;
		ResultSet dr = null;

		_logger.info("Rettifica domanda.codaccordo:" + ciAccBean.getCodaccordoorig() + ",prgaccordo:"
				+ ciAccBean.getPrgaccordo() + ",codstato:" + codStato);
		command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
		command.registerOutParameter(1, Types.VARCHAR);
		command.setString(2, ciAccBean.getCodaccordoorig());
		command.setString(3, codStato);
		command.setString(4, ciAccBean.getPrgaccordo());
		command.setString(5, cdnUt);

		dr = command.executeQuery();

		String codiceRit = command.getString(1);
		dr.close();
		command.close();

		if (codiceRit.equals("-20004"))
			throw new CigException(codiceRit, "Impossibile trovare l'accordo da rettificare.");
		if (!codiceRit.equals("0"))
			throw new CigException(codiceRit, "Errore nella rettifica dell' accordo.");
	}

	/**
	 * Aggiorna la domanda cambiandone il codStatoAtto nella tabella CI_ACCORDO
	 * 
	 * @param cdnUt
	 *            codice utente che esegue la modifica
	 * @param codStato
	 *            nuovo codStatoAtto con cui aggiornare il record
	 * @throws SQLException
	 * @throws CigException
	 */
	public void variaDomanda(String cdnUt, String codStato) throws CigException, SQLException {
		String stmProcedure = "{ call ? := PG_GESTIONE_CIG.variaDomanda(?,?,?,?) }";
		CallableStatement command = null;
		ResultSet dr = null;

		_logger.info("Variazione domanda.codaccordo:" + ciAccBean.getCodaccordo() + ",prgaccordo:"
				+ ciAccBean.getPrgaccordo() + ",codstato:" + codStato);
		command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
		command.registerOutParameter(1, Types.VARCHAR);
		command.setString(2, ciAccBean.getCodaccordo());
		command.setString(3, codStato);
		command.setString(4, ciAccBean.getPrgaccordo());
		command.setString(5, cdnUt);

		dr = command.executeQuery();

		String codiceRit = command.getString(1);
		dr.close();
		command.close();

		if (codiceRit.equals("-20004"))
			throw new CigException(codiceRit, "Impossibile trovare l'accordo da variare.");
		if (!codiceRit.equals("0"))
			throw new CigException(codiceRit, "Errore nella variazione dell' accordo.");
	}

	/**
	 * Aggiorna lo stato di un lavoratore. Viene di solito chiamata dalla funzione rettificaLavoratori() che esegue la
	 * rettifica su tutti i lavoratori collegati alla domanda. Questa funzione richiama la funzione
	 * PG_GESTIONE_CIG.rettificaLavoratore che si occupa della rettifica sulla tabella AM_ALTRA_ISCR.
	 * 
	 * @param lav
	 *            lavoratore da aggiornare
	 * @param cdnUt
	 *            codice utente che effettua la modifica
	 * @param codStato
	 *            stato della domanda di cui si vogliono rettificare i lavoratori.
	 * @throws Exception
	 */
	private void rettificaLavoratore(CILavoratoreBean lav, String cdnUt, String codStato) throws Exception {
		String codStatoIscr = "";
		String codAccordoOrig = "";

		String nuovaDataLicenziamento;
		String nuovoFlgdirittoDO;
		String nuovoCodMotivoNotDO;

		String nuovaDatInizioCig = lav.getDatInizioCig();
		String nuovaDatFineCig = lav.getDatFineCig();

		// in generale non aggiorno questi tre campi...li aggiorno solo in caso di rettifica...vedi sotto
		nuovaDataLicenziamento = null;
		nuovoFlgdirittoDO = "0";
		nuovoCodMotivoNotDO = "0";

		// CASO C: VARIAZIONE
		if (codStato.equals(CigConst.ACC_CHIUSO)) {
			codAccordoOrig = ciAccBean.getCodaccordo();
			if (this.ciAccBean.getCodtipoconcessione().equals(CigConst.CONC_NO)
					|| (lav.getDatInizioCig() == null || lav.getDatFineCig() == null)) {
				codStatoIscr = CigConst.ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE;
				_logger.info("Lo stato dell'iscrizione per il lavoratore " + lav.getStrcodicefiscale()
						+ " è ANNULLAMENTO PER NON APPROVAZIONE");
			} else {
				codStatoIscr = "0"; // nel caso in cui il codtipoconcessione sia diverso da N,
									// lo stato dell'iscrizione va lasciato inalterato, quindi
									// passo "0" come stato per rendermi conto successivamente di
									// tale situazione.
				_logger.info("Lo stato dell'iscrizione per il lavoratore " + lav.getStrcodicefiscale()
						+ " rimarrà invariato");
			}

			// se la domanda è di tipo mobilità in deroga segnalo che i campi non verranno aggiornati
			// e nemmeno datInizio e datFine
			if (CigConst.AM_MOBILITA.equals(this.ciAccBean.getCodtipoaccordo())) {
				_logger.info(
						"I campi della domanda in deroga non verranno aggiornati e, in quanto variazione, nemmeno datInizio e datFine");
				nuovaDatInizioCig = null;
				nuovaDatFineCig = null;
			}

		}
		// CASO B: RETTIFICA
		else if (codStato.equals(CigConst.ACC_RETTIFICATO)) {
			codAccordoOrig = ciAccBean.getCodaccordoorig(); // codice accordo originale

			// se il tipo concessione vale NO oppure la data inizio o data fine calcolati sono nulli
			if (this.ciAccBean.getCodtipoconcessione().equals(CigConst.CONC_NO)
					|| (lav.getDatInizioCig() == null || lav.getDatFineCig() == null)) {
				// imposto lo stato come ANNULLAMENTO CIG PER NON APPROVAZIONE
				codStatoIscr = CigConst.ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE;
				_logger.info("Lo stato dell'iscrizione per il lavoratore " + lav.getStrcodicefiscale()
						+ " è ANNULLAMENTO PER NON APPROVAZIONE");
			} else {
				// imposto lo stato come valido
				codStatoIscr = "";
				_logger.info("Lo stato dell'iscrizione per il lavoratore " + lav.getStrcodicefiscale() + " è VALIDO");
			}

			// nelle domande cig in mobilità in deroga aggiorno i tre campi specifici
			if (CigConst.AM_MOBILITA.equals(this.ciAccBean.getCodtipoaccordo())) {
				// in caso di variazione non aggiorno questi tre campi
				nuovaDataLicenziamento = lav.getDatLicenziamento();
				nuovoFlgdirittoDO = lav.getFlgDirittoDO();
				nuovoCodMotivoNotDO = lav.getCodMotivoNotDO();

				_logger.info("I campi della domanda in deroga verranno aggiornati:\n" + "datLicenziamento: "
						+ nuovaDataLicenziamento + "\n" + "flgdirittodo: " + nuovoFlgdirittoDO + "\n"
						+ "codMotivoNotDO: " + nuovoCodMotivoNotDO);
			}

		}

		// 240310 Rodi - modifico il codtipoaccordo e prgunita ed inserisco l'ultimo ricevuto
		// aggiorno il tipo di iscrizione
		String codtipoaccordo = ciAccBean.getCodtipoaccordo();
		// aggiorno l'unità operativa legata all'iscrizione
		String prgunitastr = lav.getPrgunita();

		String stmProcedure = "{ call ? := PG_GESTIONE_CIG.rettificaLavoratore(?, ?,  ?, ?, ?, ?, ?, ?, ?, ?,?,?,?) }";
		CallableStatement command = null;
		ResultSet dr = null;
		command = tex.getDataConnection().getInternalConnection().prepareCall(stmProcedure);

		_logger.info("Rettifica lavoratore " + lav.getStrcodicefiscale() + ".Parametri:\n" + "codAccordoOrig: "
				+ codAccordoOrig + ";\n" + "codtipoaccordo: " + codtipoaccordo + ";\n" + "prgAccordo: "
				+ lav.getPrgaccordo() + ";\n" + "nuovaDatInizioCig: " + nuovaDatInizioCig + ";\n" + "nuovaDatFineCig: "
				+ nuovaDatFineCig + ";\n" + "codStato: " + codStato + ";\n" + "codStatoIscr: " + codStatoIscr + ";\n"
				+ "prgunitastr: " + prgunitastr + ";\n");
		{
			int j = 1;
			command.registerOutParameter(j++, Types.VARCHAR);

			// codice accordo al quale è legato il lavoratore nella tabella CI_LAVORATORE
			command.setString(j++, codAccordoOrig);
			// nuovo codTipoIscr da inserire nella tabella AM_ALTRA_ISCR
			command.setString(j++, codtipoaccordo);
			// codice fiscale del lavoratore da aggiornare
			command.setString(j++, lav.getStrcodicefiscale());
			// nuovo prgAccordo da impostare
			command.setString(j++, lav.getPrgaccordo());
			// nuove date cig da impostare. se null, non vengono aggiornate
			command.setString(j++, nuovaDatInizioCig);
			command.setString(j++, nuovaDatFineCig);
			// codice stato atto nella CI_ACCORDO
			command.setString(j++, codStato);
			// nuovo codice stato iscr. se valorizzato a '0' non verrà aggiornato.
			command.setString(j++, codStatoIscr);
			command.setString(j++, cdnUt);
			// nuovo prgUnità
			command.setString(j++, prgunitastr);

			// nuova data licenziamento, se null non viene aggiornata.
			command.setString(j++, nuovaDataLicenziamento);
			// nuovo flag diritto DO, se '0' non viene aggiornato
			command.setString(j++, nuovoFlgdirittoDO);
			// nuovo codice motivo no DO, se '0' non viene aggiornato
			command.setString(j++, nuovoCodMotivoNotDO);

		}
		dr = command.executeQuery();
		String codiceRit = command.getString(1);
		dr.close();
		command.close();

		if ("1".equals(codiceRit)) {
			// Il codice di ritorno = 1 significa che il lavoratore non e' presente nell'iscrizione da
			// rettificare/variare,
			// quindi inseriamo il nuovo lavoratore sia nelle tabelle CIG che nell'anagrafica (se non gia' presente) e
			// in am_altraiscr.
			_logger.info("Il lavoratore " + lav.getStrcodicefiscale()
					+ " non era presente nella domanda che sto rettificando.");
			insertLavoratore(lav, this.ciAccBean.getCodtipoconcessione(), true, true);

			return;

		}
		if ("-20003".equals(codiceRit))
			throw new CigException(codiceRit, "Impossibile trovare l'accordo da rettificare/variare.");
		else if ("-2291".equals(codiceRit)) {
			throw new CigException(codiceRit,
					"Errore nell'aggiornamento su AM_ALTRA_ISCR.Dati non validi nella domanda.");
		} else if (!"0".equals(codiceRit))
			throw new CigException(codiceRit, "Errore nella rettifica/variazione dell'iscrizione.");

		_logger.info("La rettifica è andata a buon fine.");
		// inserisco il lavoratore nelle tabelle CIG
		insertLavoratore(lav, this.ciAccBean.getCodtipoconcessione(), true, false);
	}

	/**
	 * Questa funzione permette di rettificare le informazioni sui lavoratori collegati alla domanda
	 * 
	 * @param cdnUt
	 *            codice utente che ha eseguito la modifica
	 * @param codStato
	 *            stato della domanda. AS: variazione, AR: rettifica
	 * @param prgAccordo
	 * @throws Exception
	 */
	public void rettificaLavoratori(String cdnUt, String codStato, String prgAccordo) throws Exception {
		for (int i = 0; i < listaRelAccordo.size(); i++) {
			List<CILavoratoreBean> listalav = listaRelAccordo.get(i).getListaLavoratori();
			for (int j = 0; j < listalav.size(); j++) {
				listalav.get(j).setPrgaccordo(prgAccordo);
				rettificaLavoratore(listalav.get(j), cdnUt, codStato);
			}
		}
	}

	public static boolean matchIndirizzi(String indirizzo1, String indirizzo2) {
		return indirizzo1.equalsIgnoreCase(indirizzo2);
	}

	public CIAccordoBean getCiAccBean() {
		return ciAccBean;
	}

	public void setCiAccBean(CIAccordoBean ciAccBean) {
		this.ciAccBean = ciAccBean;
	}

	public CIEsameCongiuntoBean getCiEsameCong() {
		return ciEsameCong;
	}

	public void setCiEsameCong(CIEsameCongiuntoBean ciEsameCong) {
		this.ciEsameCong = ciEsameCong;
	}

	public AziendaBean getCiAzienda() {
		return ciAzienda;
	}

	public void setCiAzienda(AziendaBean ciAzienda) {
		this.ciAzienda = ciAzienda;
	}

	public List<CISindacato> getListaSindacati() {
		return listaSindacati;
	}

	public void setListaSindacati(List<CISindacato> listaSindacati) {
		this.listaSindacati = listaSindacati;
	}

	public List<CIRelAccordoAzBean> getListaRelAccordo() {
		return listaRelAccordo;
	}

	public void setListaRelAccordo(List<CIRelAccordoAzBean> listaRelAccordo) {
		this.listaRelAccordo = listaRelAccordo;
	}
}
