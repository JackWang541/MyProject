package com.ruoyi.wms.controller;

import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.SysPermission;
import com.ruoyi.wms.domain.SysRolePermission;
import com.ruoyi.wms.service.SysPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.mapper.SysPermissionMapper;
import com.ruoyi.wms.mapper.SysRolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private SysPermissionMapper permissionMapper;

    /**
     * 获取所有角色列表
     */
    @GetMapping("/list")
    public Result<List<String>> list() {
        // 系统预设角色
        List<String> roles = List.of("root", "admin", "user");
        return Result.success(roles);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/permissions/{role}")
    public Result<List<SysPermission>> getRolePermissions(@PathVariable String role) {
        List<SysPermission> permissions = permissionService.getPermissionsByRole(role);
        return Result.success(permissions);
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping("/allPermissions")
    public Result<List<SysPermission>> getAllPermissions() {
        List<SysPermission> permissions = permissionService.list();
        return Result.success(permissions);
    }

    /**
     * 更新角色权限
     */
    @PutMapping("/permissions/{role}")
    public Result<Void> updateRolePermissions(@PathVariable String role, @RequestBody List<Long> permissionIds) {
        // 先删除原有权限
        QueryWrapper<SysRolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role", role);
        rolePermissionMapper.delete(wrapper);
        
        // 新增权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setRole(role);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        
        Result<Void> result = Result.success();
        result.setMessage("更新成功");
        return result;
    }
}
