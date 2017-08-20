package rpc.exception;

import org.json.JSONObject;

public abstract class RpcException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final int PARSE_ERROR			= -32700;
	public static final int INVALID_REQUEST		= -32600;
	public static final int METHOD_NOT_FOUND	= -32601;
	public static final int INVALID_PARAMS		= -32602;
	public static final int INTERNAL_ERROR		= -32603;
	
	public static final int AUTHENTIFICATION_ERROR= -32001;
	public static final int PERMISSION_DENIED	= -32002;
	
	// client error
	
	// user defined error

	
	public RpcException() {
	}
	
	public RpcException(String message) {
		super(message);
	}
	
	abstract public int errorCode();
	
	public String errorData() {
		return null;
	}
	
	public JSONObject toError() {
		JSONObject e = new JSONObject();
		e.put("code", errorCode());
		e.put("message", this.toString());
		e.put("data", errorData());
		return e;
	}
}
