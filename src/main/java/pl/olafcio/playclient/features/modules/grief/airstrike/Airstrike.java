package pl.olafcio.playclient.features.modules.grief.airstrike;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import pl.olafcio.playclient.PlayAddon;

import java.util.ArrayList;
import java.util.function.Function;

public class Airstrike extends Module {
    protected ArrayList<AirstrikeRecord> records;
    public Airstrike() {
        super(PlayAddon.GRIEF, "Airstrike", "Spawns entities as configured. Requires GMC.");
        records = new ArrayList<>();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        for (var record : records) {
            mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(
                    0,
                    Registries.ITEM.get(Identifier.of(record.entityType + "_spawn_egg"))
                                   .getDefaultStack()
            ));

            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        super.fromTag(tag);

        tag.getList("settings-records")
           .ifPresent(nbtElements -> {
               records.clear();
               nbtElements.forEach(el -> {
                   if (!(el instanceof NbtCompound))
                       throw new AssertionError("How the fuck does this contain non-NBT compounds! Did you tamper with data?");

                   records.add(AirstrikeRecord.fromNBT((NbtCompound) el));
               });
           });

        return this;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        var list = theme.verticalList();
        fillList(theme, list);
        return list;
    }

    protected void fillList(GuiTheme theme, WVerticalList list) {
        var bottom = list.add(theme.horizontalList()).expandX().widget();
        var entityTypes = Registries.ENTITY_TYPE.streamEntries();

        var etRest = entityTypes.map(x ->
                                    EntityType.getId(x.value())
                                              .getPath()
                                ).toArray(String[]::new);
        var etFirst = etRest[0];
        ArrayUtils.shift(etRest, 0);

        var newEntity = bottom.add(theme.dropdown(etRest, etFirst)).expandX().widget();
        var add = bottom.add(theme.plus()).widget();

        add.action = () -> {
            var entityType = newEntity.get();
            var record = new AirstrikeRecord(
                    entityType,
                    null,
                    false,
                    false
            );

            records.add(record);
            appendRecord(theme, list, record);

            ((WContainer) list.cells.getLast().widget()).cells.removeLast();

            if (list.cells.size() > 1) {
                var lastRecord = (WContainer) list.cells.get(list.cells.size() - 2).widget();
                lastRecord.add(theme.horizontalSeparator()).expandX();
            }
        };

        if (!records.isEmpty()) {
            for (var record : records)
                appendRecord(theme, list, record);

            var lastRecord = (WContainer) list.cells.getLast().widget();
            lastRecord.cells.removeLast();
        }
    }

    protected void appendRecord(GuiTheme theme, WVerticalList list, AirstrikeRecord record) {
        var widget = theme.verticalList();
        var table = theme.table();

        setting(table, "Entity Type", theme.textBox(
                record.entityType
        ), x -> {
            x.action = () -> {
                record.entityType = x.get();
            };
            return x;
        }, theme);

        setting(table, "Custom Name", theme.textBox(
                record.customName
        ), x -> {
            x.action = () -> {
                record.customName = x.get();
            };
            return x;
        }, theme);

        setting(table, "No Gravity", theme.checkbox(
                record.noGravity
        ), x -> {
            x.action = () -> {
                record.noGravity = x.checked;
            };
            return x;
        }, theme);

        setting(table, "No AI", theme.checkbox(
                record.noAI
        ), x -> {
            x.action = () -> {
                record.noAI = x.checked;
            };
            return x;
        }, theme);

        var removeBtn = table.add(theme.minus()).right().widget();

        var c1 = widget.add(table).expandX();
        var c2 = widget.add(theme.horizontalSeparator()).expandX();

        removeBtn.action = () -> {
            widget.remove(c1);
            widget.remove(c2);

            records.remove(record);
        };

        list.add(widget).expandX();
    }

    protected <W extends WWidget> void setting(
            WTable table,
            String name,
            W value,
            Function<W, W> init,
            GuiTheme theme
    ) {
        table.add(theme.label(name));

        var initedValue = init.apply(value);
        var valueCell = table.add(initedValue);

        if (!(initedValue instanceof WCheckbox))
            valueCell.expandX();

        table.row();
    }

    @Override
    public NbtCompound toTag() {
        var tag = super.toTag();
        var output = new NbtList();

        var stream = records.stream().map(AirstrikeRecord::toNBT);
        stream.forEach(output::add);

        tag.put("settings-records", output);
        return tag;
    }
}
