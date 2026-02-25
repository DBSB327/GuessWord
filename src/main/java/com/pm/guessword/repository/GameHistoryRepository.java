package com.pm.guessword.repository;

import com.pm.guessword.model.GameHistory;
import com.pm.guessword.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    Page<GameHistory> findByUser(User user, Pageable pageable);
}
