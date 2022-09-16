package it.eng.sil.myauthservice.rest.server;

import it.eng.sil.base.pojo.auth.gp.CachePOJO;
import it.eng.sil.myauthservice.model.CacheManagerSingleton;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import com.jamonapi.MonitorFactory;

/**
 * Questa classe espone i WS di profilatura unica ragion d'essere di questo .war, per il resto semplice wrapper di
 * myauth-commons
 * 
 * @author pegoraro
 *
 */
@Path("/stats")
public class StatisticsRESTServer {
	protected static Logger log = Logger.getLogger(StatisticsRESTServer.class.getName());

	@Inject
	private CacheManagerSingleton cacheManager;

	@GET
	@Path("cache")
	@Produces("application/json; charset=UTF-8")
	public CachePOJO[] getCacheStats() {
		// StringBuffer sb = new StringBuffer();

		ArrayList<CachePOJO> statsArr = new ArrayList<>();
		CacheManager man = cacheManager.getCacheMan();
		log.info("Reading Cache Stats: " + man.getDiskStorePath());
		/* get stats for all known caches */
		for (String name : man.getCacheNames()) {
			Cache cache = man.getCache(name);
			Statistics stats = cache.getStatistics();

			// log.info("Cache Stats Mem size: " + cache.calculateInMemorySize());
			// sb.append(String.format("%s: %s objects, %s hits, %s misses, %s size, %s avgTime\n", name,
			// stats.getObjectCount(), stats.getCacheHits(), stats.getCacheMisses(), cache.getSize(),
			// cache.getAverageSearchTime()));
			// containers

			CachePOJO cacheDTO = new CachePOJO();

			cacheDTO.setSearchesPerSecond(cache.getSearchesPerSecond());
			cacheDTO.setName(name);
			cacheDTO.setCacheAverageGetTime(cache.getAverageGetTime());
			cacheDTO.setCacheAverageSearchTime(stats.getAverageSearchTime());
			cacheDTO.setCachedObjects(stats.getObjectCount());
			cacheDTO.setCacheMiss(stats.getCacheMisses());
			cacheDTO.setCacheHit(stats.getCacheHits());
			cacheDTO.setCacheMemoryStoreObjectCount(stats.getMemoryStoreObjectCount());
			cacheDTO.setCacheDiskStoreObjectCount(stats.getDiskStoreObjectCount());
			cacheDTO.setDescription(stats.getStatisticsAccuracyDescription());

			statsArr.add(cacheDTO);
		}
		return statsArr.toArray(new CachePOJO[statsArr.size()]);
	}

	@GET
	@Path("jamon")
	@Produces("application/json; charset=UTF-8")
	public String getJamonStats(@QueryParam("colIdx") String idx, @QueryParam("order") String order) {
		String temp = MonitorFactory.getRootMonitor().getReport(Integer.parseInt(idx), order);
		if (temp == null || temp.equals(""))
			return "<div>Nessun dato disponibile.</div>";
		else
			return temp;
	}

	@GET
	@Path("jamonDisable")
	@Produces("application/json; charset=UTF-8")
	public String disableJamonStats() {
		MonitorFactory.getRootMonitor().disable();
		Boolean temp = MonitorFactory.getRootMonitor().isEnabled();
		return "<div>Jamon status: </div>" + temp;
	}

	@GET
	@Path("jamonEnable")
	@Produces("application/json; charset=UTF-8")
	public String enableJamonStats() {
		MonitorFactory.getRootMonitor().enable();
		Boolean temp = MonitorFactory.getRootMonitor().isEnabled();
		return "<div>Jamon status: </div>" + temp;
	}

	@GET
	@Path("jamonStatus")
	@Produces("application/json; charset=UTF-8")
	public boolean getJamonStatus() {
		return MonitorFactory.getRootMonitor().isEnabled();
	}

	@GET
	@Path("jamonReset")
	@Produces("application/json; charset=UTF-8")
	public String resetJamonStats() {
		MonitorFactory.getRootMonitor().reset();
		Boolean temp = MonitorFactory.getRootMonitor().isEnabled();
		return "<div>Jamon status: </div>" + temp;
	}
}
