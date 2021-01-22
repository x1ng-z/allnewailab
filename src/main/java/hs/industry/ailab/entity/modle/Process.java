package hs.industry.ailab.entity.modle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/22 13:23
 */
public interface Process  {
    JSONObject inprocess(Project project);

    JSONObject computeprocess(Project project);

    void outprocess(Project project,JSONObject outdata);

    void init();
}
