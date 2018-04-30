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

//[name,content,reference,owner,update date,key,ownerid]
@WebServlet("/DetailServlet")
public class DetailServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DetailServlet(){
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
                printDetail(out, owner,Integer.valueOf(request.getParameter("key").toString()));
            }
            out.flush();
            out.close();
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        }
        catch (Exception ex) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            if (ServletException.class.isInstance(ex)) {
                throw (ServletException) ex;
            } else {
                throw new ServletException(ex);
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void printDetail(PrintWriter out,int owner,int key){
        List<UQ_Library> res=HibernateUtil.getSessionFactory().getCurrentSession()
                .createQuery("from UQ_Library l where l.ulKey.userId=? and l.ulKey.libraryId=?")
                .setParameter(0,owner).setParameter(1,key).list();
        Iterator it = res.iterator();
        if (it.hasNext()){
            UQ_Library lib = (UQ_Library) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(lib.getQuestion().getName());
            arrayList.add(lib.getQuestion().getContent());
            arrayList.add(lib.getQuestion().getReference());
            arrayList.add(lib.getQuestion().getOwner().getUsername());
            arrayList.add(lib.getDate().toString());
            arrayList.add(String.valueOf(key));
            //arrayList.add(String.valueOf(owner));
            arrayList.add(String.valueOf(lib.getQuestion().getOwner().getId()));
            out.println(JSONArray.fromObject(arrayList));
        }

    }
}
