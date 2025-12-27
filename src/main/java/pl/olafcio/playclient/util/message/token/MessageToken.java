package pl.olafcio.playclient.util.message.token;

public sealed interface MessageToken permits ColorToken, FormatToken, HexToken, ResetToken, TextToken {}
