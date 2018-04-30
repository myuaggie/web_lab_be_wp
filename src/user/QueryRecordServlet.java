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

//[[recordid，date]]
///QueryRecordServlet?libraryid=
@WebServlet("/QueryRecordServlet")
public class QueryRecordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryRecordServlet(){
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
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
                printRecord(out, owner, library);
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

    @SuppressWarnings({ "unchecked" })
    private void printRecord(PrintWriter out,int userid,int libraryid){
        List<Record> res=HibernateUtil.getSessionFactory().getCurrentSession()
                .createQuery("from Record r where r.urKey.userId=? and r.urKey.libraryId=?")
                .setParameter(0,userid).setParameter(1,libraryid).list();


        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            Record rec = (Record) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(rec.getUrKey().getRecordId()));
            arrayList.add(String.valueOf(rec.getDate()));
            qJ.add(JSONArray.fromObject(arrayList));
               /* j=j+",{'name':'"+lib.getQuestion().getName()
                        +"','tag':['"+lib.getTagOne()+"','"+lib.getTagTwo()+"']"
                        +",'frequent':"+lib.getFrequency()
                        +",'date':'"+lib.getDate()
                        +"','key':"+lib.getUlKey().getLibraryId()
                        +",'content':'"+lib.getQuestion().getContent()
                        +"'}";*/
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        // j+="]})";
        // out.println(j);


    }


}
