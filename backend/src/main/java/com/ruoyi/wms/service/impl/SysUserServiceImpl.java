package com.ruoyi.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wms.domain.SysUser;
import com.ruoyi.wms.mapper.SysUserMapper;
import com.ruoyi.wms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public SysUser getByUsername(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getOne(wrapper);
    }
    
    @Override
    public SysUser login(String username, String password) {
        // 根据用户名查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            return null; // 用户不存在
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            return null; // 用户已禁用
        }
        
        // 验证密码（明文比较）
        if (!password.equals(user.getPassword())) {
            return null; // 密码错误
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        updateById(user);
        
        // 清除密码，返回用户信息
        user.setPassword(null);
        return user;
    }
    
    @Override
    public boolean saveUser(SysUser user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        
        // 密码已经在前端加密，直接保存
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLoginTime(null);
        return save(user);
    }
    
    @Override
    public boolean updateUser(SysUser user) {
        // 不允许更新密码，密码更新通过专门的方法
        user.setPassword(null);
        user.setUpdateTime(new Date());
        return updateById(user);
    }
    
    @Override
    public boolean deleteUser(Long id) {
        return removeById(id);
    }
    
    @Override
    public List<SysUser> listUsers(SysUser user) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            wrapper.like("username", user.getUsername());
        }
        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            wrapper.like("nickname", user.getNickname());
        }
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            wrapper.eq("role", user.getRole());
        }
        if (user.getStatus() != null) {
            wrapper.eq("status", user.getStatus());
        }
        return list(wrapper);
    }
    
    @Override
    public boolean resetPassword(Long id, String newPassword) {
        SysUser user = getById(id);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        user.setUpdateTime(new Date());
        return updateById(user);
    }
}