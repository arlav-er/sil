package it.eng.myportal.beans.statistic;

import it.eng.myportal.utils.ConstantsSingleton;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class StatitisticsEJB {

	protected final Log log = LogFactory.getLog(StatitisticsEJB.class);

	@PersistenceContext
	protected EntityManager entityManager;

	private String STATI_ADESIONE_QUERY = " select count(distinct y.codice_fiscale), y.cod_stato_adesione_min, a.descrizione "
			+ " from myportal.yg_adesione y "
			+ " inner join myportal.de_stato_adesione_min a on a.cod_stato_adesione_min = y.cod_stato_adesione_min "
			+ " where y.flg_adesione='Y' "
			+ " and cod_regione_rif_notifica='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' "
			+ " and dt_adesione <= (current_date - 1) "
			+ " group by a.descrizione, y.cod_stato_adesione_min "
			+ " order by  a.descrizione asc, y.cod_stato_adesione_min ";

	private String NUM_ADESIONE_TOT_QUERY = " select count(codice_fiscale) " + " from yg_adesione "
			+ " where flg_adesione='Y' " + " and cod_regione_rif_notifica = '" + ConstantsSingleton.COD_REGIONE + "' "
			+ " and dt_adesione <= (current_date - 1) ";

	private String NUM_ADESIONE_DIVISE_REGIONE_QUERY = "select " + " (case  " + " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente in Regione'  "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione'  "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente Fuori Regione' " + " else 'Non rilevato' "
			+ " end) as Residenza, " + " count(1) as numAdesioni " + " from myportal.yg_adesione y "
			+ " left join myportal.de_comune a_cres on (y.cod_com_res_rif_notifica=a_cres.cod_com) "
			+ " left join myportal.de_provincia a_pres on (a_cres.cod_provincia=a_pres.cod_provincia) "

			+ " left join mycas.utente_info u  on (y.id_pf_principal=u.id_pf_principal) "
			+ " left join myportal.de_comune p_cres on (u.cod_com_residenza=p_cres.cod_com) "
			+ " left join myportal.de_provincia p_pres on (p_cres.cod_provincia=p_pres.cod_provincia) "
			+ " where flg_adesione='Y' " + " and cod_regione_rif_notifica='" + ConstantsSingleton.COD_REGIONE + "' "
			+ " and dt_adesione <= (current_date - 1) " + " group by  " + " (case  " + " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente in Regione'  "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' " + " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente Fuori Regione' " + " else 'Non rilevato' " + " end)";

	private String NUM_ADESIONE_DIVISE_REGIONE_PER_STATO_QUERY = "select " + " (case  "
			+ " when a_pres.cod_regione='"+ ConstantsSingleton.COD_REGIONE+"' then 'Residente in Regione' "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " else 'Non rilevato' "
			+ " end) as Residenza, "
			+ " d.descrizione, "
			+ " count(1) as numAdesioniConAppuntamento "
			+ " from myportal.yg_adesione y "
			+ " inner join myportal.de_stato_adesione_min d on d.cod_stato_adesione_min = y.cod_stato_adesione_min "
			+ " left join myportal.de_comune a_cres on (y.cod_com_res_rif_notifica=a_cres.cod_com) "
			+ " left join myportal.de_provincia a_pres on (a_cres.cod_provincia=a_pres.cod_provincia) "
			+ " left join mycas.utente_info u  on (y.id_pf_principal=u.id_pf_principal) "
			+ " left join myportal.de_comune p_cres on (u.cod_com_residenza=p_cres.cod_com) "
			+ " left join myportal.de_provincia p_pres on (p_cres.cod_provincia=p_pres.cod_provincia) "
			+ " where flg_adesione='Y' "
			+ " and cod_regione_rif_notifica='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' "
			+ " and dt_adesione <= (current_date - 1) "
			+ " and dt_presa_in_carico is not null "
			+ " group by "
			+ " (case "
			+ " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " else 'Non rilevato' "
			+ " end),d.descrizione ";

	private String NUM_ADESIONI_NON_ASSEGNATE = "select count(codice_fiscale) as TOT_ADESIONI_NON_ASSEGNATE "
			+ " from myportal.yg_adesione y "
			+ " left join myportal.de_provincia p on p.cod_provincia = y.cod_provincia_rif_notifica "
			+ " where cod_regione_rif_notifica = '" + ConstantsSingleton.COD_REGIONE + "' "
			+ " and (p.cod_regione != '" + ConstantsSingleton.COD_REGIONE + "' or p.cod_regione is null) "
			+ " and flg_adesione = 'Y' " + " and dt_adesione <= (current_date - 1)";

	private String NUM_ADESIONE_APPUNTAMENTO = "select " + " count(1) as num_Adesioni_Con_Appuntamento "
			+ " from myportal.yg_adesione y " + " "
			+ " left join myportal.de_comune a_cres on (y.cod_com_res_rif_notifica=a_cres.cod_com) "
			+ " left join myportal.de_provincia a_pres on (a_cres.cod_provincia=a_pres.cod_provincia) " + "  "
			+ " left join mycas.utente_info u  on (y.id_pf_principal=u.id_pf_principal) "
			+ " left join myportal.de_comune p_cres on (u.cod_com_residenza=p_cres.cod_com) "
			+ " left join myportal.de_provincia p_pres on (p_cres.cod_provincia=p_pres.cod_provincia) "
			+ " where flg_adesione='Y' " + " and cod_regione_rif_notifica='" + ConstantsSingleton.COD_REGIONE + "' "
			+ " and dt_adesione <= (current_date - 1) " + " and dt_presa_in_carico is not null";

	private String NUM_ADESIONE_APPUNTAMENTO_DIVISE_REGIONE = "select " + " (case  " + " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente in Regione' "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' " + " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente Fuori Regione' " + " else 'Non rilevato' "
			+ " end) as Residenza, " + " count(1) as numAdesioniConAppuntamento " + " from myportal.yg_adesione y "
			+ " " + " left join myportal.de_comune a_cres on (y.cod_com_res_rif_notifica=a_cres.cod_com) "
			+ " left join myportal.de_provincia a_pres on (a_cres.cod_provincia=a_pres.cod_provincia) " + " "
			+ " left join mycas.utente_info u  on (y.id_pf_principal=u.id_pf_principal) "
			+ " left join myportal.de_comune p_cres on (u.cod_com_residenza=p_cres.cod_com) "
			+ " left join myportal.de_provincia p_pres on (p_cres.cod_provincia=p_pres.cod_provincia) "
			+ " where flg_adesione='Y' " + " and cod_regione_rif_notifica='" + ConstantsSingleton.COD_REGIONE + "' "
			+ " and dt_adesione <= (current_date - 1) " + " and dt_presa_in_carico is not null " + " group by "
			+ " (case " + " when a_pres.cod_regione='" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' " + " when a_pres.cod_regione is not null and a_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente Fuori Regione' "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='" + ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE + "' then 'Residente Fuori Regione' " + " else 'Non rilevato' " + " end)";

	private String NUM_ADESIONE_APPUNTAMENTO_DIVISE_PROVINCIA = "select  " + " (case "
			+ " when pass.cod_provincia is not null then pass.denominazione " + " else 'Non assegnata' "
			+ " end) as ProvinciaAssegnazione, " + " (case  " + " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " else 'Non rilevato' "
			+ " end) as Residenza, "
			+ " count(1) as numAdesioniConAppuntamento "
			+ " from myportal.yg_adesione y "
			+ " "
			+ " left join myportal.de_comune a_cres on (y.cod_com_res_rif_notifica=a_cres.cod_com) "
			+ " left join myportal.de_provincia a_pres on (a_cres.cod_provincia=a_pres.cod_provincia) "
			+ "  "
			+ " left join mycas.utente_info u  on (y.id_pf_principal=u.id_pf_principal) "
			+ " left join myportal.de_comune p_cres on (u.cod_com_residenza=p_cres.cod_com) "
			+ " left join myportal.de_provincia p_pres on (p_cres.cod_provincia=p_pres.cod_provincia) "
			+ " "
			+ " left join myportal.de_provincia pass on (y.cod_provincia_rif_notifica=pass.cod_provincia) "
			+ " where flg_adesione='Y' "
			+ " and cod_regione_rif_notifica='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' "
			+ " and dt_adesione <= (current_date - 1) "
			+ " and dt_presa_in_carico is not null "
			+ " group by "
			+ " (case "
			+ " when pass.cod_provincia is not null then pass.denominazione "
			+ " else 'Non assegnata' "
			+ " end), "
			+ " (case "
			+ " when a_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when a_pres.cod_regione is not null and a_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " when y.cod_com_res_rif_notifica is null and p_pres.cod_regione='"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente in Regione' "
			+ " when y.cod_com_res_rif_notifica is null and u.cod_com_residenza is not null and p_pres.cod_regione<>'"
			+ ConstantsSingleton.COD_REGIONE
			+ "' then 'Residente Fuori Regione' "
			+ " else 'Non rilevato' "
			+ " end) "
			+ " order by 1,2 desc";

	/**
	 * 
	 * statistiche per stato adesione
	 * 
	 * 
	 * @return
	 */
	public List<StatsDTO> getStatsAdesione() {
		List<StatsDTO> lista = new ArrayList<StatsDTO>();
		String nativeQueryStr = STATI_ADESIONE_QUERY;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);
		List resultList = nativeQuery.getResultList();
		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			StatsDTO stat = new StatsDTO((BigInteger) objectArray[0], (String) objectArray[1], (String) objectArray[2]);

			lista.add(stat);
		}

		return lista;
	}

	/**
	 * numero totale teste adesione
	 * 
	 */
	public BigInteger getTotNumAdesioni() {
		String nativeQueryStr = NUM_ADESIONE_TOT_QUERY;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);

		return new BigInteger(nativeQuery.getSingleResult().toString());
	}

	/**
	 * numero adesioni in regione, fuori regione e non trovato
	 * 
	 */
	public List<StatsDTO> getNumAdesioniDiviseRegione() {
		List<StatsDTO> lista = new ArrayList<StatsDTO>();
		String nativeQueryStr = NUM_ADESIONE_DIVISE_REGIONE_QUERY;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);
		List resultList = nativeQuery.getResultList();
		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			StatsDTO stat = new StatsDTO((BigInteger) objectArray[1], (String) objectArray[0], (String) objectArray[0]);

			lista.add(stat);
		}

		return lista;

	}

	/**
	 * numero adesioni in regione, fuori regione e non trovato 
	 * raggruppate per stato
	 * 
	 */
	public List<StatsDTO> getNumAdesioniDiviseRegionePerStato() {
		List<StatsDTO> lista = new ArrayList<StatsDTO>();
		String nativeQueryStr = NUM_ADESIONE_DIVISE_REGIONE_PER_STATO_QUERY;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);
		List resultList = nativeQuery.getResultList();
		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			StatsDTO stat = new StatsDTO((BigInteger) objectArray[2], (String) objectArray[1], (String) objectArray[0]);

			lista.add(stat);
		}

		return lista;

	}
	
	/**
	 * numero adesioni non assegnate
	 * 
	 */
	public BigInteger getTotNumAdesioniNonAssegnate() {
		String nativeQueryStr = NUM_ADESIONI_NON_ASSEGNATE;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);

		return new BigInteger(nativeQuery.getSingleResult().toString());
	}

	/**
	 * numero adesioni con appuntamento
	 * 
	 */
	public BigInteger getTotNumAdesioniConAppuntamento() {
		String nativeQueryStr = NUM_ADESIONE_APPUNTAMENTO;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);

		return new BigInteger(nativeQuery.getSingleResult().toString());
	}

	/**
	 * numero adesioni con appuntamento in regione, fuori regione e non trovato
	 * 
	 */
	public List<StatsDTO> getNumAdesioniAppuntamentoDiviseRegione() {
		List<StatsDTO> lista = new ArrayList<StatsDTO>();
		String nativeQueryStr = NUM_ADESIONE_APPUNTAMENTO_DIVISE_REGIONE;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);
		List resultList = nativeQuery.getResultList();
		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			StatsDTO stat = new StatsDTO((BigInteger) objectArray[1], (String) objectArray[0], (String) objectArray[0]);

			lista.add(stat);
		}

		return lista;
	}

	/**
	 * numero adesioni con appuntamento in regione, fuori regione e non trovato
	 * 
	 */
	public List<StatsDTO> getNumAdesioniAppuntamentoDiviseProvincia() {
		List<StatsDTO> lista = new ArrayList<StatsDTO>();
		String nativeQueryStr = NUM_ADESIONE_APPUNTAMENTO_DIVISE_PROVINCIA;

		Query nativeQuery = entityManager.createNativeQuery(nativeQueryStr);
		List resultList = nativeQuery.getResultList();
		for (int i = 0; i < resultList.size(); i++) {
			Object obj = resultList.get(i);
			Object[] objectArray = (Object[]) obj;
			StatsDTO stat = new StatsDTO((BigInteger) objectArray[1], (String) objectArray[0], (String) objectArray[0]);

			lista.add(stat);
		}

		return lista;
	}
}