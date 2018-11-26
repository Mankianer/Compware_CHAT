package de.woelk;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;
import de.woelk.UserSessionLocal;
import de.woelk.UserSessionRemote;

@Stateful
public class UserSessionBean implements UserSessionLocal, UserSessionRemote {

	@EJB
	private UserController userController;
	
	@EJB
	private StatisticBean statisticBean;
	
	private User user;
	
	@PostConstruct
	public void init() {
	}
	
	@Override
	public String changePassword(String oldpassword, String password){
		oldpassword = userController.hash(oldpassword + user.name);
		password = userController.hash(password+user.name);
		if(!user.password.equals(oldpassword)) return "Passwort falsch!";
		userController.addUser(user.name, password);
		return null;
	}

	@Override
	public String delete(String password){
		if(user == null) return "Nicht Angemeldet!";
		password = userController.hash(password+user.name);
		if(!user.password.equals(password)) return "Passwort falsch!";
		userController.delete(user.name);
		user = null;
		return null;
	}

	@Remove
	@Override
	public void disconnect() {
		userController.disconect(user.name);
	}

	@Override
	public int getNumberOfOnlineUsers() {
		return userController.getOnlineUser().size();
	}

	@Override
	public int getNumberOfRegisteredUsers() {
		return userController.getRegisterUserSize();
	}

	@Override
	public List<String> getOnlineUsers() {
		return userController.getOnlineUser();
	}

	@Override
	public String getUserName() {
		return user.name;
	}

	@Override
	public String login(String name, String password) {
		password = userController.hash(password+name);
		if(userController.login(name, password)) {
			user = new User(name,password);
			CommonStatistic commonStat = statisticBean.getCommonStat();
			commonStat.setLogins(commonStat.getLogins() + 1);
			statisticBean.update(commonStat);
			
			UserStatistic userStatic = statisticBean.getUserStat(name);
			userStatic.setLogins(userStatic.getLogins() + 1);
			userStatic.setLastLogin(new Date());
			statisticBean.update(userStatic);
		} else {
			return "Anmeldedaten Falsch!"; 
		}
		return null;
	}

	@Remove
	@Override
	public String logout(){
		if(user == null) return "Nicht angemeldet!";
		userController.logout(user.name);
		
		UserStatistic userStatic = statisticBean.getUserStat(user.name);
		userStatic.setLogouts(userStatic.getLogouts() + 1);
		statisticBean.update(userStatic);
		return null;
	}

	@Override
	public String register(String name, String password) {
		password = userController.hash(password+name);
		if(userController.isUser(name)) return "Nutzer schon vorhanden!";
		userController.addUser(name, password);
		return null;
	}

	@Override
	public List<CommonStatistic> getCommonStatistic() {
		return statisticBean.getCommonStatistic();
	}

	@Override
	public UserStatistic getUserStatistic() {
		return statisticBean.getUserStat(user.name);
	}
}
