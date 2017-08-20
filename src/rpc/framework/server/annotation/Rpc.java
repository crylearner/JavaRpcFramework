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
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rpc {
	public static final String[] NO_PARAMS = new String[]{};
	String[] params() default {};
}