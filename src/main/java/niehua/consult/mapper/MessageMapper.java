package niehua.consult.mapper;


import niehua.consult.entity.Message;
import niehua.consult.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @description: 访问message表
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
@Mapper
public interface MessageMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer id);

    @Select("SELECT * FROM message WHERE reciever_id = #{id} AND has_read = false ORDER BY id DESC")
    List<Message> selectNewByReciever(User reciever);

    @Select("SELECT * FROM message WHERE reciever_id = #{id} ORDER BY id DESC")
    List<Message> selectAllByReciever(User reciever);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    @Update("UPDATE message SET has_read = true WHERE reciever_id = #{id}")
    int updateHasReadOfAllByUser(User reciever);
}