package server.json;

import rpc.framework.server.RpcServer;
import rpc.framework.server.RpcServiceAdapter;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		RpcServer server = new RpcServer();
		// 继承自RpcServiceInterface的服务
//		HelloService helloservice = new HelloService();
//		server.registerService(helloservice);
//		server.registerSubject("HelloService.listenGrowUp", helloservice);
//		server.registerSubject("HelloService.unlistenGrowUp", helloservice);
		
		// 普通类，没有继承自RpcServiceInterface。 使用
		HelloRpcWrapper hello = new HelloRpcWrapper();
		server.registerService(RpcServiceAdapter.adapt(hello));
		//server.registerSubject("Hello.observeAge", hello);
		//server.registerSubject("Hello.unobserveAge", hello);
		server.serve(null, 12346);
	}
	
}
