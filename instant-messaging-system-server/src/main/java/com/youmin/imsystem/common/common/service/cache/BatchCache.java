package com.youmin.imsystem.common.common.service.cache;

import java.util.List;
import java.util.Map;

public interface BatchCache<IN,OUT> {

    OUT get(IN key);

    Map<IN,OUT> getBatch(List<IN> keys);

    void delete(IN key);

    void deleteBatch(List<IN> keys);
}
