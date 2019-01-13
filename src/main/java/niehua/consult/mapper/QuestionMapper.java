package niehua.consult.mapper;


import niehua.consult.entity.Question;
import niehua.consult.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 访问question表
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
@Mapper
public interface QuestionMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Integer id);

    @Select("SELECT * FROM question WHERE author_id = #{id} ORDER BY id DESC")
    List<Question> selectByAuthorId(User author);

/*    @Select("SELECT * FROM question WHERE content LIKE #{keyword}")
    List<Question> selectByKeyword(String keyword);*/

    @Select("SELECT * FROM question WHERE answer_number = 0  ORDER BY id DESC")
    List<Question> selectEmptyQuestion();

    @Select("SELECT id FROM question WHERE id > #{questionId}")
    List<Integer> selectIdOfAll(Integer questionId);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKey(Question record);
}