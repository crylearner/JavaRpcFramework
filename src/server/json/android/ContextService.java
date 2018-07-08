package server.json.android;

import org.json.JSONObject;

import rpc.framework.server.annotation.RpcMethod;
import rpc.framework.server.annotation.RpcService;

// 最终应该使用android中的BroadcastReceiver代替
interface BroadcastReceiver {
	public void onReceive(JSONObject intent);
}

@RpcService(name="Context")
public class ContextService {
	
	@RpcMethod(params= {"intent"})
	public void startActivity(JSONObject intent) {
		
	}
	
	@RpcMethod(params= {"intent", "permission"})
	public void sendBroadcast(JSONObject intent, String permission) {
            
		
	}
	
	@RpcMethod(params= {"intent", "permission"})
	public void sendOrderedBroadcast(JSONObject intent, String permission) {
            
		
	}
	
	@RpcMethod(params= {"intent"})
	public void sendStickyBroadcast(JSONObject intent) {
		
	}
	
	@RpcMethod(params= {"intent"})
	public void removeStickyBroadcast(JSONObject intent) {
		
	}
	
	@RpcMethod(params= {"receiver", "filter", "permission"}, subject=true)
	public JSONObject registerReceiver(BroadcastReceiver receiver,
            JSONObject filter, String permission) {
		return null;
	}
	
	@RpcMethod(params= {"receiver"}, subject=true)
	public void unregisterReceiver(BroadcastReceiver receiver) {
		
	}
	
	@RpcMethod(params= {"intent"})
	public JSONObject startService(JSONObject intent) {
		return null;
	}
	
	@RpcMethod(params= {"intent"})
	public boolean stopService(JSONObject intent) {
		return false;
	}
	
}
