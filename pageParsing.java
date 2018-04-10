package com.albert.httpclient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * ��ҳHTML����
 * */
public final class pageParsing {

	/**
	 * @param args
	 */
	public static Map<Integer, List<String>> parseGrade(String pageHTMLString) {
		
        //ȥ�����пո�
        String htmlString2 = pageHTMLString.toString().replaceAll("\\s+", "");
        
        //���������ҳ����пγ̳ɼ���Ϣ
        Pattern p =Pattern.compile("<trclass=\"t_con\"style=\"background-color:rgb\\S*?<td>&nbsp;</td></tr>");//���пո�Ҫȥ���ɾ�
        
        Matcher m = p.matcher(htmlString2);
        
        ArrayList<String> arrList = new ArrayList<String>();
        while (m.find()) {
        	arrList.add(m.group().replaceAll("<!--.*?-->", ""));//˳��ȥ��ע��
		}
		
        /**
         * 
         * ��ϴ���ݣ�����ǩ<td>����
         * **/
        LinkedHashMap<Integer, List<String>> map = new LinkedHashMap<Integer, List<String>>();
        Pattern pTable = Pattern.compile("<td\\S*?>\\S*?</td>");
        
        for (int i = 0; i < arrList.size(); i++) {
        	String temp = arrList.get(i);
        	
        	ArrayList<String> arrListTd = new ArrayList<>();
        	Matcher mTable = pTable.matcher(temp);
            while (mTable.find()) {
            	arrListTd.add(mTable.group().replaceAll("<\\S*?>", ""));
    		}
            //	 ����0,3,4,6,7
            arrListTd.set(1, "ReMove_Flag");
            arrListTd.set(2, "ReMove_Flag");
            arrListTd.set(5, "ReMove_Flag");
            arrListTd.set(8, "ReMove_Flag");
            arrListTd.set(9, "ReMove_Flag");
            arrListTd.set(10, "ReMove_Flag");
            
            arrListTd.remove("ReMove_Flag");
            arrListTd.remove("ReMove_Flag");
            arrListTd.remove("ReMove_Flag");
            arrListTd.remove("ReMove_Flag");
            arrListTd.remove("ReMove_Flag");
            arrListTd.remove("ReMove_Flag");
            
        	map.put(i, arrListTd);
        	
        	//arrList.set(i, wash);
     	
		}
		
		return map;
	}
	
	public static void main(String[] args) {

	}

}
