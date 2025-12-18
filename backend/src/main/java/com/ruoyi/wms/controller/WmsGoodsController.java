package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.common.PageResult;
import com.ruoyi.wms.common.Result;
import com.ruoyi.wms.domain.WmsGoods;
import com.ruoyi.wms.service.WmsGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class WmsGoodsController {

    @Autowired
    private WmsGoodsService goodsService;

    // 获取所有货物，支持搜索和分页
    @GetMapping
    public Result<PageResult<WmsGoods>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "goodsName", required = false) String goodsName,
            @RequestParam(value = "goodsCode", required = false) String goodsCode) {
        
        QueryWrapper<WmsGoods> wrapper = new QueryWrapper<>();
        if (goodsName != null && !goodsName.isEmpty()) {
            wrapper.like("goods_name", goodsName);
        }
        if (goodsCode != null && !goodsCode.isEmpty()) {
            wrapper.eq("goods_code", goodsCode);
        }
        
        // 获取所有匹配数据
        List<WmsGoods> allGoods = goodsService.list(wrapper);
        long total = allGoods.size();
        
        // 实现简单的分页逻辑
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allGoods.size());
        List<WmsGoods> pageList = allGoods.subList(startIndex, endIndex);
        
        // 构建分页结果
        PageResult<WmsGoods> pageResult = new PageResult<>(total, pageList, pageNum, pageSize);
        return Result.success(pageResult);
    }

    // 根据ID获取货物
    @GetMapping("/{id}")
    public Result<WmsGoods> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的货物ID");
        }
        WmsGoods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("货物不存在");
        }
        return Result.success(goods);
    }

    // 创建货物
    @PostMapping
    public Result<Boolean> save(@RequestBody WmsGoods goods) {
        if (goods == null) {
            return Result.error("货物信息不能为空");
        }
        if (goods.getGoodsCode() == null || goods.getGoodsCode().trim().isEmpty()) {
            return Result.error("货物编码不能为空");
        }
        if (goods.getGoodsName() == null || goods.getGoodsName().trim().isEmpty()) {
            return Result.error("货物名称不能为空");
        }
        if (goods.getGoodsType() == null || goods.getGoodsType().trim().isEmpty()) {
            return Result.error("货物类型不能为空");
        }
        if (goods.getUnit() == null || goods.getUnit().trim().isEmpty()) {
            return Result.error("单位不能为空");
        }
        if (goods.getPrice() == null || goods.getPrice().doubleValue() < 0) {
            return Result.error("价格不能为空且必须大于等于0");
        }
        boolean result = goodsService.save(goods);
        return result ? Result.success(result) : Result.error("创建货物失败");
    }

    // 更新货物
    @PutMapping
    public Result<Boolean> update(@RequestBody WmsGoods goods) {
        if (goods == null || goods.getId() == null) {
            return Result.error("货物信息或ID不能为空");
        }
        if (goods.getGoodsCode() == null || goods.getGoodsCode().trim().isEmpty()) {
            return Result.error("货物编码不能为空");
        }
        if (goods.getGoodsName() == null || goods.getGoodsName().trim().isEmpty()) {
            return Result.error("货物名称不能为空");
        }
        if (goods.getGoodsType() == null || goods.getGoodsType().trim().isEmpty()) {
            return Result.error("货物类型不能为空");
        }
        if (goods.getUnit() == null || goods.getUnit().trim().isEmpty()) {
            return Result.error("单位不能为空");
        }
        if (goods.getPrice() == null || goods.getPrice().doubleValue() < 0) {
            return Result.error("价格不能为空且必须大于等于0");
        }
        // 验证货物是否存在
        if (goodsService.getById(goods.getId()) == null) {
            return Result.error("货物不存在");
        }
        boolean result = goodsService.updateById(goods);
        return result ? Result.success(result) : Result.error("更新货物失败");
    }

    // 删除货物
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的货物ID");
        }
        // 验证货物是否存在
        if (goodsService.getById(id) == null) {
            return Result.error("货物不存在");
        }
        boolean result = goodsService.removeById(id);
        return result ? Result.success(result) : Result.error("删除货物失败");
    }

    // 根据货物编码查询
    @GetMapping("/code/{code}")
    public Result<WmsGoods> getByCode(@PathVariable String code) {
        if (code == null || code.trim().isEmpty()) {
            return Result.error("货物编码不能为空");
        }
        QueryWrapper<WmsGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_code", code);
        WmsGoods goods = goodsService.getOne(wrapper);
        if (goods == null) {
            return Result.error("货物不存在");
        }
        return Result.success(goods);
    }

    // 根据货物类型查询
    @GetMapping("/type/{type}")
    public Result<List<WmsGoods>> getByType(@PathVariable String type) {
        if (type == null || type.trim().isEmpty()) {
            return Result.error("货物类型不能为空");
        }
        QueryWrapper<WmsGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_type", type);
        return Result.success(goodsService.list(wrapper));
    }
    
    // 启用货物
    @PutMapping("/enable/{id}")
    public Result<Boolean> enable(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的货物ID");
        }
        // 验证货物是否存在
        WmsGoods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("货物不存在");
        }
        // 更新状态为启用（1表示启用）
        goods.setStatus(1);
        boolean result = goodsService.updateById(goods);
        return result ? Result.success(result) : Result.error("启用货物失败");
    }
    
    // 禁用货物
    @PutMapping("/disable/{id}")
    public Result<Boolean> disable(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("无效的货物ID");
        }
        // 验证货物是否存在
        WmsGoods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("货物不存在");
        }
        // 更新状态为禁用（0表示禁用）
        goods.setStatus(0);
        boolean result = goodsService.updateById(goods);
        return result ? Result.success(result) : Result.error("禁用货物失败");
    }
}
