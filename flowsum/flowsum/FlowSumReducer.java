package flowsum;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FlowSumReducer extends Reducer<Text, FlowBean, Text, FlowBean>{
	
	//���ÿ����һ������<12345678,{FlowBean,FlowBean,FlowBean...}>,����һ�����ǵ�reduce����
	//reduce�е�ҵ���߼����Ǳ���values��Ȼ������ۼ���������
	@Override
	protected void reduce(Text key,Iterable<FlowBean> values, Context context)
			throws IOException, InterruptedException{
		
		long up_flow_counter = 0;
		long dw_flow_counter = 0;
		for(FlowBean bean : values){
			up_flow_counter += bean.getUp_flow();
			dw_flow_counter += bean.getDw_flow();
		}
		
		context.write(key, new FlowBean(key.toString(),up_flow_counter,dw_flow_counter));
	}

}
