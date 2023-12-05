package com.cmy.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import com.cmy.enums.MimeEnum;
import com.cmy.handler.BlogHandler;
import com.cmy.pojo.Media;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:Cmy
 * @Date:2023-11-23 17:05
 */
@Slf4j
public class MarkdownLinkUtil {

    private static final Pattern LINK_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    /**
     * 使用正则表达式
     * 提取Markdown链接语法中的URL,上传 然后 替换
     *
     * @param markdown 风格的链接语法
     * @return URL，如果没有找到，则返回null
     */
    public static String extractUrlAndUploadFromMarkdown(String parentPath,String markdown) {
        Matcher matcher = LINK_PATTERN.matcher(markdown);
        StringBuffer updateMarkdown = new StringBuffer();
        while (matcher.find()){
            String originPath = matcher.group(2);
            log.info("originPath:{}", originPath);
            if (StringUtils.isNotBlank(originPath)){
                boolean isHttpUrl = originPath.startsWith("http");//不上传http url
                if (!isHttpUrl){

                    String updatePath;
                    //如果文件能够找到记录,沿用之前的url
                    String lastUploadUrl = BlogHandler.uploadMap.get(originPath);
                    if (StringUtils.isNotBlank(lastUploadUrl)) {
                        //通过originPath找到了上一次上传之后的路径说明不用上送该路径,直接使用上一次的路径
                        log.debug("use last uploadPath:{}",lastUploadUrl);
                        updatePath = lastUploadUrl;
                    }else {
                        //上传图片 获得返回的图片url
                        updatePath = uploadMedia(parentPath, originPath);
                        BlogHandler.uploadMap.put(originPath,updatePath);
                        log.debug("upload media uploadPath:{}",updatePath);
                    }

                    if (StringUtils.isNotBlank(updatePath)){
                        matcher.appendReplacement(updateMarkdown,"![" + matcher.group(1) + "](" + updatePath + ")");
                    }else {
                        log.error("image upload failed,use origin image");
                    }
                }
            }
            //do nothing
        }
        matcher.appendTail(updateMarkdown);
        return updateMarkdown.toString();
    }


    /**
     * 上传媒体
     *
     * @param parentPath   父路径
     * @param relativePath 相对路径
     * @return {@link String} 返回的http url
     */
    public static String uploadMedia(String parentPath,String relativePath){
        String uploadImageUrl = null;
        try {
            String abPath = parentPath + relativePath;
            String name = FilenameUtils.getName(abPath);
            String suffix = "." + FilenameUtils.getExtension(abPath);
            byte[] bytes = FileUtil.readBytes(abPath);
            byte[] base64 = Base64.getEncoder().encode(bytes);//转base64
            Media media = new Media();
            media.setUsername(BlogHandler.user.getUsername());
            media.setToken(BlogHandler.user.getToken());
            media.setFileName(name);
            media.setMimeType(MimeEnum.getMimeTypeBySuffix(suffix));
            media.setBits(new String(base64));

            Document document = MyXmlUtils.generateMediaXml(media);
            //根据路径读取文件
            String mediaXml = XmlUtil.toStr(document, true);
            String responseXml = HttpUtil.post(BlogHandler.user.getUrl(), mediaXml);
            uploadImageUrl = MyXmlUtils.getUploadImageUrl(responseXml);
        } catch (IORuntimeException e) {
            e.printStackTrace();
        }
        return uploadImageUrl;//需要解析xml
    }


}

    