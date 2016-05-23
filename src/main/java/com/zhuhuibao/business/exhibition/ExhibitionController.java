package com.zhuhuibao.business.exhibition;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zhuhuibao.common.JsonResult;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.mybatis.memCenter.entity.Exhibition;
import com.zhuhuibao.mybatis.memCenter.entity.MeetingOrder;
import com.zhuhuibao.mybatis.memCenter.service.ExhibitionService;
import com.zhuhuibao.shiro.realm.OMSRealm;
import com.zhuhuibao.shiro.realm.ShiroRealm;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import com.zhuhuibao.utils.pagination.model.Paging;
import com.zhuhuibao.utils.pagination.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会展接口类
 * Created by cxx on 2016/5/11 0011.
 */
@RestController
@RequestMapping("/rest/exhibition")
@Api(value="Exhibition", description="会展")
public class ExhibitionController {
    private static final Logger log = LoggerFactory.getLogger(ExhibitionController.class);

    @Autowired
    private ExhibitionService exhibitionService;

    /**
     * 发布会展定制
     */
    @ApiOperation(value="发布会展定制",notes="发布会展定制",response = JsonResult.class)
    @RequestMapping(value = "publishMeetingOrder", method = RequestMethod.POST)
    public JsonResult publishMeetingOrder(MeetingOrder meetingOrder) throws Exception {
        JsonResult jsonResult = new JsonResult();
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession(false);
        if(null != session) {
            ShiroRealm.ShiroUser principal = (ShiroRealm.ShiroUser)session.getAttribute("member");
            if(null != principal){
                meetingOrder.setCreateid(principal.getId().toString());
                exhibitionService.publishMeetingOrder(meetingOrder);
            }else{
                jsonResult.setCode(401);
                jsonResult.setMessage(MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
                jsonResult.setMsgCode(MsgCodeConstant.un_login);
            }
        }else{
            jsonResult.setCode(401);
            jsonResult.setMessage(MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
            jsonResult.setMsgCode(MsgCodeConstant.un_login);
        }
        return jsonResult;
    }

    /**
     * 会展定制申请处理
     */
    @ApiOperation(value="会展定制申请处理",notes="会展定制申请处理",response = JsonResult.class)
    @RequestMapping(value = "updateMeetingOrderStatus", method = RequestMethod.POST)
    public JsonResult updateMeetingOrderStatus(MeetingOrder meetingOrder) throws Exception {
        JsonResult jsonResult = new JsonResult();
        exhibitionService.updateMeetingOrderStatus(meetingOrder);
        return jsonResult;
    }

    /**
     * 会展定制查看
     */
    @ApiOperation(value="会展定制查看",notes="会展定制查看",response = JsonResult.class)
    @RequestMapping(value = "queryMeetingOrderInfoById", method = RequestMethod.GET)
    public JsonResult queryMeetingOrderInfoById(@RequestParam String id) throws Exception {
        JsonResult jsonResult = new JsonResult();
        MeetingOrder meetingOrder = exhibitionService.queryMeetingOrderInfoById(id);
        jsonResult.setData(meetingOrder);
        return jsonResult;
    }

    /**
     * 会展定制申请管理
     */
    @ApiOperation(value="会展定制申请管理",notes="会展定制申请管理",response = JsonResult.class)
    @RequestMapping(value = "findAllMeetingOrderInfo", method = RequestMethod.GET)
    public JsonResult findAllMeetingOrderInfo(@ApiParam(value = "账号")@RequestParam(required = false) String account,
        @ApiParam(value = "省")@RequestParam(required = false)String province,@ApiParam(value = "市")@RequestParam(required = false)String city,
        @ApiParam(value = "审核状态")@RequestParam(required = false)String status,@RequestParam(required = false)String pageNo,@RequestParam(required = false)String pageSize) throws Exception {
        JsonResult jsonResult = new JsonResult();
        //设定默认分页pageSize
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<MeetingOrder> pager = new Paging<MeetingOrder>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        Map<String,Object> map = new HashMap<>();
        //查询传参
        map.put("account",account);
        map.put("province",province);
        map.put("city",city);
        map.put("status",status);
        //查询
        List<MeetingOrder> meetingOrderList = exhibitionService.findAllMeetingOrderInfo(pager,map);
        pager.result(meetingOrderList);
        jsonResult.setData(pager);
        return jsonResult;
    }

    /**
     * 发布会展信息
     */
    @ApiOperation(value="发布会展信息",notes="发布会展信息",response = JsonResult.class)
    @RequestMapping(value = "publishExhibition", method = RequestMethod.POST)
    public JsonResult publishExhibition(Exhibition exhibition) throws Exception {
        JsonResult jsonResult = new JsonResult();
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession(false);
        //判断是否登陆
        if(null != session) {
            if("1".equals(exhibition.getCreaterType())) {
                OMSRealm.ShiroOmsUser principal = (OMSRealm.ShiroOmsUser) session.getAttribute("oms");
                if(null != principal){
                    exhibition.setCreateid(principal.getId().toString());
                    exhibitionService.publishExhibition(exhibition);
                }else{
                    jsonResult.setCode(401);
                    jsonResult.setMessage(MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
                    jsonResult.setMsgCode(MsgCodeConstant.un_login);
                }
            }else{
                ShiroRealm.ShiroUser principal = (ShiroRealm.ShiroUser)session.getAttribute("member");
                if(null != principal){
                    exhibition.setCreateid(principal.getId().toString());
                    exhibitionService.publishExhibition(exhibition);
                }else{
                    jsonResult.setCode(401);
                    jsonResult.setMessage(MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
                    jsonResult.setMsgCode(MsgCodeConstant.un_login);
                }
            }
        }else{
            jsonResult.setCode(401);
            jsonResult.setMessage(MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
            jsonResult.setMsgCode(MsgCodeConstant.un_login);
        }
        return jsonResult;
    }

    /**
     * 会展信息列表
     */
    @ApiOperation(value="会展信息列表",notes="会展信息列表",response = JsonResult.class)
    @RequestMapping(value = "findAllExhibition", method = RequestMethod.GET)
    public JsonResult findAllExhibition(@ApiParam(value = "标题")@RequestParam(required = false)String title,
        @ApiParam(value = "所属栏目")@RequestParam(required = false)String type,@ApiParam(value = "审核状态")@RequestParam(required = false)String status,
        @ApiParam(value = "省")@RequestParam(required = false)String province,
        @ApiParam(value = "区别后台与前台频道")@RequestParam(required = false)String type1,
        @RequestParam(required = false)String pageNo,@RequestParam(required = false)String pageSize) throws Exception {
        JsonResult jsonResult = new JsonResult();
        //设定默认分页pageSize
        if (StringUtils.isEmpty(pageNo)) {
            pageNo = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "10";
        }
        Paging<Exhibition> pager = new Paging<Exhibition>(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
        Map<String,Object> map = new HashMap<>();
        //查询传参
        map.put("title",title);
        map.put("type",type);
        map.put("status",status);
        map.put("province",province);
        map.put("type1",type1);
        //查询
        List<Exhibition> exhibitionList = exhibitionService.findAllExhibition(pager,map);
        pager.result(exhibitionList);
        jsonResult.setData(pager);
        return jsonResult;
    }

    /**
     * 会展详情查看
     */
    @ApiOperation(value="会展详情查看",notes="会展详情查看",response = JsonResult.class)
    @RequestMapping(value = "queryExhibitionInfoById", method = RequestMethod.GET)
    public JsonResult queryExhibitionInfoById(@RequestParam String id) throws Exception {
        JsonResult jsonResult = new JsonResult();
        Exhibition exhibition = exhibitionService.queryExhibitionInfoById(id);
        jsonResult.setData(exhibition);
        return jsonResult;
    }

    /**
     * 会展信息编辑更新
     */
    @ApiOperation(value="会展信息编辑更新",notes="会展信息编辑更新",response = JsonResult.class)
    @RequestMapping(value = "updateExhibitionInfoById", method = RequestMethod.POST)
    public JsonResult updateExhibitionInfoById(Exhibition exhibition) throws Exception {
        JsonResult jsonResult = new JsonResult();
        exhibitionService.updateExhibitionInfoById(exhibition);
        return jsonResult;
    }
}
