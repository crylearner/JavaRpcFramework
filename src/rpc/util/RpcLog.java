
package rpc.util;


public class RpcLog {
	static public void i(String tag, String text) {
		System.out.println(tag + ": " + text);
	}
	
	static public void d(String tag, String text) {
		System.out.println(tag + ": " + text);
	}
	
	static public void w(String tag, String text) {
		System.out.println(tag + ": " + text);
	}
	
	static public void e(String tag, String text) {
		System.out.println(tag + ": " + text);
	}
	
	static public void e(String tag, Exception e) {
//		for (StackTraceElement i:e.getStackTrace())
//			System.out.println(tag + ": " + i.toString());
		System.out.print(tag + ": ");
		e.printStackTrace(System.out);
	}
	
}
