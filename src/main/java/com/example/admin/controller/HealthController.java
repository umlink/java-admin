package com.example.admin.controller;

import com.example.admin.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "健康检查", description = "健康检查接口")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Result<String> health() {
        return Result.success("OK");
    }
}
