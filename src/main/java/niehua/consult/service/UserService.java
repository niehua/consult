package niehua.consult.service;

import niehua.consult.entity.User;
import niehua.consult.mapper.MessageMapper;
import niehua.consult.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description: 用户业务层
 * @author: sheep
 * @date: create in 21:39 2018/7/19
 */

@Service
@Transactional
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MessageMapper messageMapper;

    /*
    根据用户id，返回用户信息
     */
    public User getIntactUserByUserId(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return user;
    }

    /*
    若是新用户即user表中没有该用户，若含有该用户，则检查将用户信息刷新，并设置用户最后登录时间
     */
    public void loginByUserId(String visitorId, String nickname, String province, String headImgUrl, Byte sex) {
        User user = new User();
        user.setId(visitorId);
        user.setNickname(nickname);
        user.setProvince(province);
        user.setHeadImgUrl(headImgUrl);
        user.setSex(sex);
        if (userMapper.selectByPrimaryKey(visitorId) == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    /*
    更新用户最后一次登录时间
     */
    public void updateLoginLastTimeByUserId(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        user.setLoginLastTime(null);
        userMapper.updateByPrimaryKey(user);
    }
}
