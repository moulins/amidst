package amidst.mojangapi.world.coordinates;

import amidst.documentation.Immutable;

@Immutable
public enum Resolution {
	WORLD(0),
	QUARTER(2),
	NETHER(3),
	CHUNK(4),
	NETHER_CHUNK(7),
	FRAGMENT(9);

	public static Resolution from(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return Resolution.QUARTER;
		} else {
			return Resolution.WORLD;
		}
	}

	private final int shift;

	private Resolution(int shift) {
		this.shift = shift;
	}

	public int getStep() {
		return 1 << shift;
	}
	
	public int getShift() {
		return shift;
	}

	public int getShiftPer(Resolution resolution) {
		return resolution.shift - shift;
	}
	
	public int getStepsPer(Resolution resolution) {
		return 1 << getShiftPer(resolution);
	}
	
	public long convertFromWorldToThis(long coordinateInWorld) {
		return coordinateInWorld >> shift;
	}

	public long convertFromThisToWorld(long coordinateInThisResolution) {
		return coordinateInThisResolution << shift;
	}
	
	public long snapUpwardsToResolution(long coordinateInWorld) {
		if(toRelative(coordinateInWorld) == 0)
			return coordinateInWorld;
		return snapToResolution(coordinateInWorld+1);
	}
	
	public long snapToResolution(long coordinateInWorld) {
		return coordinateInWorld & ~getResolutionMask();
	}
	
	public long toRelative(long coordinateInWorld) {
		return coordinateInWorld & getResolutionMask();
	}
	
	private long getResolutionMask() {
		return (1 << shift) - 1;
	}
}
