package se.DMarby.Pets;

import net.minecraft.server.v1_6_R3.EntityBat;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.GenericAttributes;
import net.minecraft.server.v1_6_R3.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftBat;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;

public class EntityBatPet extends EntityBat { // old AI
    private final Player owner;

    public EntityBatPet(World world, Player owner) {
        super(world);
        this.owner = owner;
        if (owner != null){
            this.f(false);
            this.a(false);
        }
    }

    public EntityBatPet(World world) {
        this(world, null);
    }

    private int distToOwner() {
        EntityHuman handle = ((CraftPlayer) owner).getHandle();
        return (int) (Math.pow(locX - handle.locX, 2) + Math.pow(locY - handle.locY, 2) + Math.pow(locZ
                - handle.locZ, 2));
    }

    @Override
    protected void bi() {
        if (owner == null) {
            super.bi();
            return;
        }
        this.getNavigation().a(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(), 0.55F);
        this.getNavigation().a(false);
        getEntitySenses().a();
        getNavigation().f();
        getControllerMove().c(); // old API
        getControllerLook().a(); // old API
        getControllerJump().b(); // etc

        if (distToOwner() > Util.MAX_DISTANCE)
            this.getBukkitEntity().teleport(owner);
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (owner != null && bukkitEntity == null)
            bukkitEntity = new BukkitBatPet(this);
        return super.getBukkitEntity();
    }

    public static class BukkitBatPet extends CraftBat implements PetEntity {
        private final Player owner;

        public BukkitBatPet(EntityBatPet entity) {
            super((CraftServer) Bukkit.getServer(), entity);
            this.owner = entity.owner;
        }

        @Override
        public Bat getBukkitEntity() {
            return this;
        }

        @Override
        public Player getOwner() {
            return owner;
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void setLevel(int level) {
            // setSize(level);
        }
    }
}
