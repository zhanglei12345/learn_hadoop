package inverseindex;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.StringUtils;


/**
 * 
 * ������������һjob
 * @author zhanglei
 *
 */

public class InverseIndexOne {

	
	public static class StepOneMapper extends Mapper<LongWritable,Text,Text,LongWritable>{

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			//�õ�һ������
			String line = value.toString();
			//�зֳ���������
			String[] fields = StringUtils.split(line, ' ');
			//��ȡ��һ���������ڵ��ļ���Ƭ
			FileSplit inputsplit = (FileSplit)context.getInputSplit();
			//���ļ���Ƭ�л�ȡ�ļ���
			String filename = inputsplit.getPath().getName();
			
			for(String field:fields){
				//��װkv�����K:	hello-->a.txt	v:	1
				context.write(new Text(field + "-->" + filename), new LongWritable(1));
			}

		}
		
	}
	
	public static class StepOneReducer extends Reducer<Text,LongWritable,Text,LongWritable>{

		//<hello-->a.txt,{1,1,1,1...}>
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values,Context context) 
				throws IOException, InterruptedException {
			
			long counter = 0;
			for(LongWritable value:values){
				counter += value.get();
			}
			
			context.write(key, new LongWritable(counter));		
		}
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(InverseIndexOne.class);
		
		job.setMapperClass(StepOneMapper.class);
		job.setReducerClass(StepOneReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);	
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//���һ�²�����ָ�������·���Ƿ���ڣ�����Ѵ��ڣ���ɾ��
		Path output = new Path(args[1]);
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(output)){
			//�ݹ�ɾ��
			fs.delete(output, true);
		}
		FileOutputFormat.setOutputPath(job, output);
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
