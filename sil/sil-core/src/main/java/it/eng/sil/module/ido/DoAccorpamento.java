package it.eng.sil.module.ido;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractStoredProcModule;

/**
 * Accorpa due unità di un'azienda (esegue una stored procedure opportuna!) Specializza il mio (by Antenucci) modulo
 * "AbstractStoredProcModule".
 * 
 * @author Rolfini / Luigi Antenucci
 */
public class DoAccorpamento extends AbstractStoredProcModule {

	private static final long serialVersionUID = 4564992254977158192L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DoAccorpamento.class.getName());

	protected String className = StringUtils.getClassName(this);

	protected String getStoredProcStatement() {
		return SQLStatements.getStatement("IDO_DO_ACCORPAMENTO");
	}

	protected List<DataField> getAndSetStoredProcParameters(DataConnection connection, StoredProcedureCommand command) {

		// LETTURA DATI DI INPUT
		// prelevo la session ed il codice utente necessario per la stored
		// procedure
		Object cdUtente = session.getAttribute("_CDUT_");
		Object prgAzienda = request.getAttribute("prgAzienda");
		Object prgUnitaAccorpante = request.getAttribute("prgUnitaAccorpante");
		Object prgUnitaAccorpata = request.getAttribute("prgUnitaAccorpata");

		_logger.debug("prgAzienda=" + prgAzienda);

		_logger.debug("prgUnitaAccorpante=" + prgUnitaAccorpante);

		_logger.debug("prgUnitaAccorpata=" + prgUnitaAccorpata);

		// CREAZIONE ARRAY PARAMETRI
		List<DataField> parameters = new ArrayList<DataField>(5);

		parameters.add(connection.createDataField("result", Types.BIGINT, null));
		command.setAsOutputParameters(0);
		parameters.add(connection.createDataField("ut", Types.BIGINT, cdUtente));
		command.setAsInputParameters(1);
		parameters.add(connection.createDataField("prgAz", Types.BIGINT, prgAzienda));
		command.setAsInputParameters(2);
		parameters.add(connection.createDataField("prgUnitaAccorpante", Types.BIGINT, prgUnitaAccorpante));
		command.setAsInputParameters(3);
		parameters.add(connection.createDataField("prgUnitaAccorpata", Types.BIGINT, prgUnitaAccorpata));
		command.setAsInputParameters(4);

		return parameters;
	}

	protected int getMessageCodesOK() {
		return MessageCodes.Accorpamento.ACCORPAMENTO_SUCCESS;
	}

	protected int getMessageCodesERR(String strResult) {
		if (strResult.equals("-2")) {
			return MessageCodes.Accorpamento.ACCORPAMENTO_FAIL_UNI_ISCRIZIONE_CIG;
		} else {
			return MessageCodes.Accorpamento.ACCORPAMENTO_TESTATA_FAIL;
		}
	}

}