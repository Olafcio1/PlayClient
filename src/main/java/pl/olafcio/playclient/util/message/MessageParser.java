package pl.olafcio.playclient.util.message;

import pl.olafcio.playclient.util.message.token.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageParser {
    private final char[] input;
    protected int index;
    protected final ArrayList<MessageToken> output;

    protected static final HashMap<Character, String> COLOR_CODES = new HashMap<>(){{
        // Digits
        this.put('1', "dark_blue");
        this.put('2', "dark_green");
        this.put('3', "dark_aqua");
        this.put('4', "dark_red");
        this.put('5', "dark_purple");
        this.put('6', "gold");
        this.put('7', "gray");
        this.put('8', "dark_gray");
        this.put('9', "dark_blue");
        this.put('0', "black");
        // Letters
        this.put('a', "green");
        this.put('b', "aqua");
        this.put('c', "red");
        this.put('d', "purple");
        this.put('e', "yellow");
        this.put('f', "white");
    }};

    protected static final HashMap<Character, String> FORMAT_CODES = new HashMap<>(){{
            this.put('o', "italic");
            this.put('l', "bold");
            this.put('k', "obfuscated");
            this.put('n', "underlined");
            this.put('m', "strikethrough");
    }};

    public MessageParser(String msg) {
        this.input = msg.toCharArray();
        this.index = 0;
        this.output = new ArrayList<>();
    }

    protected StringBuilder value;
    public ArrayList<MessageToken> run() {
        this.value = new StringBuilder();

        while (!reachedEOF()) {
            var ch = consume();
            if (ch == '&') {
                var next = consume();
                if (next == '#') {
                    var value = new StringBuilder();
                    for (int i = 0; i < 6; i++)
                        value.append(consume());

                    this.output.add(new HexToken(value.toString()));
                } else if (next == 'x') {
                    var value = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        if (consume() != '&')
                            break;

                        value.append(consume());
                    }

                    this.output.add(new HexToken(value.toString()));
                } else if (next == 'r') {
                    this.output.add(new ResetToken());
                } else if (FORMAT_CODES.containsKey(next)) {
                    this.output.add(new ColorToken(FORMAT_CODES.get(next)));
                } else if (COLOR_CODES.containsKey(next)) {
                    this.output.add(new ColorToken(COLOR_CODES.get(next)));
                } else if (next == '&') {
                    this.value.append(ch);
                } else {
                    this.index -= 1;
                    this.value.append(ch);
                }
            } else {
                this.value.append(ch);
                continue;
            }

            reset(1);
        }

        reset(0);
        return this.output;
    }

    protected void reset(int before) {
        if (!value.isEmpty()) {
            output.add(output.size() - before, new TextToken(value.toString()));
            value.setLength(0);
        }
    }

    protected final char consume() {
        return input[index++];
    }

    protected final boolean reachedEOF() {
        return index == input.length;
    }
}
