package org.lanternpowered.server.network.vanilla.message.codec.play;

import java.util.Collection;
import java.util.List;
import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.LanternGameProfile.Property;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;
import org.spongepowered.api.text.Text;

public final class CodecPlayOutTabListEntries implements Codec<MessagePlayOutTabListEntries> {

    private static final int ADD = 0;
    private static final int UPDATE_GAME_MODE = 1;
    private static final int UPDATE_LATENCY = 2;
    private static final int UPDATE_DISPLAY_NAME = 3;
    private static final int REMOVE = 4;

    private final TObjectIntMap<Class<?>> typeLookup;

    {
        TObjectIntMap<Class<?>> typeLookup = new TObjectIntHashMap<Class<?>>();
        typeLookup.put(Entry.Add.class, ADD);
        typeLookup.put(Entry.UpdateGameMode.class, UPDATE_GAME_MODE);
        typeLookup.put(Entry.UpdateLatency.class, UPDATE_LATENCY);
        typeLookup.put(Entry.UpdateDisplayName.class, UPDATE_DISPLAY_NAME);
        typeLookup.put(Entry.Remove.class, REMOVE);
        this.typeLookup = TCollections.unmodifiableMap(typeLookup);
    }

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTabListEntries message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        List<Entry> entries = message.getEntries();
        int type = this.typeLookup.get(entries.get(0).getClass());
        context.writeVarInt(buf, type);
        context.writeVarInt(buf, entries.size());
        for (Entry entry : entries) {
            switch (type) {
                case ADD:
                    context.write(buf, String.class, entry.getName());
                    Collection<Property> properties = entry.getProperties();
                    context.writeVarInt(buf, properties.size());
                    for (Property property : properties) {
                        context.write(buf, String.class, property.getName());
                        context.write(buf, String.class, property.getValue());
                        String signature = property.getSignature();
                        boolean flag = signature != null;
                        buf.writeBoolean(flag);
                        if (flag) {
                            context.write(buf, String.class, signature);
                        }
                    }
                    context.writeVarInt(buf, ((LanternGameMode) entry.getGameMode()).getInternalId());
                    context.writeVarInt(buf, entry.getPing());
                    Text displayName = entry.getDisplayName();
                    boolean flag = displayName != null;
                    buf.writeBoolean(flag);
                    if (flag) {
                        context.write(buf, Text.class, displayName);
                    }
                    break;
                case UPDATE_GAME_MODE:
                    context.writeVarInt(buf, ((LanternGameMode) entry.getGameMode()).getInternalId());
                    break;
                case UPDATE_LATENCY:
                    context.writeVarInt(buf, entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    Text displayName0 = entry.getDisplayName();
                    boolean flag0 = displayName0 != null;
                    buf.writeBoolean(flag0);
                    if (flag0) {
                        context.write(buf, Text.class, displayName0);
                    }
                    break;
                case REMOVE:
                    break;
            }
        }
        return buf;
    }

    @Override
    public MessagePlayOutTabListEntries decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}