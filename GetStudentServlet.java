import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/getStudent")
public class GetStudentServlet extends HttpServlet {
    private Gson gson = new Gson();

    private Connection connection = null;

    public void init() {
        try {
            // 自动注册加载
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://106.12.157.37:3306/linux_final?serverTimezone=GMT";
            String user = "root";
            String password = "83387330Znn!";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Mysql Connection");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> result = new HashMap<String, Object>();

        List<Student> studentList = getStudent();

        String json = gson.toJson(studentList);
        response.getWriter().print(json);
        response.getWriter().close();
    }

    private List<Student> getStudent() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> list = new ArrayList<Student>();
        try {
            ps = connection.prepareStatement("select * from student");
            // 获取结果集
            rs = ps.executeQuery();
            // 获取结果集元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取元数据列数
            int count = rsmd.getColumnCount();
            while (rs.next()) {
                Student student = new Student();
                // 处理每行数据
                for (int i = 0; i < count; i++) {
                    // 获取属性值
                    Object objValue = rs.getObject(i + 1);
                    // 获取属性名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    // getColumnName() 获取列名(不推荐)
                    // getColumnLabel() 获取别名 无别名默认列名
                    // 通过反射找到私有属性
                    Field field = Student.class.getDeclaredField(columnName);
                    // 设置访问私有属性
                    field.setAccessible(true);
                    // 进行赋值
                    field.set(student, objValue);

                }
                list.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
