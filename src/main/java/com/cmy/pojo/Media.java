package com.cmy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Base64;

/**
 * @Author:Cmy
 * @Date:2023-11-23 15:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Media extends User{

    /**
     * 方法名字
     */
    private String methodName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String mimeType;

    /**
     * base64
     */
    private String bits;

}

    