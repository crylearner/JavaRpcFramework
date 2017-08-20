package server.json;

import rpc.framework.server.RpcServer;
import rpc.framework.server.RpcServiceAdapter;
import rpc.framework.server.RpcServiceInterface;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		RpcServer server = new RpcServer();
		// 继承自RpcServiceInterface的服务
		HelloService helloservice = new HelloService();
		server.registerService(helloservice);
		
		// 普通类，没有继承自RpcServiceInterface。 使用
		Hello hello = new Hello();
		server.registerService(RpcServiceAdapter.adapt(hello));
		
		server.serve(null, 12345);
	}
	
}
