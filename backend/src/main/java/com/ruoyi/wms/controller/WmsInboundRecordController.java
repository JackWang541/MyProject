package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.WmsGoods;
import com.ruoyi.wms.domain.WmsInboundRecord;
import com.ruoyi.wms.domain.WmsInventory;
import com.ruoyi.wms.domain.WmsWarehouse;
import com.ruoyi.wms.service.WmsGoodsService;
import com.ruoyi.wms.service.WmsInboundRecordService;
import com.ruoyi.wms.service.WmsInventoryService;
import com.ruoyi.wms.service.WmsWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inbound")
public class WmsInboundRecordController {

    @Autowired
    private WmsInboundRecordService inboundRecordService;
    
    @Autowired
    private WmsWarehouseService warehouseService;
    
    @Autowired
    private WmsGoodsService goodsService;
    
    @Autowired
    private WmsInventoryService inventoryService;

    // 获取所有入库记录，包含仓库名称和货物名称，支持分页和搜索
    @GetMapping
    public Result<PageResult<WmsInboundRecord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String recordNo,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String goodsName) {
        
        // 查询所有入库记录
        List<WmsInboundRecord> allInbound = inboundRecordService.list();
        
        // 填充仓库名称和货物名称
        for (WmsInboundRecord record : allInbound) {
            fillInboundRecordNames(record);
        }
        
        // 过滤记录
        List<WmsInboundRecord> filteredInbound = allInbound;
        if (recordNo != null && !recordNo.isEmpty()) {
            filteredInbound = filteredInbound.stream()
                    .filter(rec -> rec.getRecordNo() != null && rec.getRecordNo().contains(recordNo))
                    .toList();
        }
        if (warehouseName != null && !warehouseName.isEmpty()) {
            filteredInbound = filteredInbound.stream()
                    .filter(rec -> rec.getWarehouseName() != null && rec.getWarehouseName().contains(warehouseName))
                    .toList();
        }
        if (goodsName != null && !goodsName.isEmpty()) {
            filteredInbound = filteredInbound.stream()
                    .filter(rec -> rec.getGoodsName() != null && rec.getGoodsName().contains(goodsName))
                    .toList();
        }
        
        // 分页处理
        int total = filteredInbound.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<WmsInboundRecord> paginatedInbound = start < total ? filteredInbound.subList(start, end) : List.of();
        
        return Result.success(new PageResult<>(total, paginatedInbound));
    }

    // 根据ID获取入库记录，包含仓库名称和货物名称
    @GetMapping("/{id}")
    public Result<WmsInboundRecord> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的入库记录ID");
        }
        WmsInboundRecord inboundRecord = inboundRecordService.getById(id);
        if (inboundRecord == null) {
            return Result.error("入库记录不存在");
        }
        // 填充仓库名称和货物名称
        fillInboundRecordNames(inboundRecord);
        return Result.success(inboundRecord);
    }

    // 根据仓库ID获取入库记录，包含仓库名称和货物名称
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<WmsInboundRecord>> getByWarehouseId(@PathVariable Long warehouseId) {
        if (warehouseId == null || warehouseId <= 0) {
            return Result.error("无效的仓库ID");
        }
        QueryWrapper<WmsInboundRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        List<WmsInboundRecord> inboundRecordList = inboundRecordService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsInboundRecord record : inboundRecordList) {
            fillInboundRecordNames(record);
        }
        return Result.success(inboundRecordList);
    }

    // 根据货物ID获取入库记录，包含仓库名称和货物名称
    @GetMapping("/goods/{goodsId}")
    public Result<List<WmsInboundRecord>> getByGoodsId(@PathVariable Long goodsId) {
        if (goodsId == null || goodsId <= 0) {
            return Result.error("无效的货物ID");
        }
        QueryWrapper<WmsInboundRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        List<WmsInboundRecord> inboundRecordList = inboundRecordService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsInboundRecord record : inboundRecordList) {
            fillInboundRecordNames(record);
        }
        return Result.success(inboundRecordList);
    }
    
    // 填充入库记录的仓库名称和货物名称
    private void fillInboundRecordNames(WmsInboundRecord record) {
        if (record == null) {
            return;
        }
        // 填充仓库名称
        if (record.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseService.getById(record.getWarehouseId());
            if (warehouse != null) {
                record.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        // 填充货物名称
        if (record.getGoodsId() != null) {
            WmsGoods goods = goodsService.getById(record.getGoodsId());
            if (goods != null) {
                record.setGoodsName(goods.getGoodsName());
            }
        }
    }

    // 创建入库记录
    @PostMapping
    public Result<Boolean> save(@RequestBody WmsInboundRecord inboundRecord) {
        if (inboundRecord == null) {
            return Result.error("入库记录信息不能为空");
        }
        if (inboundRecord.getRecordNo() == null || inboundRecord.getRecordNo().trim().isEmpty()) {
            return Result.error("记录编号不能为空");
        }
        if (inboundRecord.getWarehouseId() == null || inboundRecord.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (inboundRecord.getGoodsId() == null || inboundRecord.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (inboundRecord.getQuantity() == null || inboundRecord.getQuantity() <= 0) {
            return Result.error("数量不能为空且必须大于0");
        }
        if (inboundRecord.getUnitPrice() == null || inboundRecord.getUnitPrice().doubleValue() < 0) {
            return Result.error("单价不能为空且必须大于等于0");
        }
        if (inboundRecord.getTotalAmount() == null || inboundRecord.getTotalAmount().doubleValue() < 0) {
            return Result.error("总金额不能为空且必须大于等于0");
        }
        if (inboundRecord.getSupplier() == null || inboundRecord.getSupplier().trim().isEmpty()) {
            return Result.error("供应商不能为空");
        }
        if (inboundRecord.getOperator() == null || inboundRecord.getOperator().trim().isEmpty()) {
            return Result.error("操作员不能为空");
        }
        
        // 检查货物状态，只有启用状态（1）的货物才能入库
        WmsGoods goods = goodsService.getById(inboundRecord.getGoodsId());
        if (goods == null) {
            return Result.error("货物不存在");
        }
        if (goods.getStatus() == null || goods.getStatus() != 1) {
            return Result.error("只有启用状态的货物才能入库");
        }
        
        // 保存入库记录
        boolean result = inboundRecordService.save(inboundRecord);
        if (!result) {
            return Result.error("创建入库记录失败");
        }
        
        // 更新库存数量
        try {
            // 查找现有库存记录
            QueryWrapper<WmsInventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("warehouse_id", inboundRecord.getWarehouseId())
                       .eq("goods_id", inboundRecord.getGoodsId());
            
            WmsInventory inventory = inventoryService.getOne(queryWrapper);
            
            if (inventory != null) {
                // 库存记录存在，增加数量
                Integer newQuantity = inventory.getQuantity() + inboundRecord.getQuantity();
                inventory.setQuantity(newQuantity);
                inventoryService.updateById(inventory);
            } else {
                // 库存记录不存在，创建新记录
                WmsInventory newInventory = new WmsInventory();
                newInventory.setWarehouseId(inboundRecord.getWarehouseId());
                newInventory.setGoodsId(inboundRecord.getGoodsId());
                newInventory.setQuantity(inboundRecord.getQuantity());
                newInventory.setSafeQuantity(0); // 默认安全库存为0
                inventoryService.save(newInventory);
            }
        } catch (Exception e) {
            // 如果库存更新失败，可能需要回滚入库记录
            // 这里为了简化，只记录错误并返回
            e.printStackTrace();
            return Result.error("入库成功，但库存更新失败");
        }
        
        return Result.success(result);
    }

    // 更新入库记录
    @PutMapping
    public Result<Boolean> update(@RequestBody WmsInboundRecord inboundRecord) {
        if (inboundRecord == null || inboundRecord.getId() == null) {
            return Result.error("入库记录信息或ID不能为空");
        }
        if (inboundRecord.getRecordNo() == null || inboundRecord.getRecordNo().trim().isEmpty()) {
            return Result.error("记录编号不能为空");
        }
        if (inboundRecord.getWarehouseId() == null || inboundRecord.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (inboundRecord.getGoodsId() == null || inboundRecord.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (inboundRecord.getQuantity() == null || inboundRecord.getQuantity() <= 0) {
            return Result.error("数量不能为空且必须大于0");
        }
        if (inboundRecord.getUnitPrice() == null || inboundRecord.getUnitPrice().doubleValue() < 0) {
            return Result.error("单价不能为空且必须大于等于0");
        }
        if (inboundRecord.getTotalAmount() == null || inboundRecord.getTotalAmount().doubleValue() < 0) {
            return Result.error("总金额不能为空且必须大于等于0");
        }
        if (inboundRecord.getSupplier() == null || inboundRecord.getSupplier().trim().isEmpty()) {
            return Result.error("供应商不能为空");
        }
        if (inboundRecord.getOperator() == null || inboundRecord.getOperator().trim().isEmpty()) {
            return Result.error("操作员不能为空");
        }
        // 验证入库记录是否存在
        if (inboundRecordService.getById(inboundRecord.getId()) == null) {
            return Result.error("入库记录不存在");
        }
        boolean result = inboundRecordService.updateById(inboundRecord);
        return result ? Result.success(result) : Result.error("更新入库记录失败");
    }

    // 删除入库记录
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的入库记录ID");
        }
        // 验证入库记录是否存在
        if (inboundRecordService.getById(id) == null) {
            return Result.error("入库记录不存在");
        }
        boolean result = inboundRecordService.removeById(id);
        return result ? Result.success(result) : Result.error("删除入库记录失败");
    }
}
