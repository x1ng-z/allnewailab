package hs.industry.ailab.entity.dto.request.pid;

import lombok.Data;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/3/9 9:22
 */
@Data
public class PidModleAdapter {
    private PidBaseModleParam basemodelparam;
    private PidInputproperty inputparam;
    private List<PidOutPutproperty> outputparam;
}
