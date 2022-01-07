package com.learn.spring.learnspring.repository.clickhouse;

import com.learn.spring.learnspring.entities.clickhouse.TestTableEntity;
import com.learn.spring.learnspring.entities.clickhouse.TestTableChildEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles({"dev"})
class TestTableRepositoryTest {
    @Autowired
    private TestTableRepository testTableRepository;
    @Autowired
    private TestTableSubRepository testTableSubRepository;

    @Test
    @DisplayName("Retrieve TestDataEntity from ClickHouse Using JPA")
    void TestTableEntity_Returns_TestTableEntity() {
        List<TestTableEntity> results = testTableRepository.findById(1);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("test");
        expected.add("string");
        expected.add("array");
        Assertions.assertEquals(expected, results.get(0).getCArrayString());
        System.out.println(results.get(0).getCInt256());
    }

    @Test
    @DisplayName("Retrieve TestDataSubEntity from ClickHouse Using JPA")
    void TestTableSubEntity_Returns_TestTableSubEntity() {
        List<TestTableChildEntity> results = testTableSubRepository.findById(1);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("test");
        expected.add("string");
        expected.add("array");
        Assertions.assertEquals(expected, results.get(0).getCArrayString());
        System.out.println(results.get(0).getCInt256());
    }
}