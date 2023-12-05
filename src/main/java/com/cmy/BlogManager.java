package com.cmy;

import com.cmy.config.ArgsConfig;
import com.cmy.handler.BlogHandler;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

/**
 * Hello world!
 *
 */
public class BlogManager {
    public static void main( String[] args ) throws CmdLineException {
        ArgsConfig argsConfig = new ArgsConfig();
        //使用自定义顺序
        ParserProperties properties = ParserProperties.defaults().withOptionSorter(null);
        CmdLineParser parser = new CmdLineParser(argsConfig,properties);
        parser.setUsageWidth(160);
        parser.parseArgument(args);
        // 没有参数
        if(args.length == 0){
            argsConfig.showHelp(parser);
        }
        // 如果启动参数中包含 帮助参数项， 则打印参数信息
        if(argsConfig.isHelp()){
            argsConfig.showHelp(parser);
        }
        BlogHandler.doHandler(argsConfig);
    }
}
