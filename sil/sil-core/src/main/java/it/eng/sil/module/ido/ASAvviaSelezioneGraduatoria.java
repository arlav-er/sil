package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/*
 * Modulo che si occupa di avviare a selezione i candidati selezionati
 * i cdnLavoratore dei candidati sono presenti nel vettore in Sessione
 * che ha come chiave la coppia di valori prgRichiestaAz_prgTipoIncrocio
 * 
 * alla stored procedure viene passato un Oracle.sqlArray con tutti i cdnLavoratore 
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ASAvviaSelezioneGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASAvviaSelezioneGraduatoria.class.getName());

	private String className = this.getClass().getName();
	// private OracleConnection oracleConn = null;
	private Vector vettCdnLavoratore = new Vector();
	private BigDecimal inputPrgRichiestaAz = new BigDecimal(0);
	private BigDecimal inputPrgTipoIncrocio = new BigDecimal(0);

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult ror = new ReportOperationResult(this, response);

		// recupero gli ultimi candidati checckati e setto il vettore
		// con tutti i nominativi che ho in sessione
		// XXX verifica se è meglio avere in sessione un SB oppure un Vector
		// getNominativiSelezionati(request, response);

		getNominativiSelezionatiVector(request, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		boolean success = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// chiamo la storedProcedure per l'avviamento a selezione dei
			// candidati
			success = avviaSelezioneAS();

			if (success) {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

				transExec.commitTransaction();
				ror.reportSuccess(idSuccess);
			} else {
				ror.reportFailure(idFail);
				ror.reportFailure(MessageCodes.General.INSERT_FAIL);
				if (transExec != null) {
					try {
						transExec.rollBackTransaction();
					} catch (EMFInternalError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASSalvaGraduatoria::service(): Impossibile inserire l'adesione!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			errors = true;
		}

	}

	/*
	 * richiama la stored Procedure passandogli come parametro l'Array di tutti i cdnLavoratori selezionati
	 */
	private boolean avviaSelezioneAS() {

		DataConnection dataConnection = null;
		oracle.jdbc.OracleConnection oracleConn = null;
		try {

			User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
			String p_cdnUtente = String.valueOf(user.getCodut());

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			java.sql.Connection connection = dataConnection.getInternalConnection();
			oracleConn = it.eng.sil.util.Utils.getOracleConnection(connection);
			
			ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);

			Object nominativi[] = vettCdnLavoratore.toArray();

			ARRAY arrayCdnLavoratore = new ARRAY(descriptor, oracleConn, nominativi);

			/*
			 * OraclePreparedStatement ps = (OraclePreparedStatement) oracleConn.prepareStatement( "begin
			 * PG_INCROCIO.ASInsertCandidatiGraduatoria(?, ?); end;");
			 */

			OracleCallableStatement cs = (OracleCallableStatement) oracleConn
					.prepareCall("begin ? := PG_INCROCIO.ASInsertCandidatiGraduatoria(?, ?, ?, ?); end;");

			cs.registerOutParameter(1, java.sql.Types.BIGINT);
			cs.setARRAY(2, arrayCdnLavoratore);
			cs.setBigDecimal(3, inputPrgRichiestaAz);
			cs.setBigDecimal(4, inputPrgTipoIncrocio);
			cs.setBigDecimal(5, new BigDecimal(p_cdnUtente));

			cs.execute();

			BigDecimal returnValue = ((OracleCallableStatement) cs).getBigDecimal(1);

			if (returnValue.compareTo(new BigDecimal(0)) != 0) {
				int msgCode = 0;
				String msg = null;
				switch (returnValue.intValue()) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "";

					break;
				case 1: // errore data pubblicazione
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Avvia i primi n candidati: la data di pubbllicazione è anteriore a 6 mesi";

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "";

				}

				return false;
			}
		} catch (EMFInternalError emfi) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", (Exception) emfi);

			return false;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			return false;
		} finally {
			// Chiudo tutto
			Utils.releaseResources(dataConnection, null, null);
			return true;
		}
	}

	/*
	 * metodo che si occupa di inserire nel SuorceBean in sessione i candidati checkati presenti nella ServiceRequest
	 * vengono eliminati da SourceBean quei candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE inoltre viene popolato il vettore vettCdnLavoratore con tutti i cdnLavoratore presenti in
	 * sessione
	 * 
	 */
	public void getNominativiSelezionati(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		SourceBean nominativiSB = (SourceBean) sessionContainer.getAttribute(keySB);
		if (nominativiSB == null) {
			try {
				nominativiSB = new SourceBean("CANDIDATI_SEL");
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					String ckSel = (String) request.getAttribute(keyAttr);

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un sourcebean
						ASCandidatiGraduatoriaUtil.insertNominativoSB(valueCdnLavoratore, nominativiSB);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		SourceBean newNominativiSB = null;
		try {
			newNominativiSB = new SourceBean("CANDIDATI_SEL");
			Vector newRows = ASCandidatiGraduatoriaUtil.deleteNominativoSB(request, nominativiSB);
			for (int i = 0; i < newRows.size(); i++) {
				SourceBean newRow = (SourceBean) newRows.get(i);
				newNominativiSB.setAttribute(newRow);
			}
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setta il SB in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiSB);

		inputPrgRichiestaAz = new BigDecimal(numRichiesta);

		// carico il vettore con i nominativi inseriti nel sourcebean caricsato
		// in session
		Vector vectRows = newNominativiSB.getAttributeAsVector("ROW");

		for (int j = 0; j < vectRows.size(); j++) {
			String prgNom = (String) ((SourceBean) vectRows.get(j)).getAttribute("CDNLAVORATORE");
			vettCdnLavoratore.add(j, prgNom);
		}
	}

	/*
	 * metodo che si occupa di inserire nel vettore in sessione i candidati checkati presenti nella ServiceRequest
	 * vengono eliminati dal Vector quei candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE inoltre viene popolato il vettore vettCdnLavoratore con tutti i cdnLavoratore presenti in
	 * sessione
	 * 
	 */
	public void getNominativiSelezionatiVector(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		Vector nominativiVector = (Vector) sessionContainer.getAttribute(keySB);
		if (nominativiVector == null) {
			nominativiVector = new Vector();
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					String ckSel = (String) request.getAttribute(keyAttr);

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un sourcebean
						ASCandidatiGraduatoriaUtil.insertNominativoVector(valueCdnLavoratore, nominativiVector);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		Vector newNominativiVector = null;
		newNominativiVector = ASCandidatiGraduatoriaUtil.deleteNominativoVector(request, nominativiVector);

		// setta il SB in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiVector);

		inputPrgRichiestaAz = new BigDecimal(numRichiesta);
		inputPrgTipoIncrocio = new BigDecimal(prgTipoIncrocio);

		// carico il vettore con i nominativi inseriti nel sourcebean caricsato
		// in session
		vettCdnLavoratore = newNominativiVector;

	}

}