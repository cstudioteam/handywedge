package jp.cstudio.handywedge.test.app.workflow2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.log.FWLogger;
import com.handywedge.workflow.FWWFSynchronizer;

@ApplicationScoped
public class Workflow2Synchronizer implements FWWFSynchronizer {

	@Inject
    private FWLogger logger;

	@Override
	public void doSynchronize(String wfId) {

		logger.debug("--- Synchronizer called. --- " + wfId);
	}

}
