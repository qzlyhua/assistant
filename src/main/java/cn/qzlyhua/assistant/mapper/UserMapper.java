package cn.qzlyhua.assistant.mapper;

import cn.qzlyhua.assistant.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from AS_RE_USER")
    List<User> getAllUsers();

    @Select("select * from AS_RE_USER where username=#{username}")
    User getUserByUsername(String username);

    @Insert({"insert into AS_RE_USER(nick, username, type) values( #{nick}, #{username}, #{type})"})
    int insertUser(User user);
}
