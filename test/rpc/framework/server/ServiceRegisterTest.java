package rpc.framework.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;

class ServiceTest implements RpcServiceInterface {

	@Override
	public String[] list() {
		return new String[] {"ServiceTest.s1", "ServiceTest.s2"};
	}

	@Override
	public RpcResponse execute(RpcRequest request) {
		return null;
	}
	
	public void s1(int x) {
	}
	
	public int s2() {
		return 0;
	}
}

public class ServiceRegisterTest {

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
	public final void testAddServiceStringRpcServiceInterface() {
		ServiceRegistry mSR = new ServiceRegistry();
		mSR.addService("ServiceTest.s1", new ServiceTest());
		assertTrue(mSR.getService("ServiceTest.s1") != null);
	}

	@Test
	public final void testAddServiceRpcServiceInterfaceStringArray() {
		ServiceRegistry mSR = new ServiceRegistry();
		mSR.addService(new ServiceTest(), new String[] {"s1", "s2"});
		System.out.println(mSR.listServices());
		assertTrue(mSR.getService("ServiceTest.s1") != null);
		assertTrue(mSR.getService("ServiceTest.s2") != null);
	}

	@Test
	public final void testAddServiceRpcServiceInterface() {
		ServiceRegistry mSR = new ServiceRegistry();
		mSR.addService(new ServiceTest());
		System.out.println(mSR.listServices());
		assertTrue(mSR.getService("ServiceTest.s1") != null);
		assertTrue(mSR.getService("ServiceTest.s2") != null);
	}

	@Test
	public final void testRemoveService() {
		ServiceRegistry mSR = new ServiceRegistry();
		mSR.addService(new ServiceTest());
		System.out.println(mSR.listServices());
		assertTrue(mSR.getService("ServiceTest.s1") != null);
		mSR.removeService("ServiceTest.s1");
		assertTrue(mSR.getService("ServiceTest.s1") == null);
	}

}
