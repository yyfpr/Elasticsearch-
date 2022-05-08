package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content implements Serializable {
    @Field(type = FieldType.Keyword)//模糊查询注解
    private String title;
    private String datesource;
    private String hrefurl;
   /* private String date;
    private String source;*/
    private String content;
    private String img;

}