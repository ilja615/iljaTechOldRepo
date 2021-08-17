package ilja615.iljatech.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ilja615.iljatech.client.models.ArmorBaseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.swing.text.html.parser.Entity;

public class PetrolymerHelmetModel extends ArmorBaseModel
{
	public static final ResourceLocation ourTexture = new ResourceLocation("iljatech:textures/models/armor/petrolymer_helmet.png");
	protected final ModelPart whisker1;
	protected final ModelPart whisker2;
	protected final ModelPart fin1;
	protected final ModelPart fin2;

	public PetrolymerHelmetModel(ModelPart part)
	{
		super(part, ourTexture);

		this.whisker1 = part.getChild("whisker1");
		this.whisker2 = part.getChild("whisker2");
		this.fin1 = part.getChild("fin1");
		this.fin2 = part.getChild("fin2");
	}

	@Override
	public void setupAnim(LivingEntity p_102866_, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_)
	{
		super.setupAnim(p_102866_, p_102867_, p_102868_, p_102869_, p_102870_, p_102871_);
	}

	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition modelDefinition = new MeshDefinition();
		PartDefinition def = modelDefinition.getRoot();
		def.addOrReplaceChild("whisker1", CubeListBuilder.create().texOffs(0,0).addBox(-12.0F, -5.0F, -4.0F, 8, 5, 0), PartPose.offset(0, 0,0));
		def.addOrReplaceChild("whisker2", CubeListBuilder.create().texOffs(0,5).mirror().addBox(4.0F, -5.0F, -4.0F, 8, 5, 0), PartPose.offset(0, 0,0));

		def.addOrReplaceChild("fin1", CubeListBuilder.create().texOffs(16,0).addBox(0.0F, -3.0F, 0.0F, 6, 6, 0), PartPose.offsetAndRotation(4.0F, -4.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
		def.addOrReplaceChild("fin2", CubeListBuilder.create().texOffs(16,6).addBox(-6.0F, -3.0F, 0.0F, 6, 6, 0), PartPose.offsetAndRotation(-4.0F, -4.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(modelDefinition, 64, 32);
	}
}