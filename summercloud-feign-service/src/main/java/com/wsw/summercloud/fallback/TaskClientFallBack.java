package com.wsw.summercloud.fallback;

import com.wsw.summercloud.client.TaskClient;
import feign.hystrix.FallbackFactory;

/**
 * @Author WangSongWen
 * @Date: Created in 13:46 2021/1/5
 * @Description:
 */
public class TaskClientFallBack implements FallbackFactory<TaskClient> {
    @Override
    public TaskClient create(Throwable throwable) {
        return null;
    }
}
