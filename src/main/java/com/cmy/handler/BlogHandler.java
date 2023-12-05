package com.cmy.handler;

import cn.hutool.core.util.NumberUtil;
import com.cmy.config.ArgsConfig;
import com.cmy.pojo.User;
import com.cmy.utils.FilePropertyUtil;
import com.cmy.utils.MyXmlUtils;
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
import java.util.Map;

/**
 * @Author:Cmy
 * @Date:2023-11-23 16:03
 */
@Slf4j
public class BlogHandler {

    public final static User user = new User("https://rpc.cnblogs.com/metaweblog/void-cmy","VoidCM","2F7F76537B7CEBBB4A869D051FA795DEAD3941172C6AD44213E291806B7D3ACB");

    public static Map<String,String> uploadMap = new HashMap<>();


    public static void doHandler(ArgsConfig argsConfig){
        //String originFile = argsConfig.getOriginFile();
        //String username = argsConfig.getUsername();
        //String secret = argsConfig.getSecret();
        //user.setUsername(username);
        //user.setToken(secret);
        String originFile = "F:\\CmyJournal\\codingLife\\2023-11-23\\hell.md";

        user.setUrl("https://rpc.cnblogs.com/metaweblog/void-cmy");

        //首先判断Blog的文件夹是否存在
        String fullPath = FilenameUtils.getFullPath(originFile);
        String baseName = FilenameUtils.getBaseName(originFile);
        String dataPath = fullPath + baseName + ".assets"+"\\blogData";
        String urlRecordMapPath = dataPath+"\\urlRecords.txt";
        log.info("判断文件夹:{}是否存在", dataPath);
        File file = new File(dataPath);
        if (!file.exists()){
            log.info("...文件不存在,开始创建文件夹");
            boolean mkdirs = file.mkdirs();
            log.info("...文件创建{}",mkdirs?"成功":"失败");
        }else {
            log.info("...文件夹已存在");
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
                boolean exists = UploadUtil.judgeWhetherBlogPostExistsOrNot(markdownPostID);
                //存在->修改 有可能需要处理多媒体
                if (exists){
                    UploadUtil.updateBlog(originFile,Collections.singletonList("[Markdown]"),markdownPostID,false);
                    //将urlRecords 覆写
                    UrlRecordParseUtil.saveUrlRecords(urlRecordMapPath,uploadMap);
                    return;
                }
            }
        }
        //以下都是博文不存在需要上传markdown的情况,同时需要处理多媒体
        UploadUtil.uploadMarkDown(originFile,Collections.singletonList("[Markdown]"),false);
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

    