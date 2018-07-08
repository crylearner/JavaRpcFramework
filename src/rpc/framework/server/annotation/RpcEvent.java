package rpc.framework.server.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcEvent {
	public static enum ActionType {SUBSCRIBE, UNSUBSCRIBE};
	String	method() default ""; //  rpc方法名，不填表示与修饰的方法同名
	String[] params() default {}; // rpc方法参数名列表
}
