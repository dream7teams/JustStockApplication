package net.juststock.trading.service.impl;

import net.juststock.trading.domain.user.UserProfile;
import net.juststock.trading.repository.UserProfileRepository;
import net.juststock.trading.service.interfaces.UserProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile createUser(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    @Override
    public Optional<UserProfile> getUserById(Long id) {
        return userProfileRepository.findById(id);
    }

    @Override
    public Optional<UserProfile> getUserByContactNumber(String contactNumber) {
        return userProfileRepository.findByContactNumber(contactNumber);
    }

    @Override
    public UserProfile updateUser(Long id, UserProfile updatedUser) {
        return userProfileRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(updatedUser.getFullName());
                    existing.setContactNumber(updatedUser.getContactNumber());
                    return userProfileRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userProfileRepository.deleteById(id);
    }
}
