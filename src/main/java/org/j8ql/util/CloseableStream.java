/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

/**
 * <p></p>
 */
public  class CloseableStream<T> implements Stream<T> {

	private final Stream<T> stream;
	private final AutoCloseable additionalClose;

	public CloseableStream(Stream<T> stream, AutoCloseable additionalClose) {
		this.stream = stream;
		this.additionalClose = additionalClose;
	}

	static private volatile int c = 0;
	@Override
	public final void close() {
		// close the owner stream first.
		stream.close();

		// then, do the additionalClose
		if (additionalClose != null){
			try {
				additionalClose.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	// --------- Stream Delegates --------- //
	public final Stream<T> filter(Predicate<? super T> predicate) {
		return stream.filter(predicate);
	}

	public final <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return stream.map(mapper);
	}

	public final IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return stream.mapToInt(mapper);
	}

	public final LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return stream.mapToLong(mapper);
	}

	public final DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return stream.mapToDouble(mapper);
	}

	public final <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	public final IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return stream.flatMapToInt(mapper);
	}

	public final LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return stream.flatMapToLong(mapper);
	}

	public final DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return stream.flatMapToDouble(mapper);
	}

	@Override
	public final Stream<T> distinct() {
		return stream.distinct();
	}

	@Override
	public final Stream<T> sorted() {
		return stream.sorted();
	}

	public final Stream<T> sorted(Comparator<? super T> comparator) {
		return stream.sorted(comparator);
	}

	public final Stream<T> peek(Consumer<? super T> action) {
		return stream.peek(action);
	}

	@Override
	public final Stream<T> limit(long maxSize) {
		return stream.limit(maxSize);
	}

	@Override
	public final Stream<T> skip(long n) {
		return stream.skip(n);
	}

	public final void forEach(Consumer<? super T> action) {
		stream.forEach(action);
	}

	public final void forEachOrdered(Consumer<? super T> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public final Object[] toArray() {
		return stream.toArray();
	}

	@Override
	public final <A> A[] toArray(IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	public final T reduce(T identity, BinaryOperator<T> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	public final Optional<T> reduce(BinaryOperator<T> accumulator) {
		return stream.reduce(accumulator);
	}

	public final <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	public final <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	public final <R, A> R collect(Collector<? super T, A, R> collector) {
		return stream.collect(collector);
	}

	public final Optional<T> min(Comparator<? super T> comparator) {
		return stream.min(comparator);
	}

	public final Optional<T> max(Comparator<? super T> comparator) {
		return stream.max(comparator);
	}

	@Override
	public final long count() {
		return stream.count();
	}

	public final boolean anyMatch(Predicate<? super T> predicate) {
		return stream.anyMatch(predicate);
	}

	public final boolean allMatch(Predicate<? super T> predicate) {
		return stream.allMatch(predicate);
	}

	public final boolean noneMatch(Predicate<? super T> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public final Optional<T> findFirst() {
		return stream.findFirst();
	}

	@Override
	public final Optional<T> findAny() {
		return stream.findAny();
	}

	public final static <T1> Builder<T1> builder() {
		return Stream.builder();
	}

	public final static <T1> Stream<T1> empty() {
		return Stream.empty();
	}

	public final static <T1> Stream<T1> of(T1 t1) {
		return Stream.of(t1);
	}

	@SafeVarargs
	public final static <T1> Stream<T1> of(T1... values) {
		return Stream.of(values);
	}

	public final static <T1> Stream<T1> iterate(T1 seed, UnaryOperator<T1> f) {
		return Stream.iterate(seed, f);
	}

	public final static <T1> Stream<T1> generate(Supplier<T1> s) {
		return Stream.generate(s);
	}

	public final static <T1> Stream<T1> concat(Stream<? extends T1> a, Stream<? extends T1> b) {
		return Stream.concat(a, b);
	}

	@Override
	public final Iterator<T> iterator() {
		return stream.iterator();
	}

	@Override
	public final Spliterator<T> spliterator() {
		return stream.spliterator();
	}

	@Override
	public final boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public final Stream<T> sequential() {
		return stream.sequential();
	}

	@Override
	public final Stream<T> parallel() {
		return stream.parallel();
	}

	@Override
	public final Stream<T> unordered() {
		return stream.unordered();
	}

	@Override
	public final Stream<T> onClose(Runnable closeHandler) {
		return stream.onClose(closeHandler);
	}

	// --------- /Stream Delegates --------- //

}
