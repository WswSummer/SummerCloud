package com.wsw.summercloud.fallback;

import com.wsw.summercloud.client.TeskClient;
import feign.hystrix.FallbackFactory;

/**
 * @Author WangSongWen
 * @Date: Created in 13:46 2021/1/5
 * @Description:
 */
public class TeskClientFallBack implements FallbackFactory<TeskClient> {
    @Override
    public TeskClient create(Throwable throwable) {
        return null;
    }
}
