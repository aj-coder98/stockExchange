package com.cams.stock.service;

import java.util.List;

import com.cams.stock.model.User;
//import com.cams.stock.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;

public interface UserCollectionService {
	
	boolean isEmailUnique(String email);
	boolean isPanUnique(String pan);
	boolean isPhoneUnique(String phone);
	boolean isUsernameUnique(String username);
	boolean isUserUnique(String email, String pan, String phone, String username);
	void processUsernamesAndPasswords();
	List<User> getAllUsers();
}

