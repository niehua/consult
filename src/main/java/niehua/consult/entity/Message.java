package niehua.consult.entity;

/*typeFlag
 * 1:
 * 		表示sender回答reciever提出的问题   答案
 * 		content存放回答的内容
 * 		reciever为问题提出者
 * 2：
 * 		表示sender评论reciever的回答      评论
 * 		content存放评论内容
 * 		reciever为该问题的作者，回答作者，该回答的所有评论者
 * 3:
 * 		表示sender点赞reciever的回答      点赞
 * 		content为空
 * 		reciever为回答作者
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;

import java.util.Date;
public class Message {
    private Integer id;

    private String content = "root";

    private String recieverId = "root";

    private String senderId = "root";

    private Integer answerId = 1;

    private Boolean hasRead = false;

    private Byte typeFlag = 0;

    private Date createTime = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId == null ? null : recieverId.trim();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId == null ? null : senderId.trim();
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Byte getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(Byte typeFlag) {
        this.typeFlag = typeFlag;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone="GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}