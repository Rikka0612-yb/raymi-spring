package com.rikka.raymispring.repository;

import com.rikka.raymispring.model.entity.PlayerAchievementsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author 晏波
 * 2026/3/22 3:55
 */
@Repository
public interface PlayerAchievementsRepository extends JpaRepository<PlayerAchievementsEntity, PlayerAchievementsEntity.PrimaryKey>, QuerydslPredicateExecutor<PlayerAchievementsEntity> {
}
