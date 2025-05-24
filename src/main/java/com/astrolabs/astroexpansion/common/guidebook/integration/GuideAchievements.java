package com.astrolabs.astroexpansion.common.guidebook.integration;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Handles achievement/advancement integration for the guide book
 */
public class GuideAchievements {
    
    // Advancement IDs
    public static final ResourceLocation FIRST_OPEN = new ResourceLocation(AstroExpansion.MODID, "guide/first_open");
    public static final ResourceLocation BOOKWORM = new ResourceLocation(AstroExpansion.MODID, "guide/bookworm");
    public static final ResourceLocation COMPLETIONIST = new ResourceLocation(AstroExpansion.MODID, "guide/completionist");
    public static final ResourceLocation QUIZ_MASTER = new ResourceLocation(AstroExpansion.MODID, "guide/quiz_master");
    public static final ResourceLocation EXPLORER = new ResourceLocation(AstroExpansion.MODID, "guide/explorer");
    
    /**
     * Grant advancement when player first opens the guide book
     */
    public static void onFirstOpen(ServerPlayer player) {
        grantAdvancement(player, FIRST_OPEN);
    }
    
    /**
     * Check and grant bookworm advancement (read 50% of entries)
     */
    public static void checkBookwormProgress(ServerPlayer player) {
        var progress = AstroGuideBook.getInstance().getProgress(player);
        float completion = progress.getTotalCompletion();
        
        if (completion >= 0.5f) {
            grantAdvancement(player, BOOKWORM);
        }
    }
    
    /**
     * Check and grant completionist advancement (read 100% of entries)
     */
    public static void checkCompletionistProgress(ServerPlayer player) {
        var progress = AstroGuideBook.getInstance().getProgress(player);
        float completion = progress.getTotalCompletion();
        
        if (completion >= 1.0f) {
            grantAdvancement(player, COMPLETIONIST);
        }
    }
    
    /**
     * Grant quiz master advancement (perfect score on all quizzes)
     */
    public static void checkQuizMasterProgress(ServerPlayer player) {
        var progress = AstroGuideBook.getInstance().getProgress(player);
        
        // Check if all quizzes have perfect scores
        boolean allPerfect = true;
        int quizCount = 0;
        
        for (var entry : AstroGuideBook.getInstance().getAllEntries()) {
            for (var page : entry.getPages()) {
                if (page.getType().equals("quiz")) {
                    quizCount++;
                    // Check quiz score
                    // TODO: Get quiz ID from page and check score
                }
            }
        }
        
        if (allPerfect && quizCount >= 5) {
            grantAdvancement(player, QUIZ_MASTER);
        }
    }
    
    /**
     * Grant explorer advancement (discover all hidden entries)
     */
    public static void checkExplorerProgress(ServerPlayer player) {
        var progress = AstroGuideBook.getInstance().getProgress(player);
        
        // Count hidden/secret entries
        int hiddenCount = 0;
        int foundHidden = 0;
        
        for (var entry : AstroGuideBook.getInstance().getAllEntries()) {
            if (entry.getTags().contains("hidden") || entry.getTags().contains("secret")) {
                hiddenCount++;
                if (progress.getViewedEntries().contains(entry.getId())) {
                    foundHidden++;
                }
            }
        }
        
        if (hiddenCount > 0 && foundHidden == hiddenCount) {
            grantAdvancement(player, EXPLORER);
        }
    }
    
    /**
     * Create custom trigger for guide book events
     */
    public static class GuideBookTrigger extends SimpleCriterionTrigger<GuideBookTrigger.TriggerInstance> {
        private static final ResourceLocation TRIGGER_ID = new ResourceLocation(AstroExpansion.MODID, "guide_book_event");
        
        @Override
        public ResourceLocation getId() {
            return TRIGGER_ID;
        }
        
        @Override
        protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context) {
            String action = json.get("action").getAsString();
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            return new TriggerInstance(predicate, action, count);
        }
        
        public void trigger(ServerPlayer player, String action, int count) {
            this.trigger(player, instance -> instance.matches(action, count));
        }
        
        public static class TriggerInstance extends AbstractCriterionTriggerInstance {
            private final String action;
            private final int count;
            
            public TriggerInstance(ContextAwarePredicate predicate, String action, int count) {
                super(TRIGGER_ID, predicate);
                this.action = action;
                this.count = count;
            }
            
            public boolean matches(String action, int count) {
                return this.action.equals(action) && count >= this.count;
            }
            
            @Override
            public JsonObject serializeToJson(SerializationContext context) {
                JsonObject json = super.serializeToJson(context);
                json.addProperty("action", action);
                json.addProperty("count", count);
                return json;
            }
        }
    }
    
    /**
     * Register custom criterion triggers
     */
    public static void registerTriggers() {
        CriteriaTriggers.register(new GuideBookTrigger());
    }
    
    /**
     * Grant an advancement to a player
     */
    private static void grantAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        var advancement = player.server.getAdvancements().getAdvancement(advancementId);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }
    
    /**
     * Create guide book related advancements
     */
    public static void createAdvancements() {
        // These would normally be defined in data/astroexpansion/advancements/guide/
        // But we can generate them programmatically if needed
    }
}