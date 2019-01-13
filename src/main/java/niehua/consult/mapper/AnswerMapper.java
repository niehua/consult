package niehua.consult.mapper;

import niehua.consult.entity.Answer;
import niehua.consult.entity.Question;
import niehua.consult.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 访问answer表
 * @author: sheep
 * @date: create in 15:57 2018/7/19
 */
@Mapper
public interface AnswerMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Answer record);

    int insertSelective(Answer record);

    Answer selectByPrimaryKey(Integer id);

    @Select("SELECT * FROM answer WHERE question_id = #{id}")
    public List<Answer> selectByQuestion(Question question);

    @Select("SELECT * FROM answer WHERE author_id = #{id}")
    public List<Answer> selectByAuthor(User author);

    @Select("SELECT id FROM answer WHERE id > #{answerId}")
    List<Integer> selectIdOfAll(Integer answerId);

    int updateByPrimaryKeySelective(Answer record);

    int updateByPrimaryKey(Answer record);
}