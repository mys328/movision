package com.zhuhuibao.business.member.mc;

import com.wordnik.swagger.annotations.ApiOperation;
import com.zhuhuibao.common.Response;
import com.zhuhuibao.common.constant.MsgCodeConstant;
import com.zhuhuibao.common.util.ShiroUtil;
import com.zhuhuibao.exception.AuthException;
import com.zhuhuibao.mybatis.expo.service.ExpoService;
import com.zhuhuibao.mybatis.memCenter.service.SuccessCaseService;
import com.zhuhuibao.mybatis.witkey.service.CooperationService;
import com.zhuhuibao.utils.MsgPropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by cxx on 2016/6/29 0029.
 */
@RestController
@RequestMapping("/rest/member/mc/index")
public class MemberIndexController {

    @Autowired
    private ExpoService exhibitionService;

    @Autowired
    SuccessCaseService successCaseService;

    @Autowired
    private CooperationService cooperationService;

    @ApiOperation(value = "宣传推广相关信息", notes = "宣传推广相关信息", response = Response.class)
    @RequestMapping(value = "sel_campaign_info_size", method = RequestMethod.GET)
    public Response sel_campaign_info_size() {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        Map map = new HashMap();
        if(memberId!=null){
            Map<String,Object> exhibitionMap = new HashMap<>();
            exhibitionMap.put("type",3);
            exhibitionMap.put("createId", String.valueOf(memberId));
            int size1 = exhibitionService.queryMyExhibitionListSize(exhibitionMap);

            Map<String,Object> successCaseMap = new HashMap<>();
            exhibitionMap.put("createId", String.valueOf(memberId));
            int size2 = successCaseService.queryMySuccessCaseListSize(successCaseMap);

            map.put("exhibitionSize",size1);
            map.put("successCaseSize",size2);

            result.setData(map);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }

    @ApiOperation(value = "威客信息", notes = "威客信息", response = Response.class)
    @RequestMapping(value = "sel_witkey_info_size", method = RequestMethod.GET)
    public Response sel_witkey_info_size() {
        Response result = new Response();
        Long memberId = ShiroUtil.getCreateID();
        Map map = new HashMap();
        if(memberId!=null){
            Map<String,Object> taskMap = new HashMap<>();
            taskMap.put("parentId",1);
            taskMap.put("createId", String.valueOf(memberId));
            int size1 = cooperationService.queryMyWitkeyListSize(taskMap);

            Map<String,Object> serviceMap = new HashMap<>();
            serviceMap.put("parentId",2);
            serviceMap.put("createId", String.valueOf(memberId));
            int size2 = cooperationService.queryMyWitkeyListSize(serviceMap);

            Map<String,Object> cooperationMap = new HashMap<>();
            cooperationMap.put("parentId",3);
            cooperationMap.put("createId", String.valueOf(memberId));
            int size3 = cooperationService.queryMyWitkeyListSize(cooperationMap);

            map.put("taskSize",size1);
            map.put("serviceSize",size2);
            map.put("cooperationSize",size3);

            result.setData(map);
        }else {
            throw new AuthException(MsgCodeConstant.un_login, MsgPropertiesUtils.getValue(String.valueOf(MsgCodeConstant.un_login)));
        }
        return result;
    }
}
