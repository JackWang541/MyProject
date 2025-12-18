package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.WmsWarehouse;
import com.ruoyi.wms.service.WmsWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WmsWarehouseController {

    @Autowired
    private WmsWarehouseService warehouseService;

    // 获取所有仓库（支持分页和搜索）
    @GetMapping
    public Result<PageResult<WmsWarehouse>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String manager) {
        QueryWrapper<WmsWarehouse> wrapper = new QueryWrapper<>();
        if (warehouseName != null && !warehouseName.isEmpty()) {
            wrapper.like("warehouse_name", warehouseName);
        }
        if (manager != null && !manager.isEmpty()) {
            wrapper.like("manager", manager);
        }
        Page<WmsWarehouse> page = new Page<>(pageNum, pageSize);
        page = warehouseService.page(page, wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    // 根据ID获取仓库
    @GetMapping("/{id}")
    public Result<WmsWarehouse> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的仓库ID");
        }
        WmsWarehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return Result.error("仓库不存在");
        }
        return Result.success(warehouse);
    }

    // 创建仓库
    @PostMapping
    public Result<Boolean> save(@RequestBody WmsWarehouse warehouse) {
        if (warehouse == null) {
            return Result.error("仓库信息不能为空");
        }
        if (warehouse.getWarehouseName() == null || warehouse.getWarehouseName().trim().isEmpty()) {
            return Result.error("仓库名称不能为空");
        }
        if (warehouse.getAddress() == null || warehouse.getAddress().trim().isEmpty()) {
            return Result.error("仓库地址不能为空");
        }
        if (warehouse.getManager() == null || warehouse.getManager().trim().isEmpty()) {
            return Result.error("仓库管理员不能为空");
        }
        if (warehouse.getPhone() == null || warehouse.getPhone().trim().isEmpty()) {
            return Result.error("联系电话不能为空");
        }
        // 验证电话号码格式
        if (!warehouse.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号");
        }
        boolean result = warehouseService.save(warehouse);
        return result ? Result.success(result) : Result.error("创建仓库失败");
    }

    // 更新仓库
    @PutMapping
    public Result<Boolean> update(@RequestBody WmsWarehouse warehouse) {
        if (warehouse == null || warehouse.getId() == null) {
            return Result.error("仓库信息或ID不能为空");
        }
        if (warehouse.getWarehouseName() == null || warehouse.getWarehouseName().trim().isEmpty()) {
            return Result.error("仓库名称不能为空");
        }
        if (warehouse.getAddress() == null || warehouse.getAddress().trim().isEmpty()) {
            return Result.error("仓库地址不能为空");
        }
        if (warehouse.getManager() == null || warehouse.getManager().trim().isEmpty()) {
            return Result.error("仓库管理员不能为空");
        }
        if (warehouse.getPhone() == null || warehouse.getPhone().trim().isEmpty()) {
            return Result.error("联系电话不能为空");
        }
        // 验证电话号码格式
        if (!warehouse.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号");
        }
        // 验证仓库是否存在
        if (warehouseService.getById(warehouse.getId()) == null) {
            return Result.error("仓库不存在");
        }
        boolean result = warehouseService.updateById(warehouse);
        return result ? Result.success(result) : Result.error("更新仓库失败");
    }

    // 删除仓库
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的仓库ID");
        }
        // 验证仓库是否存在
        if (warehouseService.getById(id) == null) {
            return Result.error("仓库不存在");
        }
        boolean result = warehouseService.removeById(id);
        return result ? Result.success(result) : Result.error("删除仓库失败");
    }

    // 根据名称查询仓库
    @GetMapping("/name/{name}")
    public Result<List<WmsWarehouse>> getByName(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            return Result.success(warehouseService.list());
        }
        QueryWrapper<WmsWarehouse> wrapper = new QueryWrapper<>();
        wrapper.like("warehouse_name", name);
        return Result.success(warehouseService.list(wrapper));
    }
}
