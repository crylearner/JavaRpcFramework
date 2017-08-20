package rpc.exception;

public class RpcInternalError extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcInternalError(String message) {
		super(message);
	}
	
	public int errorCode() {
		return RpcException.INTERNAL_ERROR;
	}

}
