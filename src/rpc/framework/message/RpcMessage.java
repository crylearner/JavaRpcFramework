package rpc.framework.message;

public interface RpcMessage {
	public int getId();
	
	/**encode RpcMessage itself to String
	 * @return encoded string
	 */
	public String encode();
	
	/**decode from codes to RpcMessage
	 * @param codes
	 */
	public void decode(String codes);
}
