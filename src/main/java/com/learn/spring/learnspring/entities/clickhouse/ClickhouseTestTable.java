package com.learn.spring.learnspring.entities.clickhouse;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_table")
public class ClickhouseTestTable {
    @Id
    private Short cInt8;
}
