package com.cmy.enums;

/**
 * @Author:Cmy
 * @Date:2023-11-23 15:24
 */
public enum MethodEnum {

    BLOG_UPLOAD("metaWeblog.newPost"),
    MEDIA_POST("metaWeblog.newMediaObject"),
    GET_POST("metaWeblog.getPost"),
    BLOG_EDIT_POST("metaWeblog.editPost");

    private final String methodName;

    MethodEnum(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}

    