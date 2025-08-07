package org.eu.rainx0.raintool.ex.influxdb.raw;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author xiaoyu
 * @time 2025/8/5 22:47
 */
@Data
@Measurement(name = "server_performance")
@Accessors(chain = true)
public class ServerPerformance {
    /**
     * Tag: 服务器所在区域 (例如: us-west, eu-central)
     * Tag 会被索引，适合用于查询过滤。
     */
    @Column(tag = true)
    private String region;

    /**
     * Tag: 主机名 (例如: server-01, app-worker-a)
     */
    @Column(tag = true)
    private String host;

    /**
     * Field: CPU 负载
     * Field 是实际的测量值。
     */
    @Column
    private Double cpuLoad;

    /**
     * Field: 内存使用量 (MB)
     * 字段名与 InfluxDB 中的 field key 相同 (memoryUsage)。
     */
    @Column
    private Long memoryUsage;

    /**
     * Field: 另一个示例字段
     * 在 InfluxDB 中存储为 "disk_free_gb"。
     */
    @Column(name = "disk_free_gb")
    private Long diskFreeGb;

    /**
     * Timestamp: 数据点的时间戳
     * 必须是 java.time.Instant 类型。
     */
    @Column(timestamp = true)
    private Instant timestamp;

    public ServerPerformance setTimestamp(LocalDateTime now) {
        this.timestamp = now.atZone(ZoneId.systemDefault()).toInstant();
        return this;
    }
}
