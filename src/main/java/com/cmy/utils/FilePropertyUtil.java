package com.cmy.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * @Author:Cmy
 * @Date:2023-11-29 15:30
 */
@Slf4j
public class FilePropertyUtil {

    private static final String postId = "user.postid";

    /**
     * some os may not support this method
     * @param filePath
     * @return
     */
    public static boolean setMarkdownPostID(String filePath,String inputPostId){
        Path path = Paths.get(filePath);
        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        try {
            int write = fileAttributeView.write(postId, StandardCharsets.UTF_8.encode(inputPostId));
            return write>0;
        } catch (Exception e) {
            log.debug(ThrowableUtil.getStackTrace(e));
        }
        return false;
    }

    public static String getMarkdownPostID(String filePath){
        String thePostId = "";
        Path path = Paths.get(filePath);
        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        ByteBuffer buf = null;
        try {
            buf = ByteBuffer.allocate(fileAttributeView.size(postId));
            fileAttributeView.read(postId,buf);
            buf.flip();
            thePostId = StandardCharsets.UTF_8.decode(buf).toString();
        } catch (Exception e) {
            log.debug(ThrowableUtil.getStackTrace(e));
        }
        return thePostId;
    }

    public static void main(String[] args) {
        //String markdownPostID = getMarkdownPostID("F:\\CmyJournal\\codingLife\\2023-11-23\\a.md");
        //String markdownPostID2 = getMarkdownPostID("F:\\CmyJournal\\codingLife\\2023-11-23\\2023-11-23-idea技巧-自定义后缀补全.md");
        //System.out.println(markdownPostID);
        //System.out.println(markdownPostID2);
        String filePath = "F:\\CmyJournal\\codingLife\\2023-11-23\\hell.md";
        //setMarkdownPostID(filePath,"fasdfasdfasdf");
        String markdownPostID = getMarkdownPostID(filePath);
        System.out.println(markdownPostID);
    }
}

    