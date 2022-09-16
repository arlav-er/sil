package it.eng.sil.myaccount.model.ejb.stateless.utils;

import it.eng.sil.base.pojo.auth.gp.CachePOJO;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.chartistjsf.model.chart.BarChartModel;
import org.chartistjsf.model.chart.BarChartSeries;

/**
 * Questa classe espone i WS di profilatura unica ragion d'essere di questo .war, per il resto semplice wrapper di
 * myauth-commons
 * 
 * @author pegoraro
 *
 */
@Stateless
public class MyAccountStatisticsEJB {
	protected static Logger log = Logger.getLogger(MyAccountStatisticsEJB.class.getName());

	@EJB
	private MyAccountCacheManagerSingleton cacheManager;

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

	public BarChartModel getCacheHitChartData() {
		BarChartModel ret = new BarChartModel();
		try {
			CachePOJO[] rawData = getCacheStats();

			BarChartSeries serieHit = new BarChartSeries();
			BarChartSeries serieMiss = new BarChartSeries();
			ArrayList<Number> hitData = new ArrayList<Number>();
			ArrayList<Number> missData = new ArrayList<Number>();

			for (int i = 0; i < rawData.length; i++) {
				CachePOJO cache = rawData[i];
				Long hit = cache.getCacheHit();
				Long miss = cache.getCacheMiss();
				if (hit != 0 || miss != 0) {
					ret.addLabel(cache.getName() + " - HIT: " + hit * 100 / (hit + miss) + "%");
					hitData.add(hit);
					missData.add(miss);
				}
			}
			serieHit.setName("Cache HIT");
			serieHit.setData(hitData);
			ret.addSeries(serieHit);

			serieMiss.setName("Cache MISS");
			serieMiss.setData(missData);
			ret.addSeries(serieMiss);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

	}
}
