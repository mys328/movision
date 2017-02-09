package com.movision.facade.boss;

import com.movision.mybatis.accusation.service.AccusationService;
import com.movision.mybatis.circle.service.CircleService;
import com.movision.mybatis.comment.entity.Comment;
import com.movision.mybatis.comment.entity.CommentVo;
import com.movision.mybatis.comment.service.CommentService;
import com.movision.mybatis.period.entity.Period;
import com.movision.mybatis.period.service.PeriodService;
import com.movision.mybatis.post.entity.Post;
import com.movision.mybatis.post.entity.PostList;
import com.movision.mybatis.post.service.PostService;
import com.movision.mybatis.rewarded.entity.Rewarded;
import com.movision.mybatis.rewarded.entity.RewardedVo;
import com.movision.mybatis.rewarded.service.RewardedService;
import com.movision.mybatis.share.service.SharesService;
import com.movision.mybatis.user.entity.User;
import com.movision.mybatis.user.service.UserService;
import com.movision.utils.pagination.model.Paging;
import com.movision.utils.pagination.util.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author zhurui
 * @Date 2017/2/7 9:16
 */
@Service
public class PostFacade {
    @Autowired
    PostService postService;

    @Autowired
    CircleService circleService;

    @Autowired
    UserService userService;

    @Autowired
    SharesService sharesService;

    @Autowired
    RewardedService rewardedService;

    @Autowired
    AccusationService accusationService;

    @Autowired
    CommentService commentService;

    @Autowired
    PeriodService periodService;


    /**
     * 后台管理-查询帖子列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Map<String, Object> queryPostByList(String pageNo, String pageSize) {
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Post> pager = new Paging<Post>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List<Post> list = postService.queryPostByList(pager);
        Map<String, Object> map = new HashedMap();
        Integer num = postService.queryPostNum();//查询帖子总数
        map.put("total", num);
        List<Object> rewardeds = new ArrayList<Object>();
        for (int i = 0; i < list.size(); i++) {
                PostList postList = new PostList();
            Integer id = list.get(i).getId();
                Integer circleid = list.get(i).getCircleid();//获取到圈子id
            String nickname = userService.queryUserByNickname(circleid);//获取发帖人
            Integer share = sharesService.querysum(id);//获取分享数
            Integer rewarded = rewardedService.queryRewardedBySum(id);//获取打赏积分
            Integer accusation = accusationService.queryAccusationBySum(id);//查询帖子举报次数
            postList.setId(list.get(i).getId());
                postList.setTitle(list.get(i).getTitle());
                postList.setNickname(nickname);
                postList.setCollectsum(list.get(i).getCollectsum());
                postList.setShare(share);
                postList.setCommentsum(list.get(i).getCommentsum());
                postList.setZansum(list.get(i).getZansum());
                postList.setRewarded(rewarded);
                postList.setAccusation(accusation);
                postList.setIsessence(list.get(i).getIsessence());
                rewardeds.add(postList);

        }
        map.put("list", rewardeds);
        return map;
    }

    /**
     * 后台管理-帖子列表-发帖人信息
     *
     * @param postid
     * @return
     */
    public User queryPostByPosted(String postid) {
        Integer circleid = postService.queryPostByCircleid(postid);
        String phone = circleService.queryCircleByPhone(circleid);//获取圈子中的用户手机号
        User user = userService.queryUser(phone);
        return user;
    }

    /**
     * 后台管理-帖子列表-删除帖子
     *
     * @param postid
     * @return
     */
    public Map<String, Integer> deletePost(String postid) {
        int resault = postService.deletePost(Integer.parseInt(postid));
        Map<String, Integer> map = new HashedMap();
        map.put("resault", resault);
        return map;
    }

    /**
     * 后台管理-帖子列表-查看评论
     *
     * @param pageNo
     * @param pageSize
     * @param postid
     * @return
     */
    public List<CommentVo> queryPostAppraise(String postid, String pageNo, String pageSize) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<CommentVo> pager = new Paging<CommentVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List<CommentVo> list = commentService.queryComments(postid, pager);
        return list;
    }

    /**
     * 删除帖子评论
     *
     * @param id
     * @return
     */
    public int deletePostAppraise(String id) {
        return commentService.deletePostAppraise(Integer.parseInt(id));
    }


    /**
     * 帖子打赏列表
     *
     * @param postid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<RewardedVo> queryPostAward(String postid, String pageNo, String pageSize) {
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<RewardedVo> pager = new Paging<RewardedVo>(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        return rewardedService.queryPostAward(Integer.parseInt(postid), pager);//分页返回帖子打赏列表
    }

    /**
     * 帖子预览
     *
     * @param postid
     * @return
     */
    public Post queryPostParticulars(String postid) {
        return postService.queryPostParticulars(Integer.parseInt(postid));
    }

    public int addPost(String title, String type, String circleid,
                       String coverimg, String postcontent, String isessence, String time, String begintime, String endtime) {
        Post post = new Post();
        Period period = new Period();
        post.setTitle(title);
        post.setType(Integer.parseInt(type));
        post.setCircleid(Integer.parseInt(circleid));
        if (coverimg != null) {//判断传入的值是否为空
            post.setCoverimg(coverimg);
        }
        post.setPostcontent(postcontent);
        if (isessence != null) {
            post.setIsessence(Integer.parseInt(isessence));
        }
        post.setIntime(new Date());
        Calendar c = Calendar.getInstance();//将字符串数据转换为毫秒值
        Date be = null;
        Date en = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            be = format.parse(begintime);
            en = format.parse(endtime);
        } catch (ParseException e) {
        }
        int p = postService.addPost(post);//添加帖子/活动
        Integer postid = post.getId();//获取帖子id
        period.setPostid(postid);
        period.setBegintime(be);
        period.setEndtime(en);
        int per = periodService.insertPostRecord(period);//设置活动时间
        return per;

    }

/*    *//**
     * 帖子按条件查询
     * @param title
     * @param circleid
     * @param name
     * @param date
     * @return
     *//*
    public List<Object> postSearch(String title, String circleid, String name, Date date,String pageNo,String pageSize){
        if (title!=null&&circleid!=null&&name!=null&&date!=null){//当没有添加条件的情况下执行全部搜索
            Map<String ,Object> map=new HashedMap();
            map.put("title",title);
            map.put("circleid",circleid);
            map.put("name",name);
            map.put("date",date);
            return postService.postSearch(map);
        }else{
            return queryPostByList(pageNo,pageSize);
        }

    }*/

}
