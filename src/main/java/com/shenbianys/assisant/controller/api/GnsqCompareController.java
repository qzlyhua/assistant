package com.shenbianys.assisant.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 功能授权比较（sq_gnsq表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class GnsqCompareController extends BaseController {
    @RequestMapping("/gnsq/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT UPPER(MD5(CONCAT(dm,mc,sjdm,lx))) as md5,id,dm,mc,sjdm,lx FROM sq_gnsq order by dm asc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
