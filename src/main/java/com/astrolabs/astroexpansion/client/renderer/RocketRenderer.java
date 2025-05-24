package com.astrolabs.astroexpansion.client.renderer;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RocketRenderer<T extends Entity> extends EntityRenderer<T> {
    public static final ModelLayerLocation ROCKET_LAYER = new ModelLayerLocation(
        new ResourceLocation(AstroExpansion.MODID, "rocket"), "main");
    
    private final ResourceLocation texture;
    private final RocketModel<T> model;
    
    public RocketRenderer(EntityRendererProvider.Context context, String type) {
        super(context);
        this.texture = new ResourceLocation(AstroExpansion.MODID, "textures/entity/rocket_" + type + ".png");
        this.model = new RocketModel<>(context.bakeLayer(ROCKET_LAYER));
    }
    
    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        // Render the model
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(texture));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 
            1.0F, 1.0F, 1.0F, 1.0F);
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
    
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        // Simple rocket shape
        partdefinition.addOrReplaceChild("body", 
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -24.0F, -4.0F, 8.0F, 16.0F, 8.0F), 
            PartPose.offset(0.0F, 24.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("nose", 
            CubeListBuilder.create()
                .texOffs(0, 24)
                .addBox(-3.0F, -32.0F, -3.0F, 6.0F, 8.0F, 6.0F), 
            PartPose.offset(0.0F, 24.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("fin1", 
            CubeListBuilder.create()
                .texOffs(32, 0)
                .addBox(-8.0F, -8.0F, -1.0F, 4.0F, 8.0F, 2.0F), 
            PartPose.offset(0.0F, 24.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("fin2", 
            CubeListBuilder.create()
                .texOffs(32, 0)
                .addBox(4.0F, -8.0F, -1.0F, 4.0F, 8.0F, 2.0F), 
            PartPose.offset(0.0F, 24.0F, 0.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    
    static class RocketModel<T extends Entity> extends EntityModel<T> {
        private final ModelPart body;
        private final ModelPart nose;
        private final ModelPart fin1;
        private final ModelPart fin2;
        
        public RocketModel(ModelPart root) {
            this.body = root.getChild("body");
            this.nose = root.getChild("nose");
            this.fin1 = root.getChild("fin1");
            this.fin2 = root.getChild("fin2");
        }
        
        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, 
                             float netHeadYaw, float headPitch) {
            // No animation needed for rockets
        }
        
        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, 
                                  int packedOverlay, float red, float green, float blue, float alpha) {
            body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            nose.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            fin1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            fin2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}