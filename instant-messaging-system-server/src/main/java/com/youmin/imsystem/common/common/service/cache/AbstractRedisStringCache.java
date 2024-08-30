package com.youmin.imsystem.common.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.youmin.imsystem.common.utils.RedisUtils;
import javafx.util.Pair;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;



public abstract class AbstractRedisStringCache<IN,OUT> implements BatchCache<IN,OUT>{

    Class<OUT> outClass;

    public AbstractRedisStringCache(){
        ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) parameterizedType.getActualTypeArguments()[1];
    }

    /**
     * get value of a key
     * @param key
     * @return
     */
    @Override
    public OUT get(IN key) {
        return getBatch(Arrays.asList(key)).get(key);
    }


    /**
     * get value of batch keys
     * @param keys
     * @return
     */
    @Override
    public Map<IN,OUT> getBatch(List<IN> keys) {
        if(CollectionUtil.isEmpty(keys)){
            return new HashMap<>();
        }
        //convert keys into redis key format (remove duplicate key)
        keys = keys.stream().distinct().collect(Collectors.toList());
        List<String> redisKey = keys.stream().map(this::getKey).collect(Collectors.toList());
        //get value from redis using redis key
        List<OUT> valueList = RedisUtils.mget(redisKey, outClass);

        List<IN> needLoaded = new ArrayList<>();
        //filter the keys that require to be loaded from mysql
        for (int i = 0; i < keys.size(); i++) {
            if(Objects.isNull(valueList.get(i))){
                needLoaded.add(keys.get(i));
            }
        }
        Map<IN,OUT> load = new HashMap<>();
        if(!CollectionUtil.isEmpty(needLoaded)){
            //load from database
             load = load((needLoaded));
            Map<String, OUT> redisMap = load.entrySet().stream().map((kv) ->
                    new Pair<String, OUT>(this.getKey(kv.getKey()), kv.getValue())
            ).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
            //store in redis cache
            RedisUtils.mset(redisMap,expire());
        }
        HashMap<IN, OUT> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            IN in = keys.get(i);
            OUT out = Optional.ofNullable(valueList.get(i))
                    .orElse(load.get(in));
            result.put(in,out);
        }
        return result;

    }

    @Override
    public void delete(IN key) {
        deleteBatch(Arrays.asList(key));
    }


    @Override
    public void deleteBatch(List<IN> keys) {
        List<String> redisKey = keys.stream().map(this::getKey).collect(Collectors.toList());
        RedisUtils.del(redisKey);
    }

    /**
     * key expire (TTL) in redis
     * @return
     */
    public abstract Long expire();

    /**
     * load from database logic
     * @param keys
     * @return
     */
    public abstract Map<IN,OUT> load(List<IN> keys);

    /**
     * transform IN to redis key
     * @param in
     * @return
     */
    public abstract String getKey(IN in);
}
