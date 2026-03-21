package com.rikka.raymispring.repository;

import com.rikka.raymispring.model.entity.OwnedSteamGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * OwnedGame 实体的数据访问层
 * 继承 JpaRepository 提供基础 CRUD
 * 继承 QuerydslPredicateExecutor 提供 QueryDSL 复杂查询支持
 */
@Repository
public interface OwnedGameRepository extends JpaRepository<OwnedSteamGameEntity, OwnedSteamGameEntity.OwnedSteamGameId>, QuerydslPredicateExecutor<OwnedSteamGameEntity> {
}
