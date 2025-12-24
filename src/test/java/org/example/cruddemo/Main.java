package org.example.cruddemo;

import java.util.*;

import static java.lang.Math.min;

public class Main {


    static class Pair{
        Integer a;
        Character b;
        public Pair(Integer a, Character b){
            this.a = a;
            this.b = b;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    static class Pair2{
        Integer a;
        Integer b;
        public Pair2(Integer a, Integer b){
            this.a = a;
            this.b = b;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Map<Pair, Integer> map = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            Integer num =  sc.nextInt();
            Character ch =  sc.next().charAt(0);
            map.putIfAbsent(new Pair(num, ch), i);
        }
        List<Pair2> pairs = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            if (map.get(new Pair(i, 'A')) != null &&  map.get(new Pair(i, 'B')) != null) {
                int pos1 = map.get(new Pair(i, 'A'));
                int pos2 = map.get(new Pair(i, 'B'));
                map.remove(new Pair(i, 'A'));
                map.remove(new Pair(i, 'B'));
                System.out.println(pos1 + " " + pos2);
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'A')) != null &&  map.get(new Pair(i, 'C')) != null) {
                int pos1 = map.get(new Pair(i, 'A'));
                int pos2 = map.get(new Pair(i, 'C'));
                map.remove(new Pair(i, 'A'));
                map.remove(new Pair(i, 'C'));
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'A')) != null &&  map.get(new Pair(i, 'D')) != null) {
                int pos1 = map.get(new Pair(i, 'A'));
                int pos2 = map.get(new Pair(i, 'D'));
                map.remove(new Pair(i, 'A'));
                map.remove(new Pair(i, 'D'));
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'A')) != null &&  map.get(new Pair(i, 'B')) != null) {
                int pos1 = map.get(new Pair(i, 'A'));
                int pos2 = map.get(new Pair(i, 'B'));
                map.remove(new Pair(i, 'A'));
                map.remove(new Pair(i, 'B'));
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'B')) != null &&  map.get(new Pair(i, 'C')) != null) {
                int pos1 = map.get(new Pair(i, 'B'));
                int pos2 = map.get(new Pair(i, 'C'));
                map.remove(new Pair(i, 'B'));
                map.remove(new Pair(i, 'C'));
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'B')) != null &&  map.get(new Pair(i, 'D')) != null) {
                int pos1 = map.get(new Pair(i, 'B'));
                int pos2 = map.get(new Pair(i, 'D'));
                map.remove(new Pair(i, 'B'));
                map.remove(new Pair(i, 'D'));
                pairs.add(new Pair2(pos1, pos2));
            }
            if (map.get(new Pair(i, 'C')) != null &&  map.get(new Pair(i, 'D')) != null) {
                int pos1 = map.get(new Pair(i, 'C'));
                int pos2 = map.get(new Pair(i, 'D'));
                map.remove(new Pair(i, 'C'));
                map.remove(new Pair(i, 'D'));
                pairs.add(new Pair2(pos1, pos2));
            }
        }
        System.out.println(pairs.size() * 2);
        for (int i = 0; i < pairs.size(); i++) {
            Pair2 p = pairs.get(i);
            System.out.println(p.a + " " + p.b);
        }
    }
}
