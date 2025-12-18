package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.wms.domain.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据角色获取权限列表
     * @param role 角色标识
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRole(String role);
}
