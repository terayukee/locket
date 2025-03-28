package com.locket.user.domain.pet.repository;

import com.locket.user.domain.pet.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    boolean existsByUserId(Long userId);
    Optional<Character> findByUserId(Long userId);
}