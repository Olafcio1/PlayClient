package pl.olafcio.playclient.features.modules.grief;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.Text;
import pl.olafcio.playclient.PlayAddon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OperatorSelect extends Module {
    SettingGroup sgAction = settings.createGroup("Action");
    BoolSetting addSelf = sgAction.add(new BoolSetting.Builder()
            .name("add-self")
            .description("Adds self to the operator list.")
    .build());
    StringListSetting opList = sgAction.add(new StringListSetting.Builder()
            .name("op-list")
            .description("The list of players to /op.")
    .build());

    SettingGroup sgExecution = settings.createGroup("Execution");
    IntSetting deopDelay = sgExecution.add(new IntSetting.Builder()
            .name("deop-delay")
            .description("The time delay between each /deop.")
            .min(0)
            .sliderRange(0, 1000)
            .defaultValue(300)
    .build());
    IntSetting opDelay = sgExecution.add(new IntSetting.Builder()
            .name("op-delay")
            .description("The time delay between each /op.")
            .min(0)
            .sliderRange(0, 1000)
            .defaultValue(300)
    .build());

    private ExecutorService executor;
    private Future<?> currentTask;

    private boolean processing;
    public OperatorSelect() {
        super(PlayAddon.GRIEF, "OperatorSelect", "Sets the specified players as the only operators.");
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onActivate() {
        mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(
                0,
                "deop "
        ));

        processing = false;
    }

    @Override
    public void onDeactivate() {
        if (currentTask != null)
            currentTask.cancel(true);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (
                !processing &&
                event.packet instanceof CommandSuggestionsS2CPacket packet &&
                packet.id() == 0
        ) {
            processing = true;

            event.cancel();
            execute(() -> {
                try {
                    for (var group : packet.suggestions()) {
                        var item = group.text();
                        if (!item.equals(mc.player.getStringifiedName()))
                            continue;

                        mc.player.networkHandler.sendChatCommand("deop " + item);
                        Thread.sleep(deopDelay.get());
                    }

                    if (addSelf.get()) {
                        mc.player.networkHandler.sendChatCommand("op " + mc.player.getStringifiedName());
                        Thread.sleep(opDelay.get());
                    }

                    for (var name : opList.get()) {
                        mc.player.networkHandler.sendChatCommand("op " + name);
                        Thread.sleep(opDelay.get());
                    }
                } catch (InterruptedException e) {
                    System.out.println("[OperatorSelect] Ended");
                } catch (Exception e) {
                    mc.player.sendMessage(Text.of("[OperatorSelect] Module in dev, client crash prevented"), false);
                }

                toggle();
            });
        }
    }

    protected synchronized final void execute(Runnable command) {
        if (currentTask != null && !currentTask.isDone())
            currentTask.cancel(true);

        currentTask = executor.submit(command);
    }
}
