package io.arona74.mobmountingcontrol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobMountingControl implements ModInitializer {
    public static final String MOD_ID = "mobmountingcontrol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static MinecraftServer server;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Mob Mounting Control mod");
        
        // Load configuration
        MountingConfig.load();
        
        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        
        LOGGER.info("Mob Mounting Control mod initialized successfully");
    }
    
    private void onServerStarting(MinecraftServer server) {
        MobMountingControl.server = server;
        LOGGER.info("Server starting - Mob Mounting Control is active");
    }
    
    private void onServerStopping(MinecraftServer server) {
        MobMountingControl.server = null;
        LOGGER.info("Server stopping - Mob Mounting Control is shutting down");
    }
    
    public static MinecraftServer getServer() {
        return server;
    }
}