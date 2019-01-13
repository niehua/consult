package niehua.consult.service;


import niehua.consult.entity.Answer;
import niehua.consult.entity.Comment;
import niehua.consult.mapper.AnswerMapper;
import niehua.consult.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * @description: 评论业务层
 * @author: sheep
 * @date: create in 21:28 2018/7/19
 */
@Service
@Transactional
public class CommentService {

    @Resource
    private MessageService messageService;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private AnswerMapper answerMapper;

    /*
    传入用户Id，回答id，评论内容，完成评论
     */
    public Comment commentAnswer(String authorId, Integer answerId, String commentContent) {
        Comment comment = new Comment();
        comment.setAuthorId(authorId);
        comment.setAnswerId(answerId);
        comment.setContent(commentContent);
        commentMapper.insert(comment);
        messageService.addMessage(comment.getId(), (byte) 2);
        Answer answer = answerMapper.selectByPrimaryKey(answerId);//评论个数+1
        answer.setCommentNumber(answer.getCommentNumber() + 1);
        answerMapper.updateByPrimaryKey(answer);
        return comment;
    }

    /*
    传入回答id，返回所有评论
     */
    public List<Comment> getCommentListByAnswerId(Integer answerId) {
        return commentMapper.selectByAnswer(answerMapper.selectByPrimaryKey(answerId));
    }

    /*
    传入评论id，删除评论
     
    public void deleteComment(Integer commentId) {
        commentMapper.deleteByPrimaryKey(commentId);
    }*/
}
