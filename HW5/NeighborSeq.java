import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// import Set;

public class Seven {

    public static Map<Integer, Set<Integer>> g = new HashMap<Integer, Set<Integer>>() {
        @Override
        public Set<Integer> get(Object key) {
            Set<Integer> set = super.get(key); 
            if (set == null) {
                set = new HashSet<Integer>();
                put((int)key, set);
            }
            return set;
        }
    };


    public static Map<Integer, Set<Integer>> ans = new HashMap<Integer, Set<Integer>>() {
        @Override
        public Set<Integer> get(Object key) {
            Set<Integer> set = super.get(key); 
            if (set == null) {
                set = new HashSet<Integer>();
                put((int)key, set);
            }
            return set;
        }
    };

    public static void main(String[] args) {
        readFile();
  
        twoHops();
   
    }

    private static void twoHops() {
        for(Map.Entry<Integer, Set<Integer>> entry : g.entrySet()) {
            Integer key = (Integer)entry.getKey();
            System.out.print(key+": ");
            for (Integer value : entry.getValue()) {
                if (!g.get(value).isEmpty()) {
                    for (Integer a : g.get(value)) {
                        System.out.print(a+", ");
                    }
                }
            }
            System.out.println();
        }
    }

    private static void showGraph(Map<Integer, Set<Integer>> map) {
        for(Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
            Integer key = (Integer)entry.getKey();
            System.out.print(key+": ");
            for (Integer value : entry.getValue()) {
                System.out.print(value+", ");
            }
            System.out.println();
        }
    }

    private static void readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("web-Google.txt")))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split("[ \t]+");
                int a = Integer.parseInt(tokens[0]);
                int b = Integer.parseInt(tokens[1]);
                g.get(a).add(b);
                g.get(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
