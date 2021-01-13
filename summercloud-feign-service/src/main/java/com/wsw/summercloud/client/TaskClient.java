package com.wsw.summercloud.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author WangSongWen
 * @Date: Created in 13:42 2021/1/5
 * @Description: 调用task服务
 */
@FeignClient(value = "summercloud-task-service")
public interface TaskClient {

    @PostMapping("/recepienter/create")
    int create(@RequestParam("taskId") Long taskId, @RequestParam("taskName") String taskName, @RequestParam("name") String name, @RequestParam("remark") String remark);

}
