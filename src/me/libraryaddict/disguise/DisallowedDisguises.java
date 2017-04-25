package me.libraryaddict.disguise;

import java.util.Arrays;
import java.util.List;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class DisallowedDisguises {

    private static final List<DisguiseType> forbiddenDisguises = Arrays.asList(DisguiseType.ITEM_FRAME, DisguiseType.ENDER_DRAGON, DisguiseType.PLAYER, DisguiseType.GIANT, DisguiseType.GHAST, DisguiseType.MAGMA_CUBE, DisguiseType.SLIME, DisguiseType.DROPPED_ITEM, DisguiseType.ENDER_CRYSTAL, DisguiseType.AREA_EFFECT_CLOUD, DisguiseType.WITHER);
    public static boolean disabled = false;

    public static boolean isAllowed(Disguise disguise) {
        return isAllowed(disguise.getType());

    }

    public static boolean isAllowed(DisguiseType type) {
        if (forbiddenDisguises.contains(type)) {
            return false;
        }

        return true;
    }
}
