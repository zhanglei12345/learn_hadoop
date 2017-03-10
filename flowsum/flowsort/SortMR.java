package flowsort;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.StringUtils;

import flowsort.FlowBean;


public class SortMR {

	public static class SortMapper extends Mapper<LongWritable, Text, FlowBean, NullWritable>{

		//拿到一行数据，切分出各字段，封装为一个flowbean，作key输出
		//map输出数据先进行排序再传递给reduce
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {

			//拿一行数据
			String line = value.toString();
			//切分成各个字段
			String[] fields = StringUtils.split(line,'\t');
			
			String phoneNB = fields[0];
			//将字符串转换为十进制的long
			long up_flow = Long.parseLong(fields[1]);
			long dw_flow = Long.parseLong(fields[2]);
			
			//不能使用new NullWritable()来定义,只能NullWritable.get()来获取实例
			context.write(new FlowBean(phoneNB, up_flow, dw_flow), NullWritable.get());
	
		}
	}
	
	public static class SortReducer extends Reducer<FlowBean, NullWritable, Text, FlowBean>{

		@Override
		protected void reduce(FlowBean key, Iterable<NullWritable> values,Context context)
				throws IOException, InterruptedException {
			
			String phoneNB = key.getPhoneNB();
			context.write(new Text(phoneNB),key);

		}
		
	}
	
	public static void main(String[] args) throws Exception{
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(SortMR.class);
		job.setMapperClass(SortMapper.class);
		job.setReducerClass(SortReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		job.setMapOutputKeyClass(FlowBean.class);
		job.setMapOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
