package hs.industry.ailab.entity.dto.response.dmc;

import hs.industry.ailab.entity.dto.PinDataDto;
import lombok.Data;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/6/4 10:40
 */
@Data
public class DmcData4PlantDto implements java.io.Serializable{
    private List<PinDataDto> mvData;
    private List<DmcPvPredictData4PlantDto> pvpredict;
    private List<DmcDmvData4PlantDto> dmv;
}
