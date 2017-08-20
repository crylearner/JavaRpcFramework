# JavaRpcFramework
一个具有良好分层的RPC框架，使用java语言实现


服务端使用
只支持普通的接口实现方式，也支持服务自动发现，基于annotation

e.g. giving a class below
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
 
 //register and start server.  where, RpcServiceAdapter.adapt(object) will do auto service discovery
 RpcServer server = new RpcServer();
 Hello hello = new Hello();
 server.registerService(RpcServiceAdapter.adapt(hello));
 server.serve(null, 12345);
