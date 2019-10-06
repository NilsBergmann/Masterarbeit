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

    public boolean contains(Instant i) {
        // Check start
        int s = this.start.compareTo(i);
        boolean startOK = s < 0;
        if (this.includeLeft)
            startOK = s <= 0;
        // Check end
        int e = this.end.compareTo(i);
        boolean endOK = e > 0;
        if (this.includeLeft)
            endOK = s >= 0;
        return startOK && endOK;
    }

}