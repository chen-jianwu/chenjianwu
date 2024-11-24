# cjw - 代码生成器共享平台

> 作者：cjw


基于 React + Spring Boot + Picocli + 对象存储的 **代码生成器共享平台** 。

开发者可以在平台上制作并发布代码生成器，用户可以搜索、下载、在线使用代码生成器，管理员可以集中管理所有用户和生成器。



## 项目简介

该项目基于命令行的本地代码生成器、代码生成器制作工具、在线代码生成器平台。



## 初始化根目录
![img.png](img.png)

### 准备ACM示例代码

只是一个简单的JAVA项目,没有使用Maven 和 第三方依赖
结构如下,核心组成是静态文件 README.md 和 代码文件 MainTemplate:

![img_2.png](img_2.png)

README.md 内容如下:

![img_3.png](img_3.png)

MainTemplate.java 是一段ACM示例输入代码，作用是输出多数之和。
内容如下:

```
package com.cjw.acm

import java.util.Scanner
/**
 * ACM 输入模板(多数之和)
 */

public class MainTemplate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        while (scanner.hasNextLine()) {
            //读取输入元素的个数
            int n = scanner.nextInt();

            //读取数组
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = scanner.nextInt();
            }

            //处理问题逻辑
            //计算数组元素之和
            int sum = 0;
            for (int num : arr) {
                sum += num;
            }
            System.out.println(sum);
        }
        scanner.close();
    }
}
```

第一阶段中,我们将改造这个方法Java代码文件，让它支持多种输入方式

### 在根目录下（chenjianwu/chenjianwu-generator） 新建cjw-generator-basic 项目

使用Maven管理项目
JDK选择1.8

在cjw-generator-basic下的Pom.xml下 添加依赖

```angular2html
<dependencies>
    <!-- https://doc.hutool.cn/ -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.16</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-collections4 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
        <version>4.4</version>
    </dependency>
    <!-- https://projectlombok.org/ -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

执行Main

![img_4.png](img_4.png)

成功输出

## 静态文件生成

我们现在已经引入了 Hutool 库的依赖

Hutool是一个功能非常齐全的工具集,报上了HTTP请求、日期时间处理、集合类处理、文件类处理、JSON处理等能够大幅度提高开发效率的工具类

Htool官方文档  https://www.hutool.cn/

在 com.cjw.generator 下新建一个StaticGenerator类

### 现成工具库复制目录

```
* 拷贝文件（Hutool 实现，会将输入目录完整拷贝到输出目录下）
* @param inputPath
* @param outputPath
  */
  public static void copyFilesByHutool(String inputPath, String outputPath) {
  FileUtil.copy(inputPath, outputPath, false);
  }
  ```

然后编写Main方法调用即可

```
public static void main(String[] args) {
//获取整个项目的根目录
String projectPath = System.getProperty("user.dir");

        File parentFile = new File(projectPath);
        //输入路径 : ACM示例代码模版目录
        String inputPath = parentFile + File.separator + "cjw-generator-demo-projects" + File.separator + "acm-template";

        //输出路径: 直接输出到项目的根目录
        String outputPath = projectPath;

        copyFilesByHutool(inputPath, outputPath);

}
```

执行后就复制了整个目录:

![img_5.png](img_5.png)

## 递归遍历
代码如下:
```
/**
* 递归拷贝文件（递归实现，会将输入目录完整拷贝到输出目录下）
* @param inputPath
* @param outputPath
*/
public static void copyFilesByRecursive(String inputPath, String outputPath) {
    File inputFile = new File(inputPath);
    File outputFile = new File(outputPath);
    try {
        copyFileByRecursive(inputFile, outputFile);
       } catch (Exception e) {
        System.err.println("文件复制失败");
        e.printStackTrace();
        }
}

    /**
     * 文件 A => 目录 B，则文件 A 放在目录 B 下
     * 文件 A => 文件 B，则文件 A 覆盖文件 B
     * 目录 A => 目录 B，则目录 A 放在目录 B 下
     *
     * 核心思路：先创建目录，然后遍历目录内的文件，依次复制
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    private static void copyFileByRecursive(File inputFile, File outputFile) throws IOException {
        // 区分是文件还是目录
        if (inputFile.isDirectory()) {
            System.out.println(inputFile.getName());
            File destOutputFile = new File(outputFile, inputFile.getName());
            // 如果是目录，首先创建目标目录
            if (!destOutputFile.exists()) {
                destOutputFile.mkdirs();
            }
            // 获取目录下的所有文件和子目录
            File[] files = inputFile.listFiles();
            // 无子文件，直接结束
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                // 递归拷贝下一层文件
                copyFileByRecursive(file, destOutputFile);
            }
        } else {
            // 是文件，直接复制到目标目录下
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
 ```

同样执行Main方法

```
public static void main(String[] args) {
//获取整个项目的根目录
String projectPath = System.getProperty("user.dir");

        File parentFile = new File(projectPath);
        //输入路径 : ACM示例代码模版目录
        String inputPath = parentFile + File.separator + "cjw-generator-demo-projects" + File.separator + "acm-template";

        //输出路径: 直接输出到项目的根目录
        String outputPath = projectPath;

        copyFilesByRecursive(inputPath, outputPath);

}
```

复制文件目录成功

![img_6.png](img_6.png)


## 动态文件生成

实现了静态文件生成后(文件复制),我们接着生成动态文件

我们先增加一个作者注释、输出提示信息、循环或单次

示例代码如下:

```
/**
* ACM 输入模板（多数之和）
* @author cjw（1. 增加作者注释）
 */
  public class MainTemplate {
  public static void main(String[] args) {
  Scanner scanner = new Scanner(System.in);

  // 2. 可选是否循环
  //        while (scanner.hasNext()) {
  // 读取输入元素个数
  int n = scanner.nextInt();

           // 读取数组
           int[] arr = new int[n];
           for (int i = 0; i < n; i++) {
               arr[i] = scanner.nextInt();
           }

           // 处理问题逻辑，根据需要进行输出
           // 示例：计算数组元素的和
           int sum = 0;
           for (int num : arr) {
               sum += num;
           }

           // 3. 输出信息可以修改
           System.out.println("求和结果: " + sum);
//        }

        scanner.close();
    }
}
```

### 动态生成的核心原理

实现方法就是,"挖坑"  - > "填坑" 的思想

编写模版文件 -> 用户输入参数 -> 生成完整代码

举个例子,用户输入参数:
```
author = cjw
```
模版代码文件:
```
/**
 * ACM 输入模板（多数之和）
 * @author ${author}
 */
```
将参数注入到模板文件中,生成完整代码:
```
/**
 * ACM 输入模板（多数之和）
 * @author cjw
 */
```

引出问题

如何编写模版文件?  
程序如何知道哪些文件需要替换?  
通过正则表达式或字符串匹配来扫描文件?

这里使用 模板引擎技术  FreeMarker  
官方文档:https://freemarker.apache.org/docs/index.html

### 模版

这里我们举个模板文件例子:
```
<!DOCTYPE html>
<html>
  <head>
    <title>健武官网</title>
  </head>
  <body>
    <h1>欢迎来到健武官网</h1>
    <ul>
      <#-- 循环渲染导航条 -->
      <#list menuItems as item>
        <li><a href="${item.url}">${item.label}</a></li>
      </#list>
    </ul>
    <#-- 底部版权信息（注释部分，不会被输出）-->
      <footer>
        ${currentYear} 健武官网. All rights reserved.
      </footer>
  </body>
</html>
```
### 数据模型

```
{
  "currentYear": 2024,
  "menuItems": [
    {
      "url": "https://www.douyin.com",
      "label": "抖音",
    },
    {
      "url": "https://www.baidu.com",
      "label": "百度",
    }
  ]
}
```

### 我们通过编写Java代码对二者组合

首先我们需要在 cjw-generator-basic 的 pom.xml 下引入依赖

```angular2html
<!-- https://freemarker.apache.org/index.html -->
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.32</version>
</dependency>
```

如果是springboot项目 引入以下依赖
```angular2html
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```

我们在test/java 目录下新建一个 单元测试类  
完整代码如下:
```
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {

    @Test
    public void Test() throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        configuration.setNumberFormat("0.######");
        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate("myweb.html.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2024);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url", "https://www.baidu.com");
        menuItem1.put("label", "百度");
        Map<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url", "https://www.douyin.com/");
        menuItem2.put("label", "抖音");
        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        dataModel.put("menuItems", menuItems);

        Writer out = new FileWriter("myweb.html");
        template.process(dataModel, out);

        // 生成文件后别忘了关闭哦
        out.close();

    }
}
```

## 动态文件生成实现

核心步骤 :   
定义数据模型  ->  编写动态模板  ->  组合生成    ->  完善优化

### 定义数据模型
我们在com.cjw.model 下新建一个 模板对象  
代码如下:  
```
/**
 * 动态模版配置
 */
@Data
public class MainTemplateConfig {

    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者注释
     */
    private String author;

    /**
     * 输出信息
     */
    private String outputText;
}
```
### 编写动态模版
我们在resources/templates 目录下 新建FTL模板文件 MainTemlate.java.ftl
```
/**
 * ACM 输入模板（多数之和）
 * @author ${author}
 */
public class MainTemplate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

<#if loop>
        while (scanner.hasNext()) {
</#if>
            // 读取输入元素个数
            int n = scanner.nextInt();
            // 读取数组
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = scanner.nextInt();
            }
            // 处理问题逻辑，根据需要进行输出
            // 示例：计算数组元素的和
            int sum = 0;
            for (int num : arr) {
                sum += num;
            }
            System.out.println("${outputText}" + sum);
<#if loop>
        }
</#if>
        scanner.close();
    }
}
```


### 组合生成
同静态文件生成一样, 我们在 com.cjw.generator 下 新建DynamicGenerator类  
依次完成 : Configuration对象、模板对象、创建数据模型、指定输出路径、执行生成

完整代码如下:
```
/**
 * 动态文件生成
 */
public class DynamicGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate("MainTemplate.java.ftl");

        // 创建数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("cjw");
        // 不使用循环
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");

        // 生成
        Writer out = new FileWriter("MainTemplate.java");
        template.process(mainTemplateConfig, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }

}
```
### 完善优化

## 完善模版文件
如果不设置默认值,当用户没有传入参数时会报错
```
/**
 * 动态模版配置
 */
@Data
public class MainTemplateConfig {

    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者注释
     */
    private String author = "陈健武";

    /**
     * 输出信息
     */
    private String outputText  = "输出结果";
}
```



