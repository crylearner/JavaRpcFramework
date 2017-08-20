package server.json;

import rpc.framework.server.annotation.Rpc;

public class Hello {
	String mName = "no one";
	public Hello() {
		
	}
	
	@Rpc
	public String sayName() {
		return mName;
	}
	
	@Rpc(params={"a","b"})
	public int add(int a, int b) {
		return a +b;
	}
	
	@Rpc(params={"name"})
	public void giveName(String name) {
		mName = name;
	}
 }
