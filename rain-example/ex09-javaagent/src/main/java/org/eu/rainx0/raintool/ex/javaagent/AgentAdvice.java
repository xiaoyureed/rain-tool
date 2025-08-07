package org.eu.rainx0.raintool.ex.javaagent;

import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

/**
 * java -javaagent:agent.jar -jar app.jar
 *
 * @author xiaoyu
 * @time 2025/8/6 23:08
 */
public class AgentAdvice {
    @Advice.OnMethodEnter
    public static void enter(@Advice.This Object obj) {
        System.out.println("method enter");
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.This Object obj) {
        System.out.println("method exit");
    }

}
