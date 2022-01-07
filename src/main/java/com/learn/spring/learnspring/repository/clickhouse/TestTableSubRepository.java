package com.learn.spring.learnspring.repository.clickhouse;

import com.learn.spring.learnspring.entities.clickhouse.TestTableChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTableSubRepository extends JpaRepository<TestTableChildEntity, Short> {
    List<TestTableChildEntity> findById(Integer id);

}
