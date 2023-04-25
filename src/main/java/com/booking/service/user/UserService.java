package com.booking.service.user;

import com.booking.entity.ChartData;
import com.booking.entity.domain.Authority;
import com.booking.entity.domain.Profile;
import com.booking.entity.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Set<Authority> getAuthoritiesByUsername(String username);

    Optional<User> find(String username);
    Profile findProfileByUser(User user);
    List<Profile> findAllProfiles();
    boolean changeActiveStatus(String username, boolean isActive);
    Profile editProfileData(Profile profile, User user);
    User changeAuthData(User user, String oldPass, String newPass);

    boolean registerUser(Profile profile);

    ChartData getNumberOfUsersPerLastYear();
}
