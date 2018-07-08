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
 * usage: @Rpc(params=["arg1","arg2"])
 *
 */ 
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
	String	name() default ""; //  rpc主服务名，不填表示与修饰的方法同名
}