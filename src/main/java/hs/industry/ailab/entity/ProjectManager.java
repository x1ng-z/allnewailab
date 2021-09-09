package hs.industry.ailab.entity;

import hs.industry.ailab.dao.mysql.service.ProjectOperaterImp;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;

import hs.industry.ailab.service.HttpClientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/26 8:24
 */
@Component
@Slf4j
@Data
public class ProjectManager {

    private HttpClientService httpClientService;

    private ProjectOperaterImp projectOperaterImp;
    private String pyproxyexecute;

    private int mpcpinnumber;

    private String filterscript;


    private String pidscript;


    private String simulatorscript;


    private String mpcscrip;


    private String oceandir;


    private String pydriverport;


    /***memory**/
    private Map<Integer, Project> projectPool = new ConcurrentHashMap();

    private ExecutorService executethreadpool;

    @Autowired
    public ProjectManager(
            HttpClientService httpClientService,
            ProjectOperaterImp projectOperaterImp,
            @Value("${pyproxyexecute}") String pyproxyexecute,
            @Value("${mpcpinnumber}") int mpcpinnumber,
            @Qualifier("executethreadpool") ExecutorService executethreadpool
    ) {
        this.executethreadpool = executethreadpool;
        this.httpClientService = httpClientService;
        this.projectOperaterImp = projectOperaterImp;
        this.pyproxyexecute = pyproxyexecute;
        this.mpcpinnumber = mpcpinnumber;

        List<Project> dbprojetc = projectOperaterImp.findAllProject();
        for (Project project : dbprojetc) {
//            if(project.getProjectid()==5){
                activeProject(project);
//            }

        }


    }


    @PreDestroy
    public void destory() {
        for (Project project : projectPool.values()) {
            for (Modle modle : project.getModleList()) {
                BaseModleImp baseModleImp = (BaseModleImp) modle;
                if (baseModleImp != null) {
                    baseModleImp.destory();
                }
            }
        }
    }

    public void activeProject(Project project) {
        if (project != null) {
            project.init();
            List<Modle> projectModleList = project.getModleList();
            for (Modle modle : projectModleList) {
                if(modle instanceof MPCModle){
                    ((MPCModle) modle).toBeRealModle(Double.valueOf(project.getRunperiod()).intValue(),mpcpinnumber);
                }
                modle.init();
                ((BaseModleImp)modle).setHttpClientService(httpClientService);
            }

            projectPool.put(project.getProjectid(), project);

            this.executethreadpool.execute(project);

        }
    }

    public Modle getspecialModle(int modleid) {
        for (Project project : projectPool.values()) {
            if (project.getIndexmodles().containsKey(modleid)) {
                return project.getIndexmodles().get(modleid);
            }

        }
        return null;
    }

}
