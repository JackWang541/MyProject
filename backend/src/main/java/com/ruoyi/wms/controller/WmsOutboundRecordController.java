package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.WmsGoods;
import com.ruoyi.wms.domain.WmsInventory;
import com.ruoyi.wms.domain.WmsOutboundRecord;
import com.ruoyi.wms.domain.WmsWarehouse;
import com.ruoyi.wms.service.WmsGoodsService;
import com.ruoyi.wms.service.WmsInventoryService;
import com.ruoyi.wms.service.WmsOutboundRecordService;
import com.ruoyi.wms.service.WmsWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outbound")
public class WmsOutboundRecordController {

    @Autowired
    private WmsOutboundRecordService outboundRecordService;
    
    @Autowired
    private WmsWarehouseService warehouseService;
    
    @Autowired
    private WmsGoodsService goodsService;
    
    @Autowired
    private WmsInventoryService inventoryService;

    // 获取所有出库记录，包含仓库名称和货物名称，支持分页和搜索
    @GetMapping
    public Result<PageResult<WmsOutboundRecord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String recordNo,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String goodsName) {
        
        // 查询所有出库记录
        List<WmsOutboundRecord> allOutbound = outboundRecordService.list();
        
        // 填充仓库名称和货物名称
        for (WmsOutboundRecord record : allOutbound) {
            fillOutboundRecordNames(record);
        }
        
        // 过滤记录
        List<WmsOutboundRecord> filteredOutbound = allOutbound;
        if (recordNo != null && !recordNo.isEmpty()) {
            filteredOutbound = filteredOutbound.stream()
                    .filter(rec -> rec.getRecordNo() != null && rec.getRecordNo().contains(recordNo))
                    .toList();
        }
        if (warehouseName != null && !warehouseName.isEmpty()) {
            filteredOutbound = filteredOutbound.stream()
                    .filter(rec -> rec.getWarehouseName() != null && rec.getWarehouseName().contains(warehouseName))
                    .toList();
        }
        if (goodsName != null && !goodsName.isEmpty()) {
            filteredOutbound = filteredOutbound.stream()
                    .filter(rec -> rec.getGoodsName() != null && rec.getGoodsName().contains(goodsName))
                    .toList();
        }
        
        // 分页处理
        int total = filteredOutbound.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<WmsOutboundRecord> paginatedOutbound = start < total ? filteredOutbound.subList(start, end) : List.of();
        
        return Result.success(new PageResult<>(total, paginatedOutbound));
    }

    // 根据ID获取出库记录，包含仓库名称和货物名称
    @GetMapping("/{id}")
    public Result<WmsOutboundRecord> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的出库记录ID");
        }
        WmsOutboundRecord outboundRecord = outboundRecordService.getById(id);
        if (outboundRecord == null) {
            return Result.error("出库记录不存在");
        }
        // 填充仓库名称和货物名称
        fillOutboundRecordNames(outboundRecord);
        return Result.success(outboundRecord);
    }

    // 根据仓库ID获取出库记录，包含仓库名称和货物名称
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<WmsOutboundRecord>> getByWarehouseId(@PathVariable Long warehouseId) {
        if (warehouseId == null || warehouseId <= 0) {
            return Result.error("无效的仓库ID");
        }
        QueryWrapper<WmsOutboundRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        List<WmsOutboundRecord> outboundRecordList = outboundRecordService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsOutboundRecord record : outboundRecordList) {
            fillOutboundRecordNames(record);
        }
        return Result.success(outboundRecordList);
    }

    // 根据货物ID获取出库记录，包含仓库名称和货物名称
    @GetMapping("/goods/{goodsId}")
    public Result<List<WmsOutboundRecord>> getByGoodsId(@PathVariable Long goodsId) {
        if (goodsId == null || goodsId <= 0) {
            return Result.error("无效的货物ID");
        }
        QueryWrapper<WmsOutboundRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        List<WmsOutboundRecord> outboundRecordList = outboundRecordService.list(wrapper);
        // 填充仓库名称和货物名称
        for (WmsOutboundRecord record : outboundRecordList) {
            fillOutboundRecordNames(record);
        }
        return Result.success(outboundRecordList);
    }
    
    // 填充出库记录的仓库名称和货物名称
    private void fillOutboundRecordNames(WmsOutboundRecord record) {
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

    // 创建出库记录
    @PostMapping
    public Result<Boolean> save(@RequestBody WmsOutboundRecord outboundRecord) {
        if (outboundRecord == null) {
            return Result.error("出库记录信息不能为空");
        }
        if (outboundRecord.getRecordNo() == null || outboundRecord.getRecordNo().trim().isEmpty()) {
            return Result.error("记录编号不能为空");
        }
        if (outboundRecord.getWarehouseId() == null || outboundRecord.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (outboundRecord.getGoodsId() == null || outboundRecord.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (outboundRecord.getQuantity() == null || outboundRecord.getQuantity() <= 0) {
            return Result.error("数量不能为空且必须大于0");
        }
        if (outboundRecord.getUnitPrice() == null || outboundRecord.getUnitPrice().doubleValue() < 0) {
            return Result.error("单价不能为空且必须大于等于0");
        }
        if (outboundRecord.getTotalAmount() == null || outboundRecord.getTotalAmount().doubleValue() < 0) {
            return Result.error("总金额不能为空且必须大于等于0");
        }
        if (outboundRecord.getCustomer() == null || outboundRecord.getCustomer().trim().isEmpty()) {
            return Result.error("客户不能为空");
        }
        if (outboundRecord.getOperator() == null || outboundRecord.getOperator().trim().isEmpty()) {
            return Result.error("操作员不能为空");
        }
        
        // 检查货物状态，只有启用状态（1）的货物才能出库
        WmsGoods goods = goodsService.getById(outboundRecord.getGoodsId());
        if (goods == null) {
            return Result.error("货物不存在");
        }
        if (goods.getStatus() == null || goods.getStatus() != 1) {
            return Result.error("只有启用状态的货物才能出库");
        }
        
        // 检查库存是否足够
        QueryWrapper<WmsInventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", outboundRecord.getWarehouseId())
                   .eq("goods_id", outboundRecord.getGoodsId());
        
        WmsInventory inventory = inventoryService.getOne(queryWrapper);
        
        if (inventory == null) {
            return Result.error("该仓库中没有该货物的库存记录");
        }
        
        if (inventory.getQuantity() < outboundRecord.getQuantity()) {
            return Result.error("库存不足，无法完成出库");
        }
        
        // 保存出库记录
        boolean result = outboundRecordService.save(outboundRecord);
        if (!result) {
            return Result.error("创建出库记录失败");
        }
        
        // 更新库存数量
        try {
            Integer newQuantity = inventory.getQuantity() - outboundRecord.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryService.updateById(inventory);
        } catch (Exception e) {
            // 如果库存更新失败，可能需要回滚出库记录
            // 这里为了简化，只记录错误并返回
            e.printStackTrace();
            return Result.error("出库成功，但库存更新失败");
        }
        
        return Result.success(result);
    }

    // 更新出库记录
    @PutMapping
    public Result<Boolean> update(@RequestBody WmsOutboundRecord outboundRecord) {
        if (outboundRecord == null || outboundRecord.getId() == null) {
            return Result.error("出库记录信息或ID不能为空");
        }
        if (outboundRecord.getRecordNo() == null || outboundRecord.getRecordNo().trim().isEmpty()) {
            return Result.error("记录编号不能为空");
        }
        if (outboundRecord.getWarehouseId() == null || outboundRecord.getWarehouseId() <= 0) {
            return Result.error("仓库ID不能为空且必须大于0");
        }
        if (outboundRecord.getGoodsId() == null || outboundRecord.getGoodsId() <= 0) {
            return Result.error("货物ID不能为空且必须大于0");
        }
        if (outboundRecord.getQuantity() == null || outboundRecord.getQuantity() <= 0) {
            return Result.error("数量不能为空且必须大于0");
        }
        if (outboundRecord.getUnitPrice() == null || outboundRecord.getUnitPrice().doubleValue() < 0) {
            return Result.error("单价不能为空且必须大于等于0");
        }
        if (outboundRecord.getTotalAmount() == null || outboundRecord.getTotalAmount().doubleValue() < 0) {
            return Result.error("总金额不能为空且必须大于等于0");
        }
        if (outboundRecord.getCustomer() == null || outboundRecord.getCustomer().trim().isEmpty()) {
            return Result.error("客户不能为空");
        }
        if (outboundRecord.getOperator() == null || outboundRecord.getOperator().trim().isEmpty()) {
            return Result.error("操作员不能为空");
        }
        // 验证出库记录是否存在
        if (outboundRecordService.getById(outboundRecord.getId()) == null) {
            return Result.error("出库记录不存在");
        }
        boolean result = outboundRecordService.updateById(outboundRecord);
        return result ? Result.success(result) : Result.error("更新出库记录失败");
    }

    // 删除出库记录
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的出库记录ID");
        }
        // 验证出库记录是否存在
        if (outboundRecordService.getById(id) == null) {
            return Result.error("出库记录不存在");
        }
        boolean result = outboundRecordService.removeById(id);
        return result ? Result.success(result) : Result.error("删除出库记录失败");
    }
}
