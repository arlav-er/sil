package it.eng.sil.batch.mdb;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/BatchQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName = "maxMessages", propertyValue = "1") })
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class BatchMDB implements MessageListener {
	protected Log log = LogFactory.getLog(this.getClass());

	public void onMessage(Message message) {

		BatchObject batchObject = null;

		try {
			ObjectMessage msg = (ObjectMessage) message;
			batchObject = (BatchObject) msg.getObject();

			log.info("Inizio elaborazione messaggio Batch MDB. Params = " + batchObject);

			if (batchObject != null && batchObject.getClassName() != null) {
				execBatch(batchObject);
			} else {
				log.error("Numero di argomenti non valido");
				return;
			}

			log.info("Fine elaborazione messaggio Batch MDB. Params = " + batchObject);

		} catch (Exception e) {
			log.error("Errore grave sulla coda dei batch. Impossibile elaborare il messaggio = " + batchObject, e);
		}
	}

	private void execBatch(BatchObject batchObject)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		String className = batchObject.getClassName();

		// log.debug("ClassName: " + className);

		List<Class<?>> interfaces = Arrays.asList(Class.forName(className).getInterfaces());

		if (interfaces.contains(IBatchMDBConsumer.class)) {
			// La classe implementa l'interfaccia IBatchMDBConsumer, è possibile consumare il messaggio.
			// Il costruttore che deve essere presente è quello che ha un argomento di tipo BatchObject
			IBatchMDBConsumer batchConsumer = (IBatchMDBConsumer) Class.forName(className)
					.getConstructor(BatchObject.class).newInstance(new Object[] { batchObject });

			batchConsumer.execBatch();
		}

	}
}
