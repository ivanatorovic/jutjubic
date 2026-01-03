package com.example.jutjubic.logger;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLogger implements CacheEventListener<Object, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(CacheLogger.class);

    @Override
    public void onEvent(CacheEvent<?, ?> event) {
        LOG.info("[CACHE] key={} type={} old={} new={}",

                event.getKey(),
                event.getType(),
                event.getOldValue() == null ? "null" : event.getOldValue().getClass().getSimpleName(),
                event.getNewValue() == null ? "null" : event.getNewValue().getClass().getSimpleName()
        );
    }
}
