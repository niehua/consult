package niehua.consult.service;


import niehua.consult.entity.Answer;
import niehua.consult.entity.Question;
import niehua.consult.mapper.AnswerMapper;
import niehua.consult.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 回答业务层
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
@Service
@Transactional
public class AnswerService {

    @Resource
    private AnswerMapper answerMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private KeywordService keywordService;

    @Value("${SPLIT_MARK}")
    private String SPLIT_MARK;

    /*
    传入用户id，问题id，回答内容
     */
    public Answer answerQuestion(String authorId, Integer questionId, String answerContent) {
        Answer answer = new Answer();
        answer.setAuthorId(authorId);
        answer.setQuestionId(questionId);
        answer.setContent(answerContent);
        answerMapper.insert(answer);
        messageService.addMessage(answer.getId(), (byte) 1);
        //问题回答个数+1
        Question question = questionMapper.selectByPrimaryKey(questionId);
        question.setAnswerNumber(question.getAnswerNumber() + 1);
        questionMapper.updateByPrimaryKey(question);
        keywordService.processKeywordOfAnswer(answer);
        return answer;
    }

    /*
    输入用户id，答案序列，设置答案序列该用户是否点赞
     */
    public List<Answer> setLikedList(String visitorId, List<Answer> answerList) {
        for (Answer a : answerList) {
            a.setLiked(isLiked(visitorId,a.getId()));
        }
        return answerList;
    }


    /*
    传入用户id，答案id，取消点赞
     */
    public void cancelLiked(String visitorId, Integer answerId) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        Integer likeNumber = answer.getLikeNumber() - 1;
        String likeUser = answer.getLikeUser();
        int index = likeUser.indexOf(SPLIT_MARK + visitorId + SPLIT_MARK);
        likeUser = likeUser.substring(0, index) + likeUser.substring(index + visitorId.length() + 3, likeUser.length());
        answer.setLikeNumber(likeNumber);
        answer.setLikeUser(likeUser);
        answerMapper.updateByPrimaryKey(answer);
    }

    /*
    传入答案id，用户id，完成点赞
     */
    public void like(String visitorId, Integer answerId) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        Integer likeNumber = answer.getLikeNumber() + 1;
        String likeUser = answer.getLikeUser();
        if (likeUser.isEmpty()) {
            likeUser = SPLIT_MARK;
        }
        likeUser += visitorId + SPLIT_MARK;
        answer.setLikeNumber(likeNumber);
        answer.setLikeUser(likeUser);
        answerMapper.updateByPrimaryKey(answer);
        messageService.addMessage(visitorId, answerId, (byte) 3);
    }

    public boolean isLiked(String visitorId, int id){
    	return answerMapper.selectByPrimaryKey(id).getLikeUser().contains(SPLIT_MARK + visitorId + SPLIT_MARK);// 查看用户是否点赞，已点赞返回yes
    }
}

