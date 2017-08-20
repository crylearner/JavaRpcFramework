package rpc.exception;

public class RpcInvalidParameters extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcInvalidParameters(String message) {
		super(message);
	}
	
	public int errorCode() {
		return RpcException.INVALID_PARAMS;
	}

}
