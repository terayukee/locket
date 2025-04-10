package com.locket.user.domain.pet.repository;

import com.locket.user.domain.pet.entity.Character;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    boolean existsByUserId(Long userId);
    Optional<Character> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Character c WHERE c.userId = :userId")
    Optional<Character> findByUserIdForUpdate(@Param("userId") Long userId);
}