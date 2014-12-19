package com.biggestnerd.miscutil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod(modid="miscutil", name="Miscellaneous Utilities", version="v1.0.0")
public class MiscUtil {
	@Mod.Instance("MiscUtil")
	public static MiscUtil instance;
	  
	static boolean renderPigmen;
	static boolean animatePortals;
	static boolean loginOnKick;
	
	ServerData info;
	
	Minecraft mc = Minecraft.getMinecraft();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		renderPigmen = config.get(config.CATEGORY_GENERAL, "renderPigmen", false).getBoolean();
		animatePortals = config.get(config.CATEGORY_GENERAL, "animatePortals", false).getBoolean();
		loginOnKick = config.get(config.CATEGORY_GENERAL, "loginOnKick", true).getBoolean();
		
		config.save();
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	}
	  
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
	    if (mc.thePlayer != null && !animatePortals) {
	      mc.thePlayer.timeInPortal = 0.0F;
	    }
	    
	    if(mc.currentScreen instanceof GuiDisconnected) {
	    	if(info != null && loginOnKick) {
	    		
	    	}
	    }
	}
	
	@SubscribeEvent
	public void connectToServer(ClientConnectedToServerEvent e) {
		if(mc.theWorld != null) {
	    	info = mc.func_147104_D();
	    }
	}
	
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Pre e) {
		if(e.entity instanceof EntityPigZombie && !renderPigmen) {
			e.setCanceled(true);
		}
	}	
	
}
