package com.chanseok.api.repository;

import com.chanseok.api.entity.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWordRepository extends JpaRepository<UserWord, Long> {
}
