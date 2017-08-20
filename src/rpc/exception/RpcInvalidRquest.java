package rpc.exception;

public class RpcInvalidRquest extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcInvalidRquest(String message) {
		super(message);
	}
	
	public int errorCode() {
		return RpcException.INVALID_REQUEST;
	}

}
