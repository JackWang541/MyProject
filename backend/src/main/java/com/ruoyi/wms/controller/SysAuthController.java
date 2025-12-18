package com.ruoyi.wms.controller;

import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.SysLoginLog;
import com.ruoyi.wms.domain.SysUser;
import com.ruoyi.wms.dto.LoginDTO;
import com.ruoyi.wms.service.SysLoginLogService;
import com.ruoyi.wms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class SysAuthController {

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysLoginLogService loginLogService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @param session HTTP会话
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody LoginDTO loginDTO, HttpSession session, HttpServletRequest request) {
        // 验证参数
        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty() ||
                loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            recordLoginLog(loginDTO.getUsername(), request, 0, "用户名和密码不能为空");
            return Result.error("用户名和密码不能为空");
        }

        // 调用登录服务
        SysUser user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            recordLoginLog(loginDTO.getUsername(), request, 0, "用户名或密码错误");
            return Result.error("用户名或密码错误");
        }

        // 将用户信息存入会话
        session.setAttribute("user", user);
        recordLoginLog(loginDTO.getUsername(), request, 1, "登录成功");
        Result<SysUser> result = Result.success(user);
        result.setMessage("登录成功");
        return result;
    }
    
    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, HttpServletRequest request, Integer status, String message) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setIp(request.getRemoteAddr());
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setStatus(status);
        loginLog.setMessage(message);
        loginLogService.save(loginLog);
    }

    /**
     * 用户登出
     * @param session HTTP会话
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate(); // 销毁会话
        Result<Void> result = Result.success();
        result.setMessage("登出成功");
        return result;
    }

    /**
     * 获取当前登录用户信息
     * @param session HTTP会话
     * @return 当前用户信息
     */
    @PostMapping("/getCurrentUser")
    public Result<SysUser> getCurrentUser(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        return Result.success(user);
    }
}