package com.learn.spring.learnspring.repository.clickhouse;

import com.learn.spring.learnspring.entities.clickhouse.TestTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTableRepository extends JpaRepository<TestTableEntity, Short> {
    List<TestTableEntity> findById(Integer id);

}
