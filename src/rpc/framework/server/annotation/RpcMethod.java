/**
 * 
 */
package rpc.framework.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunshyran
 * usage: @Rpc(method="call_abc", params=["arg1","arg2"])
 * method 服务名。 可省略，默认同定义的函数名
 * params 形参名。 不可省略。
 * subject 是否是订阅， 默认false
 */ 
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcMethod {
	public static final String[] NO_PARAMS = new String[]{};
	String	method() default ""; //  rpc方法名，不填表示与修饰的方法同名
	String[] params() default {}; // 成员方法的形参名
	boolean subject() default false; 
}