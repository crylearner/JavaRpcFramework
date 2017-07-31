package rpc.json;

import rpc.framework.message.MessageSerializer;
import rpc.framework.message.RpcMessage;

public class Serializer implements MessageSerializer {
	@Override
	public String serialize(RpcMessage message) {
		return message.encode();
	}
}
