package rpc.framework.service;

import rpc.framework.server.RpcServiceInterface;
import rpc.json.RpcRequest;
import rpc.json.RpcResponse;
import rpc.util.RpcLog;

public class MPTFacade implements RpcServiceInterface {
	private static final String TAG = "MPTFacade";
	public static final String NAME = "MPTFacade";

    public static final String METHOD_INSTANCE = NAME+".factory.instance";
    public static final String METHOD_DESTROY = NAME+".destroy";
    public static final String METHOD_CHECKIN = NAME+".userCheckin";
    public static final String METHOD_CHECKOUT = NAME+".userCheckout";
    public static final String METHOD_INVITE = NAME+".startTalkPhone";
    public static final String METHOD_HANGUP = NAME+".stopTalkPhone";
	
    @Override
	public String[] list() {
		return new String[] {
				METHOD_INSTANCE,
				METHOD_DESTROY,
				METHOD_CHECKIN,
				METHOD_CHECKOUT,
				METHOD_INVITE,
				METHOD_HANGUP
		};
	}
    
    
	@Override
	public RpcResponse execute(RpcRequest request) {
		String method = request.getMethod();
		if (METHOD_INSTANCE.equals(method)) {
			return instance(request);
		} else if (METHOD_DESTROY.equals(method)) {
			return destroy(request);
		} else if (METHOD_CHECKIN.equals(method)) {
			return checkin(request);
		} else if (METHOD_CHECKOUT.equals(method)) {
			return checkout(request);
		} else if (METHOD_INVITE.equals(method)) {
			return invite(request);
		} else if (METHOD_HANGUP.equals(method)) {
			return hangup(request);
		} else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}
	
	
	RpcResponse instance(RpcRequest request) {
		return null;
	}
	
	RpcResponse destroy(RpcRequest request) {
		return null;
	}
	RpcResponse checkin(RpcRequest request) {
		return null;
	}
	RpcResponse checkout(RpcRequest request) {
		return null;
	}
	RpcResponse invite(RpcRequest request) {
		return null;
	}
	RpcResponse hangup(RpcRequest request) {
		return null;
	}
	
	
    
    
    
}
