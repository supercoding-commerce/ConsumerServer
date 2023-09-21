package com.github.messageconsumer.repository;

import com.github.messageconsumer.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query(
            "SELECT c FROM Cart c " +
                    "WHERE c.users.id = :userId " +
                    //"SELECT c FROM Cart c " +
                    "AND c.id > :cursorId " +
                    "ORDER BY c.id ASC "
    )
    Page<Cart> findAllByUserId(Long userId, Long cursorId, Pageable pageable);

    Optional<Cart> findById(Long cartId);

    Optional<Cart> findByIdAndUsersId(Long id, Long userId);

    boolean existsByUsersIdAndProductsId(Long userId, Long productId);

    void deleteAllByUsersId(Long userId);

    List<Cart> findAllByUsersId(Long userId);

    List<Cart> findAllByUsersIdOrderByCreatedAtDesc(Long userId);
}
