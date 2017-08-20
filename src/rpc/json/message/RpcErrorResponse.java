package rpc.json.message;

public class RpcErrorResponse extends RpcResponse {

	public RpcErrorResponse(int id, Object result) {
		super(id, result, false);
	}

}
