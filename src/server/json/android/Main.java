package server.json.android;

import org.json.JSONObject;

import rpc.framework.server.RpcServer;
import rpc.framework.server.RpcServiceAdapter;
import rpc.util.RpcLog;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		RpcServer server = new RpcServer();
		// 普通类，没有继承自RpcServiceInterface。 使用
		ContentProviderService provider = new ContentProviderService();
		CursorService cursor = new CursorService();
		ContextService context = new ContextService();

		server.registerService(RpcServiceAdapter.adapt(provider));
		server.registerService(RpcServiceAdapter.adapt(cursor));
		server.registerService(RpcServiceAdapter.adapt(context));
		server.serve(null, 12346);
	}
	
}
