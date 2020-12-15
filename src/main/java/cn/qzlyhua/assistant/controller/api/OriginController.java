package cn.qzlyhua.assistant.controller.api;

import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.entity.Origin;
import cn.qzlyhua.assistant.service.OriginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户域概览
 *
 * @author yanghua
 */
@RestController
@Response
@RequestMapping("/api")
public class OriginController {
    @Resource
    OriginService originService;

    @RequestMapping("/origins")
    public List<Origin> getAllOrigins() {
        return originService.getAllOrigins();
    }
}
