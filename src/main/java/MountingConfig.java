package io.arona74.mobmountingcontrol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Type;
import java.util.*;

public class MountingConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mobmountingcontrol.json");
    
    // Configuration fields
    public static boolean enableMod = true;
    public static boolean defaultAllowMounting = true;
    public static boolean playersAlwaysAllowed = true;
    public static List<String> entityBlacklist = new ArrayList<>();
    public static List<String> entityWhitelist = new ArrayList<>();
    
    // Vehicle control settings
    public static boolean defaultAllowVehicles = true;
    public static List<String> vehicleBlacklist = new ArrayList<>();
    public static List<String> vehicleWhitelist = new ArrayList<>();
    
    public static boolean logMountingAttempts = false;
    
    // Config class for JSON serialization
    private static class Config {
        public boolean enableMod = true;
        public boolean defaultAllowMounting = true;
        public boolean playersAlwaysAllowed = true;
        public List<String> entityBlacklist = Arrays.asList(
            "minecraft:zombie",
            "minecraft:skeleton",
            "minecraft:creeper",
            "minecraft:spider"
        );
        public List<String> entityWhitelist = Arrays.asList(
            "minecraft:villager",
            "minecraft:horse",
            "minecraft:donkey",
            "minecraft:mule"
        );
        
        // Vehicle control settings
        public boolean defaultAllowVehicles = true;
        public List<String> vehicleBlacklist = Arrays.asList(
            "minecraft:tnt_minecart"
        );
        public List<String> vehicleWhitelist = Arrays.asList(
            "minecraft:boat",
            "minecraft:chest_boat",
            "minecraft:minecart",
            "minecraft:chest_minecart",
            "minecraft:furnace_minecart",
            "minecraft:hopper_minecart"
        );
        
        public boolean logMountingAttempts = false;
        public String _comment1 = "Entity Control: Set defaultAllowMounting to false to use entity whitelist mode, true for blacklist mode";
        public String _comment2 = "Vehicle Control: Set defaultAllowVehicles to false to use vehicle whitelist mode, true for blacklist mode";
        public String _comment3 = "The mod will only control vehicles that pass the vehicle filter, then check entity permissions for those vehicles";
    }
    
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                // Create default config
                saveDefault();
                MobMountingControl.LOGGER.info("MobMountingControl - Created default configuration file");
                return;
            }
            
            String json = Files.readString(CONFIG_PATH);
            Config config = GSON.fromJson(json, Config.class);
            
            // Apply loaded values
            enableMod = config.enableMod;
            defaultAllowMounting = config.defaultAllowMounting;
            playersAlwaysAllowed = config.playersAlwaysAllowed;
            entityBlacklist = new ArrayList<>(config.entityBlacklist);
            entityWhitelist = new ArrayList<>(config.entityWhitelist);
            
            // Vehicle control settings
            defaultAllowVehicles = config.defaultAllowVehicles;
            vehicleBlacklist = new ArrayList<>(config.vehicleBlacklist);
            vehicleWhitelist = new ArrayList<>(config.vehicleWhitelist);
            
            logMountingAttempts = config.logMountingAttempts;
            
            MobMountingControl.LOGGER.info("MobMountingControl - Loaded configuration successfully");
            MobMountingControl.LOGGER.info("MobMountingControl - Entity Mode: {} || Blacklist: {} entities | Whitelist: {} entities", 
                defaultAllowMounting ? "Blacklist" : "Whitelist", 
                entityBlacklist.size(), 
                entityWhitelist.size());
            MobMountingControl.LOGGER.info("MobMountingControl - Vehicle Mode: {} || Blacklist: {} vehicles | Whitelist: {} vehicles", 
                defaultAllowVehicles ? "Blacklist" : "Whitelist", 
                vehicleBlacklist.size(), 
                vehicleWhitelist.size());
                
        } catch (Exception e) {
            MobMountingControl.LOGGER.error("MobMountingControl - Failed to load configuration, using defaults", e);
            loadDefaults();
        }
    }
    
    private static void saveDefault() throws IOException {
        Config defaultConfig = new Config();
        String json = GSON.toJson(defaultConfig);
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, json);
    }
    
    private static void loadDefaults() {
        Config defaultConfig = new Config();
        enableMod = defaultConfig.enableMod;
        defaultAllowMounting = defaultConfig.defaultAllowMounting;
        playersAlwaysAllowed = defaultConfig.playersAlwaysAllowed;
        entityBlacklist = new ArrayList<>(defaultConfig.entityBlacklist);
        entityWhitelist = new ArrayList<>(defaultConfig.entityWhitelist);
        
        // Vehicle control defaults
        defaultAllowVehicles = defaultConfig.defaultAllowVehicles;
        vehicleBlacklist = new ArrayList<>(defaultConfig.vehicleBlacklist);
        vehicleWhitelist = new ArrayList<>(defaultConfig.vehicleWhitelist);
        
        logMountingAttempts = defaultConfig.logMountingAttempts;
    }
    
    /**
     * Checks if an entity is allowed to mount a vehicle
     * @param entity The entity trying to mount
     * @param vehicle The vehicle being mounted
     * @return true if mounting is allowed, false otherwise
     */
    public static boolean canEntityMount(Entity entity, Entity vehicle) {
        if (!enableMod) {
            return true;
        }
        
        // Players are always allowed if configured
        if (playersAlwaysAllowed && entity instanceof PlayerEntity) {
            return true;
        }
        
        // First check if this vehicle type is controlled by the mod
        String vehicleId = getEntityId(vehicle);
        if (!isVehicleControlled(vehicleId)) {
            return true; // Not a vehicle we control
        }
        
        String entityId = getEntityId(entity);
        
        if (logMountingAttempts) {
            MobMountingControl.LOGGER.info("MobMountingControl - Mounting attempt: {} -> {}", entityId, vehicleId);
        }
        
        // Check entity permissions
        if (defaultAllowMounting) {
            // Blacklist mode: allow unless in blacklist
            boolean blocked = entityBlacklist.contains(entityId);
            if (blocked && logMountingAttempts) {
                MobMountingControl.LOGGER.info("MobMountingControl - Blocked mounting (entity blacklisted): {}", entityId);
            }
            return !blocked;
        } else {
            // Whitelist mode: deny unless in whitelist
            boolean allowed = entityWhitelist.contains(entityId);
            if (!allowed && logMountingAttempts) {
                MobMountingControl.LOGGER.info("MobMountingControl - Blocked mounting (entity not whitelisted): {}", entityId);
            }
            return allowed;
        }
    }
    
    /**
     * Checks if a vehicle type is controlled by this mod
     * @param vehicleId The vehicle entity ID
     * @return true if this vehicle type should be controlled
     */
    private static boolean isVehicleControlled(String vehicleId) {
        if (defaultAllowVehicles) {
            // Blacklist mode: control unless in blacklist
            return !vehicleBlacklist.contains(vehicleId);
        } else {
            // Whitelist mode: only control if in whitelist
            return vehicleWhitelist.contains(vehicleId);
        }
    }
    
    public static String getEntityId(Entity entity) {
        EntityType<?> type = entity.getType();
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        return id.toString();
    }
    
    /**
     * Reloads the configuration from disk
     */
    public static void reload() {
        load();
    }
}