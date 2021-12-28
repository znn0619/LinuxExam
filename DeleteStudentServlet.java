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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/deleteStudent")
public class DeleteStudentServlet extends HttpServlet {
    private Connection connection = null;
    private Gson gson = new Gson();
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
        Map<String, Object> result = new HashMap<String, Object>();
        deleteStudent(Integer.parseInt(getRequestBody(request).get("id")));
        response.getWriter().print("删除成功");
        response.getWriter().close();
    }

    private Map<String,String> getRequestBody(HttpServletRequest request) {
        Map map = null;
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            map = gson.fromJson(sb.toString(), Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private void deleteStudent(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("delete from student where id = ?");
            ps.setInt(1, id);
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
