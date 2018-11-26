package de.woelk;

import static org.junit.Assert.assertEquals;
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

public class ServiceHandlerImplTest{

	private ServiceHandlerImpl handler;
	
	@Before
	public void setUpTest() {
		System.out.println("setUP");
		handler = new ServiceHandlerImpl();
	}
	
	@After
	public void clean() {
		try {
			handler.delete("test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Close");

	}
	
	private void addUser() {
		try {
			handler.register("test", "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			handler.login("test", "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getUserNameTest_1() {
		assertEquals(handler.getUserName(), null);
	}
	
	@Test
	public void getUserNameTest_2() {
		addUser();
		assertEquals("test", handler.getUserName());
	}
	
	@Test(expected = Exception.class)
	public void registerTest() throws Exception {
		handler.register("test", "test");
		handler.register("test", "test");
	}
	
	@Test
	public void registerTest_2() throws Exception {
		handler.register("1234", "test");
		handler.delete("test");
	}

	@Test(expected = Exception.class)
	public void login_unbekant_Nutzer() throws Exception {
		handler.login("Nutzer", "test");
	}
	
	@Test(expected = Exception.class)
	public void login_falscher_Nutzer() throws Exception {
		handler.login("falscher Nutzer", "test");
	}
	

	@Test(expected = Exception.class)
	public void logout_fehler() throws Exception {
		handler.logout();
	}
	
	@Test
	public void logout() throws Exception {
		addUser();
		handler.logout();
	}

	@Test(expected = Exception.class)
	public void disconnect() {
		addUser();
		handler.disconnect();
	}

	@Test
	public void delete() throws Exception {
		addUser();
		handler.delete("test");
	}
	
	@Test(expected = Exception.class)
	public void delete_falschesPW() throws Exception {
		addUser();
		handler.delete("1234");
	}
	
	@Test(expected = Exception.class)
	public void changePassword_falschesPW() throws Exception {
		addUser();
		handler.changePassword("123", "1234");
	}

	@Test
	public void changePassword() throws Exception {
		addUser();
		handler.changePassword("test", "1234");
		handler.changePassword("1234", "test");
	}
	
	@Test
	public void getOnlineUsers_leer() {
		assertEquals(0, handler.getOnlineUsers().size());
	}
	
	@Test
	public void getOnlineUsers() {
		addUser();
		assertEquals(1, handler.getOnlineUsers().size());
	}

	@Test
	public void getNumberOfRegisteredUsers_leer() {
		assertEquals(0, handler.getNumberOfRegisteredUsers());
	}
	
	@Test
	public void getNumberOfRegisteredUsers() {
		addUser();
		assertEquals(1, handler.getNumberOfRegisteredUsers());
	}

	@Test
	public void getNumberOfOnlineUsers_leer() {
		assertEquals(0, handler.getOnlineUsers().size());
	}
	
	@Test
	public void getNumberOfOnlineUsers() {
		addUser();
		assertEquals(1, handler.getOnlineUsers().size());
	}
	
}
