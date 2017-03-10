package hbase;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;



public class HbaseDao {
	
	public static void main(String[] args) throws Exception{
		
		Configuration conf = HBaseConfiguration.create();
		//操作Hbase的入口
		Connection conn = ConnectionFactory.createConnection(conf);
		//从Connection中获得Admin对象
		Admin admin = conn.getAdmin();
			
		//本例将要操作的表名
		TableName tablename = TableName.valueOf("table_test");		
		if(admin.tableExists(tablename)){
			System.out.println("表已存在，不需新建");
		}
		else{
			System.out.println("建表开始");
			HTableDescriptor desc = new HTableDescriptor(tablename);
			HColumnDescriptor base_info = new HColumnDescriptor("base_info");
			HColumnDescriptor extra_info = new HColumnDescriptor("extra_info");
			
			//设置base_info的最大版本号为5
			base_info.setMaxVersions(5);
			//创建列族base_info
			desc.addFamily(base_info);
			//创建列族extra_info
			desc.addFamily(extra_info);
			//建表
			admin.createTable(desc);	
			System.out.println("建表完成");
		}
		
		Table table_test = conn.getTable(tablename);
		
		System.out.println("新增修改表数据开始(put)");		
		//Hbase中存储的数据类型均为字节数组byte[]
		Put name = new Put(Bytes.toBytes("id0003"));
		name.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("name"), Bytes.toBytes("yuan"));
		Put age = new Put(Bytes.toBytes("id0003"));
		age.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("age"), Bytes.toBytes("23"));

		ArrayList<Put> puts =new ArrayList<>();
		puts.add(name);
		puts.add(age);
		//新增
		table_test.put(puts);
		System.out.println("新增修改表数据完成(put)");
		
		System.out.println("查询某条数据开始(get)");
		Get get = new Get(Bytes.toBytes("id0001"));
		get.setMaxVersions(5);
		Result result = table_test.get(get);
		//遍历查询结果
		List<Cell> cells = result.listCells();
		for(Cell cell : cells){
			String rowkey = Bytes.toString(CellUtil.cloneRow(cell));
			long timestamp = cell.getTimestamp();
			String famliy = Bytes.toString(CellUtil.cloneFamily(cell));
			String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
			String value = Bytes.toString(CellUtil.cloneValue(cell));
			System.out.println("===>rowkey: " + rowkey + ",timestamp: " + timestamp +
						",famliy: " + famliy + ",qualifier: " + qualifier + ",value: " + value);
			}
		System.out.println("查询某条数据完成(get)");
		
		System.out.println("扫描数据开始");
		Scan scan = new Scan(Bytes.toBytes("id0001"),Bytes.toBytes("id0003"));
		ResultScanner scanner = table_test.getScanner(scan);
		//遍历结果
		for(Result res : scanner){
			for(Cell cell : res.listCells()){
					String rowkey = Bytes.toString(CellUtil.cloneRow(cell));
					long timestamp = cell.getTimestamp();
					String famliy = Bytes.toString(CellUtil.cloneFamily(cell));
					String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
					String value = Bytes.toString(CellUtil.cloneValue(cell));
					System.out.println("===>rowkey: " + rowkey + ",timestamp: " + timestamp +
								",famliy: " + famliy + ",qualifier: " + qualifier + ",value: " + value);
			}
			//获取特定qualifier的值
			byte[] scanname = res.getValue(Bytes.toBytes("base_info"), Bytes.toBytes("name"));
			System.out.println("===>scanname: " + new String(scanname));
		}
		scanner.close();
		System.out.println("扫描数据完成");
		
		System.out.println("删除某条数据开始");
		Delete del = new Delete(Bytes.toBytes("id0001"));
		del.addColumn(Bytes.toBytes("extra_info"), Bytes.toBytes("work"));
		table_test.delete(del);
		System.out.println("删除某条数据完成");
		
		table_test.close();
		admin.close();
		conn.close();
	}
}
