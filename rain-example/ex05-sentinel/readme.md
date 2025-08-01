## Concepts

```shell
规则:
  流量控制 (FlowRule)
    如每秒仅仅允许 5 个请求
    
    模式:
      直接 (默认) 
      链路  : 如service层通过 @SentinelResource("crateOrder") 定义一个资源, 在 web 层有两个 api, /normal-create 和 /seckill-create 调用 service 层的 crateOrder,
            链路模式可以指定一个资源名称如 /seckill-create, 从这个链路进来的请求会被流控, /normal-create 不会被流控
      关联  : 如有两个 api 如下 /writeDb, /readDb, 希望实现当 writeDb 流量很大时, 限制 readDb 的访问, 防止影响writeDb, writeDb 流量不大时, 随便访问 readDb,
      
    流控效果
      快速失败/直接拒绝 : 溢出的请求会直接抛异常
      warm up(预热) : 设置 qps 为 10, 预热时间 10s, 那么效果是 10s 内, 允许的 qps 会逐渐从 0 增加到 10, 直到达到设置的 qps
      排队等待:   设置 qps 为 2, 排队timeout 为 20s, 效果是
              溢出的请求会排队, 在 timeout 时间内, 如果能被处理不会被 block, 否则 block
    
    
  熔断降级 (DegradeRule)
  
    策略:
      慢调用比例:  如 设置最大 RT 1000ms, 比例 0.8, 统计时长 5000ms, 最小请求数 5, 那么表示 5s 内, 当请求数量超过 5 ,当 80% 的请求的 RT 都大于 1000ms 时,开始熔断
      异常比例
      异常数
  
  系统保护 (SystemRule)
    根据CPU使用率、内存使用率进行限流
    
  热点参数限流 (ParamFlowRule) : 可以根据 api 的参数进行限流
    默认 rest api 不支持热点参数限流, 需要配合 @SentinelResource("xxx") 手动定义一个资源名, 不要和默认的资源名重合 
  
  来源访问限流 (AuthorityRule)
    只允许指定上游/下游



配置方式:

- 默认所有 web api 还有 openfeign client 都是 Sentinel 资源, 可以设置流控策略

- 普通自定义方法若需要控制, 需要手动 @SentinelResource("xxx", blockHandler = "blockHandlerFunction", fallback = "xxxFallbackFunc") 定义一个资源

      - blockHandlerFunction(..., BlockException ex) 函数参数基本和该受控自定义方法一致, 最后一个参数为 BlockException
      - fallback 指定的方法是处理 通用异常的 (即最后一个参数/异常 是通用异常 Exception)
          优先级没有 blockHandler 高
      - 若blockHandler/fallback 均没有指定, 会抛出 BlockException到上层, 可以在 @ControllerAdvice 中处理
      
          也可实现 BlockExceptionHandler @bean 处理异常
```
