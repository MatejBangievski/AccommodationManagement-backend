package com.example.emtlab.repository;

import com.example.emtlab.model.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(
        type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {}
    )
    @Query("SELECT u FROM User u")
    List<User> findAllWithoutReservations();

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);
}
