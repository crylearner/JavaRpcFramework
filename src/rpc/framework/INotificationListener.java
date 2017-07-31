package rpc.framework;

import rpc.json.message.RpcNotification;


public interface INotificationListener {
	boolean onNotify(RpcNotification notify);
}
