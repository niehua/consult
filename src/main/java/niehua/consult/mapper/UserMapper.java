package niehua.consult.mapper;


import niehua.consult.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 访问user表
 * @author: sheep
 * @date: create in 14:57 2018/7/19
 */
@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}