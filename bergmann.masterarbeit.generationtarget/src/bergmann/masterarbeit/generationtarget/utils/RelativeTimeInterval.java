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
    }

    public AbsoluteTimeInterval addInstant(Instant instant) {
        return new AbsoluteTimeInterval(instant.plus(start), instant.plus(start), this.includeLeft, this.includeRight);
    }

    public void makeNegative() {
        this.start = this.start.negated();
        this.end = this.start.negated();
    }
}