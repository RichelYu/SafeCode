package com.ybr.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
public @interface SvrItfc {
	
	/**
	 * 命令字
	 * @return
	 */
	String SvrCode();
	
	/**
	 * 不进行登录检查
	 * @return
	 */
	boolean noLoginCmd() default false;
}
