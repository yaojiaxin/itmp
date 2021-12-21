package cn.pioneeruniverse.project.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.common.utils.CommonUtil;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@TableName("tbl_asset_business_system")
public class TblAssetBusinessSystem extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assetBusinessTreeId; // 资产业务树表ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long assetSystemTreeId;// 资产系统树表ID

    public Long getAssetBusinessTreeId() {
        return assetBusinessTreeId;
    }

    public TblAssetBusinessSystem(){}

    public TblAssetBusinessSystem(HttpServletRequest request){
        Long currentUserId = CommonUtil.getCurrentUserId(request);
        long time = System.currentTimeMillis();
        this.setCreateBy(currentUserId);
        this.setLastUpdateBy(currentUserId);
        this.setCreateDate(new Timestamp(time));
        this.setLastUpdateDate(new Timestamp(time));
    }

    public void setAssetBusinessTreeId(Long assetBusinessTreeId) {
        this.assetBusinessTreeId = assetBusinessTreeId;
    }

    public Long getAssetSystemTreeId() {
        return assetSystemTreeId;
    }

    public void setAssetSystemTreeId(Long assetSystemTreeId) {
        this.assetSystemTreeId = assetSystemTreeId;
    }
}