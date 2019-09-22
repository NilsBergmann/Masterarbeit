package bergmann.masterarbeit.generationtarget.utils;

import java.time.Instant;

public class AbsoluteTimeInterval {
    public Instant start, end;
    public boolean includeLeft, includeRight;

    public AbsoluteTimeInterval(Instant start, Instant end, boolean includeLeft, boolean includeRight) {
        this.start = start;
        this.end = end;
        this.includeLeft = includeLeft;
        this.includeRight = includeRight;
    }

}