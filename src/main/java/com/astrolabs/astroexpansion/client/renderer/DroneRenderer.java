package com.astrolabs.astroexpansion.client.renderer;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.entities.drones.AbstractDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DroneRenderer<T extends AbstractDroneEntity> extends EntityRenderer<T> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(
        new ResourceLocation(AstroExpansion.MODID, "drone"), "main");
    
    private final DroneModel<T> model;
    private final ResourceLocation texture;
    
    public DroneRenderer(EntityRendererProvider.Context context, String textureType) {
        super(context);
        this.model = new DroneModel<>(context.bakeLayer(MODEL_LAYER));
        this.texture = new ResourceLocation(AstroExpansion.MODID, "textures/entity/drone_" + textureType + ".png");
    }
    
    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }
    
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        // Main body
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), 
            PartPose.offset(0.0F, 20.0F, 0.0F));
        
        // Propellers
        partdefinition.addOrReplaceChild("propeller1", CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 8.0F), 
            PartPose.offset(-4.0F, 16.0F, -4.0F));
        
        partdefinition.addOrReplaceChild("propeller2", CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 8.0F), 
            PartPose.offset(4.0F, 16.0F, -4.0F));
        
        partdefinition.addOrReplaceChild("propeller3", CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 8.0F), 
            PartPose.offset(-4.0F, 16.0F, 4.0F));
        
        partdefinition.addOrReplaceChild("propeller4", CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 8.0F), 
            PartPose.offset(4.0F, 16.0F, 4.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 32);
    }
    
    public static class DroneModel<T extends Entity> extends EntityModel<T> {
        private final ModelPart body;
        private final ModelPart propeller1;
        private final ModelPart propeller2;
        private final ModelPart propeller3;
        private final ModelPart propeller4;
        
        public DroneModel(ModelPart root) {
            this.body = root.getChild("body");
            this.propeller1 = root.getChild("propeller1");
            this.propeller2 = root.getChild("propeller2");
            this.propeller3 = root.getChild("propeller3");
            this.propeller4 = root.getChild("propeller4");
        }
        
        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            // Rotate propellers
            float rotation = ageInTicks * 0.5F;
            this.propeller1.yRot = rotation;
            this.propeller2.yRot = -rotation;
            this.propeller3.yRot = rotation;
            this.propeller4.yRot = -rotation;
        }
        
        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            body.render(poseStack, buffer, packedLight, packedOverlay);
            propeller1.render(poseStack, buffer, packedLight, packedOverlay);
            propeller2.render(poseStack, buffer, packedLight, packedOverlay);
            propeller3.render(poseStack, buffer, packedLight, packedOverlay);
            propeller4.render(poseStack, buffer, packedLight, packedOverlay);
        }
    }
}