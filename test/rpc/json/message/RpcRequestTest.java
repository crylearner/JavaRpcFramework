package rpc.json.message;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RpcRequestTest {

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
	public final void testRpcRequestConstructor() {
		JSONObject params = new JSONObject("{'x':1}");
		RpcRequest r = new RpcRequest(1, "test", params);
		assertEquals(r.getId(), 1);
		assertEquals(r.getMethod(), "test");
		assertEquals(r.getParams(), params);
	}

	@Test
	public final void testEncode() {
		JSONObject params = new JSONObject("{'x':1}");
		RpcRequest r = new RpcRequest(1, "test", params);
		String expect = "{\"method\":\"test\",\"id\":1,\"params\":{\"x\":1}}";
		assertEquals(r.encode(), expect);
	}

	@Test
	public final void testDecode() {
		String expect = "{\"method\":\"test\",\"id\":1,\"params\":{\"x\":1}}";
		RpcRequest r = new RpcRequest();
		r.decode(expect);
		assertEquals(r.getMethod(), "test");
	}

}
