package org.eu.rainx0.raintool.ex.javaagent;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author xiaoyu
 * @time 2025/8/6 23:32
 */
public class test1 {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        //创建的类型将扩展 Object 类并覆盖其 toString 方法，该方法应返回固定值 Hello World！
        Class<?> dynType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello."))
                .make()
                .load(test1.class.getClassLoader())
                .getLoaded();
        String hello = dynType.newInstance().toString();
        System.out.println(hello);
    }
}
