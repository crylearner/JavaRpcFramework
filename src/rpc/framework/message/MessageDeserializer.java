package rpc.framework.message;

public interface MessageDeserializer {
	RpcMessage deserialize(String data);
}
