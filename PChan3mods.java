package pchan3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.lwjgl.input.Keyboard;

import pchan3.pirate.EntityPirate;
import pchan3.steamboat.EntitySteamBoat;
import pchan3.steamboat.ItemSteamBoat;
import pchan3.steamship.EntityAirship;
import pchan3.steamship.ItemAirship;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
/**
*
* @author pchan3
*/
@Mod(modid = "PChan3mods", name = "PChan3mods", version = "0.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec = @SidedPacketHandler(channels = {"Steamship" }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(channels = {"Steamship" }, packetHandler = ServerPacketHandler.class))
public class PChan3mods{
	@Instance("PChan3mods")
	public static PChan3mods instance;
	@SidedProxy(clientSide="pchan3.ClientProxy", serverSide="pchan3.CommonProxy")
	public static CommonProxy proxy;
	private static int steamboatItemID=12500,airshipItemID=12503,engineItemID=12502,balloonItemID=12501;
	private static int  airshipEntityID=1,steamboatEntityID=2,pirateEntityID=3;
    public static Item airShip,engine,balloon,steamBoat;  
    public static int KEY_UP = Keyboard.KEY_NUMPAD8,KEY_DOWN = Keyboard.KEY_NUMPAD2;
    public static int KEY_CHEST = Keyboard.KEY_R,KEY_FIRE = Keyboard.KEY_NUMPAD5;
    public static int GUI_ID=100;
    public static boolean SHOW_BOILER = false;
    private static World world;
	
  @PreInit
  public void preload(FMLPreInitializationEvent event){
	  instance=this;
	  proxy.preloadTextures();
	  // Read properties file.
		Properties properties = new Properties();
		File file=new File(getMinecraftBaseDir()+ "/config/airship.properties");
		if (file.exists()){
		try {
		    properties.load(new FileInputStream(getMinecraftBaseDir()+ "/config/airship.properties"));

		    String temp = properties.getProperty("show_boiler");
		    if (temp.contains("true")) {
			SHOW_BOILER = true;
		    } else {
			SHOW_BOILER = false;
		    }
		    airshipEntityID=Integer.parseInt(properties.getProperty("AirshipEntityID"));
		    steamboatEntityID=Integer.parseInt(properties.getProperty("SteamboatEntityID"));
		    pirateEntityID=Integer.parseInt(properties.getProperty("PirateEntityID"));
		    airshipItemID=Integer.parseInt(properties.getProperty("AirshipItemID"));
		    engineItemID=Integer.parseInt(properties.getProperty("EngineItemID"));
		    balloonItemID=Integer.parseInt(properties.getProperty("BalloonItemID"));
		    steamboatItemID=Integer.parseInt(properties.getProperty("SteamboatItemID"));
		    GUI_ID=Integer.parseInt(properties.getProperty("GUI_ID"));

		} catch (IOException e) {}
		}
		else{
		    properties.setProperty("show_boiler", "true");
		    properties.setProperty("AirshipEntityID", "1");
		    properties.setProperty("SteamboatEntityID","2");
		    properties.setProperty("PirateEntityID", "3");
		    properties.setProperty("AirshipItemID", "12503");
		    properties.setProperty("EngineItemID", "12502");
		    properties.setProperty("BalloonItemID", "12501");
		    properties.setProperty("SteamboatItemID","12500");
		    properties.setProperty("GUI_ID", "100");
		    }

		// Write properties file.
		try {
		    properties.store(new FileOutputStream(getMinecraftBaseDir()
			    + "/config/airship.properties"), null);
		} catch (IOException e) {}
  }
  @Init
    public void load(FMLInitializationEvent event) { 
	  
     if (airshipItemID!=0 && airshipEntityID!=0)
	{
	// Engine
	engine = new Item(engineItemID).setIconIndex(1).setTextureFile("/pchan3/gui/items.png")
		    .setItemName("Engine").setCreativeTab(CreativeTabs.tabTransport);
	LanguageRegistry.addName(engine, "Engine");
	GameRegistry.addRecipe(new ItemStack(engine, 1), new Object[]{
	    "###",
	    "#X#",
	    "###",
	    Character.valueOf('#'), Item.ingotIron,
	    Character.valueOf('X'), Block.pistonBase});
     
  // Balloon
	balloon = new Item(balloonItemID).setIconIndex(2).setTextureFile("/pchan3/gui/items.png")
		    .setItemName("Balloon").setCreativeTab(CreativeTabs.tabTransport);
	LanguageRegistry.addName(balloon, "Balloon");
	GameRegistry.addRecipe(new ItemStack(balloon, 1), new Object[]{
	    "###",
	    "###",
	    "L L",
	    Character.valueOf('#'), Item.leather,
	    Character.valueOf('L'), Item.silk});
	
	//AirShip
    airShip = new ItemAirship(airshipItemID).setIconIndex(0).setTextureFile("/pchan3/gui/items.png")
    		.setItemName("Airship");
	LanguageRegistry.addName(airShip, "Airship");
	EntityRegistry.registerModEntity(EntityAirship.class, "Airship", airshipEntityID, this, 40, 1, true);
	GameRegistry.addRecipe(new ItemStack(airShip, 1), new Object[]{
	    "XBX",
	    "EFE",
	    "XDX",
	    Character.valueOf('X'), Item.silk,
	    Character.valueOf('B'), balloon,
	    //Character.valueOf('C'), Block.chest,
	    Character.valueOf('E'), engine,
	    //Character.valueOf('L'), Block.dispenser,
	    Character.valueOf('D'), Item.boat,
	    Character.valueOf('F'), Block.stoneOvenIdle});
	
	NetworkRegistry.instance().registerGuiHandler(this, proxy);	
	}
	//Boat
     if (steamboatItemID!=0 && steamboatEntityID!=0)
 	{
	steamBoat = new ItemSteamBoat(steamboatItemID).setIconIndex(3).setTextureFile("/pchan3/gui/items.png")
			.setItemName("Steamboat").setCreativeTab(CreativeTabs.tabTransport);
	LanguageRegistry.addName(steamBoat, "Steam Boat");
	EntityRegistry.registerModEntity(EntitySteamBoat.class, "SteamBoat", steamboatEntityID,this,40,1,false);
	GameRegistry.addRecipe(new ItemStack(steamBoat, 1), new Object[] {
        "#X#",
        "###",
        Character.valueOf('#'), Block.planks,
        Character.valueOf('X'), Item.ingotIron
    });
 	}
	
	proxy.registerRenderInformation();
	//Pirate
	if (pirateEntityID!=0)
	{
	EntityRegistry.registerModEntity(EntityPirate.class, "Pirate", pirateEntityID, this, 80, 1, true);      
	EntityRegistry.addSpawn(EntityPirate.class, 6,5,5, EnumCreatureType.monster, new BiomeGenBase[]{BiomeGenBase.swampland,BiomeGenBase.plains,BiomeGenBase.taiga});
	LanguageRegistry.instance().addStringLocalization("entity.Pirate.name", "en_US", "Pirate");
	}
	 
  }  
  public static File getMinecraftBaseDir()
  {
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
      {
          return FMLClientHandler.instance().getClient().getMinecraftDir();
      }         
      return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");
  }
}
