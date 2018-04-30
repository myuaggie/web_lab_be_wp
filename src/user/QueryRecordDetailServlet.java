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
import java.util.Iterator;
import java.util.List;

@WebServlet("/QueryRecordDetailServlet")
public class QueryRecordDetailServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryRecordDetailServlet(){
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
                int library=Integer.valueOf(request.getParameter("libraryid").toString());
                int record=Integer.valueOf(request.getParameter("recordid").toString());
                List<Record> res=HibernateUtil.getSessionFactory().getCurrentSession()
                        .createQuery("from Record r where r.urKey.userId=? and r.urKey.libraryId=? and r.urKey.recordId=?")
                        .setParameter(0,owner).setParameter(1,library).setParameter(2,record).list();
                Iterator it = res.iterator();
                if (it.hasNext()){
                    Record r=(Record)it.next();
                    Blob blob=r.getAnswer();
                    String answer = new String(blob.getBytes((long)1, (int)blob.length()));
                    out.println(answer);
                }
            }
            out.flush();
            out.close();
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
