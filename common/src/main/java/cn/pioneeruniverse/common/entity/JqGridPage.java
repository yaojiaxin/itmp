package cn.pioneeruniverse.common.entity;

import cn.pioneeruniverse.common.utils.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageInfo;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2018/11/1.
 */
public class JqGridPage<T extends BaseEntity> implements Serializable {
    private static final long serialVersionUID = 9081663781709034389L;

    //jqGrid请求参数
    private JqGridPrmNames jqGridPrmNames;

    /*
    * jqGrid回传参数
    **/
    private Integer page;//当前页
    private Integer total;//页码总数
    private Long records;//数据行数   总条数
    private Collection<JqGridRows<T>> rows;

    public JqGridPage() {
    }

    public JqGridPage(HttpServletRequest request, HttpServletResponse response) {
        String page = request.getParameter("page");
        JqGridPrmNames jqGridPrmNames = new JqGridPrmNames();
        if (StringUtils.isNumeric(page)) {
            jqGridPrmNames.setPage(Integer.parseInt(page));
        }
        String rows = request.getParameter("rows");
        if (StringUtils.isNumeric(rows)) {
            jqGridPrmNames.setRows(Integer.parseInt(rows));
        }
        String sidx = request.getParameter("sidx");
        if (StringUtils.isNotBlank(sidx)) {
            jqGridPrmNames.setSidx(sidx);
        }
        String sord = request.getParameter("sord");
        if (StringUtils.isNotBlank(sord)) {
            jqGridPrmNames.setSord(sord);
        }
        String search = request.getParameter("search");
        if (StringUtils.isNotBlank(search)) {
            jqGridPrmNames.setSearch(Boolean.parseBoolean(search));
        }
        String nd = request.getParameter("nd");
        if (StringUtils.isNumeric(nd)) {
            jqGridPrmNames.setNd(Long.parseLong(nd));
        }
        String totalrows = request.getParameter("totalrows");
        if (StringUtils.isNumeric(totalrows)) {
            jqGridPrmNames.setTotalrows(Integer.parseInt(totalrows));
        }
        String filters = request.getParameter("filters");
        if (StringUtils.isNotEmpty(filters)) {
            jqGridPrmNames.setFilters(filters);
        }
        this.jqGridPrmNames = jqGridPrmNames;
    }

    public void filtersAttrToEntityField(T t) throws Exception {
        if (this.getJqGridPrmNames().getFilters() != null && CollectionUtil.isNotEmpty(this.getJqGridPrmNames().getFilters().getRules())) {
            Class<? extends Object> clazz = t.getClass();
            for (JqGridPrmFiltersRule rule : this.getJqGridPrmNames().getFilters().getRules()) {
                Field field = clazz.getDeclaredField(rule.getField());
                field.setAccessible(true);
                field.set(t, ConvertUtils.convert(field.getType() == String.class ? rule.getData().toString().trim() : rule.getData(), field.getType()));
            }
        }
    }

    public T filtersToEntityField(T t) throws Exception {
        if (this.getJqGridPrmNames().getFilters() != null && CollectionUtil.isNotEmpty(this.getJqGridPrmNames().getFilters().getRules())) {
            List<JqGridPrmFiltersRule> rules = this.getJqGridPrmNames().getFilters().getRules();
            Class<? extends Object> clazz = t.getClass();
            t = (T) Class.forName(clazz.getName()).newInstance();
            for (JqGridPrmFiltersRule rule : rules) {
                Field field = clazz.getDeclaredField(rule.getField());
                field.setAccessible(true);
                field.set(t, ConvertUtils.convert(field.getType() == String.class ? rule.getData().toString().trim() : rule.getData(), field.getType()));
            }
        }
        return t;
    }

    public void processDataForResponse(PageInfo pageInfo) {
        this.setPage(pageInfo.getPageNum());
        this.setTotal(pageInfo.getPages());
        this.setRecords(pageInfo.getTotal());
        this.setCells(pageInfo.getList());

    }


    @SuppressWarnings("unchecked")
    public void setCells(Collection<T> collection) {
        if (CollectionUtils.isNotEmpty(collection)) {
            Collection<JqGridRows<T>> rowsCollection = new ArrayList<>();
            int size = collection.size();
            Object[] ts = collection.toArray();
            for (int i = 0; i < size; i++) {
                rowsCollection.add(new JqGridRows<T>((T) ts[i]));
            }
            setRows(rowsCollection);
        }
    }

    @JsonIgnore
    public JqGridPrmNames getJqGridPrmNames() {
        return jqGridPrmNames;
    }

    public void setJqGridPrmNames(JqGridPrmNames jqGridPrmNames) {
        this.jqGridPrmNames = jqGridPrmNames;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


    public Long getRecords() {
        return records;
    }

    public void setRecords(Long records) {
        this.records = records;
    }

    public Collection<JqGridRows<T>> getRows() {
        return rows;
    }

    public void setRows(Collection<JqGridRows<T>> rows) {
        this.rows = rows;
    }
}
