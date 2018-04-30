package user;

import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//success[userid,username,phone,email]
//wrong pwd[userid=0]
//userid no existence[userid=-1]
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet(){
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings({ "unchecked" })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Begin unit of work
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            int userid=Integer.valueOf(request.getParameter("userid"));
            String pwd=request.getParameter("password");
            List<User> res=HibernateUtil.getSessionFactory().getCurrentSession()
                    .createQuery("from User u where u.id=?").setParameter(0,userid).list();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            Iterator it = res.iterator();
            if (it.hasNext()){
                User u=(User)it.next();
                if (MD5Util.md5Encode(pwd).equals(u.getPassword())){  //密码正确
                    HttpSession session = request.getSession();
                    session.setAttribute("userid", userid);
                    session.setAttribute("username",u.getUsername());
                    session.setAttribute("phone",u.getPhone());
                    session.setAttribute("email",u.getEmail());
                    //writer.println("<p>"+u.getUsername()+"</p>");
                    //String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                    //JSONObject j=JSONObject.fromObject(u);
                     ArrayList<String> ur=new ArrayList<String>();
                     ur.add(String.valueOf(userid));
                     ur.add(u.getUsername());
                     ur.add(u.getPhone());
                     ur.add(u.getEmail());
                     writer.println(JSONArray.fromObject(ur));
                    /*writer.println(jsonpCallback+"("+"{" +
                            "'username':'"+u.getUsername()+
                            "','id':'"+u.getId()+
                            "'}"+")");*/
                    //log(j.toString());
                    //writer.print(JSONObject.fromObject(u).toString());
                }
                else{ //密码错误
                    //String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                    /*writer.println(jsonpCallback+"("+"{" +
                            "'id':'0'}"+")");*/
                    ArrayList<String> ur=new ArrayList<String>();
                    ur.add("0");
                    writer.println(JSONArray.fromObject(ur));
                }

            }
            else{  //用户id不存在
                /*String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
                writer.println(jsonpCallback+"("+"{" +
                        "'id':'-1'}"+")");*/
                ArrayList<String> ur=new ArrayList<String>();
                ur.add("-1");
                writer.println(JSONArray.fromObject(ur));
            }
            writer.flush();
            writer.close();
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
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


