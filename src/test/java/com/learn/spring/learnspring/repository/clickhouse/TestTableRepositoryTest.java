package com.learn.spring.learnspring.repository.clickhouse;

import com.learn.spring.learnspring.entities.clickhouse.TestTableEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"dev"})
class TestTableRepositoryTest {
    @Autowired
    private TestTableRepository testTableRepository;

    @Test
    @DisplayName("Retrieve TestDataEntity from ClickHouse Using JPA")
    void TestTableEntity_Returns_TestTableEntity() {
        List<TestTableEntity> results = testTableRepository.findById(1);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("test");
        expected.add("string");
        expected.add("array");
        Assertions.assertEquals(expected, results.get(0).getCArrayString());
    }
}