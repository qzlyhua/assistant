package com.shenbianys.assisant.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 功能授权比较（sq_gnsq表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class GnsqCompareController extends BaseController {
    @RequestMapping("/gnsq/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(dm,mc,sjdm,lx))) as md5,id,dm,mc,sjdm," +
                " CASE lx WHEN 1 THEN '医生端' ELSE '居民端' END lx FROM sq_gnsq order by lx, dm asc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
