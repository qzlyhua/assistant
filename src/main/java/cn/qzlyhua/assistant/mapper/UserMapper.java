package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from AS_RE_USER")
    List<User> getAllUsers();

    @Select("select * from user where username=#{username}")
    User getUserByUsername(String username);
}
