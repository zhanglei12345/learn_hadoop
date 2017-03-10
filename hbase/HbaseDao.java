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
		//����Hbase�����
		Connection conn = ConnectionFactory.createConnection(conf);
		//��Connection�л��Admin����
		Admin admin = conn.getAdmin();
			
		//������Ҫ�����ı���
		TableName tablename = TableName.valueOf("table_test");		
		if(admin.tableExists(tablename)){
			System.out.println("���Ѵ��ڣ������½�");
		}
		else{
			System.out.println("����ʼ");
			HTableDescriptor desc = new HTableDescriptor(tablename);
			HColumnDescriptor base_info = new HColumnDescriptor("base_info");
			HColumnDescriptor extra_info = new HColumnDescriptor("extra_info");
			
			//����base_info�����汾��Ϊ5
			base_info.setMaxVersions(5);
			//��������base_info
			desc.addFamily(base_info);
			//��������extra_info
			desc.addFamily(extra_info);
			//����
			admin.createTable(desc);	
			System.out.println("�������");
		}
		
		Table table_test = conn.getTable(tablename);
		
		System.out.println("�����޸ı����ݿ�ʼ(put)");		
		//Hbase�д洢���������;�Ϊ�ֽ�����byte[]
		Put name = new Put(Bytes.toBytes("id0003"));
		name.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("name"), Bytes.toBytes("yuan"));
		Put age = new Put(Bytes.toBytes("id0003"));
		age.addColumn(Bytes.toBytes("base_info"), Bytes.toBytes("age"), Bytes.toBytes("23"));

		ArrayList<Put> puts =new ArrayList<>();
		puts.add(name);
		puts.add(age);
		//����
		table_test.put(puts);
		System.out.println("�����޸ı��������(put)");
		
		System.out.println("��ѯĳ�����ݿ�ʼ(get)");
		Get get = new Get(Bytes.toBytes("id0001"));
		get.setMaxVersions(5);
		Result result = table_test.get(get);
		//������ѯ���
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
		System.out.println("��ѯĳ���������(get)");
		
		System.out.println("ɨ�����ݿ�ʼ");
		Scan scan = new Scan(Bytes.toBytes("id0001"),Bytes.toBytes("id0003"));
		ResultScanner scanner = table_test.getScanner(scan);
		//�������
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
			//��ȡ�ض�qualifier��ֵ
			byte[] scanname = res.getValue(Bytes.toBytes("base_info"), Bytes.toBytes("name"));
			System.out.println("===>scanname: " + new String(scanname));
		}
		scanner.close();
		System.out.println("ɨ���������");
		
		System.out.println("ɾ��ĳ�����ݿ�ʼ");
		Delete del = new Delete(Bytes.toBytes("id0001"));
		del.addColumn(Bytes.toBytes("extra_info"), Bytes.toBytes("work"));
		table_test.delete(del);
		System.out.println("ɾ��ĳ���������");
		
		table_test.close();
		admin.close();
		conn.close();
	}
}
