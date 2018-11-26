package de.woelk;

import java.util.List;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

public interface UserSessionInterface {
	
	public String changePassword(String oldpassword, String password);

	public String delete(String password);

	public void disconnect();

	public int getNumberOfOnlineUsers();

	public int getNumberOfRegisteredUsers();

	public List<String> getOnlineUsers();

	public String getUserName();

	public String login(String name, String password);

	public String logout();

	public String register(String name, String password);
	
	public List<CommonStatistic> getCommonStatistic();
	
	public UserStatistic getUserStatistic();
}
