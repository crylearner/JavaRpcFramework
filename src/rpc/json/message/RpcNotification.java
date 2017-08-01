package rpc.json.message;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.framework.message.RpcKey;
import rpc.framework.message.RpcMessage;
import rpc.util.RpcLog;


public class RpcNotification implements RpcMessage {
	private static final String TAG = "RpcNotification";
	private String mMethod = null;
	private int mCallback = 0;
	private JSONObject mParams = null;
	
	public RpcNotification() {
		
	}
	
	@Override
	public int getId() {
		return mCallback;
	}

	
	@Override
	public String encode() {
        JSONObject message = new JSONObject();
        try {
            message.put(RpcKey.METHOD, mMethod);
            
            if (mCallback!=0) { 
            	message.put(RpcKey.CALLBACK, mCallback); 
            }
            if (mParams == null) {
                message.put(RpcKey.PARAMS, JSONObject.NULL);
            } else {
				message.put(RpcKey.PARAMS, mParams);
			}
        } catch (JSONException e) {
            RpcLog.e(TAG, e);
        }
        return message.toString();
    }
	
	@Override
	public void decode(String codes) {
		JSONObject result = new JSONObject(codes);
    	try {
            setMethod(result.getString(RpcKey.METHOD));
            mCallback = result.getInt(RpcKey.CALLBACK);
            setParams(result.get(RpcKey.PARAMS));
        } catch (JSONException e) {
            RpcLog.e(TAG, e);
        }
	}

	public JSONObject getParams() {
		return mParams;
	}

	public void setParams(Object params) {
		if (params == JSONObject.NULL) {
			mParams = null;
		} else {
			mParams = (JSONObject)params;
		}
	}

	public String getMethod() {
		return mMethod;
	}

	public void setMethod(String method) {
		mMethod = method;
	}

	public int getCallback() {
		return mCallback;
	}

	private void setCallback(int callback) {
		mCallback = callback;
	}


	@Override
	public String toString() {
		return "RpcNotification: " + "callback="+ mCallback +",method="+mMethod
                +",\nparmas=" + (mParams==null ? null : mParams.toString());
	}
	
}
