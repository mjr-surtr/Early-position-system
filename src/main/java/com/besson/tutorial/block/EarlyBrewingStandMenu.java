package com.besson.tutorial.block;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EarlyBrewingStandMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    private static final int SLOT_POTION_0 = 0;
    private static final int SLOT_POTION_1 = 1;
    private static final int SLOT_POTION_2 = 2;
    private static final int SLOT_INGREDIENT = 3;
    private static final int SLOT_FUEL = 4;

    // Server constructor
    public EarlyBrewingStandMenu(int id, Inventory inv, EarlyBrewingStandBlockEntity entity) {
        super(ModBlocks.EARLY_BREWING_STAND_MENU.get(), id);
        this.container = entity.container;
        this.data = entity.dataAccess;
        addSlots(inv);
        addDataSlots(data);
    }

    // Client constructor (from network)
    public EarlyBrewingStandMenu(int id, Inventory inv) {
        super(ModBlocks.EARLY_BREWING_STAND_MENU.get(), id);
        this.container = new SimpleContainer(5);
        this.data = new SimpleContainerData(2);
        addSlots(inv);
        addDataSlots(data);
    }

    private void addSlots(Inventory inv) {
        // Potion slots (bottom 3)
        this.addSlot(new Slot(container, SLOT_POTION_0, 56, 51) {
            @Override public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.POTION)
                        || stack.is(Items.LINGERING_POTION) || stack.is(Items.SPLASH_POTION);
            }
        });
        this.addSlot(new Slot(container, SLOT_POTION_1, 79, 58) {
            @Override public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.POTION)
                        || stack.is(Items.LINGERING_POTION) || stack.is(Items.SPLASH_POTION);
            }
        });
        this.addSlot(new Slot(container, SLOT_POTION_2, 102, 51) {
            @Override public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.POTION)
                        || stack.is(Items.LINGERING_POTION) || stack.is(Items.SPLASH_POTION);
            }
        });

        // Ingredient slot (top center)
        this.addSlot(new Slot(container, SLOT_INGREDIENT, 79, 17) {
            @Override public boolean mayPlace(ItemStack stack) {
                return EarlyBrewingStandBlockEntity.isValidIngredient(stack);
            }
        });

        // Fuel slot (left)
        this.addSlot(new Slot(container, SLOT_FUEL, 17, 17) {
            @Override public boolean mayPlace(ItemStack stack) {
                return EarlyBrewingStandBlockEntity.isFuel(stack);
            }
        });

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < 5) {
                // From brewing stand to inventory
                if (!this.moveItemStackTo(stackInSlot, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From inventory to brewing stand
                if (EarlyBrewingStandBlockEntity.isFuel(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, SLOT_FUEL, SLOT_FUEL + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (EarlyBrewingStandBlockEntity.isValidIngredient(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, SLOT_INGREDIENT, SLOT_INGREDIENT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.is(Items.GLASS_BOTTLE) || stackInSlot.is(Items.POTION)
                        || stackInSlot.is(Items.LINGERING_POTION) || stackInSlot.is(Items.SPLASH_POTION)) {
                    if (!this.moveItemStackTo(stackInSlot, SLOT_POTION_0, SLOT_POTION_2 + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.moveItemStackTo(stackInSlot, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41) {
                    if (!this.moveItemStackTo(stackInSlot, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public int getBrewTime() {
        return data.get(0);
    }

    public int getFuel() {
        return data.get(1);
    }

    public int getBrewTimeMax() {
        return 400;
    }

    public int getFuelMax() {
        return 20;
    }
}
