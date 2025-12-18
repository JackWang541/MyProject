package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.SysUser;
import com.ruoyi.wms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        
        // 构建查询条件（用于count查询，不带ORDER BY）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser> countWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            countWrapper.like("username", username);
        }
        if (nickname != null && !nickname.trim().isEmpty()) {
            countWrapper.like("nickname", nickname);
        }
        if (role != null && !role.trim().isEmpty()) {
            countWrapper.eq("role", role);
        }
        if (status != null) {
            countWrapper.eq("status", status);
        }
        
        // 手动查询总数（使用不带ORDER BY的wrapper）
        long total = userService.count(countWrapper);
        
        // 构建查询条件（用于数据查询，带ORDER BY）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser> dataWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            dataWrapper.like("username", username);
        }
        if (nickname != null && !nickname.trim().isEmpty()) {
            dataWrapper.like("nickname", nickname);
        }
        if (role != null && !role.trim().isEmpty()) {
            dataWrapper.eq("role", role);
        }
        if (status != null) {
            dataWrapper.eq("status", status);
        }
        dataWrapper.orderByDesc("create_time");
        
        // 查询数据列表
        List<SysUser> records = userService.list(dataWrapper);
        
        // 构建分页结果
        PageResult<SysUser> pageResult = new PageResult<>(total, records, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 清除密码信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> save(@RequestBody SysUser user) {
        if (userService.saveUser(user)) {
            Result<Void> result = Result.success();
            result.setMessage("新增成功");
            return result;
        }
        return Result.error("用户名已存在");
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        if (userService.updateUser(user)) {
            Result<Void> result = Result.success();
            result.setMessage("更新成功");
            return result;
        }
        return Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        }
        return Result.error("删除失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword/{id}")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        if (userService.resetPassword(id, newPassword)) {
            Result<Void> result = Result.success();
            result.setMessage("密码重置成功");
            return result;
        }
        return Result.error("密码重置失败");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        if (userService.updateUser(user)) {
            Result<Void> result = Result.success();
            result.setMessage("状态更新成功");
            return result;
        }
        return Result.error("状态更新失败");
    }
}
