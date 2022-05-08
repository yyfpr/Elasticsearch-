package com.controller;

import com.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;

///////
@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;
    @ResponseBody
    @GetMapping("/parse/{keyword}/{pageNo}")
    public Boolean parse(@PathVariable("keyword") String keyword, @PathVariable("pageNo") String pageNo) throws Exception {
        return contentService.parseContent(keyword,pageNo);
    }

    @ResponseBody
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public Map<String, Object> search(@PathVariable("keyword") String keyword,
                                           @PathVariable("pageNo") Integer pageNo,
                                           @PathVariable("pageSize") Integer pageSize) throws IOException {
        return contentService.searchPageHighlightBuilder(keyword,pageNo,pageSize);
    }

}
