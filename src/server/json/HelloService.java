package server.json;

import org.json.JSONObject;

import rpc.exception.RpcException;
import rpc.framework.server.RpcServiceInterface;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

public class HelloService implements RpcServiceInterface {
	private static final String TAG = "HelloService";
	private Hello mHello = null;
	
	public HelloService() {
		mHello = new Hello();
	}
	
	@Override
	public String[] list() {
		return new String[] {
				"HelloService.add",
				"HelloService.sayName",
				"HelloService.giveName",
				"HelloService.growUp",
				"HelloService.listenGrowUp",
				"HelloService.unlistenGrowUp"
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
		
		} else if ("HelloService.growUp".equals(method)) {
			return growUp(request);
		
		} else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}


	
	

	private RpcResponse sayName(RpcRequest request) {
		return new RpcResponse(request.getId(), mHello.sayName(), true);
	}

	private RpcResponse add(RpcRequest request) {
		JSONObject args = (JSONObject)request.getParams();
		int a = args.getInt("a");
		int b = args.getInt("b");
		return new RpcResponse(request.getId(), mHello.add(a,b), true);
	}
	
	private RpcResponse giveName(RpcRequest request) {
		JSONObject args = (JSONObject)request.getParams();
		mHello.giveName(args.getString("name"));
		return new RpcResponse(request.getId(), null, true);
	}
	
	private RpcResponse growUp(RpcRequest request) {
		int age = mHello.growUp();
		return new RpcResponse(request.getId(), age, true);
	}
	
	public void listenGrowUp(String type, Hello.AgeListener listener) {
		mHello.observeAge(type, listener);
	}
	
	public void unlistenGrowUp(String type, Hello.AgeListener listener) {
		mHello.unobserveAge(type, listener);
	}
	
}