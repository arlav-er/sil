package it.eng.sil.myauthservice.model;

import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import net.sf.ehcache.CacheManager;

@Startup
@Singleton
public class CacheManagerSingleton {
	protected final Logger log = Logger.getLogger(CacheManagerSingleton.class.getName());
	private CacheManager cacheManager;

	private CacheManager provideCacheManager() {

		if (cacheManager == null)
			cacheManager = CacheManager.getInstance();
		
		return cacheManager;
	}

	public CacheManager getCacheMan() {
		if (cacheManager == null)
			return provideCacheManager();

		return cacheManager;
	}
}