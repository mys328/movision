package com.movision.mybatis.goods.mapper;

import com.movision.mybatis.combo.entity.Combo;
import com.movision.mybatis.combo.entity.ComboVo;
import com.movision.mybatis.goods.entity.*;
import com.movision.mybatis.goodsAssessment.entity.GoodsAssessment;
import com.movision.mybatis.goodsAssessment.entity.GoodsAssessmentVo;
import com.movision.mybatis.goodscombo.entity.GoodsCombo;
import com.movision.mybatis.goodscombo.entity.GoodsComboDetail;
import com.movision.mybatis.goodscombo.entity.GoodsComboVo;
import com.movision.mybatis.subOrder.entity.SubOrder;
import org.apache.ibatis.session.RowBounds;
import org.apache.taglibs.standard.lang.jstl.Literal;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Integer id);

    List<GoodsVo> queryActiveGoods(int postid);

    List<GoodsVo> queryMonthHot();

    List<GoodsVo> queryDefaultGoods(Map<String, Object> parammap);

    List<GoodsVo> queryWeekHot();

    List<GoodsVo> findAllMonthHot(RowBounds rowBounds);

    List<GoodsVo> findAllWeekHot(RowBounds rowBounds);

    List<GoodsVo> queryLastDayGodList();

    List<GoodsVo> findAllGodRecommend(RowBounds rowBounds);

    List<GoodsVo> queryEssenceGoods();

    List<GoodsVo> queryHotGoods();

    GoodsDetail queryGoodDetail(int goodsid);

    List<GoodsImg> queryGoodsImgList(int goodsid);

    int queryStore(int goodsid);

    List<GoodsVo> queryComboGoodsList(int comboid);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<GoodsVo> findAllGoodsList(RowBounds rowBounds);//查询商品列表

    int deleteGoods(Integer id);//删除商品

    int deleteAssessment(Integer id);//删除评价
    int AddToGoods(Integer id);//上架

    int queryisdel(Integer id);
    int DownToGoods(Integer id);//下架
    List<GoodsVo> findAllGoodsCondition(Map map, RowBounds rowBounds);//条件查询

    int recommendHot(Integer id);//推荐到热门

    int recommendisessence(Integer id);//推荐到精选

    int updateDate(GoodsVo goodsVo);//修改推荐日期

    List<GoodsVo> findAllQueryPostByGoodsList(RowBounds rowBounds);//查询商品列表（帖子使用）

    List<Map> findAllMyCollectGoodsList(RowBounds rowBounds, Map<String, Object> map);//查询我收藏的商品列表

    List<GoodsVo> findAllQueryLikeGoods(Map map, RowBounds rowBounds);//查询商品列表，联合搜索（帖子使用）

    List<GoodsComboVo> findAllComboT(RowBounds rowBounds);//查询套餐列表

    List<GoodsComboVo> findAllComCondition(Map map, RowBounds rowBounds);//套餐条件搜索

    List<GoodsVo> findAllType();//查询商品分类

    List<GoodsVo> findAllBrand();//查看品牌

    GoodsVo findGoodDetail(Integer id);//商品详情

    int updateGoods(GoodsVo goodsVo);//修改商品

    int updateImage(Map map);//修改图片

    int updateCom(Integer id);//取消推荐

    Goods todayCommend(Integer id);//今日推荐

    List<GoodsAssessmentVo> findAllAssessment(RowBounds rowBounds, String goodsid);//查询评价列表

    List<GoodsAssessmentVo> findAllAssessmentCondition(Map map, RowBounds rowBounds);//条件查询商品

    GoodsAssessmentVo queryAssessmentRemark(Integer id);//评论详情

    GoodsImg queryImgGoods(Integer goodsid);//商品参数图

    GoodsImg queryCommodityDescription(Integer goodsid);//商品描述图

    List<GoodsImg> queryAllGoodsPicture(Integer goodsid);//商品图片

    List<GoodsImg> queryblueprint(Integer id);//晒图

    int deleteGoodsPicture(Integer id);//删除图片

    int addAssessment(GoodsAssessment assessment);//回复评论

    int queryAssessment(Integer pid);//查询是否有评价过


    int updateImgGoods(GoodsImg goodsImg);//修改参数图

    int updateCommodityDescription(GoodsImg img);//修改描述图

    int addPicture(GoodsImg goodsImg);//增加图片

    int deletebanner(String goodsid);
    int addGoodsPic(GoodsImg goodsImg);//增加商品图片
    int addGoods(GoodsVo goodsVo);//增加商品

    List<GoodsComboVo> findAllC(Integer comboid);

    List<GoodsComboVo> queryName(Integer comboid);//根据id查询所有商品

    int deleteComGoods(Integer comboid);//删除套餐

    int queryByCom(Integer comboid);//根据id查询套餐内是否有商品

    List<GoodsComboVo> findByIdCom(Integer comboid);//根据id查询详情

    List<GoodsCom> findAllGoods(Integer comboid);//根据套餐id查询商品信息

    Integer queryAllStock(Integer comboid);//根据套餐id查询最小库存

    Integer updateComDetail(Combo goodsCombo);//修改套餐详情

    Integer addCom(Combo combo);//增加套餐

    Integer addComGoods(GoodsCombo goodsCombo);//根据id插入商品


    Integer addImgGoods(GoodsImg goodsImg);//增加参数图

    Integer addCommodityDescription(GoodsImg goodsImg);//增加描述图

    List<Integer> findcomboid();//查询套餐id

    List<GoodsVo> findAllQueryCollectionGoodsListByUserid(String goodsid, RowBounds rowBounds);

    List<GoodsImg> queryBannerImg(String goodsid);//查询banner图

    void deductStock(List<SubOrder> subOrderList);

    Integer delectAllComboGoods(Integer goodsid);//批量删除商品
}