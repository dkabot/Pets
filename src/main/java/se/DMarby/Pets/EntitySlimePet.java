package se.DMarby.Pets;

import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntitySlime;
import net.minecraft.server.v1_6_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftSlime;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

public class EntitySlimePet extends EntitySlime { // old AI
    private final Player owner;
    private int jumpDelay = jumpDelay();

    public EntitySlimePet(World world, Player owner) {
        super(world);
        this.owner = owner;
    }

    public EntitySlimePet(World world) {
        this(world, null);
    }

    private int distToOwner() {
        EntityHuman handle = ((CraftPlayer) owner).getHandle();
        return (int) (Math.pow(locX - handle.locX, 2) + Math.pow(locY - handle.locY, 2) + Math.pow(locZ
                - handle.locZ, 2));
    }

    int jumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    protected void bl() {
        if (owner == null) {
            super.bl();
            return;
        }
        EntityHuman entityhuman = ((CraftPlayer) owner).getHandle();
        double dist = distToOwner();
        if (entityhuman.world != this.world || dist > Util.MAX_DISTANCE)
            this.getBukkitEntity().teleport(owner);

        this.a(entityhuman, 10.0F, 20.0F);

        if (dist <= 16 && entityhuman.motX == 0 && entityhuman.motY == 0 && entityhuman.motZ == 0)
            return;
        if (this.onGround && this.jumpDelay-- <= 0) {
            this.jumpDelay = this.bL();
            // if (entityhuman != null)
            this.jumpDelay /= 3;

            this.bd = true;
            if (this.bS()) {
                this.makeSound(this.bP(), this.ba(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.be = 1.0F - this.random.nextFloat() * 2.0F;
            this.bf = (float) (1 * this.getSize());
        } else {
            this.bd = false;
            if (this.onGround) {
                this.be = this.bf = 0.0F;
            }
        }
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (owner != null && bukkitEntity == null)
            bukkitEntity = new BukkitSlimePet(this);
        return super.getBukkitEntity();
    }

    public static class BukkitSlimePet extends CraftSlime implements PetEntity {
        private final Player owner;

        public BukkitSlimePet(EntitySlimePet entity) {
            super((CraftServer) Bukkit.getServer(), entity);
            this.owner = entity.owner;
        }

        @Override
        public Slime getBukkitEntity() {
            return this;
        }

        @Override
        public Player getOwner() {
            return owner;
        }

        @Override
        public void upgrade() {
            int size = getSize() + 1;
            if (Util.MAX_LEVEL != -1)
                size = Math.min(Util.MAX_LEVEL, size);
            setSize(size);
            Location petLoc = getLocation();
            getWorld().playSound(petLoc, Sound.LEVEL_UP, size, 1);
            for (int i = 0; i < size; i++)
                getWorld().playEffect(petLoc, Effect.SMOKE, (size + i) / 8);
        }

        @Override
        public void setLevel(int level) {
            setSize(level);
        }
    }
}
