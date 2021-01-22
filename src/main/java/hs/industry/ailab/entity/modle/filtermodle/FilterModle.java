package hs.industry.ailab.entity.modle.filtermodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.filter.Filter;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/11 12:48
 */
public class FilterModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(FilterModle.class);




    /**
     * memery
     */
    private boolean iscomplete = false;
    private String datasource;
    private Map<Integer,BaseModlePropertyImp> indexproperties;//key=modleid

    @Override
    public JSONObject inprocess(Project project) {
        return null;
    }

    @Override
    public JSONObject computeprocess(Project project) {
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {

    }

    @Override
    public void init() {
        indexproperties = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }
    }




    /**db*/
    private Filter filter;
    private List<ModleProperty> propertyImpList;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public boolean isIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<Integer, BaseModlePropertyImp> getIndexproperties() {
        return indexproperties;
    }

    public void setIndexproperties(Map<Integer, BaseModlePropertyImp> indexproperties) {
        this.indexproperties = indexproperties;
    }
}
