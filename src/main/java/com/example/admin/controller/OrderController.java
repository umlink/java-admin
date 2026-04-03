package com.example.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.admin.annotation.OperateLog;
import com.example.admin.annotation.RepeatSubmit;
import com.example.admin.common.api.Result;
import com.example.admin.dto.CancelOrderDTO;
import com.example.admin.dto.CreateOrderDTO;
import com.example.admin.dto.OrderQueryDTO;
import com.example.admin.service.OrderService;
import com.example.admin.vo.CreateOrderVO;
import com.example.admin.vo.OrderDetailVO;
import com.example.admin.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@Tag(name = "订单管理", description = "订单相关接口")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @SaCheckLogin
    @Operation(summary = "创建订单", description = "创建新订单")
    @RepeatSubmit
    @OperateLog(module = "订单管理", description = "创建订单")
    public Result<CreateOrderVO> create(@Valid @RequestBody CreateOrderDTO dto) {
        return Result.success(orderService.create(dto));
    }

    /**
     * 分页查询订单列表
     */
    @PostMapping("/page")
    @SaCheckLogin
    @Operation(summary = "订单列表", description = "分页查询订单列表（仅查询自己的订单）")
    @OperateLog(module = "订单管理", description = "查询订单列表", recordResult = false)
    public Result<IPage<OrderVO>> page(@RequestBody OrderQueryDTO queryDTO) {
        return Result.success(orderService.page(queryDTO));
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    @Operation(summary = "订单详情", description = "查询订单详情（含明细）")
    @OperateLog(module = "订单管理", description = "查询订单详情", recordResult = false)
    public Result<OrderDetailVO> detail(@Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(orderService.detail(id));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    @SaCheckLogin
    @Operation(summary = "取消订单", description = "取消待支付订单")
    @RepeatSubmit
    @OperateLog(module = "订单管理", description = "取消订单")
    public Result<Void> cancel(@Valid @RequestBody CancelOrderDTO dto) {
        orderService.cancel(dto);
        return Result.success();
    }

}
