package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.spring;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * @author xiaoyu
 * @time 2025/7/18 16:03
 */
@ExtRocketMQTemplateConfiguration // 注入一个自定义 template, 名字 MyRocketmqTemplate
public class MyRocketmqTemplate extends RocketMQTemplate {
}
