package com.cmy.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.XmlUtil;
import com.cmy.enums.MethodEnum;
import com.cmy.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.Collection;

/**
 * @Author:Cmy
 * @Date:2023-11-28 14:15
 */
@Slf4j
public class MyXmlUtils {

    private final static Struct struct = new Struct();

    public static <T> Document createDocument(String rootName, String methodName){
        Document document = XmlUtil.createXml("methodCall");//创建带根节点的xml
        Element methodCallElement = XmlUtil.getRootElement(document);
        //方法名
        Element methodNameElement = document.createElement("methodName");
        methodNameElement.setTextContent(methodName);
        methodCallElement.appendChild(methodNameElement);
        return document;
    }

    public static <T> void addParam(Document document, T param){
        Element methodCallElement = XmlUtil.getRootElement(document);

        Element paramsElement = null;
        NodeList paramsNodeList = document.getElementsByTagName("params");
        if (paramsNodeList.item(0)==null) {
            paramsElement = document.createElement("params");
        }else {
            paramsElement = (Element) paramsNodeList.item(0);
        }


        Element paramElement = document.createElement("param");
        Element valueElement = document.createElement("value");
        Element typeElement = null;
        //判断T的类型
        if (param instanceof String){
            String theString = (String) param;
            Element string = document.createElement("string");
            string.setTextContent(theString);
            typeElement = string;
        }else if (param instanceof Boolean){
            Boolean theBoolean = (Boolean) param;
            Element aBoolean = document.createElement("boolean");
            aBoolean.setTextContent(BooleanUtil.isTrue(theBoolean)?"1":"0");
            typeElement = aBoolean;
        }else if (param instanceof Struct){
            Element struct = document.createElement("struct");
            typeElement = struct;
        }
        valueElement.appendChild(typeElement);
        paramElement.appendChild(valueElement);
        paramsElement.appendChild(paramElement);
        methodCallElement.appendChild(paramsElement);
    }

    public static <T> void addMember(Document document, String name, T value){

        Element structElement = null;
        NodeList structNodeList = document.getElementsByTagName("struct");
        if (structNodeList.item(0)==null) {
            addParam(document,struct);//创建
            structNodeList = document.getElementsByTagName("struct");
        }
        structElement = (Element) structNodeList.item(0);

        Element memberElement = document.createElement("member");
        Element nameElement = document.createElement("name");
        nameElement.setTextContent(name);

        Element valueElement = document.createElement("value");
        Element typeElement = null;
        //判断T的类型
        if (value instanceof String){
            String theValue = (String) value;
            Element string = document.createElement("string");
            string.setTextContent(theValue);
            typeElement = string;
        }else if (value instanceof Boolean){
            Boolean theBoolean = (Boolean) value;
            Element aBoolean = document.createElement("boolean");
            aBoolean.setTextContent(BooleanUtil.isTrue(theBoolean)?"1":"0");
        }else if (value instanceof Collection){//如果是集合的话
            //这里只考虑String集合
            Collection<String> list = (Collection<String>) value;
            Element arrayElement = document.createElement("array");
            Element dataElement = document.createElement("data");
            arrayElement.appendChild(dataElement);
            for (String category : list) {
                Element categoryElement = document.createElement("value");
                Element stringElement = document.createElement("string");
                stringElement.setTextContent(category);
                categoryElement.appendChild(stringElement);
                dataElement.appendChild(categoryElement);
            }
            typeElement = arrayElement;
        }else if (value instanceof Base64){
            Base64 bits = (Base64) value;
            Element base64Element = document.createElement("base64");
            base64Element.setTextContent(bits.getBits());
            typeElement = base64Element;
        }
        valueElement.appendChild(typeElement);
        memberElement.appendChild(nameElement);
        memberElement.appendChild(valueElement);
        structElement.appendChild(memberElement);

    }

    public static Document generateBlogXml(Blog blog){
        Document methodCall = createDocument("methodCall", MethodEnum.BLOG_UPLOAD.getMethodName());
        String username = blog.getUsername();
        String secret = blog.getToken();
        addParam(methodCall,username);
        addParam(methodCall,username);
        addParam(methodCall,secret);

        addParam(methodCall,struct);
        addMember(methodCall,"description",blog.getContent());
        addMember(methodCall,"title",blog.getTitle());
        addMember(methodCall,"categories",blog.getCategories());

        addParam(methodCall,blog.getPublish());
        return methodCall;
    }

    public static Document generateUpdateBlogXml(Blog blog){
        Document methodCall = createDocument("methodCall", MethodEnum.BLOG_EDIT_POST.getMethodName());
        String username = blog.getUsername();
        String secret = blog.getToken();
        addParam(methodCall,blog.getBlogId());
        addParam(methodCall,username);
        addParam(methodCall,secret);

        addParam(methodCall,struct);
        addMember(methodCall,"description",blog.getContent());
        addMember(methodCall,"title",blog.getTitle());
        addMember(methodCall,"categories",blog.getCategories());

        addParam(methodCall,blog.getPublish());
        return methodCall;
    }

    public static Document generateMediaXml(Media media){

        Document methodCall = createDocument("methodCall", MethodEnum.MEDIA_POST.getMethodName());
        String username = media.getUsername();
        String secret = media.getToken();
        String fileName = media.getFileName();
        String mimeType = media.getMimeType();
        String bits = media.getBits();

        addParam(methodCall,username);
        addParam(methodCall,username);
        addParam(methodCall,secret);

        addParam(methodCall,struct);
        addMember(methodCall,"bits",new Base64(bits));
        addMember(methodCall,"name",fileName);
        addMember(methodCall,"type",mimeType);
        return methodCall;
    }

    public static Document generateGetBlogXml(Blog blog){
        Document methodCall = createDocument("methodCall", MethodEnum.GET_POST.getMethodName());
        String username = blog.getUsername();
        String token = blog.getToken();
        addParam(methodCall,blog.getBlogId());
        addParam(methodCall,username);
        addParam(methodCall,token);
        return methodCall;
    }

    public static String transferToXmlStr(Document document){

        String str = XmlUtil.toStr(document, true);
        //log.debug("\nrequest:\n{}",str);
        return str;
    }


    /**
     * 解析XML RES
     *
     * @param xml XML
     * @return {@link XmlRes}
     */
    public static XmlRes parseXmlRes(String xml,String xPath){
        XmlRes xmlRes = new XmlRes();
        xmlRes.setCode(1);
        xmlRes.setSuccess(true);
        xmlRes.setMsg("success");

        Document document = XmlUtil.readXML(xml);
        //String str = XmlUtil.toStr(document);
        //log.info("\nres:\n" + "{}",str);


        //获取faultCode
        Object faultCode = XmlUtil.getByXPath("/methodResponse/fault/value/struct/member[name='faultCode']/value/int/text()",document,XPathConstants.STRING);
        String faultCodeStr = String.valueOf(faultCode);
        if (StringUtils.isNotBlank(faultCodeStr)){//说明报错了

            xmlRes.setCode(Integer.parseInt(faultCodeStr));
            Object faultString = XmlUtil.getByXPath("/methodResponse/fault/value/struct/member[name='faultString']/value/string/text()",document,XPathConstants.STRING);
            xmlRes.setMsg(String.valueOf(faultString));
            xmlRes.setSuccess(false);

        }else {//说明没有问题
            String byXPath = String.valueOf(XmlUtil.getByXPath(xPath, document, XPathConstants.STRING)).trim();
            xmlRes.setData(byXPath);
        }

        return xmlRes;
    }

    /**
     * 获取markdown上传名称
     *
     * <?xml version="1.0" encoding="utf-8"?>
     * <methodResponse>
     *     <params>
     *         <param>
     *             <value>
     *                 <string>17862417</string>
     *             </value>
     *         </param>
     *     </params>
     * </methodResponse>
     *
     * @return {@link String}
     */
    public static XmlRes getBlogUploadedResXml(String responseXml){
        Document document = XmlUtil.readXML(responseXml);
        String str = XmlUtil.toStr(document);
        XmlRes xmlRes = parseXmlRes(str, "/methodResponse/params[param/value/string/text()]");
        //log.info("xmlRes:{}", xmlRes);
        if (xmlRes.getSuccess()) {
            log.info("upload success,the postId is : {}",xmlRes.getData());
        }else {
            log.error("upload failed,the faultCode is : {},the reason maybe : {}",xmlRes.getCode(),xmlRes.getData());
        }
        return xmlRes;
    }

    /**
     * <?xml version="1.0" encoding="utf-8"?>
     * <methodResponse>
     *     <params>
     *         <param>
     *             <value>
     *                 <boolean>1</boolean>
     *             </value>
     *         </param>
     *     </params>
     * </methodResponse>
     * @param responseXml
     * @return
     */
    public static XmlRes getBlogUpdateResXml(String responseXml){
        Document document = XmlUtil.readXML(responseXml);
        String str = XmlUtil.toStr(document);
        XmlRes xmlRes = parseXmlRes(str, "/methodResponse/params[param/value/boolean/text()]");
        log.info("xmlRes:{}",xmlRes);
        return xmlRes;
    }

    /**
     * 判断博文是否已经存在
     * @param responseXml
     * @return
     */
    public static boolean existenceDetermination(String responseXml){
        Document document = XmlUtil.readXML(responseXml);
        String str = XmlUtil.toStr(document);
        XmlRes xmlRes = parseXmlRes(str, "/methodResponse/params/param/value/struct/member[name='postid']/value/i4/text()");
        return StringUtils.isNotBlank(xmlRes.getData());
    }

    /**
     * 获取上传图片URL
     *
     * @param responseXml 响应XML
     * @return {@link String}
     */
    public static String getUploadImageUrl(String responseXml){
        Document document = XmlUtil.readXML(responseXml);
        String str = XmlUtil.toStr(document);
        XmlRes xmlRes = parseXmlRes(str, "/methodResponse/params/param/value/struct/member[name='url']/value/string/text()");
        return xmlRes.getData();
    }


    public static void main(String[] args) {
        String res = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<methodResponse>\n" +
                "    <fault>\n" +
                "        <value>\n" +
                "            <struct>\n" +
                "                <member>\n" +
                "                    <name>faultCode</name>\n" +
                "                    <value>\n" +
                "                        <int>3</int>\n" +
                "                    </value>\n" +
                "                </member>\n" +
                "                <member>\n" +
                "                    <name>faultString</name>\n" +
                "                    <value>\n" +
                "                        <string>参数有误, 缺失部分参数</string>\n" +
                "                    </value>\n" +
                "                </member>\n" +
                "            </struct>\n" +
                "        </value>\n" +
                "    </fault>\n" +
                "</methodResponse>";
        String res2 = "<methodResponse>\n" +
                "         <params>\n" +
                "             <param>\n" +
                "                 <value>\n" +
                "                     <string>17862417</string>\n" +
                "                 </value>\n" +
                "             </param>\n" +
                "         </params>\n" +
                "     </methodResponse>";

        String res3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<methodResponse>\n" +
                "    <params>\n" +
                "        <param>\n" +
                "            <value>\n" +
                "                <struct>\n" +
                "                    <member>\n" +
                "                        <name>url</name>\n" +
                "                        <value>\n" +
                "                            <string>https://img2023.cnblogs.com/blog/2320867/202311/2320867-20231129114357006-561552390.jpg</string>\n" +
                "                        </value>\n" +
                "                    </member>\n" +
                "                </struct>\n" +
                "            </value>\n" +
                "        </param>\n" +
                "    </params>\n" +
                "</methodResponse>";

        existenceDetermination(res3);
    }




}

    