package feri.mazgon.auction.core.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.dao.hibernate.GenericDAOImpl;

public abstract class MyRepository<T> extends GenericDAOImpl<T, Long> {
	@Autowired
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
	    super.setSessionFactory(sessionFactory);
	}
}
