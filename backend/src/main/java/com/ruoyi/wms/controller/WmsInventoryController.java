package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.WmsGoods;
import com.ruoyi.wms.domain.WmsInventory;
import com.ruoyi.wms.domain.WmsWarehouse;
import com.ruoyi.wms.service.WmsGoodsService;
import com.ruoyi.wms.service.WmsInventoryService;
import com.ruoyi.wms.service.WmsWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class WmsInventoryController {

    @Autowired
    private WmsInventoryService inventoryService;
    
    @Autowired
    private WmsWarehouseService warehouseService;
    
    @Autowired
    private WmsGoodsService goodsService;

    // 获取所有库存，包含仓库名称和货物名称，支持分页和搜索
    @GetMapping
    public Result<PageResult<WmsInventory>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String goodsName) {
        
        // 查询所有库存记录
        List<WmsInventory> allInventory = inventoryService.list();
        
        // 填充仓库名称和货物名称
        for (WmsInventory inventory : allInventory) {
            fillInventoryNames(inventory);
        }
        
        // 过滤记录
        List<WmsInventory> filteredInventory = allInventory;
        if (warehouseName != null && !warehouseName.isEmpty()) {
            filteredInventory = filteredInventory.stream()
                    .filter(inv -> inv.getWarehouseName() != null && inv.getWarehouseName().contains(warehouseName))
                    .toList();
        }
        if (goodsName != null && !goodsName.isEmpty()) {
            filteredInventory = filteredInventory.stream()
                    .filter(inv -> inv.getGoodsName() != null && inv.getGoodsName().contains(goodsName))
                    .toList();
        }
        
        // 分页处理
        int total = filteredInventory.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<WmsInventory> paginatedInventory = start < total ? filteredInventory.subList(start, end) : List.of();
        
        return Result.success(new PageResult<>(total, paginatedInventory));
    }

    // 根据ID获取库存，包含仓库名称和货物名称
    @GetMapping("/{id}")
    public Result<WmsInventory> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的库存ID");
        }
        WmsInventory inventory = inventoryService.getById(id);
        if (inventory == null) {
            return Result.error("库存不存在");
        }
        // 填充仓库名称和货物名称
        fillInventoryNames(inventory);
        return Result.success(inventory);
    }

    // 根据仓库ID获取库存，包含仓库名称和货物名称
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<WmsInventory>> getByWarehouseId(@PathVariable Long warehouseId) {
        if (warehouseId == null || warehouseId <= 0) {
            return Result.error("无效的仓库ID");
        }
        QueryWrapper<WmsInventory> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        List<WmsInventory> inventoryList = inventoryService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsInventory inventory : inventoryList) {
            fillInventoryNames(inventory);
        }
        return Result.success(inventoryList);
    }

    // 根据货物ID获取库存，包含仓库名称和货物名称
    @GetMapping("/goods/{goodsId}")
    public Result<List<WmsInventory>> getByGoodsId(@PathVariable Long goodsId) {
        if (goodsId == null || goodsId <= 0) {
            return Result.error("无效的货物ID");
        }
        QueryWrapper<WmsInventory> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        List<WmsInventory> inventoryList = inventoryService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsInventory inventory : inventoryList) {
            fillInventoryNames(inventory);
        }
        return Result.success(inventoryList);
    }
    
    // 填充库存对象的仓库名称和货物名称
    private void fillInventoryNames(WmsInventory inventory) {
        if (inventory == null) {
            return;
        }
        // 填充仓库名称
        if (inventory.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseService.getById(inventory.getWarehouseId());
            if (warehouse != null) {
                inventory.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        // 填充货物名称
        if (inventory.getGoodsId() != null) {
            WmsGoods goods = goodsService.getById(inventory.getGoodsId());
            if (goods != null) {
                inventory.setGoodsName(goods.getGoodsName());
            }
        }
    }

    // 创建库存
    @PostMapping
    public Result<Boolean> save(@RequestBody WmsInventory inventory) {
        if (inventory == null) {
            return Result.error("库存信息不能为空");
        }
        if (inventory.getWarehouseId() == null || inventory.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (inventory.getGoodsId() == null || inventory.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
            return Result.error("库存数量不能为空且必须大于等于0");
        }
        if (inventory.getSafeQuantity() == null || inventory.getSafeQuantity() < 0) {
            return Result.error("安全库存不能为空且必须大于等于0");
        }
        boolean result = inventoryService.save(inventory);
        return result ? Result.success(result) : Result.error("创建库存失败");
    }

    // 更新库存
    @PutMapping
    public Result<Boolean> update(@RequestBody WmsInventory inventory) {
        if (inventory == null || inventory.getId() == null) {
            return Result.error("库存信息或ID不能为空");
        }
        if (inventory.getWarehouseId() == null || inventory.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (inventory.getGoodsId() == null || inventory.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
            return Result.error("库存数量不能为空且必须大于等于0");
        }
        if (inventory.getSafeQuantity() == null || inventory.getSafeQuantity() < 0) {
            return Result.error("安全库存不能为空且必须大于等于0");
        }
        // 验证库存是否存在
        if (inventoryService.getById(inventory.getId()) == null) {
            return Result.error("库存不存在");
        }
        boolean result = inventoryService.updateById(inventory);
        return result ? Result.success(result) : Result.error("更新库存失败");
    }

    // 删除库存
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的库存ID");
        }
        // 验证库存是否存在
        if (inventoryService.getById(id) == null) {
            return Result.error("库存不存在");
        }
        boolean result = inventoryService.removeById(id);
        return result ? Result.success(result) : Result.error("删除库存失败");
    }
}
