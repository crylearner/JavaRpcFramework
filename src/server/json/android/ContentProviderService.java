package server.json.android;

import org.json.JSONArray;
import org.json.JSONObject;

import rpc.framework.server.annotation.RpcMethod;
import rpc.framework.server.annotation.RpcService;

@RpcService(name="ContentProvider")
public class ContentProviderService {

	@RpcMethod(params={"uri", "projection", "selection", "selectionArgs", "sortOrder"})
	public JSONObject query(String uri, JSONArray projection,
            String selection, JSONArray selectionArgs, String sortOrder) {
		// convert uri to Uri()
		// call
		JSONObject result = new JSONObject("{'token':1}");
		return result;
	}
	
	@RpcMethod(params= {"uri", "values"})
	public String insert(String uri, JSONObject values) {
		// jsonobject ot contenttypes
		
		return uri;
	}
	
	@RpcMethod(params= {"uri", "selection", "selectionArgs"})
	public int delete(String uri, String selection, JSONArray selectionArgs) {
		return 0;
	}
	
	@RpcMethod(params= {"uri", "values", "selection", "selectionArgs"})
	public int update(String uri, JSONObject values, String selection,
            JSONArray selectionArgs) {
		
		return 0;
	}
	
	@RpcMethod(params= {"method", "arg", "extras"})
	public JSONObject call(String method, String arg, JSONObject extras) {
		return null;
	}
}
