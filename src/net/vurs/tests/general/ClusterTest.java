package net.vurs.tests.general;

import net.vurs.common.FutureResponse;
import net.vurs.service.definition.cluster.protocol.GraphProtocol;
import net.vurs.tests.TestRunner;
import net.vurs.util.ErrorControl;

public class ClusterTest extends TestRunner {

	public void test() {
		try {
			GraphProtocol graphProtocol = this.logicService.getClusterService().getProtocol(GraphProtocol.class);
			FutureResponse<Object> res = graphProtocol.test("testing");
			this.logger.info(String.format("cluster response: %s", res.get().toString()));
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
}
