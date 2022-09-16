package it.eng.sil.mycas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sil.dto.StNotiziaDTO;

public class NotizieBean {
	private static final String JNDI_DS = "java:/jdbc/MyCasDS";
	private static final Logger LOG = LoggerFactory.getLogger(NotizieBean.class);
	
	public static final String AGG = "AGG";
	public static final String AVV = "AVV";
	public static final String INT = "INT";
	public static final String STO = "STO";
	
	private final static String QUERY_ST_NOTIZIA = "SELECT oggetto, contenuto, to_char(dtm_pubblicazione, 'dd/mm/yyyy') as data_pubbl " +
			"FROM mycas.st_notizia " + 
			"WHERE dtm_scadenza >= current_date " +
			"ORDER by ordine";
	
	public NotizieBean(){
		
	}
	
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(JNDI_DS);
			conn = dbDS.getConnection();
		} catch (Exception dbEx) {
			LOG.error("Errore nel reperimento della connessione da JNDI: " + JNDI_DS, dbEx);
			throw new SQLException("Eccezione nell'accesso al database. " + dbEx.getMessage());
		}
		return conn;
	}

	@Deprecated
	public String getNotizie() {

		StringBuilder notizie = new StringBuilder();
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement("select oggetto, contenuto, to_char(dtm_pubblicazione, 'dd/mm/yyyy') as data_pubbl from mycas.st_notizia where dtm_scadenza >= current_date order by ordine");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String contenuto = rs.getString("contenuto");
				String oggetto = rs.getString("oggetto");
				String data_pubbl = rs.getString("data_pubbl");
				
				String riga =String.format("<li><b>%s - %s:</b> %s</li>", oggetto, data_pubbl, contenuto);        		
						
				notizie.append(riga);
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			notizie = new StringBuilder("AVVISI NON DISPONIBILI");
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("Errore nel reperimento degli avvisi", e);
					notizie = new StringBuilder("AVVISI NON DISPONIBILI");
				}

		}
		return notizie.toString();

	}
	
	//Passare le costanti definite sopra (AGG, AVV, INT, STO, ...) come parametro formale.
	//In assenza di parametri formali viene invocata la select senza condizioni where
	//Facile no? ;)
	public List<StNotiziaDTO> getStNotiziaDTOs(String... codTipoMessaggio){
		List<StNotiziaDTO> stNotiziaDTOList = new ArrayList<StNotiziaDTO>();
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = (codTipoMessaggio == null || codTipoMessaggio.length==0) ? conn.prepareStatement(QUERY_ST_NOTIZIA) : conn.prepareStatement(buildQueryStNotiziaByCodTipoMessaggio(codTipoMessaggio));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				StNotiziaDTO stNotiziaDTO = new StNotiziaDTO();
				stNotiziaDTO.setContenuto(rs.getString("contenuto"));
				stNotiziaDTO.setOggetto(rs.getString("oggetto"));
				stNotiziaDTO.setDtmPubblicazione(rs.getString("data_pubbl"));
				
				stNotiziaDTOList.add(stNotiziaDTO);
			}
			rs.close();
			pstmt.close();
		} catch (Exception ex) {
			stNotiziaDTOList = new ArrayList<StNotiziaDTO>();
		} finally {
			if (conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("Errore nel reperimento degli avvisi", e);
					stNotiziaDTOList = new ArrayList<StNotiziaDTO>();
				}
			}
		}
		return stNotiziaDTOList;
	}
	
	private String getParamsInOR(String[] codTipoMessaggio){
		String paramsInOr = "";
		for (int index = 0; index < codTipoMessaggio.length; index++) {
			if(index == 0){
				paramsInOr = String.format("cod_tipo_messaggio='%s'", codTipoMessaggio[index]);				
			}else{
				paramsInOr += " OR " + String.format("cod_tipo_messaggio='%s'", codTipoMessaggio[index]);
			}
		}
		return paramsInOr;
	}
	
	private String buildQueryStNotiziaByCodTipoMessaggio(String[] codTipoMessaggio){
		String paramsInOr = getParamsInOR(codTipoMessaggio);
		String queryStNotiziaByCodTipoMessaggio = String.format("SELECT oggetto, contenuto, to_char(dtm_pubblicazione, 'dd/mm/yyyy') as data_pubbl " +
				"FROM mycas.st_notizia " + 
				"WHERE dtm_scadenza >= current_date " +
				"AND (%s) " + 
				"ORDER by ordine;", paramsInOr);
		return queryStNotiziaByCodTipoMessaggio;
	}

}
