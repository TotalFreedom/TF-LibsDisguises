package me.libraryaddict.disguise.disguisetypes.watchers;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MetaIndex;

public class SnowmanWatcher extends InsentientWatcher {
    public SnowmanWatcher(Disguise disguise) {
        super(disguise);
    }

    public void setDerp(boolean derp) {
        setData(MetaIndex.SNOWMAN_DERP, (byte) (derp ? 0 : 16));
        sendData(MetaIndex.SNOWMAN_DERP);
    }

    public boolean isDerp() {
        return getData(MetaIndex.SNOWMAN_DERP) == 0;
    }
}
