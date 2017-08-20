package server.json;

import rpc.framework.server.annotation.Rpc;

public class Hello {

	public Hello() {
		// TODO Auto-generated constructor stub
	}
	
	@Rpc
	public String sayHello() {
		return "Hello world";
	}
	
	@Rpc(params={"a","b"})
	public int add(int a, int b) {
		return a +b;
	}
}
