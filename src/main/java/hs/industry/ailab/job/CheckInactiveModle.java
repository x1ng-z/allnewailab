package hs.industry.ailab.job;

import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ProjectManager;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.Modle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/29 12:52
 */
@Component
@EnableAsync
public class CheckInactiveModle {
    private Logger logger = LoggerFactory.getLogger(CheckInactiveModle.class);

    @Autowired
    private ProjectManager projectManager;

//    @Scheduled(fixedRate = 1000 * 60 * 10, initialDelay = 1000 * 60 * 30)
//    @Async
    public void checkinactivemodle() {
        try {
            Map<Integer, Project> projectPool = projectManager.getProjectPool();
            for (Project project : projectPool.values()) {
                for (Modle modle : project.getModleList()) {
                    BaseModleImp baseModleImp = (BaseModleImp) modle;
                    if (baseModleImp != null) {
                        if (baseModleImp.getActivetime().plusSeconds(60 * 10).isBefore(Instant.now())) {
                            baseModleImp.reconnect();
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
