package com.ew.autofly.internal.key.base;

import java.util.Objects;

public abstract class FlyKey {

    private String key;

  
    private Object cache;

  
    private Object defaultCache;

  
    private String cacheFile;

  
    private boolean isSave = false;

  
    private boolean isSecure = false;

    private Class clazz;


    public FlyKey(String key) {
        this(key, null, null);
    }


    public FlyKey(String key, Object defaultCache, Class clazz) {

        if (defaultCache != null && !defaultCache.getClass().equals(clazz)) {
            throw new RuntimeException("初始化FlyKey错误：" + key + "值类型不匹配");
        }
        this.clazz = clazz;
        this.key = key;
        this.defaultCache = defaultCache;
        this.isSave = initSave();
        this.cacheFile = initCacheFile();
        this.isSecure = initSecure();
        if (isSave) {
            FlyKeyCacheHelper.getInstance().createConfig(cacheFile, isSecure);
        }
    }

    public String getKey() {
        return key;
    }


    public Object getCache() {
        if (this.cache == null) {
            if (isSave) {
                this.cache = FlyKeyCacheHelper.getInstance().get(cacheFile, key, defaultCache, clazz);
            } else {
                return defaultCache;
            }
        }

        return this.cache;
    }

    public boolean setCache(Object cache) {

        if (cache != null) {

            if (!cache.getClass().equals(clazz)) {
                throw new RuntimeException("保存YFKey值错误：" + key + "值类型不匹配");
            }

            if (isSave) {
                boolean isSetOk = FlyKeyCacheHelper.getInstance().set(cacheFile, key, cache);
                if (isSetOk) {
                    this.cache = cache;
                    return true;
                } else {
                    return false;
                }
            }
        }

        this.cache = cache;
        return true;
    }

    
    public void clearCache() {
        this.cache = null;
    }

    /**
     * 设置SharedPreferences的文件名
     *
     * @return
     */
    protected abstract String initCacheFile();

    /**
     * 设置是否保存到SharedPreferences
     *
     * @return
     */
    protected abstract boolean initSave();

    /**
     * 设置是否加密保存到SharedPreferences
     *
     * @return
     */
    protected abstract boolean initSecure();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlyKey yfKey = (FlyKey) o;
        return Objects.equals(key, yfKey.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key);
    }
}
