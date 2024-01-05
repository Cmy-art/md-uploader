## 工具介绍

### 工具由来

对于程序员等常常需要写文档的人来说,将本地markdown文档同步到云端博客平台,是一件比较繁琐的事情,首当其冲的是,大量的本地图片需要"互联网"化,即使网络上不乏有些工具能做到将图片自动上传到某些图床来解决这个问题,但是还是需要自己手动复制文档到对应的博客平台,无法一步到位,总是有些"不美"

### 功能简述

本工具的目的就是希望能做到**一键**上传所有的**图片**以及包含转换后图片的**markdown本身**到**博客园**

### 生态组合

本工具基于**Java**,针对**windows**平台(笔者所用为 windows 10),**markdown**编写工具为"所见即所得"的**Typora**,博客云端平台为**博客园**

## 工具获取

github:https://github.com/Cmy-art/md-uploader

release:https://github.com/Cmy-art/md-uploader/releases/tag/V1.0.0

可下载下图的release包

![image-20240105155446561.png](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105162450058-1209674483.png)

## 配置说明

### Typora配置

依次点击**文件-偏好设置-图像**-选择如下图的**复制图片到./${filename}.asserts文件夹**

目的是插入图片时,会自动在同级目录下生成文档名称.assets的文件夹来统一管理本地图片

![image-20240105103621871](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140656683-2107034637.png)

### 配置项说明

首先将release包**固定存放**到某个盘符路径,**视为工作目录**,**工作目录修改需要重新执行步骤右键集成**

打开release包,修改配置文件**conf.txt**

![image-20240105105736484](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140657173-1812124341.png)

文件中有四个配置项

- url => MetaWeblog访问地址
- username => MetaWeblog登录名
- token => MetaWeblog访问令牌

前三个在博客园后台的,**设置-博客设置-设置-其他设置**中获取,如下图

![image-20240105110426641](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140657491-1467486532.png)

- publish

  表示是否发布,0表示不发布,1表示直接发布,默认是0,按需修改

配置好后保存即可

### 右键集成

**一键上传**的实现方式是将脚本命令通过修改注册表集成到windows右键菜单

以**管理员身份**运行release包中的**init.ba**t命令,**注意**!该批处理命令中有**修改注册表**的操作,**建议创建系统还原点或者备份注册表**之后再操作(一般没啥问题)

![image-20240105104534178](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140657830-827974731.png)

执行成功后,右键点击markdown文件,菜单中出现Upload Markdown文件

![image-20240105105207843](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140658158-419119995.png)

## 使用示例

![log](https://img2023.cnblogs.com/blog/2320867/202401/2320867-20240105140658963-156889211.gif)

## 注意事项

- 该工具基于Java,如果您的电脑存在Java环境(配置了JAVA_HOME)就会默认使用您自己的Jre,否则会直接使用release吧中的jre

- 文档移动位置

  请**务必**将**文档和其.asserts文件一起移动**

- 文件名修改

  文档名称请慎重决定(最好确定了就不在修改文档名称了)

  如果修改了可能导致

  **会导致生成多个.assert文件**

  **图片会重复上传**

- 文档内容可以正常变更

- 大量图片的上传

  由于速度受限于接口的响应速度,所以如果文档中有大量图片可能需要**一定时间**才能上送完成

- 关于文档的分类和tag等**更细粒度**的配置**请到博客园管理页面配置**

- 图片暂时不支持缩放

- 修改文档时,优先使用博客园定义的名称,如果博客园定义的名称为空则使用本地的文档名称

- 接口变更,如果博客园的接口变更可能导致上传失败
