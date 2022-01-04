@TypeDefs({
        @TypeDef(
                name = "json", typeClass = JsonType.class
        ),
        @TypeDef(
                name = "list-array", typeClass = ListArrayType.class
        )
})

package com.learn.spring.learnspring.entities.clickhouse;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
