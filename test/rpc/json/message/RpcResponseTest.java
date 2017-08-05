package rpc.json.message;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RpcResponseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testConstructor() {
		JSONObject params = new JSONObject("{'x':1}");
		RpcResponse r = new RpcResponse(1, params, true);
		assertEquals(params, r.getResult());
		r = new RpcResponse(1, params, false);
		assertEquals(params, r.getError());
	}

	@Test
	public final void testEncodeDecode() {
		JSONObject params = new JSONObject("{'x':1}");
		RpcResponse r = new RpcResponse(1, params, true);
		RpcResponse r2 = new RpcResponse();
		r2.decode(r.encode());
		assertEquals(r.toString(), r2.toString());
	}
}
