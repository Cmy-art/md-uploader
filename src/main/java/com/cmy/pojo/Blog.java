package com.cmy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:Cmy
 * @Date:2023-11-23 15:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Blog extends User{

    /**
     * 方法名字
     */
    private String methodName;

    /**
     * 博客id
     */
    private String blogId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 分类
     */
    private List<String> categories;

    /**
     * 是否发布-默认不发布 在管理页面发布
     */
    private Boolean publish = false;

    private String mtTextMore;

    private String mtExcerpt;

    private String mtKeywords;

}

    