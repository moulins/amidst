package amidst.mojangapi.mocking.json;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;

@Immutable
@GsonObject
public class BiomeRequestRecordJson {
	
	public volatile int x;
	public volatile int y;
	public volatile int width;
	public volatile int height;
	public volatile boolean isQuarterResolution;
	public volatile long startTime;
	public volatile long duration;
	public volatile String threadName;
	
	public BiomeRequestRecordJson() {
	}
	
	public BiomeRequestRecordJson(Region.Box region, boolean isQuarterResolution, long startTime, long duration, String threadName) {
		Coordinates corner = region.getCorner();
		this.x = corner.getX();
		this.y = corner.getY();
		this.width = region.getWidth();
		this.height = region.getHeight();
		this.isQuarterResolution = isQuarterResolution;
		this.startTime = startTime;
		this.duration = duration;
		this.threadName = threadName;
	}
}
