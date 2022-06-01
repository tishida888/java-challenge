package jp.co.axa.apidemo;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import lombok.extern.slf4j.Slf4j;

/**
 *  Cache Logger class to monitor what's occurrrng on cache level for debug.
 */
@Slf4j
public class CacheLogger implements CacheEventListener<Object, Object> {

	@Override
	public void onEvent(CacheEvent<?, ?> cacheEvent) {
	  log.info("Key: {} | EventType: {} | Old value: {} | New value: {}",
			   cacheEvent.getKey(), cacheEvent.getType(), cacheEvent.getOldValue(), 
			   cacheEvent.getNewValue());
	}
}
