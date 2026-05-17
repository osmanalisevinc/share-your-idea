package com.share.repository;

 import com.share.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

 import java.util.List;
 import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository< User, String> {
    Optional<com.share.model.User> findByUserName(String username);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u " +
            "FROM User u " +
            "where (:search is null or (u.userName ILIKE (%:search%)) or u.userSurname ILIKE (%:search%) or u.email ILIKE (%:search%))")
    Page<User> findAllAsPage(String search, Pageable pageable);

    @Query(value = "WITH RankedUsers AS (" +
            "SELECT " +
            "u.id AS id, " +
            "u.star AS star, " +
            "u.rosette AS rosette, " +
            "ROW_NUMBER() OVER (ORDER BY u.star DESC, u.rosette DESC,u.user_name) AS rank " +
            "FROM users u" +
            ") " +
            "SELECT " +
            "rank " +
            "FROM RankedUsers " +
            "WHERE id = :id",
            nativeQuery = true)
    Long findOrderByUser(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.transactions " +
            "WHERE u.isDeleted = false")
    List<User> findAllUsersWithRolesAndTransactions();
}
