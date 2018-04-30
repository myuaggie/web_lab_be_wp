package user;

import net.sf.json.JSONArray;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//dup phone[0]
//success[id]
// RegisterServlet?username=&email=&password=&phone=
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet(){
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings({ "unchecked" })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Session session=HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            response.setHeader("Content-Type", "text/html;charset=utf-8");

            String name = request.getParameter("username");
            String pwd = MD5Util.md5Encode(request.getParameter("password"));
            String email=request.getParameter("email");
            String phone=request.getParameter("phone");
            List<User> res=HibernateUtil.getSessionFactory().getCurrentSession()
                    .createQuery("from User u where u.phone=?").setParameter(0,phone).list();
            Iterator it = res.iterator();
            PrintWriter writer = response.getWriter();
            if (it.hasNext()){ //手机号已被注册
                /*String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                writer.println(jsonpCallback+"("+"{" +
                        "'id':'0'}"+")");*/
                ArrayList<String> ur=new ArrayList<String>();
                ur.add("0");
                writer.println(JSONArray.fromObject(ur));
            }
            else{ //可以注册
                User u=new User();
               // u.setId(id);
                u.setUsername(name);
                u.setPassword(pwd);
                u.setEmail(email);
                u.setPhone(phone);
                session.save(u);
                /*String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                writer.println(jsonpCallback+"("+"{" +
                        "'id':'"+String.valueOf(u.getId())+"'}"+")");*/
                ArrayList<String> ur=new ArrayList<String>();
                ur.add(String.valueOf(u.getId()));
                writer.println(JSONArray.fromObject(ur));
            }
            session.getTransaction().commit();
        }
        catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if ( ServletException.class.isInstance( ex ) ) {
                throw ( ServletException ) ex;
            }
            else {
                throw new ServletException( ex );
            }
        }
    }
}

