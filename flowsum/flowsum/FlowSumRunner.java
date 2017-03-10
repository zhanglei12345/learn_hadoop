package flowsum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//这是job描述和提交类的规范写法
public class FlowSumRunner extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		////创建Job对象
		Job job = Job.getInstance(conf);
		//设置整个job的所用的那些类在哪个jar包
		job.setJarByClass(FlowSumRunner.class);
		//本job使用的mapper和reducer的类
		job.setMapperClass(FlowSumMapper.class);
		job.setReducerClass(FlowSumReducer.class);
		
		//job.setMapOutputKeyClass(Text.class);
		//job.setMapOutputValueClass(FlowBean.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		//指定要处理的输入数据存放在哪里
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//将job提交给集群运行,true：打印运行日志
		return job.waitForCompletion(true)?0:1;
	}
	
	public static void main(String[] args) throws Exception{
		
		int res = ToolRunner.run(new Configuration(), new FlowSumRunner(), args);
		System.exit(res);
	}

}
