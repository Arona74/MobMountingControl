package io.arona74.mobmountingcontrol.mixins;

import io.arona74.mobmountingcontrol.MountingConfig;
import io.arona74.mobmountingcontrol.MobMountingControl;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMountMixin {
    
    /**
     * Check on every tick if entity should be dismounted
     * Using intermediary method name for tick
     */
    @Inject(method = "method_5773", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        
        // Check if this entity is riding something it shouldn't be
        if (thisEntity.getVehicle() != null) {
            Entity vehicle = thisEntity.getVehicle();
            if (!MountingConfig.canEntityMount(thisEntity, vehicle)) {
                // Force dismount if mounting is not allowed
                thisEntity.stopRiding();
                if (MountingConfig.logMountingAttempts) {
                    MobMountingControl.LOGGER.info("MobMountingControl - Forcibly dismounted {} from {}", 
                        MountingConfig.getEntityId(thisEntity), 
                        MountingConfig.getEntityId(vehicle));
                }
            }
        }
    }
}