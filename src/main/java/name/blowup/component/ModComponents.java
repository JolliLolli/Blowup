package name.blowup.component;

import name.blowup.Blowup;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * This class is used to register custom components for the mod.
 * There aren't any yet, but this is a good place to put them.
 * It contains a static field for the custom model data component.
 */
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