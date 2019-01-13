package niehua.consult;

import com.github.pagehelper.PageHelper;
import niehua.consult.controller.ControllerInterface;
import niehua.consult.entity.Answer;
import niehua.consult.entity.Message;
import niehua.consult.entity.Question;
import niehua.consult.entity.User;
import niehua.consult.mapper.AnswerMapper;
import niehua.consult.mapper.MessageMapper;
import niehua.consult.mapper.QuestionMapper;
import niehua.consult.mapper.UserMapper;
import niehua.consult.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsultApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Resource
	private ControllerInterface controllerInterface;

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
	private QuestionMapper questionMapper;

	@Resource
	private AnswerMapper answerMapper;

	@Resource
	private UserMapper userMapper;

	@Resource
	private MessageMapper messageMapper;

	@Value("${SPLIT_MARK}")
	private String mark;

	private Map map;

	/*
    测试提问
     */
	@Test
	public void testAskQuestion() {
		String questionContent = "问题7";
		String userId = "sheep3";
		questionService.askQuestion(userId, questionContent);
		System.out.println();
	}

    /*
    测试回答
     */
	@Test
	public void testAnswerQuestion() {
		answerService.answerQuestion("sheep3", 2, "2回答4");
	}

    /*
    测试点赞人数
     */
	@Test
	public void testNumberOfLike() {
		String userId = "sheep";
		Question question = questionService.getIntactQuestion(userId, 1, 1);
		List<Answer> answerList = question.getAnswerList();
		for (int i = 0; i < answerList.size(); i++) {
			Answer answer = answerList.get(i);
			System.out.println(userId + ":" + answer.getId() + " " + answer.getLikeNumber());
		}
	}

    /*
    测试hasliked
     */
	@Test
	public void testHasLiked() {
		String userId = "sheep";
		Question question = questionService.getIntactQuestion(userId, 1, 1);
		List<Answer> answerList = question.getAnswerList();
		for (int i = 0; i < answerList.size(); i++) {
			Answer answer = answerList.get(i);
			System.out.println(userId + ":" + answer.getId() + " " + answerService.isLiked(userId, answer.getId()));
		}
	}

    /*
    测试查看用户是否点赞
     */
	@Test
	public void testIsLiked(){
		System.out.println(answerService.isLiked("sheep1",2));
	}


    /*
    测设取消点赞
     */
	@Test
	public void testCancelLike() {
		answerService.cancelLiked("sheep1", 3);
		//System.out.println(mainService.isLiked("test1",3));
	}

    /*
    测试分页查询
     */
	@Test
	public void testSplitPage() {
		Question question = questionMapper.selectByPrimaryKey(3);//13个回答
		PageHelper.startPage(3, 6);
		List<Answer> answerList = answerMapper.selectByQuestion(question);
		System.out.println(answerList.size());
        /*System.out.println(answerList.size()+" id0:"+answerList.get(0).getId()+" id1:"+answerList.get(1).getId()+" id2:"+answerList.get(2).getId()+" id3:"+answerList.get(3).getId()+" id4:"+answerList.get(4).getId()+" id5:"+answerList.get(5).getId());
    }*/
	}
    /*
    自动返回插入id
     */
	@Test
	public void testAutoReturnId() {

	}

    /*
    测试登录将用户信息存入数据库
     */
	@Test
	public void testUserLogin() {
		userService.loginByUserId("sheep5", "谁普5", "北京", "", (byte)0);
	}


    /*
    小功能测试
     */
	@Test
	public void test(){
		String s = "sheep";
		s = s.substring(0,4);
		System.out.println(s);
	}

    /*
    测试返回所有消息
     */
	@Test
	public void testGetAllMessageListByUserId(){
		List<Message> messageList = messageService.getAllMessageListByUserId("sheep1");
		for(int i= 0;i<messageList.size();i++){
			Message message = messageList.get(i);
			System.out.println(message.getId());//输出14,15,17,19
		}
	}

    /*
    测试返回用户新消息
     */
	@Test
	public void testGetSizeOfNewMessageListByUserId(){
		System.out.println(messageService.getSizeOfNewMessageListByUserId("sheep"));
	}

    /*
    测试varchar(32)的真实个数
     */
	@Test
	public void testNumbersOfVarchar(){
		User user = new User();
		String userId = "";
		for(int i = 0;i<32;i++){
			userId+="的";
		}
		user.setId(userId);
		userMapper.insert(user);
	}

    /*
    测试用户查看问题详情，like是否正确
     */
	@Test
	public void testCorrectLikeList(){
		Question question = questionService.getIntactQuestion("ow2vg0s5NPjcVQfrXgI3v3iuaMIM",1,1);
		List<Answer> answerList = question.getAnswerList();
		for (int i = 0;i<answerList.size();i++){
			Answer answer = answerList.get(i);
			System.out.println("!"+answer.getId()+" "+answer.isLiked());
		}
		answerService.like("ow2vg0s5NPjcVQfrXgI3v3iuaMIM",1);

		question = questionService.getIntactQuestion("ow2vg0s5NPjcVQfrXgI3v3iuaMIM",1,1);
		answerList = question.getAnswerList();
		for (int i = 0;i<answerList.size();i++){
			Answer answer = answerList.get(i);
			System.out.println(answer.getId()+" "+answer.isLiked());
		}
	}


    /*
    测试将问题按createtime从新到旧排序
     */
	@Test
	public void getNewEmptyQuestionList(){
		List<Question> questionList = questionService.getNewEmptyQuestionList(1).getList();
		for (int i = 0;i<questionList.size();i++){
			Question question = questionList.get(i);
			System.out.println(question.getId()+" "+question.getCreateTime());
		}
	}
}

