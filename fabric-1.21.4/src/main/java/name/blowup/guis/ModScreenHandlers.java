package name.blowup.guis;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;

import static name.blowup.Blowup.MOD_ID;
import static net.minecraft.util.math.BlockPos.PACKET_CODEC;

public final class ModScreenHandlers {
    private static final Logger LOG = LoggerFactory.getLogger("ModScreenHandlers");

    /** Call me once from your common ModInitializer to force the static fields to load. */
    public static void initialise() {
        LOG.info("Registering screen handlers");
    }

    /**
     * Registers an extended screen handler (the kind whose server‐ctor is (int, Inventory, PacketByteBuf))
     * by creating a new ExtendedScreenHandlerType<> with your handler’s constructor reference.
     */
    private static <T extends ScreenHandler> ScreenHandlerType<T> register(
            String name,
            ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory
    ) {
        Identifier id = Identifier.of(MOD_ID, name);
        ScreenHandlerType<T> type = Registry.register(
                Registries.SCREEN_HANDLER,
                id,
                new ExtendedScreenHandlerType<>(factory, PACKET_CODEC)
        );
        LOG.info(" → registered {}", id);
        return type;
    }

    // === actual registrations ===
    public static final ScreenHandlerType<DetonatorScreenHandler> DETONATOR = register(
            "detonator",
            DetonatorScreenHandler::new
    );
}
