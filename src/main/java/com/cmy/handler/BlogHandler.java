package com.cmy.handler;

import ch.qos.logback.core.pattern.FormatInfo;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.cmy.config.ArgsConfig;
import com.cmy.pojo.ExistenceDTO;
import com.cmy.pojo.User;
import com.cmy.utils.UploadUtil;
import com.cmy.utils.UrlRecordParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:Cmy
 * @Date:2023-11-23 16:03
 */
@Slf4j
public class BlogHandler {

    public final static User user = new User();

    public static Map<String,String> uploadMap = new HashMap<>();


    public static void doHandler(ArgsConfig argsConfig){
        String metaWeblogUrl = argsConfig.getMetaWeblogUrl();
        String originFile = argsConfig.getOriginFile();
        String username = argsConfig.getUsername();
        String secret = argsConfig.getSecret();
        boolean publish = "1".equals(argsConfig.getPublish());
        //String categories = argsConfig.getCategories();//categories在博客中调

        //参数校验
        if (StringUtils.isBlank(metaWeblogUrl)){
            log.error("url is null");
            return;
        }

        if (StringUtils.isBlank(originFile)){
            log.error("input file is null");
            return;
        }else {
            if (!"md".equals(FilenameUtils.getExtension(originFile))) {
                log.error("input file is not markdown file");
                return;
            }
        }

        if (StringUtils.isBlank(username)){
            log.error("username is null");
            return;
        }

        if (StringUtils.isBlank(secret)){
            log.error("token is null");
            return;
        }

        user.setUrl(metaWeblogUrl);
        user.setUsername(username);
        user.setToken(secret);


        //首先判断Blog的文件夹是否存在
        String fullPath = FilenameUtils.getFullPath(originFile);
        String baseName = FilenameUtils.getBaseName(originFile);
        String dataPath = fullPath + baseName + ".assets"+"\\blogData";
        String urlRecordMapPath = dataPath+"\\urlRecords.txt";
        log.info("determine whether the folder: {} exists", dataPath);
        File file = new File(dataPath);
        if (!file.exists()){
            log.info("...the folder does not exist, start creating folder");
            boolean mkdirs = file.mkdirs();
            log.info("...the folder create {}",mkdirs?"success":"failed");
        }else {
            log.info("...the folder has already existed");
            //读取dataPath+"\\urlRecords.txt"
            if (Files.exists(Paths.get(urlRecordMapPath))){
                uploadMap = UrlRecordParseUtil.parseUrlRecords(urlRecordMapPath);
            }
        }

        //首先判断文档是否存在
        String markdownPostID = uploadMap.get(FilenameUtils.getName(originFile));
        if (StringUtils.isNotBlank(markdownPostID)){//文档被标记过 说明上传过
            if (NumberUtil.isNumber(markdownPostID)){//如果是数字
                //需要请求 判断文件是否存在
                ExistenceDTO existenceDTO = UploadUtil.judgeWhetherBlogPostExistsOrNot(markdownPostID);
                boolean exists = BooleanUtil.isTrue(existenceDTO.getExist());
                //存在->修改 有可能需要处理多媒体
                if (exists){
                    String remoteName = existenceDTO.getRemoteName();
                    String mtTextMore = existenceDTO.getMtTextMore();
                    String mtExcerpt = existenceDTO.getMtExcerpt();
                    String mtKeywords = existenceDTO.getMtKeywords();
                    List<String> categories = existenceDTO.getRemotecategoryList()==null||existenceDTO.getRemotecategoryList().isEmpty()?Collections.singletonList("[Markdown]"):existenceDTO.getRemotecategoryList();
                    UploadUtil.updateBlog(originFile, categories,markdownPostID,publish,remoteName,mtTextMore,mtExcerpt,mtKeywords);
                    //将urlRecords 覆写
                    UrlRecordParseUtil.saveUrlRecords(urlRecordMapPath,uploadMap);
                    return;
                }
            }
        }
        //以下都是博文不存在需要上传markdown的情况,同时需要处理多媒体
        UploadUtil.uploadMarkDown(originFile,Collections.singletonList("[Markdown]"),publish);
        //将urlRecords 覆写
        UrlRecordParseUtil.saveUrlRecords(urlRecordMapPath,uploadMap);
    }

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("F:\\CmyJournal\\codingLife\\2023-11-23\\a.md");
        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        ByteBuffer allocate = ByteBuffer.allocate(fileAttributeView.size("user.postid"));
        fileAttributeView.read("user.postid",allocate);
        allocate.flip();
        String string = Charset.defaultCharset().decode(allocate).toString();

        System.out.println(string);
    }

}

    