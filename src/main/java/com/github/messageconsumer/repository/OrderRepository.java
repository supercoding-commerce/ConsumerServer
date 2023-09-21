package com.github.messageconsumer.repository;


import com.github.messageconsumer.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(
            "SELECT o FROM Order o " +
                    "WHERE o.users.id = :userId " +
                    //"SELECT c FROM Cart c " +
                    "AND o.id > :cursorId " +
                    "ORDER BY o.id ASC "
    )
    Page<Order> findAllByUsersIdAndCursorId(Long userId, Long cursorId, PageRequest of);

    Optional<Order> findByIdAndUsersId(Long orderId, Long userId);

    List<Order> findAllByUsersIdOrderByCreatedAtDesc(Long userId);
}
