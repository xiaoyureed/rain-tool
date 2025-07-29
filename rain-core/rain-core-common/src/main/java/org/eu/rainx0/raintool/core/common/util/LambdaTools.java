package org.eu.rainx0.raintool.core.common.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:35
 */
public class LambdaTools {
    private LambdaTools() {
    }

    /**
     * 包装普通函数（Function）
     * <p>
     * 注：方法签名中的" throws E "编译器会提示多余，但其实是为了将实际的异常向外传递，如果不这么做：
     * 1.外层代码中编译器将无法提示有异常需要处理
     * 2.也无法主动在外层捕获具体的异常，如果尝试try一个具体的异常，编译器将提示：
     * 在try语句体中永远不会抛出相应异常（Exception 'XXX' is never thrown in the corresponding try block）
     */
    public static <T, R, E extends Throwable> Function<T, R> wrapFunction(FunctionWithExceptions<T, R, E> function) throws E {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装双入参普通函数（BiFunction）
     */
    public static <T, U, R, E extends Throwable> BiFunction<T, U, R> wrapBiFunction(BiFunctionWithExceptions<T, U, R, E> biFunction) throws E {
        return (t, u) -> {
            try {
                return biFunction.apply(t, u);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装消费函数（Consumer）
     */
    public static <T, E extends Throwable> Consumer<T> wrapConsumer(ConsumerWithExceptions<T, E> consumer) throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * 包装双重消费函数（BiConsumer）
     */
    public static <T, U, E extends Throwable> BiConsumer<T, U> wrapBiConsumer(BiConsumerWithExceptions<T, U, E> biConsumer) throws E {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * 包装生产函数（Supplier）
     */
    public static <T, E extends Throwable> Supplier<T> wrapSupplier(SupplierWithExceptions<T, E> function) throws E {
        return () -> {
            try {
                return function.get();
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装条件函数（Predicate）
     */
    public static <T, E extends Throwable> Predicate<T> wrapPredicate(PredicateWithExceptions<T, E> predicate) throws E {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
                return false;
            }
        };
    }

    /**
     * 包装双入参条件函数（BiPredicate）
     */
    public static <T, U, E extends Throwable> BiPredicate<T, U> wrapBiPredicate(BiPredicateWithExceptions<T, U, E> predicate) throws E {
        return (t, u) -> {
            try {
                return predicate.test(t, u);
            } catch (Throwable exception) {
                throwAsUnchecked(exception);
                return false;
            }
        };
    }

    /**
     * 包装纯执行函数（Runnable）
     */
    public static <E extends Throwable> void wrapRunnable(RunnableWithExceptions<E> runnable) throws E {
        try {
            runnable.run();
        } catch (Throwable exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * 如果一段代码确保不会抛出所申明的异常，可以使用该方法进行包装
     * 如：new String(byteArr, "UTF-8")申明了UnsupportedEncodingException，
     * 但编码"UTF-8"是必定不会抛异常的，使用sure进行包装
     * String text = sure(() -> new String(byteArr, "UTF-8"))
     * <p>
     * 注： sure方法有一定的风险，因为它隐藏了可能的异常申明，所以请谨慎使用，确保(sure)不会抛出异常才可以使用
     */
    public static <R, E extends Throwable> R sure(SupplierWithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        } catch (Throwable exception) {
            throwAsUnchecked(exception);
            // 用来解决在调用方法时提示可能NPE的警告
            throw new UnreachableException();
        }
    }

    /**
     * 同上
     *
     * @param runner
     * @param <E>
     */
    public static <E extends Throwable> void sure(RunnableWithExceptions<E> runner) {
        try {
            runner.run();
        } catch (Throwable exception) {
            throwAsUnchecked(exception);
            // 用来解决在调用方法时提示可能NPE的警告
            throw new UnreachableException();
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Throwable exception) throws E {
        throw (E) exception;
    }

    @FunctionalInterface
    public interface ConsumerWithExceptions<T, E extends Throwable> {
        void accept(T t) throws E;
    }


    @FunctionalInterface
    public interface BiConsumerWithExceptions<T, U, E extends Throwable> {
        void accept(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface FunctionWithExceptions<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface BiFunctionWithExceptions<T, U, R, E extends Throwable> {
        R apply(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface SupplierWithExceptions<T, E extends Throwable> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface RunnableWithExceptions<E extends Throwable> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface PredicateWithExceptions<T, E extends Throwable> {
        boolean test(T t) throws E;
    }

    @FunctionalInterface
    public interface BiPredicateWithExceptions<T, U, E extends Throwable> {
        boolean test(T t, U u) throws E;
    }

    private static class UnreachableException extends RuntimeException {

    }
}
