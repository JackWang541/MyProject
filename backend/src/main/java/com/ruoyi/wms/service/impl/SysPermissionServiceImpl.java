package com.ruoyi.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wms.domain.SysPermission;
import com.ruoyi.wms.mapper.SysPermissionMapper;
import com.ruoyi.wms.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现类
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysPermissionMapper permissionMapper;

    @Override
    public List<SysPermission> getPermissionsByRole(String role) {
        return permissionMapper.selectByRole(role);
    }
}
