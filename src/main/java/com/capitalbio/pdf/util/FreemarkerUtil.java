package com.capitalbio.pdf.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FreemarkerUtil {
	public static void convert(String templateName, String destFileName, Map<String,Object> map) throws Exception{
		Configuration conf = new Configuration();
		conf.setClassicCompatible(true);
		conf.setDefaultEncoding("UTF-8");
		conf.setClassForTemplateLoading(FreemarkerUtil.class, "/template");
		Template template = conf.getTemplate(templateName);
		template.setEncoding("UTF-8");  
		template.setObjectWrapper(new DefaultObjectWrapper());
//		FileWriter writer = new FileWriter(destFileName);
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), "UTF-8"));
		template.process(map, out);
		out.close();
	}

}
