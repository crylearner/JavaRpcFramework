package server.json;

import rpc.framework.server.annotation.RpcMethod;
import rpc.framework.server.annotation.RpcService;

@RpcService(name="Hello")
public class HelloRpcWrapper  {
	private Hello mHello = null;
	
	public HelloRpcWrapper() {
		mHello = new Hello();
	}
	
	@RpcMethod
	public String sayName() {
		return mHello.sayName();
	}
	
	@RpcMethod(params={"a","b"})
	public int add(int a, int b) {
		return mHello.add(a, b);
	}
	
	@RpcMethod(params={"name"})
	public void giveName(String name) {
		mHello.giveName(name);
	}
	
	@RpcMethod
	public int growUp() {
		return mHello.growUp();
	}
	
	@RpcMethod(params= {"type", "listener"}, subject=true)
	public void observeAge(String type, Hello.AgeListener listener) {
		mHello.observeAge(type, listener);
	}
	
	@RpcMethod(params= {"type", "listener"}, subject=true)
	public void unobserveAge(String type, Hello.AgeListener listener) {
		mHello.unobserveAge(type, listener);
	}
	
 }
