package com.cmy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:Cmy
 * @Date:2023-11-29 9:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XmlRes {

    private Integer code;

    private String msg;

    private String data;

    private Boolean success;
}

    