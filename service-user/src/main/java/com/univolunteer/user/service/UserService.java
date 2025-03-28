package com.univolunteer.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univolunteer.user.domain.dto.LoginUserDto;
import com.univolunteer.user.domain.dto.RegisterUserDto;
import com.univolunteer.user.domain.entity.Users;
import com.univolunteer.common.result.Result;


public interface UserService extends IService<Users> {

    Result login(LoginUserDto loginDto);

    Result register(RegisterUserDto registerDto);
}
