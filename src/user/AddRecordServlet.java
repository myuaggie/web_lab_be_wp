package user;

import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.util.Date;

@WebServlet("/AddRecordServlet")
public class AddRecordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRecordServlet(){
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
            Object o=request.getSession().getAttribute("userid");
            int owner;
            if (o==null){owner=-1;}
            else {owner=Integer.parseInt(o.toString());}
            // Write HTML header

            PrintWriter out = response.getWriter();
            // String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数

            if (owner==-1){
                // out.println(jsonpCallback+"({'question':[{'login':0}]})");
            }
            else {
                int libraryid=Integer.valueOf(request.getParameter("libraryid").toString());
                int recordid=Integer.valueOf(request.getParameter("recordid").toString());
                String answer=request.getParameter("answer");
                Record rec=new Record();
                URKey urKey=new URKey();
                urKey.setUserId(owner);
                urKey.setLibraryId(libraryid);
                urKey.setRecordId(recordid);
                rec.setUrKey(urKey);
                byte[] bytes=answer.getBytes("utf-8");
                Blob blobContent= session.getLobHelper().createBlob(bytes);
                rec.setAnswer(blobContent);
                //rec.setAnswer(answer);
                rec.setDate(new Date());
                session.save(rec);
            }
            out.flush();
            out.close();
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        } catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if (ServletException.class.isInstance(ex)) {
                throw (ServletException) ex;
            } else {
                throw new ServletException(ex);
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
