package com.example.smartpengesture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.CaseFormat;

import android.util.Log;
import android.widget.FrameLayout;

public class testxml {
	
	static Document doc;
    static String pagexml;
    /**
     * 
     * @param x
     * @param y
     * @param pageid
     * @return :tag[2],tag=null:不在任何区域 tag[0]:题号；tag[1]:题号对应题目的某个区域
     */
    public static ArrayList<Integer> test(float x,float y, int bookid,int pageid)
    {
    	switch (bookid) {
		case 0:
			pagexml = "book_"+bookid+"_page_"+(pageid%20)+".xml";
			break;
		case 1:
			pagexml = "book_"+bookid+"_page_"+(pageid%8)+".xml";
			break;
		default:
			break;
		}
    	

    	int count = 2;//计数器,页眉和组只有quyu属性，在使用其他属性时要减去模板中的页眉和组的数量
    	
    	ArrayList<Integer> tag = new ArrayList<Integer>();
    	
    	File file = new File("/sdcard/xml/" +pagexml);
    	try 
    	{
    		doc = Jsoup.parse(file, "UTF-8");
    	
    	 }
    	catch (IOException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	if (doc==null) {
			return null;
		}
    	Elements item = doc.getElementsByTag("item");
    	Elements type = doc.getElementsByTag("type");
    	Elements quyu = doc.getElementsByTag("quyu");
    	Elements tihaoqu = doc.getElementsByTag("tihaoqu");
    	Elements tiganqu = doc.getElementsByTag("tiganqu");
    	Elements datiqu = doc.getElementsByTag("datiqu");
    	Elements itemson;
    	Elements liubaiqu;
    	Elements tiganquson;
    	
    	
    	//0-页眉  1-组  2-题号区  3-答题区  4-题干区  5-留白区
    	for(int i = 0;i<item.size();i++)
    	{
    		//根据区域坐标判断属于哪个区域（题目/页眉/组）
    		if(y>Double.valueOf(quyu.get(i).getElementsByTag("y1").text().toString()) && y<Double.valueOf(quyu.get(i).getElementsByTag("y2").text().toString()) && x>Double.valueOf(quyu.get(i).getElementsByTag("x1").text().toString()) && x<Double.valueOf(quyu.get(i).getElementsByTag("x2").text().toString()))
    		{
    			//判断页眉
    			if(type.get(i).text().toString().equals("页眉") == true)
    			{
    				tag.add(i);
    				tag.add(0);
    				break;
    			}
    			
    			//判断组
    			else if(type.get(i).text().toString().equals("组") == true)
    			{
    				tag.add(i);
    				tag.add(1);
    				break;
    			}
    			
    			//判断题目区域
    			else
    			{
    				//判断题号区
    				if(x>Double.valueOf(tihaoqu.get(i-count).getElementsByTag("x1").text().toString()) && x<Double.valueOf(tihaoqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(tihaoqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(tihaoqu.get(i-count).getElementsByTag("y2").text().toString()))
    				{
    					tag.add(i);
        				tag.add(2);
    					break;
    				}
    				
    				//判断答题区
    				if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 2)
					{
						if(x > Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
					}
					
					if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 4)
					{
						if(x> Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
						
						else if(x > Double.valueOf(datiqu.get(i-count).getElementsByTag("x3").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x4").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y3").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y4").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
					}
					
					if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 6)
					{
						if(x > Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
						
						else if(x > Double.valueOf(datiqu.get(i-count).getElementsByTag("x3").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x4").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y3").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y4").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
						
						else if(x > Double.valueOf(datiqu.get(i-count).getElementsByTag("x5").text().toString()) && x < Double.valueOf(datiqu.get(i-count).getElementsByTag("x6").text().toString()) && y > Double.valueOf(datiqu.get(i-count).getElementsByTag("y5").text().toString()) && y < Double.valueOf(datiqu.get(i-count).getElementsByTag("y6").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
					}
    				
    				//判断题干区
					if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 2)
					{
						if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							
							tag.add(i);
		    				tag.add(4);
							break;
						}
					}
					
					if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 4)
					{
						if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							
							tag.add(i);
		    				tag.add(4);
							break;
						}
						
						else if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x3").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x4").text().toString()) && y > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y3").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y4").text().toString()))
						{
							
							tag.add(i);
		    				tag.add(4);
							break;
						}
					}
					
					
					if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 6)
					{
						if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && y > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							
							tag.add(i);
		    				tag.add(4);
							break;
						}
						
						else if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x3").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x4").text().toString()) && y> Double.valueOf(tiganqu.get(i-count).getElementsByTag("y3").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y4").text().toString()))
						{
							
							tag.add(i);
		    				tag.add(4);
							break;
						}
						
						else if(x > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x5").text().toString()) && x < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x6").text().toString()) && y > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y5").text().toString()) && y < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y6").text().toString()))
						{
							tag.add(i);
		    				tag.add(4);
							break;
						}
					}
    				
    				//留白区
    				tag.add(i);
        			tag.add(5);
        			break;
    				
    			}
    		}
    	}
    	
    	if(tag.size()<2)
    	{
    		return null;
    	}
    	
    	else
    	{
    		tag.add(JudgeGroup(y, bookid, pageid));
    		return tag;
    	}
    }

    static Document doc2;
    static String pagexml2;
	private static Integer JudgeGroup(float meanyy, int bookID, int pageID) {
		// TODO Auto-generated method stub
        pagexml2 = "book_"+bookID+"_page_"+(pageID%20)+".xml";
    	
    	File file = new File("/sdcard/xml/" +pagexml2);
    	try 
    	{
    		doc2 = Jsoup.parse(file, "UTF-8");
    	
    	 }
    	catch (IOException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	Elements element = doc2.getElementsByTag("itemnumber");
    	Elements quyu = doc2.getElementsByTag("quyu");
    	if(meanyy > Double.valueOf(quyu.get(1).getElementsByTag("y2").text().toString()))
    	{
    		if(element.get(1).text().toString().equals("A") == true)
    		{
    			return -1;
    		}
    		else
    		{
    			return -2;
    		}
    	}
    	
    	else
    	{
    		if(element.get(1).text().toString().equals("A") == true)
    		{
    			return -2;
    		}
    		else
    		{
    			return -1;
    		}
    	}
	}


}
