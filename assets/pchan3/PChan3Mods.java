package assets.pchan3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.network.FMLEventChannel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

import assets.pchan3.pirate.EntityPirate;
import assets.pchan3.steamboat.EntitySteamBoat;
import assets.pchan3.steamboat.ItemSteamBoat;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.EntityAnchor;
import assets.pchan3.steamship.ItemAirship;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author pchan3
 */
@Mod(modid = "pchan3", name = "PChan3 mods", version = "0.7")
public class PChan3Mods {
	@Instance("pchan3")
	public static PChan3Mods instance;
	@SidedProxy(clientSide = "assets.pchan3.ClientProxy", serverSide = "assets.pchan3.CommonProxy")
	public static CommonProxy proxy;
	private static boolean ENABLE_AIRSHIP = true, ENABLE_STEAMBOAT = true, ENABLE_PIRATE = true;
	public static boolean SHOW_BOILER = true, usePlayerArrow = true, usePlayerCoal = true;
	public static Item airShip, engine, balloon, steamBoat, anchor;
	public static int GUI_ID = 0;
	private static String[] SPAWNABLE_BIOMES = new String[] { "Ocean", "Plains" };
	private static int[] spawnChance = new int[] { 2, 1 }, packSize = new int[] { 1, 2 };
	public static double airUpSpeed, airDownSpeed, airSpeed;
	private Configuration config;
	private org.apache.logging.log4j.Logger logger;
    public static FMLEventChannel channel;

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		//Pirate
		if (ENABLE_PIRATE) {
			SPAWNABLE_BIOMES = config.get("general", "Pirate_spawn_in_Biomes", SPAWNABLE_BIOMES).getStringList();
			spawnChance = config.get("general", "Pirate_Spawn_Chance_per_biome", spawnChance).getIntList();
			packSize = config.get("general", "Pirate_Max_Pack_Size_per_biome", packSize).getIntList();
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
		ENABLE_AIRSHIP = config.get("general", "Enable_Airship", true).getBoolean(true);
		ENABLE_STEAMBOAT = config.get("general", "Enable_Steamboat", true).getBoolean(true);
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
			GameRegistry.addRecipe(new ItemStack(engine), "###", "#X#", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('X'), Blocks.piston);
			// Balloon
			balloon = new Item().setUnlocalizedName("pchan3:Balloon").setCreativeTab(CreativeTabs.tabTransport).setTextureName("pchan3:Balloon");
			GameRegistry.registerItem(balloon, "Balloon");
			GameRegistry.addRecipe(new ItemStack(balloon), "###", "###", "L L", Character.valueOf('#'), Items.leather, Character.valueOf('L'), Items.string);
			//AirShip
			airShip = new ItemAirship().setUnlocalizedName("pchan3:Airship").setTextureName("pchan3:Airship");
			GameRegistry.registerItem(airShip, "Airship");
			EntityRegistry.registerModEntity(EntityAirship.class, "Airship", 1, this, 40, 1, true);
			GameRegistry.addRecipe(new ItemStack(airShip), "XBX", "EFE", "XDX", Character.valueOf('X'), Items.string, Character.valueOf('B'), balloon, Character.valueOf('E'), engine,
					Character.valueOf('D'), Items.boat, Character.valueOf('F'), Blocks.furnace);
			//Anchor
			anchor = new ItemAnchor().setUnlocalizedName("pchan3:anchor").setTextureName("lead");
            GameRegistry.registerItem(anchor, "Anchor");
            GameRegistry.addRecipe(new ItemStack(anchor), " L ", " L ", "III", Character.valueOf('L'), Items.string, Character.valueOf('I'), Items.iron_ingot);
			NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
			EntityRegistry.registerModEntity(EntityAnchor.class, "Anchor", 0, this, 160, 80, false);
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(PacketHandler.CHANNEL);
            channel.register(new PacketHandler());
		}
		//Boat
		if (ENABLE_STEAMBOAT) {
			steamBoat = new ItemSteamBoat().setUnlocalizedName("pchan3:Steamboat").setTextureName("pchan3:Steamboat");
			GameRegistry.registerItem(steamBoat, "Steam Boat");
			EntityRegistry.registerModEntity(EntitySteamBoat.class, "SteamBoat", 2, this, 40, 1, false);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(steamBoat), "#X#", "###", Character.valueOf('#'), "plankWood", Character.valueOf('X'), Items.iron_ingot));
		}
	}

	private BiomeGenBase[] getAvailableBiomes() {
		ArrayList<BiomeGenBase> result = new ArrayList<BiomeGenBase>();
		Iterator<BiomeGenBase> itr = Arrays.asList(BiomeGenBase.getBiomeGenArray()).iterator();
		BiomeGenBase biome = null;
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
}
