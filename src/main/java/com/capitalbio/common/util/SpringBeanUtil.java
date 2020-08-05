package com.capitalbio.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanUtil implements ApplicationContextAware {

	private static ApplicationContext context;  
	  
    public void setApplicationContext(ApplicationContext context) throws BeansException {  
    	SpringBeanUtil.context = context;  
    }  
  
     
    public static Object getSpringBean(String beanName) {  
        return context==null?null:context.getBean(beanName);  
    }  
  
    public static String[] getBeanDefinitionNames() {  
        return context.getBeanDefinitionNames();  
    }  

}
