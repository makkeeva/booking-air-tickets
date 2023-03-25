package com.booking.repository;

import com.booking.entity.domain.Authority;
import com.booking.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findUsersByRolesContains(Authority authority);

    int removeByUsername(String username);

    @Modifying
    @Query("update User u set u.isActive =:isActive where u.username =:username")
    int changeActiveStatus(@Param("username") String username,
                           @Param("isActive") boolean isActive);
}
