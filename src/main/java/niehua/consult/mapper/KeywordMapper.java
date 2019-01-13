package niehua.consult.mapper;

import niehua.consult.entity.Keyword;
import niehua.consult.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 访问keyword表
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
@Mapper
public interface KeywordMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Keyword record);

    int insertSelective(Keyword record);

    Keyword selectByPrimaryKey(Integer id);

/*    @Select("SELECT * FROM keyword WHERE answer_id = #{id}")
    List<Keyword> selectByAnswer(Answer answer);*/

    @Select("SELECT * FROM keyword WHERE question_id = #{id}")
    List<Keyword> selectByQuestion(Question question);

    @Select("SELECT * FROM keyword WHERE content LIKE #{keywordContent}")
    List<Keyword> selectByKeyword(String keywordContent);

    @Select("SELECT weight FROM keyword WHERE question_id = #{questionId} AND content LIKE #{keywordContent}")
    List<Float> selectWeightOfQuestionForKeyword(Integer questionId, String keywordContent);

    int updateByPrimaryKeySelective(Keyword record);

    int updateByPrimaryKeyWithBLOBs(Keyword record);

    int updateByPrimaryKey(Keyword record);
}