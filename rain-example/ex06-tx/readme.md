## Concepts

```sh
4 features of Transaction:

- 原子性(Atomicity)  : 指令集合全部成功/全部失败
- 一致性(Consistency) : 事务执行前后, 整体数据不被破坏. eg: 转账前后, 钱的总流通量不变
- 隔离性(Isolation)  : 并发事务不会相互影响
- 持久性(Durability): 事务执行成功后, 数据被持久化存储



本地事务

- 原理
  
    使用aop(切面编程), 底层是基于动态代理, 在事务方法前后, conn.begin() conn.commit() conn.rollback()

- 用法

    - 声明式 @Transactional(rollback  = Exception.class/Throwable.class) 标注事务方法 (前提是@EnableTransactionManagement 开启事务支持)

        默认 rollbackFor只会回滚 RuntimeException/Error, 需要手动指定希望回滚的异常

    - 编程式  通过注入 TransactionTemplate来使用execute 方法
        //设置事务传播属性
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 设置事务的隔离级别,设置为读已提交（默认是ISOLATION_DEFAULT:使用的是底层数据库的默认的隔离级别）
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置是否只读，默认是false
        transactionTemplate.setReadOnly(true);
        // 默认使用的是数据库底层的默认的事务的超时时间
        transactionTemplate.setTimeout(30000);
        transactionTemplate.execute(txStatus -> { # TransactionCallback<T> / TransactionCallbackWithoutResult
          try {
            //...
            return Boolean.TRUE;
          } catch (Exception e) {
            txStatus.setRollbackOnly(); # rollback
            return null;
          }
          
        })
      

- 失效场景:

  - 非 public 的事务方法会失效 (应为是基于动态代理, private 方法无法被代理)
  - final 方法无法代理, 事务失效
  - 事务方法互相嵌套调用, 可能会失效, 不能直接 this.xxx 调用, 需要通过代理对象调用
      eg. AopContext.currentProxy().xxx()
  - 两个线程中的事务, 无法保证同时提交/回滚




分布式事务


- 本地消息表, 基于 mq 实现


- Seata

  - 概念
      TC(Tx coordinator) 事务协调者 -------- Seata server
        核心, 全局唯一, 维护子事务状态
      
      TM(Tx Manager) 事务管理器  --------------- Seata client
      
        顶层事务所在的服务, 负责开启/提交/回滚全局事务
      
      RM(Resource manager) 资源管理器------------- Seata client
        每个子事务所在的服务, 都是一个 RM, 与 TC 协商

  - 支持 4 种事务模式:
  
      AT模式(auto 模式): 二阶段提交协议
      
          事务提交分为了两个阶段
            1. 本地事务提交 (当前子事务中业务数据提交 + undo_log 提交 + 向 TC 申请当前数据的全局锁)
              undo_log 包含当前操作的数据的 前镜像/后镜像
              - 前镜像: 提取 sql 的 where 部分内容, 查询出修改前的数据, 称为前镜像
              - 后镜像: 执行完 sql后的数据, 称为后镜像
            2. TC 收集各个子事务状态, 都成功, 则提交全局事务, 所有子事务删除 undo_log, 
              任意一个失败, 通知所有子事务, 回滚: 
                - 通过后镜像校验数据是否被外部修改, 如果被修改, 根据配置的 策略处理, 没被修改, 则根据前镜像恢复数据 
        
      TCC 模式: 全手动模式的二阶提交协议
      Saga 模式: 适合事务持续时间长的场景, 应为其他模式都会申请一个全局锁, 不适合长事务的场景
        基于 mq, 实现最终一致性
      XA 模式 
        类似 AT 的二阶段提交, 但是基于数据库特性实现, 性能底下

  - 使用
      引入依赖后, 自动读取 file.conf 中的配置
      在全局事务入口方法, 标注 @GlobalTransactional, 其他子事务, 使用 @Transactional 标注
     
```
