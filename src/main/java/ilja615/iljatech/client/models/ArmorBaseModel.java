package ilja615.iljatech.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public abstract class ArmorBaseModel extends HumanoidModel<LivingEntity>
{
    protected final ModelPart armorHead;
    protected final ModelPart armorBody;
    protected final ModelPart armorRightArm;
    protected final ModelPart armorLeftArm;
    protected final ModelPart armorRightLeg;
    protected final ModelPart armorLeftLeg;
    protected final ModelPart armorRightBoot;
    protected final ModelPart armorLeftBoot;

    private String texture;

    public ArmorBaseModel(ModelPart part, ResourceLocation textureIn)
    {
        super(part);
        this.armorHead = part.getChild("armor_head");
        this.armorBody = part.getChild("armor_body");
        this.armorRightArm = part.getChild("armor_right_arm");
        this.armorLeftArm = part.getChild("armor_left_arm");
        this.armorRightLeg = part.getChild("armor_right_leg");
        this.armorLeftLeg = part.getChild("armor_left_leg");
        this.armorRightBoot = part.getChild("armor_right_boot");
        this.armorLeftBoot = part.getChild("armor_left_boot");

        this.texture = textureIn.toString();

        //setupArmorParts();
    }

    public final String getTexture(){
        return this.texture;
    }

    /**
     * Feel free to override this method as needed.
     * It's just required to hide armor parts depending on the equipment slot
     */
    public HumanoidModel<LivingEntity> applySlot(EquipmentSlot slot){
        armorHead.visible = false;
        armorBody.visible = false;
        armorRightArm.visible = false;
        armorLeftArm.visible = false;
        armorRightLeg.visible = false;
        armorLeftLeg.visible = false;
        armorRightBoot.visible = false;
        armorLeftBoot.visible = false;

        switch(slot){
            case HEAD:
                armorHead.visible = true;
                break;
            case CHEST:
                armorBody.visible = true;
                armorRightArm.visible = true;
                armorLeftArm.visible = true;
                break;
            case LEGS:
                armorRightLeg.visible = true;
                armorLeftLeg.visible = true;
                break;
            case FEET:
                armorRightBoot.visible = true;
                armorLeftBoot.visible = true;
                break;
            default:
                break;
        }

        return this;
    }

    public final ArmorBaseModel applyEntityStats(HumanoidModel defaultArmor){
        this.young = defaultArmor.young;
        this.crouching = defaultArmor.crouching;
        this.riding = defaultArmor.riding;
        this.rightArmPose = defaultArmor.rightArmPose;
        this.leftArmPose = defaultArmor.leftArmPose;

        return this;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        copyModelAngles(this.head, this.armorHead);
        copyModelAngles(this.body, this.armorBody);
        copyModelAngles(this.rightArm, this.armorRightArm);
        copyModelAngles(this.leftArm, this.armorLeftArm);
        copyModelAngles(this.rightLeg, this.armorRightLeg);
        copyModelAngles(this.leftLeg, this.armorLeftLeg);
        copyModelAngles(this.rightLeg, this.armorRightBoot);
        copyModelAngles(this.leftLeg, this.armorLeftBoot);

        poseStack.pushPose();
        if(this.crouching) poseStack.translate(0, 0.2, 0);

        renderChildrenOnly(this.head, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderChildrenOnly(this.body, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderChildrenOnly(this.rightArm, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderChildrenOnly(this.leftArm, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderChildrenOnly(this.rightLeg, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderChildrenOnly(this.leftLeg, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();
    }

    public final void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    private final void copyModelAngles(ModelPart in, ModelPart out){
        out.xRot = in.xRot;
        out.yRot = in.yRot;
        out.zRot = in.zRot;
    }
    
    private final void renderChildrenOnly(ModelPart bodyPart, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if(bodyPart.getAllParts().findAny().isPresent()){
            poseStack.pushPose();
            bodyPart.translateAndRotate(poseStack);
            bodyPart.getAllParts().forEach(child -> child.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
            poseStack.popPose();
        }
    }
}