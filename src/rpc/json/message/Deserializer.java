package rpc.json.message;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.framework.message.MessageDeserializer;
import rpc.framework.message.RpcKey;
import rpc.framework.message.RpcMessage;
import rpc.util.RpcLog;

public class Deserializer implements MessageDeserializer{

	@Override
	public RpcMessage deserialize(String data) {
		JSONObject value = null;
		try {
			value = new JSONObject(data);
		} catch (JSONException e) {
			RpcLog.e("Deserializer", "deserialize failed:" + data + e.getMessage());
			return null;
		}
		if (value.has(RpcKey.ID) && value.has(RpcKey.METHOD)) {
			RpcRequest request = new RpcRequest();
			request.decode(data);
			return request;
		} else if (value.has(RpcKey.ID)) {
			RpcResponse response = new RpcResponse();
			response.decode(data);
			return response;
		} else {
			RpcNotification noti = new RpcNotification();
			noti.decode(data);
			return noti;
		}
	}
}
