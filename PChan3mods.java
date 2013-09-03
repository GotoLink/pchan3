package assets.pchan3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraftforge.common.Configuration;

import org.lwjgl.input.Keyboard;

import assets.pchan3.pirate.EntityPirate;
import assets.pchan3.steamboat.EntitySteamBoat;
import assets.pchan3.steamboat.ItemSteamBoat;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.ItemAirship;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
/**
*
* @author pchan3
*/
@Mod(modid = "pchan3", name = "PChan3 mods", version = "0.7")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,channels = {"Steamship" },
		packetHandler = PacketHandler.class)
public class PChan3Mods{
	@Instance("pchan3")
	public static PChan3Mods instance;
	@SidedProxy(clientSide="assets.pchan3.ClientProxy", serverSide="assets.pchan3.CommonProxy")
	public static CommonProxy proxy;
	private static int steamboatItemID=12500,airshipItemID=12503,engineItemID=12502,balloonItemID=12501,anchorItemID=12504;
	private static boolean  ENABLE_AIRSHIP=true,ENABLE_STEAMBOAT=true,ENABLE_PIRATE=true;
	public static boolean SHOW_BOILER=true;
    public static Item airShip,engine,balloon,steamBoat,anchor;  
    public static int KEY_UP = Keyboard.KEY_NUMPAD8,KEY_DOWN = Keyboard.KEY_NUMPAD2;
    public static int KEY_CHEST = Keyboard.KEY_R,KEY_FIRE = Keyboard.KEY_NUMPAD5;
    public static int GUI_ID=0;
    private static String[] SPAWNABLE_BIOMES=new String[]{"Ocean","Plains"};
    private int[] spawnChance = new int[]{2,1}, packSize = new int[]{1,2};
	private Configuration config;
	private Logger logger;
	 
	@EventHandler
	public void preload(FMLPreInitializationEvent event){
		logger = event.getModLog();
		// Read properties file.
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		SHOW_BOILER=config.get("general", "show_boiler", true).getBoolean(true);
		ENABLE_AIRSHIP=config.get("general", "Enable_Airship", true).getBoolean(true);
		ENABLE_STEAMBOAT=config.get("general", "Enable_Steamboat", true).getBoolean(true);
		ENABLE_PIRATE=config.get("general", "Enable_Pirate", true).getBoolean(true);
		airshipItemID=config.getItem("AirshipID", airshipItemID).getInt();
		engineItemID=config.getItem("EngineID", engineItemID).getInt();
		balloonItemID=config.getItem("BalloonID", balloonItemID).getInt();
		steamboatItemID=config.getItem("SteamboatID", steamboatItemID).getInt();
		anchorItemID=config.getItem("AnchorID", anchorItemID).getInt();
	}
	@EventHandler
    public void load(FMLInitializationEvent event) { 
	  
		if (ENABLE_AIRSHIP)
		{
			// Engine
			engine = new Item(engineItemID).setUnlocalizedName("pchan3:Engine").setCreativeTab(CreativeTabs.tabTransport).func_111206_d("pchan3:Engine");
			LanguageRegistry.addName(engine, "Engine");
			GameRegistry.addRecipe(new ItemStack(engine), new Object[]{
			    "###",
			    "#X#",
			    "###",
			    Character.valueOf('#'), Item.ingotIron,
			    Character.valueOf('X'), Block.pistonBase});
		     
		  // Balloon
			balloon = new Item(balloonItemID).setUnlocalizedName("pchan3:Balloon").setCreativeTab(CreativeTabs.tabTransport).func_111206_d("pchan3:Balloon");
			LanguageRegistry.addName(balloon, "Balloon");
			GameRegistry.addRecipe(new ItemStack(balloon), new Object[]{
			    "###",
			    "###",
			    "L L",
			    Character.valueOf('#'), Item.leather,
			    Character.valueOf('L'), Item.silk});
			
			//AirShip
		    airShip = new ItemAirship(airshipItemID).setUnlocalizedName("pchan3:Airship").func_111206_d("pchan3:Airship");
			LanguageRegistry.addName(airShip, "Airship");
			EntityRegistry.registerModEntity(EntityAirship.class, "Airship", 1, this, 40, 1, true);
			GameRegistry.addRecipe(new ItemStack(airShip), new Object[]{
			    "XBX",
			    "EFE",
			    "XDX",
			    Character.valueOf('X'), Item.silk,
			    Character.valueOf('B'), balloon,
			    Character.valueOf('E'), engine,
			    Character.valueOf('D'), Item.boat,
			    Character.valueOf('F'), Block.furnaceIdle});
			//Anchor
			anchor = new ItemAnchor(anchorItemID).setUnlocalizedName("pchan3:anchor").func_111206_d("lead");
			LanguageRegistry.addName(anchor, "Anchor");
			GameRegistry.addRecipe(new ItemStack(anchor), new Object[]{
				" L ",
				" L ",
				"III",
				Character.valueOf('L'), Item.silk,
				Character.valueOf('I'), Item.ingotIron});
			NetworkRegistry.instance().registerGuiHandler(this,proxy);
		}
		//Boat
	    if (ENABLE_STEAMBOAT)
	 	{
			steamBoat = new ItemSteamBoat(steamboatItemID).setUnlocalizedName("pchan3:Steamboat").func_111206_d("pchan3:Steamboat");
			LanguageRegistry.addName(steamBoat, "Steam Boat");
			EntityRegistry.registerModEntity(EntitySteamBoat.class, "SteamBoat", 2,this,40,1,false);
			GameRegistry.addRecipe(new ItemStack(steamBoat), new Object[] {
		        "#X#",
		        "###",
		        Character.valueOf('#'), Block.planks,
		        Character.valueOf('X'), Item.ingotIron
		    });
	 	}
	}  
	private BiomeGenBase[] getAvailableBiomes(){
		  ArrayList<BiomeGenBase> result=new ArrayList<BiomeGenBase>();
		  Iterator<BiomeGenBase> itr = Arrays.asList(BiomeGenBase.biomeList).iterator();
		  BiomeGenBase biome=null;
		  while(itr.hasNext())
		  {
				biome=itr.next();
				if(biome!=null)
					for(int id=0;id<SPAWNABLE_BIOMES.length;id++)
					{ 
						if(SPAWNABLE_BIOMES[id]!=null &&  SPAWNABLE_BIOMES[id]!="" && biome.biomeName.equalsIgnoreCase(SPAWNABLE_BIOMES[id].trim()))
						{			
							result.add(biome);
							break;
						}
					}
		  }
		  result.trimToSize();
		  return (result.size()!=0 && !result.isEmpty())?(BiomeGenBase[]) result.toArray(new BiomeGenBase[result.size()]):null; 
	}
	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event)
	{
	//Pirate
		if (ENABLE_PIRATE)
		{
			SPAWNABLE_BIOMES=config.get("general", "Pirate_spawn_in_Biomes", SPAWNABLE_BIOMES).getStringList();
			spawnChance = config.get("general", "Pirate_Spawn_Chance_per_biome", spawnChance).getIntList();
			packSize = config.get("general", "Pirate_Max_Pack_Size_per_biome", packSize).getIntList();
			if (SPAWNABLE_BIOMES!=null && SPAWNABLE_BIOMES.length!=0){		
				EntityRegistry.registerModEntity(EntityPirate.class, "Pirate", 3, this, 80, 1, true);
				BiomeGenBase[] biomes=getAvailableBiomes();
				for(int i=0; i<biomes.length && i<spawnChance.length && i<packSize.length; i++)
				{
					List spawns=null;
					if( biomes[i]!=null)
						spawns = biomes[i].getSpawnableList(EnumCreatureType.monster);
					if (spawns!=null){
						spawns.add(new SpawnListEntry(EntityPirate.class, spawnChance[i], 1, packSize[i]));
						logger.finest("Pirate added to biome "+biomes[i].biomeName);
					}
				}
				LanguageRegistry.instance().addStringLocalization("entity.pchan3.Pirate.name", "en_US", "Pirate");
			}
		}
		if(config.hasChanged())
			config.save();
		proxy.registerRenderInformation();
	}
}
