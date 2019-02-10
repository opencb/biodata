package org.opencb.biodata.tools.variant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.Variant;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created on 22/02/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantDeduplicationTaskTest {

    @Test
    public void testMultiChromosome() throws Exception {
        VariantDeduplicationTask t = new VariantDeduplicationTask();

        List<Variant> l = t.apply(Arrays.asList(
//                new Variant("1:100:A:C"),
                new Variant("2:100:A:T"),
                new Variant("3:100:A:T"),
                new Variant("10:101:A:C"),
                new Variant("11:100:A:C"),
                new Variant("12:105:A:C")));
        l.addAll(t.drain());
        System.out.println(l);
        assertEquals(5, l.size());

    }

    @Test
    public void testSimpleDuplication() throws Exception {
        VariantDeduplicationTask t = new VariantDeduplicationTask(list -> {
            assertEquals(2, list.size());
            assertEquals("1:100:A:C", list.get(0).toString());
            assertEquals("1:100:A:C", list.get(1).toString());
            return Collections.emptyList();
        });

        List<Variant> l = t.apply(Arrays.asList(
                new Variant("1:100:A:C"),
                new Variant("1:100:A:T"),
                new Variant("1:101:A:C"),
                new Variant("1:100:A:C"),
                new Variant("1:105:A:C")));
        l.addAll(t.drain());
        System.out.println(l);
        assertEquals(3, l.size());
        assertEquals(2, t.getDiscardedVariants());


    }

    @Test
    public void testCircularQueue() {
        VariantDeduplicationTask.CircularSortedArrayQueue<Integer> l = new VariantDeduplicationTask.CircularSortedArrayQueue<>(4, Integer::compare);

        List<Integer> v = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 4, 5, 6, 8, 7, 8, 9));

        for (int i = 0; i < 20; i++) {
            v.add(i + 10);
        }

        int limit = 0;
        Iterator<Integer> iterator = v.iterator();
        while ((iterator.hasNext() || l.size() > 0) && limit++ < v.size() * 2) {
            if (l.hasNext()) {
                if (l.isDuplicated()) {
                    List<Integer> list = l.nextDuplicated();
                    assertNotNull(list);
                    System.out.println(list);
                } else {
                    Integer next = l.next();
                    assertNotNull(next);
                    System.out.println(next);
                }
            }
            if (iterator.hasNext()) {
                Integer i = iterator.next();
                l.add(i);
            } else {
                l.setFinished(true);
            }
        }

    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCircularQueueFull() {
        VariantDeduplicationTask.CircularSortedArrayQueue<Integer> l =
                new VariantDeduplicationTask.CircularSortedArrayQueue<>(4, Integer::compare);

        l.add(1);
        l.add(2);
        l.add(3);
        l.add(4);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Queue full");
        l.add(5);

    }

    @Test
    public void testCircularAllDuplicated() {
        VariantDeduplicationTask.CircularSortedArrayQueue<Integer> l =
                new VariantDeduplicationTask.CircularSortedArrayQueue<>(4, Integer::compare);

        l.add(2);
        l.add(2);
        l.add(2);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("New element '1' below than any other");
        l.add(1);

    }
}