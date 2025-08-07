package org.eu.rainx0.raintool.ex.javaagent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * @author xiaoyu
 * @time 2025/8/6 23:41
 */
public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                // which class to enhance
                .type(ElementMatchers.nameEndsWith("Bean"))
                .transform(new AgentBuilder.Transformer.ForAdvice()
                    .advice(
                        // which method you want to enhance
                        ElementMatchers.nameStartsWith("printInfo"),
                        "org.eu.rainx0.raintool.javaagent.AgentAdvice"
                    )
                )
                .installOn(inst);
    }
}
