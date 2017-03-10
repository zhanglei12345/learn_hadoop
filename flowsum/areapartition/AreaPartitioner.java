package areapartition;

import java.util.HashMap;

import org.apache.hadoop.mapreduce.Partitioner;

public class AreaPartitioner<KEY, VALUE> extends Partitioner<KEY, VALUE>{

	private static HashMap<String,Integer> areaMap = new HashMap<String,Integer>();
	
	static{
		areaMap.put("139", 0);
		areaMap.put("157", 1);
		areaMap.put("133", 2);
		areaMap.put("167", 3);
		areaMap.put("150", 3);
	}
	@Override
	public int getPartition(KEY key, VALUE value, int numPartitions) {
		//��key���õ��ֻ��ţ���ѯ�ֻ��������ֵ䣬��ͬ��ʡ�ݷ��ز�ͬ�����
		
		int areaCode = areaMap.get(key.toString().substring(0,3))==null?5:areaMap.get(key.toString().substring(0,3));
		
		return areaCode;
	}



}
