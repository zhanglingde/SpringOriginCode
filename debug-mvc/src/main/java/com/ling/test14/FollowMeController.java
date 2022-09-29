package com.ling.test14;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;


/**
 * 先去掉评论中的敏感词，然后保存到数据库，接着redirect到一个显示结果的处理器，在其中通过文章Id获取文章标题和文章内容，最后显示到页面
 */
@Controller
@SessionAttributes("articleId")
public class FollowMeController {
    private final Log logger = LogFactory.getLog(FollowMeController.class);

    // 敏感词
    private final String[] sensitiveWords = new String[]{"k1", "s2"};

    // 去掉评论中的敏感词
    @ModelAttribute("comment")
    public String replaceSensitiveWords(String comment) throws IOException {
        if (comment != null) {
            logger.info("原始comment：" + comment);
            for (String sw : sensitiveWords) {
                comment = comment.replaceAll(sw, "");
            }
            logger.info("去敏感词后comment：" + comment);
        }
        return comment;
    }

    // 评论保存到数据库
    @RequestMapping(value = {"/articles/{articleId}/comment"})
    public String doComment(@PathVariable String articleId, RedirectAttributes attributes, Model model) throws Exception {
        attributes.addFlashAttribute("comment", model.asMap().get("comment"));
        model.addAttribute("articleId", articleId);
        // 此处将评论内容保存到数据库
        return "redirect:/showArticle";
    }

    // 转发到显示页面视图
    @RequestMapping(value = {"/showArticle"}, method = {RequestMethod.GET})
    public String showArticle(Model model, SessionStatus sessionStatus) throws Exception {
        String articleId = (String) model.asMap().get("articleId");
        model.addAttribute("articleTitle", articleId + "号文章标题");
        model.addAttribute("article", articleId + "号文章内容");
        sessionStatus.setComplete();
        return "article";
    }

    @RequestMapping(value = "/body",method = RequestMethod.POST)
    public String postBody(@RequestBody List<User> userList) {
        System.out.println("userList = " + userList);
        return "success";
    }

    @RequestMapping("/testRequestBody")
    public String testRequestBody(@RequestBody List<User> body) {
        System.out.println("执行了...");
        System.out.println(body);
        return "success";
    }
}
