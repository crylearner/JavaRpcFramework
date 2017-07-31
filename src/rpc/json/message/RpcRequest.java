package rpc.json.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rpc.framework.message.RpcKey;
import rpc.framework.message.RpcMessage;
import rpc.util.RpcLog;



public class RpcRequest implements RpcMessage{
	private static final String TAG = "RpcRequest";
	
	private int mRequestId = 0; ///请求id
    private String mMethod = null; ///请求方法名
    private Object mParams = null; ///方法参数

    
    public RpcRequest() {
    	
    }
    
    public RpcRequest(int requestId, String method, Object params) {
    	mRequestId = requestId;
        mParams = params;
        mMethod = method;
    }

    @Override
    public String toString() {
    	return "RpcRequest: "
    			+ "id=" + mRequestId 
    			+ ",method=" + mMethod
                + ",\nparams=" + (mParams==null ? null : mParams.toString());
    }
    
    @Override
    public int getId() {
        return mRequestId;
    }
    
    public void setId(int requestId) {
    	mRequestId = requestId;
    }
    
	public Object getParams(){
    	return mParams;
    }
    
	public void setParams(Object params){
        mParams = params;
    }
    public String getMethod(){
        return mMethod;
    }
    public void setMethods(String method){
        mMethod = method;
    }
    
    @Override
    public String encode() {
        JSONObject request = new JSONObject();
        try {
            request.put(RpcKey.METHOD, mMethod);
            if (mParams == null) {
                request.put(RpcKey.PARAMS, JSONObject.NULL);
            } else {
				request.put(RpcKey.PARAMS, mParams);
			}
            request.put(RpcKey.ID, mRequestId);
        } catch (JSONException e) {
            RpcLog.w(TAG, e.getMessage());
        }
        return request.toString();
    }
    
    @Override
    public void decode(String codes) {
    	JSONObject result = new JSONObject(codes);
    	
    	try {
            this.mMethod = result.getString(RpcKey.METHOD);
        } catch (JSONException e) {
			RpcLog.w(TAG , "JSONException: RpcRequest "+RpcKey.METHOD +" is not found!");
        }
        try {
            this.mRequestId = result.getInt(RpcKey.ID);
        } catch (JSONException e) {
            RpcLog.w(TAG , "JSONException: RpcRequest "+RpcKey.ID+" is not found!");
        }
        try {
            this.mParams = result.get(RpcKey.PARAMS);
        } catch (JSONException e) {
            RpcLog.w(TAG , "JSONException: RpcRequest "+RpcKey.PARAMS+" is not found!");
            this.mParams = null;
        }
    }

}