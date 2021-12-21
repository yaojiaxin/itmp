package cn.pioneeruniverse.dev.feignInterface.worktask;

import org.springframework.cloud.netflix.feign.FeignClient;

import cn.pioneeruniverse.dev.feignFallback.worktask.WorktaskFallback;


@FeignClient(value="devManage",fallbackFactory=WorktaskFallback.class)
public interface WorktaskInterface {

}
