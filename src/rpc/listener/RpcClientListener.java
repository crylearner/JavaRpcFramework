package rpc.listener;


import org.json.JSONObject;

import rpc.framework.INotificationListener;
import rpc.json.RpcNotification;
import rpc.util.RpcTools;


public abstract class RpcClientListener implements INotificationListener{
    private static final String TAG = "RpcClientListener";
    private static final boolean DEBUG = RpcTools.DEBUG;
    public static final String NAME = "client";
    
    public static final String METHOD_EVENTLISTENER = NAME+".notifyEventStream";
    public static final String METHOD_TALKLISTENER = NAME+".notifyTalkState";
    public static final String METHOD_UPGRADELISTENER = NAME+".notifyUpgraderStream";
    
    private static int sProcCount = 0;
    
    private String mMethod;
    private int mProc;
    private Object[] mPrivates;

    public RpcClientListener(String method){
        mMethod = method;
        mProc = ++sProcCount;
    }

    public int getProc() {
        return mProc;
    }

    public Object[] getPrivates() {
        return mPrivates;
    }

    public void setPrivates(Object[] mPrivates) {
        this.mPrivates = mPrivates;
    }
    public abstract JSONObject getJSONObject();
    
    public boolean checkEvent(RpcNotification event) {
    	return true;
    }
    
    protected abstract boolean checkPrivateData(RpcNotification event);

    public boolean processEvent(RpcNotification event){
        if(checkEvent(event)){
            return execEvent(event);
        }
        return false;
    }
    protected abstract boolean execEvent(RpcNotification event);

	@Override
	public boolean onNotify(RpcNotification notify) {
		return processEvent(notify);
	}

}
