package com.cmy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

/**
 * @Author:Cmy
 * @Date:2023-11-23 16:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArgsConfig {

    @Option(name = "-m",aliases = {"--metaWeblogUrl"},metaVar = "the metaWeblog file",usage = "MetaWeblog访问地址")
    private String metaWeblogUrl;

    @Option(name = "-o",aliases = {"--originFile"},metaVar = "the origin file",usage = "要上传的markdown文件")
    private String originFile;

    @Option(name = "-u",aliases = {"--username"},metaVar = "the blog platform username",usage = "博客平台用户名")
    private String username;

    @Option(name = "-s",aliases = {"--secret"},metaVar = "the metaWebLog token",usage = "MetaWeblog访问令牌")
    private String secret;

    //@Option(name = "-c",aliases = {"--categories"},metaVar = "the blog categories",usage = "博客分类,默认MarkDown")
    //private String categories;

    @Option(name = "-p",aliases = {"--publish"},metaVar = "publish or not", usage = "是否发布,0:不发布,1:发布")
    private String publish;

    @Option (name="-h", aliases = {"-?","--help"}, metaVar = "show help info", usage="帮助信息", handler = BooleanOptionHandler.class, help = true)
    public boolean help;

    public void showHelp(CmdLineParser parser){
        System.out.println("参数说明 [options ...] [arguments...]");
        parser.printUsage(System.out);
    }

    public static void main(String[] args) throws CmdLineException {
        int USAGE_WIDTH = 160;

        ArgsConfig argsConfig = new ArgsConfig();
        //使用自定义顺序
        ParserProperties properties = ParserProperties.defaults().withOptionSorter(null);
        CmdLineParser parser = new CmdLineParser(argsConfig,properties);
        parser.setUsageWidth(USAGE_WIDTH);
        parser.parseArgument(args);
        // 没有参数
        if(args.length == 0){
            argsConfig.showHelp(parser);
        }
        // 如果启动参数中包含 帮助参数项， 则打印参数信息
        if(argsConfig.isHelp()){
            argsConfig.showHelp(parser);
        }
        System.out.println(argsConfig.toString());
    }
}

    