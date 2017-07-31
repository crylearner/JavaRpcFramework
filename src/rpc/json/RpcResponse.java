package rpc.json;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.framework.message.RpcKey;
import rpc.framework.message.RpcMessage;
import rpc.util.RpcLog;
import rpc.util.RpcTools;

public class RpcResponse implements RpcMessage {
	private static final String TAG = "RpcResponse";
    
    private int mId = 0;
    private Object mResult = null;
    
    public Object getResult() {
		return mResult;
	}

	public Object getError() {
		return mError;
	}

	private Object mError = null;
    private boolean mSuccessful = false;
    
    
    public RpcResponse() {
	}
    
    public RpcResponse(int id, Object result, boolean successful) {
    	mId = id;
    	if (successful) {
    		mResult = result;
    	} else {
    		mError = result;
    	}
    	mSuccessful = successful;
    }

    @Override
    public String encode() {
        JSONObject message = new JSONObject();
        try {
            message.put(RpcKey.ID, mId);
            if (mResult != null) { 
            	message.put(RpcKey.RESULT, mResult); 
            }
            if (mError != null) {
                message.put(RpcKey.ERROR, mError);
            }
        } catch (JSONException e) {
            RpcLog.w(TAG,  e.getMessage());
        }
        return message.toString();
    }
    
    @Override
	public void decode(String codes) {
    	try {
	    	JSONObject result = new JSONObject(codes);
	    	this.mId = result.getInt(RpcKey.ID);
			if (result.has(RpcKey.RESULT)) {
				this.setResult(result.get(RpcKey.RESULT));
				mSuccessful = true;
			} else {
				this.mError = result.get(RpcKey.ERROR);
				mSuccessful = false;
			}
    	} catch (JSONException e) {
    		RpcLog.e(TAG,e.getMessage());
    	}
        
    }
    
    public boolean isSuccessful() {
        return mSuccessful;
    }
 
   
    private void setResult(Object result) {
    	mResult = result;
    }
    
    @Override
    public String toString() {
        String str = "RpcRespone: id=" + mId;
        if (mError == null) {
        	str += ", \nresult=" + (mResult==null ? null : mResult.toString());
        } else {
        	str += ", \nerror=" + (mError==null ? null : mError.toString());
        }
        return str + ",\nSuccessful=" + mSuccessful;
    }
    
    @Override
    public int getId() {
        return mId;
    }
}
