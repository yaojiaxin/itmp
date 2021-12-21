package cn.pioneeruniverse.system.feignInterface.dic;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.TblDataDicDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.system.feignFallback.dic.DataDicFallback;
import cn.pioneeruniverse.system.vo.dic.TblDataDictionary;

@FeignClient(value="system",fallbackFactory=DataDicFallback.class)
public interface DataDicInterface {

	@RequestMapping(value="dataDic/getDataDicByTermCode",method=RequestMethod.POST)
    List<TblDataDicDTO> getDataDicByTermCode(@RequestParam("termCode") String termCode);

}
