package alwaysxp.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.util.Nullables;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    @Nullable
    public abstract DamageSource getRecentDamageSource();


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(
            method = "dropExperience",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    @Expression("this.playerHitTimer > 0")
    @Definition(
            id = "playerHitTimer",
            field = "Lnet/minecraft/entity/LivingEntity;playerHitTimer:I"
    )
    private boolean ignorePlayerInXpCalc(boolean original) {
        if (Boolean.TRUE.equals(
                Nullables.map(
                        this.getRecentDamageSource(),
                        source -> source.isOf(
                                DamageTypes.GENERIC_KILL
                        )
                )
        )) {
            return original;
        }
        return true;
    }
}
