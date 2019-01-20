package niehua.consult.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import niehua.consult.entity.*;
import niehua.consult.service.*;
import niehua.consult.wechat.util.LoginUtil;
import niehua.consult.wechat.util.HttpsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Controller
public class ConsultController implements ControllerInterface {

    @Resource
    private AnswerService answerService;

    @Resource
    private QuestionService questionService;

    @Resource
    private CommentService commentService;

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    @Resource
    private HttpsRequest httpsRequest;

    private Map<String, Object> map;

    @Value("${login.appID}")
    private String APPID;

    @Value("${login.appsecret}")
    private String SECRET;

    @Value("${login.scope_userinfo}")
    private String SCOPE;

    @Value("${login.redirect_uri}")
    private String REDIRECT_URI;

    /**
     * 第一步：用户同意授权，获取code
     * 网页微信授权登录接口
     *
     * @throws UnsupportedEncodingException
     * @return如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE。
     */
    @GetMapping("/")
    public ModelAndView code() throws UnsupportedEncodingException {
        String url = REDIRECT_URI + "/access_token";
        //System.out.println("编码前，授权登录url:"+url);
        url = URLEncoder.encode(url, "UTF-8");
        //System.out.println("编码后，授权登录url:"+url);
        String urlCode = LoginUtil.getCode(APPID, url, SCOPE);
        return new ModelAndView(new RedirectView(urlCode));//response.sendRedirect(urls);HttpServletResponse response
    }

    /**
     * 第二步：通过code换取网页授权access_token
     * 首先请注意，这里通过code换取的是一个特殊的网页授权access_token,
     * 与基础支持中的access_token（该access_token用于调用其他接口）不同。
     * 公众号可通过下述接口来获取网页授权access_token。本步骤中获取到网页授权access_token的同时，也获取到了openid
     *
     * @return JSON数据包
     */
    @GetMapping("/access_token")
    public String access_token(@RequestParam(name = "code", required = false) String CODE) {
        //System.out.println("获得code:"+CODE);

        String urlAccess_token = LoginUtil.getWebAccess(APPID, SECRET, CODE);
        String resultAccess_token = httpsRequest.getHttpsResponse(urlAccess_token, "");
        //System.out.println("获取到的access_token="+resultAccess_token);
        //存储返回集
        JSONObject json = JSON.parseObject(resultAccess_token);

        /**第三步：通过基础支持的access_token换取用户信息（该access_token用于调用其他接口）
         * @return JSON数据包
         */
        String urlUserInfo = LoginUtil.getUserInfo(json.getString("access_token"), json.getString("openid"));
        String resultUserInfo = httpsRequest.getHttpsResponse(urlUserInfo, "");
        //System.out.println("获取到的usrtinfo="+resultUserInfo);
        //存储返回集
        JSONObject jsonUI = JSON.parseObject(resultUserInfo);
        String openid = jsonUI.getString("openid");
        userService.loginByUserId(openid, jsonUI.getString("nickname"),
                jsonUI.getString("province"), jsonUI.getString("headimgurl"), jsonUI.getInteger("sex").byteValue());
        return "redirect:/index.html?openid=" + openid;
    }

    /*
 根据关键字搜索问题，其回答是精简的，并分页返回
  */
    @GetMapping("/searchByWord")
    @ResponseBody
    @Override
    public PageInfo<Question> searchByWord(String visitorId, String keyword, Integer currentPage) {
        PageInfo<Question> info = questionService.getQuestions(keyword, currentPage);
        return info;
    }

    /*
    根据用户id和问题id返回问题及其所有回答
     */
    @GetMapping("/getQuestionDetail")
    @ResponseBody
    @Override
    public Question getQuestionDetailByQuestionId(String visitorId, Integer questionId, Integer currentPage) {
        return questionService.getIntactQuestion(visitorId, questionId, currentPage);
    }

    /*
    用户对相应回答点赞
     */
    @GetMapping("/likeAnswer")
    @ResponseBody
    @Override
    public void likeAnswer(String visitorId, Integer answerId) {
        answerService.like(visitorId, answerId);
    }

    /*
    取消用户对相应回答的点赞
     */
    @GetMapping("/cancelLikeAnswer")
    @ResponseBody
    @Override
    public void cancelLikeAnswer(String visitorId, Integer answerId) {

        answerService.cancelLiked(visitorId, answerId);
    }

    /*
    用户回答相关问题
     */
    @GetMapping("/answerQuestion")
    @ResponseBody
    @Override
    public Answer answerQuestion(String visitorId, Integer questionId, String answerContent) {
        return answerService.answerQuestion(visitorId, questionId, answerContent);
    }

    /*
    专用于帮助别人界面回答问题
     */
    @GetMapping("/answerQuestionAndJumpToQuestionDetail")
    @ResponseBody
    @Override
    public void answerQuestionAndJumpToQuestionDetail(String visitorId, Integer questionId, String answerContent) {
        answerService.answerQuestion(visitorId, questionId, answerContent);
    }

    /*
    用户提问题
     */
    @GetMapping("/askQuestion")
    @ResponseBody
    @Override
    public Question askQuestion(String visitorId, String questionContent) {
        return questionService.askQuestion(visitorId, questionContent);
    }


    /*
    根据用户id返回其所有提出的问题，其回答是精简的，并分页返回
     */
    @GetMapping("/getQuestionByUser")
    @ResponseBody
    @Override
    public PageInfo<Question> getQuestionByUserId(String visitorId, Integer currentPage) {

        return questionService.getUserQuestion(visitorId, currentPage);
    }

    /*
    获取用户个人资料
     */
    @GetMapping("/userDetail")
    @ResponseBody
    @Override
    public User getUserDetail(String visitorId) {
        return userService.getIntactUserByUserId(visitorId);
    }

    @GetMapping("/getNumberOfNewMessage")
    @ResponseBody
    @Override
    public Integer getSizeOfNewMessageList(String visitorId) {
        return messageService.getSizeOfNewMessageListByUserId(visitorId);
    }


    /*
 根据用户id返回其所有消息
  */
    @GetMapping("/getAllMessageByUserId")
    @ResponseBody
    @Override
    public List<Message> getAllMessageByUserId(String visitorId) {

        return messageService.getAllMessageListByUserId(visitorId);

    }

    @GetMapping("/getAllMessageByUserId1")
    @ResponseBody
    public List<Message> getAllMessageByUserId1(String visitorId) {

        return messageService.getAllMessageListByUserId(visitorId);
    }

    /*
    根据用户id返回其所有新消息
     */
    @GetMapping("/getNewMessageByUserId")
    @ResponseBody
    @Override
    public List<Message> getNewMessageByUserId(String visitorId) {
        return messageService.getNewMessageListByUserId(visitorId);
    }

    /*
    根据用户id返回其所有新消息
     */
    @GetMapping("/getNewMessageByUserId1")
    @ResponseBody
    public List<Message> getNewMessageByUserId1(String visitorId) {
        return messageService.getNewMessageListByUserId(visitorId);
    }

    /*
    设置全部未读消息已读
     */
    @GetMapping("/setAllNewMessagesHasRead")
    @ResponseBody
    @Override
    public void setAllNewMessagesHasRead(String visitorId) {
        messageService.setAllNewMessagesHasReadByUserId(visitorId);
    }


    /*
    查看单条消息的详情
     */
    @GetMapping("/getMessageDetail")
    @ResponseBody
    @Override
    public Question getMessageDetail(String visitorId, Integer messageId) {
        return messageService.getMessageDetail(visitorId, messageId);
    }

    /*
    根据用户id评论对应问题
     */
    @GetMapping("/commentAnswer")
    @ResponseBody
    @Override
    public Comment commentAnswer(String visitorId, Integer answerId, String commentContent) {
        return commentService.commentAnswer(visitorId, answerId, commentContent);
    }

    /*
    根据答案id返回所有评论
     */
    @GetMapping("/getCommentListByAnswerId")
    @ResponseBody
    @Override
    public List<Comment> getCommentListByAnswerId(String visitorId, Integer answerId) {
        return commentService.getCommentListByAnswerId(answerId);
    }

    /*
    用户点击帮助别人查看最新且未被回答的问题
     */
    @GetMapping("/getNewEmptyQuestionList")
    @ResponseBody
    @Override
    public PageInfo<Question> getNewEmptyQuestionList(String visitorId, Integer currentPage) {
        return questionService.getNewEmptyQuestionList(currentPage);
        // System.out.println(questionService.getNewEmptyQuestionList(currentPage));
    }
}

