package pl.olafcio.playclient.util.message;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import pl.olafcio.playclient.util.message.token.*;

import java.util.ArrayList;
import java.util.function.BiConsumer;

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
                reset();
            else if (token instanceof ColorToken(var name))
                reset("color", name, value::putString);
            else if (token instanceof HexToken(var hex))
                reset("color", "#" + hex, value::putString);
            else if (token instanceof TextToken(var text))
                value.putString("text", text);
            else if (token instanceof FormatToken(var type))
                reset(type, true, value::putBoolean);
        }

        reset();
        return this.output;
    }

    protected void reset() {
        output.add(value);
        value = new NbtCompound();
    }

    protected <T> void reset(String name, T value, BiConsumer<String, T> function) {
        reset();
        function.accept(name, value);
    }

    protected final MessageToken consume() {
        return input.get(index++);
    }

    protected final boolean reachedEOF() {
        return index == input.size();
    }
}
