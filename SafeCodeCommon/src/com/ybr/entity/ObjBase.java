package com.ybr.entity;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.ybr.Constants;
import com.ybr.util.CommonUtil;
import com.ybr.util.LogUtil;

public class ObjBase implements Serializable {

	public ObjBase() {
		this.className = this.getClass().getName();
	}

	public String className;

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (this.getClass().equals(obj.getClass())) {
			boolean temp = true;
			/**
			 * field:Java反射中Field类描述的是类的属性信息,功能包括：
			 * 获取当前对象的成员变量类型
			 * 对成员变量重新设值
			 * Class.getFields():获取类中public类型的属性,返回一个包含某些Field对象的数组,该数组包含此Class对象所表示的类或接口的所有可访问公共字段
			 * 
			 */
			for (Field field : this.getClass().getFields()) {
				try {
					if (field.get(this) == null)
						temp = field.get(obj) == null;
					else
						temp = field.get(this).equals(field.get(obj));
				} catch (IllegalArgumentException e) {
					LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
					e.printStackTrace();
				}
				if (!temp)
					return false;
			}
			return temp;
		}
		return false;
	}

	@Override
	public String toString() {
		this.className = this.getClass().getName();
		StringBuffer temp = new StringBuffer();
		try {
			temp.append(Constants.CHAR_BEANBASE_BRACKET_LEFT);//左括号
			Field[] fs = this.getClass().getFields();
			for (Field field : fs) {//处理android中的$change和serialVersionUID字符串
				if (field.getName().equals("$change") || field.getName().equals("serialVersionUID"))
					continue;
				temp.append(Constants.CHAR_MSG_SEPARATE);//#
				temp.append(field.getName());
				temp.append(Constants.CHAR_MSG_EQUAL);//=
				temp.append(CommonUtil.objectToString(field.get(this)));
			}
			temp.append(Constants.CHAR_BEANBASE_BRACKET_RIGHT);//右括号
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp.toString();
	}
}
