package server.json.android;

import org.json.JSONArray;

import rpc.framework.server.annotation.RpcMethod;
import rpc.framework.server.annotation.RpcService;

@RpcService(name="Cursor")
public class CursorService {
	
	@RpcMethod(params= {"size"})
	public JSONArray fetch(int size) {
		return null;
	}
	
	@RpcMethod
	public void close() {
		
	}
	
	@RpcMethod
	public int getCount() {
		return 0;
	}
	
}
