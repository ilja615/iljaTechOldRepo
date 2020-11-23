package ilja615.iljatech.proxy;

import net.minecraft.world.World;

public class ServerProxy implements IProxy
{
    @Override
    public World getClientWorld()
    {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public void init()
    {

    }
}
