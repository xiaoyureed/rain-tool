package org.eu.rainx0.raintool.example.hello.service.web;

import java.time.LocalDateTime;

import org.eu.raintool.example.hello.serviceapi.HelloQ;
import org.eu.raintool.example.hello.serviceapi.HelloR;
import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/7/2 13:28
 */
@RestController
@Tag(name = "hello 控制器")
@RequestMapping("hello")
@Slf4j
public class HelloController {

    @PostMapping
    @Operation(summary = "hello 接口")
    public ResponseEntity<ResponseWrapper<HelloR>> hello(@RequestBody @Nullable HelloQ req) {
        return ResponseEntity.ok(ResponseWrapper.ok(new HelloR().setTime(LocalDateTime.now())));
    }

    @GetMapping({"", "/"})
    @Operation(summary = "测试方法 index")
    public String index() {
        log.debug("!!! hello index");
        return "hello index";
    }
}
