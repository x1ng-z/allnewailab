package hs.industry.ailab.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/8/8 21:57
 */
@Configuration
@ConfigurationProperties(prefix = "dcs")
@Data
public class DcsApiConfig {
    private String oceandir;

    private String dcsread="/realdata/read";

    private String dcswrite="/opc/write";

    private String pointslist="/pointoperate/getalpoints";
}
