package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.blockentities.CrafterBlockEntity;

import java.util.ArrayList;

public class CrafterBlockController extends CottonCraftingController {
    public ArrayList<WItemSprite> itemSprites = new ArrayList<WItemSprite>();

    public CrafterBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, Text title, BlockPos blockPos, String recipeItems) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        String[] itemStrings = recipeItems.split(",");

        WMaxedPanel root = new WMaxedPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WLabel label = new WLabel(title);
        label.setAlignment(Alignment.CENTER);
        root.add(label, 0, 0, 160, 10);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 1, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(fuelSlot, 1, 2);

        int lastSlotIndex = 1;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                WItemSlot craftingSlot = WItemSlot.of(blockInventory, lastSlotIndex);
                machinePanel.add(craftingSlot, i + 3, j);
                WItemSprite item = new WItemSprite(new ItemStack(Registry.ITEM.get(Identifier.tryParse(itemStrings[i + j * 3])), 1));
                itemSprites.add(item);
                machinePanel.add(item, i + 3, j);
                lastSlotIndex++;
            }
        }

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 6, 1);

        WButton resetButton = new WButton(new LiteralText("X"));
        resetButton.setOnClick(new Runnable() {
            @Override
            public void run() {
                sendPacket(-10, blockPos);
            }
        });
        machinePanel.add(resetButton, 6, 2);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 10);
        machinePanel.add(outputSlot, 7, 1);

        root.add(machinePanel, 0, 10);

        WLabel inventoryLabel = new WLabel(new TranslatableText("container.inventory"));
        inventoryLabel.setSize(256, 10);

        root.add(inventoryLabel, 0, 64);
        root.add(this.createPlayerInventoryPanel(), 0, 74);
        root.validate(this);
    }

    private void sendPacket (int change, BlockPos blockPos) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(blockPos);
        // Send packet to server to change the block for us
        ClientSidePacketRegistry.INSTANCE.sendToServer(VanillaAutomated.crafter_reset_packet, passedData);
        ((CrafterBlockEntity)world.getBlockEntity(blockPos)).resetRecipeClient();
    }
}
