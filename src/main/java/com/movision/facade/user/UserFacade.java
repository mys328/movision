package com.movision.facade.user;

import com.movision.aop.UserSaveCache;
import com.movision.mybatis.post.entity.ActiveVo;
import com.movision.mybatis.post.entity.PostVo;
import com.movision.mybatis.post.service.PostService;
import com.movision.mybatis.user.entity.RegisterUser;
import com.movision.mybatis.user.entity.User;
import com.movision.mybatis.user.entity.UserVo;
import com.movision.mybatis.user.service.UserService;
import com.movision.utils.pagination.model.Paging;
import org.apache.commons.lang3.StringUtils;
import com.movision.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * APP用户 facade
 * @Author zhuangyuhao
 * @Date 2017/1/24 20:20
 */
@Service
public class UserFacade {

    private static Logger log = LoggerFactory.getLogger(UserFacade.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Cacheable(value = "is_exist_account", key = "'login_'+#phone")
    public int isExistAccount(String phone) {
        return userService.isExistAccount(phone);
    }

    public int registerAccount(RegisterUser registerUser) {
        return userService.registerAccount(registerUser);
    }

    public UserVo queryUserInfo(String userid) {
        return userService.queryUserInfo(Integer.parseInt(userid));
    }

    public List<PostVo> personPost(String userid, String pageNo, String pageSize) {
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<PostVo> pager = new Paging<PostVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        return userService.personPost(pager, Integer.parseInt(userid));
    }

    public List<ActiveVo> personActive(String userid, String pageNo, String pageSize) {
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<ActiveVo> pager = new Paging<ActiveVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List<ActiveVo> activeVoList = userService.personActive(pager, Integer.parseInt(userid));

        for (int i = 0; i < activeVoList.size(); i++) {
            //遍历计算活动累计参与总人数
            int postid = activeVoList.get(i).getId();//取出活动id
            int partsum = postService.queryActivePartSum(postid);
            activeVoList.get(i).setPartsum(partsum);
            //遍历计算活动距结束天数
            Date begin = activeVoList.get(i).getBegintime();//活动开始时间
            Date end = activeVoList.get(i).getEndtime();//活动结束时间
            Date now = new Date();//当前时间
            int enddays = DateUtils.activeEndDays(now, begin, end);
            activeVoList.get(i).setEnddays(enddays);
        }
        return activeVoList;
    }

    /**
     * 测试使用
     *
     * @param user
     * @return
     */
    //@CachePut(value = "user", key = "'id_'+#user.getId()")
    /*@Caching(put = {
            @CachePut(value = "user", key = "'user_id_'+#user.id"),
            @CachePut(value = "user", key = "'user_username_'+#user.username"),
            @CachePut(value = "user", key = "'user_phone_'+#user.phone")
    })*/
    @UserSaveCache  //自定义缓存注解
    public User addUser(User user) {
        System.out.println("addUser,@CachePut注解方法被调用啦。。。。。");
        return user;
    }

    /**
     * 测试使用
     *
     * @param id
     * @return
     */
    @Cacheable(value = "user", key = "'id_'+#id") //,key="'user_id_'+#id"
    public User getUserByID(Integer id) {
        System.out.println("@Cacheable注解方法被调用啦。。。。。");
        User user = new User();
        user.setId(123);
        user.setPhone("18051989558");
        user.setNickname("zyh");
        return user;
    }

    /**
     * 测试使用
     *
     * @param user
     * @return
     */
    @CachePut(value = "user", key = "'users_'+#user.getId()")
    public List<User> getUsers(User user) {
        System.out.println("@CachePut注解方法被调用啦。。。。。");
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 10; i++) {
            User temp = new User();
            temp.setNickname("username" + i);
            users.add(temp);
        }
        return users;
    }


}
