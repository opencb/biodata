package org.opencb.biodata.tools.variant;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.run.Task;

import java.util.*;

/**
 * Created on 22/02/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantDeduplicationTask implements Task<Variant, Variant> {

    private final DuplicatedVariantsResolver resolver;
    private final CircularSortedArrayQueue<Variant> queue;

    private static final Comparator<Variant> VARIANT_COMPARATOR = Comparator
            .comparing(Variant::getChromosome, (chr1, chr2) -> chr1.equals(chr2) ? 0 : -1)
            .thenComparing(Variant::getStart)
            .thenComparing(Variant::getEnd)
            .thenComparing(Variant::getReference)
            .thenComparing(Variant::getAlternate)
            .thenComparing(Variant::getSv, (sv1, sv2) ->
                    sv1 == sv2 ? 0
                            : sv1 == null ? -1
                            : sv2 == null ? 1
                            : sv1.compareTo(sv2));
    private int discardedVariants = 0;

    public VariantDeduplicationTask() {
        this((list) -> {
            if (list.size() == 1) {
                throw new IllegalStateException("Unexpected list of 1 duplicated variants : " + list);
            } else {
                return Collections.emptyList();
            }
        });
    }

    public VariantDeduplicationTask(DuplicatedVariantsResolver duplicatedVariantsResolver) {
        resolver = duplicatedVariantsResolver;
        queue = new CircularSortedArrayQueue<>(100, VARIANT_COMPARATOR);
    }

    @FunctionalInterface
    public interface DuplicatedVariantsResolver {
        List<Variant> resolveDuplicatedVariants(List<Variant> variants);

    }

    @Override
    public List<Variant> apply(List<Variant> list) throws Exception {
        List<Variant> filteredVariants = new ArrayList<>(list.size());
        for (Variant variant : list) {
            if (queue.hasNext()) {
                if (queue.isDuplicated()) {
                    List<Variant> dupVariants = queue.nextDuplicated();
                    List<Variant> resolved = resolver.resolveDuplicatedVariants(dupVariants);
                    discardedVariants += (dupVariants.size() - resolved.size());
                    filteredVariants.addAll(resolved);
                } else {
                    filteredVariants.add(queue.next());
                }
            }
            queue.add(variant);
        }
        return filteredVariants;
    }

    @Override
    public List<Variant> drain() throws Exception {
        queue.setFinished(true);
        List<Variant> filteredVariants = new ArrayList<>(queue.size);
        while (!queue.isEmpty()) {
            if (queue.isDuplicated()) {
                List<Variant> dupVariants = queue.nextDuplicated();
                List<Variant> resolved = resolver.resolveDuplicatedVariants(dupVariants);
                discardedVariants += (dupVariants.size() - resolved.size());
                filteredVariants.addAll(resolved);
            } else {
                filteredVariants.add(queue.next());
            }
        }
        return filteredVariants;
    }

    public int getDiscardedVariants() {
        return discardedVariants;
    }

    static class CircularSortedArrayQueue<T> {

        private final T[] array;
        private final boolean[] duplicated;
        private boolean finished = false;
        private final Comparator<T> comparator;
        private final int capacity;
        private int head = 0;
        private int tail = 0;
        private int size = 0;

        public CircularSortedArrayQueue(int capacity, Comparator<T> comparator) {
            this.array = (T[]) new Object[capacity];
            this.duplicated = new boolean[capacity];
            this.capacity = array.length;
            this.comparator = comparator;
        }

        public CircularSortedArrayQueue setFinished(boolean finished) {
            this.finished = finished;
            return this;
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isDuplicated() {
            return duplicated[tail];
        }

        public T get() {
            return array[tail];
        }

        public boolean hasNext() {
            return finished || size >= (capacity - 1);
        }

        public T next() {
            if (hasNext()) {
                return getNext();
            } else {
                return null;
            }
        }

        private T getNext() {
            T value = array[tail];
            array[tail] = null;
            duplicated[tail] = false;
            if (value != null) {
                size--;
            }
            incrementTail();
            return value;
        }

        public List<T> nextDuplicated() {
            ArrayList<T> list = new ArrayList<>(3);
            T first = next();
            if (first == null) {
                return Collections.emptyList();
            }
            list.add(first);
            while (duplicated[tail]) {
                T next = array[tail];
                if (next != null && comparator.compare(first, next) == 0) {
                    list.add(getNext());
                } else {
                    break;
                }
            }
            return list;
        }

        public void add(T elem) {
            if (elem == null) {
                finished = true;
                return;
            }
            size++;
            T t = array[head];
            if (t != null) {
                throw new IllegalStateException("Queue full");
            }
            array[head] = elem;

            int position = this.head;
            boolean swap = checkSorted(elem, position);
            while (swap) {
                position = prevPosition(position);
                swap = checkSorted(elem, position);
                if (position == this.tail) {
                    // Something really exceptional happened
                    throw new IllegalStateException("New element below than any other");
                }
            }
            incrementHead();
        }

        private boolean checkSorted(T elem, int position) {
            int prevPosition = prevPosition(position);
            boolean swap = false;
            T prev = array[prevPosition];
            if (prev != null) {
                int c = comparator.compare(prev, elem);
                if (c < 0) {
                    // prev < elem
                    return swap;
                } else if (c == 0) {
                    // Duplicated
                    duplicated[position] = true;
                    duplicated[prevPosition] = true;
                } else if (c > 0) {
                    // prev > elem
                    // Swap
                    array[position] = prev;
                    array[prevPosition] = elem;
                    boolean b = duplicated[position];
                    duplicated[position] = duplicated[prevPosition];
                    duplicated[prevPosition] = b;
                    swap = true;
                }
            }
            return swap;
        }

        private void incrementTail() {
            tail = nextPosition(tail);
        }

        private void incrementHead() {
            head = nextPosition(head);
        }

        private int nextPosition(int position) {
            int next = position + 1;
            if (next == capacity) {
                return 0;
            } else {
                return next;
            }
        }

        private int prevPosition(int position) {
            int prev = position - 1;
            if (prev == -1) {
                return capacity - 1;
            } else {
                return prev;
            }
        }
    }

}
