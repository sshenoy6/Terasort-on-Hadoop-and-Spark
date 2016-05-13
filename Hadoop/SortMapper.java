

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SortMapper extends Mapper<LongWritable,Text,Text,Text>{
		
	public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
		  //String []inputTokens = value.toString().split("\\s{2,}");
		//public void map(Text key,Text value,Context context) throws IOException,InterruptedException{
			Text key1 = new Text();
			key1.set(value.toString().substring(0, 10));
			Text word = new Text();
			word.set(value.toString().substring(10,value.toString().length()));
			context.write(key1, word);
		}
		

	}
