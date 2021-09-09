package hs.industry.ailab.entity.dto.request.dmc;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/3/8 12:02
 */
@Data
public class DmcModleAdapter {

    private DmcBasemodleparam basemodelparam;


    @JSONField(name = "pv")
    private List<Pvparam> pv;

    @JSONField(name = "mv")
    private List<Mvparam> mv;


    @JSONField(name = "ff")
    private List<Ffparam> ff;

    @JSONField(name = "model")
    private List<DmcResponparam> model;


    @JSONField(name = "outputparam")
    private List<DmcOutproperty> outputparam;






}
