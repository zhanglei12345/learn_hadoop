package wordcount;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//4�������У�ǰ������ָ��mapper�������ݵ����ͣ�keyin�������key�����ͣ�valuein�������value������
//map��reduce�������������������key-value�Ե���ʽ��װ��
//Ĭ������£���ܴ��ݸ����ǵ�mapper�����������У�key��Ҫ������ı���һ�е���ʼƫ��������һ�е�������Ϊvalue
//�����������ݽ��д��䣬Ҫ���л�
public class WCMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
	
	//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
		//����ҵ���߼���д������������У���������ҵ��Ҫ����������Ѿ�����ܴ��ݽ������ڷ����Ĳ�����key-value
		
		//����һ�е�����ת����string
		String line = value.toString();
		//����һ�е��ı����ض��ָ����з�
		String[] words = StringUtils.split(line, " ");
		//������������������Ϊkv��ʽ	k������	v��1
		for(String word : words){
			context.write(new Text(word), new LongWritable(1));
		}
	}
}
