package rpc.framework.server;

import java.lang.reflect.Method;
import java.util.Arrays;

class TEST {
			
	@Rpc
	public int m(boolean b) { return b?1:0;}
	
	public static void main(String [] args) {
		TEST t = new TEST();
		try {
			Method m = t.getClass().getMethod("m", boolean.class);
			Rpc r = m.getAnnotation(Rpc.class);
			String[] p = r.params();
			System.out.println(Arrays.toString(p));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}