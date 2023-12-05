package com.cmy.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import com.cmy.handler.BlogHandler;
import com.cmy.pojo.Blog;
import com.cmy.pojo.XmlRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @Author:Cmy
 * @Date:2023-11-23 17:22
 */
@Slf4j
public class UploadUtil {


    /**
     * 上传markdown
     *
     * @param absolutePath 绝对路径
     * @return {@link String}
     */
    public static void uploadMarkDown(String absolutePath,List<String> categories,Boolean publish){

        try {
            String fullPath = FilenameUtils.getFullPath(absolutePath);
            String fileName = FilenameUtils.getName(absolutePath);
            String markdown = FileUtil.readUtf8String(absolutePath);
            String updateMarkdown = MarkdownLinkUtil.extractUrlAndUploadFromMarkdown(fullPath,markdown);

            Blog blog = new Blog();
            blog.setBlogId(UUID.randomUUID().toString().replaceAll("-",""));
            blog.setUsername(BlogHandler.user.getUsername());
            blog.setToken(BlogHandler.user.getToken());
            String baseName = FilenameUtils.getBaseName(fileName);
            blog.setTitle(baseName);
            blog.setContent(updateMarkdown);
            blog.setCategories(categories);
            blog.setPublish(publish);

            Document blogXml = MyXmlUtils.generateBlogXml(blog);
            String responseXml = HttpUtil.post(BlogHandler.user.getUrl(), MyXmlUtils.transferToXmlStr(blogXml));
            XmlRes blogUploadedResXml = MyXmlUtils.getBlogUploadedResXml(responseXml);
            if (blogUploadedResXml.getSuccess()){
                String postId = blogUploadedResXml.getData();
                String laterMarkdown = fullPath+baseName+".assets/blogData/" + postId +".md";
                //将markdown 写入./{filename}.assets/blogData
                log.info("will generate markdown :{} in path : {}",laterMarkdown,fullPath);
                File file = FileUtil.writeString(updateMarkdown, laterMarkdown, StandardCharsets.UTF_8);
                if (file.exists()){
                    //用来标识文档是否上传过
                    BlogHandler.uploadMap.put(fileName,postId);
                    log.info("associate blog :{} and postid :{}",absolutePath,postId);
                }
            }else {
                log.info("upload failed!!!!!!!!!!!");
            }
        } catch (IORuntimeException e) {
            log.error(ThrowableUtil.getStackTrace(e));
        }
    }

    /**
     * 判断博客文章是否存在
     *
     * @param postId 帖子ID
     * @return boolean
     */
    public static boolean judgeWhetherBlogPostExistsOrNot(String postId){
        Blog blog = new Blog();
        blog.setBlogId(postId);
        blog.setUsername(BlogHandler.user.getUsername());
        blog.setToken(BlogHandler.user.getToken());
        Document document = MyXmlUtils.generateGetBlogXml(blog);
        String questXml = MyXmlUtils.transferToXmlStr(document);
        String responseXml = HttpUtil.post(BlogHandler.user.getUrl(), questXml);
        return MyXmlUtils.existenceDetermination(responseXml);
    }

    /**
     * 更新博客
     *
     * @param originFile     原始文件
     * @param categories     分类
     * @param markdownPostID 帖子ID
     * @param publish        是否发布
     */
    public static void updateBlog(String originFile, List<String> categories, String markdownPostID, boolean publish) {
        String fullPath = FilenameUtils.getFullPath(originFile);
        String fileName = FilenameUtils.getName(originFile);
        String markdown = FileUtil.readUtf8String(originFile);

        //上传多媒体
        String updateMarkdown = MarkdownLinkUtil.extractUrlAndUploadFromMarkdown(fullPath,markdown);
        Blog blog = new Blog();

        blog.setBlogId(markdownPostID);
        blog.setUsername(BlogHandler.user.getUsername());
        blog.setToken(BlogHandler.user.getToken());
        String baseName = FilenameUtils.getBaseName(fileName);
        blog.setTitle(baseName);
        blog.setContent(updateMarkdown);
        blog.setCategories(categories);
        blog.setPublish(publish);
        Document blogUpdateResXml = MyXmlUtils.generateUpdateBlogXml(blog);
        String responseXml = HttpUtil.post(BlogHandler.user.getUrl(), MyXmlUtils.transferToXmlStr(blogUpdateResXml));
        XmlRes blogUploadedResXml = MyXmlUtils.getBlogUpdateResXml(responseXml);
        log.info("update blog:{}-{}",blog.getBlogId(),(blogUploadedResXml.getSuccess()&&"1".equals(blogUploadedResXml.getData()))?"success":"failed");
    }

    public static void main(String[] args) {
        uploadMarkDown("F:\\CmyJournal\\codingLife\\2023-11-23\\hell.md", Collections.singletonList("[Markdown]"),false);
    }


}

    