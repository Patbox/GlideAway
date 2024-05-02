package eu.pb4.glideaway.mixin.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.Schema1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1460.class)
public abstract class Schema1460Mixin extends Schema {
    public Schema1460Mixin(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Inject(method = "registerEntities", at = @At("RETURN"))
    private void registerGlideAwayEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();

        schema.register(map, "glideaway:hang_glider", (n) -> DSL.optionalFields("stack", TypeReferences.ITEM_STACK.in(schema)));
    }

}