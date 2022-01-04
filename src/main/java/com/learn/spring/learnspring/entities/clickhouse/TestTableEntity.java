package com.learn.spring.learnspring.entities.clickhouse;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "test_table", schema = "test_db")
public class TestTableEntity implements Serializable {
    @Basic
    @Id
    @Column(name = "c_int8", columnDefinition = "tinyint")
    private Integer id; // cInt8에서 id로 이름을 바꾸고, TestTableRepository에서도 바꾸니 정상 실행
    @Basic
    @Column(name = "c_int16", columnDefinition = "int16")
    private Integer cInt16;
    @Basic
    @Column(name = "c_int32", columnDefinition = "int32")
    private Integer cInt32;
    @Basic
    @Column(name = "c_int64", columnDefinition = "int64")
    private Long cInt64;
    @Basic
    @Column(name = "c_int128", columnDefinition = "int128")
    private BigDecimal cInt128;
    @Basic
    @Column(name = "c_int256", columnDefinition = "int256")
    private BigDecimal cInt256;
    @Basic
    @Column(name = "c_float32", columnDefinition = "float32")
    private Float cFloat32;
    @Basic
    @Column(name = "c_float64", columnDefinition = "float64")
    private Float cFloat64;
    @Basic
    @Column(name = "c_decimal32_9", columnDefinition = "Decimal(9,9)")
    private BigDecimal cDecimal329;
    @Basic
    @Column(name = "c_decimal64_18", columnDefinition = "Decimal(18,18)")
    private BigDecimal cDecimal6418;
    @Basic
    @Column(name = "c_decimal128_38", columnDefinition = "Decimal(38,38)")
    private BigDecimal cDecimal12838;
    @Basic
    @Column(name = "c_decimal256_76", columnDefinition = "Decimal(76,76)")
    private BigDecimal cDecimal25676;
    @Basic
    @Column(name = "c_string", columnDefinition = "string")
    private String cString;
    @Basic
    @Column(name = "c_fixedstring", columnDefinition = "fixedstring")
    private String cFixedstring;
    @Basic
    @Column(name = "c_date", columnDefinition = "date")
    private LocalDateTime cDate;
    @Basic
    @Column(name = "c_date32", columnDefinition = "date32")
    private LocalDateTime cDate32;
    @Basic
    @Column(name = "c_datetime", columnDefinition = "datetime")
    private LocalDateTime cDatetime;
    @Basic
    @Column(name = "c_datetime64", columnDefinition = "datetime64")
    private LocalDateTime cDatetime64;
    @Basic
    @Column(name = "c_array_int8", columnDefinition = "array")
    @Type(type = "json")
    private List<Short> cArrayInt8;
    @Basic
    @Column(name = "c_array_int16", columnDefinition = "array")
    @Type(type = "json")
    private List<Integer> cArrayInt16;
    @Basic
    @Column(name = "c_array_string", columnDefinition = "array")
    @Type(type = "list-array")
    private List<String> cArrayString;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TestTableEntity that = (TestTableEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
