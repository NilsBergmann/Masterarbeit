package bergmann.masterarbeit.generationtarget.utils;

import java.time.Duration;
import java.time.Instant;

public class RelativeTimeInterval {
    public Duration start, end;
    public boolean includeLeft, includeRight;

    public RelativeTimeInterval(Duration start, Duration end, boolean includeLeft, boolean includeRight) {
        this.start = start;
        this.end = end;
        this.includeLeft = includeLeft;
        this.includeRight = includeRight;
        if (start.toMillis() > end.toMillis())
            this.switchSides();
    }

    public AbsoluteTimeInterval addInstant(Instant instant) {
        return new AbsoluteTimeInterval(instant.plus(start), instant.plus(start), this.includeLeft, this.includeRight);
    }

    public void makeNegative() {
        this.start = this.start.negated();
        this.end = this.start.negated();
    }

    public void switchSides() {
        boolean tmp = this.includeLeft;
        Duration tmpD = this.start;

        this.includeLeft = this.includeRight;
        this.start = this.end;

        this.includeRight = tmp;
        this.end = tmpD;
    }

    public boolean contains(Duration dur) {
        // Check start
        int s = this.start.compareTo(dur);
        boolean startOK = s < 0;
        if (this.includeLeft)
            startOK = s <= 0;
        // Check end
        int e = this.end.compareTo(dur);
        boolean endOK = e > 0;
        if (this.includeLeft)
            endOK = s >= 0;
        return startOK && endOK;
    }
}