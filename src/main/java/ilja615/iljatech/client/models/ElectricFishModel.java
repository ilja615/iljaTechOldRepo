package ilja615.iljatech.client.models;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16 (but i just self convert it h)

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;

public class ElectricFishModel<T extends LivingEntity> extends EntityModel<T>
{
	protected final ModelPart body;
	protected final ModelPart mouth;
	protected final ModelPart topfin;
	protected final ModelPart backfin;
	protected final ModelPart whisker1;
	protected final ModelPart whisker2;
	protected final ModelPart fin1;
	protected final ModelPart fin2;

	public ElectricFishModel(ModelPart part)
	{
		super(RenderType::entityCutoutNoCull);

		this.whisker1 = part.getChild("whisker1");
		this.whisker2 = part.getChild("whisker2");
		this.fin1 = part.getChild("fin1");
		this.fin2 = part.getChild("fin2");
		this.body = part.getChild("body");
		this.mouth = part.getChild("mouth");
		this.topfin = part.getChild("topfin");
		this.backfin = part.getChild("backfin");

		this.whisker1.setPos(-4.0F, 19.5F, -6.0F);
		this.whisker2.setPos(2.0F, 19.5F, -6.0F);
		this.fin1.setPos(-4.0F, 17.0F, -2.0F);
		this.fin1.setRotation(0.0F, -0.4363F, 0.0F);
		this.fin2.setPos(2.0F, 17.0F, -2.0F);
		this.fin2.setRotation(0.0F, 0.4363F, 0.0F);
		this.body.setPos(-1.0F, 17.0F, 0.0F);
		this.mouth.setPos(0.0F, 24.0F, 0.0F);
		this.topfin.setPos(2.0F, 19.0F, -6.0F);
		this.backfin.setPos(2.0F, 19.0F, -6.0F);
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition modelDefinition = new MeshDefinition();
		PartDefinition def = modelDefinition.getRoot();
		def.addOrReplaceChild("whisker1", CubeListBuilder.create().texOffs(26, 6).addBox(-8.0F, -2.5F, 0.0F, 8.0F, 6.0F, 0.0F), PartPose.ZERO);
		def.addOrReplaceChild("whisker2", CubeListBuilder.create().texOffs(26, 0).addBox(0.0F, -2.5F, 0.0F, 8.0F, 6.0F, 0.0F), PartPose.ZERO);
		def.addOrReplaceChild("fin1", CubeListBuilder.create().texOffs(18, 23).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 9.0F), PartPose.ZERO);
		def.addOrReplaceChild("fin2", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 9.0F), PartPose.ZERO);
		def.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -6.0F, 6.0F, 10.0F, 14.0F), PartPose.ZERO);
		def.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(20, 24).addBox(-4.5F, -7.0F, -8.0F, 7.0F, 4.0F, 2.0F), PartPose.ZERO);
		def.addOrReplaceChild("topfin", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -15.0F, 6.0F, 0.0F, 8.0F, 10.0F), PartPose.ZERO);
		def.addOrReplaceChild("backfin", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -7.0F, 14.0F, 0.0F, 10.0F, 8.0F), PartPose.ZERO);

		return LayerDefinition.create(modelDefinition, 64, 64);
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
	{
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		this.whisker1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.whisker2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fin1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fin2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.mouth.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.topfin.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.backfin.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}