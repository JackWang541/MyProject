package com.ruoyi.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.domain.SysPermission;

import java.util.List;

/**
 * 权限Mapper接口
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据角色获取权限列表
     * @param role 角色标识
     * @return 权限列表
     */
    List<SysPermission> selectByRole(String role);
}
