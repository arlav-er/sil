package it.eng.sil.module.ido;

import java.math.BigDecimal;
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
 * Accorpa due testate di azienda (esegue una stored procedure opportuna!) Specializza il mio modulo
 * "AbstractStoredProcModule".
 * 
 * @author Luigi Antenucci
 */
public class DoAccorpamentoTestata extends AbstractStoredProcModule {

	private static final long serialVersionUID = 1860891036593231338L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DoAccorpamentoTestata.class.getName());

	protected String className = StringUtils.getClassName(this);

	protected String getStoredProcStatement() {

		return SQLStatements.getStatement("IDO_DO_ACCORPAMENTO_TESTATA");
	}

	protected List<DataField> getAndSetStoredProcParameters(DataConnection connection, StoredProcedureCommand command) {

		// LETTURA DATI DI INPUT
		// prelevo la session ed il codice utente necessario per la stored
		// procedure
		Object cdUtente = session.getAttribute("_CDUT_");
		String prgAziendaAccorpante = (String) request.getAttribute("prgAziendaAccorpante");
		String prgAziendaAccorpata = (String) request.getAttribute("prgAziendaAccorpata");

		_logger.debug("prgAziendaAccorpante=" + prgAziendaAccorpante);

		_logger.debug("prgAziendaAccorpata=" + prgAziendaAccorpata);

		// CREAZIONE ARRAY PARAMETRI
		List<DataField> parameters = new ArrayList<DataField>(4);

		parameters.add(connection.createDataField("result", Types.BIGINT, null));
		command.setAsOutputParameters(0);
		parameters.add(connection.createDataField("ut", Types.BIGINT, cdUtente));
		command.setAsInputParameters(1);
		parameters.add(
				connection.createDataField("prgAziendaAccorpante", Types.BIGINT, new BigDecimal(prgAziendaAccorpante)));
		command.setAsInputParameters(2);
		parameters.add(
				connection.createDataField("prgAziendaAccorpata", Types.BIGINT, new BigDecimal(prgAziendaAccorpata)));
		command.setAsInputParameters(3);

		return parameters;
	}

	protected int getMessageCodesOK() {
		return MessageCodes.Accorpamento.ACCORPAMENTO_TESTATA_SUCCESS;
	}

	protected int getMessageCodesERR(String strResult) {
		if (strResult.equals("-2")) {
			return MessageCodes.Accorpamento.ACCORPAMENTO_FAIL_TESTATA_ISCRIZIONE_CIG;
		} else {
			return MessageCodes.Accorpamento.ACCORPAMENTO_TESTATA_FAIL;
		}
	}
}