package rpc.framework;

import rpc.json.RpcNotification;


public interface INotificationListener {
	boolean onNotify(RpcNotification notify);
}
