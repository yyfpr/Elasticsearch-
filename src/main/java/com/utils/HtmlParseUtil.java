package com.utils;

import com.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.swing.border.TitledBorder;
import javax.xml.transform.Source;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        HtmlParseUtil.parseTitle("三文鱼","2");
       // HtmlParseUtil.parseContent("https://k.sina.com.cn/article_1288369910_v4ccaf6f6020012qdl.html");

    }

    //获取新闻标题
    public static List<Content>  parseTitle(String keyword,String pageNo) throws IOException {
        //获取请求  https://search.jd.com/Search?keyword=java&enc=utf-8&wq=java&pvid=42039118427145a79ba97185fdd627ee
        String url="https://search.sina.com.cn/news/?range=all&q="+keyword+"&c=news&size=20&page="+pageNo+"&ie=utf-8";
        //解析网页
        Document document = Jsoup.parse(new URL(url),300000000);
        Element element=document.getElementById("result");

        //获取所有的box-result元素
        Elements elements= element.getElementsByClass("box-result");
        ArrayList<Content> titleList=new ArrayList<Content>();

        for (int i=0;i<elements.size();i++) {
            Content content1 = new Content();

            Element  el=elements.get(i);
            Element  h2=el.getElementsByTag("h2").get(0);
            String hrefurl=h2.getElementsByTag("a").eq(0).attr("href");
            //通过遍历elements 获取 Element   h2  来获取u'r'l  zifuchuan   datesource
            String datesource=h2.getElementsByClass("fgray_time").eq(0).html();
           /* HtmlParseUtil.parseContent(hrefurl);*/
            //输出标题
            String tilte= h2.getElementsByTag("a").eq(0).text();
            if(el.getElementsByClass("r-img").size()>0){

                Element rimg=el.getElementsByClass("r-img").get(0);
                String img= rimg.getElementsByTag("img").eq(0).attr("src");
                System.out.println(img);
                content1.setImg(img);
            }

            Element rinfo=el.getElementsByClass("r-info").get(0);
            String content=rinfo.getElementsByClass("content").get(0).html();
            System.out.println(hrefurl);//明细链接
            System.out.println(tilte);
           // System.out.println(date);
            System.out.println(datesource);
            System.out.println(content);
            content1.setDatesource(datesource);
            content1.setTitle(tilte);
            content1.setContent(content);
            content1.setHrefurl(hrefurl);
            System.out.println("******************************************");
            titleList.add(content1);

        }

        return titleList;
    }

    //获取新闻内容
    /**public static List<Content>  parseContent(String url) throws IOException {
        //获取请求  https://search.jd.com/Search?keyword=java&enc=utf-8&wq=java&pvid=42039118427145a79ba97185fdd627ee

        //解析网页
        Document document = Jsoup.parse(new URL(url),3000);
        Element element=document.getElementById("article");
        //获取时间来源
        Element top_bar_wrap=document.getElementById("top_bar_wrap");
        Element top_bar=top_bar_wrap.getElementById("top_bar");
        Element top_bar_inner=top_bar.getElementsByClass("top-bar-inner clearfix").get(0);
        Element date_source=top_bar_inner.getElementsByClass("date-source").get(0);
        String date=date_source.getElementsByClass("date").get(0).html();
        String source=date_source.getElementsByClass("source ent-source").get(0).html();
        System.out.println(date+source);

        //获取所有的box-result元素
        Elements imgwrappers= element.getElementsByClass("img_wrapper");
        //  ArrayList<Content> goodsList=new ArrayList<Content>();

        for (int i=0;i<imgwrappers.size();i++) {
            Element  imgs=imgwrappers.get(i);
            String img= imgs.getElementsByTag("img").eq(0).attr("src");
            System.out.println(img);
        }

        Elements ps= element.getElementsByTag("p");
        for (int i=0;i<ps.size();i++) {
            Element p=  ps.get(i);
            if(p.getElementsByTag("font").size()>0){
                String font=p.getElementsByTag("font").get(0).html();
                System.out.println(font);
            }else{
                String pContent=p.html();
                System.out.println(pContent);
            }

        }
        return null;
    }
     */

}
