package org.eu.rainx0.raintool.ex.influxdb.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * 指标
 *
 * @author xiaoyu
 * @time 2025/8/6 10:27
 */
@Data
@Accessors(chain = true)
@Measurement(name = "quota")
public class QuotaInfo {

    @Column(tag = true)
    private String deviceId;

    @Column(tag = true)
    private String quotaId;

    @Column(tag = true)
    private String quotaName;

    @Column(tag = true)
    private String alarm; // 是否为告警指标, 1 true, 0 false

    @Column(tag = true)
    private String level; // 告警级别

    @Column(tag = true)
    private String alarmName; // 告警名称

    @Column(tag = true) // 单位
    private String unit;

    @Column(tag = true)
    private String refVal; // 参考值

//    ------------------------------------------------------------------------------------------------------------------

    @Column
    private Double value;

    private String strVal;

    @Column(timestamp = true)
    private Instant timestamp;

}
