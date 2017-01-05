import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {
	private static final String DRIVER="com.mysql.jdbc.Driver";
	private static final String URL="jdbc:mysql:///exam_1?characterEncoding=utf8";
	private static final String SUER="root";
	private static final String PWD="accp";
	
	static
	{
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(URL, SUER, PWD);
		
	}
	
	public PreparedStatement createPreparedStatement(Connection conn,String sql,Object...args) throws SQLException
	{
		PreparedStatement ps=conn.prepareStatement(sql);
		//填充？
		if(args!=null&&args.length>0)
		{
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i+1, args[i]);
			}
		}
		return ps;
	}
	
	//关闭资源
	public void release(Connection conn,PreparedStatement ps,ResultSet rs)
	{
		try {
			if(rs!=null)
			{
				rs.close();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		try {
			if(ps!=null)
			{
				ps.close();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		try {
			if(conn!=null)
			{
				conn.close();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	//增删改模板方法
	public Integer update(String sql,Object...args)
	{
		Connection conn=null;
		PreparedStatement ps=null;
		Integer result=null;
		try {
			conn=getConnection();
			ps=createPreparedStatement(conn, sql, args);
			result=ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			this.release(conn, ps, null);
		}
		return result;
	}
	
	//查询所有的方法
	public <T> List<T> queryAll(RowMapper<T> rowMapper,String sql,Object...args)
	{
		List<T> list=new ArrayList<T>();
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			conn=getConnection();
			ps=createPreparedStatement(conn, sql, args);
			rs=ps.executeQuery();
			
			while(rs.next())
			{
				T obj=rowMapper.mapper(rs);
				list.add(obj);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			this.release(conn, ps, rs);
		}
		return list;
	}
	
	//查询单个的方法
		public <T> T querySingle(RowMapper<T> rowMapper,String sql,Object...args)
		{
			
			Connection conn=null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			
			try {
				conn=getConnection();
				ps=createPreparedStatement(conn, sql, args);
				rs=ps.executeQuery();
				
				if(rs.next())
				{
					T obj=rowMapper.mapper(rs);
					return obj;
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}finally{
				this.release(conn, ps, rs);
			}
			return null;
		}
	
		//求单列行数的方法
		public Object getSingleColoum(String sql,Object...args)
		{
			Connection conn=null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			
			try {
				conn=getConnection();
				ps=createPreparedStatement(conn, sql, args);
				rs=ps.executeQuery();
				
				if(rs.next())
				{
					return rs.getObject(1);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}finally{
				this.release(conn, ps, rs);
			}
			return null;
		}
}
