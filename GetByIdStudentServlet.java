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
import java.util.List;

@WebServlet("/getByIdStudent")
public class GetByIdStudentServlet extends HttpServlet {
    private Gson gson = new Gson();
    private Connection connection = null;
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://106.12.157.37:3306/linux_final?serverTimezone=GMT";
            String user = "root";
            String password = "83387330Znn!";
            connection = DriverManager.getConnection(url, user, password);
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
        Student student = getStudentById(Integer.valueOf(request.getParameter("id")));
        String json = gson.toJson(student);
        response.getWriter().print(json);
        response.getWriter().close();
    }

    private Student getStudentById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> list = new ArrayList<Student>();
        try {
            ps = connection.prepareStatement("select * from student where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            while (rs.next()) {
                Student student = new Student();
                for (int i = 0; i < count; i++) {
                    Object objValue = rs.getObject(i + 1);
                    String columnName = rsmd.getColumnLabel(i + 1);
                    Field field = Student.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(student, objValue);
                }
                list.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list.get(0);
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
