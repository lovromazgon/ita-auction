package feri.mazgon.auction.core.domain;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.ISearch;

import feri.mazgon.auction.core.repository.MyRepository;

@MappedSuperclass
public abstract class DBEntity<T> {
	@Id
	@GeneratedValue
	protected long id;
	@Transient
	protected MyRepository<T> repository;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public MyRepository<T> getRepository() {
		return repository;
	}
	@Autowired
	public void setRepository(MyRepository<T> repository) {
		this.repository = repository;
	}
	
	@Override
	public String toString() {
		return super.toString() + " id: " + id;
	}
	
	// ---------------------------
	//      DATABASE METHODS
	// ---------------------------
	
	public T find(Long id) {
		return repository.find(id);
	}
	public List<T> findAll() {
		return repository.findAll();
	}
	public void flush() {
		repository.flush();
	}
	public boolean remove(T entity) {
		return repository.remove(entity);
	}
	public <RT> List<RT> search(ISearch search) {
		return repository.search(search);
	}
}
