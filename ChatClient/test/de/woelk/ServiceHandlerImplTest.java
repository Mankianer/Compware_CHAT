package de.woelk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;

public class ServiceHandlerImplTest {

	private ServiceHandlerImpl handler;

	@Before
	public void setUpTest() {
		System.out.println("setUP");
		handler = new ServiceHandlerImpl();
	}

	@After
	public void clean() {

	}

	@Test
	public void test1() throws Exception {
		int i = handler.getNumberOfOnlineUsers();
		int r = handler.getNumberOfRegisteredUsers();
		handler.register("test1", "test");
		handler.login("test1", "test");
		assertEquals(i + 1, handler.getNumberOfOnlineUsers());
		assertEquals(r + 1, handler.getNumberOfRegisteredUsers());
		assertEquals("test1", handler.getUserName());
		assertTrue(handler.getOnlineUsers().contains("test1"));
		handler.sendChatMessage("test213");
		assertNotNull(handler.getStatistics());
		assertNotNull(handler.getUserStatistic());
		handler.changePassword("test", "neu");
		handler.logout();
		handler = new ServiceHandlerImpl();
		handler.login("test1", "neu");
		handler.delete("neu");
	}

	@Test
	public void disconect() throws Exception {
		handler.register("disconect", "test");
		handler.login("disconect", "test");
		handler.disconnect();

		handler = new ServiceHandlerImpl();
		handler.login("disconect", "test");
		handler.delete("test");
	}

	@Test(expected = Exception.class)
	public void exeptionChangePW() throws Exception {
		handler.register("changePW", "test");
		handler.login("changePW", "test");
		handler.changePassword("falsch", "neu");
	}

	@Test(expected = Exception.class)
	public void exeptionDelete() throws Exception {
		handler.register("del", "test");
		handler.login("del", "test");
		handler.delete("falsche");
	}

	@Test(expected = Exception.class)
	public void exeptionLogin() throws Exception {
		handler.register("login", "test");
		handler.login("login", "falsch");
	}

	@Test(expected = Exception.class)
	public void exeptionLogOut() throws Exception {
		handler.logout();
	}

	@Test(expected = Exception.class)
	public void exeptionRegister() throws Exception {
		handler.register("login", "test");
		handler.register("login", "test");
	}
}
