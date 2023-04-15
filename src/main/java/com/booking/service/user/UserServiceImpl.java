package com.booking.service.user;

import com.booking.entity.ChartData;
import com.booking.entity.domain.Authority;
import com.booking.entity.domain.Profile;
import com.booking.entity.domain.User;
import com.booking.entity.enums.Role;
import com.booking.exception.ExistingDataException;
import com.booking.exception.IncorrectDataException;
import com.booking.exception.NoSuchDataException;
import com.booking.repository.AuthorityRepository;
import com.booking.repository.ProfileRepository;
import com.booking.repository.UserRepository;
import com.booking.utils.ValidationUtil;
import com.booking.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean changeActiveStatus(String username, boolean isActive) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NoSuchDataException("Данного пользователя не существует"));
        return userRepository.changeActiveStatus(user.getUsername(), isActive) == 1;
    }

    @Override
    public List<Profile> findAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public ChartData getNumberOfUsersPerLastYear() {
        List<User> users = userRepository.findAll();

        LocalDateTime localDateTime = LocalDateTime.now();
        users = users.stream().filter(user ->
                        user.getDate().after(Timestamp.valueOf(localDateTime.minusYears(1))))
                .collect(Collectors.toList());

        int[] array = new int[12];
        String[] axiosX = new String[12];
        for (int i = 0; i < 12; i++) {
            final int iterator = i;
            array[i] = (int) users.stream().filter(user ->
                    user.getDate().after(Timestamp.valueOf(localDateTime.minusYears(1).plusMonths(iterator))) &&
                    user.getDate().before(Timestamp.valueOf(localDateTime.minusYears(1).plusMonths(iterator + 1)))
            ).count();
            axiosX[i] = localDateTime.minusYears(1).plusMonths(iterator).getMonth()
                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        }

        return ChartData.builder().data(array).axisX(axiosX).build();
    }

    @Override
    public boolean registerUser(Profile profile) {
        ValidationUtil.validate(profile.getUser(), userValidator);
        if (!userRepository.existsById(profile.getUser().getUsername())) {
            profile.getUser().setDate(new Date(System.currentTimeMillis()));
            profile.setUser(saveAllRoles(profile.getUser(), Role.USER));
            profile.getUser().setActive(true);
            profile.getUser().setPassword("{bcrypt}" + passwordEncoder.encode(profile.getUser().getPassword()));
            profileRepository.save(profile);
            return true;
        } else throw new ExistingDataException("Данный пользователь уже существует");
    }

    @Override
    public Optional<User> find(String username) {
        return userRepository.findById(username);
    }

    @Override
    public Set<Authority> getAuthoritiesByUsername(String username) {
        User user = userRepository.findById(username).orElse(null);
        if (user != null && user.getRoles() != null)
            return user.getRoles();
        return null;
    }

    private User saveAllRoles(User user, Role role) {
        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            user.getRoles().add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return userRepository.save(user);
    }
}
