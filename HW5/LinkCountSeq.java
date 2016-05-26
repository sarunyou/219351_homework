import java.io.File;
import java.util.Scanner;

public class Degree{
	public static Scanner scanner;
	public static File file;
	public static int length = 0;
    public static int[] degree = new int[1000000];

    public static void read_input(){
        file = new File("web-Google.txt");
        try{
            scanner = new Scanner(file);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void split_node(){
        while(scanner.hasNextLine()){
            String edge = scanner.nextLine();
                String[] nodes = new String [2];
                nodes = edge.split("\t");

                int a,b;
                a = Integer.parseInt(nodes[0]);
                b = Integer.parseInt(nodes[1]);
                count_edge(a,b);

                if(a>length){
                    length = a;
                }
                if(b>length){
                    length = b;
                }
        }
    }

    public static void count_edge(int a, int b){
        degree[a]++;
        degree[b]++;
    }

    public static void print(){
        for(int i = 0; i<length+1; i++){
            String formattedString = String.format("Node %s: %s", i, degree[i]);
            System.out.println(formattedString);
        }
    }

    public static void main(String[] args){
        read_input();
        split_node();
        print();
        scanner.close();
    }
}
