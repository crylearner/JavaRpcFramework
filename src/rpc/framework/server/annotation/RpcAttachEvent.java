package rpc.framework.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcAttachEvent {
	String[] events() default {}; // 监听的事件类型
	Class<?> listener(); // 回调函数
}
