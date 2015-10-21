package feri.mazgon.auction.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
public class AuctionCategory extends DBEntity<AuctionCategory> {
	@Column
	private String name;

	// ---------------------------
	//        CONSTRUCTORS
	// ---------------------------
	public AuctionCategory() {
		this(null);
	}
	public AuctionCategory(String name) {
		this.name = name;
	}
	
	// ---------------------------
	//           GETTERS
	// ---------------------------
	public String getName() {
		return name;
	}
	
	// ---------------------------
	//           SETTERS
	// ---------------------------
	public void setName(String name) {
		this.name = name;
	}
	
	// ---------------------------
	//           METHODS
	// ---------------------------
	public void save() throws IllegalStateException {
		repository.save(this);
	}
	
	// ---------------------------
	//     EXCEPTION MESSAGES
	// ---------------------------

}
