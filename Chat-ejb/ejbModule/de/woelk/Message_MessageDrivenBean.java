package de.woelk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@MessageDriven(mappedName = "java:global/jms/InMSG", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class Message_MessageDrivenBean implements MessageListener{

	@Inject
	private JMSContext jmsContext;
	
	@EJB
	private StatisticBean statisticBean;
	
	@Resource(lookup="java:global/jms/OutMSG")
	private Topic topic;
	
	private List<String> swearList;
	
	public Message_MessageDrivenBean() {
		swearList = new ArrayList<>();
		swearList.add("dumm");
		swearList.add("Arsch");
	}
	
	public void send(Message message) {
		jmsContext.createProducer().send(topic, message);
	}
	
	public void send(String user, ChatMessageType type) {
		send(user, type, null);
	}

	public void send(String user, ChatMessageType type, String text) {
		send(jmsContext.createObjectMessage(new ChatMessage(type, user, text, new Date())));
	}
	
	private String cleanMSG(String msg) {
		for (String string : swearList) {
			
			msg = msg.replaceAll(string, string.replaceAll(".", "*"));
		}
		
		return msg;
	}
	
	@Override
	public void onMessage(Message message) {
		String user = null;
		String msg = null;
		try {
			user = message.getStringProperty("user");
			msg = message.getStringProperty("msg");
			
			UserStatistic userStat = statisticBean.getUserStat(user);
			userStat.setMessages(userStat.getMessages() + 1);
			statisticBean.update(userStat);
			CommonStatistic commonStat = statisticBean.getCommonStat();
			commonStat.setMessages(commonStat.getMessages() + 1);
			statisticBean.update(commonStat);
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
		
		
		
		ChatMessage body = new ChatMessage(ChatMessageType.TEXT, user, cleanMSG(msg), new Date());
		
		try {
			Message createMessage = jmsContext.createObjectMessage(body);
			createMessage.setStringProperty("user", user);
			send(createMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
