package com.albert.httpclient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 网页HTML解析
 * */
public final class pageParsing {

	/**
	 * @param args
	 */
	public static Map<Integer, List<String>> parseGrade(String pageHTMLString) {
		
        //去除所有空格
        String htmlString2 = pageHTMLString.toString().replaceAll("\\s+", "");
        
        //按该特征找出所有课程成绩信息
        Pattern p =Pattern.compile("<trclass=\"t_con\"style=\"background-color:rgb\\S*?<td>&nbsp;</td></tr>");//所有空格都要去除干净
        
        Matcher m = p.matcher(htmlString2);
        
        ArrayList<String> arrList = new ArrayList<String>();
        while (m.find()) {
        	arrList.add(m.group().replaceAll("<!--.*?-->", ""));//顺便去除注释
		}
		
        /**
         * 
         * 清洗数据，按标签<td>分行
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
            //	 保留0,3,4,6,7
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
