package hs.industry.ailab.constant;

import lombok.Getter;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/8/8 22:24
 */
@Getter
public enum ModelTypeEnum {
    MODEL_TYPE_INPUT("input","input"),
    MODEL_TYPE_OUTPUT("output","output"),
    MODEL_TYPE_FILTER("filter","filter"),
    MODEL_TYPE_CUSTOMIZE("customize","customize"),
    MODEL_TYPE_MPC("mpc","mpc"),
    MODEL_TYPE_PID("pid","pid"),
    ;

    ModelTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
}
