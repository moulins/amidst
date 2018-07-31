package amidst.mojangapi.minecraftinterface.local;

import amidst.documentation.Immutable;

@Immutable
public enum SymbolicNames {
	;
	
	//TODO: correctly manage world types; remove duplication with LegacySymbolicNames
	public static final String CLASS_WORLD_TYPE = "WorldType";
	public static final String FIELD_WORLD_TYPE_DEFAULT = "default";
	public static final String FIELD_WORLD_TYPE_FLAT = "flat";
	public static final String FIELD_WORLD_TYPE_LARGE_BIOMES = "largeBiomes";
	public static final String FIELD_WORLD_TYPE_AMPLIFIED = "amplified";
	public static final String FIELD_WORLD_TYPE_CUSTOMIZED = "customized";

	public static final String CLASS_BOOTSTRAP = "Bootstrap";
	public static final String METHOD_BOOTSTRAP_REGISTER = "register";
	
	public static final String CLASS_LAYER_UTIL = "LayerUtil";
	public static final String METHOD_LAYER_UTIL_INITIALIZE_ALL = "initializeAll";
	
	public static final String CLASS_GEN_SETTINGS = "OverworldGenSettings";
	public static final String CONSTRUCTOR_GEN_SETTINGS = "<init>";
	
	public static final String CLASS_GEN_LAYER = "GenLayer";
	public static final String METHOD_GEN_LAYER_GET_BIOME_DATA = "getBiomeData";
	
	public static final String CLASS_BIOME = "Biome";
	public static final String METHOD_BIOME_GET_ID = "getBiomeId";
}
