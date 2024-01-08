package com.cmy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:Cmy
 * @Date:2024-01-05 16:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistenceDTO {

    private String postId;

    private Boolean exist = false;

    private String remoteName;

    private List<String> remotecategoryList;

    private String mtTextMore;

    private String mtExcerpt;

    private String mtKeywords;

}

    