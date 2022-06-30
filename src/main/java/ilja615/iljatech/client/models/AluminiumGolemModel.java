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

public class AluminiumGolemModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelPart arm1;
	private final ModelPart arm2;
	private final ModelPart propellor;
	private final ModelPart bb_main;

	public AluminiumGolemModel(ModelPart part)
	{
		super(RenderType::entitySolid);

		this.arm1 = part.getChild("arm1");
		this.arm2 = part.getChild("arm2");
		this.propellor = part.getChild("propellor");
		this.bb_main = part.getChild("bb_main");

		this.arm1.setPos(6.0F, 8.0F, -1.5F);
		this.arm2.setPos(-6.0F, 8.0F, -1.5F);
		this.propellor.setPos(0.0F, -2.0F, 0.0F);
		this.bb_main.setPos(0.0F, 24.0F, 0.0F);
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition modelDefinition = new MeshDefinition();
		PartDefinition def = modelDefinition.getRoot();
		def.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(12, 37).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 8.0F, -1.5F));
		def.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 37).addBox(-3.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 8.0F, -1.5F));
		def.addOrReplaceChild("propellor", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));
		def.getChild("propellor").addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, -1.0F, -2.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));
		def.getChild("propellor").addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
		def.getChild("propellor").addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -1.0F, -12.0F, 4.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));
		def.getChild("propellor").addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 56).addBox(-12.0F, -1.0F, -2.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));
		def.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -27.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(modelDefinition, 64, 64);
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
	{
		//this.propellor.yRot = ;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		this.arm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.arm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.propellor.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.bb_main.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}