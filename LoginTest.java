package com.albert.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 流程：
 * 
 * 访问登录页面（不登录）-->登录（post表单数据）-->
 * 
 * */

public class LoginTest {

	//全局请求设置
	private RequestConfig globalConfig;
	
	//创建cookie store 的本地实例
	private CookieStore cookieStore;
	
	//创建一个HttpClient
	private CloseableHttpClient httpClient;
	
	//创建HttpClient上下文
	private HttpClientContext context;
	
	/*//服务器发来的响应包
	private CloseableHttpResponse responsePaket;*/
	
	/*
	 * 构造方法
	 * */
	public LoginTest() {
		//全局请求设置
		globalConfig = RequestConfig.custom()
									.setCookieSpec(CookieSpecs.STANDARD)
									.build();
				
		//创建cookie store 的本地实例
		 cookieStore = new BasicCookieStore();
		
		//创建一个HttpClient
		 httpClient = HttpClients.custom()
								.setDefaultRequestConfig(globalConfig)
								.setDefaultCookieStore(cookieStore)
								.build();
		 
		//创建HttpClient上下文
		 context = HttpClientContext.create();
	}
	
	/*
	 * 打印当前的cookie
	 * */
	private void printlnCookie() {
		//访问登录首页获取常用的cookie
		for (Cookie c : cookieStore.getCookies()) {
			System.out.println(c.getName() + " ： " + c.getValue());
		}	
	}
	
	/**
	 * 打印页面的html
	 */
	public void printlnWeb(CloseableHttpResponse responsePaket) {
		//打印页面
		String web = null;
		try {
			web = EntityUtils.toString(responsePaket.getEntity(), "UTF-8");
			System.out.println("当前页面 ： " + "\r\n" + web);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 首次访问页面
	 * 不登录
	 * 使用get方式请求
	 * 以获取cookie
	 * 并打印出获取的该cookie
	 * */
	private void accessingPage(URI URIofPage) {


		try {		
			CloseableHttpResponse responsePacket = getRequest(URIofPage);
		
			//打印当前页面
			printlnWeb(responsePacket);
			
			//关闭连接
			responsePacket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/*
	 * 重载accessingPage方法,参数为URI字符串
	 * */
	private void accessingPage(String URIStringofPage) {
		try {
			CloseableHttpResponse responsePacket =  getRequest(new URI(URIStringofPage));
			
			//打印当前页面
			//printlnWeb(responsePacket);
			
			//关闭连接
			responsePacket.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 访问页面
	 * 不登录
	 * 使用get方式请求
	 * 以获取cookie
	 * 并打印出获取的该cookie
	 * 返回页面HTML字符串
	 * str="NEEDED"
	 * */
	private String htmlOfAccessingPage(String URIStringofPage) {

		try {		
			CloseableHttpResponse responsePacket = getRequest(new URI(URIStringofPage));
			
			String web = EntityUtils.toString(responsePacket.getEntity(), "UTF-8");
			
			//String htmlString = responsePacket.getEntity().toString();
			
			//关闭连接
			responsePacket.close();
			
			return web;

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/*
	 * 用表单数据登录网站
	 * 将表单数据post到网站中
	 * 需要处理302重定向问题
	 * 向重定向的URI中重新发送post请求
	 * 然后用get方式获取登录成功后的页面html
	 * */
	private void loginWebsite(URI URIofLoginPage, UrlEncodedFormEntity dataEntity) {
		
		URI newURI = null;
		
		CloseableHttpResponse responsePaket = postRequest(URIofLoginPage, dataEntity);	

		try {		
			
			//判断302重定向
			if (responsePaket.getStatusLine().getStatusCode() == 302) {
				System.out.println("/********需重定向********/");
				//获取重定向的新链接
				newURI = new URI(responsePaket.getFirstHeader("location").getValue());
				responsePaket.close();
				
				//向新链接中重新发送post请求
				postRequest(newURI, dataEntity).close();		
			}	
			
			//重发get请求，获取页面html
			CloseableHttpResponse responsePacket = getRequest(newURI);
			
			//打印当前页面，即登录成功后的页面
			printlnWeb(responsePacket);
			
			//关闭连接
			responsePacket.close();	
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			return;
		}
		
	}
	
	/*
	 * 重载loginWebsite方法，参数为URI字符串
	 * */
	private void loginWebsite(String URIStringofLoginPage, UrlEncodedFormEntity dataEntity) {
		
		URI newURI = null;
		
		CloseableHttpResponse responsePaket = null;
		try {
			responsePaket = postRequest(new URI(URIStringofLoginPage), dataEntity);
			
			//判断302重定向
			if (responsePaket.getStatusLine().getStatusCode() == 302) {
				System.out.println("/********需重定向********/");
				//获取重定向的新链接
				newURI = new URI(responsePaket.getFirstHeader("location").getValue());
				
				responsePaket.close();
				
				//向新链接中重新发送post请求
				//postRequest(newURI, dataEntity).close();		
			}		
			
			//重发get请求，获取页面html
			CloseableHttpResponse responsePacket =  getRequest(newURI);
			
			//打印当前页面，即登录成功后的页面
			//printlnWeb(responsePacket);
			
			//关闭连接
			responsePacket.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			return;
		}
	
	}
	
	/*
	 * 一个标准的get请求
	 * */
	private CloseableHttpResponse getRequest(URI targetURI) {
		//创建一个get请求
		HttpGet get = new HttpGet(targetURI);

		try {		
			//然后将get请求发送到服务器
			CloseableHttpResponse responsePaket = this.httpClient.execute(get, context);
			
			//打印当前的cookie
			printlnCookie();
			
			//返回响应包
			return responsePaket;
			
			/**** 在外部关闭 HttpResponse ****/		
		
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}	
	}
	
	/*
	 *  (不装载表单数据)
	 * 一个标准的post请求
	 * 该post请求不装载表单数据
	 * */
	private CloseableHttpResponse postRequest(URI targetURI) {
		//创建一个post请求
		HttpPost post = new HttpPost(targetURI);
		
		try {		
			//然后将post请求发送到服务器
			CloseableHttpResponse responsePaket = this.httpClient.execute(post, context);
			
			//打印当前的cookie
			printlnCookie();
			
			//返回响应包
			return responsePaket;
			/**** 在外部关闭 HttpResponse ****/
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * (装载表单数据)
	 * 一个标准的post请求
	 * 该post请求装载表单数据
	 * */
	private CloseableHttpResponse postRequest(URI targetURI, UrlEncodedFormEntity dataEntity) {
		//创建一个post请求
		HttpPost post = new HttpPost(targetURI);
		
		//装载表单数据
		post.setEntity(dataEntity);
		
		try {		
			//然后将post请求发送到服务器
			CloseableHttpResponse responsePaket = this.httpClient.execute(post, context);
			
			//打印当前的cookie
			printlnCookie();
			
			//返回响应包
			return responsePaket;
			
			/**** 在外部关闭 HttpResponse ****/
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * 根据用户信息构建post的表单数据
	 * 
	 * */
	private UrlEncodedFormEntity buildFormdata(String username, String password) {
		List<NameValuePair> pairs = new LinkedList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", username));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("code", "code"));
		pairs.add(new BasicNameValuePair("lt", "LT-NeusoftAlwaysValidTicket"));
		pairs.add(new BasicNameValuePair("execution", "e1s1"));
		pairs.add(new BasicNameValuePair("_eventId", "submit"));
		
		//把表单数据按UTF-8格式进行编码
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Consts.UTF_8);
		entity.setContentType("application/x-www-form-urlencoded");
		
		return entity;
	}

	public static void main(String[] args) {
		LoginTest myLogin = new LoginTest();
		//访问登录页面，不登录
		myLogin.accessingPage(LoginConst.loginURLString);		
		
		//创建用户表单数据
		UrlEncodedFormEntity userForm = myLogin.buildFormdata(LoginConst.username, LoginConst.password);
		
		//登录，post表单数据
		myLogin.loginWebsite(LoginConst.loginURLString, userForm);
	
		//查成绩
		//myLogin.accessingPage(LoginConst.gradeURLString);
		String htmlString = myLogin.htmlOfAccessingPage(LoginConst.gradeURLString);

		//课程成绩map
		Map<Integer, List<String>> gradeMap = pageParsing.parseGrade(htmlString);
		
		for (int i = 0; i < gradeMap.size(); i++) {
			System.out.println(gradeMap.get(i));
		}
		
	}

}





























