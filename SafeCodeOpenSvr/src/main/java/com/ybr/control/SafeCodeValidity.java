package main.java.com.ybr.control;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ybr.Constants;
import com.ybr.entity.request.ReqS1001;
import com.ybr.entity.request.ReqS2002;
import com.ybr.entity.response.RspBase;

import main.java.com.ybr.util.MsgUtil;


@Controller
@CrossOrigin(origins = {"http://127.0.0.1：8090"})
public class SafeCodeValidity {
	@RequestMapping(value = "/scvalidity", method = RequestMethod.GET)
	@ResponseBody
	String home(String compKey, String userKey, String safeCode,HttpServletRequest request,HttpServletResponse response) {
		System.out.println("进入方法验证");
		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0); 
    	String jsonp = request.getParameter("callback");
		ReqS1001 req = new ReqS1001();
		req.compKey = compKey;
		req.userKey = userKey;
		req.safeCode = safeCode;
		RspBase rsp = MsgUtil.sendMsg(req);
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			for (Field field : rsp.getClass().getFields())
				if (!("className".equals(field.getName()) || "reqID".equals(field.getName())))
					map.put(field.getName(), field.get(rsp).toString());
		} catch (Exception e) {
			map.clear();
			map.put("rspCode", Constants.RSP_CODE_ERROR);
			map.put("rspMsg", "unknow exception");
		}
		return jsonp+"("+new Gson().toJson(map)+")";
	}
	
	@RequestMapping("/file")
	String file(String filename) {
		return filename;
	}
	
	@RequestMapping("/*")
	@ResponseBody
	String error() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("rspCode", Constants.RSP_CODE_ERROR);
		map.put("rspMsg", "wrong request");
		System.out.println("request");
		return new Gson().toJson(map);
	}
	
	@RequestMapping(value="/bind",method=RequestMethod.GET)
	@ResponseBody
	public String bind(String compKey,String userKey,String safeCode,String username,String password,HttpServletRequest request,HttpServletResponse response){
		System.out.println("compKey为"+compKey+"  "+"userkey为: "+userKey+" "+"safecode为:"+"  "+safeCode);
		HashMap<String, String> map = new HashMap<String, String>();
		response.setContentType("text/plain");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
		ReqS2002 reqS2002=new ReqS2002();
		reqS2002.compKey = compKey;
		reqS2002.userKey = userKey;
		reqS2002.safeCode = safeCode;
		RspBase rsp = MsgUtil.sendMsg(reqS2002);
		String jsonp = request.getParameter("callback");
		if(rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
			System.out.println("注册验证成功");
			map.put("rsp","success");
			String g = new Gson().toJson(map);
			System.out.println(g.toString());
			
			return jsonp+"("+g+")";
		}else{
			map.put("rsp","fail");
			String g = new Gson().toJson(map);
			System.out.println("注册验证失败"+rsp.rspCode+"提示信息"+rsp.rspMsg);
			return jsonp+"("+g+")";
		}
	}
	
}
	

class Bean {
	public String a = "123";
	public String b = "12345";
	public String c = "asd";
	public String d = "12";
	public Date e = new Date();
}