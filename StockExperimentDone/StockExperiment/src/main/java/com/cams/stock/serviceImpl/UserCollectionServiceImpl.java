package com.cams.stock.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cams.stock.model.User;
import com.cams.stock.repository.UserRepository;
import com.cams.stock.service.UserCollectionService;

@Service
public class UserCollectionServiceImpl implements UserCollectionService {

    private UserRepository userRepository;
    
	public UserCollectionServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override

    public boolean isEmailUnique(String email) {
        return !userRepository.findByEmail(email).isPresent();

    }

    @Override
    public boolean isPanUnique(String pan) {

        return !userRepository.findByPan(pan).isPresent();

    }

    @Override
    public boolean isPhoneUnique(String phone) {

        return !userRepository.findByPhone(phone).isPresent();

    }

    @Override
    public boolean isUsernameUnique(String username) {
        return !userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isUserUnique(String email, String pan, String username, String phone) {

        return isEmailUnique(email) && isPanUnique(pan) && isUsernameUnique(username) && isPhoneUnique(phone);

    }
	
    // Method to get usernames and passwords as separate arrays
	@Override
	public void processUsernamesAndPasswords() {
        List<User> users = userRepository.findAll();

        // Create arrays to store usernames and passwords
        String[] usernames = new String[users.size()];
        String[] passwords = new String[users.size()];

        // Populate arrays
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            usernames[i] = user.getUsername();
            passwords[i] = user.getPassword();
        }

        // Optionally print or return the arrays for verification
        System.out.println("Usernames: " + String.join(", ", usernames));
        System.out.println("Passwords: " + String.join(", ", passwords));
    }
	
	@Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
	
}
