package hs.industry.ailab.entity.dto.response.customize;

import hs.industry.ailab.entity.dto.BaseModelResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/6/3 18:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPythonResponse4PlantDto extends BaseModelResponseDto {
    private CPythonDataDto data;

}
