package com.rikka.raymispring.mapper;

import com.rikka.raymispring.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 28389
* @description 针对表【tbl_user】的数据库操作Mapper
* @createDate 2026-03-19 23:04:53
* @Entity com.rikka.raymispring.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




