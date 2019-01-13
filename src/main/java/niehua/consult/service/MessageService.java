package niehua.consult.service;

import niehua.consult.entity.*;
import niehua.consult.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @description: 消息业务层
 * @author: sheep
 * @date: create in 21:08 2018/7/23
 */

@Service
@Transactional
public class MessageService {

    @Resource
    private AnswerService answerService;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AnswerMapper answerMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private CommentMapper commentMapper;


    /*
    获取用户新消息个数
     */
    public Integer getSizeOfNewMessageListByUserId(String recieverId) {
        User user = userMapper.selectByPrimaryKey(recieverId);
        Integer size = messageMapper.selectNewByReciever(user).size();
        return size;
    }

    /*
    获取用户的所有消息列表
     */
    public List<Message> getAllMessageListByUserId(String recieverId) {
        return messageMapper.selectAllByReciever(userMapper.selectByPrimaryKey(recieverId));
    }

    /*
    设置所有消息已读
     */
    public void setAllNewMessagesHasReadByUserId(String recieverId) {
        messageMapper.updateHasReadOfAllByUser(userMapper.selectByPrimaryKey(recieverId));
    }

    /*
    获取消息详情，返回一个只有一个回答的问题，这个回答设置是否已赞，并将这个消息设置为已查阅
     */
    public Question getMessageDetail(String recieverId, Integer messageId) {
        Message message = messageMapper.selectByPrimaryKey(messageId);
        Answer answer = answerMapper.selectByPrimaryKey(message.getAnswerId());
        Question question = questionMapper.selectByPrimaryKey(answer.getQuestionId());
        answer.setLiked(answerService.isLiked(recieverId, answer.getId()));
        List<Answer> answerList = new LinkedList<>();
        answerList.add(answer);
        question.setAnswerList(answerList);
        setNewMessageHasReadByMessageId(messageId);
        return question;
    }

    /*
    设置单个消息已读
     */
    public void setNewMessageHasReadByMessageId(Integer messageId) {
        Message message = messageMapper.selectByPrimaryKey(messageId);
        message.setHasRead(true);
        messageMapper.updateByPrimaryKey(message);//应该改为只要一次sql操作
    }

    /*
    获取用户的所有新消息列表
     */
    public List<Message> getNewMessageListByUserId(String recieverId) {
        return messageMapper.selectNewByReciever(userMapper.selectByPrimaryKey(recieverId));
    }

    /*
    向数据库添加消息
     */
    public void addMessage(Integer idOfAnswerOrComment, Byte typerFlag) {
        String content = "";
        List<String> recieverIdList = getRecieverId(idOfAnswerOrComment, typerFlag);
        String senderId = "";
        Integer answerId = 0;
        switch (typerFlag) {
            case 1:
                Answer answer = answerMapper.selectByPrimaryKey(idOfAnswerOrComment);
                content = answer.getContent();
                if (content.length() > 20) {
                    content = content.substring(0, 20);
                    content += "...";
                }
                answerId = idOfAnswerOrComment;
                senderId = answer.getAuthorId();
                break;
            case 2:
                Comment comment = commentMapper.selectByPrimaryKey(idOfAnswerOrComment);
                content = comment.getContent();
                if (content.length() > 20) {
                    content = content.substring(0, 20);
                    content += "...";
                }
                answerId = comment.getAnswerId();
                senderId = comment.getAuthorId();
                break;
            default:
                break;
        }
        for (String aRecieverIdList : recieverIdList) {
            Message message = new Message();
            message.setContent(content);
            message.setSenderId(senderId);
            message.setAnswerId(answerId);
            message.setTypeFlag(typerFlag);
            message.setRecieverId(aRecieverIdList);
            messageMapper.insert(message);
        }
    }

    /*
    flag为3时需传入visitorid
     */
    public void addMessage(String visitorIdOflike, Integer idOfAnswerOrComment, Byte typeFlag) {
        Answer answer = answerMapper.selectByPrimaryKey(idOfAnswerOrComment);
        String recieverId = answer.getAuthorId();
        if (!visitorIdOflike.equals(recieverId)) {
            Message message = new Message();
            message.setRecieverId(recieverId);
            message.setSenderId(visitorIdOflike);
            message.setAnswerId(idOfAnswerOrComment);
            message.setTypeFlag(typeFlag);
            messageMapper.insert(message);
        }
    }

    /*
    根据预定逻辑设置接受者
     */
    private List<String> getRecieverId(Integer idOfAnswerOrComment, Byte typeFlag) {
        List<String> recieverIdList = new LinkedList<>();
        String sender = "";
        switch (typeFlag) {
            case 1:
                Answer answer = answerMapper.selectByPrimaryKey(idOfAnswerOrComment);
                Question question = questionMapper.selectByPrimaryKey(answer.getQuestionId());
                sender = answer.getAuthorId();
                String reciever = question.getAuthorId();
                if (!sender.equals(reciever))
                    recieverIdList.add(reciever);
                break;
            case 2:
                Comment comment = commentMapper.selectByPrimaryKey(idOfAnswerOrComment);
                answer = answerMapper.selectByPrimaryKey(comment.getAnswerId());
                question = questionMapper.selectByPrimaryKey(answer.getQuestionId());
                List<Comment> commentList = commentMapper.selectByAnswer(answer);
                sender = comment.getAuthorId();
                String reciever1 = question.getAuthorId();
                String reciever2 = answer.getAuthorId();
                if (!reciever1.equals(sender))
                    recieverIdList.add(reciever1);
                if (!reciever2.equals(sender) && !reciever2.equals(reciever1))
                    recieverIdList.add(reciever2);
                for (Comment aCommentList : commentList) {
                    comment = aCommentList;
                    String authorId = comment.getAuthorId();
                    if (recieverIdList.contains(authorId) || authorId.equals(sender))
                        continue;
                    recieverIdList.add(authorId);
                }
                break;
            default:
                break;
        }
        return recieverIdList;
    }
}
