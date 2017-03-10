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
		
		//k:	����ʼƫ���� 	v:	hello-->a.txt	3
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			//�õ�һ������
			String line = value.toString();
			//�зֳ���������
			String[] fields = StringUtils.split(line, "\t");
			String[] wordandfilename = StringUtils.split(fields[0], "-->");
			String word = wordandfilename[0];
			String filename = wordandfilename[1];
			long count = Long.parseLong(fields[1]);
			
			context.write(new Text(word), new Text(filename + "-->" + count));
			//map����Ľ���������ʽ��<hello,a.txt-->3>
		}
	}
	
	public static class StepTwoReducer extends Reducer<Text,Text,Text,Text>{

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			//�õ�������<hello,{a.txt-->3��b.txt-->2, c.txt-->1}>
			
			String result = "";
			for(Text value:values){
				result += value.toString() + " ";
			}
			
			context.write(key, new Text(result));
			//����Ľ������k��hello 	v��a.txt-->3 b.txt-->2 c.txt-->1
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
