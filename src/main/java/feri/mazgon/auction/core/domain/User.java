package feri.mazgon.auction.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
public class User extends DBEntity<User> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column
	private String email;
	@Column
	private String password;
	@Column
	private String firstName;
	@Column
	private String lastName;
	@OneToMany(mappedBy = "bidder")
	private List<Bid> bids;
	@OneToMany(mappedBy = "seller")
	private List<Auction> auctions;
	@Column
	private boolean payingUser;

	// ---------------------------
	//        CONSTRUCTORS
	// ---------------------------
	public User() {
		this(null, null, null, null, false);
	}
	public User(String email, String password, String firstName, String lastName, boolean payingUser) {
		bids = new ArrayList<Bid>();
		auctions = new ArrayList<Auction>();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.payingUser = payingUser;
	}

	// ---------------------------
	//           GETTERS
	// ---------------------------
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public List<Bid> getBids() {
		return bids;
	}
	public List<Auction> getAuctions() {
		return auctions;
	}
	public boolean isPayingUser() {
		return payingUser;
	}
	
	// ---------------------------
	//           SETTERS
	// ---------------------------
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}
	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}
	public void setPayingUser(boolean payingUser) {
		this.payingUser = payingUser;
	}

	// ---------------------------
	//          CONSTANTS
	// ---------------------------
	public static final int MAX_NON_PAYING_USER_AUCTIONS = 5;
	
	// ---------------------------
	//           METHODS
	// ---------------------------
	public boolean canCreateAuction() {
		boolean can = true;
		if (!payingUser && countActiveAuctions() >= MAX_NON_PAYING_USER_AUCTIONS)
			can = false;
		
		return can;
	}
	
	public int countActiveAuctions() {
		repository.refresh(this);
		int count = 0;
		for (Auction a : auctions) {
			if (a.isActive())
				count++;
		}
		return count;
	}
	
	public void save() throws IllegalStateException {
		repository.save(this);
	}
	
	public boolean isSameAs(User user) {
		return this.id == user.id;
	}
	
	public boolean checkPassword(String providedPassword) {
		return password.equals(providedPassword);
	}
	
	// ---------------------------
	//     EXCEPTION MESSAGES
	// ---------------------------
}
