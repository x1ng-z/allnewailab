package hs.industry.ailab.entity.modle;

import hs.industry.ailab.entity.ModleSight;

import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:08
 */
public interface Modle extends Process {
    public final String MODLETYPE_INPUT = "input";
    public final String MODLETYPE_OUTPUT = "output";
    public final String MODLETYPE_FILTER = "filter";
    public final String MODLETYPE_CUSTOMIZE = "customize";
    public final String MODLETYPE_MPC = "mpc";
    public final String MODLETYPE_PID = "pid";

}
