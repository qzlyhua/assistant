package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 第三方系统字典比较（xt_dsfxtzd表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class DsfxtzdController extends BaseController {
    @RequestMapping("/dsfxtzd/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT MD5(CONCAT(zdid,';',xtbs,';',xtmc)) as md5, zdid, xtbs, xtmc, yhbdms, qqms, sfblxx " +
                "FROM xt_dsfzd order by zdid";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
