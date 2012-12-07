package net.vurs.service.definition;

import java.sql.Timestamp;
import java.util.List;

import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.entity.definition.queue.NeighbourhoodPingJob;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.entity.manager.queue.NeighbourhoodPingJobManager;
import net.vurs.service.definition.neighbourhood.BadgeController;
import net.vurs.service.definition.neighbourhood.PingResult;

public class NeighbourhoodService extends Service {

	private EntityService entityService = null;	
	
	private NeighbourhoodPingJobManager neighbourhoodPingJobManager = null;
	
	private BadgeController badgeController = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.logger.info("Loading neighbourhood service");
		
		this.entityService = serviceManager.getService(EntityService.class);
		
		this.neighbourhoodPingJobManager = this.entityService.getManager(NeighbourhoodPingJob.class, NeighbourhoodPingJobManager.class);
		
		this.badgeController = new BadgeController(this.entityService);
	}

	public PingResult process(Entity<Ping> ping) {
		List<Entity<BadgeEarned>> badges = this.badgeController.earnBadges(ping);
		
		this.queuePostProcess(ping);
		
		return new PingResult(badges);
	}
	
	private void queuePostProcess(Entity<Ping> ping) {
		Entity<NeighbourhoodPingJob> job = this.neighbourhoodPingJobManager.create();
		job.set(NeighbourhoodPingJob.ping, ping);
		job.set(NeighbourhoodPingJob.state, NeighbourhoodPingJob.State.WAITING.getIndex());
		job.set(NeighbourhoodPingJob.stateTime, new Timestamp(System.currentTimeMillis()));
		this.neighbourhoodPingJobManager.save(job);
	}

	public void startProcessor() {}
	public void stopProcessor() {}
	
}
