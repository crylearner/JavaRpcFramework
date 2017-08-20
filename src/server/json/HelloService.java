package server.json;

import org.json.JSONObject;

import rpc.exception.RpcException;
import rpc.framework.server.RpcServiceInterface;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

public class HelloService implements RpcServiceInterface {
	private static final String TAG = "HelloService";
	String mName = "no one";
	public HelloService() {
	}
	
	@Override
	public String[] list() {
		return new String[] {
				"HelloService.add",
				"HelloService.sayName",
				"HelloService.giveName"
		};
	}

	@Override
	public RpcResponse execute(RpcRequest request) throws RpcException {
		String method = request.getMethod();
		if ("HelloService.add".equals(method)) {
			return add(request);
			
		} else if ("HelloService.sayName".equals(method)) {
			return sayName(request);
		
		} else if ("HelloService.giveName".equals(method)) {
			return giveName(request);
			
		} else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}

	
	public String sayName() {
		return mName;
	}
	
	public int add(int a, int b) {
		return a +b;
	}
	
	public void giveName(String name) {
		mName = name;
	}

	private RpcResponse sayName(RpcRequest request) {
		return new RpcResponse(request.getId(), sayName(), true);
	}

	private RpcResponse add(RpcRequest request) {
		JSONObject args = (JSONObject)request.getParams();
		int a = args.getInt("a");
		int b = args.getInt("b");
		return new RpcResponse(request.getId(), add(a,b), true);
	}
	
	private RpcResponse giveName(RpcRequest request) {
		JSONObject args = (JSONObject)request.getParams();
		giveName(args.getString("name"));
		return new RpcResponse(request.getId(), null, true);
	}
}

