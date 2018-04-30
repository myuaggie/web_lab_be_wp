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

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet(){
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession hs = request.getSession();
            int id=-1;
            String name="",email="",phone="";
            if (hs.getAttribute("userid")!=null){

                id = Integer.parseInt(hs.getAttribute("userid").toString());
                name = hs.getAttribute("username").toString();
                email = hs.getAttribute("email").toString();
                phone = hs.getAttribute("phone").toString();
            }
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            /*String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
            out.println(jsonpCallback + "({'id':" + String.valueOf(id)
                    + ",'name':'" + name + "','email':'" + email + "','phone':'" + phone + "'})");*/
            ArrayList<String> ur=new ArrayList<String>();
            ur.add(String.valueOf(id));
            ur.add(name);
            ur.add(email);
            ur.add(phone);
            out.println(JSONArray.fromObject(ur));
            out.flush();
            out.close();
        } catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if (ServletException.class.isInstance(ex)) {
                throw (ServletException) ex;
            } else {
                throw new ServletException(ex);
            }
        }
    }
}
