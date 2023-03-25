package com.booking.repository;

import com.booking.entity.domain.Profile;
import com.booking.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findProfileByUser(User user);
    List<Profile> findProfilesByUser_isActive(boolean isActive);
    int removeByUser(User user);
}
