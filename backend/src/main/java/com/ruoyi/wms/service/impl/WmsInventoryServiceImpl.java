package com.ruoyi.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wms.domain.WmsInventory;
import com.ruoyi.wms.mapper.WmsInventoryMapper;
import com.ruoyi.wms.service.WmsInventoryService;
import org.springframework.stereotype.Service;

@Service
public class WmsInventoryServiceImpl extends ServiceImpl<WmsInventoryMapper, WmsInventory> implements WmsInventoryService {
}
