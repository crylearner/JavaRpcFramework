package server.json;

import org.json.JSONObject;

import rpc.framework.server.RpcServiceInterface;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

public class HelloService implements RpcServiceInterface {
	private static final String TAG = "HelloService";
	public HelloService() {
	}
	
	@Override
	public String[] list() {
		return new String[] {
				"HelloService.add",
				"HelloService.sayHello"
		};
	}

	@Override
	public RpcResponse execute(RpcRequest request) {
		String method = request.getMethod();
		if ("HelloService.add".equals(method)) {
			return add(request);
			
		} else if ("HelloService.sayHello".equals(method)) {
			return sayHello(request);
			
		} else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}

	
	public String sayHello() {
		return "Hello world";
	}
	
	public int add(int a, int b) {
		return a +b;
	}

	private RpcResponse sayHello(RpcRequest request) {
		return new RpcResponse(request.getId(), sayHello(), true);
	}

	private RpcResponse add(RpcRequest request) {
		JSONObject args = (JSONObject)request.getParams();
		int a = args.getInt("a");
		int b = args.getInt("b");
		return new RpcResponse(request.getId(), add(a,b), true);
	}
	
}

