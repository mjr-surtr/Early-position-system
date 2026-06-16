package com.besson.tutorial.block;

import com.besson.tutorial.item.Moditems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EarlyBrewingStandBlockEntity extends BlockEntity implements MenuProvider {
    private static final int TOTAL_SLOTS = 5;
    private static final int SLOT_INGREDIENT = 3;
    private static final int SLOT_FUEL = 4;
    private static final int BREW_TIME_MAX = 400;
    private static final int FUEL_USES = 20;

    // 自定义酿造配方：原料 → 药水产物
    private static final Map<Item, Item> CUSTOM_RECIPES = new HashMap<>();
    static {
        CUSTOM_RECIPES.put(Items.CARROT, Moditems.EARLY_NIGHT_VISION_POTION.get());
        CUSTOM_RECIPES.put(Items.BEEF, Moditems.EARLY_STRENGTH_POTION.get());
        CUSTOM_RECIPES.put(Items.APPLE, Moditems.EARLY_HEALING_POTION.get());
        CUSTOM_RECIPES.put(Items.SALMON, Moditems.EARLY_WATER_BREATHING_POTION.get());
        CUSTOM_RECIPES.put(Items.CHICKEN, Moditems.EARLY_MINING_POTION.get());
    }

    private NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    int brewTime;
    int fuel;
    private Item ingredient;
    private boolean[] pItems = new boolean[3];

    public final Container container = new SimpleContainer(TOTAL_SLOTS) {
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            EarlyBrewingStandBlockEntity.this.setChanged();
        }
        @Override public void setChanged() {
            EarlyBrewingStandBlockEntity.this.setChanged();
        }
        @Override public boolean stillValid(Player player) {
            return EarlyBrewingStandBlockEntity.this.stillValid(player);
        }
        @Override public int getContainerSize() { return TOTAL_SLOTS; }
        @Override public boolean isEmpty() {
            for (ItemStack stack : items) if (!stack.isEmpty()) return false;
            return true;
        }
        @Override public ItemStack removeItem(int slot, int amount) {
            ItemStack result = ContainerHelper.removeItem(items, slot, amount);
            if (!result.isEmpty()) EarlyBrewingStandBlockEntity.this.setChanged();
            return result;
        }
        @Override public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(items, slot);
        }
        @Override public void clearContent() {
            items.clear();
            EarlyBrewingStandBlockEntity.this.setChanged();
        }
    };

    public final ContainerData dataAccess = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> brewTime;
                case 1 -> fuel;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> brewTime = value;
                case 1 -> fuel = value;
            }
        }
        @Override public int getCount() { return 2; }
    };

    public EarlyBrewingStandBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.EARLY_BREWING_STAND_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tutorial_mod.early_brewing_stand");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EarlyBrewingStandMenu(id, inv, this);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("BrewTime", (short) this.brewTime);
        tag.putByte("Fuel", (byte) this.fuel);
        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.brewTime = tag.getShort("BrewTime");
        this.fuel = tag.getByte("Fuel");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EarlyBrewingStandBlockEntity entity) {
        ItemStack fuelStack = entity.items.get(SLOT_FUEL);
        if (entity.fuel <= 0 && isFuel(fuelStack)) {
            entity.fuel = FUEL_USES;
            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }

        boolean canBrew = entity.canBrew();
        boolean isBrewing = entity.brewTime > 0;

        if (isBrewing) {
            entity.brewTime--;
            boolean validIngredient = !entity.items.get(SLOT_INGREDIENT).isEmpty()
                    && entity.items.get(SLOT_INGREDIENT).is(entity.ingredient);
            boolean slotsValid = true;
            for (int i = 0; i < 3; i++) {
                if (entity.pItems[i]) {
                    if (entity.items.get(i).isEmpty()) {
                        slotsValid = false;
                        break;
                    }
                }
            }
            if (!validIngredient || !slotsValid || entity.fuel <= 0) {
                entity.brewTime = 0;
                setChanged(level, pos, state);
            } else if (entity.brewTime == 0) {
                entity.doBrew();
                setChanged(level, pos, state);
            }
        } else if (canBrew && entity.fuel > 0) {
            entity.brewTime = BREW_TIME_MAX;
            entity.ingredient = entity.items.get(SLOT_INGREDIENT).getItem();
            for (int i = 0; i < 3; i++) {
                entity.pItems[i] = !entity.items.get(i).isEmpty();
            }
            setChanged(level, pos, state);
        } else {
            if (entity.brewTime != 0) {
                entity.brewTime = 0;
                setChanged(level, pos, state);
            }
        }
    }

    /**
     * 判断是否可以酿造：支持自定义配方和原版配方
     */
    private boolean canBrew() {
        ItemStack ingredientStack = this.items.get(SLOT_INGREDIENT);
        if (ingredientStack.isEmpty()) return false;

        Item ingredientItem = ingredientStack.getItem();

        // 1. 自定义配方：检查是否有水瓶 + 自定义原料
        if (CUSTOM_RECIPES.containsKey(ingredientItem)) {
            for (int i = 0; i < 3; i++) {
                ItemStack bottle = this.items.get(i);
                if (!bottle.isEmpty() && isWaterBottle(bottle)) {
                    return true;
                }
            }
            return false;
        }

        // 2. 原版配方：使用PotionBrewing判断
        if (!PotionBrewing.isIngredient(ingredientStack)) return false;
        for (int i = 0; i < 3; i++) {
            ItemStack bottle = this.items.get(i);
            if (!bottle.isEmpty() && PotionBrewing.hasMix(bottle, ingredientStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行酿造：优先自定义配方，否则走原版
     */
    private void doBrew() {
        ItemStack ingredientStack = this.items.get(SLOT_INGREDIENT);
        Item ingredientItem = ingredientStack.getItem();

        // 自定义配方
        if (CUSTOM_RECIPES.containsKey(ingredientItem)) {
            Item resultItem = CUSTOM_RECIPES.get(ingredientItem);
            for (int i = 0; i < 3; i++) {
                ItemStack bottle = this.items.get(i);
                if (!bottle.isEmpty() && isWaterBottle(bottle)) {
                    this.items.set(i, new ItemStack(resultItem));
                }
            }
            ingredientStack.shrink(1);
            this.fuel--;
            this.brewTime = 0;
            Arrays.fill(this.pItems, false);
            return;
        }

        // 原版配方
        for (int i = 0; i < 3; i++) {
            ItemStack bottle = this.items.get(i);
            if (!bottle.isEmpty() && PotionBrewing.hasMix(bottle, ingredientStack)) {
                ItemStack result = PotionBrewing.mix(ingredientStack, bottle);
                this.items.set(i, result);
            }
        }
        ingredientStack.shrink(1);
        this.fuel--;
        this.brewTime = 0;
        Arrays.fill(this.pItems, false);
    }

    /**
     * 判断物品是否为水瓶（支持POTION和GLASS_BOTTLE）
     */
    private static boolean isWaterBottle(ItemStack stack) {
        if (stack.is(Items.GLASS_BOTTLE)) return true;
        if (stack.is(Items.POTION)) {
            // 检查是否为原版水瓶（无效果的POTION = 水瓶）
            return PotionUtils.getPotion(stack) == Potions.WATER;
        }
        return false;
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.is(Items.COAL) || stack.is(Items.CHARCOAL);
    }

    public static boolean isValidIngredient(ItemStack stack) {
        return PotionBrewing.isIngredient(stack) || CUSTOM_RECIPES.containsKey(stack.getItem());
    }
}
