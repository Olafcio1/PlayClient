package pl.olafcio.playclient.util.message;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import pl.olafcio.playclient.util.message.token.*;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageUnparser {
    private final ArrayList<MessageToken> input;
    protected int index;
    protected final NbtList output;

    public MessageUnparser(ArrayList<MessageToken> msg) {
        this.input = msg;
        this.index = 0;
        this.output = new NbtList();
    }

    protected NbtCompound value;
    public NbtList run() {
        value = new NbtCompound();

        while (!reachedEOF()) {
            var token = consume();
            if (token instanceof ResetToken)
                reset(true);
            else if (token instanceof ColorToken(var name))
                reset("color", name, () -> value::putString);
            else if (token instanceof HexToken(var hex))
                reset("color", "#" + hex, () -> value::putString);
            else if (token instanceof TextToken(var text))
                value.putString("text", text);
            else if (token instanceof FormatToken(var type))
                sub(type, true, () -> value::putBoolean);
        }

        reset(false);
        return this.output;
    }

    protected void reset(boolean unset) {
        if (value.contains("text"))
            output.add(value);
        else if (!unset)
            return;

        var oldValue = value;
        value = new NbtCompound();

        if (unset) {
            if (!oldValue.getString("color", "white").equals("white"))
                value.putString("color", "white");

            for (var k : formatting_keys)
                if (oldValue.getBoolean(k, false))
                    value.putBoolean(k, false);
        }
    }

    protected <T> void reset(String name, T value, Supplier<BiConsumer<String, T>> function) {
        reset(true);
        function.get().accept(name, value);
    }

    protected <T> void sub(String name, T value, Supplier<BiConsumer<String, T>> function) {
        reset(false);
        function.get().accept(name, value);
    }

    protected static final String[] formatting_keys = new String[]{
            "bold",
            "italic",
            "underlined",
            "strikethrough"
    };

    protected final MessageToken consume() {
        return input.get(index++);
    }

    protected final boolean reachedEOF() {
        return index == input.size();
    }
}
