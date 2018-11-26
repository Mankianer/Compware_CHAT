package de.woelk;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessage;
import de.fh_dortmund.inf.cw.chat.server.shared.ChatMessageType;

@Singleton
public class ControllerBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static final String STUNDEN_TIMER = "StundenTimer";

	@EJB
	private StatisticBean statisticBean;
	
	@Inject
	private JMSContext jmsContext;
	
	@Resource(lookup="java:global/jms/OutMSG")
	private Topic topic;
	
	private boolean createTimer = true;
	
	@Resource
	private TimerService timerService;
	
	public void createTimer() {
		for (Timer tim : timerService.getTimers()) {
			if (STUNDEN_TIMER.equals(tim.getInfo()))
				createTimer = false;
		}

		if (createTimer) {

			TimerConfig config = new TimerConfig(STUNDEN_TIMER, true);
			timerService.createCalendarTimer(new ScheduleExpression().minute("0").hour("*"), config);

			createTimer = false;
		}
	}
	
	@Timeout
	public void scheduleVolleStunde(Timer timer) {
		if (STUNDEN_TIMER.equals(timer.getInfo())) {
			CommonStatistic stat = statisticBean.getCommonStat();
			statisticBean.newCommonStat();
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			send("server", ChatMessageType.STATISTIC,
					"Statistik von " + format.format(stat.getEndDate()) + "\n\tStatistik der letzten Stunde:" + stat);
		}
	}

	@Schedule(hour = "*", minute = "0/30", persistent = true)
	public void scheduleHalbeStunde() {
		CommonStatistic stat = statisticBean.getCommonStat();
		statisticBean.newCommonStat();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		send("server", ChatMessageType.STATISTIC, "Statistik von " + format.format(stat.getEndDate())
				+ "\n\tStatistik der letzten halben Stunde:" + stat);
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
}
