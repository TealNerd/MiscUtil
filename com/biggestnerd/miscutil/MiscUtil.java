package com.biggestnerd.miscutil;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid="miscutil", name="Miscellaneous Utilities", version="v1.3")
public class MiscUtil {
	@Mod.Instance("MiscUtil")
	public static MiscUtil instance;
	Minecraft mc = Minecraft.getMinecraft();
	ServerData last;
	int counter = 0;
	
	static boolean renderPigmen;
	static boolean animatePortals;
	static boolean autoJoin;
	static boolean nameDist;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		renderPigmen = config.get(config.CATEGORY_GENERAL, "renderPigmen", false).getBoolean();
		animatePortals = config.get(config.CATEGORY_GENERAL, "animatePortals", false).getBoolean();
		autoJoin = config.get(config.CATEGORY_GENERAL, "autoJoin", true).getBoolean();
		nameDist = config.get(config.CATEGORY_GENERAL, "nameDist", true).getBoolean();
		
		config.save();
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	    
	}
	  
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
	    if (mc.thePlayer != null && !animatePortals) {
	      mc.thePlayer.timeInPortal = 0.0F;
	    }
	    if(mc.theWorld != null) {
	    	List players = mc.theWorld.loadedEntityList;
		      for(Object o : players) {
		    	if(o instanceof EntityOtherPlayerMP) {
		    		((EntityOtherPlayerMP) o).refreshDisplayName();
		    	}
		      }
	    }
	    if(mc.currentScreen instanceof GuiDisconnected) {
			if(counter <= 420) {
				counter++;
			}
		}
		if(mc.currentScreen instanceof GuiConnecting && mc.func_147104_D() != null) {
			last = mc.func_147104_D();
		}
		if(counter >= 450 && autoJoin) {
			mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, last));
			System.out.println("Connecting to " + last.serverIP);
			counter = 0;
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void nameFormat(NameFormat e) {
		if(nameDist) {
			e.displayname += " (" + (int) mc.thePlayer.getDistanceToEntity(e.entity) + "m)";
		}
	}
	
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Pre e) {
		if(e.entity instanceof EntityPigZombie && !renderPigmen) {
			e.setCanceled(true);
		}
	}
}
