package org.eu.rainx0.raintool.ex.matchengin.init;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;

import org.eu.rainx0.raintool.ex.matchengin.disruptor.DisruptorTemplate;
import org.eu.rainx0.raintool.ex.matchengin.entity.EntrustOrder;
import org.eu.rainx0.raintool.ex.matchengin.model.Order;
import org.eu.rainx0.raintool.ex.matchengin.repository.EntrustOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * engine 启动, 需要将 db 中的委托单加载到内存中
 * @author xiaoyu
 * @time 2025/7/20 22:10
 */
@Component
public class DataLoaderRunner implements ApplicationRunner {

    @Autowired
    private EntrustOrderRepository entrustOrderRepository;

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 获取所有未完成委托单, 按照创建时间倒序排序
        List<EntrustOrder> orders = entrustOrderRepository.findEntrustOrdersByStatusOrderByCreatedAtDesc(0);
        for (EntrustOrder o : orders) {
            disruptorTemplate.publish(convert(o));
        }
    }

    private Order convert(EntrustOrder o) {
        BigDecimal amount = o.getVolume().add(o.getDealAmount().negate());
        long time = o.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Order()
            .setOrderId(o.getId())
            .setSymbol(o.getSymbol())
            .setPrice(o.getPrice())
            .setAmount(amount) // 交易量 = 总数量 - 已经成交数量
            .setTime(time)
            .setOrderDirection(o.getDirection())
            ;
    }
}
