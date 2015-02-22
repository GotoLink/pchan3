package assets.pchan3;

import assets.pchan3.pirate.EntityPirate;
import assets.pchan3.steamboat.EntitySteamBoat;
import assets.pchan3.steamboat.ItemSteamBoat;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.ItemAirship;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author pchan3
 */
@Mod(modid = "pchan3", name = "PChan3 mods", useMetadata = true)
public final class PChan3Mods {
	@Instance("pchan3")
	public static PChan3Mods instance;
	@SidedProxy(clientSide = "assets.pchan3.ClientProxy", serverSide = "assets.pchan3.CommonProxy")
	public static CommonProxy proxy;
	private static boolean ENABLE_PIRATE = true;
	public static boolean SHOW_BOILER = true, usePlayerArrow = true, usePlayerCoal = true;
	public static Item airShip, engine, balloon, steamBoat, anchor;
	public static final int GUI_ID = 0;
	private static String[] SPAWNABLE_BIOMES = new String[] { "Ocean", "Plains" };
	private static int[] spawnChance = new int[] { 2, 1 }, packSize = new int[] { 1, 2 };
	public static double airUpSpeed, airDownSpeed, airSpeed, spawn = 0.0714D;
	private Configuration config;
	private org.apache.logging.log4j.Logger logger;
    public static FMLEventChannel channel;

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		//Pirate
		if (ENABLE_PIRATE) {
			spawn = config.get("general", "Pirate_spawn_discarded_attempt", spawn, "Global ratio of actual spawn attempts. Lower to make pirates rarer.").setMinValue(0.001D).getDouble();
			SPAWNABLE_BIOMES = config.get("general", "Pirate_spawn_in_Biomes", SPAWNABLE_BIOMES, "Where pirates can spawn, per biome names.").getStringList();
			spawnChance = config.get("general", "Pirate_Spawn_Chance_per_biome", spawnChance, "Relative chance of pirate spawn against other mob spawn, biome specific. In same order as biome names in Pirate_spawn_in_Biomes option.").getIntList();
			packSize = config.get("general", "Pirate_Max_Pack_Size_per_biome", packSize, "Maximum number of pirate spawn, per biome. In same order as biome names in Pirate_spawn_in_Biomes option.").getIntList();
			if (SPAWNABLE_BIOMES != null && SPAWNABLE_BIOMES.length != 0) {
				EntityRegistry.registerModEntity(EntityPirate.class, "Pirate", 3, this, 80, 1, true);
				BiomeGenBase[] biomes = getAvailableBiomes();
				for (int i = 0; i < biomes.length && i < spawnChance.length && i < packSize.length; i++) {
					List<BiomeGenBase.SpawnListEntry> spawns = null;
					if (biomes[i] != null)
						spawns = biomes[i].getSpawnableList(EnumCreatureType.monster);
					if (spawns != null) {
						spawns.add(new BiomeGenBase.SpawnListEntry(EntityPirate.class, spawnChance[i], 1, packSize[i]));
						logger.trace("Pirate added to biome " + biomes[i].biomeName);
					}
				}
			}
		}
		if (config.hasChanged())
			config.save();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void preload(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		// Read properties file.
		config = new Configuration(event.getSuggestedConfigurationFile());
		SHOW_BOILER = config.get("general", "show_boiler", true).getBoolean(true);
		boolean ENABLE_AIRSHIP = config.get("general", "Enable_Airship", true).getBoolean(true);
		boolean ENABLE_STEAMBOAT = config.get("general", "Enable_Steamboat", true).getBoolean(true);
		ENABLE_PIRATE = config.get("general", "Enable_Pirate", true).getBoolean(true);
		airUpSpeed = config.get("cheats", "AirshipUpSpeed", 2D).getDouble(2D) / 100;
		airDownSpeed = config.get("cheats", "AirshipDownSpeed", 3D).getDouble(3D) / 100;
		airSpeed = config.get("cheats", "AirshipMainSpeed", 5D).getDouble(5D) / 100;
		usePlayerArrow = config.get("cheats", "Use arrows from player inventory", usePlayerArrow).getBoolean(true);
		usePlayerCoal = config.get("cheats", "Use coal from player inventory", usePlayerCoal).getBoolean(true);
		if (ENABLE_AIRSHIP) {
			// Engine
			engine = new Item().setUnlocalizedName("pchan3:Engine").setCreativeTab(CreativeTabs.tabTransport).setTextureName("pchan3:Engine");
			GameRegistry.registerItem(engine, "Engine");
			GameRegistry.addRecipe(new ItemStack(engine), "###", "#X#", "###",'#', Items.iron_ingot,'X', Blocks.piston);
			// Balloon
			balloon = new Item().setUnlocalizedName("pchan3:Balloon").setCreativeTab(CreativeTabs.tabTransport).setTextureName("pchan3:Balloon");
			GameRegistry.registerItem(balloon, "Balloon");
			GameRegistry.addRecipe(new ItemStack(balloon), "###", "###", "L L",'#', Items.leather,'L', Items.string);
			//AirShip
			airShip = new ItemAirship().setUnlocalizedName("pchan3:Airship").setTextureName("pchan3:Airship");
			GameRegistry.registerItem(airShip, "Airship");
			EntityRegistry.registerModEntity(EntityAirship.class, "Airship", 1, this, 40, 1, true);
			GameRegistry.addRecipe(new ItemStack(airShip), "XBX", "EFE", "XDX",'X', Items.string,'B', balloon,'E', engine,
					'D', Items.boat,'F', Blocks.furnace);
			//Anchor
			anchor = new ItemAnchor().setUnlocalizedName("pchan3:anchor").setTextureName("lead");
            GameRegistry.registerItem(anchor, "Anchor");
            GameRegistry.addRecipe(new ItemStack(anchor), " L ", " L ", "III",'L', Items.string,'I', Items.iron_ingot);
			NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(PacketHandler.CHANNEL);
            channel.register(new PacketHandler());
		}
		//Boat
		if (ENABLE_STEAMBOAT) {
			steamBoat = new ItemSteamBoat().setUnlocalizedName("pchan3:Steamboat").setTextureName("pchan3:Steamboat");
			GameRegistry.registerItem(steamBoat, "SteamBoat");
			EntityRegistry.registerModEntity(EntitySteamBoat.class, "SteamBoat", 2, this, 40, 1, false);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(steamBoat), "#X#", "###",'#', "plankWood",'X', Items.iron_ingot));
		}
        if(event.getSourceFile().getName().endsWith(".jar")){
            proxy.tryCheckForUpdate();
        }
	}

	private BiomeGenBase[] getAvailableBiomes() {
		ArrayList<BiomeGenBase> result = new ArrayList<BiomeGenBase>();
		Iterator<BiomeGenBase> itr = Arrays.asList(BiomeGenBase.getBiomeGenArray()).iterator();
		BiomeGenBase biome;
		while (itr.hasNext()) {
			biome = itr.next();
			if (biome != null)
				for (int id = 0; id < SPAWNABLE_BIOMES.length; id++) {
					if (SPAWNABLE_BIOMES[id] != null && !SPAWNABLE_BIOMES[id].equals("") && biome.biomeName.equalsIgnoreCase(SPAWNABLE_BIOMES[id].trim())) {
						result.add(biome);
						break;
					}
				}
		}
		result.trimToSize();
		return (result.size() != 0 && !result.isEmpty()) ? result.toArray(new BiomeGenBase[result.size()]) : null;
	}

    @EventHandler
    public void remap(FMLMissingMappingsEvent event){
        for(FMLMissingMappingsEvent.MissingMapping missingMapping:event.get()){
            if(missingMapping.name.equals("pchan3:Steam Boat"))
                missingMapping.remap(steamBoat);
        }
    }
}
