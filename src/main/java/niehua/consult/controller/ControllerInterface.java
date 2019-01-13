package niehua.consult.controller;

import com.github.pagehelper.PageInfo;
import niehua.consult.entity.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.List;



/**
 * @description:
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
public interface ControllerInterface {

    /*
    用户对相应回答点赞
     */
    public void likeAnswer(String visitorId, Integer answerId);

    /*
    取消用户对相应回答的点赞
     */
    public void cancelLikeAnswer(String visitorId, Integer answerId);

    /*
    用户回答相关问题
     */
    public Answer answerQuestion(String visitorId, Integer questionId, String answerContent);

    /*
    用户提问题
     */
    public Question askQuestion(String visitorId, String questionContent);

    /*
    评论回答
     */
    public Comment commentAnswer(String visitorId, Integer answerId, String commentContent);

    /*
    查看所有评论
     */
    public List<Comment> getCommentListByAnswerId(String visitorId, Integer answerId);

    /*
    根据关键字查找问题，其回答是精简的，并分页返回
     */
    public PageInfo<Question> searchByWord(String visitorId, String keyword, Integer currentPage);

    /*
    根据用户id和问题id返回问题及其所有回答
    已设置用户对该问题下所有回答点赞与否
     */
    public Question getQuestionDetailByQuestionId(String visitorId, Integer questionId, Integer currentPage);

    /*
    根据用户id返回其所有提出的问题，其回答且设置是否赞过与是精简的，并分页返回
     */
    public PageInfo<Question> getQuestionByUserId(String visitorId, Integer currentPage);

    /*
    根据用户id查看用户资料
     */
    public User getUserDetail(String visitorId);

    /*
    用户每次登录都需调用，重置用户上次登录时间
     */
    /*public Map login(String visitorId, String nikeName, String province, String headImgUrl, Integer sex);*/
    /*
    微信登录套件
     */

    /*
    第一步：用户同意授权，获取code
    网页微信授权登录接口
    @throws UnsupportedEncodingException
    @return如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE。
     */
    public ModelAndView code() throws UnsupportedEncodingException;


    /*
    第二步：通过code换取网页授权access_token
    首先请注意，这里通过code换取的是一个特殊的网页授权access_token,
    与基础支持中的access_token（该access_token用于调用其他接口）不同。
    公众号可通过下述接口来获取网页授权access_token。本步骤中获取到网页授权access_token的同时，也获取到了    openid
    @return  JSON数据包
     */
    public String access_token(String CODE);

    /*
    获取用户新消息
     */
    public List<Message> getNewMessageByUserId(String visitorId);

    /*
    获取用户所有消息
     */
    public List<Message> getAllMessageByUserId(String visitorId);

    /*
    返回用户的新消息个数
     */
    public Integer getSizeOfNewMessageList(String visitorId);



    /*
    用户点击帮助别人查看最新且未被回答的问题
     */
    public PageInfo<Question> getNewEmptyQuestionList(String visitorId, Integer currentPage);

    /*
    专用于帮助别人界面回答问题
     */
    public void answerQuestionAndJumpToQuestionDetail(String visitorId, Integer questionId, String answerContent);

    /*
    设置全部未读消息已读
     */
    public void setAllNewMessagesHasRead(String visitorId);

    /*
    查看单条消息的详情
     */
    public Question getMessageDetail(String visitorId, Integer messageId);
}










    /*
    登录
     *//*
    @GetMapping("/login")
    @ResponseBody
    public String login(String name, String password);

    *//*
    注册
     *//*
    @RequestMapping("/register")
    @ResponseBody
    public String register(String name, String password, String rePassword);

    *//*
    修改密码
     *//*
    @GetMapping("/changepassword")
    @ResponseBody
    public String changePassword(String name, String password, String newPassword);*/

