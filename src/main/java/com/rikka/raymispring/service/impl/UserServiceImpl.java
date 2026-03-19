package com.rikka.raymispring.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rikka.raymispring.domain.User;
import com.rikka.raymispring.service.UserService;
import com.rikka.raymispring.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 28389
* @description 针对表【tbl_user】的数据库操作Service实现
* @createDate 2026-03-19 23:04:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




