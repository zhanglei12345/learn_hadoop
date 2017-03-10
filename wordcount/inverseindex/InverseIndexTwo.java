package inverseindex;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class InverseIndexTwo {
	
	public static class StepTwoMapper extends Mapper<LongWritable,Text,Text,Text>{
		
		//k:	行起始偏移量 	v:	hello-->a.txt	3
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			//拿到一行数据
			String line = value.toString();
			//切分出各个单词
			String[] fields = StringUtils.split(line, "\t");
			String[] wordandfilename = StringUtils.split(fields[0], "-->");
			String word = wordandfilename[0];
			String filename = wordandfilename[1];
			long count = Long.parseLong(fields[1]);
			
			context.write(new Text(word), new Text(filename + "-->" + count));
			//map输出的结果是这个形式：<hello,a.txt-->3>
		}
	}
	
	public static class StepTwoReducer extends Reducer<Text,Text,Text,Text>{

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			//拿到的数据<hello,{a.txt-->3，b.txt-->2, c.txt-->1}>
			
			String result = "";
			for(Text value:values){
				result += value.toString() + " ";
			}
			
			context.write(key, new Text(result));
			//输出的结果就是k：hello 	v：a.txt-->3 b.txt-->2 c.txt-->1
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(InverseIndexTwo.class);
		
		job.setMapperClass(StepTwoMapper.class);
		job.setReducerClass(StepTwoReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);	
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//检查一下参数所指定的输出路径是否存在，如果已存在，先删除
		Path output = new Path(args[1]);
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(output)){
			//递归删除
			fs.delete(output, true);
		}
		FileOutputFormat.setOutputPath(job, output);
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
}
