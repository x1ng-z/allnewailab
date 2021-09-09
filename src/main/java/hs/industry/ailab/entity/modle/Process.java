package hs.industry.ailab.entity.modle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/22 13:23
 */
public interface Process   {

    /**model properties set value
     * @param project
     * @return
     * */
    JSONObject inprocess(Project project);

    /**model call algorithm api
     *
     * */
    void docomputeprocess();

    /**model deal algprithm result
     * @param project
     * @param computedata
     * @return
     * */
    JSONObject computresulteprocess(Project project,JSONObject computedata);

    /**
     *deal out properties value
     * @param project
     * @param outdata
     * */
    void outprocess(Project project,JSONObject outdata);

    /**
     * model inite
     * */
    void init();

    /**
     * destory model
     * */
     void destory() ;
}
