package com.ybr.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented//说明该注解将被包含在javadoc中
@Retention(RetentionPolicy.RUNTIME)//表示注解的信息被保留在class文件(字节码文件)中当程序编译时，会被虚拟机保留在运行时
/**
 * 定义一个注解cmd
 * @author ybr
 *
 */
public @interface Cmd {
	/**
	 * cmd Length = 4
	 * @return
	 */
	String cmd();

}

