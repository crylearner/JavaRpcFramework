package rpc.json.service;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.framework.server.RpcServiceInterface;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

public class Client implements RpcServiceInterface{
	private static final String TAG = "Client";
	public static final String NAME = "global";
	public static final String METHOD_LOGIN = NAME+".login";
    public static final String METHOD_LOGOUT = NAME+".logout";
    private static final String METHOD_KEEPALIVE = NAME+".keepAlive";
	@Override
	public String[] list() {
		return new String[] {
				METHOD_LOGIN,
				METHOD_LOGOUT,
				METHOD_KEEPALIVE
		};
	}

	@Override
	public RpcResponse execute(RpcRequest request) {
		String method = request.getMethod();
		if (METHOD_LOGIN.equals(method)) {
			return login(request);
		} else if (METHOD_LOGOUT.equals(method)) {
			return logout(request);
		} else if (METHOD_KEEPALIVE.equals(method)) {
			return keepAlive(request);
		} else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}

	public RpcResponse login(RpcRequest request) {
		
		try {
			JSONObject params = new JSONObject();
			params.put("realm", "Login to XXXXXX");
			params.put("random", "asdf857231468");
			params.put("encryption", "Default");
			params.put("mac", "52544CFA0001");
			return new RpcResponse(request.getId(), params, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public RpcResponse logout(RpcRequest request) {
		return null;
	}
	
	public RpcResponse keepAlive(RpcRequest request) {
		try {
			JSONObject reqparams = (JSONObject)request.getParams();
			if (!reqparams.has("timeout")) {
				RpcLog.e(TAG, "keepAlive no timeout param");
				return null;
			}
			JSONObject params = new JSONObject();
			params.put("timeout", "60");
			return new RpcResponse(request.getId(), params, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
