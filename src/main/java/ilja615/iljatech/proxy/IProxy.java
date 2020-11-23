package ilja615.iljatech.proxy;

import net.minecraft.world.World;

public interface IProxy
{
    World getClientWorld();

    public void init();
}
