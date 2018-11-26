package de.woelk;

import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.fh_dortmund.inf.cw.chat.client.shared.ChatMessageHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.ServiceHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.StatisticHandler;
import de.fh_dortmund.inf.cw.chat.client.shared.UserSessionHandler;
import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;

public class ServiceHandlerImpl extends ServiceHandler implements UserSessionHandler, ChatMessageHandler, MessageListener, StatisticHandler {

	private static ServiceHandlerImpl instance;
	private Context ctx;
	private UserSessionRemote handler;
	private JMSContext jmsContext;
	private JMSProducer jmsProducer;
	private Queue dest;
	

	protected ServiceHandlerImpl() {
		try {
			ctx = new InitialContext();

			handler = (UserSessionRemote) ctx
					.lookup("java:global/Chat-ear/Chat-ejb/UserSessionBean!de.woelk.UserSessionRemote");
			
			ConnectionFactory connFactory = (ConnectionFactory) ctx.lookup("java:comp/DefaultJMSConnectionFactory");
			
			jmsContext = connFactory.createContext();
			
			Topic destIn = (Topic) ctx.lookup("java:global/jms/OutMSG");
			dest = (Queue) ctx.lookup("java:global/jms/InMSG");
			jmsContext.createConsumer(destIn).setMessageListener(this);
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
//	public void close() {
//		instance = null;
//		try {
//			ctx.close();
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static ServiceHandlerImpl getInstance() {
		return instance != null ? instance : (instance = new ServiceHandlerImpl());
	}

	@Override
	public void changePassword(String oldpassword, String password) throws Exception {
		String msg = handler.changePassword(oldpassword, password);
		if(msg != null) throw new Exception(msg);

	}

	@Override
	public void delete(String password) throws Exception {
		String msg = handler.delete(password);
		if(msg != null) throw new Exception(msg);
	}

	@Override
	public void disconnect() {
		handler.disconnect();
	}

	@Override
	public int getNumberOfOnlineUsers() {
		return handler.getNumberOfOnlineUsers();
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		return handler.getNumberOfRegisteredUsers();
	}

	@Override
	public List<String> getOnlineUsers() {
		return handler.getOnlineUsers();
	}

	@Override
	public String getUserName() {
		return handler.getUserName();
	}

	@Override
	public void login(String name, String password) throws Exception {
		String msg = handler.login(name, password);
		if(msg != null) throw new Exception(msg);
	}

	@Override
	public void logout() throws Exception {
		String msg = handler.logout();
		if(msg != null) throw new Exception(msg);
	}

	@Override
	public void register(String name, String password) throws Exception {
		String msg = handler.register(name, password);
		if(msg != null) throw new Exception(msg);
	}

	@Override
	public void sendChatMessage(String msg) {
		Message message;
		try {
			message = jmsContext.createTextMessage();
			message.setStringProperty("user", getUserName());
			message.setStringProperty("msg", msg);
			jmsContext.createProducer().send(dest, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			ChatMessage body = message.getBody(ChatMessage.class);
			System.out.println("in: " + body);
			setChanged();
			notifyObservers(body);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<CommonStatistic> getStatistics() {
		List<CommonStatistic> commonStatistic = handler.getCommonStatistic();
		return commonStatistic;
	}

	@Override
	public UserStatistic getUserStatistic() {
		UserStatistic userStatistic = handler.getUserStatistic();
		System.out.println(userStatistic);
		return userStatistic;
	}

}
