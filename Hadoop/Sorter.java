

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Sorter extends Configured implements Tool{
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Sorter(), args);
	    System.exit(res);
	}
	public int run(String[] args) throws Exception {
	    Path inputPath = new Path(args[0]);
	    Path outputPath = new Path(args[1]);

	    Configuration conf = getConf();
	    @SuppressWarnings("deprecation")
		Job job = new Job(conf, this.getClass().toString());

	    FileInputFormat.setInputPaths(job, inputPath);
	    FileOutputFormat.setOutputPath(job, outputPath);

	    job.setJobName("Sorter");
	    job.setJarByClass(Sorter.class);
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);

	    job.setMapperClass(SortMapper.class);
	    job.setCombinerClass(SorterReducer.class);
	    job.setReducerClass(SorterReducer.class);
	    job.setNumReduceTasks(2);

	    return job.waitForCompletion(true) ? 0 : 1;
	  }
}
