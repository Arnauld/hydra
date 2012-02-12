package org.technbolts.hydra;

import org.technbolts.hydra.exception.HydraException;

public class Threads {
;
    public static void sleep(long amount) {
        try {
            Thread.sleep(amount);
        } catch (InterruptedException e) {
            throw new HydraException(e);
        }
    }
}
