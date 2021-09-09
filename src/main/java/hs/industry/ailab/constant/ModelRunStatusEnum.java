package hs.industry.ailab.constant;

import lombok.Getter;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/8/8 21:49
 */
@Getter
public enum ModelRunStatusEnum {
    MODEL_RUN_STATUS_INITE(-1, "inite"),
    MODEL_RUN_STATUS_RUNNING(0, "running"),
    MODEL_RUN_STATUS_COMPELTE(1, "complete"),
    MODEL_RUN_STATUS_STOP(2, "stop");

    ModelRunStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;
}
