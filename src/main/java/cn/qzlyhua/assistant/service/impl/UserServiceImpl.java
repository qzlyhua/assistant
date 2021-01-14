package cn.qzlyhua.assistant.service.impl;

import cn.qzlyhua.assistant.entity.User;
import cn.qzlyhua.assistant.mapper.UserMapper;
import cn.qzlyhua.assistant.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户管理
 *
 * @author yanghua
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }
}
