package rpc.exception;

public class RpcParseError extends RpcException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcParseError(String message) {
		super(message);
	}
	
	public int errorCode() {
		return RpcException.PARSE_ERROR;
	}

}
