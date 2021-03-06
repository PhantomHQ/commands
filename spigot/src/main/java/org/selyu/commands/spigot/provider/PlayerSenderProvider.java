package org.selyu.commands.spigot.provider;

import org.bukkit.entity.Player;
import org.selyu.commands.api.argument.CommandArg;
import java.lang.IllegalArgumentException;
import org.selyu.commands.api.parametric.ICommandProvider;
import org.selyu.commands.spigot.SpigotCommandService;
import org.selyu.commands.spigot.lang.SpigotLang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class PlayerSenderProvider implements ICommandProvider<Player> {
    private final SpigotCommandService service;

    public PlayerSenderProvider(@Nonnull SpigotCommandService service) {
        this.service = service;
    }

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @Nullable
    public Player provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (arg.getSender().getInstance() instanceof Player) {
            return (Player) arg.getSender().getInstance();
        }
        throw new IllegalArgumentException(service.getLang().get(SpigotLang.Type.PLAYER_ONLY_COMMAND));
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "player sender";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}