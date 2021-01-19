package hs.industry.ailab.entity.modle.iomodle;

import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:20
 */
public class OUTModle extends BaseModleImp {
    private List<ModleProperty> propertyImpList;

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }
}
