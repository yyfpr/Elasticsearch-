package com.service;

import com.alibaba.fastjson.JSON;
import com.pojo.Content;
import com.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//业务编写
@Service
public class ContentService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //不能直接使用，@Autowired需要spring容器
    /**  public static void main(String[] args) throws Exception {
     new ContentService().parseContent("java");
     }
     */
    //1、解析数据放入es索引中
    public Boolean parseContent(String keywords,
                                String pageNo) throws Exception {
        List<Content> content = HtmlParseUtil.parseTitle(keywords,pageNo);
        //把查询的数据放入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for(int i = 0; i < content.size(); i++){
            bulkRequest.add(
                    new IndexRequest("news")
                            .source(JSON.toJSONString(content.get(i)), XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return  !bulk.hasFailures();
    }

    //2、获取这些数据，实现搜索功能
    public List<Map<String,Object>>  searchPage(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1) {
            pageNo = 0;
        }

        //条件搜索
        SearchRequest searchRequest = new SearchRequest("news");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
      // TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
      /*  MatchPhraseQueryBuilder termQueryBuilder= QueryBuilders.matchPhraseQuery("title",keyword);
        sourceBuilder.query(termQueryBuilder);*/
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));//60s未出来，说明出问题了


      /*   MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("title","content");
        sourceBuilder.query(multiMatchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
        */
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit documentFields : searchResponse.getHits().getHits()){
            list.add(documentFields.getSourceAsMap());
        }

        System.out.println(list.size());

        return  list;
    }

    //3、实现搜索高亮功能
    public Map<String,Object>  searchPageHighlightBuilder(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1) {
            pageNo = 0;
        }

        Map<String,Object> result=new HashMap<>();
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("news");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        System.out.println(keyword);
        //精准匹配
      MatchPhraseQueryBuilder termQueryBuilder= QueryBuilders.matchPhraseQuery("title",keyword);
        sourceBuilder.query(termQueryBuilder);
       sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));//60s未出来，说明出问题了

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多个高亮关闭
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);


        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits().getHits()){
            //获得高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果
            //解析高亮的字段
            // 将高亮字段替换为原来没有高亮的字段
            if (title!=null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments){
                    n_title += text;
                }
                sourceAsMap.put("title",n_title);//高亮的字段替换原来的内容
            }
            list.add(sourceAsMap);

        }
        result.put("list",list);
        result.put("count",searchResponse.getHits().getTotalHits().value);
        return  result;
    }

}
