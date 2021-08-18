package ilja615.iljatech.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PetrolymerHelmetModel<T extends LivingEntity> extends EntityModel<T>
{
	protected final ModelPart whisker1;
	protected final ModelPart whisker2;
	protected final ModelPart fin1;
	protected final ModelPart fin2;

	public PetrolymerHelmetModel(ModelPart part)
	{
		super(RenderType::entityCutoutNoCull);

		this.whisker1 = part.getChild("whisker1");
		this.whisker2 = part.getChild("whisker2");
		this.fin1 = part.getChild("fin1");
		this.fin2 = part.getChild("fin2");
	}

	@Override
	public void setupAnim(LivingEntity entity, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_)
	{
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition modelDefinition = new MeshDefinition();
		PartDefinition def = modelDefinition.getRoot();
		def.addOrReplaceChild("whisker1", CubeListBuilder.create().texOffs(0,0).addBox(-12.0F, -5.0F, -4.0F, 8, 5, 0, LayerDefinitions.INNER_ARMOR_DEFORMATION), PartPose.offset(0.0f, -6.5f,0.0f));
		def.addOrReplaceChild("whisker2", CubeListBuilder.create().texOffs(0,5).mirror().addBox(4.0F, -5.0F, -4.0F, 8, 5, 0, LayerDefinitions.INNER_ARMOR_DEFORMATION), PartPose.offset(0.0f, -6.5f,0.0f));

		def.addOrReplaceChild("fin1", CubeListBuilder.create().texOffs(16,0).addBox(0.0F, -3.0F, 0.0F, 6, 6, 0, LayerDefinitions.INNER_ARMOR_DEFORMATION), PartPose.offsetAndRotation(4.0F, -10.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
		def.addOrReplaceChild("fin2", CubeListBuilder.create().texOffs(16,6).addBox(-6.0F, -3.0F, 0.0F, 6, 6, 0, LayerDefinitions.INNER_ARMOR_DEFORMATION), PartPose.offsetAndRotation(-4.0F, -10.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(modelDefinition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		poseStack.pushPose();
		if (this.young)
		{
			poseStack.translate(0.0F, 0.75F, 0.0F);
			poseStack.scale(0.5F, 0.5F, 0.5F);
		}
		poseStack.popPose();
		this.whisker1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.whisker2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fin1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fin2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}