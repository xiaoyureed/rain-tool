package org.eu.rainx0.raintool.ex.influxdb.raw;

import com.influxdb.client.*;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.apache.commons.io.build.AbstractOrigin;
import org.eu.rainx0.raintool.core.starter.util.BeanTools;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyu
 * @time 2025/8/5 18:29
 */
public class WriteData {
    public static void main(String[] args) {
        WriteData demo = new WriteData();
        try (InfluxDBClient client = InfluxDBClientFactory.create(
                "http://localhost:8086",
                "yvAfZRuoSvUq3SKueLiLHAY__jvRQq_ksCSvz5MRtS6wUQ_MS3HBF2Zm4_CjXwfJestt4Jl3PfRO4hPHpviM4Q==".toCharArray(),
                "hi", "b1"
        )) {
//            demo.write1(client);
//            demo.write2(client);
            demo.query1(client);
        }


    }

    private void query1(InfluxDBClient client) {
        String query = """
                from(bucket: "b1")
                |> range(start: -1h)
                |> filter(fn: (r) => r["_measurement"] == "server_performance")
                """;

        QueryApi queryApi = client.getQueryApi();

        List<FluxTable> table = queryApi.query(query);
        for (FluxTable t : table) {
            List<FluxRecord> records = t.getRecords();
            records.forEach(r -> {
                List<Object> row = r.getRow();
                System.out.println(row);
            });
        }

        System.out.println("-------------------");

        List<ServerPerformance> series = queryApi.query(query, ServerPerformance.class);
        System.out.println("length: " + series.size());
        for (ServerPerformance item : series) {
            System.out.println(item);
        }
    }

    private void write3(InfluxDBClient client) {
        // 异步
        WriteApi writeApi = client.makeWriteApi(WriteOptions.builder()
                .batchSize(100) // 达到 100条写一次
                .flushInterval(2000) // 2s也刷一次盘, 即写一次
                .build());
//        writeApi.writeMeasurement(WritePrecision.NS, );
    }

    private void write2(InfluxDBClient client) {
        ServerPerformance p = new ServerPerformance()
                .setRegion("us-west")
                .setHost("app-server-2")
                .setCpuLoad(0.78)
                .setMemoryUsage(4096L)
                .setDiskFreeGb(250L)
                .setTimestamp(LocalDateTime.now());
        // 获取阻塞式写入 API
        client.getWriteApiBlocking().writeMeasurement(WritePrecision.NS, p);

    }

    private void write1(InfluxDBClient client) {

        // 创建一个数据点 (Point)
        Point point = Point.measurement("server_performance")
                .addTag("region", "us-west")
                .addTag("host", "host1")
                .addField("cpu_load", 0.64)
                .addField("memory_usage", 45.8)
                // 时间戳
//                    .time(LocalDateTime.now().toInstant(ZoneOffset.of("+8")), WritePrecision.MS)
//                    .time(LocalDateTime.now(ZoneId.systemDefault()).toEpochSecond(ZoneOffset.UTC), WritePrecision.MS)
                // 存入 influx 会自动转为 utc, 所以提前+8h
                .time(LocalDateTime.now()
//                            .plusHours(8)
                                // 默认是 UTC 时间, 这里指定为上海时间, 所以上面一行可以不必+8h 了
                                .atZone(ZoneId.of("Asia/Shanghai"))
                                .toInstant(),
//                            WritePrecision.MS
                        WritePrecision.NS
                );

        System.out.println("Writing data point: " + point.toLineProtocol());

        client.getWriteApiBlocking().writePoint(point);

    }

}
