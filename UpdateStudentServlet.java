import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/updateStudent")
public class UpdateStudentServlet extends HttpServlet {
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
        Student student = getRequestBody(request);
        updateStudent(student);
        response.getWriter().print("更新成功");
        response.getWriter().close();
    }

    private Student getRequestBody(HttpServletRequest request) {
        Student student = new Student();
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            student = gson.fromJson(sb.toString(), new TypeToken<Student>(){

            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return student;
    }

    private void updateStudent(Student student) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("update student set name = ?,age = ? where id = ?");
            ps.setString(1, student.getName());
            ps.setString(2, student.getAge());
            ps.setInt(3, student.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
