package hs.industry.ailab.entity.dto.request.customize;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/3/10 0:44
 */
@Data
public class PythonAdapter {
    private PythonBaseParam basemodelparam;
    private String pythoncontext;
    private Map<String,String> inputparam;//输出参数数据，key=参数名称,value=值
    private List<PythonOutParam> outputparam;

}
