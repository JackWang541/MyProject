package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.wms.domain.SysUser;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    SysUser login(String username, String password);
    
    /**
     * 新增用户
     * @param user 用户信息
     * @return 是否成功
     */
    boolean saveUser(SysUser user);
    
    /**
     * 更新用户
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);
    
    /**
     * 查询用户列表
     * @param user 查询条件
     * @return 用户列表
     */
    List<SysUser> listUsers(SysUser user);
    
    /**
     * 重置用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long id, String newPassword);
}