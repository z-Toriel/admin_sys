package com.system.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.User;
import com.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;


@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 不需要判断，输入的用户名和密码对不对。只需要根据username查询该用户的详细信息即可
        // 查询出来的详细信息 分装到userDetails对象中，返回给springsecurity即可。它会完成用户的验证
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if(user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 缺少当前登录用户的权限
        return new AccountUser(user.getId(),user.getUsername(),user.getPassword(),getUserAuthority(user.getId()));
    }

    // 将权限字符串转为springScurity的权限对
    public List<GrantedAuthority> getUserAuthority(Long userId){
        // 通过sc内置的工具类，将查询出来的权限字符串转为GrantedAuthority集合对象
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userService.getUserAuthorityInfo(userId));

    }
}
