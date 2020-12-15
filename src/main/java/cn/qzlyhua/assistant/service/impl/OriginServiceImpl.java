package cn.qzlyhua.assistant.service.impl;

import cn.qzlyhua.assistant.entity.Origin;
import cn.qzlyhua.assistant.mapper.OriginMapper;
import cn.qzlyhua.assistant.service.OriginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户域
 *
 * @author yanghua
 */
@Service
@Slf4j
public class OriginServiceImpl implements OriginService {
    @Resource
    OriginMapper originMapper;

    @Override
    public List<Origin> getAllOrigins() {
        return originMapper.getAllOrigins();
    }
}
