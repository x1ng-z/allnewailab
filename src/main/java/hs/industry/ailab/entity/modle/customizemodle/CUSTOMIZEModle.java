package hs.industry.ailab.entity.modle.customizemodle;

import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.ModleProperty;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:28
 */
public class CUSTOMIZEModle extends BaseModleImp {
    /****db****/
    private String customizepyname;
    /*********/
    private List<ModleProperty> propertyImpList;



    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public String getCustomizepyname() {
        return customizepyname;
    }

    public void setCustomizepyname(String customizepyname) {
        this.customizepyname = customizepyname;
    }
}
