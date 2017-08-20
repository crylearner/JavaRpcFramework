package rpc.exception;

public class RpcPermissionDenied extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcPermissionDenied(String message) {
		super(message);
	}
	
	public int errorCode() {
		return RpcException.PERMISSION_DENIED;
	}

}
