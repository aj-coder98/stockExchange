package com.cams.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;

import com.cams.stock.model.*;
import com.cams.stock.repository.*;
import com.cams.stock.service.*;

import java.util.*;
import java.time.LocalDateTime;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//Security
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class StockExchangeController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserStockHoldingsRepository userStockHoldingsRepository;
    
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;
    
    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private StockCollectionService stockCollectionService;

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserStockHoldingsService userStockHoldingsService;
        
    private LocalDateTime time = LocalDateTime.now();
    
    private Map<Long, Queue<WaitListEntry>> waitListMap;

    @GetMapping("/")
    public String showMainPage() {
    	return "mainPage"; // Returns the mainPage.html view
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Serves the login.html file
    }

    @PostMapping("/login/submit")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              RedirectAttributes redirectAttributes,
                              HttpSession session, Model model) {
        // Check if the user exists and if the password matches
        User user = userRepository.findByUsername(username).orElse(null);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // If authentication is successful, pass the user ID to the dashboard view
            session.setAttribute("user", username);
        	if (username.equals("admin")) {
        		redirectAttributes.addAttribute("userId", user.getUserId());
            	return "redirect:/adminPage";
        	}
        
        	redirectAttributes.addAttribute("userId", user.getUserId());
            return "redirect:/dashboard"; // Redirect to the dashboard view
        } else {
            // If authentication fails, redirect to the login page with an error message
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login"; // Redirect to the login view
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam("userId") Long userId, Model model, HttpSession session) {
        User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
        List<Stock> stocks = stockCollectionService.getAllStocks(); // Assume this method exists in your service
        model.addAttribute("stocks", stocks);
        
        session.setAttribute("sessionId", 2563L);
        
        model.addAttribute("user", user);
//        model.addAttribute("invValue", 0);
//        model.addAttribute("curValue", 0);
        
        List<UserStockHoldings> userStockHoldings = userStockHoldingsRepository.findByUserId(userId);
        
        double totalInvestedValue = 0;
        double totalCurrentValue = 0;
        for (UserStockHoldings stockHoldings : userStockHoldings) {
            Stock stock = stockRepository.findById(stockHoldings.getStockId()).orElse(null);
            
            totalInvestedValue += stockHoldings.getAverageStockPrice() * stockHoldings.getQuantity();
            totalCurrentValue += stock.getCurPrice() * stockHoldings.getQuantity();
            
        }
        
        model.addAttribute("invValue", totalInvestedValue);
        model.addAttribute("curValue", totalCurrentValue);        
        model.addAttribute("change", Math.abs(totalCurrentValue - totalInvestedValue));        

        return "dashboard"; // Redirect to the dashboard view
    }
    
    @GetMapping("/stockInfo")
    public String showStockDetails(@RequestParam("userId") Long userId,
                                   @RequestParam("stockId") Long stockId,
                                   Model model, HttpSession session) {
    	User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
    	Stock stock = stockRepository.findById(stockId).orElse(null);
    	Long pairId = (stockId << 7) + userId;
    	UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
		
        // Add the stock details to the model
        model.addAttribute("stockName", stock.getName());
        model.addAttribute("stockPrice", stock.getCurPrice());
        model.addAttribute("yesPrice", stock.getYesPrice());
        model.addAttribute("stockDesc", stock.getStockDesc());
        model.addAttribute("change", Math.abs(stock.getCurPrice() - stock.getYesPrice()));
        model.addAttribute("userId", userId);
        model.addAttribute("stockId", stockId);
        model.addAttribute("username", user.getUsername());
        
        
        if (holding != null) {
        	model.addAttribute("stockHolding", holding.getQuantity());
        	model.addAttribute("pendingBuy", holding.getPendingBuy());
        	model.addAttribute("pendingSell", holding.getPendingSell());
        } else {
        	model.addAttribute("stockHolding", 0);
        	model.addAttribute("pendingBuy", 0);
        	model.addAttribute("pendingSell", 0);
        }
        
        
        List<UserStockHoldings> stockQuantity = userStockHoldingsRepository.findByStockId(userId);
        int sumQuantity=0;
        for (UserStockHoldings stockHoldings : stockQuantity) {
        	sumQuantity += stockHoldings.getQuantity();
        }
        model.addAttribute("totalShares", sumQuantity);
        
        return "stockInfo"; // Return the view name
    }
    
    @GetMapping("/buyStock")
    public String buyStock(@RequestParam("userId") Long userId,
                           @RequestParam("stockId") Long stockId,
                           @RequestParam("shares") Integer shares, 
                           HttpSession session) {
    	User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
        // Handle buying logic here
        // e.g., update database, process transaction, etc.
    	
        WaitListEntry entry = new WaitListEntry(userId, shares, true);
    	  
        transactionService.addToWaitList(entry, waitListMap.get(stockId), stockId);
    	
    	
    	return "redirect:/stockInfo?userId=" + userId + "&stockId=" + stockId; // Redirect to dashboard or another page
    }

    @GetMapping("/sellStock")
    public String sellStock(@RequestParam("userId") Long userId,
                            @RequestParam("stockId") Long stockId,
                            @RequestParam("shares") Integer shares,
                            HttpSession session) {
    	User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
        // Handle selling logic here
        // e.g., update database, process transaction, etc.
    	
    	WaitListEntry entry = new WaitListEntry(userId, shares, false);
  	  
        transactionService.addToWaitList(entry, waitListMap.get(stockId), stockId);
    	
    	
        return "redirect:/stockInfo?userId=" + userId + "&stockId=" + stockId; // Redirect to dashboard or another page
    }

    
    @PostMapping("/signup/submit")
    public String handleRegister(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("firstName") String firstName,
                                 @RequestParam("lastName") String lastName,
                                 @RequestParam("email") String email,
                                 @RequestParam("pan") String pan,
                                 Model model) {
        

        // Create a new user and save it to the database
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPan(pan);
        user.setPhone(phone);
        
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));        
        
        if (userCollectionService.isUserUnique(email, pan, username, phone)) {
      	  System.out.println("Register Success");
      	  userRepository.save(user);
        }
        else {
      	  System.out.println("Register Failed");
      	  return "mainPage";
        }
        
        userRepository.save(user);

        userCollectionService.processUsernamesAndPasswords();

        List<User> users = userCollectionService.getAllUsers(); // Assume this method exists in your service
        model.addAttribute("users", users);
        model.addAttribute("registersuccess", true);
        
        return "redirect:/login"; // Redirect to a success page or dashboard
    }
    
    @GetMapping("/adminPage")
    public String showAdminPage(@RequestParam("userId") Long userId, HttpSession session, Model model) {
    	User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
        List<TransactionHistory> userTransactionHistory = transactionHistoryRepository.findByTimeAfter(time);
        Collections.reverse(userTransactionHistory);
        model.addAttribute("transactions", userTransactionHistory);
        
        List<Stock> stocks = stockCollectionService.getAllStocks(); // Assume this method exists in your service
        model.addAttribute("stocks", stocks);
        model.addAttribute("user", user);
        model.addAttribute("transactionsCount", userTransactionHistory.size());
        
        long totalOrderVolume = 0;

        for (Stock stock : stocks) {
            totalOrderVolume += stock.getVolume();
        }
        
        model.addAttribute("totalOrderVolume", totalOrderVolume);
        
        return "adminPage"; // Serves the login.html file
    }
    
    @PostMapping("/uploadStockList")
    public String uploadStockList(@RequestParam("companyListFile") MultipartFile file,
                                  RedirectAttributes redirectAttributes, 
                                  HttpSession session) {
    	User admin = userRepository.findByUsername("admin").orElse(null);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/adminPage?userId=" + admin.getUserId(); // Redirect to the appropriate page
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Stock> stocks = mapper.readValue(file.getInputStream(), new TypeReference<List<Stock>>() {});
            
        	waitListMap = new HashMap<>();
        	time = LocalDateTime.now();
        	
        	List<UserStockHoldings> userStockHoldingsList = userStockHoldingsRepository.findAll();
        	
        	for (UserStockHoldings userStockHoldings: userStockHoldingsList) {
        		userStockHoldings.setPendingBuy(0);
        		userStockHoldings.setPendingSell(0);
        		userStockHoldingsRepository.save(userStockHoldings);
        	}
        	
        	boolean isTableEmpty = userStockHoldingsRepository.count() == 0;
        	for (Stock stock: stocks) {
        		Stock existingStock = stockRepository.findByName(stock.getName()).orElse(null);
        		
        		if (existingStock != null) {
        			existingStock.setYesPrice(existingStock.getCurPrice());
        			existingStock.setCurPrice(stock.getCurPrice());
        			stockRepository.save(existingStock);
        			stock.setStockId(existingStock.getStockId());
        		} else {
        			stockRepository.save(stock);
        		}
        		
        		if(isTableEmpty) {
        			userStockHoldingsService.addStockHolding(admin.getUserId(), 
        					stock.getStockId(), stock.getTotalShares(), 0, stock.getCurPrice());
        		}
        		
        		Queue<WaitListEntry> queueAdmin = new LinkedList<>();

        		waitListMap.put(stock.getStockId(), queueAdmin);
        		
        		Long pairId = (stock.getStockId() << 7L) + admin.getUserId();
        		UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        		
        		if (holding != null) {
        			WaitListEntry entry = new WaitListEntry(admin.getUserId(), holding.getQuantity(), false);
        			transactionService.addToWaitList(entry, waitListMap.get(stock.getStockId()), stock.getStockId());
        		}
            }
        	
            redirectAttributes.addFlashAttribute("message", "File uploaded and stocks saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file.");
        }
        return "redirect:/adminPage?userId=" + admin.getUserId(); // Redirect to the appropriate page
    }
    
    @GetMapping("/userProfile")
    public String showUserProfile(@RequestParam("userId") Long userId, Model model, HttpSession session) {
        User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        
        String userCheck = (String) session.getAttribute("user");
        if (userCheck == null || !userCheck.equals(user.getUsername())) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        
        List<UserStockHoldings> userStockHoldings = userStockHoldingsRepository.findByUserId(userId);
        
        List<UserDashboard> userDashboardList = new ArrayList<>();
        List<PendingTransactions> pendingTransactionsList = new ArrayList<>();
        
        double totalInvestedValue = 0;
        double totalCurrentValue = 0;
        for (UserStockHoldings stockHoldings : userStockHoldings) {
            UserDashboard dashboard = new UserDashboard();
            PendingTransactions pendingTransactions = new PendingTransactions();
            
            // Copy data from UserStockHoldings to UserDashboard
            Stock stock = stockRepository.findById(stockHoldings.getStockId()).orElse(null);
            
            dashboard.setStockName(stock.getName());
            dashboard.setQuantity(stockHoldings.getQuantity());
            dashboard.setAvgInvValue(stockHoldings.getAverageStockPrice());
            
            dashboard.setInvValue(stockHoldings.getAverageStockPrice() * stockHoldings.getQuantity());
            totalInvestedValue += stockHoldings.getAverageStockPrice() * stockHoldings.getQuantity();
            
            dashboard.setCurValue(stock.getCurPrice() * stockHoldings.getQuantity());
            totalCurrentValue += stock.getCurPrice() * stockHoldings.getQuantity();
            
            pendingTransactions.setStockName(stock.getName());
            if (stockHoldings.getPendingBuy() > 0) {
		        pendingTransactions.setPending(stockHoldings.getPendingBuy());
		        pendingTransactions.setBuy(true);
	            pendingTransactionsList.add(pendingTransactions);
            } else if (stockHoldings.getPendingSell() > 0) {
		        pendingTransactions.setPending(stockHoldings.getPendingSell());
		        pendingTransactions.setBuy(false);
	            pendingTransactionsList.add(pendingTransactions);
            }
            
            // Modify or calculate additional fields if needed
            // Example: Modify current value (curValue) based on some logic
            // dashboard.setCurValue(stockHoldings.getCurValue() * 1.05); // Increase by 5%
            
            // Add the dashboard entry to the list
            userDashboardList.add(dashboard);
        }
        
        List<TransactionHistory> userTransactionHistory = transactionHistoryRepository.findByUserId(userId);

        Collections.reverse(userTransactionHistory);
        model.addAttribute("transactions", userTransactionHistory);
        
        model.addAttribute("pendingTransactionsList", pendingTransactionsList);
        model.addAttribute("userDashboardList", userDashboardList);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        
        model.addAttribute("totalInvestedValue", totalInvestedValue);
        model.addAttribute("totalCurrentValue", totalCurrentValue);        
        
//        List<TransactionHistory> userTransactionHistory = transactionHistoryRepository.findByUserId(userId);
//        System.out.println(userTransactionHistory.get(2).getTransactionId());
        return "userProfile";
    }
    
    @GetMapping("/saveUsername")
    public String saveUsername(@RequestParam("userId") long userId, @RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        // Save username logic
    	User user = userRepository.findById(userId).orElse(null);
    	if (user != null) {
    		if(userCollectionService.isUsernameUnique(username)) {
        		user.setUsername(username);
        		userRepository.save(user);

    		}
    	}
    	
        return "redirect:/userProfile?userId=" + userId;
    }

    @GetMapping("/saveFirstname")
    public String saveFirstname(@RequestParam("userId") long userId,
                                @RequestParam("firstname") String firstname,
                                RedirectAttributes redirectAttributes) {
        // Retrieve user and update firstname
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setFirstName(firstname);
            userRepository.save(user);
        }
        return "redirect:/userProfile?userId=" + userId;
    }

    @GetMapping("/saveLastname")
    public String saveLastname(@RequestParam("userId") long userId,
                               @RequestParam("lastname") String lastname,
                               RedirectAttributes redirectAttributes) {
        // Retrieve user and update lastname
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setLastName(lastname);
            userRepository.save(user);
        }
        return "redirect:/userProfile?userId=" + userId;
    }

    @GetMapping("/savePhone")
    public String savePhone(@RequestParam("userId") long userId,
                            @RequestParam("phone") String phone,
                            RedirectAttributes redirectAttributes) {
        // Retrieve user and update phone number
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
        	if(userCollectionService.isPhoneUnique(phone)) {
                user.setPhone(phone); // Assuming a `setPhone` method exists in the User entity
                userRepository.save(user);
    		}
        }
        return "redirect:/userProfile?userId=" + userId;
    }

    @GetMapping("/saveEmail")
    public String saveEmail(@RequestParam("userId") long userId,
                            @RequestParam("email") String email,
                            RedirectAttributes redirectAttributes) {
        // Retrieve user and update email
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
        	if(userCollectionService.isEmailUnique(email)) {
        		  user.setEmail(email);
                  userRepository.save(user);
    		}
          
        }
        return "redirect:/userProfile?userId=" + userId;
    }
    
    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
    	session.invalidate(); // Invalidate session
    	return "redirect:/login"; 
    }
}