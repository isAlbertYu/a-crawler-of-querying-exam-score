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
 * ���̣�
 * 
 * ���ʵ�¼ҳ�棨����¼��-->��¼��post�����ݣ�-->
 * 
 * */

public class LoginTest {

	//ȫ����������
	private RequestConfig globalConfig;
	
	//����cookie store �ı���ʵ��
	private CookieStore cookieStore;
	
	//����һ��HttpClient
	private CloseableHttpClient httpClient;
	
	//����HttpClient������
	private HttpClientContext context;
	
	/*//��������������Ӧ��
	private CloseableHttpResponse responsePaket;*/
	
	/*
	 * ���췽��
	 * */
	public LoginTest() {
		//ȫ����������
		globalConfig = RequestConfig.custom()
									.setCookieSpec(CookieSpecs.STANDARD)
									.build();
				
		//����cookie store �ı���ʵ��
		 cookieStore = new BasicCookieStore();
		
		//����һ��HttpClient
		 httpClient = HttpClients.custom()
								.setDefaultRequestConfig(globalConfig)
								.setDefaultCookieStore(cookieStore)
								.build();
		 
		//����HttpClient������
		 context = HttpClientContext.create();
	}
	
	/*
	 * ��ӡ��ǰ��cookie
	 * */
	private void printlnCookie() {
		//���ʵ�¼��ҳ��ȡ���õ�cookie
		for (Cookie c : cookieStore.getCookies()) {
			System.out.println(c.getName() + " �� " + c.getValue());
		}	
	}
	
	/**
	 * ��ӡҳ���html
	 */
	public void printlnWeb(CloseableHttpResponse responsePaket) {
		//��ӡҳ��
		String web = null;
		try {
			web = EntityUtils.toString(responsePaket.getEntity(), "UTF-8");
			System.out.println("��ǰҳ�� �� " + "\r\n" + web);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * �״η���ҳ��
	 * ����¼
	 * ʹ��get��ʽ����
	 * �Ի�ȡcookie
	 * ����ӡ����ȡ�ĸ�cookie
	 * */
	private void accessingPage(URI URIofPage) {


		try {		
			CloseableHttpResponse responsePacket = getRequest(URIofPage);
		
			//��ӡ��ǰҳ��
			printlnWeb(responsePacket);
			
			//�ر�����
			responsePacket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/*
	 * ����accessingPage����,����ΪURI�ַ���
	 * */
	private void accessingPage(String URIStringofPage) {
		try {
			CloseableHttpResponse responsePacket =  getRequest(new URI(URIStringofPage));
			
			//��ӡ��ǰҳ��
			//printlnWeb(responsePacket);
			
			//�ر�����
			responsePacket.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ����ҳ��
	 * ����¼
	 * ʹ��get��ʽ����
	 * �Ի�ȡcookie
	 * ����ӡ����ȡ�ĸ�cookie
	 * ����ҳ��HTML�ַ���
	 * str="NEEDED"
	 * */
	private String htmlOfAccessingPage(String URIStringofPage) {

		try {		
			CloseableHttpResponse responsePacket = getRequest(new URI(URIStringofPage));
			
			String web = EntityUtils.toString(responsePacket.getEntity(), "UTF-8");
			
			//String htmlString = responsePacket.getEntity().toString();
			
			//�ر�����
			responsePacket.close();
			
			return web;

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/*
	 * �ñ����ݵ�¼��վ
	 * ��������post����վ��
	 * ��Ҫ����302�ض�������
	 * ���ض����URI�����·���post����
	 * Ȼ����get��ʽ��ȡ��¼�ɹ����ҳ��html
	 * */
	private void loginWebsite(URI URIofLoginPage, UrlEncodedFormEntity dataEntity) {
		
		URI newURI = null;
		
		CloseableHttpResponse responsePaket = postRequest(URIofLoginPage, dataEntity);	

		try {		
			
			//�ж�302�ض���
			if (responsePaket.getStatusLine().getStatusCode() == 302) {
				System.out.println("/********���ض���********/");
				//��ȡ�ض����������
				newURI = new URI(responsePaket.getFirstHeader("location").getValue());
				responsePaket.close();
				
				//�������������·���post����
				postRequest(newURI, dataEntity).close();		
			}	
			
			//�ط�get���󣬻�ȡҳ��html
			CloseableHttpResponse responsePacket = getRequest(newURI);
			
			//��ӡ��ǰҳ�棬����¼�ɹ����ҳ��
			printlnWeb(responsePacket);
			
			//�ر�����
			responsePacket.close();	
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			return;
		}
		
	}
	
	/*
	 * ����loginWebsite����������ΪURI�ַ���
	 * */
	private void loginWebsite(String URIStringofLoginPage, UrlEncodedFormEntity dataEntity) {
		
		URI newURI = null;
		
		CloseableHttpResponse responsePaket = null;
		try {
			responsePaket = postRequest(new URI(URIStringofLoginPage), dataEntity);
			
			//�ж�302�ض���
			if (responsePaket.getStatusLine().getStatusCode() == 302) {
				System.out.println("/********���ض���********/");
				//��ȡ�ض����������
				newURI = new URI(responsePaket.getFirstHeader("location").getValue());
				
				responsePaket.close();
				
				//�������������·���post����
				//postRequest(newURI, dataEntity).close();		
			}		
			
			//�ط�get���󣬻�ȡҳ��html
			CloseableHttpResponse responsePacket =  getRequest(newURI);
			
			//��ӡ��ǰҳ�棬����¼�ɹ����ҳ��
			//printlnWeb(responsePacket);
			
			//�ر�����
			responsePacket.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			return;
		}
	
	}
	
	/*
	 * һ����׼��get����
	 * */
	private CloseableHttpResponse getRequest(URI targetURI) {
		//����һ��get����
		HttpGet get = new HttpGet(targetURI);

		try {		
			//Ȼ��get�����͵�������
			CloseableHttpResponse responsePaket = this.httpClient.execute(get, context);
			
			//��ӡ��ǰ��cookie
			printlnCookie();
			
			//������Ӧ��
			return responsePaket;
			
			/**** ���ⲿ�ر� HttpResponse ****/		
		
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}	
	}
	
	/*
	 *  (��װ�ر�����)
	 * һ����׼��post����
	 * ��post����װ�ر�����
	 * */
	private CloseableHttpResponse postRequest(URI targetURI) {
		//����һ��post����
		HttpPost post = new HttpPost(targetURI);
		
		try {		
			//Ȼ��post�����͵�������
			CloseableHttpResponse responsePaket = this.httpClient.execute(post, context);
			
			//��ӡ��ǰ��cookie
			printlnCookie();
			
			//������Ӧ��
			return responsePaket;
			/**** ���ⲿ�ر� HttpResponse ****/
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * (װ�ر�����)
	 * һ����׼��post����
	 * ��post����װ�ر�����
	 * */
	private CloseableHttpResponse postRequest(URI targetURI, UrlEncodedFormEntity dataEntity) {
		//����һ��post����
		HttpPost post = new HttpPost(targetURI);
		
		//װ�ر�����
		post.setEntity(dataEntity);
		
		try {		
			//Ȼ��post�����͵�������
			CloseableHttpResponse responsePaket = this.httpClient.execute(post, context);
			
			//��ӡ��ǰ��cookie
			printlnCookie();
			
			//������Ӧ��
			return responsePaket;
			
			/**** ���ⲿ�ر� HttpResponse ****/
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * �����û���Ϣ����post�ı�����
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
		
		//�ѱ����ݰ�UTF-8��ʽ���б���
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Consts.UTF_8);
		entity.setContentType("application/x-www-form-urlencoded");
		
		return entity;
	}

	public static void main(String[] args) {
		LoginTest myLogin = new LoginTest();
		//���ʵ�¼ҳ�棬����¼
		myLogin.accessingPage(LoginConst.loginURLString);		
		
		//�����û�������
		UrlEncodedFormEntity userForm = myLogin.buildFormdata(LoginConst.username, LoginConst.password);
		
		//��¼��post������
		myLogin.loginWebsite(LoginConst.loginURLString, userForm);
	
		//��ɼ�
		//myLogin.accessingPage(LoginConst.gradeURLString);
		String htmlString = myLogin.htmlOfAccessingPage(LoginConst.gradeURLString);

		//�γ̳ɼ�map
		Map<Integer, List<String>> gradeMap = pageParsing.parseGrade(htmlString);
		
		for (int i = 0; i < gradeMap.size(); i++) {
			System.out.println(gradeMap.get(i));
		}
		
	}

}





























