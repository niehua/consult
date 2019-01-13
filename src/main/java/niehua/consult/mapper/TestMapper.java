package niehua.consult.mapper;


import niehua.consult.entity.Test;

public interface TestMapper {
    int insert(Test record);

    int insertSelective(Test record);
}