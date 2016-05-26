import java.io.IOException;
import java.lang.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FOF2 {

  public static class FOF2Mapper
       extends Mapper<Object, Text, Text, Text>{

    private final static Text one = new Text();
    private Text node = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {


      StringTokenizer itr = new StringTokenizer(value.toString());
    
      node.set(itr.nextToken());
      one.set(itr.nextToken());
      context.write(node, one);
      context.write(one, new Text("-"+node.toString()));
      
    }
  }

  public static class FOF2Combiner
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      String sum = new String(key.toString()+",");

      for (Text val : values) {
        String node = new String(val.toString());
        sum += node+"," ;

      }

      sum = sum.substring(0, sum.length()-1);

      result.set(sum);

      context.write(key, result);
      
    }
  }

  public static class FOF2Spliter
       extends Mapper<Object, Text, Text, Text>{

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {


      StringTokenizer itr = new StringTokenizer(value.toString());
      itr.nextToken();
    
      String v = itr.nextToken();
      String[] nodes = v.toString().split(",");

      String sum = new String();

      for(String node:nodes)
      {

        if(!node.startsWith("-"))
        {
          sum+=node+",";
        }
        //context.write(new Text(node), new Text(v));
      }

      sum = sum.substring(0, sum.length()-1);

      Text result = new Text(sum);

      for(String node:nodes)
      {

        if(node.startsWith("-"))
        {
          context.write(new Text(node.substring(1,node.length())), result);
        }
      }


      
    }
  }

  public static class FOF2Reducer
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      
      String key_str = new String(key.toString());
      String sum = new String();
      Set<String> uniq = new HashSet<String>();
      for (Text val : values) {

        for(String node : val.toString().split(","))
        {
          if(!node.equals(key_str))
          {
            uniq.add(node);
            //sum+=node+",";
          }
        }
        //sum+="|";
      }

      

      for(String node : uniq)
      {
        sum+=node+",";
      }

      sum = sum.substring(0, sum.length()-1);

      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {

    String OUTPUT_PATH = "temp_output";

    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "FOF21");
    System.out.println("<============ Job 1 ==============>");


    //job.setNumReduceTasks(2);
    job.setJarByClass(FOF2.class);
    job.setMapperClass(FOF2Mapper.class);
    job.setReducerClass(FOF2Combiner.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);



    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

    job.waitForCompletion(true);


    Configuration conf2 = new Configuration();
    Job job2 = Job.getInstance(conf2, "FOF22");
    System.out.println("<============ Job 2 ==============>");


    job2.setJarByClass(FOF2.class);
    job2.setMapperClass(FOF2Spliter.class);
    job2.setReducerClass(FOF2Reducer.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(Text.class);



    FileInputFormat.addInputPath(job2, new Path(OUTPUT_PATH));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));

    job2.waitForCompletion(true);

  }
}

