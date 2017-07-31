package rpc.framework;

import rpc.json.message.RpcResponse;


public interface IResultListener {
    public abstract boolean isAlways();// notification: return true, the
                                       // IResultListener will never remove
                                       // in list

    public abstract void onResult(String method, RpcResponse cb);
}