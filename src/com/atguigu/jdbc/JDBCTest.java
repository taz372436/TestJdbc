package com.atguigu.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.horizon.db.Access;

public class JDBCTest {

	public <T> List<T> query(Class<T> clazz, String sql){
		return null;
	}
	
	/**
	 * ResultSet: �����. ��װ��ʹ�� JDBC ���в�ѯ�Ľ��. 
	 * 1. ���� Statement ����� executeQuery(sql) ���Եõ������.
	 * 2. ResultSet ���ص�ʵ���Ͼ���һ�����ݱ�. ��һ��ָ��ָ�����ݱ�ĵ�һ����ǰ��.
	 * ���Ե��� next() ���������һ���Ƿ���Ч. ����Ч�÷������� true, ��ָ������. �൱��
	 * Iterator ����� hasNext() �� next() �����Ľ����
	 * 3. ��ָ���λ��һ��ʱ, ����ͨ������ getXxx(index) �� getXxx(columnName)
	 * ��ȡÿһ�е�ֵ. ����: getInt(1), getString("name")
	 * 4. ResultSet ��ȻҲ��Ҫ���йر�. 
	 */
	@Test
	public void testResultSet(){
		//��ȡ id=4 �� customers ���ݱ�ļ�¼, ����ӡ
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			//1. ��ȡ Connection
			conn = JDBCTools.getConnection();
			System.out.println(conn);
			
			//2. ��ȡ Statement
			statement = conn.createStatement();
			System.out.println(statement);
			
			//3. ׼�� SQL
			String sql = "SELECT id, name, email, birth " +
					"FROM customers";
			
			//4. ִ�в�ѯ, �õ� ResultSet
			rs = statement.executeQuery(sql);
			System.out.println(rs);
			
			//5. ���� ResultSet
			while(rs.next()){
				int id = rs.getInt(1);
				String name = rs.getString("name");
				String email = rs.getString(3);
				Date birth = rs.getDate(4);
				
				System.out.println(id);
				System.out.println(name);
				System.out.println(email);
				System.out.println(birth);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//6. �ر����ݿ���Դ. 
			JDBCTools.release(rs, statement, conn);
		}
		
	}
	
	/**
	 * ͨ�õĸ��µķ���: ���� INSERT��UPDATE��DELETE
	 * �汾 1.
	 */
	public void update(String sql){
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = JDBCTools.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCTools.release(statement, conn);
		}
	}
	
	/**
	 * ͨ�� JDBC ��ָ�������ݱ��в���һ����¼. 
	 * 
	 * 1. Statement: ����ִ�� SQL ���Ķ���
	 * 1). ͨ�� Connection �� createStatement() ��������ȡ
	 * 2). ͨ�� executeUpdate(sql) ����ִ�� SQL ���.
	 * 3). ����� SQL ������ INSRET, UPDATE �� DELETE. �������� SELECT
	 * 
	 * 2. Connection��Statement ����Ӧ�ó�������ݿ��������������Դ. ʹ�ú�һ��Ҫ�ر�.
	 * ��Ҫ�� finally �йر� Connection �� Statement ����. 
	 * 
	 * 3. �رյ�˳����: �ȹرպ��ȡ��. ���ȹر� Statement ��ر� Connection
	 */
	@Test
	public void testStatement() throws Exception{
		//1. ��ȡ���ݿ�����
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = getConnection2();
			
			//3. ׼������� SQL ���
			String sql = null;
			
//			sql = "INSERT INTO customers (NAME, EMAIL, BIRTH) " +
//					"VALUES('XYZ', 'xyz@atguigu.com', '1990-12-12')";
//			sql = "DELETE FROM customers WHERE id = 1";
			sql = "UPDATE customers SET name = 'TOM' " +
					"WHERE id = 4";
			System.out.println(sql);
			
			//4. ִ�в���. 
			//1). ��ȡ���� SQL ���� Statement ����: 
			//���� Connection �� createStatement() ��������ȡ
			statement = conn.createStatement();
			
			//2). ���� Statement ����� executeUpdate(sql) ִ�� SQL �����в���
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				//5. �ر� Statement ����.
				if(statement != null)
					statement.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				//2. �ر�����
				if(conn != null)
					conn.close();							
			}
		}
		
	}
	
	@Test
	public void testGetConnection2() throws Exception{
		System.out.println(getConnection2()); 
	}
	
	public Connection getConnection2() throws Exception{
		//1. ׼���������ݿ�� 4 ���ַ���. 
		//1). ���� Properties ����
		Properties properties = new Properties();
		
		//2). ��ȡ jdbc.properties ��Ӧ��������
		InputStream in = 
				this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		
		//3). ���� 2�� ��Ӧ��������
		properties.load(in);
		
		//4). ������� user, password ��4 ���ַ���. 
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		String jdbcUrl = properties.getProperty("jdbcUrl");
		String driver = properties.getProperty("driver");
		
		//2. �������ݿ���������(��Ӧ�� Driver ʵ��������ע�������ľ�̬�����.)
		Class.forName(driver);
		
		//3. ͨ�� DriverManager �� getConnection() ������ȡ���ݿ�����. 
		return DriverManager.getConnection(jdbcUrl, user, password);
	}
	
	/**
	 * DriverManager �������Ĺ�����. 
	 * 1). ����ͨ�����ص� getConnection() ������ȡ���ݿ�����. ��Ϊ����
	 * 2). ����ͬʱ��������������: ��ע���˶�����ݿ�����, ����� getConnection()
	 * ����ʱ����Ĳ�����ͬ, �����ز�ͬ�����ݿ����ӡ� 
	 * @throws Exception 
	 */
	@Test
	public void testDriverManager() throws Exception{
		//1. ׼���������ݿ�� 4 ���ַ���. 
		//������ȫ����.
		String driverClass = "com.mysql.jdbc.Driver";
		//JDBC URL
		String jdbcUrl = "jdbc:mysql:///test";
		//user
		String user = "root";
		//password
		String password = "1230";
		
		//2. �������ݿ���������(��Ӧ�� Driver ʵ��������ע�������ľ�̬�����.)
		Class.forName(driverClass);
		
		//3. ͨ�� DriverManager �� getConnection() ������ȡ���ݿ�����. 
		Connection connection = 
				DriverManager.getConnection(jdbcUrl, user, password);
		System.out.println(connection); 
		
	}
	
	/**
	 * Driver ��һ���ӿ�: ���ݿ⳧�̱����ṩʵ�ֵĽӿ�. �ܴ����л�ȡ���ݿ�����.
	 * ����ͨ�� Driver ��ʵ��������ȡ���ݿ�����.
	 * 
	 * 1. ���� mysql ����
	 * 1). ��ѹ mysql-connector-java-5.1.7.zip
	 * 2). �ڵ�ǰ��Ŀ���½� lib Ŀ¼
	 * 3). �� mysql-connector-java-5.1.7-bin.jar ���Ƶ� lib Ŀ¼��
	 * 4). �Ҽ� build-path , add to buildpath ���뵽��·����.s
	 * @throws SQLException 
	 */
	@Test
	public void testDriver() throws SQLException {
		//1. ����һ�� Driver ʵ����Ķ���
		Driver driver = new com.mysql.jdbc.Driver();
	
		//2. ׼���������ݿ�Ļ�����Ϣ: url, user, password
		String url = "jdbc:mysql://localhost:3306/test";
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "1230");
		
		//3. ���� Driver �ӿڵġ�connect(url, info) ��ȡ���ݿ�����
		Connection connection = driver.connect(url, info);
		System.out.println(connection);
	}
	
	/**
	 * ��дһ��ͨ�õķ���, �ڲ��޸�Դ����������, ���Ի�ȡ�κ����ݿ������
	 * �������: �����ݿ����� Driver ʵ�����ȫ������url��user��password ����һ��
	 * �����ļ���, ͨ���޸������ļ��ķ�ʽʵ�ֺ;�������ݿ����. 
	 * @throws Exception 
	 */
	public Connection getConnection() throws Exception{
		String driverClass = null;
		String jdbcUrl = null;
		String user = null;
		String password = null;
		
		//��ȡ��·���µ� jdbc.properties �ļ�
		InputStream in = 
				getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		properties.load(in);
		driverClass = properties.getProperty("driver");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		
		//ͨ�����䳣�� Driver ����. 
		Driver driver = 
				(Driver) Class.forName(driverClass).newInstance();
		
		Properties info = new Properties();
		info.put("user", user);
		info.put("password", password);
		
		//ͨ�� Driver �� connect ������ȡ���ݿ�����. 
		Connection connection = driver.connect(jdbcUrl, info);
		
		return connection;
	}
	
	@Test
	public void testGetConnection() throws Exception{
		System.out.println(getConnection());
	}
	
	@Test
	public void solveData(){

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		// ��ѯposition_CodeΪnull���õ�POSITION_NAME������
		//����1ʱ��ɾ������һ�����������
		//����position_Code����t_post��ѯ��dn
		//
		try {
			//1. ��ȡ Connection
			conn = JDBCTools.getConnection();
			System.out.println(conn);
			
			//2. ��ȡ Statement
			statement = conn.createStatement();
			Statement statement1 = conn.createStatement();
			Statement statement2 = conn.createStatement();
			Statement statement3 = conn.createStatement();
			System.out.println(statement);
			
			//3. ׼�� SQL ��ѯposition_CodeΪnull���õ�POSITION_NAME������
			String sql = "select POSITION_NAME,count(1) as num from "
					+"(select * from to_horizon_position where position_Code is null ) t " 
					+"GROUP BY POSITION_NAME";
			
//			String sqll = "select POSITION_NAME,count(1) as num from to_horizon_position where position_Code  = '"++"' GROUP BY POSITION_NAME";
			//4. ִ�в�ѯ, �õ� ResultSet
			rs = statement.executeQuery(sql);
			System.out.println(rs);
			System.out.println("��ʼִ�в���------------------------------------------------------");
			//5. ���� ResultSet
			while(rs.next()){
				
				String POSITION_NAME = rs.getString(1);
				int num = Integer.parseInt(rs.getString(2));
				System.out.println("POSITION_NAME:"+POSITION_NAME+"num:"+num);
				if(num>1){
					int i=0;
					String sql1 = "select id from to_horizon_position where POSITION_NAME = '"+POSITION_NAME+"' ";
					rs1 = statement1.executeQuery(sql1);
					while(rs1.next()){
						i++;
						String id = rs1.getString(1);
						if(i!=1){
							
							System.out.println("ɾ��idΪ��"+id);
							String sql2 = "delete from to_horizon_position where id=  '"+id+"'";
							statement2.executeUpdate(sql2);
						}else{
							System.out.println("δɾ��idΪ��"+id);
						}
						
					}
				}
				
				String sql3 = "update to_horizon_position set position_Code = (select dn from t_post  where postname = '"+POSITION_NAME+"' LIMIT 1)"
						+"where POSITION_NAME = '"+POSITION_NAME+"'";
				System.out.println("����sqlΪ:"+sql3);
				statement3.executeUpdate(sql3);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//6. �ر����ݿ���Դ. 
			JDBCTools.release(rs, statement, conn);
		}
	}
	
	// д����֯�����͸�λ������
	@Test
	public void solveDataTOR_HORIZON_POSITION_OBJ(){

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		// ��ѯposition_CodeΪnull���õ�POSITION_NAME������
		//����1ʱ��ɾ������һ�����������
		//����position_Code����t_post��ѯ��dn
		//
		try {
			//1. ��ȡ Connection
			conn = JDBCTools.getConnection();
			System.out.println(conn);
			
			//2. ��ȡ Statement
			statement = conn.createStatement();
			Statement statement1 = conn.createStatement();
			Statement statement2 = conn.createStatement();
			Statement statement3 = conn.createStatement();
			Statement statement4 = conn.createStatement();
			System.out.println(statement);
			
			//3. ��ѯ��������λ��û�ж�Ӧ����֯�������õ���λdn��hz7sp2��
			String sql = "select a.position_code from  to_horizon_position a where a.id not in (select b.position_id from TOR_HORIZON_POSITION_OBJ b ) "
					+ " and  a.id !='root_node_id' ";
			
			//4. ִ�в�ѯ, �õ� ResultSet
			rs = statement.executeQuery(sql);
			System.out.println(rs);
			System.out.println("��ʼִ�в���------------------------------------------------------");
			//5. ���� ResultSet
			while(rs.next()){
				String deptdn = "";
				String positionid = "";
				String deptid = "";
				String position_code = rs.getString(1);
				System.out.println("position_code:"+position_code);
				// ����dn�õ���֯������dn��uums��
				String sql1 = "SELECT dept.dn AS deptdn FROM t_post post LEFT JOIN t_dept dept " 
						+"ON post.parentid = dept.id WHERE post.dn = '"+position_code+"'";
				rs1 = statement1.executeQuery(sql1);
				
				if(rs1.first()){
					deptdn = rs1.getString(1);	
				}
				// 3.����dn�õ���λid��hz7sp2��
				String sql2 = "select id from to_horizon_position t where t.position_code  =   '"+position_code+"'";
				rs2 = statement2.executeQuery(sql2);
				if(rs2.first()){
					positionid = rs2.getString(1);
				}
				// -- 4. ������֯����dn�õ���֯����id��hz7sp2��
				String sql3 = "select id from TO_HORIZON_DEPT t   where t.f2  ='"+deptdn+"'";
				rs3 = statement3.executeQuery(sql3);
				if(rs3.first()){
					deptid = rs3.getString(1);
				}
				String id = Access.getUUID();
				System.out.println("id:"+id);
				String sql4 = "INSERT INTO TOR_HORIZON_POSITION_OBJ (`ID`, `POSITION_ID`, `OBJECT_ID`, `OBJECT_TYPE`, `ORDER_NO`) VALUES "
						+ "('"+id+"','"+positionid+"','"+deptid+"',null,'0')";
				System.out.println("sql4:"+sql4);
				statement4.executeUpdate(sql4);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//6. �ر����ݿ���Դ. 
			JDBCTools.release(rs, statement, conn);
		}
	}
}
