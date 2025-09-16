package net.juststock.trading.service.interfaces;

import net.juststock.trading.domain.user.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {
    UserProfile createUser(UserProfile userProfile);
    List<UserProfile> getAllUsers();
    Optional<UserProfile> getUserById(Long id);
    Optional<UserProfile> getUserByContactNumber(String contactNumber);
    UserProfile updateUser(Long id, UserProfile updatedUser);
    void deleteUser(Long id);
}
