package niehua.consult.mapper;

import niehua.consult.entity.Answer;
import niehua.consult.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @description: 访问comment表
 * @author: sheep
 * @date: create in 15:57 2018/7/19
 */
@Mapper
public interface CommentMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    @Select("SELECT * FROM comment WHERE answer_id = #{id}")
    List<Comment> selectByAnswer(Answer answer);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);
}