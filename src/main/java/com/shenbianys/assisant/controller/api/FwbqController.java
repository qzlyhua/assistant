package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 服务标签比较（fw_bq表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class FwbqController extends BaseController {
    @RequestMapping("/fwbq/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT MD5(CONCAT(fwbqid,';',fwbqmc)) as md5, fwbqid, fwbqmc FROM fw_bq order by fwbqid";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
