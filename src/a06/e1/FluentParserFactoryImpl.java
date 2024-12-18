package a06.e1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class FluentParserFactoryImpl implements FluentParserFactory {

    public abstract static class FluentParserImpl<T> implements FluentParser<T> {
        protected T nextValue;

        public FluentParserImpl(final T startingValue) {
            this.nextValue = startingValue;
        }

        @Override
        public FluentParser<T> accept(final T value) {
            if (!value.equals(this.nextValue)) {
                throw new IllegalStateException();
            }
            this.nextValue = getNextValue();
            return this;
        }

        protected abstract T getNextValue();
    }

    @Override
    public FluentParser<Integer> naturals() {
        return new FluentParserImpl<Integer>(0) {
            @Override
            protected Integer getNextValue() {
                return this.nextValue + 1;
            }
        };
    }

    @Override
    public FluentParser<List<Integer>> incrementalNaturalLists() {
        return new FluentParserImpl<List<Integer>>(List.of()) {
            @Override
            protected List<Integer> getNextValue() {
                if (this.nextValue.isEmpty()) {
                    return List.of(0);
                } else {
                    final var next = new ArrayList<>(this.nextValue);
                    next.add(this.nextValue.getLast() + 1);
                    return next;
                }
            }
        };
    }

    @Override
    public FluentParser<Integer> repetitiveIncrementalNaturals() {
        return new FluentParserImpl<Integer>(0) {
            private int maxValue = 0;

            @Override
            protected Integer getNextValue() {
                if (this.nextValue.intValue() >= this.maxValue) {
                    this.maxValue++;
                    return 0;
                }
                return this.nextValue + 1;
            }
        };
    }

    @Override
    public FluentParser<String> repetitiveIncrementalStrings(final String s) {
        return new FluentParserImpl<String>(s) {
            private int maxConcats = 1;

            @Override
            protected String getNextValue() {
                if (this.nextValue.length() >= this.maxConcats) {
                    this.maxConcats++;
                    return s;
                }
                return this.nextValue.concat(s);
            }
        };
    }

    @Override
    public FluentParser<Pair<Integer, List<String>>> incrementalPairs(final int i0,
            final UnaryOperator<Integer> op, final String s) {
        return new FluentParserImpl<Pair<Integer, List<String>>>(getNextPair(i0, s)) {
            private int concatCount = i0;

            @Override
            protected Pair<Integer, List<String>> getNextValue() {
                this.concatCount = op.apply(this.concatCount);
                return getNextPair(this.concatCount, s);
            }
        };
    }

    private Pair<Integer, List<String>> getNextPair(final int i0, final String s) {
        return new Pair<>(i0, Stream.generate(() -> s)
                .limit(i0)
                .toList());
    }
}
