package wordcount;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
	
	//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,values{}>,����һ��reduce����
	//<hello, {1,1,1,1...}>
	@Override
	protected void reduce(Text key,Iterable<LongWritable> values,Context context)
		throws IOException, InterruptedException{
		
		long count = 0;
		//����value��list�������ۼ����
		for(LongWritable value : values){
			count += value.get();
		}
		//�����һ�����ʵ�ͳ�ƽ��
		context.write(key, new LongWritable(count));
	}

}
