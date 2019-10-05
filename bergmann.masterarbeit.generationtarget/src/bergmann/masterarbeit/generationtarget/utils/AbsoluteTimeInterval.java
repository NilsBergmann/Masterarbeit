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
        if (start.isAfter(end))
            this.switchSides();
    }

    public void switchSides() {
        boolean tmp = this.includeLeft;
        Instant tmpD = this.start;

        this.includeLeft = this.includeRight;
        this.start = this.end;

        this.includeRight = tmp;
        this.end = tmpD;
    }

}