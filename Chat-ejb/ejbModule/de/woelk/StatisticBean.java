package de.woelk;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.fh_dortmund.inf.cw.chat.server.entities.CommonStatistic;
import de.fh_dortmund.inf.cw.chat.server.entities.Statistic;
import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

@Stateless
public class StatisticBean {

	@PersistenceContext
	private EntityManager manager;

	@PostConstruct
	public void init() {
		newCommonStat();
	}

	public CommonStatistic getCommonStat() {
		List<CommonStatistic> resultList = manager.createNamedQuery("getAllCommStats", CommonStatistic.class)
				.getResultList();
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	public void update(Statistic stats) {
//		manager.lock(stats, LockModeType.NONE);
		manager.merge(stats);
	}

	public UserStatistic getUserStat(String name) {
		
		
		TypedQuery<UserStatistic> createNamedQuery = manager.createNamedQuery("findByName", UserStatistic.class);
		createNamedQuery.setParameter(1, name);
		try {
			UserStatistic ret = createNamedQuery.getSingleResult();
			manager.lock(ret, LockModeType.PESSIMISTIC_WRITE);
			return ret;
		} catch (NoResultException e) {
			return new UserStatistic(name);
		}
	}

	public void newCommonStat() {
		CommonStatistic commonStat = getCommonStat();
		if (commonStat != null) {
			commonStat.setEndDate(new Date());
			update(commonStat);
		}

		commonStat = new CommonStatistic();
		commonStat.setStartingDate(new Date());
		manager.persist(commonStat);
	}

	public List<CommonStatistic> getCommonStatistic() {
		return manager.createNamedQuery("getAllCommStats", CommonStatistic.class).getResultList();
	}

}
