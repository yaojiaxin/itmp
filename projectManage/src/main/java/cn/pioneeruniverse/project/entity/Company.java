package cn.pioneeruniverse.project.entity;


import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_company_info")
public class Company extends BaseEntity {

    private static final long serialVersionUID = -5057265545421924569L;

    private String companyName;
    private String companyShortName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyName='" + companyName + '\'' +
                ", companyShortName='" + companyShortName + '\'' +
                '}';
    }
}
