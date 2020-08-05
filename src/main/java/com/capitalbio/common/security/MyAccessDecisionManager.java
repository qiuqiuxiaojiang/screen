package com.capitalbio.common.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
/**
 * MyAccessDecisionManager类，负责权限的控制，如果请求的url在权限集合中有这个url对应的值，则放行。注：如果数据库中没有对这个url定义访问的权限，默认是会被放行的
 * @author xiaoyanzhang
 *
 */
public class MyAccessDecisionManager implements AccessDecisionManager{
	//检查用户是否够权限访问资源  
    //参数authentication是从spring的全局缓存SecurityContextHolder中拿到的，里面是用户的权限信息  
    //参数object是url  
    //参数configAttributes所需的权限  
	
	 //这个方法在url请求时才会调用，服务器启动时不会执行这个方法，前提是需要在<http>标签内设置  <custom-filter>标签
    /*
     * 参数说明：
     * 1、configAttributes 装载了请求的url允许的角色数组 。这里是从ResourceServiceImpl里的init方法里的list对象取出的角色数据赋予给了configAttributes对象
     * 2、authentication 装载了从数据库读出来的角色 数据。这里是从MyUserDetailsService里的loadUserByUsername方法里的authList对象的值传过来给 authentication 对象
     *  
     * */ 
    public void decide(Authentication authentication, Object object,      
            Collection<ConfigAttribute> configAttributes)   
                    throws AccessDeniedException, InsufficientAuthenticationException { 
    	 /*
         * authentication装载了用户的信息数据，其中有角色。是MyUserDetailsService里的loadUserByUsername方法的userdetail对象传过来的
         * userdetail一共有7个参数（下面打印出来的数据可对应一下security的User类，这个类可以看到有寻7个参数），
         * 最后一个是用来保存角色数据的，如果角色为空，将无权访问页面。
         * 看到下面的打印数据，  Granted Authorities: ROLE_ADMIN，ROLE_ADMIN 就是角色了。
         * 如果显示Not granted any authorities，则说明userdetail的最后一个参数为空，没有传送角色的值过来
         */
    	
        if(configAttributes == null){   
            return;         
        }    
        Iterator<ConfigAttribute> ite=configAttributes.iterator();  
        while(ite.hasNext()){  
            ConfigAttribute ca=ite.next();    
            String needRole=((SecurityConfig)ca).getAttribute();  
            for(GrantedAuthority ga : authentication.getAuthorities()){   
            	//判断两个请求的url页面的权限和用户的权限是否相同，如相同，允许访问
                if(needRole.equals(ga.getAuthority())){    
                    return;                
		        }              
		    }        
		}   
        //注意：执行这里，后台是会抛异常的，但是界面会跳转到所配的access-denied-page页面  
        throw new AccessDeniedException("no right");     
    }
    
    
    /*
     * * 这个 supports(ConfigAttribute attribute) 方法在启动的时候被  
     * AbstractSecurityInterceptor调用，来决定AccessDecisionManager  
     * 是否可以执行传递ConfigAttribute。 supports(Class)方法被安全拦截器实现调用，  
     * 包含安全拦截器将显示的AccessDecisionManager支持安全对象的类型。   
     * 
     * */
    public boolean supports(ConfigAttribute attribute) {   
        return true;  
    }    
    public boolean supports(Class<?>clazz) {  
        return true;   
    }
}
