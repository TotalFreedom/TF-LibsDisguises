package me.libraryaddict.disguise.DisguiseTypes.Watchers;

import me.libraryaddict.disguise.DisguiseTypes.Disguise;

public class PigWatcher extends AgeableWatcher {

    public PigWatcher(Disguise disguise) {
        super(disguise);
    }

    public boolean isSaddled() {
        return (Byte) getValue(16, (byte) 0) == 1;
    }

    public void setSaddled(boolean isSaddled) {
        if (isSaddled() != isSaddled) {
            setValue(16, (byte) (isSaddled ? 1 : 0));
            sendData(16);
        }
    }
}
