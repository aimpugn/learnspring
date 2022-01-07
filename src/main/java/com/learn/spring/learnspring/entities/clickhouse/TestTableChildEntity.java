package com.learn.spring.learnspring.entities.clickhouse;

import javax.persistence.*;

@Entity
@Table(name = "test_table", schema = "test_db")
public class TestTableChildEntity extends TestTableParentEntity{
    @Basic
    @Id
    @Column(name = "c_int8", columnDefinition = "tinyint")
    private Integer id; // cInt8에서 id로 이름을 바꾸고, TestTableRepository에서도 바꾸니 정상 실행
}
