package hs.industry.ailab.entity.modle.filtermodle;

import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.filter.Filter;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/11 12:48
 */
public class FilterModle extends BaseModleImp {
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
}
