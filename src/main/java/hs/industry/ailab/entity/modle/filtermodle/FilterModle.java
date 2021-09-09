package hs.industry.ailab.entity.modle.filtermodle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.config.AlgprithmApiConfig;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filter.Filter;
import hs.industry.ailab.entity.modle.filter.FirstOrderLagFilter;
import hs.industry.ailab.entity.modle.filter.MoveAverageFilter;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.service.HttpClientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/11 12:48
 */
@Data
@Slf4j
public class FilterModle extends BaseModleImp {

    /**
     * memery
     */

    private String datasource;
    private String filterscript;
    private String port;
    private String pyproxyexecute;

    //数据容器
    //key=propertyId
    private Map<Integer, Double[]> constainer = null;
    //上一个值 key=propertyId
    private Map<Integer, Double> lastvalue = null;

    /*输入输出引脚*/

    List<ModleProperty> outmodleProperties = null;
    List<ModleProperty> inmodleProperties = null;


    @Override
    public void destory() {
        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();
        httpClientService.PostParam(algprithmApiConfig.getUrl() + algprithmApiConfig.getStop() + getModleId(), null);

    }

    //发送计算的数据给本模块的python脚本
    @Override
    public void docomputeprocess() {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);
        //一阶滤波
        if (filter instanceof FirstOrderLagFilter) {
            FirstOrderLagFilter orderLagFilter = (FirstOrderLagFilter) filter;
            String filtermethod = orderLagFilter.getFiltermethod();
            double alphe = orderLagFilter.getFilteralphe();

            if (lastvalue == null) {
                lastvalue = new HashMap<>();
                //数据初始化
                if (!CollectionUtils.isEmpty(inmodleProperties)) {
                    inmodleProperties.forEach(in -> {
                        BaseModlePropertyImp baseIn = (BaseModlePropertyImp) in;
                        lastvalue.put(baseIn.getModlepinsId(), baseIn.getValue());
                    });
                }

            }

            //计算滤波值，并将数据根据引脚名称来匹配到输出引脚上
            if (!CollectionUtils.isEmpty(inmodleProperties)) {

                inmodleProperties.forEach(in -> {
                    BaseModlePropertyImp baseIn = (BaseModlePropertyImp) in;
                    Double filtervalue = lastvalue.get(baseIn.getModlepinsId()) * (1 - alphe) + alphe * (baseIn.getValue());

                    if (!CollectionUtils.isEmpty(outmodleProperties)) {
                        outmodleProperties.stream().filter(out -> {
                            BaseModlePropertyImp baseout = (BaseModlePropertyImp) out;
                            if (baseIn.getModlePinName().equals(baseout.getModlePinName())) {
                                return true;
                            } else {
                                return false;
                            }
                        }).forEach(o -> {
                            BaseModlePropertyImp baseout = (BaseModlePropertyImp) o;
                            baseout.setValue(filtervalue);
                        });
                    }

                });
            }

        } else if (filter instanceof MoveAverageFilter) {

            MoveAverageFilter moveAverageFilter = (MoveAverageFilter) filter;
            String filtermethod = moveAverageFilter.getFiltermethod();
            int capacity = moveAverageFilter.getFiltercapacity();

            //初始化
            if (constainer == null) {
                constainer = new HashMap<>();

                inmodleProperties.forEach(in -> {
                    BaseModlePropertyImp baseIn = (BaseModlePropertyImp) in;
                    final int size = capacity <= 0 ? 1 : capacity;
                    Double[] subConstainer = new Double[size];
                    for (int i = 0; i < size; i++) {
                        subConstainer[i] = baseIn.getValue();
                    }
                    constainer.put(baseIn.getModlepinsId(), subConstainer);

                });

            }

            //数据更新
            /**
             * 1数据位移
             * 2数据填充
             * */
            if (!CollectionUtils.isEmpty(constainer)) {
                constainer.forEach((id, c) -> {
                    //数据位移
                    if (c.length > 1) {
                        Double[] moveValue = Arrays.copyOfRange(c, 1, c.length);
                        for (int i = 0; i < moveValue.length; i++) {
                            c[i] = moveValue[i];
                        }
                    }
                    //数据填充
                    c[c.length - 1] = getIndexproperties().get(id).getValue();
                });
            }


            if (!CollectionUtils.isEmpty(inmodleProperties)) {

                inmodleProperties.forEach(in -> {
                    BaseModlePropertyImp baseIn = (BaseModlePropertyImp) in;

                    Double[] values = constainer.get(baseIn.getModlepinsId());

                    double average = Arrays.asList(values).stream().mapToDouble(Double::doubleValue).average().getAsDouble();

                    if (!CollectionUtils.isEmpty(outmodleProperties)) {
                        outmodleProperties.stream().filter(out -> {
                            BaseModlePropertyImp baseout = (BaseModlePropertyImp) out;
                            if (baseIn.getModlePinName().equals(baseout.getModlePinName())) {
                                return true;
                            } else {
                                return false;
                            }
                        }).forEach(o -> {
                            BaseModlePropertyImp baseout = (BaseModlePropertyImp) o;
                            baseout.setValue(average);
                        });
                    }

                });

            }


        }

        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);

    }

    //将上几个模块的输出引脚数据赋值给本模块的输入引脚
    @Override
    public JSONObject inprocess(Project project) {
        setBeginruntime(Instant.now());

        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp filterproperty = (BaseModlePropertyImp) modleProperty;
            if (filterproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                int modleId = filterproperty.getResource().getInteger("modleId");
                int modlepinsId = filterproperty.getResource().getInteger("modlepinsId");
                Modle modle = project.getIndexmodles().get(modleId);
                if (modle != null) {
                    if (modle instanceof MPCModle) {
                        MPCModle mpcModle = (MPCModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    } else if (modle instanceof PIDModle) {
                        PIDModle pidModle = (PIDModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    } else if (modle instanceof CUSTOMIZEModle) {
                        CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    } else if (modle instanceof FilterModle) {
                        FilterModle filterModle = (FilterModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    } else if (modle instanceof INModle) {
                        INModle inModle = (INModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    } else if (modle instanceof OUTModle) {
                        OUTModle outModle = (OUTModle) modle;
                        BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                        filterproperty.setValue(baseModlePropertyImp.getValue());
                    }

                }
            }
        }
        return null;
    }

    /**
     * 处理python脚本处理后的数据，并且将数据赋值给输出引脚
     * OU={1：{
     * 'value':filtevalue ,
     * 'outputmodleid': subdata['outputmodleid'],
     * 'outputpropertyid': subdata['outputpropertyid']
     * }
     * }
     */
    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {


        return null;
    }

    //不用做如何事情了 computresulteprocess已经将数据赋值给输出引脚了
    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
        setActivetime(Instant.now());
    }

    @Override
    public void init() {
        setIndexproperties(new HashMap<>());
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            getIndexproperties().put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }

        if (!CollectionUtils.isEmpty(propertyImpList)) {

            outmodleProperties = propertyImpList.stream().filter(p -> {
                if (ModleProperty.PINDIROUTPUT.equals(((BaseModlePropertyImp) p).getPindir())) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());

            inmodleProperties = propertyImpList.stream().filter(p -> {
                if (ModleProperty.PINDIRINPUT.equals(((BaseModlePropertyImp) p).getPindir())) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
        }


    }


    /**
     * db
     */
    private Filter filter;
    private List<ModleProperty> propertyImpList;

}
