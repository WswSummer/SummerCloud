package com.wsw.fusertaskmanager.fallback;

import com.wsw.fusertaskmanager.client.TesterClient;
import feign.hystrix.FallbackFactory;

/**
 * @Author WangSongWen
 * @Date: Created in 13:46 2021/1/5
 * @Description:
 */
public class TesterClientFallBack implements FallbackFactory<TesterClient> {
    @Override
    public TesterClient create(Throwable throwable) {
        return null;
    }
}
