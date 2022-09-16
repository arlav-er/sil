package it.eng.myportal.entity.home;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.ConfigurazioneDatabaseDTO;
import it.eng.myportal.dtos.ConfigurazioneWsEndpointDTO;
import it.eng.myportal.entity.WsEndpoint;

@Stateless
public class ConfigurazioneAmbienteHome {
	private static final String JAVA_JDBC_MY_PORTAL_DS = "java:/jdbc/MyPortalDS";

	protected Log log = LogFactory.getLog(this.getClass());
	
	@PersistenceContext
	protected EntityManager entityManager;

	public List<ConfigurazioneDatabaseDTO> getConfigurazioniHibernate() {
		List<ConfigurazioneDatabaseDTO> results = new ArrayList<ConfigurazioneDatabaseDTO>();

		// db c3p0 pool from persistence.xml
//		EntityManagerFactory emf = entityManager.getEntityManagerFactory();
//		String connectionUrl = emf.getProperties().get("hibernate.connection.url").toString();
//		String username = emf.getProperties().get("hibernate.connection.username").toString();
//		String maxNumConnessioniPool = emf.getProperties().get("hibernate.c3p0.max_size").toString();

//		BigInteger connessioniAperte = this.getConnessioniAttiveDB(
//				connectionUrl.substring(connectionUrl.lastIndexOf("/") + 1), username);

//		ConfigurazioneDatabaseDTO confDatabaseApplicativo = new ConfigurazioneDatabaseDTO();
//		confDatabaseApplicativo.setUrl(connectionUrl);
//		confDatabaseApplicativo.setUsername(username);
//		confDatabaseApplicativo.setNumMaxConnessioni(maxNumConnessioniPool);
//		confDatabaseApplicativo.setNumConnAperte(connessioniAperte.toString());
//		confDatabaseApplicativo.setDescrizione("Applicativo");
//
//		results.add(confDatabaseApplicativo);
		Connection connection=null;
		try {

			// db per sicurezza da datasource jboss
			Context context = new InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource) context.lookup(JAVA_JDBC_MY_PORTAL_DS);
			connection = ds.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			
			
			String connectionUrl = metaData.getURL();
			String username = metaData.getUserName();
			String maxNumConnessioniPool = Integer.valueOf(metaData.getMaxConnections()).toString();

			BigInteger connessioniAperte = this.getConnessioniAttiveDB(
					connectionUrl.substring(connectionUrl.lastIndexOf("/") + 1), username);

			ConfigurazioneDatabaseDTO confDatabaseSicurezza = new ConfigurazioneDatabaseDTO();
			confDatabaseSicurezza.setUrl(connectionUrl);
			confDatabaseSicurezza.setUsername(username);
			confDatabaseSicurezza.setNumMaxConnessioni(maxNumConnessioniPool);
			confDatabaseSicurezza.setNumConnAperte(connessioniAperte.toString());
			confDatabaseSicurezza.setDescrizione("DB dell'app. server (look-up di '" + JAVA_JDBC_MY_PORTAL_DS + "')");

			results.add(confDatabaseSicurezza);

		} catch (NamingException e) {
			log.error("Errore nel recupero della risorsa JNDI del DB [" + JAVA_JDBC_MY_PORTAL_DS + "]: " + e.getMessage() );
		} catch (Exception e) {
			log.error("Errore: " + e.getMessage());
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				log.error(e);
			}
		}

		return results;
	}

	private BigInteger getConnessioniAttiveDB(String nomeDb, String username) {

		Query query = entityManager
				.createNativeQuery("SELECT count(*) as open_connection FROM pg_stat_activity where datname = :parNomeDb and usename = :parUsername");
		query.setParameter("parNomeDb", nomeDb);
		query.setParameter("parUsername", username);

		BigInteger connessioniAperte = (BigInteger) query.getSingleResult();
		return connessioniAperte;
	}

	public List<ConfigurazioneWsEndpointDTO> getWebServiceEndpoint() {
		List<ConfigurazioneWsEndpointDTO> results = new ArrayList<ConfigurazioneWsEndpointDTO>();
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> query = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);	
		
		TypedQuery<WsEndpoint> q = entityManager.createQuery(query);
		List<WsEndpoint> endpoints = q.getResultList();
		
		for(WsEndpoint endpoint: endpoints){
			ConfigurazioneWsEndpointDTO dto = new ConfigurazioneWsEndpointDTO();
			dto.setDescrizioneServizio(endpoint.getDeTipoServizio().getDescrizione());
			dto.setWsUrl(endpoint.getAddress());
			dto.setDescrProvincia(endpoint.getDeProvincia().getDenominazione());
			results.add(dto);
		}
		return results;	
		
	}

}
