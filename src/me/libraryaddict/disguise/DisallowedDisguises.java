package me.libraryaddict.disguise;

import java.util.Arrays;
import java.util.List;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import static me.libraryaddict.disguise.disguisetypes.DisguiseType.*;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class DisallowedDisguises {

    public static final List<DisguiseType> forbiddenDisguises = Arrays.asList(FISHING_HOOK, ITEM_FRAME, ENDER_DRAGON, PLAYER, GIANT, GHAST, MAGMA_CUBE, SLIME, DROPPED_ITEM, ENDER_CRYSTAL, AREA_EFFECT_CLOUD, WITHER);
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
