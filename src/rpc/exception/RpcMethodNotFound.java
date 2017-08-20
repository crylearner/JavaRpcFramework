package rpc.exception;

public class RpcMethodNotFound extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcMethodNotFound(String method) {
		super(method + " is not found");
	}
	
	public int errorCode() {
		return RpcException.METHOD_NOT_FOUND;
	}

}
