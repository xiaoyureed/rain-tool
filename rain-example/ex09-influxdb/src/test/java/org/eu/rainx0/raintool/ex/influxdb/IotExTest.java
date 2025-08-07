package org.eu.rainx0.raintool.ex.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import org.eu.rainx0.raintool.ex.influxdb.model.QuotaInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoyu
 * @time 2025/8/6 11:46
 */
@SpringBootTest
public class IotExTest {

    @Autowired
    InfluxDBClient client;

    @Test
    void test_add() {
        QuotaInfo quota = new QuotaInfo()
                .setDeviceId("111")
                .setQuotaId("1")
                .setQuotaName("湿度")
                .setRefVal("0-10")
                .setUnit("摄氏度")
                .setAlarm("1")
                .setValue(11D)
                ;
        client.getWriteApiBlocking().writeMeasurement(WritePrecision.NS, quota);
    }
}
