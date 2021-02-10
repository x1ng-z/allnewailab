package hs.industry.ailab.entity;

import hs.industry.ailab.dao.mysql.service.ProjectOperaterImp;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.pydriver.pyproxyserve.IOServer;
import hs.industry.ailab.pydriver.session.PySessionManager;
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
public class ProjectManager {
    private Logger logger = LoggerFactory.getLogger(ProjectManager.class);


    private ProjectOperaterImp projectOperaterImp;
    private PySessionManager pySessionManager;
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
    private IOServer ioServer;


    @Autowired
    public ProjectManager(
            IOServer ioServer,
            PySessionManager pySessionManager,
            ProjectOperaterImp projectOperaterImp,
            @Value("${pyproxyexecute}") String pyproxyexecute,
            @Value("${mpcpinnumber}") int mpcpinnumber,
            @Value("${filterscript}") String filterscript,
            @Value("${pidscript}") String pidscript,
            @Value("${simulatorscript}") String simulatorscript,
            @Value("${mpcscrip}") String mpcscrip,
            @Value("${oceandir}") String oceandir,
            @Value("${pydriverport}") String pydriverport,
            @Qualifier("executethreadpool") ExecutorService executethreadpool
    ) {
        this.executethreadpool = executethreadpool;
        this.pySessionManager = pySessionManager;
        this.projectOperaterImp = projectOperaterImp;
        this.pyproxyexecute = pyproxyexecute;
        this.mpcpinnumber = mpcpinnumber;
        this.filterscript = filterscript;
        this.pidscript = pidscript;
        this.simulatorscript = simulatorscript;
        this.mpcscrip = mpcscrip;
        this.oceandir = oceandir;
        this.pydriverport = pydriverport;
        this.ioServer = ioServer;
        ioServer.getNettyServerInitializer().msgDecoder_inbound.setProjectManager(this);
        List<Project> dbprojetc = projectOperaterImp.findAllProject();
        for (Project project : dbprojetc) {
//            if(project.getProjectid()==2){
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

                if (modle instanceof MPCModle) {
                    MPCModle mpcmodle = (MPCModle) modle;
                    mpcmodle.toBeRealModle(Double.valueOf(project.getRunperiod()).intValue(),
                            this.mpcscrip,
                            this.simulatorscript,
                            this.mpcpinnumber,
                            this.pySessionManager,
                            this.pydriverport,
                            this.pyproxyexecute);
                    mpcmodle.init();
                    mpcmodle.connect();

                } else if (modle instanceof PIDModle) {
                    PIDModle pidmodle = (PIDModle) modle;
                    pidmodle.toBeRealModle(this.pySessionManager, this.pidscript, this.pydriverport, this.pyproxyexecute);
                    pidmodle.init();
                    pidmodle.connect();

                } else if (modle instanceof CUSTOMIZEModle) {
                    CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                    customizeModle.toBeRealModle(this.pySessionManager, this.pydriverport, this.pyproxyexecute);
                    customizeModle.init();
                    customizeModle.connect();
                } else if (modle instanceof FilterModle) {
                    FilterModle filterModle = (FilterModle) modle;
                    filterModle.toBeRealModle(this.pySessionManager, this.filterscript, this.pydriverport, this.pyproxyexecute);
                    filterModle.init();
                    filterModle.connect();
                } else if (modle instanceof INModle) {
                    INModle inModle = (INModle) modle;
                    inModle.toBeRealModle(this.oceandir);
                    inModle.init();
                    inModle.connect();
                } else if (modle instanceof OUTModle) {
                    OUTModle outModle = (OUTModle) modle;
                    outModle.toBeRealModle(this.oceandir);
                    outModle.init();
                    outModle.connect();
                }

            }

            projectPool.put(project.getProjectid(), project);

            this.executethreadpool.execute(project);

        }
    }


    public Map<Integer, Project> getProjectPool() {
        return projectPool;
    }


    public Modle getspecialModle(int modleid) {
        for (Project project : projectPool.values()) {
            if (project.getIndexmodles().containsKey(modleid)) {
                return project.getIndexmodles().get(modleid);
            }

        }
        return null;
    }

    public void setProjectPool(Map<Integer, Project> projectPool) {
        this.projectPool = projectPool;
    }
}
