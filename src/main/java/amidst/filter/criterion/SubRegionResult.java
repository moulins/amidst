package amidst.filter.criterion;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import amidst.filter.CriterionResult;
import amidst.filter.ResultsMap;
import amidst.filter.WorldFilter;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.util.TriState;

public abstract class SubRegionResult implements CriterionResult {
	private Set<Region.Box> regionsToTest;
	
	protected SubRegionResult(Region region) {
		regionsToTest = new HashSet<>();
		forEachSubRegion(region, regionsToTest::add);
	}
	
	protected SubRegionResult(SubRegionResult other) {
		this.regionsToTest = new HashSet<>(other.regionsToTest);
	}

	@Override
	public TriState hasMatched() {
		if(hasFound())
			return TriState.TRUE;
		if(regionsToTest.isEmpty())
			return TriState.FALSE;
		return TriState.UNKNOWN;
	}
	
	@Override
	public Region.Box getNextRegionToCheck(ResultsMap map) {
		if(hasFound() || regionsToTest.isEmpty())
			return null;
		
		return regionsToTest.iterator().next();
	}
	
	@Override
	public void checkRegionAndUpdate(ResultsMap map, World world, Coordinates offset, Region.Box region) {
		if(!regionsToTest.remove(region))
			return;
		
		doCheckRegion(world, region.move(offset));
		
		if(hasFound())
			regionsToTest.clear();
	}
	
	protected abstract boolean hasFound();
	protected abstract void doCheckRegion(World world, Region.Box region);


	private static void forEachSubRegion(Region region, Consumer<Region.Box> consumer) {
		Coordinates c1 = region.getCornerNW().snapTo(WorldFilter.REGION_SIZE);
		Coordinates c2 = region.getCornerSE().snapUpwardsTo(WorldFilter.REGION_SIZE);
		
		int size = WorldFilter.REGION_SIZE.getStep();
		for(int x = c1.getX(); x < c2.getX(); x += size) {
			for(int y = c1.getY(); y < c2.getY(); y += size) {
				Region.Box r = Region.box(x, y, size, size);
				if(region.intersectsWith(r))
					consumer.accept(r);
			}
		}
	}
	
}