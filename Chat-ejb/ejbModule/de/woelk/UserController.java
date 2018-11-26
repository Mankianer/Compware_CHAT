package de.woelk;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.Statistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@Stateless
public class UserController implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private StatisticBean statisticBean;

	@PersistenceContext
	private EntityManager manager;

	private ArrayList<String> onlineUser;

	@Resource(name = "hash")
	private String hash = "SHA-1";

	private MessageDigest encoder;

	@EJB
	private ControllerBean controller;

	public UserController() {
	}

	@PostConstruct
	private void init() {
		onlineUser = new ArrayList<>();
		try {
			encoder = MessageDigest.getInstance(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < 124; i++) {
			addUser("" + i, hash("123" + i));
		}
	}

	public boolean isUser(String name) {
		return manager.find(User.class, name) != null;
	}

	public List<String> getOnlineUser() {
		return onlineUser;
	}

	public List<User> getRegisterUser() {
		TypedQuery<User> allUserQuery = manager.createNamedQuery("allUser", User.class);
		return allUserQuery.getResultList();
	}
	
	public int getRegisterUserSize() {
		TypedQuery<Long> countUserQuery = manager.createNamedQuery("countUser", Long.class);
		return countUserQuery.getSingleResult().intValue();
	}

	public void addUser(String name, String pw) {
		manager.merge(new User(name,pw));
		controller.send(name, ChatMessageType.REGISTER);
	}

	public boolean login(String name, String pw) {
		if (onlineUser.contains(name))
			controller.send(name, ChatMessageType.DISCONNECT);
		if (manager.find(User.class, name).password.equals(pw)) {
			onlineUser.add(name);
			controller.send(name, ChatMessageType.LOGIN);
			controller.createTimer();
			return true;
		}
		return false;
	}

	public void logout(String name) {
		onlineUser.remove(name);
		controller.send(name, ChatMessageType.LOGOUT);
	}

	public void disconect(String name) {
		logout(name);
	}

	public void delete(String name) {
		logout(name);
		manager.remove(manager.find(User.class, name));
	}

//	public static UserController getInstance() {
//		return instance != null ? instance : (instance = new UserController());
//	}

	public String hash(String plain) {
		return String.format("%040x", new BigInteger(1, encoder.digest(plain.getBytes())));
	}
}
