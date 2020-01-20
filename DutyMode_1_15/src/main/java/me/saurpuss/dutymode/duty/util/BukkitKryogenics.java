package me.saurpuss.dutymode.duty.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static java.util.Arrays.stream;
import static me.saurpuss.dutymode.duty.util.BukkitSerializers.*;
import static org.bukkit.entity.EntityType.values;

/**
 * https://gist.github.com/Exerosis/7a7872dd8699448d8f5e973195ff442f
 * @author Exerosis
 */
public class BukkitKryogenics {
    private static final String ERR_SAVE_ENTITY = "Failed to serialize NBT for an Entity.";
    private static final String ERR_SAVE_ITEM = "Failed to serialize NBT for an ItemStack.";
    private static final String ERR_LOAD_ITEM = "Failed to deserialize NBT for an ItemStack.";
    private static final String ERR_LOAD_ENTITY = "Failed to deserialize NBT for an Entity.";

    public static void registerSerializers(Kryo kryo) {
        stream(values()).map(EntityType::getEntityClass).forEach(type ->
                kryo.register(type, new Serializer<Entity>() {
                    @Override
                    public void write(Kryo kryo, Output out, Entity entity) {
                        try {
                            kryo.writeObject(out, entity.getWorld());
                            saveEntity(entity, out);
                        } catch (Exception reason) {
                            throw new IllegalStateException(ERR_SAVE_ENTITY, reason);
                        }
                    }

                    @Override
                    public Entity read(Kryo kryo, Input in, Class<Entity> type) {
                        try {
                            return loadEntity(kryo.readObject(in, World.class), in);
                        } catch (Exception e) {
                            throw new IllegalStateException(ERR_LOAD_ENTITY, e);
                        }
                    }
                })
        );

        new Serializer<ItemStack>() {
            {
                kryo.register(ItemStack.class, this);
                kryo.register(CLASS_CRAFT_ITEM, this);
            }

            @Override
            public void write(Kryo kryo, Output out, ItemStack item) {
                try {
                    saveItem(item, out);
                } catch (Exception e) {
                    throw new IllegalStateException(ERR_SAVE_ITEM, e);
                }
            }

            @Override
            public ItemStack read(Kryo kryo, Input in, Class<ItemStack> type) {
                try {
                    return BukkitSerializers.loadItem(in);
                } catch (Exception e) {
                    throw new IllegalStateException(ERR_LOAD_ITEM, e);
                }
            }
        };

        new Serializer<World>(){
            {
                kryo.register(World.class, this);
                kryo.register(CLASS_CRAFT_WORLD, this);
            }

            @Override
            public void write(Kryo kryo, Output out, World world) {
                kryo.writeObject(out, WorldCreator.name(world.getName()).copy(world));
            }
            @Override
            public World read(Kryo kryo, Input in, Class<World> type) {
                return kryo.readObject(in, WorldCreator.class).createWorld();
            }
        };

        kryo.register(UUID.class, new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output out, UUID uuid) {
                out.writeLong(uuid.getMostSignificantBits());
                out.writeLong(uuid.getLeastSignificantBits());
            }

            @Override
            public UUID read(Kryo kryo, Input in, Class<UUID> type) {
                return new UUID(in.readLong(), in.readLong());
            }
        });
    }
}