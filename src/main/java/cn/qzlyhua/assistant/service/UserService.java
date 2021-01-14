package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.entity.User;

import java.util.List;

/**
 * 用户管理
 *
 * @author yanghua
 */
public interface UserService {
    List<User> getAllUsers();
}
