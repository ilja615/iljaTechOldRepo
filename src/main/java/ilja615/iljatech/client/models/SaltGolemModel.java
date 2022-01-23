package ilja615.iljatech.client.models;

// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings (but i just self convert it h)

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class SaltGolemModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart arm1;
	private final ModelPart arm2;
	private final ModelPart head;

	public SaltGolemModel(ModelPart root) {
		this.body = root.getChild("body");
		this.leg1 = root.getChild("leg1");
		this.leg2 = root.getChild("leg2");
		this.arm1 = root.getChild("arm1");
		this.arm2 = root.getChild("arm2");
		this.head = root.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition def = meshdefinition.getRoot();

		def.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -22.0F, -3.0F, 12.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		def.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(36, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 16.0F, 0.0F));
		def.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(28, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 16.0F, 0.0F));
		def.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(-6.0F, 2.0F, 0.0F));
		def.getChild("arm1").addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(14, 32).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 19.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F));
		def.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(6.0F, 2.0F, 0.0F));
		def.getChild("arm2").addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, 0.0F, -2.0F, 3.0F, 19.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F));
		def.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-5.0F, -5.0F, -3.0F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(26, 26).addBox(-5.0F, -7.0F, -3.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
		def.getChild("head").addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(36, 12).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -5.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		def.getChild("head").addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(36, 19).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -5.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, buffer, packedLight, packedOverlay);
		leg1.render(poseStack, buffer, packedLight, packedOverlay);
		leg2.render(poseStack, buffer, packedLight, packedOverlay);
		arm1.render(poseStack, buffer, packedLight, packedOverlay);
		arm2.render(poseStack, buffer, packedLight, packedOverlay);
		head.render(poseStack, buffer, packedLight, packedOverlay);
	}
}