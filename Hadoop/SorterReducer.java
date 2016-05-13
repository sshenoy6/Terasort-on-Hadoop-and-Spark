

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class SorterReducer extends Reducer<Text,Text,Text,Text> {

	//private Text result = new Text();
	 
	    @Override
	    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	        for(Text val:values){
	        	context.write(key, val);
	        }
	    }


}
