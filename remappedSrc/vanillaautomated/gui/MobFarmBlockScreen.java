package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class MobFarmBlockScreen extends CottonInventoryScreen<MobFarmBlockController> {
    public MobFarmBlockScreen(MobFarmBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
