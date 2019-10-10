package com.shenbianys.assistant.service.impl;

import com.shenbianys.assistant.service.MysqlService;
import com.shenbianys.assistant.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Service
public class MysqlServiceImpl implements MysqlService {
    @Autowired
    @Qualifier("jdbcTemplateA")
    protected JdbcTemplate jdbcTemplateDev;

    @Autowired
    @Qualifier("jdbcTemplateB")
    protected JdbcTemplate jdbcTemplateTest;

    @Autowired
    @Qualifier("jdbcTemplateC")
    protected JdbcTemplate jdbcTemplateTestTjd;

    @Autowired
    @Qualifier("jdbcTemplateD")
    protected JdbcTemplate jdbcTemplatePro;

    /**
     * 根据环境变量返回对应 JdbcTemplate
     * 默认返回开发环境
     *
     * @param env
     * @return
     */
    private JdbcTemplate getJdbcTemplateByEnv(String env) {
        if ("test".equals(env)) {
            return jdbcTemplateTest;
        } else if ("testtjd".equals(env)) {
            return jdbcTemplateTestTjd;
        } else if ("pro".equals(env)) {
            return jdbcTemplatePro;
        } else {
            return jdbcTemplateDev;
        }
    }

    /**
     * 根据环境参数，在对应数据库内执行查询SQL
     *
     * @param env
     * @param sql
     * @return
     */
    @Override
    public List<Map<String, Object>> queryForList(String env, String sql) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateByEnv(env);
        if (jdbcTemplate != null) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return list;
        } else {
            return null;
        }
    }

    @Override
    public <T> T queryForObject(String env, String sql, Class<T> clazz) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateByEnv(env);
        if (jdbcTemplate != null) {
            try {
                Map<String, Object> map = jdbcTemplate.queryForMap(sql);
                return ConvertUtils.mapToBean(map, clazz);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> queryForMap(String env, String sql) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateByEnv(env);
        if (jdbcTemplate != null) {
            try {
                return jdbcTemplate.queryForMap(sql);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int update(String env, String sql) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateByEnv(env);
        if (jdbcTemplate != null) {
            return jdbcTemplate.update(sql);
        } else {
            return 0;
        }
    }
}
