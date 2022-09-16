package it.eng.sil.myauthservice.model;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;


@Singleton
@Startup
public class UtilsEJB {
	private static final Log log = LogFactory.getLog(UtilsEJB.class);
	String cachedSchema;
	String cachedDb;
	String cachedVersion;
	String cachedSize;
	String cachedAddress;

	@PersistenceContext
	protected EntityManager entityMan;

	// timestamp di quando è stata inizializzata l'applicazione
	private long startTime;

	// relies on maven filter, the const is being replaced at compile time with the correct ${project.version}
	private String myAuthVersion;

	// relies on MANUAL updates, to verify DB is updated
	private String myAuthDBVersion;

	@EJB
	DeSistemaEJB deSistemaEJB;

	@EJB
	ConstantsSingleton constantsSingleton;

	@PostConstruct
	public void init() {
		startTime = new Date().getTime();

		// On startup, update versione_pom
		synchVersionePom();
		// and read dVersion, they should be equal
		readVersioneDb();

		if (myAuthDBVersion == null || myAuthDBVersion == null || !myAuthDBVersion.equalsIgnoreCase(myAuthVersion))
			log.error("Magari va bene così, ma la versione del DB è diversa da quella dichiarata sul pom: "
					+ myAuthDBVersion);
	}

	public String getCurrentSchema() {
		if (cachedSchema == null) {
			Query query = entityMan.createNativeQuery("select current_schema()");
			cachedSchema = ((String) query.getSingleResult());
		}
		return cachedSchema;
	}

	public String getDbAddress() {

		if (cachedAddress == null) {
			org.hibernate.engine.spi.SessionImplementor sessionImp = (org.hibernate.engine.spi.SessionImplementor) entityMan
					.getDelegate();
			try {
				DatabaseMetaData metadata = sessionImp.connection().getMetaData();
				cachedAddress = metadata.getURL();
			} catch (SQLException e) {
				log.error("Impossibile ricavare i metadati di connessione");
			}
		}
		return cachedAddress;
	}

	public String getCurrentDb() {
		if (cachedDb == null) {
			Query query = entityMan.createNativeQuery("select current_database()");
			cachedDb = ((String) query.getSingleResult());
		}
		return cachedDb;
	}

	public String getDbVersion() {
		if (cachedVersion == null) {
			Query query = entityMan.createNativeQuery("select version()");
			cachedVersion = ((String) query.getSingleResult());
		}
		return cachedVersion;
	}

	public String getSchemaTotalSize() {
		if (cachedSize == null) {
			Query query = entityMan
					.createNativeQuery("SELECT  pg_size_pretty(CAST(sum(pg_total_relation_size(C.oid)) AS bigint))"
							+ "	  FROM pg_class C" + "	  LEFT JOIN pg_namespace N ON (N.oid = C.relnamespace)"
							+ "	  WHERE nspname NOT IN ('pg_catalog', 'information_schema')"
							+ "	    AND C.relkind <> 'i'" + "	    AND nspname !~ '^pg_toast';");
			cachedSize = ((String) query.getSingleResult());
		}
		return cachedSize;

	}
 

	private void synchVersionePom() {
		try {
			this.myAuthVersion = constantsSingleton.getBuildVersion();
			deSistemaEJB.synchVersionePom(DeSistemaEnum.MYAUTH.toString(), myAuthVersion);
		} catch (MyCasException e) {
			log.error("Errore nell'aggiornamento DeSistema per versione MyStage (dal pom) " + e.getMessage());
		}
	}

	private void readVersioneDb() {
		try {
			DeSistema ioStesso = deSistemaEJB.findById(DeSistemaEnum.MYAUTH.toString());
			myAuthDBVersion = ioStesso.getVersioneDb();
		} catch (MyCasException e) {
			log.error("Errore nel recupero versione DB " + e.getMessage());
			myAuthDBVersion = "?";
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getMyAuthVersion() {
		return myAuthVersion;
	}

	public void setMyAuthVersion(String myStageVersion) {
		this.myAuthVersion = myStageVersion;
	}

	public String getMyAuthDbVersion() {
		return myAuthDBVersion;
	}

	 
}
