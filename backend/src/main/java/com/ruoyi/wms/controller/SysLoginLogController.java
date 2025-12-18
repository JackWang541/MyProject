package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.SysLoginLog;
import com.ruoyi.wms.service.SysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志控制器
 */
@RestController
@RequestMapping("/log")
public class SysLoginLogController {

    @Autowired
    private SysLoginLogService loginLogService;

    /**
     * 分页查询登录日志
     */
    @GetMapping("/page")
    public Result<PageResult<SysLoginLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysLoginLog> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like("username", username);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        // 按登录时间倒序排序
        wrapper.orderByDesc("login_time");
        
        loginLogService.page(page, wrapper);
        
        PageResult<SysLoginLog> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }
}
