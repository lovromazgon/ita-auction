package feri.mazgon.auction.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import feri.mazgon.auction.core.AuctionService;
import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.util.Util;
import feri.mazgon.auction.web.dto.WebAuction;

@Controller
public class GeneralController {
	private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);
	private static final String SESSION_USER = "user";
	
	@Autowired
	private AuctionService auctionService;
	
	@RequestMapping(value = {"/", "auctions"}, method = RequestMethod.GET)
	public String home(Model model, @RequestParam(value = "category", defaultValue = "0") Long auctionCategoryId) {
		AuctionCategory auctionCategory = null;
		if (auctionCategoryId != 0)
			auctionCategory = auctionService.getAuctionCategory(auctionCategoryId);
		if (auctionCategory == null)
			auctionCategoryId = 0l;
		
		List<Auction> auctions = auctionService.getAuctions(auctionCategory);
		model.addAttribute("auctions", convertAuctionsToWebAuctions(auctions));
		
		List<AuctionCategory> auctionCategories = new ArrayList<AuctionCategory>();
		AuctionCategory defaultCategory = new AuctionCategory();
		defaultCategory.setId(0);
		defaultCategory.setName("Vse");
		auctionCategories.add(defaultCategory);
		auctionCategories.addAll(auctionService.getAuctionCategories());
		
		model.addAttribute("auctionCategories", auctionCategories);
		model.addAttribute("selectedCategory", auctionCategoryId);
		
		return "home";
	}
	
	@RequestMapping(value = "auction/{id}", method = RequestMethod.GET)
	public String auction(@PathVariable long id, Model model) {
		Auction auction = auctionService.getAuction(id);
		
		if (auction == null)
			return "redirect";
		
		model.addAttribute("auction", new WebAuction(auction));
		model.addAttribute("bidAmount", Util.formatMoney(auction.getCurrentPrice() + 100));
		
		return "auction";
	}
	
	@RequestMapping(value = "auction/{id}", method = RequestMethod.POST)
	public String bid(@PathVariable long id, @RequestParam String amount, HttpSession session, Model model) {
		Auction auction = auctionService.getAuction(id);
		
		if (auction == null)
			return "redirect";
		
		Long bidAmount = Util.stringToLong(amount);
		User user;
		
		if (bidAmount == null) {
			model.addAttribute("error", "Vpisali ste neveljavno vsoto!");
		}
		else if ((user = getLoggedUser(session)) == null) {
			model.addAttribute("error", "Niste prijavljeni!");
		}
		else {
			try {
				auctionService.bid(user, auction, bidAmount);
			} catch (IllegalStateException e) {
				model.addAttribute("error", e.getMessage());
			}
		}
		
		return auction(id, model);
	}
	
	@RequestMapping(value = "auction/new", method = RequestMethod.GET)
	public String auctionForm(Model model, HttpSession session) {
		if (getLoggedUser(session) == null)
			return "redirect";
		
		WebAuction auction = new WebAuction();
		auction.setCategoryId(1);
		
		model.addAttribute("auction", auction);
		model.addAttribute("auctionCategories", getCategoriesForForm());
		
		return "newAuction";
	}
	
	@RequestMapping(value = "auction/new", method = RequestMethod.POST)
	public String createAuction(WebAuction auction, BindingResult result, Model model, HttpSession session) {
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				logger.warn("Error: " + error.getCode() + " - " + error.getDefaultMessage());
			}
		}
		User user;
		if ((user = getLoggedUser(session)) == null)
			return "redirect";
		
		logger.info(auction.toString());
		
		try {
			auction.validate();
			auction.setAuctionCategory(auctionService.getAuctionCategory(auction.getCategoryId()));
			auction.setSeller(user);
			auctionService.addAuction(auction.getAuction());
			return "redirect:/auction/" + auction.getId();
		} catch (IOException e) {
			model.addAttribute("error", "Napaka pri nalaganju slike!");
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
		}

		model.addAttribute("auction", auction);
		model.addAttribute("auctionCategories", getCategoriesForForm());
		
		return "newAuction";
	}
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(HttpSession session, @RequestParam String email, @RequestParam String password) {
		User user = auctionService.getUser(email);
		
		if (user != null && user.checkPassword(password))
			session.setAttribute(SESSION_USER, user);
		
		return "redirect";
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		try {
			session.invalidate();
		} catch (Exception e) {}
		
		return "redirect";
	}

	@RequestMapping(value = "auction/{id}/picture")
	public ResponseEntity<byte[]> getMedia(HttpServletResponse response, @PathVariable int id) {
		ResponseEntity<byte[]> responseEntity;
		Auction auction = auctionService.getAuction(id);

		if (auction != null) {
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			responseEntity = new ResponseEntity<byte[]>(auction.getPicture(), headers, HttpStatus.CREATED);
		}
		else
			responseEntity = new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);

		return responseEntity;
	}
	
	@ModelAttribute("loggedUser")
	public User getLoggedUser(HttpSession session) {
		Object userTemp;
		if ((userTemp = session.getAttribute(SESSION_USER)) != null) {
			return (User) userTemp;
		}
		return null;
	}
	
	@ModelAttribute("appRoot")
	public String getAppRoot() {
		return Util.APP_ROOT;
	}
	
	private Map<String, String> getCategoriesForForm() {
		List<AuctionCategory> auctionCategories = auctionService.getAuctionCategories();
		Map<String, String> categoriesForForm = new HashMap<String, String>();
		for (AuctionCategory ac : auctionCategories) {
			categoriesForForm.put(Long.toString(ac.getId()), ac.getName());
		}
		return categoriesForForm;
	}
	
	private List<WebAuction> convertAuctionsToWebAuctions(List<Auction> auctions) {
		List<WebAuction> webAuctions = new ArrayList<WebAuction>();
		for (Auction a : auctions) {
			webAuctions.add(new WebAuction(a));
		}
		return webAuctions;
	}
}