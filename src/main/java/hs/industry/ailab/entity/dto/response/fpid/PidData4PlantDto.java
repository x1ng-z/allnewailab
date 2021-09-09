package hs.industry.ailab.entity.dto.response.fpid;

import hs.industry.ailab.entity.dto.PinDataDto;
import lombok.Data;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/6/3 14:09
 */
@Data
public class PidData4PlantDto  implements java.io.Serializable{
    private List<PinDataDto> mvData;
    private double partkp;
    private double partki;
    private double partkd;


}
