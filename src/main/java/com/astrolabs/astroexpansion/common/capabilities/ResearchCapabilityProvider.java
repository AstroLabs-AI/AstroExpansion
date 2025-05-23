package com.astrolabs.astroexpansion.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<ResearchCapability> RESEARCH_CAPABILITY = CapabilityManager.get(new CapabilityToken<ResearchCapability>() {});
    
    private ResearchCapability research = null;
    private final LazyOptional<ResearchCapability> optional = LazyOptional.of(this::createResearch);
    
    private ResearchCapability createResearch() {
        if (research == null) {
            research = new ResearchCapability();
        }
        return research;
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == RESEARCH_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createResearch().saveNBTData(nbt);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createResearch().loadNBTData(nbt);
    }
}