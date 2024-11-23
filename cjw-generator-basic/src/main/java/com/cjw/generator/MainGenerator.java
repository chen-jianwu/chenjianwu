package com.cjw.generator;

import com.cjw.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {

        //获取整个项目的根目录
        String projectPath = System.getProperty("user.dir");

        File parentFile = new File(projectPath);
        //输入路径 : ACM示例代码模版目录
        String inputPath = parentFile + File.separator + "cjw-generator-demo-projects" + File.separator + "acm-template";

        //输出路径: 直接输出到项目的根目录
        String outputPath = projectPath;

        StaticGenerator.copyFilesByRecursive(inputPath, outputPath);


        String dynamicInputPath = projectPath + File.separator + "cjw-generator-basic\\src\\main\\resources\\templates\\MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template\\src\\com\\cjw\\acm\\MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("老陈");
        mainTemplateConfig.setOutputText("我要输出啦");
        mainTemplateConfig.setLoop(false);
        DynamicGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, mainTemplateConfig);

    }
}
