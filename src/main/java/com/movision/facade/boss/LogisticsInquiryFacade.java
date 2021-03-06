package com.movision.facade.boss;

import com.movision.mybatis.afterservice.entity.Afterservice;
import com.movision.mybatis.bossOrders.servic.BossOrderService;
import com.movision.mybatis.invoice.entity.Invoice;
import com.movision.mybatis.invoice.entity.InvoiceVo;
import com.movision.mybatis.orderLogistics.entity.OrderLogistics;
import com.movision.mybatis.orderSubLogistics.entity.OrderSubLogistics;
import com.movision.utils.HttpRequest;
import com.movision.utils.LogisticsMD5;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhanglei
 * @Date 2017/3/17 17:35
 */

@Service
public class LogisticsInquiryFacade {

    private static Logger log = LoggerFactory.getLogger(LogisticsInquiryFacade.class);

    @Autowired
    private BossOrderService orderService;


    public Map<String, Object> LogisticInquiry(String ordernumber, int type) {
        Map<String, String> map = new HashMap<>();
        String logisticsid = "";
        String logisticscode = "";
        if (type == 2) {//订单快递类型
            logisticsid = orderService.queryLogisticsid(ordernumber);//快递单号
            logisticscode = orderService.queryLogisticsCode(logisticsid);//物流code
        }
        if (type == 0) {//用户退回类型
            Afterservice afterservice = orderService.queryReturnLogistics(ordernumber);
            Integer logisticsS = afterservice.getLogisticsway();
            logisticsid = afterservice.getReturnnumber();
            logisticscode = orderService.queryReturnWay(logisticsS);//物流code
        }
        if (type == 1) {//换货二次发货类型
            logisticsid = orderService.queryReplaceLogistic(ordernumber);
            logisticscode = orderService.queryReplaceCode(logisticsid);//物流code
        }
        if (type == 3) {//发票邮寄快递类型
            InvoiceVo vo = orderService.queryInvoiceByOrderNumber(ordernumber);
            if (null != vo.getLogisticsnumber()) {
                logisticsid = vo.getLogisticsnumber();
                logisticscode = vo.getLogisticscode();
            }
        }
        String param = "{" + "\"com\":\"" + logisticscode + "\"," + //查询的快递公司的编码
                "\"num\":\"" + logisticsid + "\"" + //快递单号
                "}";
        String key = "EvNEkbNX6345";
        String customer = "FA0777963476EFDFE72EE58900D6E89A";
        String sign = LogisticsMD5.encode(param + key + customer);
        map.put("param", param);
        map.put("sign", sign);
        map.put("customer", customer);
        String resp;
        Map<String, Object> backmap = new HashedMap();
        try {
            resp = new HttpRequest().postData("http://poll.kuaidi100.com/poll/query.do", map, "utf-8").toString();
            System.out.println(resp);
            JSONObject jsonObject = JSONObject.fromObject(resp);
            String message = jsonObject.get("message").toString();
            JSONObject row = null;
            String com = "";
            String state = "";
            if (message.equals("ok")) {
                state = jsonObject.get("state").toString();
                com = jsonObject.get("com").toString();
                backmap.put("message", message);
                backmap.put("state", state);
                backmap.put("com", com);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                backmap.put("jsonArray", jsonArray);
                String company = orderService.logisticsCompany(com);
                int result = 0;
                Date isessencetime = null;//开始时间
                Date cetime = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                OrderLogistics orderlogistics = new OrderLogistics();
                OrderSubLogistics ordersub = new OrderSubLogistics();
                orderlogistics.setLogisticstatue(Integer.parseInt(state));
                orderlogistics.setLogisticsname(company);
                orderlogistics.setIsdel(0);
                result = orderService.addLogistics(orderlogistics);
                int ida = orderlogistics.getId();
                String time = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    row = jsonArray.getJSONObject(i);
                    if (state.equals("3") && i == 0) {
                        String ftime = row.get("ftime").toString();
                        time = ftime;
                        if (time != null) {
                            try {
                                cetime = format.parse(time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String ftime = row.get("ftime").toString();
                    if (ftime != null) {
                        try {
                            isessencetime = format.parse(ftime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    String context = row.get("context").toString();
                    ordersub.setLogisticsid(ida);
                    ordersub.setInfo(context);
                    ordersub.setIntime(isessencetime);
                    ordersub.setOrderid(jsonArray.size() - i);
                    ordersub.setIsdel(0);
                    result = orderService.addSubLogistics(ordersub);

                }
                if (!time.equals("")) {
                    orderlogistics.setId(ida);
                    orderlogistics.setIntime(cetime);
                    result = orderService.updateLogistics(orderlogistics);
                }

            }
            String results = "";
            String returnCode = "";
            if (!message.equals("ok")) {
                results = jsonObject.get("result").toString();
                returnCode = jsonObject.get("returnCode").toString();
                backmap.put("message", message);
                backmap.put("results", results);
                backmap.put("returnCode", returnCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return backmap;
    }


}
