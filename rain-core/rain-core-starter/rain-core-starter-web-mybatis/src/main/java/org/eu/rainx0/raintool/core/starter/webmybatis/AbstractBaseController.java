package org.eu.rainx0.raintool.core.starter.webmybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.common.model.ResponseWrapper;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractIdEntity;
import org.eu.rainx0.raintool.core.starter.data.mybatis.util.MyBatisUtils;
import org.eu.rainx0.raintool.core.starter.data.mybatis.util.WrapperUtils;
import org.eu.rainx0.raintool.core.starter.data.mybatis.x.AbstractBaseServiceX;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 10:30
 */
public abstract class AbstractBaseController<
    ID,
    T extends AbstractIdEntity<ID>,
    S extends AbstractBaseServiceX<ID, T>
    > {

    @Autowired
    private S service;

    @GetMapping("/page")
    @Operation(summary = "find all with pagination")
    public ResponseEntity<ResponseWrapper<List<T>>> list(
        @RequestParam @Nullable @Validated T req // required 参数 和 @Nullable 选其一即可
    ) throws Throwable {
        Page<T> page;
        if (req == null) {
            page = service.page(MyBatisUtils.buildPage(ServletTools.getPageInfo()));
        } else {
            page = service.page(
                MyBatisUtils.buildPage(ServletTools.getPageInfo()),
                WrapperUtils.parse(req, service.getEntityClass())
            );
        }

        List<T> result = page.getRecords();

        return ResponseEntity.ok(ResponseWrapper.ok(result));
    }

    @GetMapping("/byid/{id}")
    @Operation(summary = "get by id")
    public ResponseEntity<ResponseWrapper<T>> getById(
        @PathVariable("id")
        @Parameter(description = "primary key", required = true)
        @NotBlank(message = "id cannot be empty")
        ID id
    ) throws Throwable {
        return ResponseEntity.ok(ResponseWrapper.ok(service.getById((Serializable) id)));
    }

    @PostMapping
    @Operation(summary = "save or update single one")
    public ResponseEntity<ResponseWrapper<?>> save(@RequestBody(required = true) T req) throws Throwable {
        service.saveOrUpdate(req);
        return ResponseEntity.ok(ResponseWrapper.ok(req.getId()));
    }

    @PostMapping("/batch")
    @Operation(summary = "batch save or update")
    public ResponseEntity<ResponseWrapper<?>> saveBatch(@RequestBody ArrayList<T> req) throws Throwable {
        service.saveOrUpdateBatch(req);
        return ResponseEntity.ok(ResponseWrapper.ok(req.stream().map(AbstractIdEntity::getId).collect(Collectors.toList())));
    }

    /**
     * todo batch 相关方法会报错, 如 removeBatchByIds()
     * @param ids
     * @return
     * @throws Throwable
     */
    @DeleteMapping
    @Operation(summary = "delete by ids")
    public ResponseEntity<ResponseWrapper<?>> deleteByIds(
        @Parameter(description = "primary keys", required = true) @RequestParam("ids") String[] ids
    ) throws Throwable {
        // List<String> collect = Arrays.stream(ids.split(",")).map(String::trim).collect(Collectors.toList());

        if (ids.length == 1) {
            service.removeById(ids[0]);
        } else if (ids.length > 1) {
            service.removeByIds(Arrays.stream(ids).toList());
        }

        return ResponseEntity.ok(ResponseWrapper.ok());
    }

}
