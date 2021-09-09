package hs.industry.ailab.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/8/8 21:59
 */
@Data
@ConfigurationProperties(prefix = "algorithm")
@Configuration
public class AlgprithmApiConfig {

    private String url;
    /**
     * 执行python接口
     */
    private String python = "/algorithm/cpython/buildrun";

    /**
     * F-PID运算接口
     */
    private String pid = "/algorithm/fpid/buildrun";

    /**
     * DMC运算接口
     */
    private String dmc = "/algorithm/dmc/buildrun";

    /**
     * 停止运行接口
     */
    private String stop = "/algorithm/stop/";
}
