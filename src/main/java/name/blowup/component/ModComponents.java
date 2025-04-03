package name.blowup.component;

import name.blowup.Blowup;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModComponents {
    public static final ComponentType<?> CUSTOM_MODEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Blowup.MOD_ID, "custom_model_data"),
            ComponentType.<Integer>builder().codec(null).build()
    );

    protected static void initialize() {
        Blowup.LOGGER.info("Registering {} components", Blowup.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized

    }
}